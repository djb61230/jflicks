package com.silicondust.libhdhomerun;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import com.silicondust.libhdhomerun.HDHomerun_Discover.hdhomerun_discover_device_t;

public class HDHomerun_Control {

	/*
	 * Create a control socket.
	 *
	 * This function will not attempt to connect to the device.
	 * The connection will be established when first used.
	 *
	 * uint32_t device_id = 32-bit device id of device. Set to HDHOMERUN_DEVICE_ID_WILDCARD to match any device ID.
	 * uint32_t device_ip = IP address of device. Set to 0 to auto-detect.
	 * struct hdhomerun_debug_t *dbg: Pointer to debug logging object. May be NULL.
	 *
	 * Returns a pointer to the newly created control socket.
	 *
	 * When no longer needed, the socket should be destroyed by calling hdhomerun_control_destroy.
	 */
	private final static int HDHOMERUN_CONTROL_CONNECT_TIMEOUT = 2500;
	private final static int HDHOMERUN_CONTROL_SEND_TIMEOUT = 2500;
	private final static int HDHOMERUN_CONTROL_RECV_TIMEOUT = 2500;
	//private final static int HDHOMERUN_CONTROL_UPGRADE_TIMEOUT = 20000;

	int desired_device_id = 0;
	int desired_device_ip = 0;
	int actual_device_id = 0;
	int actual_device_ip = 0;
	HDHomerun_Sock sock = null;
	public HDHomerun_Debug dbg = null;
	public HDHomerun_Pkt tx_pkt = new HDHomerun_Pkt();
	public HDHomerun_Pkt rx_pkt = new HDHomerun_Pkt();
	

	public void close_sock()
	{
		if (sock == null) {
			return;
		}

		sock.destroy();
		sock = null;
	}

	public void set_device(int device_id, int device_ip)
	{
		close_sock();

		desired_device_id = device_id;
		desired_device_ip = device_ip;
		actual_device_id = 0;
		actual_device_ip = 0;
	}

	public HDHomerun_Control(int device_id, int device_ip, HDHomerun_Debug dbg)
	{
		this.dbg = dbg;
		sock = null;
		set_device( device_id, device_ip);

	}

	public boolean connect_sock()
	{
		if (sock != null) {
			return true;
		}

		if ((desired_device_id == 0) && (desired_device_ip == 0)) {
			dbg.printf("hdhomerun_control_connect_sock: no device specified\n");
			return false;
		}
		if (HDHomerun_Discover.hdhomerun_discover_is_ip_multicast(desired_device_ip)) {
			dbg.printf("hdhomerun_control_connect_sock: cannot use multicast ip address for device operations\n");
			return false;
		}

		/* Find device. */
		hdhomerun_discover_device_t[] result = new hdhomerun_discover_device_t[1];
		if (HDHomerun_Discover.hdhomerun_discover_find_devices_custom(desired_device_ip, HDHomerun_Pkt.HDHOMERUN_DEVICE_TYPE_WILDCARD, desired_device_id, result, 1) <= 0) {
			dbg.printf("hdhomerun_control_connect_sock: device not found\n");
			return false;
		}
		actual_device_ip = result[0].ip_addr;
		actual_device_id = result[0].device_id;

		/* Create socket. */
		try {
			sock = new HDHomerun_Sock(actual_device_ip, HDHomerun_Pkt.HDHOMERUN_CONTROL_TCP_PORT, 0, 0, HDHOMERUN_CONTROL_CONNECT_TIMEOUT);
		} catch (IOException e) {
			dbg.printf(String.format("hdhomerun_control_connect_sock: failed to create socket (%s)\n", sock.getlasterror()));
			return false;
		}
		if (!sock.isValid()) {
			dbg.printf(String.format("hdhomerun_control_connect_sock: failed to create socket (%s)\n", sock.getlasterror()));
			close_sock();
			return false;
		}

		/* Success. */
		return true;
	}

	public  void destroy()
	{
		close_sock();
	}

	/*
	 * Get the actual device id or ip of the device.
	 *
	 * Returns 0 if the device id cannot be determined.
	 */
	int get_device_id()
	{
		if (!connect_sock()) {
			dbg.printf("hdhomerun_control_get_device_id: connect failed\n");
			return 0;
		}

		return actual_device_id;
	}
	
	public int get_device_ip()
	{
		if (!connect_sock()) {
			dbg.printf("hdhomerun_control_get_device_ip: connect failed\n");
			return 0;
		}

		return actual_device_ip;
	}

	
	public int get_device_id_requested()
	{
		return desired_device_id;
	}

	public int get_device_ip_requested()
	{
		return desired_device_ip;
	}

	/*
	 * Get the local machine IP address used when communicating with the device.
	 *
	 * This function is useful for determining the IP address to use with set target commands.
	 *
	 * Returns 32-bit IP address with native endianness, or 0 on error.
	 */
	int get_local_addr()
	{
		if (!connect_sock()) {
			dbg.printf("hdhomerun_control_get_local_addr: connect failed\n");
			return 0;
		}

		InetSocketAddress addr = sock.getsockname_addr();
		if (addr == null) {
			dbg.printf(String.format("hdhomerun_control_get_local_addr: getsockname failed (%s)\n", sock.getlasterror()));
			return 0;
		}

		return HDHomerun_Sock.IPAddr_BytesToInt(addr.getAddress().getAddress());
	}


	/*
	 * Low-level communication.
	 */
	private int send_recv_internal(HDHomerun_Pkt tx_pkt, HDHomerun_Pkt rx_pkt, short type, int recv_timeout)
	{
		tx_pkt.seal_frame(type);

		for (int i = 0; i < 2; i++) {
			if (sock == null) {
				if (!connect_sock()) {
					dbg.printf("hdhomerun_control_send_recv: connect failed\n");
					return -1;
				}
			}

			if (!send_sock(tx_pkt)) {
				continue;
			}
			if (null == rx_pkt) {
				return 1;
			}

			short[] rsp_type = new short[1];
			if (!recv_sock(rx_pkt, rsp_type, recv_timeout)) {
				continue;
			}
			if (rsp_type[0] != type + 1) {
				dbg.printf("hdhomerun_control_send_recv: unexpected frame type\n");
				close_sock();
				continue;
			}

			return 1;
		}

		dbg.printf("hdhomerun_control_send_recv: failed\n");
		return -1;
	}

	public int send_recv(HDHomerun_Pkt tx_pkt, HDHomerun_Pkt rx_pkt, short type)
	{
		return send_recv_internal(tx_pkt, rx_pkt, type, HDHOMERUN_CONTROL_RECV_TIMEOUT);
	}

	private boolean send_sock(HDHomerun_Pkt tx_pkt)
	{
		if (!sock.send(tx_pkt.buffer, tx_pkt.startIndex, tx_pkt.endIndex - tx_pkt.startIndex, HDHOMERUN_CONTROL_SEND_TIMEOUT)) {
			dbg.printf(String.format("hdhomerun_control_send_sock: send failed (%s)\n", sock.getlasterror()));
			close_sock();
			return false;
		}

		return true;
	}

	private boolean recv_sock(HDHomerun_Pkt rx_pkt, short[] ptype, long recv_timeout)
	{
		long stop_time = HDHomerun_OS.getcurrenttime() + recv_timeout;
		rx_pkt.reset();

		while (true) {
			long current_time = HDHomerun_OS.getcurrenttime();
			if (current_time >= stop_time) {
				dbg.printf("hdhomerun_control_recv_sock: timeout\n");
				close_sock();
				return false;
			}

			int[] length = new int[1];
			length[0] = rx_pkt.limitIndex - rx_pkt.endIndex;
			if (!sock.recv(rx_pkt.buffer, rx_pkt.endIndex, length, (int) (stop_time - current_time))) {
				dbg.printf(String.format("hdhomerun_control_recv_sock: recv failed (%s)\n", sock.getlasterror()));
				close_sock();
				return false;
			}

			rx_pkt.endIndex += length[0];

			int ret = rx_pkt.open_frame(ptype);
			if (ret < 0) {
				dbg.printf("hdhomerun_control_recv_sock: frame error\n");
				close_sock();
				return false;
			}
			if (ret > 0) {
				return true;
			}
		}
	}


	/*
	 * Get/set a control variable on the device.
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
	public int get(final String name, StringBuilder pvalue, StringBuilder perror)
	{
		return get_set(name, null, 0, pvalue, perror);
	}

	public int set(final String name, final String value, StringBuilder pvalue, StringBuilder perror)
	{
		return get_set(name, value, 0, pvalue, perror);
	}

	public int set_with_lockkey(final String name, final String value, int lockkey, StringBuilder pvalue, StringBuilder perror)
	{
		return get_set(name, value, lockkey, pvalue, perror);
	}
	
	private byte[] getStringBytesInUTF8(final String str) {
		
		try {
			byte[] bytes = str.getBytes("UTF8");
			byte[] ret = new byte[bytes.length + 1];
			ret[bytes.length] = '\0';
			for(int i = 0; i < bytes.length; ++i)
				ret[i] = bytes[i];
			return ret;
		} catch (UnsupportedEncodingException e) {
			return null;
		}	
	}

	private int get_set(final String name, final String value, int lockkey, StringBuilder pvalue, StringBuilder perror)
	{
		/* Request. */
		tx_pkt.reset();

		byte[] nameBytes = getStringBytesInUTF8(name);
		short name_len = (short) nameBytes.length;
		if (tx_pkt.endIndex + 3 + name_len > tx_pkt.limitIndex) {
			dbg.printf("hdhomerun_control_get_set: request too long\n");
			return -1;
		}
		tx_pkt.write_u8(HDHomerun_Pkt.HDHOMERUN_TAG_GETSET_NAME);
		tx_pkt.write_var_length(name_len);
		tx_pkt.write_mem(nameBytes, name_len);

		if (value != null && value.length() > 0) {
			byte[] valueBytes = getStringBytesInUTF8(value);
			short value_len = (short) (value.length() + 1);
			if (tx_pkt.endIndex + 3 + value_len > tx_pkt.limitIndex) {
				dbg.printf("hdhomerun_control_get_set: request too long\n");
				return -1;
			}
			tx_pkt.write_u8(HDHomerun_Pkt.HDHOMERUN_TAG_GETSET_VALUE);
			tx_pkt.write_var_length(value_len);
			tx_pkt.write_mem(valueBytes, value_len);
			
		}

		if (lockkey != 0) {
			if (tx_pkt.endIndex + 6 > tx_pkt.limitIndex) {
				dbg.printf("hdhomerun_control_get_set: request too long\n");
				return -1;
			}
			tx_pkt.write_u8(HDHomerun_Pkt.HDHOMERUN_TAG_GETSET_LOCKKEY);
			tx_pkt.write_var_length((short) 4);
			tx_pkt.write_u32(lockkey);
		}

		/* Send/Recv. */
		if (send_recv_internal(tx_pkt, rx_pkt, HDHomerun_Pkt.HDHOMERUN_TYPE_GETSET_REQ, HDHOMERUN_CONTROL_RECV_TIMEOUT) < 0) {
			dbg.printf("hdhomerun_control_get_set: send/recv error\n");
			return -1;
		}

		/* Response. */
		while (true) {
			byte[] tag = new byte[1];
			int[] len = new int[1];
			int next = rx_pkt.read_tlv(tag, len);
			if (0 == next) {
				break;
			}

			switch (tag[0]) {
			case HDHomerun_Pkt.HDHOMERUN_TAG_GETSET_VALUE:
				if (pvalue != null) {
					for(int i = 0; i < len[0] - 1; ++i)
						pvalue.append((char) rx_pkt.buffer[rx_pkt.posIndex + i]);
				}
				if (perror != null) {
					perror = new StringBuilder("");
				}
				return 1;

			case HDHomerun_Pkt.HDHOMERUN_TAG_ERROR_MESSAGE:
				
				if (perror != null) {
					for(int i = 0; i < len[0] - 1; ++i)
						perror.append((char) rx_pkt.buffer[rx_pkt.posIndex + i]);
					dbg.printf(String.format("hdhomerun_control_get_set: %s\n", perror));
				}
				rx_pkt.buffer[rx_pkt.posIndex + len[0]] = 0;
				

				if (pvalue != null) {
					pvalue = new StringBuilder("");
				}
				

				return 0;
			}

			rx_pkt.posIndex = next;
		}

		dbg.printf("hdhomerun_control_get_set: missing response tags\n");
		return -1;
	}

}
