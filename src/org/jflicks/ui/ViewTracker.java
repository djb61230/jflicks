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
package org.jflicks.ui;

import java.util.HashMap;
import javax.swing.SwingUtilities;

import org.jflicks.mvc.View;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.util.BaseTracker;
import org.jflicks.util.UI;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Track all View services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ViewTracker extends BaseTracker {

    private JFlicksController ltmsController;
    private HashMap<String, UI> uiHashMap;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param mcc Our controller that needs to be hooked up with Views.
     */
    public ViewTracker(BundleContext bc, JFlicksController mcc) {

        super(bc, View.class.getName());
        setJFlicksController(mcc);
        setUIHashMap(new HashMap<String, UI>());
    }

    private JFlicksController getJFlicksController() {
        return (ltmsController);
    }

    private void setJFlicksController(JFlicksController c) {
        ltmsController = c;
    }

    private HashMap<String, UI> getUIHashMap() {
        return (uiHashMap);
    }

    private void setUIHashMap(HashMap<String, UI> hm) {
        uiHashMap = hm;
    }

    private void addUI(String name, UI ui) {

        HashMap<String, UI> hm = getUIHashMap();
        if ((hm != null) && (name != null) && (ui != null)) {

            hm.put(name, ui);
        }
    }

    private UI removeUI(String name) {

        UI result = null;

        HashMap<String, UI> hm = getUIHashMap();
        if ((hm != null) && (name != null)) {

            result = hm.get(name);
            hm.remove(name);
        }

        return (result);
    }

    private void startUI(UI ui) {

        if (ui != null) {
            if (SwingUtilities.isEventDispatchThread()) {

                ui.run();

            } else {

                try {

                    SwingUtilities.invokeAndWait(ui);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void stopUI(UI ui) {

        if (ui != null) {

            final UI fui = ui;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    fui.dispose();
                }
            });
        }
    }

    /**
     * A new View has come online.
     *
     * @param sr The View ServiceReference object.
     * @return The View instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        String title = (String) sr.getProperty(JFlicksView.TITLE_PROPERTY);
        BundleContext bc = getBundleContext();
        if (bc != null) {

            add(title, sr);
            View v = (View) bc.getService(sr);
            //addView(v);
            result = v;

            UI ui = new UI(v, getJFlicksController());
            addUI(title, ui);
            startUI(ui);
        }

        return (result);
    }

    /**
     * A View has been modified.
     *
     * @param sr The View ServiceReference.
     * @param svc The View instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A View service has gone away.  Bye-bye.
     *
     * @param sr The View ServiceReference.
     * @param svc The View instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        String title = (String) sr.getProperty(JFlicksView.TITLE_PROPERTY);
        dispose(title);
        removeView((View) svc);
        stopUI(removeUI(title));
    }

    private void addView(View v) {

        JFlicksController c = getJFlicksController();
        if (c != null) {
            c.addView(v);
        }
    }

    private void removeView(View v) {

        JFlicksController c = getJFlicksController();
        if (c != null) {
            c.removeView(v);
        }
    }

}
