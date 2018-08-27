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
package org.jflicks.restlet;

import org.jflicks.tv.LiveTV;

/**
 * This class contains all the properties from LiveTV session object
 * that a REST client needs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LiveTVBean {

    private String id;
    private String streamURL;
    private String message;
    private boolean ready;

    /**
     * Simple empty constructor.
     */
    public LiveTVBean() {
    }

    public LiveTVBean(LiveTV ltv) {

        if (ltv != null) {

            setId(ltv.getId());
            setStreamURL(ltv.getStreamURL());
            setMessage(ltv.getMessage());
        }
    }

    public LiveTVBean(String message) {
        setMessage(message);
    }

    public String getId() {
        return (id);
    }

    public void setId(String s) {
        id = s;
    }

    public String getStreamURL() {
        return (streamURL);
    }

    public void setStreamURL(String s) {
        streamURL = s;
    }

    public String getMessage() {
        return (message);
    }

    public void setMessage(String s) {
        message = s;
    }

    public boolean isReady() {
        return (ready);
    }

    public void setReady(boolean b) {
        ready = b;
    }

}

