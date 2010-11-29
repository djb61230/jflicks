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
package org.jflicks.configure;

import java.util.ArrayList;
import java.util.Properties;

import org.jflicks.util.Util;

/**
 * This class implements the Config interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BaseConfig implements Config {

    private Configuration configuration;
    private Configuration defaultConfiguration;

    /**
     * Simple empty constructor.
     */
    public BaseConfig() {
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
        return (configuration);
    }

    /**
     * {@inheritDoc}
     */
    public void setConfiguration(Configuration c) {
        configuration = c;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertiesName() {
        return ("default.properties");
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getDefaultConfiguration() {

        if (defaultConfiguration == null) {

            Properties p = Util.findProperties(this, getPropertiesName());
            if (p == null) {

                p = Util.findProperties(getPropertiesName());
            }
            defaultConfiguration = fromProperties(p);
        }

        return (defaultConfiguration);
    }

    protected Properties toProperties(Configuration c) {

        Properties result = null;

        if (c != null) {

            result = new Properties();
            result.setProperty("configuration_name", c.getName());
            result.setProperty("configuration_source", c.getSource());
            NameValue[] array = c.getNameValues();
            if ((array != null) && (array.length > 0)) {

                result.setProperty("configuration_namevalue_count",
                    "" + array.length);
                for (int i = 0; i < array.length; i++) {

                    String prefix = "configuration_namevalue_" + i;
                    result.setProperty(prefix + "_name", array[i].getName());
                    String tmp = array[i].getDescription();
                    if (tmp != null) {
                        result.setProperty(prefix + "_description", tmp);
                    }

                    tmp = array[i].getDefaultValue();
                    if (tmp != null) {
                        result.setProperty(prefix + "_defaultValue", tmp);
                    }

                    tmp = array[i].getValue();
                    if (tmp != null) {
                        result.setProperty(prefix + "_value", tmp);
                    }

                    result.setProperty(prefix + "_type",
                        array[i].fromType(array[i].getType()));
                    if (array[i].isFromChoiceType()) {

                        arrayToChoices(result, prefix + "_list",
                            array[i].getChoices());

                    } else if (array[i].isIntegerType()) {

                        int itmp = array[i].getMin();
                        if (itmp != Integer.MIN_VALUE) {
                            result.setProperty(prefix + "_min", "" + itmp);
                        }

                        itmp = array[i].getMax();
                        if (itmp != Integer.MAX_VALUE) {
                            result.setProperty(prefix + "_max", "" + itmp);
                        }

                        itmp = array[i].getStep();
                        if (itmp != Integer.MIN_VALUE) {
                            result.setProperty(prefix + "_step", "" + itmp);
                        }
                    }
                }

            } else {

                result.setProperty("configuration_namevalue_count", "0");
            }

        }

        return (result);
    }

    protected BaseConfiguration fromProperties(Properties p) {

        BaseConfiguration result = null;

        if (p != null) {

            result = new BaseConfiguration();
            result.setName(p.getProperty("configuration_name"));
            result.setSource(p.getProperty("configuration_source"));
            int nvcount = Util.str2int(p.getProperty("configuration_"
                + "namevalue_count"), 0);
            for (int i = 0; i < nvcount; i++) {

                NameValue nv = new NameValue();
                String prefix = "configuration_namevalue_" + i;
                nv.setName(p.getProperty(prefix + "_name"));
                nv.setDescription(p.getProperty(prefix + "_description"));
                nv.setDefaultValue(p.getProperty(prefix + "_defaultValue"));
                String valprop = p.getProperty(prefix + "_value");
                if (valprop == null) {
                    nv.setValue(nv.getDefaultValue());
                } else {
                    nv.setValue(valprop);
                }
                nv.setType(nv.toType(p.getProperty(prefix + "_type")));
                if (nv.isFromChoiceType()) {

                    String[] list = listToStringArray(p, prefix + "_list");
                    nv.setChoices(list);

                } else if (nv.isIntegerType()) {

                    String tmp = p.getProperty(prefix + "_min");
                    if (tmp != null) {
                        nv.setMin(Util.str2int(tmp, Integer.MIN_VALUE));
                    }

                    tmp = p.getProperty(prefix + "_max");
                    if (tmp != null) {
                        nv.setMax(Util.str2int(tmp, Integer.MAX_VALUE));
                    }

                    tmp = p.getProperty(prefix + "_step");
                    if (tmp != null) {
                        nv.setStep(Util.str2int(tmp, Integer.MAX_VALUE));
                    }
                }

                result.addNameValue(nv);
            }
        }

        return (result);
    }

    private String[] listToStringArray(Properties p, String prefix) {

        String[] result = null;

        if ((p != null) && (prefix != null)) {

            ArrayList<String> l = new ArrayList<String>();
            int count = Util.str2int(p.getProperty(prefix + "_count"), 0);
            for (int i = 0; i < count; i++) {

                l.add(p.getProperty(prefix + "_" + i + "_choice"));
            }

            if (l.size() > 0) {

                result = l.toArray(new String[l.size()]);
            }
        }

        return (result);
    }

    private void arrayToChoices(Properties p, String prefix, String[] array) {

        if ((p != null) && (prefix != null) && (array != null)) {

            p.setProperty(prefix + "_count", "" + array.length);
            for (int i = 0; i < array.length; i++) {

                p.setProperty(prefix + "_" + i + "_choice", array[i]);
            }
        }
    }

}

