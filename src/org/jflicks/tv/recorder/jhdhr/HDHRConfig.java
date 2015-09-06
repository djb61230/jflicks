/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.tv.recorder.jhdhr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jflicks.util.Util;

import com.silicondust.libhdhomerun.HDHomerun_Channels;
import com.silicondust.libhdhomerun.HDHomerun_Device;
import com.silicondust.libhdhomerun.HDHomerun_Discover;
import com.silicondust.libhdhomerun.HDHomerun_OS;
import com.silicondust.libhdhomerun.HDHomerun_Pkt;
import com.silicondust.libhdhomerun.HDHomerun_Discover.hdhomerun_discover_device_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_program_t;
import com.silicondust.libhdhomerun.HDHomerun_Types.hdhomerun_channelscan_result_t;
import com.silicondust.libhdhomerun.HDHomerun_Video;
import com.silicondust.libhdhomerun.HDHomerun_Video.hdhomerun_video_stats_t;

/**
 * A class to interact with your HDHR devices on the network.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRConfig {

    private boolean scanTerminated;
    private boolean saveTerminated;

    /**
     * Simple no argument constructor.
     */
    public HDHRConfig() {
    }

    public HDHRDevice[] getHDHRDevices() {

        HDHRDevice[] result = null;

        hdhomerun_discover_device_t[] result_list = new hdhomerun_discover_device_t[64];
        int count = HDHomerun_Discover.hdhomerun_discover_find_devices_custom(0,
            HDHomerun_Pkt.HDHOMERUN_DEVICE_TYPE_TUNER, HDHomerun_Pkt.HDHOMERUN_DEVICE_ID_WILDCARD, result_list, 64);
        if (count > 0) {

            ArrayList<HDHRDevice> l = new ArrayList<HDHRDevice>();
            StringBuilder value = new StringBuilder();
            StringBuilder error = new StringBuilder();

            for (int i = 0; i < count; i++) {

                hdhomerun_discover_device_t dev = result_list[i];
                String model = null;
                String id = String.format("%08X", dev.device_id);
                String ip = String.format("%d.%d.%d.%d", (dev.ip_addr >> 24) & 0x0FF,
                    (dev.ip_addr >> 16) & 0x0FF, (dev.ip_addr >> 8) & 0x0FF, (dev.ip_addr >> 0) & 0x0FF);

                try {

                    HDHomerun_Device hd = new HDHomerun_Device(id, null);
                    model = hd.get_model_str();
                    hd.destroy();

                } catch (Exception ex) {

                    ex.printStackTrace();
                }

                HDHRDevice hdhr = new HDHRDevice(id, 0, ip, model);
                l.add(hdhr);
                hdhr = new HDHRDevice(id, 1, ip, model);
                l.add(hdhr);
            }

            if (l.size() > 0) {

                result = l.toArray(new HDHRDevice[l.size()]);
            }
        }

        return (result);
    }

    public void applyFrequency(HDHRRecorder r, String frequency) {

        if (r != null) {

            String device = r.getDevice();
            int index = device.indexOf("-");
            String id = device.substring(0, index);
            String tunerStr = device.substring(index + 1);
            int tuner = Util.str2int(tunerStr, 0);
            String model = r.getModel();
            applyFrequency(id, tuner, model, r.getConfiguredFrequencyType(), frequency);
        }
    }

    public void applyFrequency(String id, int tuner, String model, String frequencyType, String frequency) {

        if ((id != null) && (frequency != null)) {

            String item = "/tuner" + tuner + "/channel";
            String value = frequency;
            /*
            if (model != null) {

                if (model.equalsIgnoreCase("hdhomeruntc_atsc")) {

                    value = frequencyType + ":" + value;
                }
            }
            */

            commandSet(id, item, value);
        }
    }

    public String streaminfo(HDHRRecorder r) {

        String result = null;

        if (r != null) {

            String device = r.getDevice();
            int index = device.indexOf("-");
            String id = device.substring(0, index);
            String tuner = device.substring(index + 1);
            String item = "/tuner" + tuner + "/streaminfo";
            String text = commandGet(id, item);
        }

        return (result);
    }

    public String streaminfo(String id, int tuner) {

        String result = null;

        if (id != null) {

            String item = "/tuner" + tuner + "/streaminfo";
            System.out.println("stream info <" + item + ">");
            result = commandGet(id, item);
        }

        return (result);
    }

    public void program(HDHRRecorder r, String program) {

        if (r != null) {

            String device = r.getDevice();
            int index = device.indexOf("-");
            String id = device.substring(0, index);
            String tunerStr = device.substring(index + 1);
            int tuner = Util.str2int(tunerStr, 0);
            program(id, tuner, program);
        }
    }

    public void program(String id, int tuner, String program) {

        if ((id != null) && (program != null)) {

            String item = "/tuner" + tuner + "/program";
            commandSet(id, item, program);
        }

    }

    public void channelMap(HDHRRecorder r) {

        if (r != null) {

            String device = r.getDevice();
            int index = device.indexOf("-");
            String id = device.substring(0, index);
            String tunerStr = device.substring(index + 1);
            int tuner = Util.str2int(tunerStr, 0);
            channelMap(id, tuner, r.getConfiguredFrequencyType());
        }
    }

    public void channelMap(String id, int tuner, String frequencyType) {

        if ((id != null) && (frequencyType != null)) {

            String item = "/tuner" + tuner + "/channelmap";
            commandSet(id, item, frequencyType);
        }
    }

    public void scan(String id, int tuner, String filename) {

        setScanTerminated(false);
        if (id != null) {

            try {

                HDHomerun_Device hd = new HDHomerun_Device(id, null);
                if (hd.set_tuner_from_str("" + tuner) > 0) {

                    StringBuilder error = new StringBuilder();
                    if (hd.tuner_lockkey_request(error) > 0) {

                        hd.set_tuner_target("none");
                        StringBuilder channelmap = new StringBuilder();
                        if (hd.get_tuner_channelmap(channelmap) > 0) {

                            String channelmap_scan_group =
                                HDHomerun_Channels.hdhomerun_channelmap_get_channelmap_scan_group(
                                    channelmap.toString());
                            if ((channelmap_scan_group != null) && (channelmap_scan_group.length() > 0)) {

                                if (hd.channelscan_init(channelmap_scan_group) > 0) {

                                    FileOutputStream fp = null;
                                    if (filename != null && filename.length() > 0) {
                                        try {
                                            fp = new FileOutputStream(filename);
                                        }
                                        catch (IOException e) {
                                            System.out.println(String.format("unable to create file: %s\n", filename));
                                            return;
                                        }
                                    }

                                    //register_signal_handlers();

                                    int ret = 0;
                                    while (!isScanTerminated()) { //!sigabort_flag) {
                                        hdhomerun_channelscan_result_t result = new hdhomerun_channelscan_result_t();
                                        ret = hd.channelscan_advance(result);
                                        if (ret <= 0) {
                                            break;
                                        }

                                        writeToFileAndScreen(fp, String.format("SCANNING: %d (%s)\n", result.frequency, result.channel_str
                                        ));

                                        ret = hd.channelscan_detect(result);
                                        if (ret < 0) {
                                            break;
                                        }
                                        if (ret == 0) {
                                            continue;
                                        }

                                        writeToFileAndScreen(fp, String.format("LOCK: %s (ss=%d snq=%d seq=%d)\n",
                                                result.status.lock_str, result.status.signal_strength,
                                                result.status.signal_to_noise_quality, result.status.symbol_error_quality
                                            ));

                                        if (result.transport_stream_id_detected) {
                                            writeToFileAndScreen(fp, String.format("TSID: 0x%04X\n", result.transport_stream_id));
                                        }
                                        int i;
                                        for (i = 0; i < result.program_count; i++) {
                                            hdhomerun_channelscan_program_t program = result.programs[i];
                                            writeToFileAndScreen(fp, String.format("PROGRAM %s\n", program.program_str));
                                        }
                                    }

                                    hd.tuner_lockkey_release();

                                    if (fp != null) {
                                        try {
                                            fp.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                hd.destroy();

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }

    public int save(String id, int tuner, String filename) {

        int result = 0;

        setSaveTerminated(false);
        if ((id != null) && (filename != null)) {

            try {

                //HDHomerun_Debug dbg = new HDHomerun_Debug();
                HDHomerun_Device hd = new HDHomerun_Device(id, null);

                if (hd.set_tuner_from_str("" + tuner) <= 0) {
                    System.out.println("invalid tuner number\n");
                    return -1;
                }

                boolean printToConsole = false;
                FileOutputStream fp = null;
                if (filename.compareTo("null") == 0) {
                    fp = null;
                } else if (filename.compareTo("-") == 0) {
                    printToConsole = true;
                } else {
                    try {
                        fp = new FileOutputStream(filename);
                    } catch (FileNotFoundException e) {
                        System.out.println(String.format("unable to create file %s\n", filename));
                        return -1;
                    }
                }

                int ret = hd.stream_start();
                if (ret <= 0) {
                    System.out.println("unable to start stream\n");
                    if (fp != null) {
                        try {
                            fp.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return ret;
                }

                //register_signal_handlers(sigabort_handler, sigabort_handler, siginfo_handler);

                hdhomerun_video_stats_t stats_old = new hdhomerun_video_stats_t();
                hdhomerun_video_stats_t stats_cur = new hdhomerun_video_stats_t();
                hd.get_video_stats(stats_old);

                long next_progress = HDHomerun_OS.getcurrenttime() + 1000;
                while (isSaveTerminated()) { //!sigabort_flag) {
                    long loop_start_time = HDHomerun_OS.getcurrenttime();

                    /*if (siginfo_flag) {
                        System.out.println("\n");
                        cmd_save_print_stats();
                        siginfo_flag = false;
                    }*/

                    int[] actual_size = new int[1];
                    byte[] ptr = hd.stream_recv(HDHomerun_Video.VIDEO_DATA_BUFFER_SIZE_1S, actual_size);
                    if (null == ptr) {
                        HDHomerun_OS.msleep_approx(64);
                        continue;
                    }

                    if (fp != null) {
                        try {
                            fp.write(ptr, 0, actual_size[0]);
                        }
                        catch(IOException e)
                        {
                            System.out.println("error writing output\n");
                            return -1;
                        }
                    }
                    else if(printToConsole)
                        System.out.println(String.format("Bytes Received : %d", actual_size[0]));

                    if (loop_start_time >= next_progress) {
                        next_progress += 1000;
                        if (loop_start_time >= next_progress) {
                            next_progress = loop_start_time + 1000;
                        }

                        /* Video stats. */
                        hd.get_video_stats(stats_cur);

                        if (stats_cur.overflow_error_count > stats_old.overflow_error_count) {
                            System.out.println("o");
                        } else if (stats_cur.network_error_count > stats_old.network_error_count) {
                            System.out.println("n");
                        } else if (stats_cur.transport_error_count > stats_old.transport_error_count) {
                            System.out.println("t");
                        } else if (stats_cur.sequence_error_count > stats_old.sequence_error_count) {
                            System.out.println("s");
                        } else {
                            System.out.println(".");
                        }

                        stats_old = stats_cur;
                    }
                    long delay = 64 - (HDHomerun_OS.getcurrenttime() - loop_start_time);
                    if (delay <= 0) {
                        continue;
                    }

                    HDHomerun_OS.msleep_approx(delay);
                }

                if (fp != null) {
                    try {
                        fp.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                hd.stream_stop();

                hd.destroy();

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
        
        return result;
    }

    public boolean isSaveTerminated() {
        return (saveTerminated);
    }

    public void setSaveTerminated(boolean b) {
        saveTerminated = b;
    }

    public boolean isScanTerminated() {
        return (scanTerminated);
    }

    public void setScanTerminated(boolean b) {
        scanTerminated = b;
    }

    private void writeToFileAndScreen(FileOutputStream fp, String str) {

        System.out.print(str);
        if(fp != null) {
            try {
                fp.write(str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String commandGet(String id, String item) {

        String result = null;

        if ((id != null) &&(item != null)) {

            try {

                HDHomerun_Device hd = new HDHomerun_Device(id, null);

                StringBuilder value = new StringBuilder();
                StringBuilder error = new StringBuilder();

                if (hd.get_var(item, value, error) < 0) {
                    System.out.println("communication error sending request to hdhomerun device\n");
                }

                if (error.length() > 0) {
                    System.out.println(error);
                }

                hd.destroy();
                result = value.toString();
                System.out.println(result);

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }

        return (result);
    }

    private void commandSet(String id, String item, String value) {

        if ((id != null) &&(item != null) && (value != null)) {

            try {

                System.out.println("id <" + id + ">");
                System.out.println("item <" + item + ">");
                System.out.println("value <" + value + ">");
                HDHomerun_Device hd = new HDHomerun_Device(id, null);

                StringBuilder error = new StringBuilder();
                if (hd.set_var(item, value, null, error) < 0) {

                    System.out.println("communication error sending request to hdhomerun device");
                }

                if (error.length() > 0) {
                    System.out.println(error);
                }
                hd.destroy();

            } catch (Exception ex) {

                ex.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {

        HDHRConfig config = new HDHRConfig();

        HDHRDevice[] array = config.getHDHRDevices();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                System.out.println(array[i].getId() + " " + array[i].getIp() + " " + array[i].getModel());
                if (i == 0) {

                    config.scan(array[i].getId(), array[i].getTuner(), null);
/*
                    config.applyFrequency(array[i].getId(), array[i].getTuner(), array[i].getModel(), "us-bcast", "19");
                    Thread.sleep(1000);
                    String output = config.streaminfo(array[i].getId(), array[i].getTuner());
                    System.out.println("fred <" + output + ">");
                    StreamInfo sinfo = new StreamInfo();
                    sinfo.setProgram("68.1");
                    System.out.println(sinfo.getProgramId(array[i].getId(), array[i].getTuner()));
*/
                }
            }
        }
    }

}
