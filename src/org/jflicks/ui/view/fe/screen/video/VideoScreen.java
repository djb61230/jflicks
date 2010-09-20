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
package org.jflicks.ui.view.fe.screen.video;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.Video;
import org.jflicks.mvc.View;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.player.PlayState;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.LabelPanel;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.TextIcon;
import org.jflicks.ui.view.fe.VideoDetailPanel;
import org.jflicks.ui.view.fe.VideoInfoWindow;
import org.jflicks.ui.view.fe.VideoProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

/**
 * This class supports Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoScreen extends PlayerScreen implements VideoProperty,
    NMSProperty, ParameterProperty, PropertyChangeListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.05;

    private LabelPanel subcategoryLabelPanel;
    private LabelPanel videoLabelPanel;
    private VideoInfoWindow videoInfoWindow;
    private VideoDetailPanel videoDetailPanel;
    private Video[] videos;
    private String[] parameters;
    private String selectedParameter;
    private boolean updatedParameter;
    private NMS[] nms;

    /**
     * Simple empty constructor.
     */
    public VideoScreen() {

        setTitle("Video Library");
        BufferedImage bi = getImageByName("Video_Library");
        setDefaultBackgroundImage(bi);

        File home = new File(".");
        File dbhome = new File(home, "db");
        setBookmarkFile(new File(dbhome, "vidbookmarks.dat"));
        System.out.println(getBookmarkFile());
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

        String[] array = {

            NMSConstants.VIDEO_MOVIE,
            NMSConstants.VIDEO_TV,
            NMSConstants.VIDEO_HOME,
        };

        setParameters(array);

        JButton b = getDeleteAllowButton();
        if (b != null) {
            b.setVisible(false);
        }

        b = getStopRecordingButton();
        if (b != null) {
            b.setVisible(false);
        }
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

    private LabelPanel getSubcategoryLabelPanel() {
        return (subcategoryLabelPanel);
    }

    private void setSubcategoryLabelPanel(LabelPanel p) {
        subcategoryLabelPanel = p;
    }

    private LabelPanel getVideoLabelPanel() {
        return (videoLabelPanel);
    }

    private void setVideoLabelPanel(LabelPanel p) {
        videoLabelPanel = p;
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
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            LabelPanel subPane = new LabelPanel(3, "Sub");
            subPane.setAspectRatio(3.0);
            setSubcategoryLabelPanel(subPane);

            LabelPanel vidPane = new LabelPanel(3, "Video");
            setVideoLabelPanel(vidPane);

            VideoDetailPanel vdp = new VideoDetailPanel();
            vdp.setAlpha((float) getPanelAlpha());
            setVideoDetailPanel(vdp);

            double width = d.getWidth();
            double height = d.getHeight();

            double hgap = width * HGAP;
            double vgap = height * VGAP;

            double paneWidth = width - (hgap * 2);
            double paneTopHeight = (height - (vgap * 4)) / 6;
            double paneMidHeight = (height - (vgap * 4)) / 2;
            double paneBotHeight = (height - (vgap * 4)) / 3;

            subPane.setBounds((int) hgap, (int) (vgap), (int) paneWidth,
                (int) paneTopHeight);

            vidPane.setBounds((int) hgap,
                (int) (vgap * 2.0 + paneTopHeight),
                (int) paneWidth, (int) paneMidHeight);

            vdp.setBounds((int) hgap,
                (int) (vgap * 3.0 + paneTopHeight + paneMidHeight),
                (int) paneWidth, (int) paneBotHeight);

            pane.add(subPane, Integer.valueOf(100));
            pane.add(vidPane, Integer.valueOf(100));
            pane.add(vdp, Integer.valueOf(100));

            setVideoInfoWindow(new VideoInfoWindow((int) width, (int) height,
                8, getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Bookmark createBookmark() {

        Bookmark result = null;

        Player p = getPlayer();
        LabelPanel vidPane = getVideoLabelPanel();
        if ((vidPane != null) && (p != null)) {

            PlayState ps = p.getPlayState();
            TextIcon ti = vidPane.getSelectedTextIcon();
            if ((ti != null) && (ps != null)) {

                Video v = getVideoById(ti.getId());

                if (v != null) {

                    result = new Bookmark();
                    result.setTime((int) ps.getTime());
                    result.setPosition(ps.getPosition());
                    result.setPreferTime(true);
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookmarkId() {

        String result = null;

        LabelPanel vidPane = getVideoLabelPanel();
        if (vidPane != null) {

            TextIcon ti = vidPane.getSelectedTextIcon();
            if (ti != null) {

                Video v = getVideoById(ti.getId());
                if (v != null) {

                    result = v.getId();
                }
            }
        }

        return (result);
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
    public void close() {

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

        if ((s != null) && (selectedParameter != null)) {
            setUpdatedParameter(!s.equals(selectedParameter));
        } else {
            setUpdatedParameter(true);
        }

        selectedParameter = s;
    }

    private boolean isUpdatedParameter() {
        return (updatedParameter);
    }

    private void setUpdatedParameter(boolean b) {
        updatedParameter = b;
    }

    /**
     * {@inheritDoc}
     */
    public Video[] getVideos() {

        Video[] result = null;

        if (videos != null) {

            result = Arrays.copyOf(videos, videos.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setVideos(Video[] array) {

        if (isUpdatedParameter()) {

            System.out.println("Seems like we got a new list...");

            if (array != null) {
                videos = Arrays.copyOf(array, array.length);
            } else {
                videos = null;
            }

            LabelPanel subPane = getSubcategoryLabelPanel();
            LabelPanel vidPane = getVideoLabelPanel();
            if ((subPane != null) && (vidPane != null)) {

                subPane.setControl(true);
                vidPane.setControl(false);
            }

            if (videos != null) {

                Arrays.sort(videos, new VideoSortByTitle());
                applySubcategory();
                applyVideo();
                applyVideoBackground();
                applyVideoDetail();
            }
        }
    }

    private void applySubcategory() {

        String cat = getSelectedParameter();
        LabelPanel subPane = getSubcategoryLabelPanel();
        if ((cat != null) && (subPane != null)) {

            TextIcon[] array = getByCategory(getVideos(), cat);
            subPane.setTextIcons(array);
        }
    }

    private void applyVideo() {

        String cat = getSelectedParameter();
        LabelPanel subPane = getSubcategoryLabelPanel();
        LabelPanel vidPane = getVideoLabelPanel();
        if ((cat != null) && (subPane != null) && (vidPane != null)) {

            TextIcon subti = subPane.getSelectedTextIcon();
            if (subti != null) {

                TextIcon[] array = getByCategoryAndSubcategory(getVideos(),
                    cat, subti.getText());
                vidPane.setTextIcons(array);
            }
        }
    }

    private void applyVideoBackground() {

        LabelPanel vidPane = getVideoLabelPanel();
        ImageCache ic = getImageCache();
        if ((vidPane != null) && (ic != null)) {

            TextIcon ti = vidPane.getSelectedTextIcon();
            if (ti != null) {

                Video v = getVideoById(ti.getId());
                if (v != null) {

                    BufferedImage bi = ic.getImage(v.getFanartURL(), false);
                    if (bi != null) {

                        Dimension d = getSize();
                        if (d != null) {

                            if (bi.getWidth() < d.getWidth()) {

                                bi = Util.scaleLarger((int) d.getWidth(), bi);
                            }

                            setCurrentBackgroundImage(bi);

                        } else {

                            setCurrentBackgroundImage(
                                getDefaultBackgroundImage());
                        }

                    } else {

                        setCurrentBackgroundImage(getDefaultBackgroundImage());
                    }

                } else {

                    setCurrentBackgroundImage(getDefaultBackgroundImage());
                }
            }

        } else {

            setCurrentBackgroundImage(getDefaultBackgroundImage());
        }
    }

    private void applyVideoDetail() {

        LabelPanel vidPane = getVideoLabelPanel();
        VideoDetailPanel vdp = getVideoDetailPanel();
        if ((vidPane != null) && (vdp != null)) {

            TextIcon ti = vidPane.getSelectedTextIcon();
            if (ti != null) {

                vdp.setVideo(getVideoById(ti.getId()));
            }
        }
    }

    private TextIcon[] getByCategory(Video[] array, String category) {

        TextIcon[] result = null;

        if ((array != null) && (category != null)) {

            ArrayList<String> l = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                if (array[i].isCategory(category)) {

                    String sub = array[i].getSubcategory();
                    if (sub != null) {

                        if (!l.contains(sub)) {
                            l.add(sub);
                        }
                    }
                }
            }

            if (l.size() > 0) {

                result = new TextIcon[l.size()];
                for (int i = 0; i < l.size(); i++) {

                    String sub = l.get(i);
                    result[i] = new TextIcon(sub, null);
                }

                Arrays.sort(result, new TextIconSort());
            }
        }

        return (result);
    }

    private String getIntro(Video v) {

        String result = null;

        if (v != null) {

            String s = v.getAspectRatio();
            NMS n = NMSUtil.select(getNMS(), v.getHostPort());
            System.out.println("aspect: <" + s + ">");
            System.out.println("nms: <" + n + ">");
            System.out.println("hostport: <" + v.getHostPort() + ">");
            if ((s != null) && (n != null)) {

                if (s.equals(NMSConstants.ASPECT_RATIO_16X9)) {

                    result = n.getFeatureIntro169();
                    System.out.println("result 169: <" + result +">");

                } else if (s.equals(NMSConstants.ASPECT_RATIO_235X1)) {

                    result = n.getFeatureIntro235();
                    System.out.println("result 235: <" + result +">");

                } else if (s.equals(NMSConstants.ASPECT_RATIO_4X3)) {

                    result = n.getFeatureIntro43();
                    System.out.println("result 43: <" + result +">");
                }
            }
        }

        return (result);
    }

    private Video getVideoById(String s) {

        Video result = null;

        Video[] array = getVideos();
        if ((s != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (s.equals(array[i].getId())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private TextIcon[] getByCategoryAndSubcategory(Video[] array,
        String category, String subcategory) {

        TextIcon[] result = null;

        ImageCache ic = getImageCache();
        LabelPanel vidPane = getVideoLabelPanel();
        if ((array != null) && (category != null) && (subcategory != null)
            && (ic != null) && (vidPane != null)) {

            ArrayList<TextIcon> l = new ArrayList<TextIcon>();
            for (int i = 0; i < array.length; i++) {

                if ((array[i].isCategory(category))
                    && (array[i].isSubcategory(subcategory))) {

                    ImageIcon ii = null;

                    String turl = array[i].getPosterURL();

                    // First see if a scaled image exists...
                    BufferedImage bi = ic.getImage(turl + ".scaled.png", false);
                    if (bi == null) {

                        // Then put it in if the original exists...
                        bi = ic.getImage(turl, false);
                        if (bi != null) {

                            bi = Util.resize(bi, vidPane.getLabelWidth(),
                                vidPane.getLabelHeight());
                            if (bi != null) {

                                // next time we find it...
                                ic.putImage(turl + ".scaled.png", bi);
                            }
                        }
                    }

                    if (bi != null) {

                        ii = new ImageIcon(bi);

                    } else {

                        String tmp = "missing_poster.png";
                        ii = new ImageIcon(getClass().getResource(tmp));
                    }

                    TextIcon ti = new TextIcon(array[i].getTitle(), ii);
                    ti.setId(array[i].getId());
                    l.add(ti);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new TextIcon[l.size()]);
            }
        }

        return (result);
    }

    private boolean isWantIntro() {

        boolean result = false;

        View v = getView();
        if (v != null) {

            result =
                Util.str2boolean(v.getProperty(JFlicksView.WANT_INTRO), result);
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
                LabelPanel vidPane = getVideoLabelPanel();
                if (vidPane != null) {

                    TextIcon ti = vidPane.getSelectedTextIcon();
                    if (ti != null) {

                        Video v = getVideoById(ti.getId());
                        if (v != null) {

                            close();
                            deleteBookmark(v.getId());
                        }
                    }
                }
            }

            requestFocus();
        }
    }

    /**
     * We listen for button clicks from our play button panel.
     *
     * @param event A given ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {

        LabelPanel vidPane = getVideoLabelPanel();
        Player p = getPlayer();
        if ((vidPane != null) && (p != null) && (!p.isPlaying())) {

            TextIcon ti = vidPane.getSelectedTextIcon();
            if (ti != null) {

                Video v = getVideoById(ti.getId());
                if (v != null) {

                    if (event.getSource() == getBeginningButton()) {

                        p.addPropertyChangeListener("Completed", this);
                        VideoInfoWindow w = getVideoInfoWindow();
                        if (w != null) {

                            w.setImageCache(getImageCache());
                            w.setVideo(v);
                            w.setPlayer(p);
                        }

                        if ((v.isPlayIntro()) && (isWantIntro())) {

                            String intropath = getIntro(v);
                            if (intropath != null) {

                                try {

                                    intropath = intropath + "\n";
                                    String vpath = v.getPath();
                                    vpath = vpath + "\n";
                                    FileWriter fw = new FileWriter("intro.txt");
                                    fw.write(intropath, 0, intropath.length());
                                    fw.write(vpath, 0, vpath.length());
                                    fw.close();

                                    p.play("-playlist intro.txt");

                                } catch (IOException ex) {

                                    p.play(v.getPath());
                                }

                            } else {

                                p.play(v.getPath());
                            }

                        } else {

                            p.play(v.getPath());
                        }

                    } else if (event.getSource() == getBookmarkButton()) {

                        p.addPropertyChangeListener("Completed", this);
                        VideoInfoWindow w = getVideoInfoWindow();
                        if (w != null) {

                            w.setImageCache(getImageCache());
                            w.setVideo(v);
                            w.setPlayer(p);
                        }
                        p.play(v.getPath(), getBookmark(v.getId()));

                    } else if (event.getSource() == getDeleteButton()) {

                        System.out.println("delete hit");

                    } else if (event.getSource() == getCancelButton()) {

                        System.out.println("cancel hit");
                    }
                }
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            LabelPanel subPane = getSubcategoryLabelPanel();
            LabelPanel vidPane = getVideoLabelPanel();
            if ((subPane != null) && (vidPane != null)) {

                if (subPane.isControl()) {

                    subPane.prev();
                    applyVideo();

                } else if (vidPane.isControl()) {

                    vidPane.prev();
                }

                applyVideoBackground();
                applyVideoDetail();
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            LabelPanel subPane = getSubcategoryLabelPanel();
            LabelPanel vidPane = getVideoLabelPanel();
            if ((subPane != null) && (vidPane != null)) {

                if (subPane.isControl()) {

                    subPane.next();
                    applyVideo();

                } else if (vidPane.isControl()) {

                    vidPane.next();
                }

                applyVideoBackground();
                applyVideoDetail();
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            LabelPanel subPane = getSubcategoryLabelPanel();
            LabelPanel vidPane = getVideoLabelPanel();
            if ((subPane != null) && (vidPane != null)) {

                if (subPane.isControl()) {

                    subPane.setControl(false);
                    vidPane.setControl(true);

                } else if (vidPane.isControl()) {

                    subPane.setControl(true);
                    vidPane.setControl(false);
                }
            }
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            LabelPanel subPane = getSubcategoryLabelPanel();
            LabelPanel vidPane = getVideoLabelPanel();
            if ((subPane != null) && (vidPane != null)) {

                if (subPane.isControl()) {

                    subPane.setControl(false);
                    vidPane.setControl(true);

                } else if (vidPane.isControl()) {

                    subPane.setControl(true);
                    vidPane.setControl(false);
                }
            }
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            JButton b = getBookmarkButton();
            if (b != null) {
                b.setEnabled(hasBookmark(getBookmarkId()));
            }

            Dialog.showButtonPanel(Util.findFrame(getLayeredPane()),
                getPlayButtonPanel());
        }
    }

    static class TextIconSort implements Comparator<TextIcon>, Serializable {

        public int compare(TextIcon ti0, TextIcon ti1) {

            String s0 = ti0.getText();
            String s1 = ti1.getText();

            return (s0.compareTo(s1));
        }
    }

    static class VideoSortByTitle implements Comparator<Video>, Serializable {

        private StringBuilder stringBuilder;

        public VideoSortByTitle() {

            stringBuilder = new StringBuilder();
        }

        private String toCompareString(Video v) {

            stringBuilder.setLength(0);
            stringBuilder.append(v.getTitle());
            if (v.isTV()) {

                int season = v.getSeason();
                if (season < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append("" + season);

                int episode = v.getEpisode();
                if (episode < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append("" + episode);
            }

            return (stringBuilder.toString());
        }

        public int compare(Video v0, Video v1) {

            String s0 = toCompareString(v0);
            String s1 = toCompareString(v1);

            return (s0.compareTo(s1));
        }
    }

}

