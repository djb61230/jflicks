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
package org.jflicks.rc.lirc;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import org.lirc.LIRCException;
import org.lirc.util.IRActionListener;
import org.lirc.util.SimpleLIRCClient;

import org.jflicks.rc.Display;
import org.jflicks.rc.RC;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A job that handles all lirc actions.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LircRCJob extends AbstractJob implements IRActionListener,
    PropertyChangeListener {

    private String configuration;
    private SimpleLIRCClient simpleLIRCClient;
    private Robot robot;
    private Display display;
    private BundleContext bundleContext;
    private ServiceTracker eventServiceTracker;
    private LircRC lircRC;

    /**
     * Simple contructor with a BundleContext supplied.
     *
     * @param bc A given BundleContext.
     * @param rc The RC instance we get user settings from.
     */
    public LircRCJob(BundleContext bc, LircRC rc) {

        setDisplay(new Display());
        setSleepTime(30000);
        setBundleContext(bc);
        setLircRC(rc);
        setConfiguration("conf/LircJob.lircrc");
        try {

            setRobot(new Robot());
            ServiceTracker est =
                new ServiceTracker(bc, EventAdmin.class.getName(), null);
            setEventServiceTracker(est);

        } catch (AWTException ex) {

            throw new RuntimeException(ex);
        }
    }

    /**
     * Simple contructor with a BundleContext and configuration file
     * supplied.
     *
     * @param bc A given BundleContext.
     * @param rc The RC instance we get user settings from.
     * @param configuration A lirc configuration file.
     */
    public LircRCJob(BundleContext bc, LircRC rc, String configuration) {

        this(bc, rc);
        setConfiguration(configuration);
    }

    private LircRC getLircRC() {
        return (lircRC);
    }

    private void setLircRC(LircRC rc) {
        lircRC = rc;

        if (rc != null) {
            rc.addPropertyChangeListener("MouseControl", this);
        }
    }

    private boolean isMouseControl() {

        boolean result = false;

        LircRC rc = getLircRC();
        if (rc != null) {

            result = rc.isMouseControl();
        }

        return (result);
    }

    private boolean isKeyboardControl() {

        boolean result = false;

        LircRC rc = getLircRC();
        if (rc != null) {

            result = rc.isKeyboardControl();
        }

        return (result);
    }

    private boolean isEventControl() {

        boolean result = false;

        LircRC rc = getLircRC();
        if (rc != null) {

            result = rc.isEventControl();
        }

        return (result);
    }

    private Display getDisplay() {
        return (display);
    }

    private void setDisplay(Display d) {
        display = d;
    }

    private ServiceTracker getEventServiceTracker() {
        return (eventServiceTracker);
    }

    private void setEventServiceTracker(ServiceTracker est) {
        eventServiceTracker = est;
    }

    private BundleContext getBundleContext() {
        return (bundleContext);
    }

    private void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    /**
     * The job needs a configuration file to configure lirc properly.  It
     * defaults to conf/LircJob.lircrc.
     *
     * @return The configuration as a string.
     */
    public String getConfiguration() {
        return (configuration);
    }

    /**
     * The job needs a configuration file to configure lirc properly.  It
     * defaults to conf/LircJob.lircrc.
     *
     * @param s The configuration as a string.
     */
    public void setConfiguration(String s) {
        configuration = s;
    }

    private SimpleLIRCClient getSimpleLIRCClient() {
        return (simpleLIRCClient);
    }

    private void setSimpleLIRCClient(SimpleLIRCClient c) {
        simpleLIRCClient = c;
    }

    private Robot getRobot() {
        return (robot);
    }

    private void setRobot(Robot r) {
        robot = r;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        ServiceTracker st = getEventServiceTracker();
        if (st != null) {
            st.open();
        }

        String path = getConfiguration();
        if (path != null) {

            try {

                File f = new File(path);
                if ((f.exists()) && (f.isFile())) {

                    SimpleLIRCClient c = new SimpleLIRCClient(path);
                    c.addIRActionListener(this);
                    setSimpleLIRCClient(c);
                    setTerminate(false);

                } else {

                    URL url = getClass().getResource("LircJob.lircrc");
                    if (url != null) {

                        InputStreamReader isr = new InputStreamReader(
                            url.openStream());
                        SimpleLIRCClient c = new SimpleLIRCClient(null, isr);
                        c.addIRActionListener(this);
                        setSimpleLIRCClient(c);
                        setTerminate(false);
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex);
            } catch (LIRCException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        ServiceTracker st = getEventServiceTracker();
        if (st != null) {
            st.close();
        }

        SimpleLIRCClient c = getSimpleLIRCClient();
        if (c != null) {

            c.stopListening();
            setSimpleLIRCClient(null);
        }

        setTerminate(true);
    }

    private void keyboardMoveUp() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_UP);
            r.keyRelease(KeyEvent.VK_UP);
        }
    }

    private void keyboardMoveDown() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_DOWN);
            r.keyRelease(KeyEvent.VK_DOWN);
        }
    }

    private void keyboardMoveLeft() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_LEFT);
            r.keyRelease(KeyEvent.VK_LEFT);
        }
    }

    private void keyboardMoveRight() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_RIGHT);
            r.keyRelease(KeyEvent.VK_RIGHT);
        }
    }

    private void keyboardEnter() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        }
    }

    private void keyboardEscape() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_ESCAPE);
            r.keyRelease(KeyEvent.VK_ESCAPE);
        }
    }

    private void keyboardPause() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_P);
            r.keyRelease(KeyEvent.VK_P);
        }
    }

    private void keyboardInfo() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_I);
            r.keyRelease(KeyEvent.VK_I);
        }
    }

    private void keyboardGuide() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_G);
            r.keyRelease(KeyEvent.VK_G);
        }
    }

    private void keyboardPageUp() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_PAGE_UP);
            r.keyRelease(KeyEvent.VK_PAGE_UP);
        }
    }

    private void keyboardPageDown() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
        }
    }

    private void keyboardRewind() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_R);
            r.keyRelease(KeyEvent.VK_R);
        }
    }

    private void keyboardForward() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_F);
            r.keyRelease(KeyEvent.VK_F);
        }
    }

    private void keyboardSkipBackward() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_Z);
            r.keyRelease(KeyEvent.VK_Z);
        }
    }

    private void keyboardSkipForward() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_X);
            r.keyRelease(KeyEvent.VK_X);
        }
    }

    private void mouseMoveUp() {

        Robot r = getRobot();
        Display d = getDisplay();
        if ((r != null) && (d != null)) {

            Point p = d.getUp();
            if (p != null) {

                r.mouseMove((int) p.getX(), (int) p.getY());
            }
        }
    }

    private void mouseMoveDown() {

        Display d = getDisplay();
        Robot r = getRobot();
        if ((d != null) && (r != null)) {

            Point p = d.getDown();
            if (p != null) {

                r.mouseMove((int) p.getX(), (int) p.getY());
            }
        }
    }

    private void mouseMoveLeft() {

        Display d = getDisplay();
        Robot r = getRobot();
        if ((d != null) && (r != null)) {

            Point p = d.getLeft();
            if (p != null) {

                r.mouseMove((int) p.getX(), (int) p.getY());
            }
        }
    }

    private void mouseMoveRight() {

        Display d = getDisplay();
        Robot r = getRobot();
        if ((d != null) && (r != null)) {

            Point p = d.getRight();
            if (p != null) {

                r.mouseMove((int) p.getX(), (int) p.getY());
            }
        }
    }

    private void mouseEnter() {

        Robot r = getRobot();
        if (r != null) {

            r.mousePress(InputEvent.BUTTON1_MASK);
            r.mouseRelease(InputEvent.BUTTON1_MASK);
        }
    }

    private void mouseEscape() {

        Robot r = getRobot();
        if (r != null) {

            r.mousePress(InputEvent.BUTTON3_MASK);
            r.mouseRelease(InputEvent.BUTTON3_MASK);
        }
    }

    private void eventMoveUp() {
        command(RC.UP_COMMAND);
    }

    private void eventMoveDown() {
        command(RC.DOWN_COMMAND);
    }

    private void eventMoveLeft() {
        command(RC.LEFT_COMMAND);
    }

    private void eventMoveRight() {
        command(RC.RIGHT_COMMAND);
    }

    private void eventEnter() {
        command(RC.ENTER_COMMAND);
    }

    private void eventEscape() {
        command(RC.ESCAPE_COMMAND);
    }

    private void eventPause() {
        command(RC.PAUSE_COMMAND);
    }

    private void eventInfo() {
        command(RC.INFO_COMMAND);
    }

    private void eventGuide() {
        command(RC.GUIDE_COMMAND);
    }

    private void eventPageUp() {
        command(RC.PAGE_UP_COMMAND);
    }

    private void eventPageDown() {
        command(RC.PAGE_DOWN_COMMAND);
    }

    private void eventRewind() {
        command(RC.REWIND_COMMAND);
    }

    private void eventForward() {
        command(RC.FORWARD_COMMAND);
    }

    private void eventSkipBackward() {
        command(RC.SKIPBACKWARD_COMMAND);
    }

    private void eventSkipForward() {
        command(RC.SKIPFORWARD_COMMAND);
    }

    private void eventAudiosyncPlus() {
        command(RC.AUDIOSYNC_PLUS_COMMAND);
    }

    private void eventAudiosyncMinus() {
        command(RC.AUDIOSYNC_MINUS_COMMAND);
    }

    private void moveUp() {

        if (isMouseControl()) {
            mouseMoveUp();
        }

        if (isKeyboardControl()) {
            keyboardMoveUp();
        }

        if (isEventControl()) {
            eventMoveUp();
        }
    }

    private void moveDown() {

        if (isMouseControl()) {
            mouseMoveDown();
        }

        if (isKeyboardControl()) {
            keyboardMoveDown();
        }

        if (isEventControl()) {
            eventMoveDown();
        }
    }

    private void moveLeft() {

        if (isMouseControl()) {
            mouseMoveLeft();
        }

        if (isKeyboardControl()) {
            keyboardMoveLeft();
        }

        if (isEventControl()) {
            eventMoveLeft();
        }
    }

    private void moveRight() {

        if (isMouseControl()) {
            mouseMoveRight();
        }

        if (isKeyboardControl()) {
            keyboardMoveRight();
        }

        if (isEventControl()) {
            eventMoveRight();
        }
    }

    private void enter() {

        if (isMouseControl()) {
            mouseEnter();
        }

        if (isKeyboardControl()) {
            keyboardEnter();
        }

        if (isEventControl()) {
            eventEnter();
        }
    }

    private void escape() {

        if (isMouseControl()) {
            mouseEscape();
        }

        if (isKeyboardControl()) {
            keyboardEscape();
        }

        if (isEventControl()) {
            eventEscape();
        }
    }

    private void pause() {

        if (isKeyboardControl()) {
            keyboardPause();
        }

        if (isEventControl()) {
            eventPause();
        }
    }

    private void info() {

        if (isKeyboardControl()) {
            keyboardInfo();
        }

        if (isEventControl()) {
            eventInfo();
        }
    }

    private void guide() {

        if (isKeyboardControl()) {
            keyboardGuide();
        }

        if (isEventControl()) {
            eventGuide();
        }
    }

    private void pageup() {

        if (isKeyboardControl()) {
            keyboardPageUp();
        }

        if (isEventControl()) {
            eventPageUp();
        }
    }

    private void pagedown() {

        if (isKeyboardControl()) {
            keyboardPageDown();
        }

        if (isEventControl()) {
            eventPageDown();
        }
    }

    private void rewind() {

        if (isKeyboardControl()) {
            keyboardRewind();
        }

        if (isEventControl()) {
            eventRewind();
        }
    }

    private void forward() {

        if (isKeyboardControl()) {
            keyboardForward();
        }

        if (isEventControl()) {
            eventForward();
        }
    }

    private void skipbackward() {

        if (isEventControl()) {
            eventSkipBackward();
        }
    }

    private void skipforward() {

        if (isEventControl()) {
            eventSkipForward();
        }
    }

    private void audiosyncplus() {

        if (isEventControl()) {
            eventAudiosyncPlus();
        }
    }

    private void audiosyncminus() {

        if (isEventControl()) {
            eventAudiosyncMinus();
        }
    }

    private void command(String s) {

        ServiceTracker st = getEventServiceTracker();
        if ((st != null) && (s != null)) {

            EventAdmin ea = (EventAdmin) st.getService();
            if (ea != null) {

                Hashtable<String, String> props =
                    new Hashtable<String, String>();
                props.put("command", s);
                Event evt = new Event("org/jflicks/rc/COMMAND", props);
                ea.postEvent(evt);
            }
        }
    }

    /**
     * The action received from lirc.
     *
     * @param command The action string configured for the button that the
     * user pressed.  It should then be mapped to an internal method here.
     */
    public void action(String command) {

        if (command != null) {

            if (command.equalsIgnoreCase("moveUp")) {

                moveUp();

            } else if (command.equalsIgnoreCase("moveDown")) {

                moveDown();

            } else if (command.equalsIgnoreCase("moveLeft")) {

                moveLeft();

            } else if (command.equalsIgnoreCase("moveRight")) {

                moveRight();

            } else if (command.equalsIgnoreCase("enter")) {

                enter();

            } else if (command.equalsIgnoreCase("escape")) {

                escape();

            } else if (command.equalsIgnoreCase("pause")) {

                pause();

            } else if (command.equalsIgnoreCase("info")) {

                info();

            } else if (command.equalsIgnoreCase("guide")) {

                guide();

            } else if (command.equalsIgnoreCase("pageUp")) {

                pageup();

            } else if (command.equalsIgnoreCase("pageDown")) {

                pagedown();

            } else if (command.equalsIgnoreCase("rewind")) {

                rewind();

            } else if (command.equalsIgnoreCase("forward")) {

                forward();

            } else if (command.equalsIgnoreCase("skipbackward")) {

                skipbackward();

            } else if (command.equalsIgnoreCase("skipforward")) {

                skipforward();

            } else if (command.equalsIgnoreCase("audiosyncplus")) {

                audiosyncplus();

            } else if (command.equalsIgnoreCase("audiosyncminus")) {

                audiosyncminus();
            }
        }
    }

    /**
     * We need to know when the user has turned on mouse control so we
     * can locate the mouse and initialize it's location in our Display.
     *
     * @param event A PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getPropertyName().equals("MouseControl")) {

            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                Display d = getDisplay();
                if (d != null) {

                    d.fromMouse();
                }
            }
        }
    }

}
