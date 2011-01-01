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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.Player;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activater that starts the mplayer job.  Also registers the Player
 * based upon mplayer.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ActivatorVideoTransportStream extends BaseActivator {

    private MPlayer mplayer;
    private ServiceTracker eventServiceTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        // First make a fifo so mplayer can receive commands...as of now
        // this is dependent on Linux.  Windows has named pipes but I don't
        // know how to create one...
        SystemJob job = SystemJob.getInstance("mkfifo mplayer.fifo");
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();

        mplayer = new MPlayer();
        mplayer.setType(MPlayer.PLAYER_VIDEO_TRANSPORT_STREAM);

        // Check for a properties file for mplayer....
        File conf = new File("conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File props = new File(conf, "mplayer.properties");
            if ((props.exists()) && (props.isFile())) {

                Properties p = Util.findProperties(props);
                if (p != null) {

                    mplayer.setForceFullscreen(Util.str2boolean(
                        p.getProperty("forceFullscreen"), false));

                    ArrayList<String> l = new ArrayList<String>();
                    int count = Util.str2int(p.getProperty("argCount"), 0);
                    for (int i = 0; i < count; i++) {

                        String tmp = p.getProperty("arg" + i);
                        if (tmp != null) {

                            l.add(tmp.trim());
                        }
                    }

                    if (l.size() > 0) {

                        mplayer.setArgs(l.toArray(new String[l.size()]));
                    }
                }
            }
        }

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Player.TITLE_PROPERTY, mplayer.getTitle());
        dict.put(Player.HANDLE_PROPERTY, mplayer.getType());

        bc.registerService(Player.class.getName(), mplayer, dict);

        eventServiceTracker =
            new ServiceTracker(bc, EventAdmin.class.getName(), null);
        mplayer.setEventServiceTracker(eventServiceTracker);
        eventServiceTracker.open();

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        mplayer.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SystemJob job = SystemJob.getInstance("rm mplayer.fifo");
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();

        if (eventServiceTracker != null) {

            eventServiceTracker.close();
            eventServiceTracker = null;
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

}
