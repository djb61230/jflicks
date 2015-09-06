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
package org.jflicks.ui.view;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;

import org.jflicks.mvc.BaseView;
import org.jflicks.mvc.Controller;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A base class that full Views can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class JFlicksView extends BaseView implements EventHandler {

    /**
     * Each View needs to have a Title property.
     */
    public static final String TITLE_PROPERTY = "View-Title";

    /**
     * We define property names so that extensions don't have to hardwire, and
     * in this case it is the defined DVD device.
     */
    public static final String DVD_DEVICE_PROPERTY = "dvd_device";

    /**
     * We define property names so that extensions don't have to hardwire, and
     * in this case it is allow a client to override the playing of video
     * introductions.
     */
    public static final String WANT_INTRO = "want_intro";

    private BundleContext bundleContext;
    private ServiceTracker controllerServiceTracker;
    private Properties properties;

    /**
     * Extensions receive messages from the EventAdmin via this method.  All
     * the OSGi stuff is handled here so extensions need not worry.
     *
     * @param s The message detailing the event text.
     */
    public abstract void messageReceived(String s);

    /**
     * Default constructor.
     */
    public JFlicksView() {

        File home = new File(".");
        File prop = new File(home, "jflicks.properties");
        if (prop.exists()) {

            LogUtil.log(LogUtil.INFO, "old properties exists...");
            setProperties(Util.findProperties(prop));

        } else {

            setProperties(new Properties());
        }
    }

    /**
     * We keep a reference to a BundleContext as a convenience to extensions.
     *
     * @return A BundleContext instance.
     */
    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    /**
     * We keep a reference to a BundleContext as a convenience to extensions.
     *
     * @param bc A BundleContext instance.
     */
    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    /**
     * A ServiceTracker is needed to get a handle to the UI Service.
     *
     * @return A ServiceTracker instance.
     */
    public ServiceTracker getControllerServiceTracker() {
        return (controllerServiceTracker);
    }

    /**
     * A ServiceTracker is needed to get a handle to the UI Service.
     *
     * @param st A ServiceTracker instance.
     */
    public void setControllerServiceTracker(ServiceTracker st) {
        controllerServiceTracker = st;
    }

    /**
     * {@inheritDoc}
     */
    public Controller getController() {

        Controller result = null;

        ServiceTracker st = getControllerServiceTracker();
        if (st != null) {

            result = (Controller) st.getService();
        }

        return (result);
    }

    /**
     * The app Properties instance.
     *
     * @return A properties object.
     */
    public Properties getProperties() {
        return (properties);
    }

    /**
     * The app Properties instance.
     *
     * @param p A properties object.
     */
    public void setProperties(Properties p) {
        properties = p;
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String s) {

        String result = null;

        Properties p = getProperties();
        if (p != null) {

            result = p.getProperty(s);
        }

        return (result);
    }

    /**
     * We handle Events from the EventAdmin and pass along the "message"
     * via the messageReceived(string) method implemented by extensions.
     *
     * @param event The given Event instance.
     */
    public void handleEvent(Event event) {

        LogUtil.log(LogUtil.INFO, "handleEvent: " + event);
        String message = (String) event.getProperty("message");
        if (message != null) {

            messageReceived(message);
        }
    }

    /**
     * Exit out of the OSGi framework nicely.
     */
    public void exitAction(boolean ask) {

        String title = "Exit This Program";
        String s = "Are you sure?";
        int result = JOptionPane.YES_OPTION;
        if (ask) {
            result = JOptionPane.showConfirmDialog(getFrame(),
                s, title, JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.YES_OPTION) {

            BundleContext bc = getBundleContext();
            if (bc != null) {

                Bundle b = bc.getBundle(0L);
                if (b != null) {

                    try {

                        b.stop();

                    } catch (BundleException ex) {

                        System.exit(0);
                    }
                }
            }
        }
    }

    /**
     * Given a prefix get a Rectangle bounds from our app properties.
     *
     * @param prefix The prefix to use.
     * @return A Rectangle instance.
     */
    public Rectangle getBounds(String prefix) {

        Rectangle result = null;

        Properties p = getProperties();
        if ((p != null) && (prefix != null)) {

            int x = Util.str2int(p.getProperty(prefix + "_x"), -1);
            int y = Util.str2int(p.getProperty(prefix + "_y"), -1);
            int w = Util.str2int(p.getProperty(prefix + "_w"), -1);
            int h = Util.str2int(p.getProperty(prefix + "_h"), -1);
            if ((x != -1) && (y != -1) && (w != -1) && (h != -1)) {

                result = new Rectangle(x, y, w, h);
            }
        }

        LogUtil.log(LogUtil.INFO, "getBounds: <" + prefix + "> " + result);
        return (result);
    }

    /**
     * Save a bounds off to our app properties class.
     *
     * @param prefix Prefix for tag.
     * @param r A given Rectangle.
     */
    public void setBounds(String prefix, Rectangle r) {

        Properties p = getProperties();
        if ((p != null) && (prefix != null) && (r != null)) {

            p.setProperty(prefix + "_x", "" + r.x);
            p.setProperty(prefix + "_y", "" + r.y);
            p.setProperty(prefix + "_w", "" + r.width);
            p.setProperty(prefix + "_h", "" + r.height);
        }
    }

    /**
     * Save off any UI type of state.
     */
    public void saveProperties() {

        Properties p = getProperties();
        if (p != null) {

            FileWriter fw = null;
            try {

                fw = new FileWriter("jflicks.properties");
                p.store(fw, "no comment :)");
                fw.close();
                fw = null;

            } catch (IOException ex) {
            } finally {

                if (fw != null) {

                    try {

                        fw.close();

                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

}
