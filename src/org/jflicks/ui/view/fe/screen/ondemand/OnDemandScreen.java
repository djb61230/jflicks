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
package org.jflicks.ui.view.fe.screen.ondemand;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.ondemand.StreamSession;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Hostname;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class will display video stream data from an OnDemand source.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class OnDemandScreen extends PlayerScreen implements NMSProperty,
    ParameterProperty, PropertyChangeListener {

    private NMS[] nms;
    private StreamSession streamSession;
    private JobContainer guideJobContainer;
    private Properties properties;
    private String selectedParameter;
    private String[] parameters;

    /**
     * Simple empty constructor.
     */
    public OnDemandScreen() {

        setTitle("On Demand");
        BufferedImage bi = getImageByName("On_Demand");
        setDefaultBackgroundImage(bi);

        Properties p = null;
        FileReader fr = null;
        try {

            File here = new File(".");
            File conf = new File(here, "conf");
            if ((conf.exists()) && (conf.isDirectory())) {

                File prop = new File(conf, "ondemand.properties");
                if ((prop.exists()) && (prop.isFile())) {

                    p = new Properties();
                    fr = new FileReader(prop);
                    p.load(fr);
                    fr.close();
                    fr = null;
                }
            }

        } catch (IOException ex) {

            log(WARNING, ex.getMessage());

        } finally {

            try {

                if (fr != null) {

                    fr.close();
                    fr = null;
                }

            } catch (IOException ex) {

                fr = null;
            }
        }

        if (p != null) {

            setProperties(p);
            ArrayList<String> l = new ArrayList<String>();
            Enumeration en = p.propertyNames();
            while (en.hasMoreElements()) {

                l.add((String) en.nextElement());
            }

            if (l.size() > 0) {

                Collections.sort(l);
                setParameters(l.toArray(new String[l.size()]));
            }
        }
    }

    private StreamSession getStreamSession() {
        return (streamSession);
    }

    private void setStreamSession(StreamSession ss) {
        streamSession = ss;
    }

    private Properties getProperties() {
        return (properties);
    }

    private void setProperties(Properties p) {
        properties = p;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameters() {

        String[] result = null;

        if (parameters != null) {

            result = Arrays.copyOf(parameters, parameters.length);
        }

        return (result);
    }

    private void setParameters(String[] array) {

        if (array != null) {
            parameters = Arrays.copyOf(array, array.length);
        } else {
            parameters = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectedParameter() {
        return (selectedParameter);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedParameter(String s) {
        selectedParameter = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS[] getNMS() {

        NMS[] result = null;

        if (nms != null) {

            result = Arrays.copyOf(nms, nms.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS[] array) {

        if (array != null) {
            nms = Arrays.copyOf(array, array.length);
        } else {
            nms = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Getting stream, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            panel.add(l, BorderLayout.CENTER);
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());

            pane.add(panel, Integer.valueOf(100));
        }
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            NMS[] array = getNMS();
            String odname = getSelectedParameter();
            if ((array != null) && (array.length > 0) && (odname != null)) {

                NMS n = null;

                // Just use the first one we find....
                for (int i = 0; i < array.length; i++) {

                    if (array[i].supportsOnDemand(odname)) {

                        n = array[i];
                        break;
                    }
                }

                String hostaddr = Hostname.getHostAddress();

                if ((hostaddr != null) && (n != null)) {

                    StreamSession ss = n.openSession(odname, hostaddr, 1234);
                    log(DEBUG, "Called openstream: " + ss);
                    if (ss != null) {

                        log(DEBUG, "ss: " + ss.getHostPort());
                        setStreamSession(ss);

                        Player p = getPlayer();
                        if (p != null) {

                            View v = getView();
                            if (v instanceof FrontEndView) {

                                FrontEndView fev = (FrontEndView) v;
                                p.setRectangle(fev.getPosition());
                            }
                            p.addPropertyChangeListener("Paused", this);
                            p.addPropertyChangeListener("Completed", this);
                            controlKeyboard(false);
                            p.setFrame(Util.findFrame(this));
                            p.play("udp://@" + hostaddr + ":1234");
                        }

                    } else {

                        JXLabel l =
                            new JXLabel("Sorry, " + odname + " is busy.");
                        l.setFont(getLargeFont());
                        JButton ok = new JButton("OK");
                        ok.setFont(getLargeFont());
                        JXPanel p = new JXPanel(new BorderLayout());
                        p.add(l, BorderLayout.NORTH);
                        p.add(ok, BorderLayout.SOUTH);
                        Dialog.showPanel(Util.findFrame(this), p, ok);
                        setDone(true);
                    }

                } else {

                    setDone(true);
                }

            } else {

                setDone(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Bookmark createBookmark() {

        Bookmark result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookmarkId() {

        String result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void info() {
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command home...");
                n.command(ss, OnDemand.COMMAND_HOME);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        controlKeyboard(true);
        StreamSession ss = getStreamSession();
        log(DEBUG, "OnDemandScreen: close: " + ss);
        if (ss != null) {

            log(DEBUG, "OnDemandScreen: close: " + ss.getHostPort());
            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "calling closeSession...");
                n.closeSession(ss);
                setStreamSession(null);

                Player p = getPlayer();
                if (p != null) {

                    p.stop();
                }
            }
        }

        setDone(true);
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command fwd...");
                n.command(ss, OnDemand.COMMAND_FWD);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command back...");
                n.command(ss, OnDemand.COMMAND_BACK);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command up...");
                n.command(ss, OnDemand.COMMAND_UP);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command down...");
                n.command(ss, OnDemand.COMMAND_DOWN);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command left...");
                n.command(ss, OnDemand.COMMAND_LEFT);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command right...");
                n.command(ss, OnDemand.COMMAND_RIGHT);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        StreamSession ss = getStreamSession();
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                log(DEBUG, "command select...");
                n.command(ss, OnDemand.COMMAND_SELECT);
            }
        }
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            if (event.getPropertyName().equals("Completed")) {

                // If we get this property update, then it means the video
                // finished playing on it's own.
                Boolean bobj = (Boolean) event.getNewValue();
                if (bobj.booleanValue()) {

                    getPlayer().removePropertyChangeListener(this);
                    log(DEBUG, "we are stopping player says so");

                    // What we are doing here is closing our session in
                    // another Thread because we are getting a remote
                    // exception.  We appear to be having a call getting
                    // interrupted when we close directly.  We throw it
                    // in a timer to allow the original call to complete.
                    // This is certainly a hack and I haven't determined
                    // exactly why it's happening the way it is - but this
                    // does seem to fix it.  Bleh.
                    ActionListener closePerformer = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            close();
                        }
                    };
                    Timer closeTimer = new Timer(1000, closePerformer);
                    closeTimer.setRepeats(false);
                    closeTimer.start();

                    setDone(true);
                }

            } else if (event.getPropertyName().equals("Paused")) {

                StreamSession ss = getStreamSession();
                if (ss != null) {

                    NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
                    if (n != null) {

                        n.command(ss, OnDemand.COMMAND_PAUSE);
                    }
                }

            } else if (event.getPropertyName().equals("Paused")) {

                StreamSession ss = getStreamSession();
                if (ss != null) {

                    requestFocus();
                }
            }
        }
    }

    /**
     * We actually don't do anything here at this point.
     *
     * @param event A given ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {
    }

}

