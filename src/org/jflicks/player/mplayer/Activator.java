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

import java.util.Hashtable;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.Player;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activater that starts the mplayer job.  Also registers the Player
 * based upon mplayer.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private MPlayer mplayer;

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

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Player.TITLE_PROPERTY, mplayer.getTitle());
        dict.put(Player.HANDLE_PROPERTY, mplayer.getType());

        bc.registerService(Player.class.getName(), mplayer, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SystemJob job = SystemJob.getInstance("rm mplayer.fifo");
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}
