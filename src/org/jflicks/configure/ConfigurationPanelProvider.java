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

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

import org.jflicks.wizard.WizardPanelProvider;

/**
 * A panel provider that is used to create a wizard to handle the
 * dynamic Configuration object.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ConfigurationPanelProvider extends WizardPanelProvider {

    private Configuration configuration;
    private BaseTypePanel[] baseTypePanels;

    /**
     * Simple constructor with the required Configuration argument.
     *
     * @param c A given Configuration instance.
     */
    public ConfigurationPanelProvider(Configuration c) {

        setTitle("Configuration Wizard");
        setConfiguration(c);

        if (c != null) {

            NameValue[] array = c.getNameValues();
            if ((array != null) && (array.length > 0)) {

                String[] ids = new String[array.length];
                String[] desc = new String[array.length];
                BaseTypePanel[] panels = new BaseTypePanel[array.length];

                for (int i = 0; i < ids.length; i++) {

                    if (array[i] != null) {

                        ids[i] = array[i].getName();
                        desc[i] = array[i].getDescription();
                        if (array[i].isStringType()) {
                            panels[i] = new StringTypePanel(array[i]);
                        } else if (array[i].isIntegerType()) {
                            panels[i] = new IntegerTypePanel(array[i]);
                        } else if (array[i].isBooleanType()) {
                            panels[i] = new BooleanTypePanel(array[i]);
                        } else if (array[i].isFromChoiceType()) {
                            panels[i] = new FromChoiceTypePanel(array[i]);
                        } else if (array[i].isColonListType()) {
                            panels[i] = new ListTypePanel(array[i]);
                        }
                    }
                }

                setBaseTypePanels(panels);
                setPanelIds(ids);
                setPanelDescriptions(desc);
            }
        }
    }

    private Configuration getConfiguration() {
        return (configuration);
    }

    private void setConfiguration(Configuration c) {
        configuration = c;
    }

    private BaseTypePanel[] getBaseTypePanels() {
        return (baseTypePanels);
    }

    private void setBaseTypePanels(BaseTypePanel[] array) {
        baseTypePanels = array;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        return (new Dimension(700, 500));
    }

    /**
     * {@inheritDoc}
     */
    public JPanel createPanel(String id) {

        JPanel result = null;

        Configuration c = getConfiguration();
        BaseTypePanel[] panels = getBaseTypePanels();
        if ((id != null) && (c != null) && (panels != null)) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                int index = -1;
                for (int i = 0; i < array.length; i++) {

                    if (id.equals(array[i].getName())) {

                        index = i;
                        break;
                    }
                }

                if (index != -1) {

                    result = panels[index];
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Map finish() {

        HashMap<String, Configuration> result = null;

        BaseTypePanel[] panels = getBaseTypePanels();
        Configuration c = getConfiguration();
        if ((c != null) && (panels != null)) {

            BaseConfiguration bc = (BaseConfiguration) c;
            BaseConfiguration newbc = new BaseConfiguration(bc);
            NameValue[] array = newbc.getNameValues();
            if (panels.length == array.length) {

                for (int i = 0; i < panels.length; i++) {

                    array[i].setValue(panels[i].getEditedValue());
                }

                result = new HashMap<String, Configuration>();
                result.put("Configuration", newbc);
            }
        }

        return (result);
    }

}

