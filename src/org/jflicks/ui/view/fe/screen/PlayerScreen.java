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
package org.jflicks.ui.view.fe.screen;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.fe.ButtonPanel;
import org.jflicks.ui.view.fe.FrontEndView;

/**
 * This abstract class supports playing a video in a front end UI on a TV,
 * while keeping track of bookmarks.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class PlayerScreen extends Screen implements ActionListener {

    /**
     * A constant to define a "Play" menu item.
     */
    public static final String PLAY = "Play";

    /**
     * A constant to define a "Play from Bookmark" menu item.
     */
    public static final String PLAY_FROM_BOOKMARK = "Play from Bookmark";

    /**
     * A constant to define a "Play using ANY Tags" menu item.
     */
    public static final String PLAY_USING_ANY_TAGS = "Play using ANY Tags";

    /**
     * A constant to define a "Play using ALL Tags" menu item.
     */
    public static final String PLAY_USING_ALL_TAGS = "Play using ALL Tags";

    /**
     * A constant to define a "Stop Recording" menu item.
     */
    public static final String STOP_RECORDING = "Stop Recording";

    /**
     * A constant to define a "Delete" menu item.
     */
    public static final String DELETE = "Delete";

    /**
     * A constant to define a "Delete (Allow Re-Recording)" menu item.
     */
    public static final String DELETE_ALLOW_RERECORDING =
        "Delete (Allow Re-Recording)";

    /**
     * A constant to define a "Cancel" menu item.
     */
    public static final String CANCEL = "Cancel";

    private HashMap<String, Bookmark> bookmarkHashMap;
    private File bookmarkFile;
    private ButtonPanel playButtonPanel;
    private JPanel blankPanel;
    private boolean popupEnabled;

    /**
     * Extensions need to display some info banner over the video.
     */
    public abstract void info();

    /**
     * Extensions might need to display some guide information or control.
     */
    public abstract void guide();

    /**
     * If anything needs to be "cleaned up" after the stopping of video,
     * extensions should do it here.
     */
    public abstract void close();

    /**
     * Extensions should perform a rewind.
     */
    public abstract void rewind();

    /**
     * Extensions should perform a forward.
     */
    public abstract void forward();

    /**
     * Extensions should perform a skip forward.
     */
    public abstract void skipforward();

    /**
     * Extensions should perform a skip backward.
     */
    public abstract void skipbackward();

    /**
     * Extensions can do what they want with an up command.
     */
    public abstract void up();

    /**
     * Extensions can do what they want with a down command.
     */
    public abstract void down();

    /**
     * Extensions can do what they want with a left command.
     */
    public abstract void left();

    /**
     * Extensions can do what they want with a right command.
     */
    public abstract void right();

    /**
     * Extensions can do what they want with an enter command.
     */
    public abstract void enter();

    /**
     * Create a Bookmark instance.
     *
     * @return A Bookmark instance.
     */
    public abstract Bookmark createBookmark();

    /**
     * What ID should be used for the current bookmark.
     *
     * @return An Id as a String.
     */
    public abstract String getBookmarkId();

    /**
     * Simple empty constructor.
     */
    public PlayerScreen() {

        setBookmarkHashMap(new HashMap<String, Bookmark>());
    }

    protected File getBookmarkFile() {
        return (bookmarkFile);
    }

    protected void setBookmarkFile(File f) {
        bookmarkFile = f;
    }

    private HashMap<String, Bookmark> getBookmarkHashMap() {
        return (bookmarkHashMap);
    }

    private void setBookmarkHashMap(HashMap<String, Bookmark> m) {
        bookmarkHashMap = m;
    }

    protected void putBookmark(String id, Bookmark b) {

        HashMap<String, Bookmark> m = getBookmarkHashMap();
        if ((m != null) && (id != null) && (b != null)) {

            m.put(id, b);
        }
    }

    protected void deleteBookmark(String id) {

        HashMap<String, Bookmark> m = getBookmarkHashMap();
        if ((m != null) && (id != null)) {

            m.remove(id);
        }
    }

    protected Bookmark getBookmark(String id) {

        Bookmark result = null;

        HashMap<String, Bookmark> m = getBookmarkHashMap();
        if ((m != null) && (id != null)) {

            result = m.get(id);
        }

        return (result);
    }

    protected void saveBookmark(Player p) {

        Bookmark b = createBookmark();
        if (b != null) {

            String id = getBookmarkId();
            if (id != null) {

                putBookmark(id, b);
            }
        }
    }

    protected boolean hasBookmark(String id) {
        return (getBookmark(id) != null);
    }

    /**
     * A panel that will be drawn over the current UI to "blank" it out.
     *
     * @return A JPanel instance.
     */
    public JPanel getBlankPanel() {
        return (blankPanel);
    }

    /**
     * A panel that will be drawn over the current UI to "blank" it out.
     *
     * @param p A JPanel instance.
     */
    public void setBlankPanel(JPanel p) {
        blankPanel = p;
    }

    /**
     * When launching a Player it is handy to "blank out" the UI in case the
     * player does not fully cover the screen.  This will use the BlankPanel
     * property if it exists.
     */
    public void addBlankPanel() {

        JPanel p = getBlankPanel();
        JLayeredPane pane = getLayeredPane();
        if ((p != null) && (pane != null)) {

            pane.add(p, Integer.valueOf(190));
            pane.repaint();
        }
    }

    /**
     * When launching a Player it is handy to "blank out" the UI in case the
     * player does not fully cover the screen.  This will use the BlankPanel
     * property if it exists.
     */
    public void removeBlankPanel() {

        log(DEBUG, "removeBlankPanel now");
        JPanel p = getBlankPanel();
        JLayeredPane pane = getLayeredPane();
        if ((p != null) && (pane != null)) {

            pane.remove(p);
            pane.repaint();
        }
    }

    @SuppressWarnings("unchecked")
    protected void load() {

        try {

            HashMap<String, Bookmark> m = null;
            File file = getBookmarkFile();
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file));
            Object obj = ois.readObject();
            if (obj instanceof HashMap<?, ?>) {
                m = (HashMap<String, Bookmark>) obj;
            }
            ois.close();

            if (m != null) {

                setBookmarkHashMap(m);
            }

        } catch (ClassNotFoundException ex) {

            log(WARNING, "Error loading bookmarks:" + ex.getMessage());

        } catch (IOException ex) {

            log(WARNING, "Error loading bookmarks:" + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save() {

        HashMap<String, Bookmark> l = getBookmarkHashMap();
        if (l != null) {

            try {

                File file = getBookmarkFile();
                if (file != null) {

                    ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(file));
                    oos.writeObject(l);
                    oos.close();
                }

            } catch (IOException ex) {

                log(WARNING, "Error saving bookmarks:" + ex.getMessage());
            }
        }
    }

    protected ButtonPanel getPlayButtonPanel() {
        return (playButtonPanel);
    }

    private void setPlayButtonPanel(ButtonPanel p) {
        playButtonPanel = p;
    }

    protected boolean isPopupEnabled() {
        return (popupEnabled);
    }

    protected void setPopupEnabled(boolean b) {
        popupEnabled = b;
    }

    protected void popup(String[] choices) {

        JLayeredPane pane = getLayeredPane();
        if ((pane != null) && (choices != null)) {

            Dimension d = pane.getSize();
            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            // See if we have an image as a backgraound...
            BufferedImage bi = null;
            FrontEndView fe = (FrontEndView) getView();
            if (fe != null) {

                bi = fe.getLogoImage();
            }

            ButtonPanel bp = new ButtonPanel();
            bp.addActionListener(this);
            bp.setButtons(choices);
            bp.setBufferedImage(bi);
            setPlayButtonPanel(bp);

            d = bp.getPreferredSize();
            int bpwidth = (int) d.getWidth();
            int bpheight = (int) d.getHeight();
            int bpx = (int) ((width - bpwidth) / 2);
            int bpy = (int) ((height - bpheight) / 2);
            bp.setBounds(bpx, bpy, bpwidth, bpheight);

            setPopupEnabled(true);
            pane.add(bp, Integer.valueOf(300));
            bp.requestFocus();
            bp.setControl(true);
            bp.setButtons(choices);
        }
    }

    protected void unpopup() {

        setPopupEnabled(false);
        JLayeredPane pane = getLayeredPane();
        ButtonPanel bp = getPlayButtonPanel();
        if ((pane != null) && (bp != null)) {

            bp.removeActionListener(this);
            setPlayButtonPanel(null);
            pane.remove(bp);
            pane.repaint();
        }
    }

    /**
     * Screens that use a Player sometimes need to turn keyboard control
     * on or off - this is simulated keystrokes via our RC instance.
     *
     * @param b True if keyboard control is needed.
     */
    public void controlKeyboard(boolean b) {

        RC remote = getRC();
        if (remote != null) {

            remote.setKeyboardControl(b);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void commandReceived(String s) {

        Player p = getPlayer();
        if ((s != null) && (p != null) && (!isDone())) {

            if (p.isPlaying()) {

                // Then we pay attention to remote events...
                log(DEBUG, getClass().getName() + " commandReceived: " + s);
                if (s.equals(RC.ESCAPE_COMMAND)) {

                    saveBookmark(p);
                    p.stop();
                    close();
                    requestFocus();

                } else if (s.equals(RC.PAUSE_COMMAND)) {

                    p.pause(!p.isPaused());

                } else if (s.equals(RC.INFO_COMMAND)) {

                    info();

                } else if (s.equals(RC.GUIDE_COMMAND)) {

                    guide();

                } else if (s.equals(RC.REWIND_COMMAND)) {

                    rewind();

                } else if (s.equals(RC.FORWARD_COMMAND)) {

                    forward();

                } else if (s.equals(RC.LEFT_COMMAND)) {

                    left();

                } else if (s.equals(RC.RIGHT_COMMAND)) {

                    right();

                } else if (s.equals(RC.SKIPBACKWARD_COMMAND)) {

                    skipbackward();

                } else if (s.equals(RC.SKIPFORWARD_COMMAND)) {

                    skipforward();

                } else if (s.equals(RC.UP_COMMAND)) {

                    up();

                } else if (s.equals(RC.DOWN_COMMAND)) {

                    down();

                } else if (s.equals(RC.ENTER_COMMAND)) {

                    enter();

                } else if (s.equals(RC.AUDIOSYNC_PLUS_COMMAND)) {

                    p.audiosync(0.1);

                } else if (s.equals(RC.AUDIOSYNC_MINUS_COMMAND)) {

                    p.audiosync(-0.1);
                }
            }
        }
    }

}

