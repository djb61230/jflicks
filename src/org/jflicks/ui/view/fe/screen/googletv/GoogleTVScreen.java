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
package org.jflicks.ui.view.fe.screen.googletv;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.SimpleInfoWindow;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports GoogleTV in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GoogleTVScreen extends PlayerScreen implements ParameterProperty,
    PropertyChangeListener {

    private String[] parameters;
    private String selectedParameter;
    private Properties properties;
    private SimpleInfoWindow simpleInfoWindow;

    /**
     * Simple empty constructor.
     */
    public GoogleTVScreen() {

        setTitle("Google TV");
        BufferedImage bi = getImageByName("GoogleTV");
        setDefaultBackgroundImage(bi);

        Properties p = null;
        FileReader fr = null;
        try {

            File here = new File(".");
            File conf = new File(here, "conf");
            if ((conf.exists()) && (conf.isDirectory())) {

                File prop = new File(conf, "googletv.properties");
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

    private Properties getProperties() {
        return (properties);
    }

    private void setProperties(Properties p) {
        properties = p;
    }

    private SimpleInfoWindow getSimpleInfoWindow() {
        return (simpleInfoWindow);
    }

    private void setSimpleInfoWindow(SimpleInfoWindow w) {
        simpleInfoWindow = w;
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            Properties prop = getProperties();
            if (prop != null) {

                String url = prop.getProperty(getSelectedParameter());
                if (url != null) {

                    int index = url.indexOf(",");
                    if (index != -1) {

                        String rest = url.substring(index + 1);
                        rest = rest.trim();
                        url = url.substring(0, index);
                        boolean keyboard = Util.str2boolean(rest, true);
                        RC rc = getRC();
                        if (rc != null) {

                            rc.setKeyboardControl(keyboard);
                        }
                    }

                    SimpleInfoWindow w = getSimpleInfoWindow();
                    if (w != null) {

                        w.setTitle(getSelectedParameter());
                        w.setDescription("You are watching a Google TV"
                            + " based website called " + getSelectedParameter()
                            + ".  The website URL is " + url);
                    }

                    // Just start up the player!
                    Player p = getPlayer();
                    if ((p != null) && (!p.isPlaying())) {

                        p.addPropertyChangeListener("Completed", this);
                        p.play(url);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            FrontEndView fev = (FrontEndView) getView();
            SimpleInfoWindow siv = new SimpleInfoWindow(fev.getPosition(), 8,
                getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());

            BufferedImage bi = null;

            try {

                bi = ImageIO.read(getClass().getResource("banner.png"));

            } catch (IOException ex) {

                log(INFO, "Failed to load banner image");
            }

            siv.setBannerBufferedImage(bi);
            setSimpleInfoWindow(siv);

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Launching Chrome, please wait...");
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

        SimpleInfoWindow w = getSimpleInfoWindow();
        if (w != null) {

            w.setVisible(!w.isVisible());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
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

        Player p = getPlayer();
        if ((p != null) && (p.isPlaying())) {

            p.next();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        Player p = getPlayer();
        if ((p != null) && (p.isPlaying())) {

            p.previous();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {
    }

    /**
     * {@inheritDoc}
     */
    public void down() {
    }

    /**
     * {@inheritDoc}
     */
    public void left() {
    }

    /**
     * {@inheritDoc}
     */
    public void right() {
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
    }

    /**
     * Screens that use Plaers usually need to listen for action events from
     * a user selected button but we just launch chrome.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            log(DEBUG, "Player set update");

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);
                setDone(true);

                RC rc = getRC();
                if (rc != null) {

                    rc.setKeyboardControl(true);
                }
            }
        }
    }

}
