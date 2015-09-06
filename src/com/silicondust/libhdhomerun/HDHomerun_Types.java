package com.silicondust.libhdhomerun;

public class HDHomerun_Types {

	public final static int HDHOMERUN_STATUS_COLOR_NEUTRAL  = 0xFFFFFFFF;
	public final static int HDHOMERUN_STATUS_COLOR_RED		= 0xFFFF0000;
	public final static int HDHOMERUN_STATUS_COLOR_YELLOW	= 0xFFFFFF00;
	public final static int HDHOMERUN_STATUS_COLOR_GREEN	= 0xFF00C000;

	public static final int HDHOMERUN_CHANNELSCAN_MAX_PROGRAM_COUNT = 64;
	
	public static class hdhomerun_tuner_status_t {
		public String channel;
		public String lock_str;
		public boolean signal_present; // bool_t
		public boolean lock_supported; // bool_t
		public boolean lock_unsupported; // bool_t
		public long signal_strength; //uint
		public long signal_to_noise_quality; //uint
		public long symbol_error_quality; //uint
		public long raw_bits_per_second; //uint32_t
		public long packets_per_second; //uint32_t
		
		void reset() {
			
			channel = "";
			lock_str = "";
			signal_present = false;
			lock_supported = false;
			lock_unsupported = false;
			signal_strength = 0;
			signal_to_noise_quality = 0;
			symbol_error_quality = 0;
			raw_bits_per_second = 0;
			packets_per_second = 0;
		}
	};

	public static class hdhomerun_tuner_vstatus_t {
		public String vchannel;
		public String name;
		public String auth;
		public String cci;
		public String cgms;
		public boolean not_subscribed; // bool_t
		public boolean not_available; // bool_t
		public boolean copy_protected; // bool_t
		
		public void reset() {
			vchannel = "";
			name = "";
			auth = "";
			cci = "";
			cgms = "";
			not_subscribed = false;
			not_available = false;
			copy_protected = false;
		}
	};

	public static class hdhomerun_channelscan_program_t {
		public String program_str; // char[64];
		public int program_number; // uint16_t
		public int virtual_major; // uint16_t
		public int virtual_minor; // uint16_t
		public int type; // uint16_t
		public String name; // char[32];
		
		public hdhomerun_channelscan_program_t() {
			program_str = "";
			program_number = 0;
			virtual_major = 0;
			type = 0;
			name = "";
		}
		
		public boolean compare(hdhomerun_channelscan_program_t t) {
			
			if(program_str.compareTo(t.program_str) != 0 || program_number != t.program_number ||
					virtual_major != t.virtual_major || type != t.type || name.compareTo(t.name) != 0)
				return false;
			return true;
		}
		
		public hdhomerun_channelscan_program_t(hdhomerun_channelscan_program_t t) {
			program_str = new String(t.program_str);
			program_number = t.program_number;
			virtual_major = t.virtual_major;
			type = t.type;
			name = new String(t.name);
		}
	};

	public static class hdhomerun_channelscan_result_t {
		public String channel_str; // new char[64];
		public long channelmap; // uint32_t
		public long frequency; // uint32_t
		public hdhomerun_tuner_status_t status = new hdhomerun_tuner_status_t();
		public int program_count;
		public hdhomerun_channelscan_program_t[] programs = new hdhomerun_channelscan_program_t[HDHOMERUN_CHANNELSCAN_MAX_PROGRAM_COUNT];
		public boolean transport_stream_id_detected; // bool_t
		public int transport_stream_id; // uint16_t
		
		public void clean() {
			channel_str = "";
			channelmap = 0;
			frequency = 0;
			status = new hdhomerun_tuner_status_t();
			program_count = 0;
			programs = new hdhomerun_channelscan_program_t[HDHOMERUN_CHANNELSCAN_MAX_PROGRAM_COUNT];
			transport_stream_id_detected = false;
			transport_stream_id = 0;
			
			
		}
	};

	public static class hdhomerun_plotsample_t {
		short real; // int16_t
		short imag; // int16_t
		
		public hdhomerun_plotsample_t(short r, short i) {
			real = r;
			imag = i;
		}
	};

}
