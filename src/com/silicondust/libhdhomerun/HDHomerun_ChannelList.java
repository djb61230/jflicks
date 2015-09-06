package com.silicondust.libhdhomerun;

import com.silicondust.libhdhomerun.HDHomerun_Channels.hdhomerun_channel_entry_t;
import com.silicondust.libhdhomerun.HDHomerun_Channels.hdhomerun_channelmap_range_t;
import com.silicondust.libhdhomerun.HDHomerun_Channels.hdhomerun_channelmap_record_t;

public class HDHomerun_ChannelList {

	// extern LIBTYPE struct hdhomerun_channel_entry_t *hdhomerun_channel_list_first(struct hdhomerun_channel_list_t *channel_list);
	// extern LIBTYPE struct hdhomerun_channel_entry_t *hdhomerun_channel_list_last(struct hdhomerun_channel_list_t *channel_list);
	// extern LIBTYPE struct hdhomerun_channel_entry_t *hdhomerun_channel_list_next(struct hdhomerun_channel_list_t *channel_list, struct hdhomerun_channel_entry_t *entry);
	// extern LIBTYPE struct hdhomerun_channel_entry_t *hdhomerun_channel_list_prev(struct hdhomerun_channel_list_t *channel_list, struct hdhomerun_channel_entry_t *entry);
	
	public hdhomerun_channel_entry_t head;
	public hdhomerun_channel_entry_t tail;

	public long total_count() { // uint32_t
		
		long count = 0;

		hdhomerun_channel_entry_t entry = head;
		while (entry != null) {
			count++;
			entry = entry.next;
		}

		return count;
	}
	
	public long frequency_count() { // uint32_t
		
		long count = 0;
		long last_frequency = 0;

		hdhomerun_channel_entry_t entry = head;
		while (entry != null) {
			if (entry.frequency != last_frequency) {
				last_frequency = entry.frequency;
				count++;
			}

			entry = entry.next;
		}

		return count;
	}

	public long hdhomerun_channel_number_to_frequency(int channel_number) { // uint32_t, uint16_t 

		hdhomerun_channel_entry_t entry = head;
		while (entry != null) {
			if (entry.channel_number == channel_number) {
				return entry.frequency;
			}

			entry = entry.next;
		}

		return 0;
	}

	public int channel_frequency_to_number(long frequency) { // uint16_t, uint32_t  
		frequency = HDHomerun_Channels.hdhomerun_channel_frequency_round_normal(frequency);

		hdhomerun_channel_entry_t entry = head;
		while (entry != null) {
			if (entry.frequency == frequency) {
				return entry.channel_number;
			}
			if (entry.frequency > frequency) {
				return 0;
			}

			entry = entry.next;
		}

		return 0;
	}
	
	public HDHomerun_ChannelList(final String channelmap)	{
		
		int recIndex = 0;
		hdhomerun_channelmap_record_t record = HDHomerun_Channels.hdhomerun_channelmap_table[recIndex++];
		while (record.channelmap != null) {
			if (!channelmap.contains(record.channelmap)) {
				record = HDHomerun_Channels.hdhomerun_channelmap_table[recIndex++];
				continue;
			}

			build_ranges(record);
			record = HDHomerun_Channels.hdhomerun_channelmap_table[recIndex++];
		}
	}

	private void build_ranges(final hdhomerun_channelmap_record_t record) {
		
		int recIndex = 0;
		hdhomerun_channelmap_range_t range = record.range_list[recIndex++];
		while (range.frequency > 0) {
			build_range(record.channelmap, range);
			range = record.range_list[recIndex++];
		}
	}
	
	private void build_range(final String channelmap, final hdhomerun_channelmap_range_t range)
	{
		int channel_number;
		for (channel_number = range.channel_range_start; channel_number <= range.channel_range_end; channel_number++) {
			hdhomerun_channel_entry_t entry = new hdhomerun_channel_entry_t();
			entry.channel_number = channel_number;
			entry.frequency = range.frequency + ((long)(channel_number - range.channel_range_start) * range.spacing);
			entry.frequency = HDHomerun_Channels.hdhomerun_channel_frequency_round_normal(entry.frequency);
			entry.name = String.format("%s:%d", channelmap, entry.channel_number);

			build_insert(entry);
		}
	}
	
	private void build_insert(hdhomerun_channel_entry_t entry)
	{
		hdhomerun_channel_entry_t prev = null;
		hdhomerun_channel_entry_t next = head;

		while (next != null) {
			if (next.frequency > entry.frequency) {
				break;
			}

			prev = next;
			next = next.next;
		}

		entry.prev = prev;
		entry.next = next;

		if (prev != null) {
			prev.next = entry;
		} else {
			head = entry;
		}

		if (next != null) {
			next.prev = entry;
		} else {
			tail = entry;
		}
	}
}
