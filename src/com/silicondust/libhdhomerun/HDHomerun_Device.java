package com.silicondust.libhdhomerun;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;

import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_result_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_plotsample_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_tuner_status_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_tuner_vstatus_t;
import com.silicondust.libhdhomerun.HDHomerun_Video.hdhomerun_video_stats_t;

public final class HDHomerun_Device {
	
	public static final int HDHOMERUN_DEVICE_MAX_TUNE_TO_LOCK_TIME = 1500;
	public static final int HDHOMERUN_DEVICE_MAX_LOCK_TO_DATA_TIME = 2000;
	public static final int HDHOMERUN_DEVICE_MAX_TUNE_TO_DATA_TIME = (HDHOMERUN_DEVICE_MAX_TUNE_TO_LOCK_TIME + HDHOMERUN_DEVICE_MAX_LOCK_TO_DATA_TIME);

	public static final String HDHOMERUN_TARGET_PROTOCOL_UDP = "udp";
	public static final String HDHOMERUN_TARGET_PROTOCOL_RTP = "rtp";

	private HDHomerun_Control mCS;
	private HDHomerun_Video mVS;
	private HDHomerun_Debug mDbg;
	private HDHomerun_ChannelScan mScan;
	private int mMulticast_ip;
	private int mMulticast_port;
	private int mDevice_id;
	private int mTuner;
	private int mLockkey;
	private String mName;
	private String mModel;
	
	
	 @Override
	   public boolean equals(Object obj) {
	       if (this == obj)
	           return true;
	       if (obj == null)
	           return false;
	       if (getClass() != obj.getClass())
	           return false;
	       final HDHomerun_Device other = (HDHomerun_Device) obj;	
	       return (mDevice_id == other.mDevice_id && other.mTuner == mTuner);			
	}
	/*
	 * Create a device object.
	 *
	 * Typically a device object will be created for each tuner.
	 * It is valid to have multiple device objects communicating with a single HDHomeRun.
	 *
	 * For example, a threaded application that streams video from 4 tuners (2 HDHomeRun devices) and has
	 * GUI feedback to the user of the selected tuner might use 5 device objects: 4 for streaming video
	 * (one per thread) and one for the GUI display that can switch between tuners.
	 *
	 * This function will not attempt to connect to the device. The connection will be established when first used.
	 *
	 * uint32_t device_id = 32-bit device id of device. Set to HDHOMERUN_DEVICE_ID_WILDCARD to match any device ID.
	 * uint32_t device_ip = IP address of device. Set to 0 to auto-detect.
	 * unsigned int tuner = tuner index (0 or 1). Can be changed later by calling hdhomerun_device_set_tuner.
	 * struct hdhomerun_debug_t *dbg: Pointer to debug logging object. May be NULL.
	 *
	 * Returns a pointer to the newly created device object.
	 *
	 * When no longer needed, the socket should be destroyed by calling hdhomerun_device_destroy.
	 *
	 * The hdhomerun_device_create_from_str function creates a device object from the given device_str.
	 * The device_str parameter can be any of the following forms:
	 *     <device id>
	 *     <device id>-<tuner index>
	 *     <ip address>
	 * If the tuner index is not included in the device_str then it is set to zero. Use hdhomerun_device_set_tuner
	 * or hdhomerun_device_set_tuner_from_str to set the tuner.
	 *
	 * The hdhomerun_device_set_tuner_from_str function sets the tuner from the given tuner_str.
	 * The tuner_str parameter can be any of the following forms:
	 *     <tuner index>
	 *     /tuner<tuner index>
	 */
	public HDHomerun_Device(int device_id, int device_ip, int tuner, HDHomerun_Debug dbg) throws Exception
	{
		createDevice(device_id, device_ip, tuner, dbg);
	}
	
	private void createDevice(int device_id, int device_ip, int tuner, HDHomerun_Debug dbg) throws Exception
	{
		mDbg = dbg;

		if ((device_id == 0) && (device_ip == 0) && (tuner == 0)) {
			throw new Exception();
		}

		if (set_device(device_id, device_ip) <= 0) {
			throw new Exception();
		}
		if (set_tuner(tuner) <= 0) {
			throw new Exception();
		}
	}

	private static boolean is_hex_char(char c)
	{
		if ((c >= '0') && (c <= '9')) {
			return true;
		}
		if ((c >= 'A') && (c <= 'F')) {
			return true;
		}
		if ((c >= 'a') && (c <= 'f')) {
			return true;
		}
		return false;
	}

	
	public HDHomerun_Device(final String device_str, HDHomerun_Debug dbg) throws Exception
	{
		int i;
		for (i = 0; i < 8; i++) {
			if (!is_hex_char(device_str.charAt(i))) {
				throw new Exception();
			}
		}

		if (device_str.length() == 8) {
			
			Scanner scn = new Scanner(device_str);
			int device_id = scn.nextInt(16);
			if (device_id == 0) {
				throw new Exception();
			}
			createDevice(device_id, 0, 0, dbg);
			return;
		}

		if (device_str.length() > 8 && device_str.charAt(8) == '-') {
			
			Scanner scn = new Scanner(device_str);
			int device_id = scn.nextInt(16);
			scn.next("-");
			int tuner = scn.nextInt();
			if (0 == device_id || 0 == tuner) {
				throw new Exception();
			}
			createDevice(device_id, 0, tuner, dbg);
			return;
		}

		throw new Exception();
	}


	public void destroy()
	{
		mScan = null;
		if (null != mVS) {
			mVS.destroy();
			mVS = null;
		}

		if (null != mCS) {
			mCS.destroy();
			mCS = null;
		}

	}

	/*
	 * Get the device id, ip, or tuner of the device instance.
	 */
	public final String get_name()
	{
		return mName;
	}
	
	public int get_device_id()
	{
		return mDevice_id;
	}
	
	public int get_device_ip()
	{
		if (mMulticast_ip != 0) {
			return mMulticast_ip;
		}
		if (null != mCS) {
			return mCS.get_device_ip();
		}

		return 0;
	}

	public int get_device_id_requested()
	{
		if (mMulticast_ip != 0) {
			return 0;
		}
		if (null != mCS) {
			return mCS.get_device_id_requested();
		}

		return 0;
	}

	public int get_device_ip_requested()
	{
		if (mMulticast_ip != 0) {
			return mMulticast_ip;
		}
		if (mCS != null) {
			return mCS.get_device_ip_requested();
		}

		return 0;
	}


	public long get_tuner()
	{
		return mTuner;
	}
	
	public int set_device(int device_id, int device_ip)
	{
		if ((device_id == 0) && (device_ip == 0)) {
			mDbg.printf("hdhomerun_device_set_device: device not specified\n");
			return -1;
		}

		if (HDHomerun_Discover.hdhomerun_discover_is_ip_multicast(device_ip)) {
			return set_device_multicast(device_ip);
		}

		return set_device_normal(device_id, device_ip);
	}
	
	private int set_device_normal(int device_id, int device_ip)
	{
		if (null == mCS) {
			mCS = new HDHomerun_Control(0, 0, mDbg);
			if (null == mCS) {
				mDbg.printf("hdhomerun_device_set_device: failed to create control object\n");
				return -1;
			}
		}

		mCS.set_device(device_id, device_ip);

		if ((device_id == 0) || (device_id == HDHomerun_Pkt.HDHOMERUN_DEVICE_ID_WILDCARD)) {
			device_id = mCS.get_device_id();
		}

		mMulticast_ip = 0;
		mMulticast_port = 0;
		mDevice_id = device_id;
		mTuner = 0;
		mLockkey = 0;

		mName = String.format("%08X-%d", mDevice_id, mTuner);
		mModel = "";

		return 1;
	}


	private int set_device_multicast(int multicast_ip)
	{
		if (null != mCS) {
			mCS.destroy();
			mCS = null;
		}

		mMulticast_ip = multicast_ip;
		mMulticast_port = 0;
		mDevice_id = 0;
		mTuner = 0;
		mLockkey = 0;

		int ip = multicast_ip;
		mName = String.format("%d.%d.%d.%d", (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, (ip >> 0) & 0xFF);
		mModel = "multicast";

		return 1;
	}

	
	public int set_tuner(int tuner)
	{
		if (mMulticast_ip != 0) {
			if (mTuner != 0) {
				mDbg.printf("hdhomerun_device_set_tuner: tuner cannot be specified in multicast mode\n");
				return -1;
			}

			return 1;
		}

		mTuner = tuner;
		mName = String.format("%08X-%d", mDevice_id, mTuner);

		return 1;
	}

	public int set_tuner_from_str(final String tuner_str)
	{
		Scanner scn = new Scanner(tuner_str);
		int tuner = scn.nextInt();
		if (tuner != 0 || 0 == tuner_str.compareTo("0")) {
			set_tuner(tuner);
			return 1;
		}
		
		if (null != scn.findInLine("/tuner")) {
			tuner = scn.nextInt();
			if(tuner > 0){
				set_tuner(tuner);
				return 1;
			}
		}

		return -1;
	}


	/*
	 * Get the local machine IP address used when communicating with the device.
	 *
	 * This function is useful for determining the IP address to use with set target commands.
	 *
	 * Returns 32-bit IP address with native endianness, or 0 on error.
	 */
	public int get_local_machine_addr()
	{
		if (null != mCS) {
			return mCS.get_local_addr();
		}

		return 0;
	}

	/*
	 * Get operations.
	 *
	 * struct hdhomerun_tuner_status_t *status = Pointer to caller supplied status struct to be populated with result.
	 * const char **p<name> = Caller supplied char * to be updated to point to the result string. The string will remain
	 *		valid until another call to a device function.
	 *
	 * Returns 1 if the operation was successful.
	 * Returns 0 if the operation was rejected.
	 * Returns -1 if a communication error occurred.
	 */
	public int get_tuner_status(StringBuilder pstatus_str, hdhomerun_tuner_status_t status)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_status: device not set\n");
			return -1;
		}

		status.reset();
		String name = String.format("/tuner%d/status", mTuner);

		StringBuilder status_str_b = new StringBuilder();
		int ret = mCS.get(name, status_str_b, null);
		if (ret <= 0) {
			return ret;
		}
		String status_str = new String(status_str_b);

		if (pstatus_str != null) {
			pstatus_str = status_str_b;
		}

		if (status != null) {
			Scanner scn = new Scanner(status_str);
			if (null != scn.findInLine("ch=")) {
				status.channel = String.format("%31s", scn.next());
			}
			scn = new Scanner(status_str);

			if (null != scn.findInLine("lock=")) {
				status.lock_str = scn.next();
			}

			status.signal_strength = get_status_parse(status_str, "ss=");
			status.signal_to_noise_quality = get_status_parse(status_str, "snq=");
			status.symbol_error_quality = get_status_parse(status_str, "seq=");
			status.raw_bits_per_second = get_status_parse(status_str, "bps=");
			status.packets_per_second = get_status_parse(status_str, "pps=");

			status.signal_present = (status.signal_strength >= 45);

			if (status.lock_str.compareTo("none") != 0) {
				if (status.lock_str.charAt(0) == '(') {
					status.lock_unsupported = true;
				} else {
					status.lock_supported = true;
				}
			}
		}

		return 1;
	}
	
	private int get_status_parse(final String status_str, final String tag)
	{
		Scanner scn = new Scanner(status_str); 
		if (null == scn.findInLine(tag)) {
			return 0;
		}

		return scn.nextInt();
	}

	
	public int get_tuner_vstatus(StringBuilder pvstatus_str, hdhomerun_tuner_vstatus_t vstatus)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_vstatus: device not set\n");
			return -1;
		}

		vstatus.reset();

		String var_name = String.format("/tuner%d/vstatus", mTuner);

		StringBuilder vstatus_str_b = new StringBuilder();
		int ret = mCS.get(var_name, vstatus_str_b, null);
		if (ret <= 0) {
			return ret;
		}

		String vstatus_str = new String(vstatus_str_b);
		if (pvstatus_str != null) {
			pvstatus_str = vstatus_str_b;
		}

		if (vstatus != null) {
			Scanner scn = new Scanner(vstatus_str);
			if (null != scn.findInLine("vch=")) {
				vstatus.vchannel = scn.next();
				scn = new Scanner(vstatus_str);
			}

			if (null != scn.findInLine("name=")) {
				vstatus.name = scn.next();
				scn = new Scanner(vstatus_str);
			}

			if (null != scn.findInLine("auth=")) {
				vstatus.auth = scn.next();
				scn = new Scanner(vstatus_str);
			}

			if (null != scn.findInLine("cci=")) {
				vstatus.cci = scn.next();
				scn = new Scanner(vstatus_str);
			}

			if (null != scn.findInLine("cgms=")) {
				vstatus.cgms = scn.next();
				scn = new Scanner(vstatus_str);
			}

			if (vstatus.auth.compareTo("not-subscribed") == 0) {
				vstatus.not_subscribed = true;
			}			
			if (vstatus.auth.compareTo("error") == 0) {
				vstatus.not_available = true;
			}
			if (vstatus.auth.compareTo("dialog") == 0) {
				vstatus.not_available = true;
			}
			if (vstatus.cci.compareTo("protected") == 0) {
				vstatus.copy_protected = true;
			}
			if (vstatus.cgms.compareTo("protected") == 0) {
				vstatus.copy_protected = true;
			}
		}

		return 1;
	}

	public int get_tuner_streaminfo(StringBuilder pstreaminfo)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_streaminfo: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/streaminfo", mTuner);
		return mCS.get(name, pstreaminfo, null);
	}


	public int get_tuner_channel(StringBuilder pchannel)
	{
		if (mCS == null) {
			mDbg.printf("hdhomerun_device_get_tuner_channel: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/channel", mTuner);
		return mCS.get(name, pchannel, null);
	}

	public int get_tuner_vchannel(StringBuilder pvchannel)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_vchannel: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/vchannel", mTuner);
		return mCS.get(name, pvchannel, null);
	}

	public int get_tuner_channelmap(StringBuilder pchannelmap)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_channelmap: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/channelmap", mTuner);
		return mCS.get(name, pchannelmap, null);
	}


	public int get_tuner_filter(StringBuilder pfilter)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_filter: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/filter", mTuner);
		return mCS.get(name, pfilter, null);
	}

	public int get_tuner_program(StringBuilder pprogram)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_program: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/program", mTuner);
		return mCS.get(name, pprogram, null);
	}


	public int get_tuner_target(StringBuilder ptarget)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_target: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/target", mTuner);
		return mCS.get(name, ptarget, null);
	}
	
	public int get_tuner_plotsample(List<hdhomerun_plotsample_t> samples)
	{
		if (mCS == null) {
			mDbg.printf("hdhomerun_device_get_tuner_plotsample: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/plotsample", mTuner);
		return get_tuner_plotsample_internal(name, samples);
	}

	public int get_oob_plotsample(List<hdhomerun_plotsample_t> samples)
	{
		if (mCS == null) {
			mDbg.printf("hdhomerun_device_get_oob_plotsample: device not set\n");
			return -1;
		}

		return get_tuner_plotsample_internal("/oob/plotsample", samples);
	}

	private int get_tuner_plotsample_internal(final String name, List<hdhomerun_plotsample_t> samples)
	{
		StringBuilder result_b = new StringBuilder();
		int ret = mCS.get(name, result_b, null);
		if (ret <= 0) {
			return ret;
		}
		
		String result = new String(result_b);
		Scanner scn = new Scanner(result);
		while (true) {
			
			String token = scn.findInLine(" ");
			if (null == token) {
				break;
			}

			Scanner tokScn = new Scanner(token);

			int raw = tokScn.nextInt();
			if (raw == 0) {
				break;
			}

			short real = (short) ((raw >> 12) & 0x0FFF);
			if (0 < (real & 0x0800))
				real |= 0xF000;

			short imag = (short) ((raw >> 0) & 0x0FFF);
			if (0 < (imag & 0x0800)) 
				imag |= 0xF000;

			samples.add(new HDHomerun_Types.hdhomerun_plotsample_t(real, imag));
		}

		return 1;
	}

	public int get_tuner_lockkey_owner(StringBuilder powner)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_tuner_lockkey_owner: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/lockkey", mTuner);
		return mCS.get(name, powner, null);
	}

	public int get_ir_target(StringBuilder ptarget)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_ir_target: device not set\n");
			return -1;
		}

		return mCS.get("/ir/target", ptarget, null);
	}

	public int get_lineup_location(StringBuilder plocation)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_lineup_location: device not set\n");
			return -1;
		}

		return mCS.get("/lineup/location", plocation, null);
	}

	public int get_version(StringBuilder pversion_str, int[] pversion_num)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_version: device not set\n");
			return -1;
		}

		StringBuilder version_str_b = new StringBuilder();
		int ret = mCS.get("/sys/version", version_str_b, null);
		if (ret <= 0) {
			return ret;
		}
		
		String version_str = new String(version_str_b);

		if (pversion_str != null) {
			pversion_str = version_str_b;
		}

		if (null != pversion_num) {
			
			Scanner scn = new Scanner(version_str);
			pversion_num[0] = scn.nextInt();
		}

		return 1;
	}
	
	public int get_supported(final String prefix, StringBuilder pstr)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_channel: device not set\n");
			return -1;
		}

		StringBuilder features = new StringBuilder();
		int ret = mCS.get("/sys/features", features, null);
		if (ret <= 0) {
			return ret;
		}

		if (null == prefix) {
			pstr = features;
			return 1;
		}

		int index = features.indexOf(prefix);
		if (index == -1)
			return 0;
		index += prefix.length();
		
		pstr.append(features.substring(index));
		if(pstr.charAt(pstr.length()-1) == '\n')
			pstr.setCharAt(pstr.length()-1, (char) 0);


		return 1;
	}


	private static boolean hdhomerun_device_get_tuner_status_lock_is_bcast(hdhomerun_tuner_status_t status)
	{
		if (status.lock_str.compareTo("8vsb") == 0) {
			return true;
		}
		if (status.lock_str.substring(0, 2).compareTo("t8") == 0) {
			return true;
		}
		if (status.lock_str.substring(0, 2).compareTo("t7") == 0) {
			return true;
		}
		if (status.lock_str.substring(0, 2).compareTo("t6") == 0) {
			return true;
		}

		return false;
	}

	public static int hdhomerun_device_get_tuner_status_ss_color(hdhomerun_tuner_status_t status)
	{
		int ss_yellow_min;
		int ss_green_min;

		if (!status.lock_supported) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_NEUTRAL;
		}

		if (hdhomerun_device_get_tuner_status_lock_is_bcast(status)) {
			ss_yellow_min = 50;	/* -30dBmV */
			ss_green_min = 75;	/* -15dBmV */
		} else {
			ss_yellow_min = 80;	/* -12dBmV */
			ss_green_min = 90;	/* -6dBmV */
		}

		if (status.signal_strength >= ss_green_min) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_GREEN;
		}
		if (status.signal_strength >= ss_yellow_min) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_YELLOW;
		}

		return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_RED;
	}

	public static int hdhomerun_device_get_tuner_status_snq_color(hdhomerun_tuner_status_t status)
	{
		if (status.signal_to_noise_quality >= 70) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_GREEN;
		}
		if (status.signal_to_noise_quality >= 50) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_YELLOW;
		}

		return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_RED;
	}

	public static int hdhomerun_device_get_tuner_status_seq_color(hdhomerun_tuner_status_t status)
	{
		if (status.symbol_error_quality >= 100) {
			return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_GREEN;
		}

		return HDHomerun_Types.HDHOMERUN_STATUS_COLOR_RED;
	}

	public final String get_model_str()
	{
		if (null != mModel && mModel.length() > 0) {
			return mModel;
		}

		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_model_str: device not set\n");
			return null;
		}

		StringBuilder model_str = new StringBuilder();;
		int ret = mCS.get("/sys/model", model_str, null);
		if (ret < 0) {
			return null;
		}
		if (ret == 0) {
			mModel = "hdhomerun_atsc";
			return mModel;
		}

		mModel = new String(model_str);
		return mModel;
	}

	/*
	 * Set operations.
	 *
	 * const char *<name> = String to send to device.
	 *
	 * Returns 1 if the operation was successful.
	 * Returns 0 if the operation was rejected.
	 * Returns -1 if a communication error occurred.
	 */
	public int set_tuner_channel(final String channel)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_channel: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/channel", mTuner);
		return mCS.set_with_lockkey(name, channel, mLockkey, null, null);
	}

	public int set_tuner_vchannel(final String vchannel)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_vchannel: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/vchannel", mTuner);
		return mCS.set_with_lockkey(name, vchannel, mLockkey, null, null);
	}


	
	public int set_tuner_channelmap(final String channelmap)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_channelmap: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/channelmap", mTuner);
		return mCS.set_with_lockkey(name, channelmap, mLockkey, null, null);
	}

	public int set_tuner_filter(final String filter)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_filter: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/filter", mTuner);
		return mCS.set_with_lockkey(name, filter, mLockkey, null, null);
	}
	
	private boolean set_tuner_filter_by_array_append(byte[] pptr, int[] currPos, int endPos, int range_begin, int range_end)
	{
		
		int available = endPos - currPos[0];
		int required;

		byte[] append = null;
		
		try {
			if (range_begin == range_end)
				append = String.format("0x%04x", range_begin).getBytes("UTF8");
			else 
				append = String.format("0x%04x-0x%04x ", range_begin, range_end).getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {

			return false;
		}
		
				
		required = append.length + 1;
		if (required > available) {
			return false;
		}
		
		for(int i = 0; i < required; ++i)
			pptr[currPos[0]++] = append[i];
			
		return true;
	}

	// expects array of 0x2000
	public int set_tuner_filter_by_array(byte[] filter_array)
	{
		byte[] filter = new byte[1024];
		int[] currPos = new int[1];
		currPos[0] = 0;
		final int end = 1024;

		int range_begin = 0xFFFF;
		int range_end = 0xFFFF;

		int len = filter_array.length - 1; //0x1FFF
		for (int i = 0; i <= len; i++) {
			if (0 == filter_array[i]) {
				if (range_begin == 0xFFFF) {
					continue;
				}
				if (!set_tuner_filter_by_array_append(filter, currPos, end, range_begin, range_end)) {
					return 0;
				}
				range_begin = 0xFFFF;
				range_end = 0xFFFF;
				continue;
			}

			if (range_begin == 0xFFFF) {
				range_begin = i;
				range_end = i;
				continue;
			}

			range_end = i;
		}

		if (range_begin != 0xFFFF) {
			if (!set_tuner_filter_by_array_append(filter, currPos, end, range_begin, range_end)) {
				return 0;
			}
		}

		/* Remove trailing space. */
		if (filter[currPos[0]] == ' ') {
			filter[currPos[0]] = 0;
		}

		try {
			return set_tuner_filter(new String(filter, "UTF8"));
		} catch (UnsupportedEncodingException e) {
			return 0;
		}
	}

	public int set_tuner_program(final String program)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_program: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/program", mTuner);
		return mCS.set_with_lockkey(name, program, mLockkey, null, null);
	}

	public int set_tuner_target(final String target)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_target: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/target", mTuner);
		return mCS.set_with_lockkey(name, target, mLockkey, null, null);
	}

	
	public int set_ir_target(final String target)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_ir_target: device not set\n");
			return -1;
		}

		return mCS.set("/ir/target", target, null, null);
	}

	public int set_lineup_location(final String location)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_lineup_location: device not set\n");
			return -1;
		}

		return mCS.set("/lineup/location", location, null, null);
	}

	public int set_sys_dvbc_modulation(final String modulation_list)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_sys_dvbc_modulation: device not set\n");
			return -1;
		}

		return mCS.set("/sys/dvbc_modulation", modulation_list, null, null);
	}

	/*
	 * Get/set a named control variable on the device.
	 *
	 * const char *name: The name of var to get/set (c-string). The supported vars is device/firmware dependant.
	 * const char *value: The value to set (c-string). The format is device/firmware dependant.

	 * char **pvalue: If provided, the caller-supplied char pointer will be populated with a pointer to the value
	 *		string returned by the device, or NULL if the device returned an error string. The string will remain
	 *		valid until the next call to a control sock function.
	 * char **perror: If provided, the caller-supplied char pointer will be populated with a pointer to the error
	 *		string returned by the device, or NULL if the device returned an value string. The string will remain
	 *		valid until the next call to a control sock function.
	 *
	 * Returns 1 if the operation was successful (pvalue set, perror NULL).
	 * Returns 0 if the operation was rejected (pvalue NULL, perror set).
	 * Returns -1 if a communication error occurs.
	 */
	public int get_var(final String name, StringBuilder pvalue, StringBuilder perror)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_get_var: device not set\n");
			return -1;
		}

		return mCS.get(name, pvalue, perror);
	}

	public int set_var(final String name, final String value, StringBuilder pvalue, StringBuilder perror)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_var: device not set\n");
			return -1;
		}

		return mCS.set_with_lockkey(name, value, mLockkey, pvalue, perror);
	}

	/*
	 * Tuner locking.
	 *
	 * The hdhomerun_device_tuner_lockkey_request function is used to obtain a lock
	 * or to verify that the hdhomerun_device object still holds the lock.
	 * Returns 1 if the lock request was successful and the lock was obtained.
	 * Returns 0 if the lock request was rejected.
	 * Returns -1 if a communication error occurs.
	 *
	 * The hdhomerun_device_tuner_lockkey_release function is used to release a
	 * previously held lock. If locking is used then this function must be called
	 * before destroying the hdhomerun_device object.
	 */
	public int tuner_lockkey_request(StringBuilder perror)
	{
		if (mMulticast_ip != 0) {
			return 1;
		}
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_tuner_lockkey_request: device not set\n");
			return -1;
		}

		int new_lockkey = Math.abs(HDHomerun_OS.random_get32());

		String name = String.format("/tuner%d/lockkey", mTuner);
		String new_lockkey_str = String.format("%d", new_lockkey);
		int ret = mCS.set_with_lockkey(name, new_lockkey_str, mLockkey, null, perror);
		if (ret <= 0) {
			mLockkey = 0;
			return ret;
		}

		mLockkey = new_lockkey;
		return ret;
	}

	public int tuner_lockkey_release()
	{
		if (mMulticast_ip != 0) {
			return 1;
		}
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_tuner_lockkey_release: device not set\n");
			return -1;
		}

		if (mLockkey == 0) {
			return 1;
		}

		String name = String.format("/tuner%d/lockkey", mTuner);
		int ret = mCS.set_with_lockkey(name, "none", mLockkey, null, null);

		mLockkey = 0;
		return ret;
	}

	public int tuner_lockkey_force()
	{
		if (mMulticast_ip != 0) {
			return 1;
		}
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_tuner_lockkey_force: device not set\n");
			return -1;
		}

		String name = String.format("/tuner%d/lockkey", mTuner);
		int ret = mCS.set(name, "force", null, null);

		mLockkey = 0;
		return ret;
	}


	/*
	 * Intended only for non persistent connections; eg, hdhomerun_config.
	 */
	public void tuner_lockkey_use_value(int lockkey)
	{
		if (mMulticast_ip != 0) {
			return;
		}

		mLockkey = lockkey;
	}


	/*
	 * Wait for tuner lock after channel change.
	 *
	 * The hdhomerun_device_wait_for_lock function is used to detect/wait for a lock vs no lock indication
	 * after a channel change.
	 *
	 * It will return quickly if a lock is aquired.
	 * It will return quickly if there is no signal detected.
	 * Worst case it will time out after 1.5 seconds - the case where there is signal but no lock.
	 */
	public int wait_for_lock(hdhomerun_tuner_status_t status)
	{
		/* Delay for SS reading to be valid (signal present). */
		HDHomerun_OS.msleep_minimum(250);

		/* Wait for up to 2.5 seconds for lock. */
		long timeout = HDHomerun_OS.getcurrenttime() + 2500;
		while (true) {
			/* Get status to check for lock. Quality numbers will not be valid yet. */
			int ret = get_tuner_status(null, status);
			if (ret <= 0) {
				return ret;
			}

			if (!status.signal_present) {
				return 1;
			}
			if (status.lock_supported || status.lock_unsupported) {
				return 1;
			}

			if (HDHomerun_OS.getcurrenttime() >= timeout) {
				return 1;
			}

			HDHomerun_OS.msleep_approx(250);
		}
	}


	/*
	 * Stream a filtered program or the unfiltered stream.
	 *
	 * The hdhomerun_device_stream_start function initializes the process and tells the device to start streamin data.
	 *
	 * uint16_t program_number = The program number to filer, or 0 for unfiltered.
	 *
	 * Returns 1 if the oprtation started successfully.
	 * Returns 0 if the operation was rejected.
	 * Returns -1 if a communication error occurs.
	 *
	 * The hdhomerun_device_stream_recv function should be called periodically to receive the stream data.
	 * The buffer can losslessly store 1 second of data, however a more typical call rate would be every 15ms.
	 *
	 * The hdhomerun_device_stream_stop function tells the device to stop streaming data.
	 */
	public int stream_start()
	{
		get_video_sock();
		if (null == mVS) {
			return -1;
		}

		/* Set target. */
		if (mMulticast_ip != 0) {
			int ret = mVS.join_multicast_group(mMulticast_ip);
			if (ret <= 0) {
				return ret;
			}
		} else {
			int ret = set_tuner_target_to_local(HDHOMERUN_TARGET_PROTOCOL_RTP);
			if (ret == 0) {
				ret = set_tuner_target_to_local(HDHOMERUN_TARGET_PROTOCOL_UDP);
			}
			if (ret <= 0) {
				return ret;
			}
		}

		/* Flush video buffer. */
		HDHomerun_OS.msleep_minimum(64);
		mVS.flush();

		/* Success. */
		return 1;
	}
	
	private int set_tuner_target_to_local(final String protocol)
	{
		if (null == mCS) {
			mDbg.printf("hdhomerun_device_set_tuner_target_to_local: device not set\n");
			return -1;
		}
		if (null == mVS) {
			mDbg.printf("hdhomerun_device_set_tuner_target_to_local: video not initialized\n");
			return -1;
		}

		/* Set target. */
		int local_ip = mCS.get_local_addr();
		int local_port = mVS.get_local_port();
		String target = String.format("%s://%d.%d.%d.%d:%d",
			protocol,
			(int)(local_ip >> 24) & 0xFF, (int)(local_ip >> 16) & 0xFF,
			(int)(local_ip >> 8) & 0xFF, (int)(local_ip >> 0) & 0xFF,
			(int)local_port
		);

		return set_tuner_target(target);
	}


	public byte[] stream_recv(int max_size, int[] pactual_size)
	{
		if (null == mVS) {
			mDbg.printf("hdhomerun_device_stream_recv: video not initialized\n");
			return null;
		}

		return mVS.recv(max_size, pactual_size);
	}

	public void stream_flush()
	{
		if (null == mVS) {
			mDbg.printf("hdhomerun_device_stream_flush: video not initialized\n");
			return;
		}

		mVS.flush();
	}

	public void stream_stop()
	{
		if (null == mVS) {
			mDbg.printf("hdhomerun_device_stream_stop: video not initialized\n");
			return;
		}

		if (mMulticast_ip != 0) {
			mVS.leave_multicast_group();
		} else {
			set_tuner_target("none");
		}
	}

	/*
	 * Channel scan API.
	 */
	public int channelscan_init(final String channelmap)
	{
		try {
			mScan = new HDHomerun_ChannelScan(this, channelmap);
		} catch (Exception e) {
			mScan = null;
		}
		if (null == mScan) {
			mDbg.printf("hdhomerun_device_channelscan_init: failed to create scan object\n");
			return -1;
		}

		return 1;
	}

	public int channelscan_advance(hdhomerun_channelscan_result_t result)
	{
		if (mScan == null) {
			mDbg.printf("hdhomerun_device_channelscan_advance: scan not initialized\n");
			return 0;
		}

		int ret = mScan.advance(result);
		if (ret <= 0) { /* Free scan if normal finish or fatal error */
			mScan = null;
		}

		return ret;
	}

	public int channelscan_detect(hdhomerun_channelscan_result_t result)
	{
		if (mScan == null) {
			mDbg.printf("hdhomerun_device_channelscan_detect: scan not initialized\n");
			return 0;
		}

		int ret = mScan.detect(result);
		if (ret < 0) { /* Free scan if fatal error */
			mScan = null;
		}

		return ret;
	}

	public byte channelscan_get_progress()
	{
		if (mScan == null) {
			mDbg.printf("hdhomerun_device_channelscan_get_progress: scan not initialized\n");
			return 0;
		}

		return mScan.get_progress();
	}

	/*
	 * Upload new firmware to the device.
	 *
	 * FILE *upgrade_file: File pointer to read from. The file must have been opened in binary mode for reading.
	 *
	 * Returns 1 if the upload succeeded.
	 * Returns 0 if the upload was rejected.
	 * Returns -1 if an error occurs.
	 */
	//extern LIBTYPE int hdhomerun_device_upgrade(struct hdhomerun_device_t *hd, FILE *upgrade_file);

	/*
	 * Low level accessor functions. 
	 */
	public HDHomerun_Control get_control_sock()
	{
		return mCS;
	}


	public HDHomerun_Video get_video_sock()
	{
		if (mVS != null) {
			return mVS;
		}

		try {
			mVS = new HDHomerun_Video(mMulticast_port, HDHomerun_Video.VIDEO_DATA_BUFFER_SIZE_1S * 2, (0 != mMulticast_port), mDbg);
		} catch (Exception e) {
			mVS = null;
		}
		if (null == mVS) {
			mDbg.printf("hdhomerun_device_get_video_sock: failed to create video object\n");
			return null;
		}

		return mVS;
	}


	/*
	 * Debug print internal stats.
	 */
	public void debug_print_video_stats()
	{
		if (!mDbg.enabled()) {
			return;
		}

		if (null != mCS) {
			String name = String.format("/tuner%d/debug", mTuner);

			StringBuilder debug_str = new StringBuilder();
			StringBuilder error_str = new StringBuilder();
			int ret = mCS.get(name, debug_str, error_str);
			if (ret < 0) {
				mDbg.printf("video dev: communication error getting debug stats\n");
				return;
			}

			if (error_str != null && error_str.length() > 0) {
				mDbg.printf(String.format("video dev: %s\n", error_str));
			} else {
				mDbg.printf(String.format("video dev: %s\n", debug_str));
			}
		}

		if (mVS != null) {
			mVS.debug_print_stats();
		}
	}

	public void get_video_stats(hdhomerun_video_stats_t stats)
	{
		if (mVS == null) {
			mDbg.printf("hdhomerun_device_stream_flush: video not initialized\n");
			stats.reset();
			return;
		}

		mVS.get_stats(stats);
	}

}
