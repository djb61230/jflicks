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
package org.jflicks.ui.view.fe.screen.net;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.Video;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.player.PlayState;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.fe.ButtonPanel;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.PosterPanel;
import org.jflicks.ui.view.fe.TextListPanel;
import org.jflicks.ui.view.fe.TextIcon;
import org.jflicks.ui.view.fe.VideoDetailPanel;
import org.jflicks.ui.view.fe.VideoListPanel;
import org.jflicks.ui.view.fe.VideoInfoWindow;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.ui.view.fe.screen.ScreenEvent;
import org.jflicks.util.Busy;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports net feed Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class NetVideoScreen extends PlayerScreen implements JobListener,
    NMSProperty, ParameterProperty, PropertyChangeListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.05;

    private PosterPanel posterPanel;
    private VideoInfoWindow videoInfoWindow;
    private VideoDetailPanel videoDetailPanel;
    private Video[] videos;
    private String[] parameters;
    private String selectedParameter;
    private Properties properties;
    private boolean updatedParameter;
    private NMS[] nms;
    private JXPanel waitPanel;

    /**
     * Simple empty constructor.
     */
    public NetVideoScreen() {

        setTitle("Net Video");
        BufferedImage bi = getImageByName("Net_Video");
        setDefaultBackgroundImage(bi);

        Properties p = null;
        FileReader fr = null;
        try {

            File here = new File(".");
            File conf = new File(here, "conf");
            if ((conf.exists()) && (conf.isDirectory())) {

                File prop = new File(conf, "netvideo.properties");
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

        File home = new File(".");
        File dbhome = new File(home, "db");
        setBookmarkFile(new File(dbhome, "netvidbookmarks.dat"));
        log(DEBUG, getBookmarkFile().getPath());
        load();

        setFocusable(true);
        requestFocus();

        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        LeftAction leftAction = new LeftAction();
        map.put(KeyStroke.getKeyStroke("LEFT"), "left");
        getActionMap().put("left", leftAction);

        RightAction rightAction = new RightAction();
        map.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        getActionMap().put("right", rightAction);

        UpAction upAction = new UpAction();
        map.put(KeyStroke.getKeyStroke("UP"), "up");
        getActionMap().put("up", upAction);
        DownAction downAction = new DownAction();
        map.put(KeyStroke.getKeyStroke("DOWN"), "down");
        getActionMap().put("down", downAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);
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

    private JXPanel getWaitPanel() {
        return (waitPanel);
    }

    private void setWaitPanel(JXPanel p) {
        waitPanel = p;
    }

    private PosterPanel getPosterPanel() {
        return (posterPanel);
    }

    private void setPosterPanel(PosterPanel p) {
        posterPanel = p;
    }

    private VideoDetailPanel getVideoDetailPanel() {
        return (videoDetailPanel);
    }

    private void setVideoDetailPanel(VideoDetailPanel p) {
        videoDetailPanel = p;
    }

    private VideoInfoWindow getVideoInfoWindow() {
        return (videoInfoWindow);
    }

    private void setVideoInfoWindow(VideoInfoWindow w) {
        videoInfoWindow = w;
    }

    /**
     * Override so we can layout stuff right.
     *
     * @param b When true layout the proper components.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            updateLayout(true);
            Properties p = getProperties();
            String s = getSelectedParameter();
            if ((p != null) && (s != null)) {

                String url = p.getProperty(s);
                if (url != null) {

                    FetchFeedJob job = new FetchFeedJob(this, url);
                    Busy busy = new Busy(getLayeredPane(), job);
                    busy.addJobListener(this);
                    busy.execute();
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

            float alpha = (float) getPanelAlpha();

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Getting feed data, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            panel.add(l, BorderLayout.CENTER);
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());
            setWaitPanel(panel);

            PosterPanel pp = new PosterPanel(5, 1.0);
            setEffects(isEffects());
            pp.setAlpha(alpha);
            pp.addPropertyChangeListener("SelectedVideo", this);
            setPosterPanel(pp);

            VideoDetailPanel vdp = new VideoDetailPanel();
            vdp.setAlpha(alpha);
            setVideoDetailPanel(vdp);

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            int wspan = (int) (width * 0.03);
            int listwidth = (width - (3 * wspan)) / 2;
            int sublistwidth = (width - (3 * wspan)) / 3;
            int ppwidth = (width - (3 * wspan)) - sublistwidth;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (3 * hspan)) * 0.55);

            int detailwidth = (int) (width - (2 * wspan));
            int detailheight = height - listheight - (hspan * 3);

            pp.setBounds(wspan, hspan, detailwidth, listheight);
            vdp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
                detailheight);

            FrontEndView fev = (FrontEndView) getView();
            setVideoInfoWindow(new VideoInfoWindow(fev.getPosition(),
                8, getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont()));
            getVideoInfoWindow().setVisible(false);

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));

            // Create our blank panel.
            JXPanel blank = new JXPanel();
            MattePainter blankp = new MattePainter(Color.BLACK);
            blank.setBackgroundPainter(blankp);
            blank.setBounds(0, 0, (int) width, (int) height);
            setBlankPanel(blank);
        }
    }

    private void updateLayout(boolean wait) {

        PosterPanel pp = getPosterPanel();
        JLayeredPane pane = getLayeredPane();
        if ((pp != null) && (pane != null)) {

            pane.removeAll();
            if (wait) {

                pane.add(getWaitPanel(), Integer.valueOf(100));

            } else {

                pane.add(pp, Integer.valueOf(100));
                pane.add(getVideoDetailPanel(), Integer.valueOf(100));
            }

            repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Bookmark createBookmark() {

        Bookmark result = null;

        Player p = getPlayer();
        if (p != null) {

            Video v = null;

            PosterPanel pp = getPosterPanel();
            if (pp != null) {

                v = pp.getSelectedVideo();
            }

            if (v != null) {

                PlayState ps = p.getPlayState();
                result = new Bookmark();
                result.setTime((int) ps.getTime());
                result.setPosition(ps.getPosition());
                result.setPreferTime(true);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookmarkId() {

        String result = null;

        Video v = null;
        PosterPanel pp = getPosterPanel();
        if (pp != null) {

            v = pp.getSelectedVideo();
        }

        if (v != null) {

            result = v.getId();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setDone(boolean b) {

        if (b) {

            PosterPanel pp = getPosterPanel();
            if (pp != null) {

                pp.close();
            }
        }

        super.setDone(b);
    }

    /**
     * {@inheritDoc}
     */
    public void info() {

        VideoInfoWindow w = getVideoInfoWindow();
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
    public void pageup() {
    }

    /**
     * {@inheritDoc}
     */
    public void pagedown() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        removeBlankPanel();
        controlKeyboard(true);
        VideoInfoWindow w = getVideoInfoWindow();
        if (w != null) {

            w.setVisible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(30);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(600);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(-600);
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

        Player p = getPlayer();
        if (p != null) {

            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(30);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
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

    /**
     * This allows us to have code to set the parameters instead of
     * hardwiring in the constructor.
     *
     * @param array The parameters to use.
     */
    public void setParameters(String[] array) {

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

        if ((s != null) && (selectedParameter != null)) {
            setUpdatedParameter(!s.equals(selectedParameter));
        } else {
            setUpdatedParameter(true);
        }

        selectedParameter = s;
    }

    private Properties getProperties() {
        return (properties);
    }

    private void setProperties(Properties p) {
        properties = p;
    }

    private boolean isUpdatedParameter() {
        return (updatedParameter);
    }

    private void setUpdatedParameter(boolean b) {
        updatedParameter = b;
    }

    public void applyVideo(Video[] videos) {

        PosterPanel pp = getPosterPanel();
        if ((pp != null) && (videos != null)) {

            BufferedImage[] images = getVideoPosters(videos);
            pp.setVideos(videos);
            pp.setBufferedImages(images);
        }
    }

    private void applyVideoBackground() {

        Video v = null;

        PosterPanel pp = getPosterPanel();
        if (pp != null) {

            v = pp.getSelectedVideo();
        }

        ImageCache ic = getImageCache();
        if ((v != null) && (ic != null)) {

            BufferedImage bi = ic.getImage(v.getFanartURL(), false);
            if (bi != null) {

                Dimension d = getSize();
                if (d != null) {

                    if (bi.getWidth() < d.getWidth()) {

                        bi = Util.scaleLarger((int) d.getWidth(), bi);
                    }

                    setCurrentBackgroundImage(bi);

                } else {

                    setCurrentBackgroundImage(getDefaultBackgroundImage());
                }

            } else {

                setCurrentBackgroundImage(getDefaultBackgroundImage());
            }

        } else {

            setCurrentBackgroundImage(getDefaultBackgroundImage());
        }
    }

    private BufferedImage[] getVideoPosters(Video[] array) {

        BufferedImage[] result = null;

        ImageCache ic = getImageCache();
        PosterPanel pp = getPosterPanel();
        if ((array != null) && (ic != null) && (pp != null)) {

            ReflectionRenderer rr = new ReflectionRenderer();
            ArrayList<BufferedImage> l = new ArrayList<BufferedImage>();
            for (int i = 0; i < array.length; i++) {

                String turl = array[i].getPosterURL();

                // First see if a scaled image exists...
                BufferedImage bi = ic.getImage(turl + ".scaled.png", false);
                if (bi == null) {

                    // Then put it in if the original exists...
                    bi = ic.getImage(turl, false);
                    if (bi != null) {

                        bi = Util.resize(bi, pp.getPosterWidth(),
                            pp.getPosterHeight());
                        if (bi != null) {

                            // next time we find it...
                            ic.putImage(turl + ".scaled.png", bi);
                        }

                    } else {

                        try {

                            String tmp = "missing_poster.png";
                            bi = ImageIO.read(getClass().getResource(tmp));
                            if (bi != null) {

                                bi = Util.resize(bi, pp.getPosterWidth(),
                                    pp.getPosterHeight());

                            } else {

                                bi = null;
                            }

                        } catch (IOException ex) {

                            log(INFO, ex.getMessage());
                        }
                    }

                } else {

                    bi = Util.resize(bi, pp.getPosterWidth(),
                        pp.getPosterHeight());
                }

                if (bi != null) {

                    bi = rr.appendReflection(bi);
                    l.add(bi);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new BufferedImage[l.size()]);
            }
        }

        return (result);
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);
                Video v = null;
                PosterPanel pp = getPosterPanel();
                if (pp != null) {

                    v = pp.getSelectedVideo();
                }

                if (v != null) {

                    close();
                    deleteBookmark(v.getId());
                }
            }

            requestFocus();

        } else if (event.getSource() == getPosterPanel()) {

            VideoDetailPanel dp = getVideoDetailPanel();
            if (dp != null) {

                Video v = (Video) event.getNewValue();
                dp.setVideo(v);
                applyVideoBackground();
            }
        }
    }

    /**
     * We listen for button clicks from our play button panel.
     *
     * @param event A given ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {

        Player p = getPlayer();
        if ((p != null) && (!p.isPlaying())
            && (event.getSource() == getPlayButtonPanel())) {

            Video v = null;

            PosterPanel pp = getPosterPanel();
            if (pp != null) {

                v = pp.getSelectedVideo();
            }

            if (v != null) {

                String vidpath = v.getStreamURL();
                ButtonPanel pbp = getPlayButtonPanel();
                if (PLAY.equals(pbp.getSelectedButton())) {

                    p.addPropertyChangeListener("Completed", this);
                    VideoInfoWindow w = getVideoInfoWindow();
                    if (w != null) {

                        w.setImageCache(getImageCache());
                        w.setVideo(v);
                        w.setPlayer(p);
                    }

                    View vw = getView();
                    if (vw instanceof FrontEndView) {

                        FrontEndView fev = (FrontEndView) vw;
                        p.setRectangle(fev.getPosition());
                    }

                    controlKeyboard(false);
                    p.setFrame(Util.findFrame(this));
                    addBlankPanel();
                    p.play(vidpath);

                } else if (PLAY_FROM_BOOKMARK.equals(pbp.getSelectedButton())) {

                    p.addPropertyChangeListener("Completed", this);
                    VideoInfoWindow w = getVideoInfoWindow();
                    if (w != null) {

                        w.setImageCache(getImageCache());
                        w.setVideo(v);
                        w.setPlayer(p);
                    }
                    View vw = getView();
                    if (vw instanceof FrontEndView) {

                        FrontEndView fev = (FrontEndView) vw;
                        p.setRectangle(fev.getPosition());
                    }

                    controlKeyboard(false);
                    p.setFrame(Util.findFrame(this));
                    addBlankPanel();
                    p.play(vidpath, getBookmark(v.getId()));

                } else if (CANCEL.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "cancel hit");
                }

                unpopup();
            }
        }

    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            updateLayout(false);
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                PosterPanel pp = getPosterPanel();
                if (pp != null) {

                    pp.next();
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                PosterPanel pp = getPosterPanel();
                if (pp != null) {

                    pp.prev();
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveUp();
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveDown();
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ArrayList<String> blist = new ArrayList<String>();
            blist.add(PLAY);
            if (hasBookmark(getBookmarkId())) {
                blist.add(PLAY_FROM_BOOKMARK);
            }
            blist.add(CANCEL);

            popup(blist.toArray(new String[blist.size()]));

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

}

