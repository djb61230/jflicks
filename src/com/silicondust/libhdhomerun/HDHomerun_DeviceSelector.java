package com.silicondust.libhdhomerun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class HDHomerun_DeviceSelector {

	private List<HDHomerun_Device> mHD_list;

	private HDHomerun_Debug mDbg;
	
	/*
	 * Create a device selector object for use with dynamic tuner allocation support.
	 * All tuners registered with a specific device selector instance must have the same signal source.
	 * The dbg parameter may be null.
	 */
	public HDHomerun_DeviceSelector(HDHomerun_Debug dbg)
	{
		mHD_list = new ArrayList<HDHomerun_Device>();
		mDbg = dbg;
	}

	public void destroy(boolean destroy_devices)
	{
		if (destroy_devices) {
			Iterator<HDHomerun_Device> iter = mHD_list.iterator();
			while(iter.hasNext()) {
				HDHomerun_Device dev = iter.next();
				dev.destroy();
			}
		}

		mHD_list.clear();

	}


	/*
	 * Get the number of devices in the list.
	 */
	public int get_device_count()
	{
		return mHD_list.size();
	}


	/*
	 * Populate device selector with devices from given source.
	 * Returns the number of devices populated.
	 */
	int load_from_file(final String filename)
	{
		FileInputStream f;
		try {
			f = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			return 0;
		}
		while(true) {

			byte[] device_name = new byte[32];
			try {
				if(0 >= f.read(device_name))
					break;
			} catch (IOException e) {
				break;
			}

			HDHomerun_Device hd = null;
			try {
				hd = new HDHomerun_Device(new String(device_name), mDbg);
			} catch (Exception e) {
				continue;
			}
			if (null == hd) {
				continue;
			}

			add_device(hd);
		}

		try {
			f.close();
		} catch (IOException e) {
		}
		return mHD_list.size();
	}

	/*
	 * Add/remove a device from the selector list.
	 */
	public void add_device(HDHomerun_Device hd)
	{
		Iterator<HDHomerun_Device> iter = mHD_list.iterator();
		while(iter.hasNext()) {
			HDHomerun_Device dev = iter.next();
			if (dev.equals(hd)) {
				return;
			}
		}
	
		mHD_list.add(hd);
	}

	void remove_device(HDHomerun_Device hd)
	{
		mHD_list.remove(hd);
	}


	/*
	 * Find a device in the selector list.
	 */
	HDHomerun_Device find_device(int device_id, int tuner_index)
	{
		Iterator<HDHomerun_Device> iter = mHD_list.iterator();
		while(iter.hasNext()) {
			HDHomerun_Device dev = iter.next();
			if (dev.get_device_id() != device_id) {
				continue;
			}
			if (dev.get_tuner() != tuner_index) {
				continue;
			}
			return dev;
		}

		return null;
	}

	/*
	 * Select and lock an available device.
	 * If not null, preference will be given to the prefered device specified.
	 * The device resource lock must be released by the application when no longer needed by
	 * calling hdhomerun_device_tuner_lockkey_release().
	 *
	 * Recommended channel change logic:
	 *
	 * Start (inactive -> active):
	 * - Call hdhomerun_device_selector_choose_and_lock() to choose and lock an available tuner.
	 * 
	 * Stop (active -> inactive):
	 * - Call hdhomerun_device_tuner_lockkey_release() to release the resource lock and allow the tuner
	 *   to be allocated by other computers.
	 *
	 * Channel change (active -> active):
	 * - If the new channel has a different signal source then call hdhomerun_device_tuner_lockkey_release()
	 *   to release the lock on the tuner playing the previous channel, then call
	 *   hdhomerun_device_selector_choose_and_lock() to choose and lock an available tuner.
	 * - If the new channel has the same signal source then call hdhomerun_device_tuner_lockkey_request()
	 *   to refresh the lock. If this function succeeds then the same device can be used. If this fucntion fails
	 *   then call hdhomerun_device_selector_choose_and_lock() to choose and lock an available tuner.
	 */
	public HDHomerun_Device choose_and_lock(HDHomerun_Device prefered)
	{
		/* Test prefered device first. */
		if (prefered != null) {
			if (choose_test(prefered)) {
				return prefered;
			}
		}

		/* Test other tuners. */
		Iterator<HDHomerun_Device> iter = mHD_list.iterator();
		while(iter.hasNext()) {
			HDHomerun_Device dev = iter.next();
			if (dev.equals(prefered)) {
				continue;
			}

			if (choose_test(dev)) {
				return dev;
			}
		}

		mDbg.printf("hdhomerun_device_selector_choose_and_lock: no devices available\n");
		return null;
	}

	private boolean choose_test(HDHomerun_Device test_hd)
	{
		String name = test_hd.get_name();

		/*
		 * Attempt to aquire lock.
		 */
		StringBuilder error = new StringBuilder();
		int ret = test_hd.tuner_lockkey_request(error);
		if (ret > 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s chosen\n", name));
			return true;
		}
		if (ret < 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s communication error\n", name));
			return false;
		}

		/*
		 * In use - check target.
		 */
		StringBuilder target_b = new StringBuilder();
		ret = test_hd.get_tuner_target(target_b);
		if (ret < 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s communication error\n", name));
			return false;
		}
		if (ret == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, failed to read target\n", name));
			return false;
		}
		String target = new String(target_b);
		Scanner scn = new Scanner(target);
		scn.findInLine("//");
		int[] a = new int[4];
		int target_port = 0;
		a[0] = scn.nextInt();
		if(a[0] == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, no target set (%s)\n", name, target));
			return false;
		}
		scn.next(".");
		a[1] = scn.nextInt();
		if(a[1] == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, no target set (%s)\n", name, target));
			return false;
		}
		scn.next(".");
		a[2] = scn.nextInt();
		if(a[2] == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, no target set (%s)\n", name, target));
			return false;
		}
		scn.next(".");
		a[3] = scn.nextInt();
		if(a[3] == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, no target set (%s)\n", name, target));
			return false;
		}
		scn.next(":");
		target_port = scn.nextInt();
		if (0 == target_port) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, no target set (%s)\n", name, target));
			return false;
		}

		int target_ip = ((a[0] << 24) | (a[1] << 16) | (a[2] << 8) | (a[3] << 0));
		int local_ip = test_hd.get_local_machine_addr();
		if (target_ip != local_ip) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use by %s\n", name, target));
			return false;
		}

		/*
		 * Test local port.
		 */
		boolean inuse = false;
		HDHomerun_Sock test_sock = null;
		try {
			test_sock = new HDHomerun_Sock(0, target_port, 0, 0, false);
		}
		catch (Exception e) {
			inuse = true;
		}
		
		if (test_sock == null) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use, failed to create test sock\n", name));
			return false;
		}
		test_sock.destroy();
		if (inuse) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use by local machine\n", name));
			return false;
		}

		/*
		 * Dead local target, force clear lock.
		 */
		ret = test_hd.tuner_lockkey_force();
		if (ret < 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s communication error\n", name));
			return false;
		}
		if (ret == 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use by local machine, dead target, failed to force release lockkey\n", name));
			return false;
		}

		mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s in use by local machine, dead target, lockkey force successful\n", name));

		/*
		 * Attempt to aquire lock.
		 */
		ret = test_hd.tuner_lockkey_request(error);
		if (ret > 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s chosen\n", name));
			return true;
		}
		if (ret < 0) {
			mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s communication error\n", name));
			return false;
		}

		mDbg.printf(String.format("hdhomerun_device_selector_choose_test: device %s still in use after lockkey force (%s)\n", name, error));
		return false;
	}


}
