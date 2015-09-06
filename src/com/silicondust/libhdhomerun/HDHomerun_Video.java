package com.silicondust.libhdhomerun;

public final class HDHomerun_Video {

	public static class hdhomerun_video_stats_t {
		public long packet_count;
		public long network_error_count;
		public long transport_error_count;
		public long sequence_error_count;
		public long overflow_error_count;
		
		public hdhomerun_video_stats_t() {
			reset();
		}
		public void reset() {
			
			packet_count = 0;
			network_error_count = 0;
			transport_error_count = 0;
			sequence_error_count = 0;
			overflow_error_count = 0;
		}
	};

	private static final int TS_PACKET_SIZE = 188;
	private static final int VIDEO_DATA_PACKET_SIZE = (188 * 7);
	public static final int VIDEO_DATA_BUFFER_SIZE_1S = (20000000 / 8);

	private static final int VIDEO_RTP_DATA_PACKET_SIZE = ((188 * 7) + 12);

	
	private Mutex mLock = null;
	private HDHomerun_Debug dbg = null;

		HDHomerun_Sock mSock = null;
		private int mMulticast_ip = 0;

		private volatile int mHead = 0;
		private volatile int mTail = 0;
		private byte[] mBuffer = null;
		private int mBuffer_size = 0;
		private int mAdvance = 0;

		private VideoThread mThread = null;
		private volatile boolean mTerminate = false;

		private volatile long mPacket_count = 0;
		private volatile long mTransport_error_count = 0;
		private volatile long mNetwork_error_count = 0;
		private volatile long mSequence_error_count = 0;
		private volatile long mOverflow_error_count = 0;

		private volatile long mRTP_Sequence = 0;
		private volatile byte[] mSequence = new byte[0x2000];
	


	public  HDHomerun_Video(int listen_port, int buffer_size, boolean allowReuse, HDHomerun_Debug dbg) throws Exception
	{

		this.dbg = dbg;
		mSock = null;
		mLock = new Mutex();

		/* Reset sequence tracking. */
		flush();

		/* Buffer size. */
		mBuffer_size = (buffer_size / VIDEO_DATA_PACKET_SIZE) * VIDEO_DATA_PACKET_SIZE;
		if (mBuffer_size == 0) {
			dbg.printf(String.format("hdhomerun_video_create: invalid buffer size (%d bytes)\n", mBuffer_size));
			ConstructorError();
			return;
		}
		mBuffer_size += VIDEO_DATA_PACKET_SIZE;

		/* Create buffer. */
		mBuffer = new byte[mBuffer_size];
		if (null == mBuffer) {
			dbg.printf(String.format("hdhomerun_video_create: failed to allocate buffer (%d bytes)\n", mBuffer_size));
			ConstructorError();
			return;
		}
		
		/* Create socket. */
		/* Expand socket buffer size. */
		
		int rx_size = 1024 * 1024;
		try {
			mSock = new HDHomerun_Sock(0, listen_port, 0, rx_size, allowReuse);
			if (mSock == null) {
				dbg.printf("hdhomerun_video_create: failed to allocate socket\n");
				ConstructorError();
				return;
			}
		}
		catch (Exception e) {
			dbg.printf(String.format("hdhomerun_video_create: failed to bind socket (port %d)\n", listen_port));
			ConstructorError();
			return;
		}


		/* Start thread. */
		mThread = new VideoThread();
		if (mThread == null) {
			dbg.printf("hdhomerun_video_create: failed to start thread\n");
			ConstructorError();
			return;
		}
		mThread.start();

		/* Success. */
		
	}
	
	private void ConstructorError() throws Exception {
	
		if (mSock != null) {
			mSock.destroy();
		}
		if (null != mBuffer) {
			mBuffer = null;
		}
		
		throw new Exception();
	}

	public void destroy()
	{
		mTerminate = true;
		try {
			mThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mSock.destroy();
		mBuffer = null;
	}

	public HDHomerun_Sock get_sock()
	{
		return mSock;
	}

	int get_local_port()
	{
		int port = mSock.getsockname_port();
		if (port == 0) {
			dbg.printf(String.format("hdhomerun_video_get_local_port: getsockname failed (%s)\n", mSock.getlasterror()));
			return 0;
		}

		return port;
	}

	public int join_multicast_group(int multicast_ip)
	{
		if (multicast_ip != 0) {
			leave_multicast_group();
		}

		if (!mSock.addGroup(mMulticast_ip)) {
			dbg.printf(String.format("hdhomerun_video_join_multicast_group: setsockopt failed (%s)\n", mSock.getlasterror()));
			return -1;
		}

		mMulticast_ip = multicast_ip;
		return 1;
	}

	public int leave_multicast_group()
	{
		if (mMulticast_ip == 0) {
			return 1;
		}

		if (!mSock.dropGroup(mMulticast_ip)) {
			dbg.printf(String.format("hdhomerun_video_leave_multicast_group: setsockopt failed (%s)\n", mSock.getlasterror()));
		}

		mMulticast_ip = 0;
		return 1;
	}

	private void stats_ts_pkt(byte[] ptr, int start)
	{
		short packet_identifier = (short) (((short)(ptr[start + 1] & 0x1F) << 8) | (short)ptr[start + 2]);
		if (packet_identifier == 0x1FFF) {
			return;
		}

		boolean transport_error = (ptr[start + 1] >> 7) != 0;
		if (transport_error) {
			mTransport_error_count++;
			mSequence[packet_identifier] = (byte) 0xFF;
			return;
		}

		byte btyeSequence = (byte) (ptr[start + 3] & 0x0F);

		byte previous_sequence = mSequence[packet_identifier];
		mSequence[packet_identifier] = btyeSequence;

		if (previous_sequence == 0xFF) {
			return;
		}
		if (btyeSequence == ((previous_sequence + 1) & 0x0F)) {
			return;
		}
		if (btyeSequence == previous_sequence) {
			return;
		}

		mSequence_error_count++;
	}

	private void parse_rtp(HDHomerun_Pkt pkt)
	{
		pkt.posIndex += 2;
		long iRTP_sequence = pkt.read_u16();
		pkt.posIndex += 8;

		long previous_rtp_sequence = mRTP_Sequence;
		mRTP_Sequence = iRTP_sequence;

		/* Initial case - first packet received. */
		if (previous_rtp_sequence == 0xFFFFFFFF) {
			return;
		}

		/* Normal case - next sequence number. */
		if (iRTP_sequence == ((previous_rtp_sequence + 1) & 0xFFFF)) {
			return;
		}

		/* Error case - sequence missed. */
		mNetwork_error_count++;

		/* Restart pid sequence check after packet loss. */
		for (int i = 0; i < 0x2000; i++) {
			mSequence[i] = (byte) 0xFF;
		}
	}

	private class VideoThread extends Thread
	{
		public void run()
		{
			HDHomerun_Pkt pkt = new HDHomerun_Pkt();
	
			while (!mTerminate) {

				pkt.reset();
				
				/* Receive. */
				int[] length = new int[1];
				length[0] = VIDEO_RTP_DATA_PACKET_SIZE;
				if (!mSock.recv(pkt.buffer, pkt.endIndex, length, 25)) {
					continue;
				}
	
				pkt.endIndex += length[0];
	
				if (length[0] == VIDEO_RTP_DATA_PACKET_SIZE) {
					parse_rtp(pkt);
					length[0] = pkt.endIndex - pkt.posIndex;
				}
	
				if (length[0] != VIDEO_DATA_PACKET_SIZE) {
					/* Data received but not valid - ignore. */
					continue;
				}
	
				mLock.lock();
	
				/* Store in ring buffer. */
				int head = mHead;
				for(int i = 0; i < length[0]; ++i)
					mBuffer[head + i] = pkt.buffer[pkt.posIndex + i];

	
				/* Stats. */
				mPacket_count++;
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 0));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 1));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 2));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 3));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 4));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 5));
				stats_ts_pkt(mBuffer, head + (TS_PACKET_SIZE * 6));
	
				/* Calculate new head. */
				head += length[0];
				if (head >= mBuffer_size) {
					head -= mBuffer_size;
				}
	
				/* Check for buffer overflow. */
				if (head == mTail) {
					mOverflow_error_count++;
					mLock.unlock();
					continue;
				}
	
				mHead = head;
	
				mLock.unlock();
			}
	
		}
	}
	
	byte[] recv(int max_size, int[] pactual_size)
	{
		mLock.lock();

		int head = mHead;
		int tail = mTail;

		if (mAdvance > 0) {
			tail += mAdvance;
			if (tail >= mBuffer_size) {
				tail -= mBuffer_size;
			}
		
			mTail = tail;
		}

		if (head == tail) {
			mAdvance = 0;
			pactual_size[0]= 0;
			mLock.unlock();
			return null;
		}

		int size = (max_size / VIDEO_DATA_PACKET_SIZE) * VIDEO_DATA_PACKET_SIZE;
		if (size == 0) {
			mAdvance = 0;
			pactual_size[0] = 0;
			mLock.unlock();
			return null;
		}

		int avail;
		if (head > tail) {
			avail = head - tail;
		} else {
			avail = mBuffer_size - tail;
		}
		if (size > avail) {
			size = avail;
		}
		mAdvance = size;
		pactual_size[0] = size;
		int result_len = mBuffer_size - mTail;
		byte[] result = new byte[result_len];
		for(int i = 0; i < result_len; ++i)
			result[i] = mBuffer[mTail + i];

		mLock.unlock();
		return result;
	}

	public void flush()
	{
		mLock.lock();

		mTail = mHead;
		mAdvance = 0;

		mRTP_Sequence = 0xFFFFFFFF;

		for (int i = 0; i < 0x2000; i++) {
			mSequence[i] = (byte) 0xFF;
		}

		mPacket_count = 0;
		mTransport_error_count = 0;
		mNetwork_error_count = 0;
		mSequence_error_count = 0;
		mOverflow_error_count = 0;

		mLock.unlock();
	}

	public void debug_print_stats()
	{
		hdhomerun_video_stats_t stats = new hdhomerun_video_stats_t();
		get_stats(stats);

		dbg.printf(String.format("video sock: pkt=%d net=%d te=%d miss=%d drop=%d\n",
			stats.packet_count, stats.network_error_count,
			stats.transport_error_count, stats.sequence_error_count,
			stats.overflow_error_count)
		);
	}

	public void get_stats(hdhomerun_video_stats_t stats)
	{
		stats.reset();

		mLock.lock();

		stats.packet_count = mPacket_count;
		stats.network_error_count = mNetwork_error_count;
		stats.transport_error_count = mTransport_error_count;
		stats.sequence_error_count = mSequence_error_count;
		stats.overflow_error_count = mOverflow_error_count;

		mLock.unlock();
	}

}
