package com.silicondust.libhdhomerun;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HDHomerun_Debug {
	
	private static final String HDHOMERUN_DEBUG_HOST = "debug.silicondust.com";
	private static final int	HDHOMERUN_DEBUG_PORT = 8002;

	private static final int	HDHOMERUN_DEBUG_CONNECT_RETRY_TIME = 30000;
	private static final int	HDHOMERUN_DEBUG_CONNECT_TIMEOUT = 10000;
	private static final int	HDHOMERUN_DEBUG_SEND_TIMEOUT = 10000;

	class hdhomerun_debug_message_t
	{
		hdhomerun_debug_message_t next;
		hdhomerun_debug_message_t prev;
		String buffer; 
	};

	private class hdhomerun_debug_t
	{
		public hdhomerun_debug_t() {
			
			sock = null;

			print_lock = new Mutex();
			queue_lock = new Mutex();
			send_lock = new Mutex();
			
			enabled = false;
			terminate = false;
			prefix = "";
			file_name = "";
			file_fp = null;
			queue_head = null;
			queue_tail = null;
			queue_depth = 0;

			if(mDbg == null)
				mDbg = this;
			thread = new DebugThread();
			thread.start();
		}
		DebugThread thread;
		volatile boolean enabled;
		volatile boolean terminate;
		String prefix;

		Mutex print_lock;
		Mutex queue_lock;
		Mutex send_lock;

		hdhomerun_debug_message_t queue_head;
		hdhomerun_debug_message_t queue_tail;
		int queue_depth;

		long connect_delay;

		String file_name;
		FileOutputStream file_fp;
		HDHomerun_Sock sock;
	};
	
	static hdhomerun_debug_t mDbg = null;
	static Integer base = new Integer(0);

	public HDHomerun_Debug()
	{
		synchronized(base) {
			if(mDbg == null)
				mDbg = new hdhomerun_debug_t();
		}
			
	}

	synchronized public void destroy()
	{
		if (mDbg == null) {
			return;
		}

		mDbg.terminate = true;
		try {
			mDbg.thread.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (mDbg.prefix != null)
			mDbg.prefix = null;
		
		if (mDbg.file_name != null)
			mDbg.file_name = null;
		
		if (mDbg.file_fp != null) {
			try {
				mDbg.file_fp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mDbg.file_fp = null;
		}
		if (mDbg.sock != null)
			mDbg.sock.destroy();

		mDbg = null;
	}

	/* Send lock held by caller */
	private void close_internal()
	{
		if (mDbg.file_fp != null) {
			try {
				mDbg.file_fp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mDbg.file_fp = null;
		}

		if (mDbg.sock != null) {
			mDbg.sock.destroy();
			mDbg.sock = null;
		}
	}

	public void close(int timeout)
	{
		if (mDbg == null) {
			return;
		}

		if (timeout > 0) {
			flush(timeout);
		}

		mDbg.send_lock.lock();
		close_internal();
		mDbg.connect_delay = 0;
		mDbg.send_lock.unlock();
	}

	public void set_filename(final String filename)
	{
		if (mDbg == null) {
			return;
		}

		mDbg.send_lock.lock();

		if (null == filename && null == mDbg.file_name) {
			mDbg.send_lock.unlock();
			return;
		}
		if (null != filename && null != mDbg.file_name) {
			if (filename.compareTo(mDbg.file_name) == 0) {
				mDbg.send_lock.unlock();
				return;
			}
		}

		close_internal();
		mDbg.connect_delay = 0;

		if (mDbg.file_name != null)
			mDbg.file_name = null;
		
		if (null != filename) 
			mDbg.file_name = new String(filename);
		

		mDbg.send_lock.unlock();
	}

	public void set_prefix(final String prefix)
	{
		if (null == mDbg) {
			return;
		}

		mDbg.print_lock.lock();

		mDbg.prefix = null;
		
		if (null !=prefix) {
			mDbg.prefix = new String(prefix);
		}

		mDbg.print_lock.unlock();
	}

	public void enable()
	{
		if (mDbg == null) {
			return;
		}

		mDbg.enabled = true;
	}

	public void disable()
	{
		if (mDbg == null) {
			return;
		}

		mDbg.enabled = false;
	}

	public boolean enabled()
	{
		if (mDbg == null) {
			return false;
		}

		return mDbg.enabled;
	}

	public void flush(long timeout)
	{
		if (null == mDbg) {
			return;
		}

		timeout = HDHomerun_OS.getcurrenttime() + timeout;

		while (HDHomerun_OS.getcurrenttime() < timeout) {
			mDbg.queue_lock.lock();
			hdhomerun_debug_message_t message = mDbg.queue_tail;
			mDbg.queue_lock.unlock();

			if (null == message) {
				return;
			}

			HDHomerun_OS.msleep_approx(10);
		}
	}


	public void printf(final String msg)
	{
		if (mDbg == null) {
			return;
		}
		if (!mDbg.enabled) {
			return;
		}

		hdhomerun_debug_message_t message = new hdhomerun_debug_message_t();
		if (null == message) {
			return;
		}

		/*
		 * Timestamp.
		 */
		Date now = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
	    String strBuff = sdf.format(now) + " ";
	

		/*
		 * Debug prefix.
		 */
		mDbg.print_lock.lock();

		if (null != mDbg.prefix) {
			strBuff += mDbg.prefix + " ";
		}

		mDbg.print_lock.unlock();

		/*
		 * Message text.
		 */
		strBuff += msg;

		/*
		 * Force newline.
		 */
		strBuff += "\n";


		/*
		 * Enqueue.
		 */
		mDbg.queue_lock.lock();

		message.prev = null;
		message.next = mDbg.queue_head;
		mDbg.queue_head = message;
		if (message.next != null ) {
			message.next.prev = message;
		} else {
			mDbg.queue_tail = message;
		}
		mDbg.queue_depth++;

		mDbg.queue_lock.unlock();
	}

	/* Send lock held by caller */
	private boolean output_message_file(hdhomerun_debug_message_t message)
	{
		if (null == mDbg.file_fp) {
			long current_time = HDHomerun_OS.getcurrenttime();
			if (current_time < mDbg.connect_delay) {
				return false;
			}
			mDbg.connect_delay = current_time + 30*1000;

			try {
				mDbg.file_fp = new FileOutputStream(mDbg.file_name, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (null == mDbg.file_fp) {
				return false;
			}
		}

		try {
			mDbg.file_fp.write(message.buffer.getBytes("UTF8"));
			mDbg.file_fp.write(0);
			mDbg.file_fp.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/* Send lock held by caller */
	private boolean output_message_sock(hdhomerun_debug_message_t message)
	{
		if (mDbg.sock == null) {
			long current_time = HDHomerun_OS.getcurrenttime();
			if (current_time < mDbg.connect_delay) {
				return false;
			}
			mDbg.connect_delay = (int) (current_time + HDHOMERUN_DEBUG_CONNECT_RETRY_TIME);

			int remote_addr = HDHomerun_Sock.getaddrinfo_addr(HDHOMERUN_DEBUG_HOST);
			if(remote_addr == 0) {
				close_internal();
				return false;
			}
			try {
				mDbg.sock = new HDHomerun_Sock(remote_addr, HDHOMERUN_DEBUG_PORT, 0, 0, HDHOMERUN_DEBUG_CONNECT_TIMEOUT);
			} catch(Exception e) {
				close_internal();
				return false;
			}

			if(!mDbg.sock.isValid()){
				close_internal();
				return false;
			}
		}

		try {
			if (!mDbg.sock.send(message.buffer.getBytes("UTF8"), 0, message.buffer.length(), HDHOMERUN_DEBUG_SEND_TIMEOUT)) {
				close_internal();
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return false;
		}

		return true;
	}

	private boolean output_message(hdhomerun_debug_message_t message)
	{
		mDbg.send_lock.lock();

		boolean ret;
		if (null != mDbg.file_name) {
			ret = output_message_file(message);
		} else {
			ret = output_message_sock(message);
		}

		mDbg.send_lock.unlock();
		return ret;
	}

	private void pop_and_free_message()
	{
		mDbg.queue_lock.lock();

		hdhomerun_debug_message_t message = mDbg.queue_tail;
		mDbg.queue_tail = message.prev;
		if (message.prev != null) {
			message.prev.next = null;
		} else {
			mDbg.queue_head = null;
		}
		mDbg.queue_depth--;

		mDbg.queue_lock.unlock();

		message = null;
	}

	private class DebugThread extends Thread
	{
		public void run() {
			
			while (!mDbg.terminate) {
	
				mDbg.queue_lock.lock();
				hdhomerun_debug_message_t message = mDbg.queue_tail;
				int queue_depth = mDbg.queue_depth;
				mDbg.queue_lock.unlock();
	
				if (null == message) {
					HDHomerun_OS.msleep_approx(250);
					continue;
				}
	
				if (queue_depth > 1024) {
					pop_and_free_message();
					continue;
				}
	
				if (!output_message(message)) {
					HDHomerun_OS.msleep_approx(250);
					continue;
				}
	
				pop_and_free_message();
			}
		}
	}

}
