package com.silicondust.libhdhomerun;

public final class HDHomerun_Channels {

	public static class hdhomerun_channel_entry_t {
		hdhomerun_channel_entry_t next;
		hdhomerun_channel_entry_t prev;
		long frequency; // uint32_t 
		int channel_number; //uint16_t 
		String name; // char[16];
		
		public hdhomerun_channel_entry_t() {
			
		}
	};

	public static class hdhomerun_channelmap_range_t {
		int channel_range_start; // uint16_t 
		int channel_range_end; // uint16_t 
		long frequency; // uint32_t 
		long spacing; // uint32_t 
		
		public hdhomerun_channelmap_range_t(int start, int end, int freq, int spac) {
			channel_range_start = start;
			channel_range_end = end;
			frequency = freq;
			spacing = spac;
		}
	};

	public static class hdhomerun_channelmap_record_t {
		final String channelmap;
		final hdhomerun_channelmap_range_t[] range_list;
		final String channelmap_scan_group;
		final String countrycodes;
		
		public hdhomerun_channelmap_record_t(final String map, final hdhomerun_channelmap_range_t[] list, final String group, final String countrycds) {
			channelmap = map;
			range_list = list;
			channelmap_scan_group = group;
			countrycodes = countrycds;			
		}
	};

	/* AU antenna channels. Channels {6, 7, 8, 9, 9A} are numbered {5, 6, 7, 8, 9} by the HDHomeRun. */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_au_bcast = new hdhomerun_channelmap_range_t[] {
		new hdhomerun_channelmap_range_t(5,  12, 177500000, 7000000),
		new hdhomerun_channelmap_range_t( 21,  69, 480500000, 7000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* EU antenna channels. */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_eu_bcast = {
		new hdhomerun_channelmap_range_t(  5,  12, 177500000, 7000000),
		new hdhomerun_channelmap_range_t( 21,  69, 474000000, 8000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* EU cable channels. No common standard - use frequency in MHz for channel number. */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_eu_cable = {
		new hdhomerun_channelmap_range_t( 50, 998,  50000000, 1000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* US antenna channels. */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_us_bcast = {
		new hdhomerun_channelmap_range_t(  2,   4,  57000000, 6000000),
		new hdhomerun_channelmap_range_t(  5,   6,  79000000, 6000000),
		new hdhomerun_channelmap_range_t(  7,  13, 177000000, 6000000),
		new hdhomerun_channelmap_range_t( 14,  69, 473000000, 6000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* US cable channels. */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_us_cable = {
		new hdhomerun_channelmap_range_t(  2,   4,  57000000, 6000000),
		new hdhomerun_channelmap_range_t(  5,   6,  79000000, 6000000),
		new hdhomerun_channelmap_range_t(  7,  13, 177000000, 6000000),
		new hdhomerun_channelmap_range_t( 14,  22, 123000000, 6000000),
		new hdhomerun_channelmap_range_t( 23,  94, 219000000, 6000000),
		new hdhomerun_channelmap_range_t( 95,  99,  93000000, 6000000),
		new hdhomerun_channelmap_range_t(100, 158, 651000000, 6000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* US cable channels (HRC). */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_us_hrc = {
		new hdhomerun_channelmap_range_t(  2,   4,  55752700, 6000300),
		new hdhomerun_channelmap_range_t(  5,   6,  79753900, 6000300),
		new hdhomerun_channelmap_range_t(  7,  13, 175758700, 6000300),
		new hdhomerun_channelmap_range_t( 14,  22, 121756000, 6000300),
		new hdhomerun_channelmap_range_t( 23,  94, 217760800, 6000300),
		new hdhomerun_channelmap_range_t( 95,  99,  91754500, 6000300),
		new hdhomerun_channelmap_range_t(100, 158, 649782400, 6000300),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	/* US cable channels (IRC). */
	public static final hdhomerun_channelmap_range_t[] hdhomerun_channelmap_range_us_irc = {
		new hdhomerun_channelmap_range_t(  2,   4,  57012500, 6000000),
		new hdhomerun_channelmap_range_t(  5,   6,  81012500, 6000000),
		new hdhomerun_channelmap_range_t(  7,  13, 177012500, 6000000),
		new hdhomerun_channelmap_range_t( 14,  22, 123012500, 6000000),
		new hdhomerun_channelmap_range_t( 23,  41, 219012500, 6000000),
		new hdhomerun_channelmap_range_t( 42,  42, 333025000, 6000000),
		new hdhomerun_channelmap_range_t( 43,  94, 339012500, 6000000),
		new hdhomerun_channelmap_range_t( 95,  97,  93012500, 6000000),
		new hdhomerun_channelmap_range_t( 98,  99, 111025000, 6000000),
		new hdhomerun_channelmap_range_t(100, 158, 651012500, 6000000),
		new hdhomerun_channelmap_range_t(  0,   0,         0,       0)
	};

	public static final hdhomerun_channelmap_record_t[] hdhomerun_channelmap_table = {
		new hdhomerun_channelmap_record_t("au-bcast", hdhomerun_channelmap_range_au_bcast, "au-bcast",               "AU"), 
		new hdhomerun_channelmap_record_t("au-cable", hdhomerun_channelmap_range_eu_cable, "au-cable",               "AU"),
		new hdhomerun_channelmap_record_t("eu-bcast", hdhomerun_channelmap_range_eu_bcast, "eu-bcast",               "EU PA"),
		new hdhomerun_channelmap_record_t("eu-cable", hdhomerun_channelmap_range_eu_cable, "eu-cable",               "EU"),
		new hdhomerun_channelmap_record_t("tw-bcast", hdhomerun_channelmap_range_us_bcast, "tw-bcast",               "TW"),
		new hdhomerun_channelmap_record_t("tw-cable", hdhomerun_channelmap_range_us_cable, "tw-cable",               "TW"),

		new hdhomerun_channelmap_record_t("us-bcast", hdhomerun_channelmap_range_us_bcast, "us-bcast",               "CA US"),
		new hdhomerun_channelmap_record_t("us-cable", hdhomerun_channelmap_range_us_cable, "us-cable us-hrc us-irc", "CA PA US"),
		new hdhomerun_channelmap_record_t("us-hrc",   hdhomerun_channelmap_range_us_hrc  , "us-cable us-hrc us-irc", "CA PA US"),
		new hdhomerun_channelmap_record_t("us-irc",   hdhomerun_channelmap_range_us_irc,   "us-cable us-hrc us-irc", "CA PA US"),

		new hdhomerun_channelmap_record_t(null,       null,                                null,                     null)
	};


	public static String hdhomerun_channelmap_get_channelmap_from_country_source(final String countrycode, final String source) 	{
		boolean country_found = false;

		int recIndex = 0;
		hdhomerun_channelmap_record_t record = hdhomerun_channelmap_table[recIndex++];
		while (record.channelmap != null && record.channelmap.length() > 0) {
			if (!record.countrycodes.contains(countrycode)) {
				record = hdhomerun_channelmap_table[recIndex++];
				continue;
			}

			if (record.channelmap.contains(source)) {
				return record.channelmap;
			}

			country_found = true;
			record = hdhomerun_channelmap_table[recIndex++];
		}

		if (!country_found) {
			return hdhomerun_channelmap_get_channelmap_from_country_source("EU", source);
		}

		return null;
	}
	
	public static final String hdhomerun_channelmap_get_channelmap_scan_group(final String channelmap) {
		
		int recIndex = 0;
		hdhomerun_channelmap_record_t record = hdhomerun_channelmap_table[recIndex++];		
		while (record.channelmap != null && record.channelmap.length() > 0) {
			if (channelmap.contains(record.channelmap)) {
				return record.channelmap_scan_group;
			}
			record = hdhomerun_channelmap_table[recIndex++];
		}

		return null;
	}

	// extern LIBTYPE uint16_t hdhomerun_channel_entry_channel_number(struct hdhomerun_channel_entry_t *entry);
	// extern LIBTYPE uint32_t hdhomerun_channel_entry_frequency(struct hdhomerun_channel_entry_t *entry);
	// extern LIBTYPE const char *hdhomerun_channel_entry_name(struct hdhomerun_channel_entry_t *entry);

	public static long hdhomerun_channel_frequency_round(long frequency, long resolution) { // uint32_t, uint32_t, uint32_t
	
		frequency += resolution / 2;
		return (frequency / resolution) * resolution;
	}

	public static long hdhomerun_channel_frequency_round_normal(long frequency) { // uint32_t , uint32_t 
	
		return hdhomerun_channel_frequency_round(frequency, 125000);
	}
	
}
