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

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.fe.ButtonPanel;

/**
 * This abstract class supports playing a video in a front end UI on a TV,
 * while keeping track of bookmarks.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class PlayerScreen extends Screen implements ActionListener {

    private HashMap<String, Bookmark> bookmarkHashMap;
    private File bookmarkFile;
    private ButtonPanel playButtonPanel;
    private JButton beginningButton;
    private JButton bookmarkButton;
    private JButton stopRecordingButton;
    private JButton deleteButton;
    private JButton deleteAllowButton;
    private JButton cancelButton;
    private JPanel blankPanel;

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

        // We need a ButtonPanel when the user wishes to play
        JButton begin = new JButton("Play");
        setBeginningButton(begin);

        JButton book = new JButton("Play from Bookmark");
        setBookmarkButton(book);

        JButton stop = new JButton("Stop Recording");
        setStopRecordingButton(stop);

        JButton del = new JButton("Delete");
        setDeleteButton(del);

        JButton delAllow = new JButton("Delete (Allow Re-Recording)");
        setDeleteAllowButton(delAllow);

        JButton can = new JButton("Cancel");
        setCancelButton(can);

        JButton[] buts = {
            begin, book, stop, del, delAllow, can
        };

        ButtonPanel bp = new ButtonPanel(buts);
        bp.addActionListener(this);
        setPlayButtonPanel(bp);
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

        System.out.println("removeBlankPanel now");
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

            System.out.println("Error loading bookmarks:" + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("Error loading bookmarks:" + ex.getMessage());
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

                System.out.println("Error saving bookmarks:" + ex.getMessage());
            }
        }
    }

    protected ButtonPanel getPlayButtonPanel() {
        return (playButtonPanel);
    }

    private void setPlayButtonPanel(ButtonPanel p) {
        playButtonPanel = p;
    }

    protected JButton getBeginningButton() {
        return (beginningButton);
    }

    private void setBeginningButton(JButton b) {
        beginningButton = b;
    }

    protected JButton getBookmarkButton() {
        return (bookmarkButton);
    }

    private void setBookmarkButton(JButton b) {
        bookmarkButton = b;
    }

    protected JButton getStopRecordingButton() {
        return (stopRecordingButton);
    }

    private void setStopRecordingButton(JButton b) {
        stopRecordingButton = b;
    }

    protected JButton getDeleteButton() {
        return (deleteButton);
    }

    private void setDeleteButton(JButton b) {
        deleteButton = b;
    }

    protected JButton getDeleteAllowButton() {
        return (deleteAllowButton);
    }

    private void setDeleteAllowButton(JButton b) {
        deleteAllowButton = b;
    }

    protected JButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JButton b) {
        cancelButton = b;
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
                System.out.println(getClass().getName() + " commandReceived: "
                    + s);
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

