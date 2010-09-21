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
package org.jflicks.tv.ondemand.roku;

import org.jflicks.tv.ondemand.BaseOnDemand;
import org.jflicks.tv.ondemand.StreamSession;

/**
 * Roku is a set-top box that can access Netflix and other online video
 * sources.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RokuOnDemand extends BaseOnDemand {

    private RokuControl rokuControl;
    private String host;
    private int port;

    /**
     * Simple empty constructor.
     */
    public RokuOnDemand() {

        setTitle("Roku");
    }

    private String getHost() {
        return (host);
    }

    private void setHost(String s) {
        host = s;
    }

    private int getPort() {
        return (port);
    }

    private void setPort(int i) {
        port = i;
    }

    /**
     * {@inheritDoc}
     */
    public void command(StreamSession ss, int type) {

        RokuControl rc = getRokuControl();

        if (rc != null) {

            System.out.println("RokuOnDemand: command : " + type);
            switch (type) {

            default:
            case COMMAND_UP:
                rc.command("press up\n");
                break;

            case COMMAND_DOWN:
                rc.command("press down\n");
                break;

            case COMMAND_LEFT:
                rc.command("press left\n");
                break;

            case COMMAND_RIGHT:
                rc.command("press right\n");
                break;

            case COMMAND_SELECT:
                rc.command("press select\n");
                break;

            case COMMAND_HOME:
                rc.command("press home\n");
                break;

            case COMMAND_FWD:
                rc.command("press fwd\n");
                break;

            case COMMAND_BACK:
                rc.command("press back\n");
                break;

            case COMMAND_PAUSE:
                rc.command("press pause\n");
                break;
            }
        }
    }

    private RokuControl getRokuControl() {

        if (rokuControl == null) {

            String h = getConfiguredHost();
            int p = getConfiguredPort();
            rokuControl = new RokuControl(h, p);
        }

        return (rokuControl);
    }

    /**
     * {@inheritDoc}
     */
    public void closeSession(StreamSession ss) {

        super.closeSession(ss);

        if (rokuControl != null) {

            rokuControl.close();
            rokuControl = null;
        }

    }

}

