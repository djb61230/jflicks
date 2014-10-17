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
 * A class to capture the JSON defining an account.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Status implements Serializable {

    private Account account;
    private Lineup[] lineups;
    private String lastDataUpdate;
    private String[] notifications;
    private SystemStatus[] systemStatus;
    private String serverID;
    private int code;

    /**
     * Simple empty constructor.
     */
    public Status() {
    }

    public Account getAccount() {
        return (account);
    }

    public void setAccount(Account a) {
        account = a;
    }

    public Lineup[] getLineups() {
        return (lineups);
    }

    public void setLineups(Lineup[] array) {
        lineups = array;
    }

    public String getLastDataUpdate() {
        return (lastDataUpdate);
    }

    public void setLastDataUpdate(String s) {
        lastDataUpdate = s;
    }

    public String[] getNotifications() {
        return (notifications);
    }

    public void setNotifications(String[] array) {
        notifications = array;
    }

    public SystemStatus[] getSystemStatus() {
        return (systemStatus);
    }

    public void setSystemStatus(SystemStatus[] array) {
        systemStatus = array;
    }

    public String getServerID() {
        return (serverID);
    }

    public void setServerID(String s) {
        serverID = s;
    }

    public int getCode() {
        return (code);
    }

    public void setCode(int i) {
        code = i;
    }

}

