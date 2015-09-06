package com.silicondust.libhdhomerun;

import java.util.Random;

public final class HDHomerun_OS {

	public static short getRealUByteVal(byte b) {
		
		short ret = b;
		if(ret < 0)
			ret = (short) ((short) 256 + ret);
		
		return ret;
	}
	public static void msleep_approx(long ms) { // uint64_t
		
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void msleep_minimum(long ms)
	{
		long stop_time = getcurrenttime() + ms;

		while (true) {
			long current_time = getcurrenttime();
			if (current_time >= stop_time) {
				return;
			}

			msleep_approx(stop_time - current_time);
		}
	}
	public static long getcurrenttime() // uint64_t
	{
		return System.currentTimeMillis();
	}
	
	public static int random_get32()
	{
		Random generator = new Random();
		
		return generator.nextInt();
	}
}
