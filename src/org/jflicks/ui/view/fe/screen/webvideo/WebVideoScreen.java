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
package org.jflicks.ui.view.fe.screen.webvideo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.nms.WebVideo;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.HtmlDetailPanel;
import org.jflicks.ui.view.fe.HtmlInfoWindow;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.WebVideoListPanel;
import org.jflicks.ui.view.fe.WebVideoProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

/**
 * This class supports Web Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WebVideoScreen extends PlayerScreen implements WebVideoProperty,
    ParameterProperty, PropertyChangeListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.05;
    private static final String FEEDS = "Feeds";
    private static final String YOUTUBE_LEANBACK = "YouTube Leanback";
    private static final String YOUTUBE_LEANBACK_URL =
        "http://www.youtube.com/leanback";

    private String[] parameters;
    private String selectedParameter;
    private boolean updatedParameter;
    private WebVideo[] webVideos;
    private WebVideoListPanel sourceWebVideoListPanel;
    private WebVideoListPanel webVideoListPanel;
    private HtmlDetailPanel htmlDetailPanel;
    private HtmlInfoWindow htmlInfoWindow;

    /**
     * Simple empty constructor.
     */
    public WebVideoScreen() {

        setTitle("Web Video");
        BufferedImage bi = getImageByName("Web_Video");
        setDefaultBackgroundImage(bi);

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

        PageUpAction pageUpAction = new PageUpAction();
        map.put(KeyStroke.getKeyStroke("PAGE_UP"), "pageup");
        getActionMap().put("pageup", pageUpAction);

        PageDownAction pageDownAction = new PageDownAction();
        map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "pagedown");
        getActionMap().put("pagedown", pageDownAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);

        String[] array = {

            FEEDS,
            YOUTUBE_LEANBACK
        };

        setParameters(array);

        JButton b = getDeleteAllowButton();
        if (b != null) {
            b.setVisible(false);
        }

        b = getBookmarkButton();
        if (b != null) {
            b.setVisible(false);
        }

        b = getDeleteButton();
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

    private boolean isParameterFeeds() {
        return (FEEDS.equals(getSelectedParameter()));
    }

    private boolean isParameterYouTubeLeanback() {
        return (YOUTUBE_LEANBACK.equals(getSelectedParameter()));
    }

    /**
     * {@inheritDoc}
     */
    public WebVideo[] getWebVideos() {

        WebVideo[] result = null;

        if (webVideos != null) {

            result = Arrays.copyOf(webVideos, webVideos.length);
        }

        return (result);
    }

    private boolean equals(WebVideo first, WebVideo second) {

        boolean result = false;

        if ((first != null) && (second != null)) {

            String source = first.getSource();
            if (source != null) {

                result = source.equals(second.getSource());
            }
        }

        return (result);
    }

    private boolean contains(ArrayList<WebVideo> l, WebVideo wv) {

        boolean result = false;

        if ((l != null) && (wv != null)) {

            String source = wv.getSource();
            if (source != null) {

                for (int i = 0; i < l.size(); i++) {

                    if (source.equals(l.get(i).getSource())) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setWebVideos(WebVideo[] array) {

        if (array != null) {
            webVideos = Arrays.copyOf(array, array.length);
        } else {
            webVideos = null;
        }

        if (webVideos != null) {

            for (int i = 0; i < webVideos.length; i++) {

                System.out.println("source: " + webVideos[i].getSource());
                System.out.println("title: " + webVideos[i].getTitle());
                System.out.println("URL: " + webVideos[i].getURL());
                System.out.println("Description: "
                    + webVideos[i].getDescription());
                System.out.println("Released: " + webVideos[i].getReleased());
            }
        }

        // Update the UI.
        WebVideoListPanel swvlp = getSourceWebVideoListPanel();
        WebVideoListPanel wvlp = getWebVideoListPanel();
        if ((swvlp != null) && (wvlp != null)) {

            ArrayList<WebVideo> list = new ArrayList<WebVideo>();
            if (webVideos != null) {

                for (int i = 0; i < webVideos.length; i++) {

                    WebVideo tmp = webVideos[i];
                    if (!contains(list, tmp)) {

                        list.add(tmp);
                    }
                }

                Collections.sort(list, new WebVideoSortBySource());
                WebVideo all = new WebVideo();
                all.setSource("All");
                list.add(0, all);
            }

            if (list.size() > 0) {

                WebVideo[] newarray = list.toArray(new WebVideo[list.size()]);
                swvlp.setWebVideos(newarray);

            } else {

                swvlp.setWebVideos(null);
            }
        }
    }

    private WebVideoListPanel getSourceWebVideoListPanel() {
        return (sourceWebVideoListPanel);
    }

    private void setSourceWebVideoListPanel(WebVideoListPanel p) {
        sourceWebVideoListPanel = p;
    }

    private WebVideoListPanel getWebVideoListPanel() {
        return (webVideoListPanel);
    }

    private void setWebVideoListPanel(WebVideoListPanel p) {
        webVideoListPanel = p;
    }

    private HtmlDetailPanel getHtmlDetailPanel() {
        return (htmlDetailPanel);
    }

    private void setHtmlDetailPanel(HtmlDetailPanel p) {
        htmlDetailPanel = p;
    }

    private HtmlInfoWindow getHtmlInfoWindow() {
        return (htmlInfoWindow);
    }

    private void setHtmlInfoWindow(HtmlInfoWindow w) {
        htmlInfoWindow = w;
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
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

            System.out.println("time to layout...");
            float alpha = (float) getPanelAlpha();

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            int wspan = (int) (width * 0.03);
            int listwidth = (width - (3 * wspan)) / 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (3 * hspan)) / 1.5);

            int detailwidth = (int) (width - (2 * wspan));
            int detailheight = (height - (3 * hspan)) - listheight;

            WebVideoListPanel swvlp = new WebVideoListPanel();
            swvlp.setAlpha(alpha);
            swvlp.addPropertyChangeListener("SelectedWebVideo", this);
            swvlp.setSourceName(true);
            swvlp.setControl(true);

            setSourceWebVideoListPanel(swvlp);

            WebVideoListPanel wvlp = new WebVideoListPanel();
            wvlp.setAlpha(alpha);

            wvlp.addPropertyChangeListener("SelectedWebVideo", this);
            setWebVideoListPanel(wvlp);

            HtmlDetailPanel hdp = new HtmlDetailPanel();
            setHtmlDetailPanel(hdp);

            swvlp.setBounds(wspan, hspan, listwidth, listheight);
            wvlp.setBounds(wspan + wspan + listwidth, hspan, listwidth,
                listheight);
            hdp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
               detailheight);

            setHtmlInfoWindow(new HtmlInfoWindow((int) width, (int) height,
                8, getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont()));

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));
        }
    }

    private void updateLayout() {

        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            pane.removeAll();
            if (isParameterFeeds()) {

                pane.add(getSourceWebVideoListPanel(), Integer.valueOf(100));
                pane.add(getWebVideoListPanel(), Integer.valueOf(100));
                pane.add(getHtmlDetailPanel(), Integer.valueOf(100));
                repaint();

            } else if (isParameterYouTubeLeanback()) {

                repaint();

                // Just start up the player!
                Player p = getPlayer();
                if ((p != null) && (!p.isPlaying())) {

                    p.addPropertyChangeListener("Completed", this);
                    p.play(YOUTUBE_LEANBACK_URL);
                }
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

        if (isParameterFeeds()) {

            HtmlInfoWindow w = getHtmlInfoWindow();
            if (w != null) {

                w.setVisible(!w.isVisible());
            }
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

        HtmlInfoWindow w = getHtmlInfoWindow();
        if (w != null) {

            w.setVisible(false);
        }
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
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {
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
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getSource() == getSourceWebVideoListPanel()) {

            WebVideo wv = (WebVideo) event.getNewValue();
            if (wv != null) {

                String source = wv.getSource();
                WebVideoListPanel wvsp = getWebVideoListPanel();
                if ((source != null) && (wvsp != null)) {

                    if (source.equals("All")) {

                        wvsp.setWebVideos(getWebVideos());

                    } else {

                        WebVideo[] vids = getWebVideos();
                        if (vids != null) {

                            ArrayList<WebVideo> wvlist =
                                new ArrayList<WebVideo>();
                            for (int i = 0; i < vids.length; i++) {

                                if (equals(wv, vids[i])) {

                                    wvlist.add(vids[i]);
                                }
                            }

                            if (wvlist.size() > 0) {

                                wvsp.setWebVideos(wvlist.toArray(
                                    new WebVideo[wvlist.size()]));

                            } else {

                                wvsp.setWebVideos(null);
                            }
                        }
                    }

                    wvsp.setSelectedIndex(0);
                }
            }

        } else if (event.getSource() == getWebVideoListPanel()) {

            WebVideo wv = (WebVideo) event.getNewValue();
            ImageCache ic = getImageCache();
            if ((ic != null) && (wv != null)) {

                HtmlDetailPanel hdp = getHtmlDetailPanel();
                if (hdp != null) {

                    hdp.setMarkup(wv.getDescription());
                }

                BufferedImage bi = ic.getImage(wv.getFanartURL());
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
            }

        } else if ((event.getSource() == getPlayer()) && (!isDone())) {

            System.out.println("Player set update");

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);

                RC rc = getRC();
                if (rc != null) {

                    rc.setKeyboardControl(true);
                    rc.setMouseControl(false);
                }

                if (isParameterFeeds()) {

                    requestFocus();

                } else {

                    setDone(true);
                }
            }
        }
    }

    /**
     * We listen for button clicks from our play button panel.
     *
     * @param event A given ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {

        WebVideoListPanel wvlp = getWebVideoListPanel();
        if (wvlp != null) {

            WebVideo wv = wvlp.getSelectedWebVideo();
            Player p = getPlayer();
            RC rc = getRC();
            if ((p != null) && (!p.isPlaying()) && (wv != null)
                && (rc != null)) {

                if (event.getSource() == getBeginningButton()) {

                    HtmlInfoWindow w = getHtmlInfoWindow();
                    if (w != null) {

                        w.setImageCache(getImageCache());
                        w.setWebVideo(wv);
                        w.setPlayer(p);
                    }
                    rc.setKeyboardControl(false);
                    rc.setMouseControl(true);
                    p.addPropertyChangeListener("Completed", this);
                    p.play(wv.getURL());
                }
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel swvlp = getSourceWebVideoListPanel();
            if (swvlp != null) {

                swvlp.setControl(true);
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                wvlp.setControl(false);
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel swvlp = getSourceWebVideoListPanel();
            if (swvlp != null) {

                swvlp.setControl(false);
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                wvlp.setControl(true);
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel swvlp = getSourceWebVideoListPanel();
            if (swvlp != null) {

                if (swvlp.isControl()) {
                    swvlp.moveUp();
                }
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                if (wvlp.isControl()) {
                    wvlp.moveUp();
                }
            }
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel svwlp = getSourceWebVideoListPanel();
            if (svwlp != null) {

                if (svwlp.isControl()) {
                    svwlp.moveDown();
                }
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                if (wvlp.isControl()) {
                    wvlp.moveDown();
                }
            }
        }
    }

    class PageUpAction extends AbstractAction {

        public PageUpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel swvlp = getSourceWebVideoListPanel();
            if (swvlp != null) {

                if (swvlp.isControl()) {
                    swvlp.movePageUp();
                }
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                if (wvlp.isControl()) {
                    wvlp.movePageUp();
                }
            }
        }
    }

    class PageDownAction extends AbstractAction {

        public PageDownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            WebVideoListPanel svwlp = getSourceWebVideoListPanel();
            if (svwlp != null) {

                if (svwlp.isControl()) {
                    svwlp.movePageDown();
                }
            }

            WebVideoListPanel wvlp = getWebVideoListPanel();
            if (wvlp != null) {

                if (wvlp.isControl()) {
                    wvlp.movePageDown();
                }
            }
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isParameterFeeds()) {

                Dialog.showButtonPanel(Util.findFrame(getLayeredPane()),
                    getPlayButtonPanel());
            }
        }
    }

    static class WebVideoSortBySource implements Comparator<WebVideo>,
        Serializable {

        public int compare(WebVideo wv0, WebVideo wv1) {
            return (wv0.getSource().compareTo(wv1.getSource()));
        }
    }

}
