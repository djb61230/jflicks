package com.silicondust.libhdhomerun_config;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import com.silicondust.libhdhomerun.HDHomerun_Channels;
import com.silicondust.libhdhomerun.HDHomerun_Debug;
import com.silicondust.libhdhomerun.HDHomerun_Device;
import com.silicondust.libhdhomerun.HDHomerun_Discover;
import com.silicondust.libhdhomerun.HDHomerun_OS;
import com.silicondust.libhdhomerun.HDHomerun_Pkt;
import com.silicondust.libhdhomerun.HDHomerun_Video;
import com.silicondust.libhdhomerun.HDHomerun_Discover.hdhomerun_discover_device_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_program_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_result_t;
import com.silicondust.libhdhomerun.HDHomerun_Video.hdhomerun_video_stats_t;

public class HDHomerun_Config {

	static String appname;

	static HDHomerun_Device hd;
	static HDHomerun_Debug dbg;

	static int help()
	{
		System.out.println("Usage:\n");
		System.out.println(String.format("\t%s discover\n", appname));
		System.out.println(String.format("\t%s <id> get help\n", appname));
		System.out.println(String.format("\t%s <id> get <item>\n", appname));
		System.out.println(String.format("\t%s <id> set <item> <value>\n", appname));
		System.out.println(String.format("\t%s <id> scan <tuner> [<filename>]\n", appname));
		System.out.println(String.format("\t%s <id> save <tuner> <filename>\n", appname));
		System.out.println(String.format("\t%s <id> upgrade <filename>\n", appname));
		return -1;
	}

	static void extract_appname(final String argv0)
	{
		String arg = argv0;
		int index = arg.lastIndexOf('/');
		if (index >= 0) {
			arg = arg.substring(index + 1);
		}
		index = arg.lastIndexOf('\\');
		if (index >= 0) {
			arg = arg.substring(index + 1);
		}
		appname = arg;
	}

	static boolean contains(final String arg, final String cmpstr)
	{
		if (arg.compareTo(cmpstr) == 0) {
			return true;
		}

		if (arg.charAt(0) != '-') {
			return false;
		}
		if (arg.charAt(1) != '-') {
			return false;
		}
		if (arg.substring(2).compareTo(cmpstr) == 0) {
			return true;
		}

		return false;
	}

	static int parse_ip_addr(final String str)
	{
		int[] a = new int[4];
		Scanner scn = new Scanner(str);
		a[0] = scn.nextInt();
		scn.next(".");
		a[1] = scn.nextInt();
		scn.next(".");
		a[2] = scn.nextInt();
		scn.next(".");
		a[3] = scn.nextInt();
		
		if (a[0] == 0 || a[1] == 0 || a[2] == 0 || a[3] == 0) {
			return 0;
		}

		return ((a[0] << 24) | (a[1] << 16) | (a[2] << 8) | (a[3] << 0));
	}

	static int discover_print(final String target_ip_str)
	{
		int target_ip = 0;
		if (target_ip_str != null && target_ip_str.length() > 0) {
			target_ip = parse_ip_addr(target_ip_str);
			if (target_ip == 0) {
				System.out.println(String.format("invalid ip address: %s\n", target_ip_str));
				return -1;
			}
		}

		hdhomerun_discover_device_t[] result_list = new hdhomerun_discover_device_t[64];
		int count = HDHomerun_Discover.hdhomerun_discover_find_devices_custom(target_ip, HDHomerun_Pkt.HDHOMERUN_DEVICE_TYPE_TUNER, HDHomerun_Pkt.HDHOMERUN_DEVICE_ID_WILDCARD, result_list, 64);
		if (count < 0) {
			System.out.println("error sending discover request\n");
			return -1;
		}
		if (count == 0) {
			System.out.println("no devices found\n");
			return 0;
		}

		int index;
		for (index = 0; index < count; index++) {
			hdhomerun_discover_device_t result = result_list[index];
			System.out.println(String.format("hdhomerun device %08X found at %d.%d.%d.%d\n",
					result.device_id,
					(result.ip_addr >> 24) & 0x0FF, (result.ip_addr >> 16) & 0x0FF,
					(result.ip_addr >> 8) & 0x0FF, (result.ip_addr >> 0) & 0x0FF
				));
		}

		return count;
	}

	static int cmd_get(final String item)
	{
		StringBuilder ret_value = new StringBuilder();
		StringBuilder ret_error = new StringBuilder();
		if (hd.get_var(item, ret_value, ret_error) < 0) {
			System.out.println("communication error sending request to hdhomerun device\n");
			return -1;
		}

		if (ret_error.length() > 0) {
			System.out.println(String.format("%s\n", new String(ret_error)));
			return 0;
		}

		System.out.println(String.format("%s\n", new String(ret_value)));
		return 1;
	}

	static int cmd_set_internal(final String item, final String value)
	{
		StringBuilder ret_error = new StringBuilder();
		if (hd.set_var(item, value, null, ret_error) < 0) {
			System.out.println("communication error sending request to hdhomerun device\n");
			return -1;
		}

		if (ret_error.length() > 0) {
			System.out.println(String.format("%s\n", new String(ret_error)));
			return 0;
		}

		return 1;
	}

	static int cmd_set(final String item, final String value)
	{
		if (value.compareTo("-") == 0) {
			String buffer = new String();
			while (true) {
				
				byte[] b = new byte[1024];
				int size = 0;
				try {
					size = System.in.read(b);
				} catch (IOException e) {
					break;
				}
				buffer += new String(b);
				if (size < 1024) {
					break;
				}
			}

			int ret = cmd_set_internal(item, buffer);

			
			return ret;
		}

		return cmd_set_internal(item, value);
	}

	static volatile boolean sigabort_flag = false;

	synchronized static boolean isTerminated() {
		
		return sigabort_flag;
	}
	 
	synchronized static void sigTerm()
	{
		sigabort_flag = true;
	}

	private static void register_signal_handlers()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	sigTerm();
		    }
		 });
	}

	private static void writeToFileAndScreen(FileOutputStream fp, String str) {
		
		System.out.print(str);
		if(fp != null) {
			try {
				fp.write(str.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static int cmd_scan(final String tuner_str, final String filename)
	{
		if (hd.set_tuner_from_str(tuner_str) <= 0) {
			System.out.println("invalid tuner number\n");
			return -1;
		}

		StringBuilder ret_error = new StringBuilder();
		if (hd.tuner_lockkey_request(ret_error) <= 0) {
			System.out.println("failed to lock tuner\n");
			if (ret_error.length() > 0) {
				System.out.println(String.format("%s\n", new String(ret_error)));
			}
			return -1;
		}

		hd.set_tuner_target("none");

		StringBuilder channelmap = new StringBuilder();
		if (hd.get_tuner_channelmap(channelmap) <= 0) {
			System.out.println("failed to query channelmap from device\n");
			return -1;
		}

		String channelmap_scan_group = HDHomerun_Channels.hdhomerun_channelmap_get_channelmap_scan_group(new String(channelmap));
		if (null == channelmap_scan_group || channelmap_scan_group.length() == 0) {
			System.out.println(String.format("unknown channelmap '%s'\n", new String(channelmap)));
			return -1;
		}

		if (hd.channelscan_init(channelmap_scan_group) <= 0) {
			System.out.println("failed to initialize channel scan\n");
			return -1;
		}

		FileOutputStream fp = null;
		if (filename != null && filename.length() > 0) {
			try {
				fp = new FileOutputStream(filename);
			}
			catch (IOException e) {
				System.out.println(String.format("unable to create file: %s\n", filename));
				return -1;
			}
		}

		register_signal_handlers();

		int ret = 0;
		while (!isTerminated()) { //!sigabort_flag) {
			hdhomerun_channelscan_result_t result = new hdhomerun_channelscan_result_t();
			ret = hd.channelscan_advance(result);
			if (ret <= 0) {
				break;
			}

			writeToFileAndScreen(fp, String.format("SCANNING: %d (%s)\n", result.frequency, result.channel_str
			));

			ret = hd.channelscan_detect(result);
			if (ret < 0) {
				break;
			}
			if (ret == 0) {
				continue;
			}

			writeToFileAndScreen(fp, String.format("LOCK: %s (ss=%d snq=%d seq=%d)\n",
					result.status.lock_str, result.status.signal_strength,
					result.status.signal_to_noise_quality, result.status.symbol_error_quality
				));

			if (result.transport_stream_id_detected) {
				writeToFileAndScreen(fp, String.format("TSID: 0x%04X\n", result.transport_stream_id));
			}

			int i;
			for (i = 0; i < result.program_count; i++) {
				hdhomerun_channelscan_program_t program = result.programs[i];
				writeToFileAndScreen(fp, String.format("PROGRAM %s\n", program.program_str));
			}
		}

		hd.tuner_lockkey_release();

		if (fp != null) {
			try {
				fp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ret < 0) {
			System.out.println("communication error sending request to hdhomerun device\n");
		}
		return ret;
	}

	static void cmd_save_print_stats()
	{
		hdhomerun_video_stats_t stats = new hdhomerun_video_stats_t();
		hd.get_video_stats(stats);

		System.out.println(String.format("%d packets received, %d overflow errors, %d network errors, %d transport errors, %d sequence errors\n",
			stats.packet_count, 
			stats.overflow_error_count,
			stats.network_error_count, 
			stats.transport_error_count, 
			stats.sequence_error_count
		));
	}

	static int cmd_save(final String tuner_str, final String filename)
	{
		if (hd.set_tuner_from_str(tuner_str) <= 0) {
			System.out.println("invalid tuner number\n");
			return -1;
		}

		boolean printToConsole = false;
		FileOutputStream fp = null;
		if (filename.compareTo("null") == 0) {
			fp = null;
		} else if (filename.compareTo("-") == 0) {
			printToConsole = true;
		} else {
			try {
				fp = new FileOutputStream(filename);
			} catch (FileNotFoundException e) {
				System.out.println(String.format("unable to create file %s\n", filename));
				return -1;
			}
		}

		int ret = hd.stream_start();
		if (ret <= 0) {
			System.out.println("unable to start stream " + ret);
			if (fp != null) {
				try {
					fp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return ret;
		}

		//register_signal_handlers(sigabort_handler, sigabort_handler, siginfo_handler);

		hdhomerun_video_stats_t stats_old = new hdhomerun_video_stats_t();
		hdhomerun_video_stats_t stats_cur = new hdhomerun_video_stats_t();
		hd.get_video_stats(stats_old);

		long next_progress = HDHomerun_OS.getcurrenttime() + 1000;

		while (isTerminated()) { //!sigabort_flag) {
			long loop_start_time = HDHomerun_OS.getcurrenttime();

			/*if (siginfo_flag) {
				System.out.println("\n");
				cmd_save_print_stats();
				siginfo_flag = false;
			}*/

			int[] actual_size = new int[1];
			byte[] ptr = hd.stream_recv(HDHomerun_Video.VIDEO_DATA_BUFFER_SIZE_1S, actual_size);
			if (null == ptr) {
				HDHomerun_OS.msleep_approx(64);
				continue;
			}

			if (fp != null) {
				try {
					fp.write(ptr, 0, actual_size[0]);
				}
				catch(IOException e)
				{
					System.out.println("error writing output\n");
					return -1;
				}
			}
			else if(printToConsole)
				System.out.println(String.format("Bytes Received : %d", actual_size[0]));

			if (loop_start_time >= next_progress) {
				next_progress += 1000;
				if (loop_start_time >= next_progress) {
					next_progress = loop_start_time + 1000;
				}

				/* Video stats. */
				hd.get_video_stats(stats_cur);

				if (stats_cur.overflow_error_count > stats_old.overflow_error_count) {
					System.out.println("o");
				} else if (stats_cur.network_error_count > stats_old.network_error_count) {
					System.out.println("n");
				} else if (stats_cur.transport_error_count > stats_old.transport_error_count) {
					System.out.println("t");
				} else if (stats_cur.sequence_error_count > stats_old.sequence_error_count) {
					System.out.println("s");
				} else {
					System.out.println(".");
				}

				stats_old = stats_cur;
			}

			long delay = 64 - (HDHomerun_OS.getcurrenttime() - loop_start_time);
			if (delay <= 0) {
				continue;
			}

			HDHomerun_OS.msleep_approx(delay);
		}

		if (fp != null) {
			try {
				fp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		hd.stream_stop();

		System.out.println("\n");
		System.out.println("-- Video statistics --\n");
		cmd_save_print_stats();

		return 0;
	}

	
	static int cmd_execute()
	{
		StringBuilder ret_value = new StringBuilder();
		StringBuilder ret_error = new StringBuilder();
		if (hd.get_var("/sys/boot", ret_value, ret_error) < 0) {
			System.out.println("communication error sending request to hdhomerun device\n");
			return -1;
		}

		if (ret_error.length() > 0) {
			System.out.println(String.format("%s\n", ret_error));
			return 0;
		}		
		String currStr = new String(ret_value);
		int endIndex = currStr.length();
		while (true) {

			int eol_r = currStr.indexOf('\r'); 
			if (-1 == eol_r) {
				eol_r = endIndex;
			}

			int eol_n = currStr.indexOf('\n');
			if (-1 == eol_n) {
				eol_n = endIndex;
			}

			int eol = eol_r;
			if (eol_n < eol) {
				eol = eol_r;
			}

			int sep = currStr.indexOf(' ');
			if (-1 == sep || sep > eol) {
				if(eol == endIndex)
					break;
				currStr = currStr.substring(eol + 1);
				endIndex = currStr.length();
				continue;
			}

			String item = currStr.substring(0, sep);
			String value = currStr.substring(sep+1, (eol - (sep+1)));
			System.out.println(String.format("set %s \"%s\"\n", item, value));

			cmd_set_internal(item, value);

			if(eol == endIndex)
				break;
			currStr = currStr.substring(eol + 1);
			endIndex = currStr.length();
		}

		return 1;
	}

	static int main_cmd(String[] argc)
	{
		if (argc.length < 1) {
			return help();
		}

		String cmd = argc[0];
		argc = removeFirstArg(argc);

		if (contains(cmd, "key")) {
			if (argc.length < 2) {
				return help();
			}
			Scanner scn = new Scanner(cmd);
			int lockkey = scn.nextInt();
			hd.tuner_lockkey_use_value(lockkey);

			cmd = argc[0];
			argc = removeFirstArg(argc);
		}

		if (contains(cmd, "get")) {
			if (argc.length < 1) {
				return help();
			}
			return cmd_get(argc[0]);
		}

		if (contains(cmd, "set")) {
			if (argc.length < 2) {
				return help();
			}
			return cmd_set(argc[0], argc[1]);
		}

		if (contains(cmd, "scan")) {
			if (argc.length < 1) {
				return help();
			}
			if (argc.length < 2) {
				return cmd_scan(argc[0], null);
			} else {
				return cmd_scan(argc[0], argc[1]);
			}
		}

		if (contains(cmd, "save")) {
			if (argc.length < 2) {
				return help();
			}
			return cmd_save(argc[0], argc[1]);
		}

		if (contains(cmd, "execute")) {
			return cmd_execute();
		}

		return help();
	}
	
	private static String[] removeFirstArg(String[] inArg) {
		
		String[] args = new String[inArg.length-1];
		for(int i = 0; i < args.length; ++i)
			args[i] = inArg[1+i];
		
		return args;
	}

	static int main_internal(String[] args)
	{
		//extract_appname(args[0]);
		//args = removeFirstArg(args);	

		if (args.length == 0) {
			return help();
		}

		final String id_str = args[0];
		args = removeFirstArg(args);	
		if (contains(id_str, "help")) {
			return help();
		}
		//dbg = new HDHomerun_Debug();
		if (contains(id_str, "discover")) {
			if (args.length < 1) {
				return discover_print(null);
			} else {
				return discover_print(args[0]);
			}
		}

		/* Device object. */
		try {
			hd = new HDHomerun_Device(id_str, dbg);
		} catch (Exception e) {
			System.out.println(String.format("invalid device id: %s\n", id_str));
			return -1;
		}
		if (null == hd) {
			System.out.println(String.format("invalid device id: %s\n", id_str));
			return -1;
		}

		/* Device ID check. */
		int device_id_requested = hd.get_device_id_requested();
		if (!HDHomerun_Discover.hdhomerun_discover_validate_device_id(device_id_requested)) {
			System.out.println(String.format("invalid device id: %08X\n", device_id_requested));
		}

		/* Connect to device and check model. */
		final String model = hd.get_model_str();
		if (null == model || model.length() == 0) {
			System.out.println("unable to connect to device\n");
			hd.destroy();
			return -1;
		}

		/* Command. */
		int ret = main_cmd(args);

		/* Cleanup. */
		hd.destroy();

		/* Complete. */
		return ret;
	}

	public static void main(String[] args)
	{
		int ret = main_internal(args);
		if (ret >= 0)
			System.out.println("\nSuccess\n");
		else
			System.out.println("\nFail\n");
		
		return;
	}

}
