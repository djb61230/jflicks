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
package org.jflicks.mvc;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Abstract class that implements much of the Controller interface including
 * maintaining lists for models and views associated with this controller.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseController implements Controller {

    private ArrayList<View> views;
    private ArrayList<Model> models;

    /**
     * Simple constructor that instantiates our model and view lists.
     */
    public BaseController() {

        views = new ArrayList<View>();
        models = new ArrayList<Model>();
    }

    /**
     * {@inheritDoc}
     */
    public void addView(View v) {

        if (v != null) {

            views.add(v);
            for (Model m : models) {

                m.fireAllProperties();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeView(View v) {

        if (v != null) {

            views.remove(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addModel(Model m) {

        if (m != null) {

            models.add(m);
            m.addPropertyChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeModel(Model m) {

        if (m != null) {

            models.remove(m);
            m.removePropertyChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent event) {

        for (View v : views) {

            v.modelPropertyChange(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setModelProperty(String name, Object newValue) {

        for (Model m : models) {

            try {

                Class[] args = new Class[] {
                    newValue.getClass()
                };
                Method method = m.getClass().getMethod("set" + name, args);
                method.invoke(m, newValue);

            } catch (InvocationTargetException ex) {
                System.out.println("No such property in this model");
            } catch (NoSuchMethodException ex) {
                System.out.println("No such property in this model");
            } catch (IllegalAccessException ex) {
                System.out.println("Permission denied!");
            }
        }
    }

}
