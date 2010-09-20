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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.imagecache.ImageCacheProperty;
import org.jflicks.mvc.View;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.rc.RCProperty;
import org.jflicks.tv.Recording;
import org.jflicks.ui.view.fe.BaseCustomizePanel;

import org.jdesktop.swingx.painter.ImagePainter;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class is a "titled component".
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class Screen extends BaseCustomizePanel implements RCProperty,
    EventHandler, ImageCacheProperty, Screenable, Comparable<Screen> {

    /**
     * The Screen needs a title property.
     */
    public static final String TITLE_PROPERTY = "Screen-Title";

    private ArrayList<ScreenListener> screenList =
        new ArrayList<ScreenListener>();

    private String title;
    private boolean done;
    private ServiceTracker playerServiceTracker;
    private RC rc;
    private ImageCache imageCache;
    private BufferedImage defaultBackgroundImage;
    private BufferedImage currentBackgroundImage;
    private View view;

    /**
     * This base class does the necessary OSGi Event code so we insist
     * extensions implement this method so the Event command can be passed
     * along.
     *
     * @param command A command from the RC service.
     */
    public abstract void commandReceived(String command);

    /**
     * A Screen might need to persist it's "state" so when users revisit
     * they will find things the way they left them.  If it is not important
     * to do so a Screen can chose not to do anything.
     */
    public abstract void save();

    /**
     * Simple empty constructor.
     */
    public Screen() {

        setFocusable(true);
        setDone(true);
        requestFocus();
        EscapeAction escapeAction = new EscapeAction();
        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        getActionMap().put("escape", escapeAction);
    }

    /**
     * Each screen gets access to the View class running the whole show.
     *
     * @return A View instance.
     */
    public View getView() {
        return (view);
    }

    /**
     * Each screen gets access to the View class running the whole show.
     *
     * @param v A View instance.
     */
    public void setView(View v) {
        view = v;
    }

    /**
     * Convenience method for extensions so external images can be used
     * instead of the embedded jar images for their backgrounds.  We look
     * in the theme directory for a file with the given name.
     *
     * @param name File to look for at least as a resource.
     * @return A buffered image.
     */
    public BufferedImage getImageByName(String name) {

        BufferedImage result = null;

        if (name != null) {

            name = name + ".png";

            try {

                // First see if it's in a theme directory...
                File theme = getCurrentThemeDirectory();
                File file = new File(theme, name);
                if (file.exists()) {

                    result = ImageIO.read(file);

                } else {

                    result = ImageIO.read(getClass().getResource(name));
                }

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void restartUI() {

        String t = getTitle();
        if (t != null) {

            t = t.replaceAll(" ", "_");
            setDefaultBackgroundImage(getImageByName(t));
        }

        super.restartUI();
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * The title of the screen.  This title will show up on the main window
     * as a button.
     *
     * @return The title as a String.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * The title of the screen.  This title will show up on the main window
     * as a button.
     *
     * @param s The title as a String.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * Property that signifies the user is done with this Screen.
     *
     * @return True if done.
     */
    public boolean isDone() {
        return (done);
    }

    /**
     * Property that signifies the user is done with this Screen.
     *
     * @param b True if done.
     */
    public void setDone(boolean b) {

        boolean old = done;
        done = b;
        firePropertyChange("Done", old, done);
    }

    /**
     * A screen has a default background image.
     *
     * @return The default background image as a BufferedImage instance.
     */
    public BufferedImage getDefaultBackgroundImage() {
        return (defaultBackgroundImage);
    }

    /**
     * A screen has a default background image.
     *
     * @param bi The default background image as a BufferedImage instance.
     */
    public void setDefaultBackgroundImage(BufferedImage bi) {
        defaultBackgroundImage = bi;

        if (bi != null) {

            ImagePainter p = (ImagePainter) getBackgroundPainter();
            if (p != null) {

                p.setImage(bi);
                repaint();

            } else {

                p = new ImagePainter(bi);
                p.setScaleToFit(true);
                setBackgroundPainter(p);
            }
        }
    }

    /**
     * Ability to change the background image of a screen when ever it
     * is necessary.
     *
     * @return A given BufferedImage instance.
     */
    public BufferedImage getCurrentBackgroundImage() {
        return (currentBackgroundImage);
    }

    /**
     * Ability to change the background image of a screen when ever it
     * is necessary.
     *
     * @param bi A given BufferedImage instance.
     */
    public void setCurrentBackgroundImage(BufferedImage bi) {

        currentBackgroundImage = bi;

        if (bi != null) {

            ImagePainter p = (ImagePainter) getBackgroundPainter();
            if (p != null) {

                p.setImage(bi);
                repaint();

            } else {

                p = new ImagePainter(bi);
                p.setScaleToFit(true);
                setBackgroundPainter(p);
            }
        }
    }

    /**
     * A Screen is instantiated with a ServiceTracker which can find a
     * Player for a particular screen.  This property is populated by a
     * Theme implementation as they have the knowledge of the type of
     * player their screens need to have to do their job.
     *
     * @return A ServiceTracker instance.
     */
    public ServiceTracker getPlayerServiceTracker() {
        return (playerServiceTracker);
    }

    /**
     * A Screen is instantiated with a ServiceTracker which can find a
     * Player for a particular screen.  This property is populated by a
     * Theme implementation as they have the knowledge of the type of
     * player their screens need to have to do their job.
     *
     * @param t A ServiceTracker instance.
     */
    public void setPlayerServiceTracker(ServiceTracker t) {
        playerServiceTracker = t;
    }

    /**
     * Each screen could have a Player that they need to play media for the
     * user.  It gets the player from the ServiceTracker property.
     *
     * @return A Player instance if it exists.
     */
    public Player getPlayer() {

        Player result = null;

        ServiceTracker st = getPlayerServiceTracker();
        if (st != null) {

            result = (Player) st.getService();
        }

        return (result);
    }

    /**
     * The Activator for the FrontEndView keeps us uptodate with the
     * current RC instance.  We don't expect more than one running or
     * needed so we just keep track of one.
     *
     * @return A given RC instance.
     */
    public RC getRC() {
        return (rc);
    }

    /**
     * The Activator for the FrontEndView keeps us uptodate with the
     * current RC instance.  We don't expect more than one running or
     * needed so we just keep track of one.
     *
     * @param r A given RC instance.
     */
    public void setRC(RC r) {
        rc = r;
    }

    /**
     * {@inheritDoc}
     */
    public ImageCache getImageCache() {
        return (imageCache);
    }

    /**
     * {@inheritDoc}
     */
    public void setImageCache(ImageCache ic) {
        imageCache = ic;
    }

    /**
     * {@inheritDoc}
     */
    public void addScreenListener(ScreenListener l) {
        screenList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeScreenListener(ScreenListener l) {
        screenList.remove(l);
    }

    /**
     * Convenience method to fire an event with a certain type and Recording.
     *
     * @param type A given type.
     * @param r A given Recording.
     */
    public void fireScreenEvent(int type, Recording r) {
        processScreenEvent(new ScreenEvent(this, type, r));
    }

    protected synchronized void processScreenEvent(ScreenEvent event) {

        synchronized (screenList) {

            for (int i = 0; i < screenList.size(); i++) {

                ScreenListener l = screenList.get(i);
                l.screenUpdate(event);
            }
        }
    }

    /**
     * The OSGi Event Handler method we need to implement to receive
     * messages from the RC service (or any other event from OSGi.
     *
     * @param event A given Event instance.
     */
    public void handleEvent(Event event) {

        String command = (String) event.getProperty("command");
        if ((command != null) && (!isDone())) {

            commandReceived(command);
        }
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getTitle().hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Screen)) {

            result = false;

        } else {

            Screen scr = (Screen) o;
            String s = getTitle();
            if (s != null) {

                result = s.equals(scr.getTitle());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param s The given Screen instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Screen s) throws ClassCastException {

        int result = 0;
        if (s == null) {

            throw new NullPointerException();
        }

        if (s == this) {

            result = 0;

        } else {

            String title0 = getTitle();
            String title1 = s.getTitle();
            if ((title0 != null) && (title1 != null)) {

                result = title0.compareTo(title1);
            }
        }

        return (result);
    }

    class EscapeAction extends AbstractAction {

        public EscapeAction() {
        }

        public void actionPerformed(ActionEvent e) {

            save();
            setDone(true);
        }
    }

}

