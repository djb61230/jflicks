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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.imagecache.ImageCache;
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
import org.jflicks.ui.view.fe.TextIcon;
import org.jflicks.ui.view.fe.VideoDetailPanel;
import org.jflicks.ui.view.fe.VideoListPanel;
import org.jflicks.ui.view.fe.VideoInfoWindow;
import org.jflicks.ui.view.fe.VideoProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

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

    private VideoListPanel seasonVideoListPanel;
    private VideoListPanel episodeVideoListPanel;
    private PosterPanel posterPanel;
    private SubcategoryListPanel subcategoryListPanel;
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

        String[] array = {

            NMSConstants.VIDEO_MOVIE,
            NMSConstants.VIDEO_TV,
            NMSConstants.VIDEO_HOME,
        };

        setParameters(array);
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

    private VideoListPanel getSeasonVideoListPanel() {
        return (seasonVideoListPanel);
    }

    private void setSeasonVideoListPanel(VideoListPanel p) {
        seasonVideoListPanel = p;
    }

    private VideoListPanel getEpisodeVideoListPanel() {
        return (episodeVideoListPanel);
    }

    private void setEpisodeVideoListPanel(VideoListPanel p) {
        episodeVideoListPanel = p;
    }

    private PosterPanel getPosterPanel() {
        return (posterPanel);
    }

    private void setPosterPanel(PosterPanel p) {
        posterPanel = p;
    }

    private SubcategoryListPanel getSubcategoryListPanel() {
        return (subcategoryListPanel);
    }

    private void setSubcategoryListPanel(SubcategoryListPanel p) {
        subcategoryListPanel = p;
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

    private boolean isParameterTV() {
        return (NMSConstants.VIDEO_TV.equals(getSelectedParameter()));
    }

    /**
     * Override so we can layout stuff right.
     *
     * @param b When true layout the proper components.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            updateLayout();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            float alpha = (float) getPanelAlpha();

            VideoListPanel svlp = new VideoListPanel();
            svlp.setAlpha(alpha);
            svlp.addPropertyChangeListener("SelectedVideo", this);
            svlp.setControl(true);
            setSeasonVideoListPanel(svlp);

            VideoListPanel evlp = new VideoListPanel();
            evlp.setAlpha(alpha);
            evlp.setUseEpisode(true);
            evlp.addPropertyChangeListener("SelectedVideo", this);
            setEpisodeVideoListPanel(evlp);

            PosterPanel pp = new PosterPanel();
            pp.setAlpha(alpha);
            pp.addPropertyChangeListener("SelectedVideo", this);
            setPosterPanel(pp);

            SubcategoryListPanel slp = new SubcategoryListPanel();
            slp.setAlpha(alpha);
            slp.addPropertyChangeListener("SelectedSubcategory", this);
            slp.setControl(true);
            setSubcategoryListPanel(slp);

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

            svlp.setBounds(wspan, hspan, listwidth, listheight);
            evlp.setBounds(wspan + wspan + listwidth, hspan, listwidth,
                listheight);
            slp.setBounds(wspan, hspan, sublistwidth, listheight);
            pp.setBounds(wspan + wspan + sublistwidth, hspan, ppwidth,
                listheight);
            vdp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
                detailheight);

            setVideoInfoWindow(new VideoInfoWindow((int) width, (int) height,
                8, getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont()));

            // Create our blank panel.
            JXPanel blank = new JXPanel();
            MattePainter blankp = new MattePainter(Color.BLACK);
            blank.setBackgroundPainter(blankp);
            blank.setBounds(0, 0, (int) width, (int) height);
            setBlankPanel(blank);
        }
    }

    private void updateLayout() {

        PosterPanel pp = getPosterPanel();
        JLayeredPane pane = getLayeredPane();
        if ((pp != null) && (pane != null)) {

            pp.close();
            pane.removeAll();
            if (isParameterTV()) {

                pane.add(getSeasonVideoListPanel(), Integer.valueOf(100));
                pane.add(getEpisodeVideoListPanel(), Integer.valueOf(100));
                pane.add(getVideoDetailPanel(), Integer.valueOf(100));

            } else {

                pp.open();
                pane.add(pp, Integer.valueOf(100));
                pane.add(getSubcategoryListPanel(), Integer.valueOf(100));
                pane.add(getVideoDetailPanel(), Integer.valueOf(100));
            }
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
            if (isParameterTV()) {

                VideoListPanel evlp = getEpisodeVideoListPanel();
                if (evlp != null) {

                    v = evlp.getSelectedVideo();
                }

            } else {

                PosterPanel pp = getPosterPanel();
                if (pp != null) {

                    v = pp.getSelectedVideo();
                }
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
        if (isParameterTV()) {

            VideoListPanel evlp = getEpisodeVideoListPanel();
            if (evlp != null) {

                v = evlp.getSelectedVideo();
            }

        } else {

            PosterPanel pp = getPosterPanel();
            if (pp != null) {

                v = pp.getSelectedVideo();
            }
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

            log(DEBUG, "Seems like we got a new list...");

            if (array != null) {
                videos = Arrays.copyOf(array, array.length);
            } else {
                videos = null;
            }

            if (isParameterTV()) {

                VideoListPanel svlp = getSeasonVideoListPanel();
                VideoListPanel evlp = getEpisodeVideoListPanel();
                if ((svlp != null) && (evlp != null)) {

                    svlp.setControl(true);
                    evlp.setControl(false);
                }
            }

            if (videos != null) {

                Arrays.sort(videos, new VideoSortByTitle());
                applySubcategory();
                applyVideo();
                applyVideoBackground();
            }
        }
    }

    private boolean equals(Video first, Video second) {

        boolean result = false;

        if ((first != null) && (second != null)) {

            String title = first.getTitle();
            if (title != null) {

                result = title.equals(second.getTitle());
                if (result) {

                    result = first.getSeason() == second.getSeason();

                    if (result) {

                        result = first.isTV() == second.isTV();
                    }
                }
            }
        }

        return (result);
    }

    private boolean contains(ArrayList<Video> l, Video v) {

        boolean result = false;

        if ((l != null) && (v != null)) {

            int season = v.getSeason();
            String title = v.getTitle();
            if (title != null) {

                for (int i = 0; i < l.size(); i++) {

                    Video tmp = l.get(i);
                    if ((title.equals(tmp.getTitle()))
                        && (season == tmp.getSeason())) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private void applySubcategory() {

        if (!isParameterTV()) {

            String cat = getSelectedParameter();
            SubcategoryListPanel slp = getSubcategoryListPanel();
            if ((cat != null) && (slp != null)) {

                String[] array = getSubcategoryByCategory(getVideos(), cat);
                slp.setSubcategories(array);
            }
        }
    }

    private void applyVideo() {

        if (isParameterTV()) {

            // We have to pick out the TV episodes first.
            VideoListPanel svlp = getSeasonVideoListPanel();
            if (svlp != null) {

                if (videos != null) {

                    ArrayList<Video> justtv = new ArrayList<Video>();
                    for (int i = 0; i < videos.length; i++) {

                        if (videos[i].isTV()) {

                            justtv.add(videos[i]);
                        }
                    }

                    // Next we make a list of just Title/Season so we have
                    // a season to pick.
                    if (justtv.size() > 0) {

                        ArrayList<Video> season = new ArrayList<Video>();
                        for (int i = 0; i < justtv.size(); i++) {

                            Video tmp = justtv.get(i);
                            if (!contains(season, tmp)) {

                                season.add(tmp);
                            }
                        }

                        // At this point we have all the seasons...
                        if (season.size() > 0) {

                            Video[] array =
                                season.toArray(new Video[season.size()]);
                            svlp.setVideos(array);

                        } else {

                            svlp.setVideos(null);
                        }

                    } else {

                        svlp.setVideos(null);
                    }

                } else {

                    svlp.setVideos(null);
                }
            }

        } else {

            // We have to pick out the Video by cat and subcat.
            String cat = getSelectedParameter();
            SubcategoryListPanel slp = getSubcategoryListPanel();
            PosterPanel pp = getPosterPanel();
            if ((cat != null) && (slp != null) && (pp != null)) {

                String sub = slp.getSelectedSubcategory();
                if ((sub != null) && (videos != null)) {

                    Video[] catsub = getVideoByCategoryAndSubcategory(videos,
                        cat, sub);
                    BufferedImage[] images = getVideoPosters(catsub);
                    pp.setVideos(catsub);
                    pp.setBufferedImages(images);
                }
            }
        }
    }

    private void applyVideoBackground() {

        Video v = null;
        if (isParameterTV()) {

            VideoListPanel evlp = getEpisodeVideoListPanel();
            if (evlp != null) {

                v = evlp.getSelectedVideo();
            }

        } else {

            PosterPanel pp = getPosterPanel();
            if (pp != null) {

                v = pp.getSelectedVideo();
            }
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

    private String[] getSubcategoryByCategory(Video[] array, String category) {

        String[] result = null;

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

                result = l.toArray(new String[l.size()]);
                Arrays.sort(result);
            }
        }

        return (result);
    }

    private Video[] getVideoByCategoryAndSubcategory(Video[] array,
        String category, String subcategory) {

        Video[] result = null;

        if ((array != null) && (category != null) && (subcategory != null)) {

            ArrayList<Video> l = new ArrayList<Video>();
            for (int i = 0; i < array.length; i++) {

                if ((array[i].isCategory(category))
                    && (array[i].isSubcategory(subcategory))) {

                    l.add(array[i]);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new Video[l.size()]);
            }
        }

        return (result);
    }

    private BufferedImage[] getVideoPosters(Video[] array) {

        BufferedImage[] result = null;

        ImageCache ic = getImageCache();
        PosterPanel pp = getPosterPanel();
        if ((array != null) && (ic != null) && (pp != null)) {

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

                    l.add(bi);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new BufferedImage[l.size()]);
            }
        }

        return (result);
    }

    private String getIntro(Video v) {

        String result = null;

        if (v != null) {

            String s = v.getAspectRatio();
            NMS n = NMSUtil.select(getNMS(), v.getHostPort());
            FrontEndView fev = (FrontEndView) getView();
            log(DEBUG, "aspect: <" + s + ">");
            log(DEBUG, "nms: <" + n + ">");
            log(DEBUG, "hostport: <" + v.getHostPort() + ">");
            if ((s != null) && (n != null) && (fev != null)) {

                if (s.equals(NMSConstants.ASPECT_RATIO_16X9)) {

                    result = fev.transformPath(n.getFeatureIntro169());
                    log(DEBUG, "result 169: <" + result + ">");

                } else if (s.equals(NMSConstants.ASPECT_RATIO_235X1)) {

                    result = fev.transformPath(n.getFeatureIntro235());
                    log(DEBUG, "result 235: <" + result + ">");

                } else if (s.equals(NMSConstants.ASPECT_RATIO_4X3)) {

                    result = fev.transformPath(n.getFeatureIntro43());
                    log(DEBUG, "result 43: <" + result + ">");
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
                Video v = null;
                if (isParameterTV()) {

                    VideoListPanel evlp = getEpisodeVideoListPanel();
                    if (evlp != null) {

                        v = evlp.getSelectedVideo();
                    }

                } else {

                    PosterPanel pp = getPosterPanel();
                    if (pp != null) {

                        v = pp.getSelectedVideo();
                    }
                }

                if (v != null) {

                    close();
                    deleteBookmark(v.getId());
                }
            }

            requestFocus();

        } else if (event.getSource() == getSeasonVideoListPanel()) {

            VideoListPanel svlp = getSeasonVideoListPanel();
            VideoListPanel evlp = getEpisodeVideoListPanel();
            Video v = svlp.getSelectedVideo();
            if (v != null) {

                if (videos != null) {

                    ArrayList<Video> vlist = new ArrayList<Video>();
                    for (int i = 0; i < videos.length; i++) {

                        if (equals(v, videos[i])) {

                            vlist.add(videos[i]);
                        }
                    }

                    if (vlist.size() > 0) {

                        Video[] episodes =
                            vlist.toArray(new Video[vlist.size()]);
                        Arrays.sort(episodes, new VideoSortByEpisode());
                        evlp.setVideos(episodes);

                    } else {

                        evlp.setVideos(null);
                    }
                }

            } else {

                evlp.setVideos(null);
            }

        } else if (event.getSource() == getEpisodeVideoListPanel()) {

            VideoDetailPanel dp = getVideoDetailPanel();
            if (dp != null) {

                Video v = (Video) event.getNewValue();
                dp.setVideo(v);
                applyVideoBackground();
            }

        } else if (event.getSource() == getSubcategoryListPanel()) {

            applyVideo();

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
            if (isParameterTV()) {

                VideoListPanel evlp = getEpisodeVideoListPanel();
                if (evlp != null) {

                    v = evlp.getSelectedVideo();
                }

            } else {

                PosterPanel pp = getPosterPanel();
                if (pp != null) {

                    v = pp.getSelectedVideo();
                }
            }

            if (v != null) {

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

                    if ((v.isPlayIntro()) && (isWantIntro())) {

                        String intropath = getIntro(v);
                        if (intropath != null) {

                            controlKeyboard(false);
                            p.setFrame(Util.findFrame(this));
                            addBlankPanel();
                            p.play(intropath, v.getPath());

                        } else {

                            controlKeyboard(false);
                            p.setFrame(Util.findFrame(this));
                            addBlankPanel();
                            p.play(v.getPath());
                        }

                    } else {

                        controlKeyboard(false);
                        p.setFrame(Util.findFrame(this));
                        addBlankPanel();
                        p.play(v.getPath());
                    }

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
                    p.play(v.getPath(), getBookmark(v.getId()));

                } else if (CANCEL.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "cancel hit");
                }

                unpopup();
            }
        }

    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                if (isParameterTV()) {

                    VideoListPanel svlp = getSeasonVideoListPanel();
                    if (svlp != null) {

                        svlp.setControl(true);
                    }

                    VideoListPanel evlp = getEpisodeVideoListPanel();
                    if (evlp != null) {

                        evlp.setControl(false);
                    }

                } else {

                    PosterPanel pp = getPosterPanel();
                    if (pp != null) {

                        pp.prev();
                    }
                }
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                if (isParameterTV()) {

                    VideoListPanel svlp = getSeasonVideoListPanel();
                    if (svlp != null) {

                        svlp.setControl(false);
                    }

                    VideoListPanel evlp = getEpisodeVideoListPanel();
                    if (evlp != null) {

                        evlp.setControl(true);
                    }

                } else {

                    PosterPanel pp = getPosterPanel();
                    if (pp != null) {

                        pp.next();
                    }
                }
            }
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

            } else {

                if (isParameterTV()) {

                    VideoListPanel svlp = getSeasonVideoListPanel();
                    if (svlp != null) {

                        if (svlp.isControl()) {
                            svlp.moveUp();
                        }
                    }

                    VideoListPanel evlp = getEpisodeVideoListPanel();
                    if (evlp != null) {

                        if (evlp.isControl()) {
                            evlp.moveUp();
                        }
                    }

                } else {

                    SubcategoryListPanel slp = getSubcategoryListPanel();
                    if (slp != null) {

                        slp.moveUp();
                    }
                }
            }
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

            } else {

                if (isParameterTV()) {

                    VideoListPanel svlp = getSeasonVideoListPanel();
                    if (svlp != null) {

                        if (svlp.isControl()) {
                            svlp.moveDown();
                        }
                    }

                    VideoListPanel evlp = getEpisodeVideoListPanel();
                    if (evlp != null) {

                        if (evlp.isControl()) {
                            evlp.moveDown();
                        }
                    }

                } else {

                    SubcategoryListPanel slp = getSubcategoryListPanel();
                    if (slp != null) {

                        slp.moveDown();
                    }
                }
            }
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

    static class VideoSortByEpisode implements Comparator<Video>, Serializable {

        public VideoSortByEpisode() {
        }

        public int compare(Video v0, Video v1) {

            Integer i0 = Integer.valueOf(v0.getEpisode());
            Integer i1 = Integer.valueOf(v1.getEpisode());

            return (i0.compareTo(i1));
        }
    }

}

