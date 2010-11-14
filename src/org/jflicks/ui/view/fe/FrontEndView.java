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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
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
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.Video;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.Tag;
import org.jflicks.rc.RC;
import org.jflicks.rc.RCProperty;
import org.jflicks.tv.Recording;
import org.jflicks.tv.Upcoming;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.fe.screen.ExecuteScreen;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.ui.view.fe.screen.ScreenEvent;
import org.jflicks.ui.view.fe.screen.ScreenListener;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

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

    /**
     * Default constructor.
     */
    public FrontEndView() {

        setScreenList(new ArrayList<Screen>());
        setPathCount(-1);
        setFromPathList(new ArrayList<String>());
        setToPathList(new ArrayList<String>());
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

    /**
     * {@inheritDoc}
     */
    public void modelPropertyChange(PropertyChangeEvent event) {

        String name = event.getPropertyName();
        if (name != null) {

            if (name.equals("NMS")) {

                NMS[] array = (NMS[]) event.getNewValue();
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

        return (new Rectangle(x, y, width, height));
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

            // For good measure lets warp the mouse to the bottom right.
            try {

                Robot robot = new Robot();
                robot.mouseMove(x + width, (y + height) / 2);

            } catch (AWTException event) {

                System.out.println("failed to warp mouse...");
            }

            frame = new JXFrame();
            frame.setTitle("Herm Schmiget");
            frame.setUndecorated(true);
            frame.setBounds(x, y, width, height);

            JXPanel cards = new JXPanel(new CardLayout());
            setPanel(cards);

            // This assumes we get all screens before we get here to
            // build our Frame.  Probably a bad assumption but this is
            // what we will do for now...
            Screen[] array = getScreens();
            if (array != null) {

                ArrayList<TextImage> list = new ArrayList<TextImage>();
                for (int i = 0; i < array.length; i++) {

                    array[i].setDone(true);
                    array[i].addScreenListener(this);
                    array[i].addPropertyChangeListener("Done", this);
                    String title = array[i].getTitle();
                    cards.add(title, array[i]);
                    TextImage ti = new TextImage(title,
                        array[i].getDefaultBackgroundImage());
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

                        System.out.println("Now getting parameter - screen.");
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

    private BufferedImage getLogoImage() {

        BufferedImage result = null;

        try {

            result = ImageIO.read(getClass().getResource("logo.png"));

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
                        cl.show(p, title);
                        break;
                    }
                }
            }
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

                if (array.length == 1) {

                    Recording[] recs = array[0].getRecordings();
                    transformRecordings(recs);
                    rp.setRecordings(recs);

                } else {

                    // We have to merge from multiple NMS.
                    ArrayList<Recording> list = new ArrayList<Recording>();
                    for (int i = 0; i < array.length; i++) {

                        Recording[] recs = array[i].getRecordings();
                        if (recs != null) {

                            Collections.addAll(list, recs);
                        }
                    }

                    Recording[] recs = list.toArray(new Recording[list.size()]);
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

                    up.setUpcomings(list.toArray(new Upcoming[list.size()]));
                }
            }
        }
    }

    private void focus(String s) {

        TextImagePanel p = getTextImagePanel();
        if ((p != null) && (s != null)) {

            /*
            JButton[] array = p.getButtons();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (s.equals(array[i].getText())) {

                        array[i].requestFocus();
                    }
                }
            }
            */
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
                cl.show(p, "main");
                focus(screen.getTitle());
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

                    System.out.println("whence: " + todelete.getHostPort());
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

                    exitAction();
                }

            } else {

                exitAction();
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

}
