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
package org.jflicks.player.mplayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.util.Util;

/**
 * This job starts a system job that runs mplayer.  It also is a conduit to
 * send mplayer commands over stdin.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MPlayerUdpJob extends MPlayerJob {

    private int port;

    /**
     * Constructor with four required arguments.
     *
     * @param mplayer The player instance creating the job.
     * @param wid A window ID.
     * @param args An array of arguments to give to mplayer.
     * @param port The number of seconds into the video to begin playing.
     */
    public MPlayerUdpJob(MPlayer mplayer, String wid, String[] args, int port) {

        setMPlayer(mplayer);
        setWindowId(wid);
        setArgs(args);
        setPort(port);
    }

    private int getPort() {
        return (port);
    }

    private void setPort(int i) {
        port = i;
    }

    private void write(byte[] data, int length) {

        SystemJob job = getSystemJob();
        if (job != null) {

            try {

                job.write(data, 0, length);

            } catch (IOException ex) {

                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        String userArg = "";
        String[] userArgs = getArgs();
        if ((userArgs != null) && (userArgs.length > 0)) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userArgs.length; i++) {

                sb.append(userArgs[i]);
                sb.append(" ");
            }

            userArg = sb.toString().trim();
        }

        String programName = getProgramName();

        SystemJob job = null;

        String wid = getWindowId();
        if (wid != null) {

            job = SystemJob.getInstance(
                programName + " -wid " + wid + " " + userArg
                + " -input nodefault-bindings:conf=/dev/null -");

        } else {

            File conf = new File("conf");
            File full = new File(conf, "mplayer.conf");
            job = SystemJob.getInstance(
                programName + " -fs -zoom" + " " + userArg
                + " -input nodefault-bindings:conf="
                + full.getAbsolutePath() + " -");
        }

        log(MPlayer.DEBUG, "started: " + job.getCommand());
        job.addJobListener(this);
        setSystemJob(job);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        byte[] buffer = new byte[1024];
        DatagramSocket dsocket = null;
        DatagramPacket packet = null;

        while (!isTerminate()) {

            try {

                if (dsocket == null) {

                    dsocket = new DatagramSocket(getPort());
                    packet = new DatagramPacket(buffer, buffer.length);
                }

                dsocket.receive(packet);
                int size = packet.getLength();
                write(buffer, size);
                packet.setLength(buffer.length);

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }

        System.out.println("Time to close socket: " + dsocket);
        if (dsocket != null) {

            try {

                dsocket.close();
                dsocket = null;

            } catch (Exception ex) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        // Zap the process after we have stopped writing data.
        JobContainer jc = getJobContainer();
        if (jc != null) {

            System.out.println("calling stop on job container");
            jc.stop();
        }

        setTerminate(true);
    }

}

