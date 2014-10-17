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
package org.jflicks.tv.programdata.sd.json;

import java.io.Serializable;

/**
 * A class to capture the JSON defining a crew person.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Crew implements Serializable {

    private String personId;
    private String nameId;
    private String name;
    private String role;
    private String billingOrder;

    /**
     * Simple empty constructor.
     */
    public Crew() {
    }

    public String getPersonId() {
        return (personId);
    }

    public void setPersonId(String s) {
        personId = s;
    }

    public String getNameId() {
        return (nameId);
    }

    public void setNameId(String s) {
        nameId = s;
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public String getRole() {
        return (role);
    }

    public void setRole(String s) {
        role = s;
    }

    public String getBillingOrder() {
        return (billingOrder);
    }

    public void setBillingOrder(String s) {
        billingOrder = s;
    }

}

