package com.silicondust.libhdhomerun;

public final class HDHomerun_Pkt {

	/*
	 * The discover protocol (UDP port 65001) and control protocol (TCP port 65001)
	 * both use the same packet based format:
	 *	uint16_t	Packet type
	 *	uint16_t	Payload length (bytes)
	 *	uint8_t[]	Payload data (0-n bytes).
	 *	uint32_t	CRC (Ethernet style 32-bit CRC)
	 *
	 * All variables are big-endian except for the crc which is little-endian.
	 *
	 * Valid values for the packet type are listed below as defines prefixed
	 * with "HDHOMERUN_TYPE_"
	 *
	 * Discovery:
	 *
	 * The payload for a discovery request or reply is a simple sequence of
	 * tag-length-value data:
	 *	uint8_t		Tag
	 *	varlen		Length
	 *	uint8_t[]	Value (0-n bytes)
	 *
	 * The length field can be one or two bytes long.
	 * For a length <= 127 bytes the length is expressed as a single byte. The
	 * most-significant-bit is clear indicating a single-byte length.
	 * For a length >= 128 bytes the length is expressed as a sequence of two bytes as follows:
	 * The first byte is contains the least-significant 7-bits of the length. The
	 * most-significant bit is then set (add 0x80) to indicate that it is a two byte length.
	 * The second byte contains the length shifted down 7 bits.
	 *
	 * A discovery request packet has a packet type of HDHOMERUN_TYPE_DISCOVER_REQ and should
	 * contain two tags: HDHOMERUN_TAG_DEVICE_TYPE and HDHOMERUN_TAG_DEVICE_ID.
	 * The HDHOMERUN_TAG_DEVICE_TYPE value should be set to HDHOMERUN_DEVICE_TYPE_TUNER.
	 * The HDHOMERUN_TAG_DEVICE_ID value should be set to HDHOMERUN_DEVICE_ID_WILDCARD to match
	 * all devices, or to the 32-bit device id number to match a single device.
	 *
	 * The discovery response packet has a packet type of HDHOMERUN_TYPE_DISCOVER_RPY and has the
	 * same format as the discovery request packet with the two tags: HDHOMERUN_TAG_DEVICE_TYPE and
	 * HDHOMERUN_TAG_DEVICE_ID. In the future additional tags may also be returned - unknown tags
	 * should be skipped and not treated as an error.
	 *
	 * Control get/set:
	 *
	 * The payload for a control get/set request is a simple sequence of tag-length-value data
	 * following the same format as for discover packets.
	 *
	 * A get request packet has a packet type of HDHOMERUN_TYPE_GETSET_REQ and should contain
	 * the tag: HDHOMERUN_TAG_GETSET_NAME. The HDHOMERUN_TAG_GETSET_NAME value should be a sequence
	 * of bytes forming a null-terminated string, including the NULL. The TLV length must include
	 * the NULL character so the length field should be set to strlen(str) + 1.
	 *
	 * A set request packet has a packet type of HDHOMERUN_TYPE_GETSET_REQ (same as a get request)
	 * and should contain two tags: HDHOMERUN_TAG_GETSET_NAME and HDHOMERUN_TAG_GETSET_VALUE.
	 * The HDHOMERUN_TAG_GETSET_NAME value should be a sequence of bytes forming a null-terminated
	 * string, including the NULL.
	 * The HDHOMERUN_TAG_GETSET_VALUE value  should be a sequence of bytes forming a null-terminated
	 * string, including the NULL.
	 *
	 * The get and set reply packets have the packet type HDHOMERUN_TYPE_GETSET_RPY and have the same
	 * format as the set request packet with the two tags: HDHOMERUN_TAG_GETSET_NAME and
	 * HDHOMERUN_TAG_GETSET_VALUE. A set request is also implicit get request so the updated value is
	 * returned.
	 *
	 * If the device encounters an error handling the get or set request then the get/set reply packet
	 * will contain the tag HDHOMERUN_TAG_ERROR_MESSAGE. The format of the value is a sequence of
	 * bytes forming a null-terminated string, including the NULL.
	 *
	 * In the future additional tags may also be returned - unknown tags should be skipped and not
	 * treated as an error.
	 *
	 * Security note: The application should not rely on the NULL character being present. The
	 * application should write a NULL character based on the TLV length to protect the application
	 * from a potential attack.
	 *
	 * Firmware Upgrade:
	 *
	 * A firmware upgrade packet has a packet type of HDHOMERUN_TYPE_UPGRADE_REQ and has a fixed format:
	 *	uint32_t	Position in bytes from start of file.
	 *	uint8_t[256]	Firmware data (256 bytes)
	 *
	 * The data must be uploaded in 256 byte chunks and must be uploaded in order.
	 * The position number is in bytes so will increment by 256 each time.
	 *
	 * When all data is uploaded it should be signaled complete by sending another packet of type
	 * HDHOMERUN_TYPE_UPGRADE_REQ with payload of a single uint32_t with the value 0xFFFFFFFF.
	 */

	public final static int HDHOMERUN_DISCOVER_UDP_PORT = 65001;
	public final static int HDHOMERUN_CONTROL_TCP_PORT = 65001;

	public final static int HDHOMERUN_MAX_PACKET_SIZE = 1460;
	public final static int HDHOMERUN_MAX_PAYLOAD_SIZE = 1452;

	public final static short HDHOMERUN_TYPE_DISCOVER_REQ = 0x0002;
	public final static short HDHOMERUN_TYPE_DISCOVER_RPY = 0x0003;
	public final static short HDHOMERUN_TYPE_GETSET_REQ = 0x0004;
	public final static short HDHOMERUN_TYPE_GETSET_RPY = 0x0005;
	public final static short HDHOMERUN_TYPE_UPGRADE_REQ = 0x0006;
	public final static short HDHOMERUN_TYPE_UPGRADE_RPY = 0x0007;

	public final static byte HDHOMERUN_TAG_DEVICE_TYPE = 0x01;
	public final static byte HDHOMERUN_TAG_DEVICE_ID = 0x02;
	public final static byte HDHOMERUN_TAG_GETSET_NAME = 0x03;
	public final static byte HDHOMERUN_TAG_GETSET_VALUE = 0x04;
	public final static byte HDHOMERUN_TAG_GETSET_LOCKKEY = 0x15;
	public final static byte HDHOMERUN_TAG_ERROR_MESSAGE = 0x05;
	public final static byte HDHOMERUN_TAG_TUNER_COUNT = 0x10;

	public final static int HDHOMERUN_DEVICE_TYPE_WILDCARD = 0xFFFFFFFF;
	public final static int HDHOMERUN_DEVICE_TYPE_TUNER = 0x00000001;
	public final static int HDHOMERUN_DEVICE_ID_WILDCARD = 0xFFFFFFFF;

	public final static int HDHOMERUN_MIN_PEEK_LENGTH = 4;

	public final static int PKT_BUFFER_SIZE = 3074;
	public int posIndex; // ubyte8* damn java
	public int startIndex; // ubyte8* 
	public int endIndex; // ubyte8* 
	public int limitIndex; // ubyte8* 
	public byte[] buffer = new byte[PKT_BUFFER_SIZE];
	

	public HDHomerun_Pkt()
	{
		reset();
	}

	private static final int BUFFER_START_INDEX = 1024;
	public void reset()
	{
		limitIndex = PKT_BUFFER_SIZE - 4;
		startIndex = BUFFER_START_INDEX;
		endIndex = startIndex;
		posIndex = startIndex;
	}

	public byte read_u8() // uint8_t
	{
		return (byte) HDHomerun_OS.getRealUByteVal(buffer[posIndex++]);
	}

	public short read_u16() // uint16_t 
	{
		short v = 0;
		v |= (short) HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 8;
		v |= (short) HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 0;
		return v;
	}

	public int read_u32()
	{
		int v = 0;
		v |=  HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 24;
		v |=  HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 16;
		v |=  HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 8;
		v |=  HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 0;
		return v;
	}

	public int read_var_length()
	{
		int length;
		
		if (posIndex + 1 > endIndex) {
			return -1;
		}

		length = (int) buffer[posIndex++];
		if ((length & 0x0080) > 0) {
			if (posIndex + 1 > endIndex) {
				return -1;
			}

			length &= 0x007F;
			length |= ((int) buffer[posIndex++]) << 7;
		}
		
		return length; 
	}

	public int read_tlv(byte[] ptag, int[] plength) // uint8_t
	{
		if (posIndex + 2 > endIndex) {
			return 0;
		}
		
		ptag[0] = read_u8();
		plength[0] = read_var_length();

		if (posIndex + plength[0] > endIndex) {
			return 0;
		}
		
		return posIndex + plength[0];
	}

	public void write_u8( byte v) // uint8_t 
	{
		buffer[posIndex++] = v;

		if (posIndex > endIndex) {
			endIndex = posIndex;
		}
	}

	public void write_u16(short v) // uint16_t
	{
		buffer[posIndex++] = (byte)(v >> 8);
		buffer[posIndex++] = (byte)(v >> 0);

		if (posIndex > endIndex) {
			endIndex = posIndex;
		}
	}

	public void write_u32(int v) // uint32_t
	{
		buffer[posIndex++] = (byte)(v >> 24);
		buffer[posIndex++] = (byte)(v >> 16);
		buffer[posIndex++] = (byte)(v >> 8);
		buffer[posIndex++] = (byte)(v >> 0);

		if (posIndex > endIndex) {
			endIndex = posIndex;
		}
	}

	public void write_var_length(short v)  // size_t 
	{
		if (v <= 127) {
			buffer[posIndex++] = (byte)v;
		} else {
			buffer[posIndex++] = (byte)(v | 0x80);
			buffer[posIndex++] = (byte)(v >> 7);
		}

		if (posIndex > endIndex) {
			endIndex = posIndex;
		}
	}

	public void write_mem(final byte[] mem, short length) // const void*, size_t
	{
		for(short cnt = 0; cnt < length; ++cnt)
			buffer[posIndex++] = mem[cnt];
		
		if (posIndex > endIndex) {
			endIndex = posIndex;
		}
	}

	public int open_frame(short[] ptype) // uint16_t *
	{
		posIndex = startIndex;

		if ((posIndex + 4) > endIndex) {
			return 0;
		}

		ptype[0] = read_u16();
		short length = read_u16();
		posIndex += length;

		if ((posIndex + 4) > endIndex) {
			posIndex = startIndex;
			return 0;
		}

		final int calc_crc = calc_crc(buffer, startIndex, posIndex); // uint32_t

		int packet_crc = 0; // uint32_t
		packet_crc |= HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 0;
		packet_crc |= HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 8;
		packet_crc |= HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 16;
		packet_crc |= HDHomerun_OS.getRealUByteVal(buffer[posIndex++]) << 24;
		if (calc_crc != packet_crc) {
			return -1;
		}

		startIndex += 4;
		endIndex = (short) (startIndex + length);
		posIndex = startIndex;
		return 1;
	}

	public void seal_frame(short frame_type) // uint16_t
	{
		short length = (short) (endIndex - startIndex);

		startIndex -= 4;
		posIndex = startIndex;
		write_u16(frame_type);
		write_u16(length);

		int crc = calc_crc(buffer, startIndex, endIndex);
		buffer[endIndex++] = (byte)(crc >> 0);
		buffer[endIndex++] = (byte)(crc >> 8);
		buffer[endIndex++] = (byte)(crc >> 16);
		buffer[endIndex++] = (byte)(crc >> 24);

		posIndex = startIndex;
	}
	
	private int calc_crc(byte[] buffer, int start, int end) // uint32_t 
	{
		int pos = start; // uint8_t *
		int crc = 0xFFFFFFFF; // uint32_t 
		while (pos < end) {
			byte x = (byte) ((byte)(crc) ^ buffer[pos++]);
			crc = crc >>> 8;
			if ((x & 0x01) > 0) crc ^= 0x77073096;
			if ((x & 0x02) > 0) crc ^= 0xEE0E612C;
			if ((x & 0x04) > 0) crc ^= 0x076DC419;
			if ((x & 0x08) > 0) crc ^= 0x0EDB8832;
			if ((x & 0x10) > 0) crc ^= 0x1DB71064;
			if ((x & 0x20) > 0) crc ^= 0x3B6E20C8;
			if ((x & 0x40) > 0) crc ^= 0x76DC4190;
			if ((x & 0x80) > 0) crc ^= 0xEDB88320;
		}
		return crc ^ 0xFFFFFFFF;
	}


}
	