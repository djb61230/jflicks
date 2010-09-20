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

import javax.swing.JPanel;

/**
 * A base class that can be extended to build panel instances to edit
 * NameValue objects.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseTypePanel extends JPanel {

    private NameValue nameValue;

    /**
     * Acquire the edited value of the original NameValue value input.
     *
     * @return A String instance.
     */
    public abstract String getEditedValue();

    /**
     * Empty Contructor.
     */
    public BaseTypePanel() {
    }

    /**
     * Simple constructor with required NameValue argument.
     *
     * @param nv A given NameValue instance.
     */
    public BaseTypePanel(NameValue nv) {

        setNameValue(nv);
    }

    /**
     * We need a NameValue instance to edit.
     *
     * @return A NameValue instance.
     */
    public NameValue getNameValue() {
        return (nameValue);
    }

    /**
     * We need a NameValue instance to edit.
     *
     * @param nv A NameValue instance.
     */
    public void setNameValue(NameValue nv) {
        nameValue = nv;
    }

}

