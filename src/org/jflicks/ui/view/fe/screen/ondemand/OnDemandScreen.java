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
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.job.JobContainer;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.rc.RC;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.ondemand.StreamSession;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;

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
    PropertyChangeListener {

    private NMS[] nms;
    private StreamSession streamSession;
    private JobContainer guideJobContainer;

    /**
     * Simple empty constructor.
     */
    public OnDemandScreen() {

        setTitle("On Demand");
        BufferedImage bi = getImageByName("On_Demand");
        setDefaultBackgroundImage(bi);
    }

    private StreamSession getStreamSession() {
        return (streamSession);
    }

    private void setStreamSession(StreamSession ss) {
        streamSession = ss;
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

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

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
            if ((array != null) && (array.length > 0)) {

                NMS n = null;

                // Just use the first one we find....
                for (int i = 0; i < array.length; i++) {

                    if (array[i].supportsOnDemand()) {

                        n = array[i];
                        break;
                    }
                }

                InetAddress addr = null;
                try {

                    addr = InetAddress.getLocalHost();

                } catch (UnknownHostException ex) {
                }

                if ((addr != null) && (n != null)) {

                    String hostaddr = addr.getHostAddress();
                    StreamSession ss = n.openSession("Roku", hostaddr, 1234);
                    System.out.println("Called openstream: " + ss);
                    if (ss != null) {

                        System.out.println("ss: " + ss);
                        setStreamSession(ss);

                        Player p = getPlayer();
                        if (p != null) {

                            p.addPropertyChangeListener("Paused", this);
                            p.addPropertyChangeListener("Completed", this);
                            p.play("udp://@" + hostaddr + ":1234");
                        }

                        RC rc = getRC();
                        if (rc != null) {

                            rc.setKeyboardControl(false);
                        }

                    } else {

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

                System.out.println("command home...");
                n.command(ss, OnDemand.COMMAND_HOME);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        StreamSession ss = getStreamSession();
        System.out.println("OnDemandScreen: close: " + ss);
        if (ss != null) {

            NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
            if (n != null) {

                System.out.println("calling stop...");
                n.closeSession(ss);
                setStreamSession(null);

                Player p = getPlayer();
                if (p != null) {

                    p.stop();
                }

                RC rc = getRC();
                if (rc != null) {

                    rc.setKeyboardControl(true);
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

                System.out.println("command fwd...");
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

                System.out.println("command back...");
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

                System.out.println("command up...");
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

                System.out.println("command down...");
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

                System.out.println("command left...");
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

                System.out.println("command right...");
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

                System.out.println("command select...");
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
                if (!bobj.booleanValue()) {

                    getPlayer().removePropertyChangeListener(this);
                    System.out.println("we are stopping player says so");

                    close();
                    setDone(true);
                }

            } else if (event.getPropertyName().equals("Paused")) {

                StreamSession ss = getStreamSession();
                if (ss != null) {

                    NMS n = NMSUtil.select(getNMS(), ss.getHostPort());
                    if (n != null) {

                        System.out.println("command pause...");
                        n.command(ss, OnDemand.COMMAND_PAUSE);
                    }
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

