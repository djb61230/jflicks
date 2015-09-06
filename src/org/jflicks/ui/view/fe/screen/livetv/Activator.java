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
package org.jflicks.ui.view.fe.screen.livetv;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.player.Player;
import org.jflicks.rc.RCTracker;
import org.jflicks.imagecache.ImageCacheTracker;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Filter;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Activator for the Live TV screen.  It checks for a properties file to
 * determine whether to start a DVR-like LiveTV or a pure streaming one.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ServiceTracker serviceTracker;
    private RCTracker rcTracker;
    private TransferTracker transferTracker;
    private ImageCacheTracker imageCacheTracker;
    private int initialTime;
    private int restTime;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        String playerfilter = null;
        Screen s = null;
        if (isDVR()) {

            playerfilter = "(|(Player-Handle="
                + Player.PLAYER_VIDEO_TRANSPORT_STREAM + ")"
                + "(Player-Handle=" + Player.PLAYER_VIDEO + "))";
            DVRLiveTVScreen dvr = new DVRLiveTVScreen();
            dvr.setInitialTime(getInitialTime());
            dvr.setRestTime(getRestTime());
            s = dvr;
            TransferTracker tt = new TransferTracker(bc, (DVRLiveTVScreen) s);
            setTransferTracker(tt);
            tt.open();

        } else {

            playerfilter =
                "(Player-Handle=" + Player.PLAYER_VIDEO_STREAM_UDP + ")";
            s = new LiveTVScreen();
        }

        // Now we listen for command events.
        String[] topics = new String[] {
            "org/jflicks/rc/COMMAND"
        };

        Hashtable<String, String[]> h = new Hashtable<String, String[]>();
        h.put(EventConstants.EVENT_TOPIC, topics);
        bc.registerService(EventHandler.class.getName(), s, h);

        RCTracker rct = new RCTracker(bc, s);
        setRCTracker(rct);
        rct.open();

        ImageCacheTracker ict = new ImageCacheTracker(bc, s);
        setImageCacheTracker(ict);
        ict.open();

        try {

            Filter filter = bc.createFilter(playerfilter);
            ServiceTracker st = new ServiceTracker(bc, filter, null);
            setServiceTracker(st);
            st.open();
            s.setPlayerServiceTracker(st);

        } catch (InvalidSyntaxException ex) {
        }

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Screen.TITLE_PROPERTY, s.getTitle());

        bc.registerService(Screen.class.getName(), s, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        ServiceTracker t = getServiceTracker();
        if (t != null) {
            t.close();
        }

        TransferTracker tt = getTransferTracker();
        if (tt != null) {
            tt.close();
        }

        RCTracker rct = getRCTracker();
        if (rct != null) {
            rct.close();
        }

        ImageCacheTracker ict = getImageCacheTracker();
        if (ict != null) {
            ict.close();
        }
    }

    private int getInitialTime() {
        return (initialTime);
    }

    private void setInitialTime(int i) {
        initialTime = i;
    }

    private int getRestTime() {
        return (restTime);
    }

    private void setRestTime(int i) {
        restTime = i;
    }

    private ServiceTracker getServiceTracker() {
        return (serviceTracker);
    }

    private void setServiceTracker(ServiceTracker t) {
        serviceTracker = t;
    }

    private TransferTracker getTransferTracker() {
        return (transferTracker);
    }

    private void setTransferTracker(TransferTracker t) {
        transferTracker = t;
    }

    private RCTracker getRCTracker() {
        return (rcTracker);
    }

    private void setRCTracker(RCTracker t) {
        rcTracker = t;
    }

    private ImageCacheTracker getImageCacheTracker() {
        return (imageCacheTracker);
    }

    private void setImageCacheTracker(ImageCacheTracker t) {
        imageCacheTracker = t;
    }

    private boolean isDVR() {

        boolean result = false;

        File conf = new File("conf");
        if ((conf.exists() && (conf.isDirectory()))) {

            File prop = new File(conf, "livetvscreen.properties");
            if ((prop.exists() && (prop.isFile()))) {

                Properties p = Util.findProperties(prop);
                if (p != null) {

                    result = Util.str2boolean(p.getProperty("dvr"), result);

                    setInitialTime(Util.str2int(p.getProperty("initialTime"),
                        20));
                    setRestTime(Util.str2int(p.getProperty("restTime"), 2));
                }
            }
        }

        return (result);
    }

}
