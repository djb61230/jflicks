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
package org.jflicks.ui.view.fe.screen.photo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.mvc.View;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.PhotoUtil;
import org.jflicks.photomanager.Tag;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.MessagePanel;
import org.jflicks.ui.view.fe.PhotoTagProperty;
import org.jflicks.ui.view.fe.TagListPanel;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Photos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PhotoScreen extends PlayerScreen implements PhotoTagProperty,
    PropertyChangeListener {

    private Tag[] tags;
    private Photo[] photos;
    private TagListPanel allTagListPanel;
    private TagListPanel tagListPanel;
    private MessagePanel messagePanel;
    private Photo[] anyPhotos;
    private Photo[] allPhotos;

    /**
     * Simple empty constructor.
     */
    public PhotoScreen() {

        setTitle("Photos");

        BufferedImage bi = getImageByName("Photos");
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

        RewindAction rewindAction = new RewindAction();
        map.put(KeyStroke.getKeyStroke("R"), "r");
        getActionMap().put("r", rewindAction);

        ForwardAction forwardAction = new ForwardAction();
        map.put(KeyStroke.getKeyStroke("F"), "f");
        getActionMap().put("f", forwardAction);

        JButton b = getDeleteAllowButton();
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

        b = getBeginningButton();
        if (b != null) {

            b.setText("Play using ANY Tags");
        }

        b = getBookmarkButton();
        if (b != null) {

            b.setText("Play using ALL Tags");
        }

    }

    /**
     * {@inheritDoc}
     */
    public Tag[] getTags() {

        Tag[] result = null;

        if (tags != null) {

            result = Arrays.copyOf(tags, tags.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setTags(Tag[] array) {

        if (array != null) {
            tags = Arrays.copyOf(array, array.length);
        } else {
            tags = null;
        }

        TagListPanel p = getAllTagListPanel();
        if (p != null) {

            p.setTags(tags);
            p.setSelectedIndex(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Photo[] getPhotos() {

        Photo[] result = null;

        if (photos != null) {

            result = Arrays.copyOf(photos, photos.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setPhotos(Photo[] array) {

        if (array != null) {
            photos = Arrays.copyOf(array, array.length);
        } else {
            photos = null;
        }
    }

    private TagListPanel getAllTagListPanel() {
        return (allTagListPanel);
    }

    private void setAllTagListPanel(TagListPanel p) {
        allTagListPanel = p;
    }

    private TagListPanel getTagListPanel() {
        return (tagListPanel);
    }

    private void setTagListPanel(TagListPanel p) {
        tagListPanel = p;
    }

    private MessagePanel getMessagePanel() {
        return (messagePanel);
    }

    private void setMessagePanel(MessagePanel p) {
        messagePanel = p;
    }

    private Photo[] getAllPhotos() {
        return (allPhotos);
    }

    private void setAllPhotos(Photo[] array) {
        allPhotos = array;
    }

    private Photo[] getAnyPhotos() {
        return (anyPhotos);
    }

    private void setAnyPhotos(Photo[] array) {
        anyPhotos = array;
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            float alpha = (float) getPanelAlpha();

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            int wspan = (int) (width * 0.03);
            int listwidth = (width - (3 * wspan)) / 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) (height - (6 * hspan));

            int statuswidth = (int) (width - (2 * wspan));
            int statusheight = (int) (3 * hspan);

            TagListPanel atlp = new TagListPanel();
            atlp.setAlpha(alpha);

            atlp.addPropertyChangeListener("SelectedTag", this);
            atlp.setControl(true);

            setAllTagListPanel(atlp);

            TagListPanel tlp = new TagListPanel();
            tlp.setAlpha(alpha);

            tlp.addPropertyChangeListener("SelectedTag", this);
            setTagListPanel(tlp);

            MessagePanel mp = new MessagePanel();
            mp.setAlpha(alpha);
            mp.setMessage("Select Tag(s) to display Photo(s) in a Slideshow");
            setMessagePanel(mp);

            atlp.setBounds(wspan, hspan, listwidth, listheight);
            tlp.setBounds(wspan + wspan + listwidth, hspan, listwidth,
                listheight);
            mp.setBounds(wspan, hspan + hspan + listheight, statuswidth,
                statusheight);

            pane.add(atlp, Integer.valueOf(100));
            pane.add(tlp, Integer.valueOf(100));
            pane.add(mp, Integer.valueOf(100));

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));

            // Create our blank panel.
            JXPanel blank = new JXPanel();
            MattePainter blankp = new MattePainter(Color.BLACK);
            blank.setBackgroundPainter(blankp);
            blank.setBounds(0, 0, width, height);
            setBlankPanel(blank);
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
     * We listen for property change events from the panels that deal
     * with selecting a tag.
     *
     * @param event A given PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            String pname = event.getPropertyName();

            if (pname.equals("Completed")) {

                // If we get this property update, then it means the slideshow
                // finished playing on it's own.
                Boolean bobj = (Boolean) event.getNewValue();
                if (bobj.booleanValue()) {

                    getPlayer().removePropertyChangeListener(this);
                    requestFocus();
                }
            }
        }
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
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        removeBlankPanel();
        Player p = getPlayer();
        if (p != null) {

            p.stop();
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

    private void updateStatus() {

        // Some change has occurred so we update the message displayed.
        TagListPanel p = getTagListPanel();
        MessagePanel mp = getMessagePanel();
        if ((p != null) && (mp != null)) {

            Tag[] tarray = p.getTags();
            if (tarray != null) {

                ArrayList<Tag> subtags = new ArrayList<Tag>();
                for (int i = 0; i < tarray.length; i++) {

                    if (tarray[i].isLeaf()) {

                        subtags.add(tarray[i]);
                    }
                }

                if (subtags.size() > 0) {

                    tarray = subtags.toArray(new Tag[subtags.size()]);

                    Photo[] parray = getPhotos();
                    if (parray != null) {

                        Photo[] anysub = PhotoUtil.getTaggedPhotos(
                            PhotoUtil.ANY, parray, tarray);
                        setAnyPhotos(anysub);
                        Photo[] allsub = PhotoUtil.getTaggedPhotos(
                            PhotoUtil.ALL, parray, tarray);
                        setAllPhotos(allsub);

                        StringBuilder sb = new StringBuilder();
                        if (anysub != null) {

                            sb.append("Using ANY, " + anysub.length + " photos"
                                + " will display.  ");
                        }

                        if (allsub != null) {

                            sb.append("Using ALL, " + allsub.length + " photos"
                                + " will display.");
                        }

                        mp.setMessage(sb.toString());

                    } else {

                        mp.setMessage("No Photo(s) to show!");
                    }
                }

            } else {

                mp.setMessage("No Tag(s) selected so no photos to show!");
            }
        }
    }

    private void writePlaylist(Photo[] array) {

        if ((array != null) && (array.length > 0)) {

            try {

                File dir = new File(".");
                File playlist = new File(dir, "list.txt");
                if (playlist.exists()) {

                    if (!playlist.delete()) {

                        log(WARNING, "couldn't delete olf file...");
                    }
                }

                FileWriter fw = new FileWriter(playlist);
                PrintWriter pw = new PrintWriter(fw);

                for (int i = 0; i < array.length; i++) {

                    pw.println(array[i].getPath());
                }

                pw.close();
                fw.close();

            } catch (IOException ex) {

                log(WARNING, "BLOW_UP: " + ex.getMessage());
            }
        }
    }

    /**
     * We need to listen for events from the "play popup dialog".
     *
     * @param event A given event.
     */
    public void actionPerformed(ActionEvent event) {

        Player p = getPlayer();
        if ((p != null) && (!p.isPlaying())) {

            View v = getView();
            if (v instanceof FrontEndView) {

                FrontEndView fev = (FrontEndView) v;
                p.setRectangle(fev.getPosition());
            }

            if (event.getSource() == getBeginningButton()) {

                Photo[] any = getAnyPhotos();
                if (any != null) {

                    writePlaylist(any);
                    p.addPropertyChangeListener("Completed", this);
                    p.setFrame(Util.findFrame(this));
                    addBlankPanel();
                    p.play("list.txt");
                }

            } else if (event.getSource() == getBookmarkButton()) {

                Photo[] all = getAllPhotos();
                if (all != null) {

                    writePlaylist(all);
                    p.addPropertyChangeListener("Completed", this);
                    p.setFrame(Util.findFrame(this));
                    addBlankPanel();
                    p.play("list.txt");
                }
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                if (!atlp.isControl()) {

                    atlp.setControl(true);

                } else {

                    atlp.toggle();
                }
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                tlp.setControl(false);
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                atlp.setControl(false);
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                if (!tlp.isControl()) {

                    tlp.setControl(true);

                } else {

                    tlp.toggle();
                }
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                if (atlp.isControl()) {
                    atlp.moveUp();
                }
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                if (tlp.isControl()) {
                    tlp.moveUp();
                }
            }
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                if (atlp.isControl()) {
                    atlp.moveDown();
                }
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                if (tlp.isControl()) {
                    tlp.moveDown();
                }
            }
        }
    }

    class PageUpAction extends AbstractAction {

        public PageUpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                if (atlp.isControl()) {
                    atlp.movePageUp();
                }
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                if (tlp.isControl()) {
                    tlp.movePageUp();
                }
            }
        }
    }

    class PageDownAction extends AbstractAction {

        public PageDownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel atlp = getAllTagListPanel();
            if (atlp != null) {

                if (atlp.isControl()) {
                    atlp.movePageDown();
                }
            }

            TagListPanel tlp = getTagListPanel();
            if (tlp != null) {

                if (tlp.isControl()) {
                    tlp.movePageDown();
                }
            }
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            Dialog.showButtonPanel(Util.findFrame(getLayeredPane()),
                getPlayButtonPanel());
        }
    }

    class RewindAction extends AbstractAction {

        public RewindAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel all = getAllTagListPanel();
            TagListPanel p = getTagListPanel();
            if ((all != null) && (p != null)) {

                if (p.isControl()) {

                    // Time to remove a tag or (tags) from the selected list.
                    Tag current = p.getSelectedTag();
                    if (current != null) {

                        // We want to actually copy parents too so the UI
                        // "structure" stays the same.  However we don't
                        // remove parents unless they have no other kids
                        // in the list.  So we make a special parent list
                        // first.
                        ArrayList<Tag> l = new ArrayList<Tag>();
                        l.add(current);

                        ArrayList<Tag> pl = new ArrayList<Tag>();

                        Tag parent = current.getParent();
                        while ((parent != null) && (!parent.isRoot())) {

                            pl.add(parent);
                            parent = parent.getParent();
                        }

                        // Now we add any kids so they are removed.
                        if (!current.isLeaf()) {

                            Tag[] kids = current.getChildren();
                            for (int i = 0; i < kids.length; i++) {

                                Tag[] tarray = kids[i].toArray();
                                if (tarray != null) {

                                    for (int j = 0; j < tarray.length; j++) {

                                        l.add(tarray[j]);
                                    }
                                }
                            }
                        }

                        // At this point we should have the current and
                        // it's children ready to remove.
                        p.removeTags(l.toArray(new Tag[l.size()]));

                        // Last job is to remove any parents that no
                        // longer have children in the selected list.
                        for (int i = 0; i < pl.size(); i++) {

                            boolean shouldRemove = true;
                            Tag t = pl.get(i);
                            Tag[] kids = t.getChildren();
                            if (kids != null) {

                                for (int j = 0; j < kids.length; j++) {

                                    if (p.containsTag(kids[j])) {

                                        // We keep it.
                                        shouldRemove = false;
                                        break;
                                    }
                                }

                                if (shouldRemove) {

                                    p.removeTag(t);
                                }
                            }
                        }
                    }
                }
            }

            updateStatus();
        }

    }

    class ForwardAction extends AbstractAction {

        public ForwardAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TagListPanel all = getAllTagListPanel();
            TagListPanel p = getTagListPanel();
            if ((all != null) && (p != null)) {

                if (all.isControl()) {

                    // Time to copy a tag or (tags) to the selected list.
                    Tag current = all.getSelectedTag();
                    if (current != null) {

                        // We want to actually copy parents too so the UI
                        // "structure" stays the same.
                        ArrayList<Tag> l = new ArrayList<Tag>();
                        l.add(current);
                        Tag parent = current.getParent();
                        while ((parent != null) && (!parent.isRoot())) {

                            l.add(parent);
                            parent = parent.getParent();
                        }

                        // Now we add any kids.
                        if (!current.isLeaf()) {

                            Tag[] kids = current.getChildren();
                            for (int i = 0; i < kids.length; i++) {

                                Tag[] tarray = kids[i].toArray();
                                if (tarray != null) {

                                    for (int j = 0; j < tarray.length; j++) {

                                        l.add(tarray[j]);
                                    }
                                }
                            }
                        }

                        // At this point we should have everything.  The
                        // TagListPanel will handle any duplications.
                        p.addTags(l.toArray(new Tag[l.size()]));
                    }
                }
            }

            updateStatus();
        }

    }

}

