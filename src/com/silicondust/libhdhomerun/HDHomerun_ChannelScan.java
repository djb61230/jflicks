package com.silicondust.libhdhomerun;

import java.util.NoSuchElementException;
import java.util.Scanner;

import com.silicondust.libhdhomerun.HDHomerun_Channels.hdhomerun_channel_entry_t;
import com.silicondust.libhdhomerun.HDHomerun_ChannelList;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_program_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_result_t;

public final class HDHomerun_ChannelScan {
	
	public static final int HDHOMERUN_CHANNELSCAN_PROGRAM_NORMAL = 0;
	public static final int HDHOMERUN_CHANNELSCAN_PROGRAM_NODATA = 1;
	public static final int HDHOMERUN_CHANNELSCAN_PROGRAM_CONTROL = 2;
	public static final int HDHOMERUN_CHANNELSCAN_PROGRAM_ENCRYPTED = 3;

	private HDHomerun_Device mDevice = null;
	private long mScanned_channels; // uint32_t
    private HDHomerun_ChannelList mChannel_list = null;	
	private hdhomerun_channel_entry_t mNext_channel = new hdhomerun_channel_entry_t();

	public HDHomerun_ChannelScan(HDHomerun_Device hd, final String channelmap) throws Exception
	{
		mChannel_list = new HDHomerun_ChannelList(channelmap);
		mDevice = hd;
		if (mChannel_list == null) {
			throw new Exception();
		}

		mNext_channel = mChannel_list.tail;
	}

	public int advance(hdhomerun_channelscan_result_t result)
	{
		result.clean();
		
		hdhomerun_channel_entry_t entry = mNext_channel;
		if (entry == null) {
			return 0;
		}

		/* Combine channels with same frequency. */
		result.frequency = entry.frequency;
		result.channel_str = entry.name;

		while (true) {
			entry = entry.prev;
			if (entry == null) {
				mNext_channel = null;
				break;
			}

			if (entry.frequency != result.frequency) {
				mNext_channel = entry;
				break;
			}

			result.channel_str += ", " + entry.name;
		}

		return 1;
	}

	public int detect(hdhomerun_channelscan_result_t result)
	{
		mScanned_channels++;

		/* Find lock. */
		int ret = find_lock(result.frequency, result);
		if (ret <= 0) {
			return ret;
		}
		if (!result.status.lock_supported) {
			return 1;
		}

		/* Detect programs. */
		result.program_count = 0;

		long timeout; // uint64_t
		if (mDevice.get_model_str().contains("atsc")) {
			timeout = HDHomerun_OS.getcurrenttime() + 4000;
		} else {
			timeout = HDHomerun_OS.getcurrenttime() + 10000;
		}

		long complete_time = HDHomerun_OS.getcurrenttime() + 1000; // uint64_t

		while (true) {
			boolean changed[] = new boolean[1];
			boolean incomplete[] = new boolean [1];
			ret = detect_programs(result, changed, incomplete);
			if (ret <= 0) {
				return ret;
			}

			if (changed[0]) {
				complete_time = HDHomerun_OS.getcurrenttime() + 1000;
			}

			if (!incomplete[0] && (HDHomerun_OS.getcurrenttime() >= complete_time)) {
				break;
			}

			if (HDHomerun_OS.getcurrenttime() >= timeout) {
				break;
			}

			HDHomerun_OS.msleep_approx(250);
		}

		/* Lock => skip overlapping channels. */
		long max_next_frequency = result.frequency - 5500000; // uint32_t
		while (true) {
			if (mNext_channel == null) {
				break;
			}

			if (mNext_channel.frequency <= max_next_frequency) {
				break;
			}

			mNext_channel = mNext_channel.prev;
		}

		/* Success. */
		return 1;
	}

	public byte get_progress() { // uint8_t
		hdhomerun_channel_entry_t entry = mNext_channel;
		if (entry == null) {
			return 100;
		}

		long channels_remaining = 1;
		long frequency = entry.frequency;

		while (true) {
			entry = entry.prev;
			if (entry == null) {
				break;
			}

			if (entry.frequency != frequency) {
				channels_remaining++;
				frequency = entry.frequency;
			}
		}

		return (byte) ((mScanned_channels * 100) / (mScanned_channels + channels_remaining));
	}
	
	private int find_lock(long frequency, hdhomerun_channelscan_result_t result) { // uint32_t
		/* Set channel. */
		String channel_str = String.format("auto:%d", frequency);

		int ret = mDevice.set_tuner_channel(channel_str);
		if (ret <= 0) {
			return ret;
		}

		/* Wait for lock. */
		ret = mDevice.wait_for_lock(result.status);
		if (ret <= 0) {
			return ret;
		}
		if (!result.status.lock_supported) {
			return 1;
		}

		/* Wait for symbol quality = 100%. */
		long timeout = HDHomerun_OS.getcurrenttime() + 5000;
		while (true) {
			ret = mDevice.get_tuner_status(null, result.status);
			if (ret <= 0) {
				return ret;
			}

			if (result.status.symbol_error_quality == 100) {
				return 1;
			}

			if (HDHomerun_OS.getcurrenttime() >= timeout) {
				return 1;
			}

			HDHomerun_OS.msleep_approx(250);
		}
	}

	private int detect_programs(hdhomerun_channelscan_result_t result, boolean[] pchanged, boolean[] pincomplete)
	{
		pchanged[0] = false;
		pincomplete[0] = false;

		StringBuilder streaminfo = new StringBuilder();
		int ret = mDevice.get_tuner_streaminfo(streaminfo);
		if (ret <= 0) {
			return ret;
		}

		String next_line = streaminfo.toString();
		int program_count = 0;

		while (true) {
			String line = next_line;

			int index = line.indexOf('\n');
			if (index == -1) {
				break;
			}
			next_line = line.substring(index + 1);	
			line = line.substring(0, index);
			
			if(line.startsWith("tsid=0x")) {
				try {
					line = line.substring(7);
					if(line != null && line.length() > 0) {
						Scanner scn = new Scanner(line);
						result.transport_stream_id = scn.nextInt(16);
						result.transport_stream_id_detected = true;
						continue;
					}
				}
				catch (NoSuchElementException e) {
					
				}
			}
			Scanner scn = new Scanner(line);

			if (program_count >= HDHomerun_Types.HDHOMERUN_CHANNELSCAN_MAX_PROGRAM_COUNT) {
				continue;
			}

			hdhomerun_channelscan_program_t program = new hdhomerun_channelscan_program_t();

			program.program_str = line;

			// %u: %u or %u: %u.%u
			try {
				scn.useDelimiter(":");
				program.program_number = scn.nextInt();
				boolean hasMinor = line.contains(".");
				if(hasMinor) {
					int colonNxt = line.indexOf(':') + 2;
					int decimal = line.indexOf('.');
					Scanner major = new Scanner(line.substring(colonNxt, decimal));
					program.virtual_major = major.nextInt();
					int nameStart = line.indexOf(' ', ++decimal);
					Scanner minor = new Scanner(line.substring(decimal, nameStart));
			    	program.virtual_minor = minor.nextInt();
				}
				else {
					scn.useDelimiter(" ");
					scn.next();
					program.virtual_major = scn.nextInt();
				}
				
		    	
		    } catch (NoSuchElementException e) {
				continue;
			}		 

			channelscan_extract_name(program, line);

			if (line.contains("(control)")) {
				program.type = HDHOMERUN_CHANNELSCAN_PROGRAM_CONTROL;
			} else if (line.contains("(encrypted)")) {
				program.type = HDHOMERUN_CHANNELSCAN_PROGRAM_ENCRYPTED;
			} else if (line.contains("(no data)")) {
				program.type = HDHOMERUN_CHANNELSCAN_PROGRAM_NODATA;
				pincomplete[0] = true;
			} else {
				program.type = HDHOMERUN_CHANNELSCAN_PROGRAM_NORMAL;
				if ((program.virtual_major == 0) || (program.name.length() == 0)) {
					pincomplete[0] = true;
				}
			}
			
			if(result.programs[program_count] == null)
				result.programs[program_count] = program;

			else if (!result.programs[program_count].compare(program)) {
				result.programs[program_count] = new hdhomerun_channelscan_program_t(program);
				pchanged[0] = true;
			}

			program_count++;
		}

		if (program_count == 0) {
			pincomplete[0] = true;
		}
		if (result.program_count != program_count) {
			result.program_count = program_count;
			pchanged[0] = true;
		}

		return 1;
	}

	private static void channelscan_extract_name(hdhomerun_channelscan_program_t program, final String line)
	{
		/* Find start of name. */
		int index = line.indexOf(' ');
		if (index == -1) {
			return;
		}
		index = line.indexOf(' ', ++index);
		if (index == -1) {
			return;
		}
		++index;

		/* Find end of name. */
		int endIndex = line.indexOf("(", index);
		if (-1 == endIndex) {
			endIndex = line.length();
		}

		if (endIndex <= index) {
			return;
		}
		
		/* Extract name. */
		program.name = line.substring(index, endIndex);
	}

}
