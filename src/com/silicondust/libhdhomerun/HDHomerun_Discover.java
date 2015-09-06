package com.silicondust.libhdhomerun;

import com.silicondust.libhdhomerun.HDHomerun_Sock;
import com.silicondust.libhdhomerun.HDHomerun_Sock.hdhomerun_local_ip_info_t;



public class HDHomerun_Discover {


	public class hdhomerun_discover_device_t {
		public int ip_addr; // uint32_t
		public int device_type; // uint32_t
		public int device_id; // uint32_t
		public short tuner_count; // uint8_t 

		public hdhomerun_discover_device_t() {
			reset();
		}
		
		boolean compare(hdhomerun_discover_device_t r) {
			
			return (ip_addr == r.ip_addr & device_type == r.device_type && device_id == r.device_id && tuner_count == r.tuner_count);
		}
		
		void reset() {
			ip_addr = 0;
			device_type = 0;
			device_id = 0;
			tuner_count = 0;
		}
	};
	
	private final static int HDHOMERUN_DISOCVER_MAX_SOCK_COUNT = 16;

	private static class hdhomerun_discover_sock_t {
		HDHomerun_Sock sock;
		boolean detected; // bool_t 
		int local_ip; // uint32_t 
		int subnet_mask; // uint32_t 
		
		public void set(hdhomerun_discover_sock_t r) {
			sock = r.sock;
			detected = r.detected;
			local_ip = r.local_ip;
			subnet_mask = r.subnet_mask;
		}
	};

	private hdhomerun_discover_sock_t[] socks = new hdhomerun_discover_sock_t[HDHOMERUN_DISOCVER_MAX_SOCK_COUNT];
	private int sock_count = 0; // unsigned int 
	private HDHomerun_Pkt tx_pkt = new HDHomerun_Pkt();
	private HDHomerun_Pkt rx_pkt = new HDHomerun_Pkt();
	private boolean mIsValid = false;

	private boolean sock_add(int local_ip, int subnet_mask)
	{
		for (int i = 1; i < sock_count; i++) {
			hdhomerun_discover_sock_t dss = socks[i];

			if ((dss.local_ip == local_ip) && (dss.subnet_mask == subnet_mask)) {
				dss.detected = true;
				return true;
			}
		}

		if (sock_count >= HDHOMERUN_DISOCVER_MAX_SOCK_COUNT) {
			return false;
		}

		/* Create socket. */
		HDHomerun_Sock sock = null;
		try {
			sock = new HDHomerun_Sock(local_ip, (short) 0, 0, 0, false);
		} catch (Exception e) {
			if(local_ip != 0)
				return false;
		}
		finally {
			if (local_ip > 0 && (sock == null || !sock.isValid())) {
				return false;
			}
		}


		/* Write sock entry. */
		socks[sock_count] = new hdhomerun_discover_sock_t();
		hdhomerun_discover_sock_t dss = socks[sock_count++];
		dss.sock = sock;
		dss.detected = true;
		dss.local_ip = local_ip;
		dss.subnet_mask = subnet_mask;

		return true;
	}

	/*
	 * Optional: persistent discover instance available for discover polling use.
	 */
	public HDHomerun_Discover()
	{
		/* Create a routable socket (always first entry). */
		if (sock_add(0, 0)) {
			/* Success. */
			mIsValid = true;
		}		
	}

	public boolean isValid() {
		
		return mIsValid;
	}
	public void destroy()
	{
		for (int i = 0; i < sock_count; i++) {
			hdhomerun_discover_sock_t dss = socks[i];
			dss.sock.destroy();
			socks[i] = null;
		}

	}
	
	private void sock_detect()
	{
		for (int i = 1; i < sock_count; i++) 
			socks[i].detected = false;

		hdhomerun_local_ip_info_t[] ip_info_list = new hdhomerun_local_ip_info_t[HDHOMERUN_DISOCVER_MAX_SOCK_COUNT];
		int count = HDHomerun_Sock.hdhomerun_local_ip_info(ip_info_list, HDHOMERUN_DISOCVER_MAX_SOCK_COUNT);
		if (count < 0) {
			count = 0;
		}

		for (int index = 0; index < count; index++) {
			hdhomerun_local_ip_info_t ip_info = ip_info_list[index];
			sock_add(ip_info.ip_addr, ip_info.subnet_mask);
		}

		hdhomerun_discover_sock_t src = socks[1];
		int currSrc = 1;
		hdhomerun_discover_sock_t dst = socks[1];
		int currDst = 1;
		count = 1;
		for (int i = 1; i < sock_count; i++) {
			if (!src.detected) {
				src.sock.destroy();
				src = socks[currSrc++];
				continue;
			}
			if (dst != src) {
				dst.set(src);
			}
			src = socks[currSrc++];
			dst = socks[currDst++];
			count++;
		}

		sock_count = count;
	}

	public int find_devices(int target_ip, int device_type, int device_id, hdhomerun_discover_device_t result_list[], int max_count)
	{
		sock_detect();

		int count = 0;
		int attempt;
		for (attempt = 0; attempt < 2; attempt++) {
			if (!send(target_ip, device_type, device_id)) {
				return -1;
			}

			long timeout = HDHomerun_OS.getcurrenttime() + 200;
			while (true) {
				result_list[count] = new hdhomerun_discover_device_t(); 
				hdhomerun_discover_device_t result = result_list[count];
				result.reset();

				if (!recv(result)) {
					if (HDHomerun_OS.getcurrenttime() >= timeout) {
						break;
					}
					HDHomerun_OS.msleep_approx(10);
					continue;
				}

				/* Filter. */
				if (device_type != HDHomerun_Pkt.HDHOMERUN_DEVICE_TYPE_WILDCARD) {
					if (device_type != result.device_type) {
						continue;
					}
				}
				if (device_id != HDHomerun_Pkt.HDHOMERUN_DEVICE_ID_WILDCARD) {
					if (device_id != result.device_id) {
						continue;
					}
				}

				/* Ensure not already in list. */
				if (null != find_in_list(result_list, count, result)) {
					continue;
				}

				/* Add to list. */
				count++;
				if (count >= max_count) {
					return count;
				}
			}
		}

		return count;
	}

	private boolean send_target_ip(int target_ip, int device_type, int device_id)
	{
		boolean result = false;

		/*
		 * Send targeted packet from any local ip that is in the same subnet.
		 * This will work with multiple separate 169.254.x.x interfaces.
		 */
		for (int i = 1; i < sock_count; i++) {
			hdhomerun_discover_sock_t dss = socks[i];
			if ((target_ip & dss.subnet_mask) != (dss.local_ip & dss.subnet_mask)) {
				continue;
			}

			result |= send_internal(dss, target_ip, device_type, device_id);
		}

		/*
		 * If target IP does not match a local subnet then fall back to letting the OS choose the gateway interface.
		 */
		if (!result) {
			hdhomerun_discover_sock_t dss = socks[0];
			result = send_internal(dss, target_ip, device_type, device_id);
		}

		return result;
	}
	
	private boolean send_internal(hdhomerun_discover_sock_t dss, int target_ip, int device_type, int device_id)
	{
		tx_pkt.reset();

		tx_pkt.write_u8(HDHomerun_Pkt.HDHOMERUN_TAG_DEVICE_TYPE);
		tx_pkt.write_var_length((short) 4);
		tx_pkt.write_u32(device_type);
		tx_pkt.write_u8(HDHomerun_Pkt.HDHOMERUN_TAG_DEVICE_ID);
		tx_pkt.write_var_length((short) 4);
		tx_pkt.write_u32(device_id);
		tx_pkt.seal_frame(HDHomerun_Pkt.HDHOMERUN_TYPE_DISCOVER_REQ);

		return dss.sock.sendto(target_ip, HDHomerun_Pkt.HDHOMERUN_DISCOVER_UDP_PORT, tx_pkt.buffer, tx_pkt.startIndex, tx_pkt.endIndex - tx_pkt.startIndex, 0);
	}

	private boolean send_wildcard_ip(int device_type, int device_id)
	{
		boolean result = false;

		/*
		 * Send subnet broadcast using each local ip socket.
		 * This will work with multiple separate 169.254.x.x interfaces.
		 */
		for (int i = 1; i < sock_count; i++) {
			hdhomerun_discover_sock_t dss = socks[i];
			int target_ip = dss.local_ip | ~dss.subnet_mask;
			result |= send_internal(dss, target_ip, device_type, device_id);
		}

		/*
		 * If no local ip sockets then fall back to sending a global broadcast letting the OS choose the interface.
		 */
		if (!result) {
			hdhomerun_discover_sock_t dss = socks[0];
			result = send_internal(dss, 0xFFFFFFFF, device_type, device_id);
		}

		return result;
	}


	public boolean send(int target_ip, int device_type, int device_id)
	{
		if (target_ip == 0) {
			return send_wildcard_ip(device_type, device_id);
		} else {
			return send_target_ip(target_ip, device_type, device_id);
		}
	}
	
	private hdhomerun_discover_device_t find_in_list(hdhomerun_discover_device_t result_list[], int count, hdhomerun_discover_device_t lookup)
	{
		for (int index = 0; index < count; index++) {
			hdhomerun_discover_device_t entry = result_list[index];
			if (entry.compare(lookup)) {
				return entry;
			}
		}

		return null;
	}

	
	private boolean recv_internal(hdhomerun_discover_sock_t dss, hdhomerun_discover_device_t result)
	{
		rx_pkt.reset();

		int[] remote_addr = new int[1];
		int[] remote_port = new int[1];
		int[] length = new int[1];
		length[0]= rx_pkt.limitIndex - rx_pkt.endIndex;
		if (!dss.sock.recvfrom(remote_addr, remote_port, rx_pkt.buffer, rx_pkt.endIndex, length, 0)) {
			return false;
		}

		rx_pkt.endIndex += length[0];

		short[] type = new short[1];
		if (rx_pkt.open_frame(type) <= 0) {
			return false;
		}
		if (type[0] != HDHomerun_Pkt.HDHOMERUN_TYPE_DISCOVER_RPY) {
			return false;
		}

		result.ip_addr = remote_addr[0];
		result.device_type = 0;
		result.device_id = 0;
		result.tuner_count = 0;

		while (true) {
			
			byte[] tag = new byte[1];
			int[] len = new int[1];
			int next = rx_pkt.read_tlv(tag, len);
			if (next == 0) {
				break;
			}

			switch (tag[0]) {
			case HDHomerun_Pkt.HDHOMERUN_TAG_DEVICE_TYPE:
				if (len[0] != 4) {
					break;
				}
				result.device_type = rx_pkt.read_u32();
				break;

			case HDHomerun_Pkt.HDHOMERUN_TAG_DEVICE_ID:
				if (len[0] != 4) {
					break;
				}
				result.device_id = rx_pkt.read_u32();
				break;

			case HDHomerun_Pkt.HDHOMERUN_TAG_TUNER_COUNT:
				if (len[0] != 1) {
					break;
				}
				result.tuner_count = rx_pkt.read_u8();
				break;

			default:
				break;
			}

			rx_pkt.posIndex = (short) next;
		}

		/* Fixup for old firmware. */
		if (result.tuner_count == 0) {
			switch (result.device_id >> 20) {
			case 0x102:
				result.tuner_count = 1;
				break;

			case 0x100:
			case 0x101:
			case 0x121:
				result.tuner_count = 2;
				break;

			default:
				break;
			}
		}

		return true;
	}

	private boolean recv(hdhomerun_discover_device_t result)
	{

		for (int i = 0; i < sock_count; i++) {
			hdhomerun_discover_sock_t dss = socks[i];

			if (recv_internal(dss, result)) {
				return true;
			}
		}

		return false;
	}


	/*
	 * Verify that the device ID given is valid.
	 *
	 * The device ID contains a self-check sequence that detects common user input errors including
	 * single-digit errors and two digit transposition errors.
	 *
	 * Returns TRUE if valid.
	 * Returns FALSE if not valid.
	 */
	private static final int[] lookup_table = new int[] {0xA, 0x5, 0xF, 0x6, 0x7, 0xC, 0x1, 0xB, 0x9, 0x2, 0x8, 0xD, 0x4, 0x3, 0xE, 0x0};
	public static boolean hdhomerun_discover_validate_device_id(int device_id)
	{
		int checksum = 0;

		checksum ^= lookup_table[(device_id >> 28) & 0x0F];
		checksum ^= (device_id >> 24) & 0x0F;
		checksum ^= lookup_table[(device_id >> 20) & 0x0F];
		checksum ^= (device_id >> 16) & 0x0F;
		checksum ^= lookup_table[(device_id >> 12) & 0x0F];
		checksum ^= (device_id >> 8) & 0x0F;
		checksum ^= lookup_table[(device_id >> 4) & 0x0F];
		checksum ^= (device_id >> 0) & 0x0F;

		return (checksum == 0);
	}

	/*
	 * Detect if an IP address is multicast.
	 *
	 * Returns TRUE if multicast.
	 * Returns FALSE if zero, unicast, expermental, or broadcast.
	 */
	public static boolean hdhomerun_discover_is_ip_multicast(int ip_addr)
	{
		return (ip_addr >= 0xE0000000) && (ip_addr < 0xF0000000);
	}

	/*
	 * Find devices.
	 *
	 * The device information is stored in caller-supplied array of hdhomerun_discover_device_t vars.
	 * Multiple attempts are made to find devices.
	 * Execution time is typically 400ms if max_count is not reached.
	 *
	 * Set target_ip to zero to auto-detect the IP address.
	 * Set device_type to HDHOMERUN_DEVICE_TYPE_TUNER to detect HDHomeRun tuner devices.
	 * Set device_id to HDHOMERUN_DEVICE_ID_WILDCARD to detect all device ids.
	 *
	 * Returns the number of devices found.
	 * Retruns -1 on error.
	 */
	public static int hdhomerun_discover_find_devices_custom(int target_ip, int device_type, int device_id, hdhomerun_discover_device_t result_list[], int max_count)
	{
		if (hdhomerun_discover_is_ip_multicast(target_ip)) {
			return 0;
		}

		HDHomerun_Discover ds = new HDHomerun_Discover();
		if (null == ds || !ds.isValid()) {
			return -1;
		}

		int ret = ds.find_devices(target_ip, device_type, device_id, result_list, max_count);
		ds = null;
		
		return ret;
	}


}
