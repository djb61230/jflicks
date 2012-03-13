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
package org.jflicks.ui.view.fe;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.Video;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.Tag;
import org.jflicks.rc.RC;
import org.jflicks.rc.RCProperty;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Upcoming;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.fe.screen.ExecuteScreen;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.ui.view.fe.screen.ScreenEvent;
import org.jflicks.ui.view.fe.screen.ScreenListener;
import org.jflicks.util.Util;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * A base class that full Views can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FrontEndView extends JFlicksView implements ActionListener,
    PropertyChangeListener, RCProperty, ScreenListener {

    private JXFrame frame;
    private JXPanel panel;
    private TextImagePanel textIconPanel;
    private ArrayList<Screen> screenList;
    private NMS[] nms;
    private RC rc;
    private Cursor noCursor;
    private boolean hideMouse;
    private int pathCount;
    private ArrayList<String> fromPathList;
    private ArrayList<String> toPathList;
    private Rectangle positionRectangle;
    private Timer activityTimer;
    private ActivityActionListener activityActionListener;
    private JWindow blankWindow;
    private long lastActivityMillis;

    /**
     * Default constructor.
     */
    public FrontEndView() {

        setScreenList(new ArrayList<Screen>());
        setPathCount(-1);
        setFromPathList(new ArrayList<String>());
        setToPathList(new ArrayList<String>());

        setLastActivityMillis(System.currentTimeMillis());
        ActivityActionListener aal = new ActivityActionListener();
        setActivityActionListener(aal);
        Timer t = new Timer(60000, aal);
        setActivityTimer(t);
        t.start();
    }

    /**
     * {@inheritDoc}
     */
    public RC getRC() {
        return (rc);
    }

    /**
     * {@inheritDoc}
     */
    public void setRC(RC r) {
        rc = r;

        if (rc != null) {

            rc.setKeyboardControl(true);
            rc.setEventControl(true);
        }
    }

    private NMS[] getNMS() {
        return (nms);
    }

    private void setNMS(NMS[] array) {
        nms = array;
    }

    private Cursor getNoCursor() {
        return (noCursor);
    }

    private void setNoCursor(Cursor c) {
        noCursor = c;
    }

    private JXPanel getPanel() {
        return (panel);
    }

    private void setPanel(JXPanel p) {
        panel = p;
    }

    private TextImagePanel getTextImagePanel() {
        return (textIconPanel);
    }

    private void setTextImagePanel(TextImagePanel p) {
        textIconPanel = p;
    }

    private ArrayList<Screen> getScreenList() {
        return (screenList);
    }

    private void setScreenList(ArrayList<Screen> l) {
        screenList = l;
    }

    private ActivityActionListener getActivityActionListener() {
        return (activityActionListener);
    }

    private void setActivityActionListener(ActivityActionListener aal) {
        activityActionListener = aal;
    }

    private Timer getActivityTimer() {
        return (activityTimer);
    }

    private void setActivityTimer(Timer t) {
        activityTimer = t;
    }

    private long getLastActivityMillis() {
        return (lastActivityMillis);
    }

    private void setLastActivityMillis(long l) {
        lastActivityMillis = l;
    }

    private boolean isBlanked() {

        boolean result = false;

        if (blankWindow != null) {

            result = blankWindow.isVisible();
        }

        return (result);
    }

    private void setBlanked(boolean b) {

        if (blankWindow != null) {

            blankWindow.setVisible(b);
        }
    }

    /**
     * We keep track of screens that are available to us.
     *
     * @param s A given screen.
     */
    public void addScreen(Screen s) {

        ArrayList<Screen> l = getScreenList();
        if ((l != null) && (s != null)) {

            s.setView(this);
            l.add(s);
            Collections.sort(l);
        }
    }

    /**
     * We keep track of screens that are available to us.
     *
     * @param s A given screen.
     */
    public void removeScreen(Screen s) {

        ArrayList<Screen> l = getScreenList();
        if ((l != null) && (s != null)) {

            l.remove(s);
        }
    }

    private Screen[] getScreens() {

        Screen[] result = null;

        ArrayList<Screen> l = getScreenList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Screen[l.size()]);
        }

        return (result);
    }

    private ArrayList<String> getFromPathList() {
        return (fromPathList);
    }

    private void setFromPathList(ArrayList<String> l) {
        fromPathList = l;
    }

    private ArrayList<String> getToPathList() {
        return (toPathList);
    }

    private void setToPathList(ArrayList<String> l) {
        toPathList = l;
    }

    private int getPathCount() {

        if (pathCount == -1) {

            // First time here.  Lets check the Properties just once if
            // path transforms even exist.  This will save parsing Strings
            // a tremendous amount og times.  First thing set it to zero.
            pathCount = 0;
            Properties p = getProperties();
            if (p != null) {

                pathCount = Util.str2int(p.getProperty("pathCount"), 0);

                if (pathCount > 0) {

                    // It's easier to process the paths if we put them in
                    // associative lists.  So lets do that now.
                    ArrayList<String> fl = getFromPathList();
                    ArrayList<String> tl = getToPathList();
                    if ((fl != null) && (tl != null)) {

                        fl.clear();
                        tl.clear();
                        for (int i = 0; i < pathCount; i++) {

                            String from = p.getProperty("pathFrom" + i);
                            String to = p.getProperty("pathTo" + i);
                            if ((from != null) && (to != null)) {

                                fl.add(from);
                                tl.add(to);
                            }
                        }

                        // Reset the pathCount to the actual pairs we found.
                        pathCount = fl.size();
                    }
                }
            }
        }

        return (pathCount);
    }

    private void setPathCount(int i) {
        pathCount = i;
    }

    /**
     * Convenience method available to Screens to transform any path
     * to something else that is available to this client.  Path
     * transformations are defined in jflicks.properties in the install
     * directory.
     *
     * @param path Transform the given path to something else.
     * @return A proper path for this client.
     */
    public String transformPath(String path) {

        String result = path;

        if (path != null) {

            int pcount = getPathCount();
            if (pcount > 0) {

                ArrayList<String> fl = getFromPathList();
                ArrayList<String> tl = getToPathList();
                if ((fl != null) && (tl != null)) {

                    for (int i = 0; i < fl.size(); i++) {

                        String from = fl.get(i);
                        if (path.startsWith(from)) {

                            // We are done.  Set out result.
                            int length = from.length();
                            result = tl.get(i) + path.substring(length);
                            if (Util.isWindows()) {

                                result = result.replace("/", "\\");
                            }
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private void transformRecording(Recording r) {

        if (r != null) {

            if (getPathCount() > 0) {

                r.setPath(transformPath(r.getPath()));
            }
        }
    }

    private void transformRecordings(Recording[] array) {

        if ((array != null) && (array.length > 0)) {

            if (getPathCount() > 0) {

                // We have paths to check.  We can't be sure actually
                // anything will change but we have to check.
                for (int i = 0; i < array.length; i++) {

                    array[i].setPath(transformPath(array[i].getPath()));
                }
            }
        }
    }

    private void transformPhotos(Photo[] array) {

        if ((array != null) && (array.length > 0)) {

            if (getPathCount() > 0) {

                // We have paths to check.  We can't be sure actually
                // anything will change but we have to check.
                for (int i = 0; i < array.length; i++) {

                    array[i].setPath(transformPath(array[i].getPath()));
                }
            }
        }
    }

    private void transformVideos(Video[] array) {

        if ((array != null) && (array.length > 0)) {

            if (getPathCount() > 0) {

                // We have paths to check.  We can't be sure actually
                // anything will change but we have to check.
                for (int i = 0; i < array.length; i++) {

                    array[i].setPath(transformPath(array[i].getPath()));
                }
            }
        }
    }

    private NMS[] filterNMS(NMS[] array) {

        // By default don't filter anything...
        NMS[] result = array;

        if (array != null) {

            Properties p = getProperties();
            if (p != null) {

                String groups = p.getProperty("groups");
                if (groups != null) {

                    // We only filter if groups is set to something...
                    String[] garray = groups.split(",");
                    if ((garray != null) && (garray.length > 0)) {

                        ArrayList<NMS> list = new ArrayList<NMS>();
                        for (int i = 0; i < garray.length; i++) {

                            for (int j = 0; j < array.length; j++) {

                                String gn = array[j].getGroupName();
                                if (garray[i].equals(gn)) {

                                    if (!list.contains(array[j])) {

                                        list.add(array[j]);
                                    }
                                }
                            }
                        }

                        if (list.size() > 0) {

                            result = list.toArray(new NMS[list.size()]);

                        } else {

                            result = null;
                        }
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void modelPropertyChange(PropertyChangeEvent event) {

        String name = event.getPropertyName();
        if (name != null) {

            if (name.equals("NMS")) {

                NMS[] array = (NMS[]) event.getNewValue();

                array = filterNMS(array);
                setNMS(array);
            }
        }
    }

    /**
     * Determine the size of the desktop but retuen a subset of it if the user
     * has defined a size in the application's properties file.
     *
     * @return A Rectangle instance.
     */
    public Rectangle getPosition() {

        if (positionRectangle == null) {

            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = 0;
            int y = 0;
            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            Properties p = getProperties();
            if (p != null) {

                x = Util.str2int(p.getProperty("x"), x);
                y = Util.str2int(p.getProperty("y"), y);
                width = Util.str2int(p.getProperty("width"), width);
                height = Util.str2int(p.getProperty("height"), height);
            }

            positionRectangle = new Rectangle(x, y, width, height);

        } else {

            // We have been here before.  We want to change positionRectangle
            // if the user has dragged it off somewhere.  We only worry
            // about this when undecorated is false.
            if (!isUndecorated()) {

                positionRectangle = frame.getBounds();
            }
        }

        return (positionRectangle);
    }

    private boolean isBlankingEnabled() {

        boolean result = false;

        Properties p = getProperties();
        if (p != null) {

            result = Util.str2boolean(p.getProperty("blankingenabled"), result);
        }

        return (result);
    }

    private int getMaxInactivity() {

        int result = 3;

        Properties p = getProperties();
        if (p != null) {

            result = Util.str2int(p.getProperty("maxinactivity"), result);
        }

        return (result);
    }

    private long getMaxInactivityMillis() {

        long mins = (long) getMaxInactivity();
        return (mins * 60 * 1000);
    }

    private boolean isUndecorated() {

        boolean result = true;

        Properties p = getProperties();
        if (p != null) {

            result = Util.str2boolean(p.getProperty("undecorated"), result);
        }

        return (result);
    }

    private boolean isEffects() {

        boolean result = true;

        Properties p = getProperties();
        if (p != null) {

            result = Util.str2boolean(p.getProperty("effects"), result);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public JFrame getFrame() {

        if (frame == null) {

            Toolkit t = Toolkit.getDefaultToolkit();
            if (t != null) {

                Dimension d = t.getBestCursorSize(1, 1);
                if (d != null) {

                    int w = (int) d.getWidth();
                    int h = (int) d.getHeight();
                    if ((w != 0) && (h != 0)) {

                        setNoCursor(t.createCustomCursor(
                            new BufferedImage(w, h,
                                BufferedImage.TYPE_INT_ARGB),
                                new Point(0, 0), "NO_CURSOR"));

                    } else {

                        setNoCursor(null);
                    }
                }
            }

            Rectangle position = getPosition();
            int x = (int) position.getX();
            int y = (int) position.getY();
            int width = (int) position.getWidth();
            int height = (int) position.getHeight();

            frame = new JXFrame();
            frame.setTitle("jflicks media system TV UI");
            frame.setUndecorated(isUndecorated());
            frame.setResizable(false);
            frame.setBounds(x, y, width, height);

            blankWindow = new JWindow(frame);
            blankWindow.setBounds(x, y, width, height);
            JXPanel bwpanel = new JXPanel();
            bwpanel.setBackground(Color.BLACK);
            bwpanel.setBounds(0, 0, width, height);
            bwpanel.setFocusable(false);
            blankWindow.getLayeredPane().add(bwpanel);
            blankWindow.getContentPane().setCursor(getNoCursor());

            JXPanel cards = new JXPanel(new CardLayout());
            cards.setBackground(Color.BLACK);
            setPanel(cards);

            // This assumes we get all screens before we get here to
            // build our Frame.  Probably a bad assumption but this is
            // what we will do for now...
            Screen[] array = getScreens();
            if (array != null) {

                ArrayList<TextImage> list = new ArrayList<TextImage>();
                for (int i = 0; i < array.length; i++) {

                    array[i].setEffects(isEffects());
                    array[i].setDone(true);
                    array[i].addScreenListener(this);
                    array[i].addPropertyChangeListener("Done", this);
                    String title = array[i].getTitle();
                    cards.add(title, array[i]);
                    BufferedImage bufcap = GraphicsUtilities.toCompatibleImage(
                        array[i].getDefaultBackgroundImage());
                    TextImage ti = new TextImage(title, bufcap);
                    list.add(ti);

                    if (array[i] instanceof ExecuteScreen) {

                        ExecuteScreen escreen = (ExecuteScreen) array[i];
                        String[] commands = escreen.getCommands();
                        if (commands != null) {

                            for (int j = 0; j < commands.length; j++) {

                                TextImage cti = new TextImage(commands[j], ti);
                                list.add(cti);
                            }
                        }
                    }

                    if (array[i] instanceof ParameterProperty) {

                        log(WARNING, "Now getting parameter - screen.");
                        ParameterProperty pp = (ParameterProperty) array[i];
                        String[] parameters = pp.getParameters();
                        if (parameters != null) {

                            for (int j = 0; j < parameters.length; j++) {

                                TextImage pti =
                                    new TextImage(parameters[j], ti);
                                list.add(pti);
                            }
                        }
                    }
                }

                TextImage[] tiarray =
                    list.toArray(new TextImage[list.size()]);

                TextImagePanel tip =
                    new TextImagePanel(tiarray, getLogoImage());
                tip.setEffects(isEffects());
                cards.add(tip, "main");

                tip.addActionListener(this);
                tip.setBounds(0, 0, width, height);
                setTextImagePanel(tip);

                EscapeAction escapeAction = new EscapeAction();
                InputMap map =
                    tip.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                map.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
                tip.getActionMap().put("escape", escapeAction);
            }

            CardLayout cl = (CardLayout) cards.getLayout();
            cl.show(cards, "main");
            setLastActivityMillis(System.currentTimeMillis());

            frame.setLayout(new BorderLayout());
            frame.add(cards, BorderLayout.CENTER);
            setHideMouse(true);
        }

        return (frame);
    }

    /**
     * This method will re-layout our application.  This needs to be called
     * when the user has changed their theme or other display property.
     */
    public void restartUI() {

        JXPanel p = getPanel();
        if (p != null) {

            Screen[] array = getScreens();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    array[i].restartUI();
                }
            }

            TextImagePanel tip = getTextImagePanel();
            if (tip != null) {

                TextImage[] tiarray = tip.getTextImages();
                if (tiarray != null) {

                    for (int i = 0; i < tiarray.length; i++) {

                        reset(tiarray[i]);
                    }
                }

                tip.restartUI();
            }
        }
    }

    private void reset(TextImage ti) {

        Screen[] array = getScreens();
        if ((ti != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                String s = ti.getText();
                if (s != null) {

                    if (s.equals(array[i].getTitle())) {

                        ti.setImage(array[i].getDefaultBackgroundImage());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Screens may want access to the defined logo.  They can get it
     * here and display it however they choose.
     *
     * @return A BufferedImage instance if such a logo exists.
     */
    public BufferedImage getLogoImage() {

        BufferedImage result = null;

        try {

            result = ImageIO.read(getClass().getResource("logo.png"));
            result = GraphicsUtilities.toCompatibleImage(result);

        } catch (IOException ex) {
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void messageReceived(String s) {

        NMS[] array = getNMS();
        if ((s != null) && (array != null)) {

            Screen[] sarray = getScreens();
            if (sarray != null) {

                if (s.startsWith(NMSConstants.MESSAGE_RECORDING_UPDATE)) {

                    String rid = s.substring(s.indexOf(":") + 1);
                    rid = rid.trim();
                    for (int i = 0; i < array.length; i++) {

                        Recording r = array[i].getRecordingById(rid);
                        if (r != null) {

                            for (int j = 0; j < sarray.length; j++) {

                                if (sarray[j] instanceof RecordingProperty) {

                                    RecordingProperty rp =
                                        (RecordingProperty) sarray[j];
                                    transformRecording(r);
                                    rp.updateRecording(r);
                                }
                            }
                        }
                    }

                } else if ((s.startsWith(NMSConstants.MESSAGE_RECORDING_ADDED))
                    || (s.startsWith(NMSConstants.MESSAGE_RECORDING_REMOVED))) {

                    for (int i = 0; i < sarray.length; i++) {

                        if (sarray[i] instanceof RecordingProperty) {

                            applyRecordings((RecordingProperty) sarray[i]);
                        }
                    }

                } else if (s.startsWith(NMSConstants.MESSAGE_SCHEDULE_UPDATE)) {

                    for (int i = 0; i < sarray.length; i++) {

                        if (sarray[i] instanceof UpcomingProperty) {

                            applyUpcomings((UpcomingProperty) sarray[i]);
                        }
                    }

                } else if (s.startsWith(NMSConstants.MESSAGE_RULE_UPDATE)) {

                    for (int i = 0; i < sarray.length; i++) {

                        if (sarray[i] instanceof RecordingRuleProperty) {

                            applyRecordingRules(
                                (RecordingRuleProperty) sarray[i]);
                        }
                    }
                }
            }
        }
    }

    /**
     * We listen for button clicks (or enter keys) that trigger the display
     * of a screen.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        String title = event.getActionCommand();
        JXPanel p = getPanel();
        if ((title != null) && (p != null)) {

            Screen[] array = getScreens();
            for (int i = 0; i < array.length; i++) {

                if (title.startsWith(array[i].getTitle())) {

                    if (array[i] instanceof ParameterProperty) {

                        ParameterProperty pp = (ParameterProperty) array[i];
                        String s = title.substring(title.indexOf(":") + 1);
                        if (s != null) {

                            pp.setSelectedParameter(s);
                        }
                    }

                    if (array[i] instanceof PhotoTagProperty) {

                        applyPhotoTag((PhotoTagProperty) array[i]);
                    }

                    if (array[i] instanceof RecordingProperty) {

                        applyRecordings((RecordingProperty) array[i]);
                    }

                    if (array[i] instanceof NMSProperty) {

                        applyNMS((NMSProperty) array[i]);
                    }

                    if (array[i] instanceof VideoProperty) {

                        applyVideos((VideoProperty) array[i]);
                    }

                    if (array[i] instanceof UpcomingProperty) {

                        applyUpcomings((UpcomingProperty) array[i]);
                    }

                    if (array[i] instanceof RecordingRuleProperty) {

                        applyRecordingRules((RecordingRuleProperty) array[i]);
                    }

                    // See if it's a screen that just executes a script.
                    // if so do it, otherwise update the current screen.
                    if (array[i] instanceof ExecuteScreen) {

                        ExecuteScreen escreen = (ExecuteScreen) array[i];
                        String s = title.substring(title.indexOf(":") + 1);
                        if (s != null) {

                            escreen.execute(s);
                        }

                    } else {

                        array[i].setDone(false);
                        CardLayout cl = (CardLayout) p.getLayout();
                        int index = title.indexOf(":");
                        if (index != -1) {
                            title = title.substring(0, index);
                        }

                        TextImagePanel tip = getTextImagePanel();

                        if (isEffects()) {

                            TimingTarget tt = PropertySetter.getTarget(tip,
                                "alpha", Float.valueOf(1.0f),
                                Float.valueOf(0.0f));
                            Animator fadeout =
                                new Animator.Builder().setDuration(125,
                                TimeUnit.MILLISECONDS).addTarget(tt).build();
                            fadeout.addTarget(
                                new TripScreenFadeIn(p, title, array[i]));
                            fadeout.start();

                        } else {

                            array[i].setAlpha(1.0f);
                            cl.show(panel, title);
                            setLastActivityMillis(System.currentTimeMillis());
                        }
                        break;
                    }
                }
            }
        }

        setLastActivityMillis(System.currentTimeMillis());
        if (isBlanked()) {
            setBlanked(false);
        }
    }

    private void applyPhotoTag(PhotoTagProperty ptp) {

        if (ptp != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                if (array.length == 1) {

                    Tag root = array[0].getRootTag();
                    if (root != null) {

                        Tag[] tarray = root.toArray();
                        if (tarray != null) {

                            tarray[0].dump();
                            tarray = Arrays.copyOfRange(tarray, 1,
                                tarray.length);
                            ptp.setTags(tarray);

                        } else {

                            ptp.setTags(null);
                        }

                    } else {

                        ptp.setTags(null);
                    }

                    Photo[] parray = array[0].getPhotos();
                    if (parray != null) {

                        transformPhotos(parray);
                        ptp.setPhotos(parray);

                    } else {

                        ptp.setPhotos(null);
                    }

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<Tag> tlist = new ArrayList<Tag>();
                    for (int i = 0; i < array.length; i++) {

                        Tag root = array[i].getRootTag();
                        if (root != null) {

                            Tag[] tarray = root.toArray();
                            if (tarray != null) {

                                tarray = Arrays.copyOfRange(tarray, 1,
                                    tarray.length - 1);
                                Collections.addAll(tlist, tarray);
                            }
                        }
                    }

                    ptp.setTags(tlist.toArray(new Tag[tlist.size()]));

                    ArrayList<Photo> plist = new ArrayList<Photo>();
                    for (int i = 0; i < array.length; i++) {

                        Photo[] picts = array[i].getPhotos();
                        if (picts != null) {

                            Collections.addAll(plist, picts);
                        }
                    }

                    Photo[] parray = plist.toArray(new Photo[plist.size()]);
                    transformPhotos(parray);
                    ptp.setPhotos(parray);
                }
            }
        }
    }

    private void applyRecordings(RecordingProperty rp) {

        if (rp != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                log(DEBUG, "NMS count: " + array.length);
                if (array.length == 1) {

                    Recording[] recs = array[0].getRecordings();
                    transformRecordings(recs);
                    rp.setRecordings(recs);

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<Recording> list = new ArrayList<Recording>();
                    for (int i = 0; i < array.length; i++) {

                        Recording[] recs = array[i].getRecordings();
                        log(DEBUG, "recs from NMS : " + i);
                        if (recs != null) {

                            log(DEBUG, "recs from NMS : " + recs.length);
                            Collections.addAll(list, recs);
                        }
                    }

                    Recording[] recs = list.toArray(new Recording[list.size()]);
                    if (recs != null) {

                        Arrays.sort(recs);
                    }
                    transformRecordings(recs);
                    rp.setRecordings(recs);
                }
            }
        }
    }

    private void applyNMS(NMSProperty np) {

        if (np != null) {

            np.setNMS(getNMS());
        }
    }

    private void applyVideos(VideoProperty vp) {

        if (vp != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                if (array.length == 1) {

                    Video[] vids = array[0].getVideos();
                    transformVideos(vids);
                    vp.setVideos(vids);

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<Video> list = new ArrayList<Video>();
                    for (int i = 0; i < array.length; i++) {

                        Video[] vids = array[i].getVideos();
                        if (vids != null) {

                            Collections.addAll(list, vids);
                        }
                    }

                    Video[] vids = list.toArray(new Video[list.size()]);
                    transformVideos(vids);
                    vp.setVideos(vids);
                }
            }
        }
    }

    private void applyUpcomings(UpcomingProperty up) {

        if (up != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                if (array.length == 1) {

                    up.setUpcomings(array[0].getUpcomings());

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<Upcoming> list = new ArrayList<Upcoming>();
                    for (int i = 0; i < array.length; i++) {

                        Upcoming[] ups = array[i].getUpcomings();
                        if (ups != null) {

                            Collections.addAll(list, ups);
                        }
                    }

                    Upcoming[] upa = list.toArray(new Upcoming[list.size()]);
                    if (upa != null) {

                        Arrays.sort(upa);
                    }
                    up.setUpcomings(upa);
                }
            }
        }
    }

    private void applyRecordingRules(RecordingRuleProperty rrp) {

        if (rrp != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                if (array.length == 1) {

                    rrp.setRecordingRules(array[0].getRecordingRules());

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<RecordingRule> list =
                        new ArrayList<RecordingRule>();
                    for (int i = 0; i < array.length; i++) {

                        RecordingRule[] rrs = array[i].getRecordingRules();
                        if (rrs != null) {

                            Collections.addAll(list, rrs);
                        }
                    }

                    rrp.setRecordingRules(list.toArray(
                        new RecordingRule[list.size()]));
                }
            }
        }
    }

    /**
     * We need to listen for when screens are done so the main
     * UI component is displayed.
     *
     * @param event A given PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        Screen screen = (Screen) event.getSource();
        if (screen.isDone()) {

            // Set back to the main UI.
            JXPanel p = getPanel();
            if (p != null) {

                CardLayout cl = (CardLayout) (p.getLayout());
                TextImagePanel tip = getTextImagePanel();
                if (isEffects()) {

                    TimingTarget tt = PropertySetter.getTarget(tip,
                        "alpha", Float.valueOf(1.0f), Float.valueOf(0.0f));
                    Animator fadeout = new Animator.Builder().setDuration(
                        125, TimeUnit.MILLISECONDS).addTarget(tt).build();
                    fadeout.addTarget(new TripMainFadeIn(p));
                    fadeout.start();

                } else {

                    tip.setAlpha(1.0f);
                    cl.show(p, "main");
                    setLastActivityMillis(System.currentTimeMillis());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void screenUpdate(ScreenEvent event) {

        NMS[] array = getNMS();
        boolean allow = false;

        switch (event.getType()) {

        default:
            break;

        case ScreenEvent.DELETE_RECORDING_ALLOW_RERECORDING:
            allow = true;
            // fall-thru
        case ScreenEvent.DELETE_RECORDING:

            if (array != null) {

                Recording todelete = event.getRecording();
                if (todelete != null) {

                    log(DEBUG, "whence: " + todelete.getHostPort());
                    NMS from = NMSUtil.select(array, todelete.getHostPort());
                    if (from != null) {

                        from.removeRecording(todelete, allow);
                    }

                    if (event.getSource() instanceof RecordingProperty) {

                        RecordingProperty rp =
                            (RecordingProperty) event.getSource();
                        applyRecordings(rp);
                    }
                }
            }
            break;

        case ScreenEvent.STOP_RECORDING:

            if (array != null) {

                Recording tostop = event.getRecording();
                if (tostop != null) {

                    NMS from = NMSUtil.select(array, tostop.getHostPort());
                    if (from != null) {

                        from.stopRecording(tostop);
                    }
                }
            }

            break;

        case ScreenEvent.USER_INPUT:
            setLastActivityMillis(System.currentTimeMillis());
            if (isBlanked()) {
                setBlanked(false);
            }
            break;
        }
    }

    class EscapeAction extends AbstractAction {

        public EscapeAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TextImagePanel tip = getTextImagePanel();
            if (tip != null) {

                if (tip.isPopup()) {

                    tip.deactivatePopup();

                } else {

                    exitAction(true);
                }

            } else {

                exitAction(true);
            }
        }
    }

    private boolean isHideMouse() {
        return (hideMouse);
    }

    private void setHideMouse(boolean b) {

        hideMouse = b;
        if (frame != null) {

            if (hideMouse) {

                frame.getContentPane().setCursor(getNoCursor());

            } else {

                frame.getContentPane().setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private class TripScreenFadeIn extends TimingTargetAdapter {

        private JXPanel panel;
        private String title;
        private Screen screen;

        public TripScreenFadeIn(JXPanel p, String t, Screen s) {

            panel = p;
            title = t;
            screen = s;
        }

        public void end(Animator source) {

            CardLayout cl = (CardLayout) panel.getLayout();
            screen.setAlpha(0.0f);
            cl.show(panel, title);
            setLastActivityMillis(System.currentTimeMillis());

            TimingTarget tt = PropertySetter.getTarget(screen,
                "alpha", Float.valueOf(0.0f), Float.valueOf(1.0f));
            Animator fadein = new Animator.Builder().setDuration(
                125, TimeUnit.MILLISECONDS).addTarget(tt).build();
            fadein.start();
        }

    }

    private class TripMainFadeIn extends TimingTargetAdapter {

        private JXPanel panel;

        public TripMainFadeIn(JXPanel p) {

            panel = p;
        }

        public void end(Animator source) {

            CardLayout cl = (CardLayout) panel.getLayout();
            TextImagePanel p = getTextImagePanel();
            p.setAlpha(0.0f);
            cl.show(panel, "main");
            setLastActivityMillis(System.currentTimeMillis());

            TimingTarget tt = PropertySetter.getTarget(p,
                "alpha", Float.valueOf(0.0f), Float.valueOf(1.0f));
            Animator fadein = new Animator.Builder().setDuration(
                125, TimeUnit.MILLISECONDS).addTarget(tt).build();
            fadein.start();
        }

    }

    class ActivityActionListener implements ActionListener {

        public ActivityActionListener() {
        }

        public void actionPerformed(ActionEvent event) {

            if (isBlankingEnabled()) {

                if (!isBlanked()) {

                    // We do not ever unblank here so no need to do
                    // anything unless the screen is active.
                    long last = getLastActivityMillis();
                    Screen[] all = getScreens();
                    if ((all != null) && (all.length > 0)) {

                        for (int i = 0; i < all.length; i++) {

                            long tmp = all[i].getLastActivityMillis();
                            if (tmp > last) {
                                last = tmp;
                            }
                        }
                    }

                    setLastActivityMillis(last);
                    long max = getMaxInactivityMillis();
                    long now = System.currentTimeMillis();
                    long inactivity = now - last;
                    if (inactivity > max) {

                        setBlanked(true);
                    }
                }
            }
        }
    }

}
