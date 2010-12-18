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
package org.jflicks.ui.view.aspirin.analyze.localhost;

import org.jflicks.ui.view.aspirin.analyze.BaseAnalyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;
import org.jflicks.util.Hostname;

/**
 * This class is an Analyze implementation that can check if a program
 * is in the users path.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LocalhostAnalyze extends BaseAnalyze {

    /**
     * Simple empty constructor.
     */
    public LocalhostAnalyze() {

        setTitle("LocalhostAnalyze");
        setShortDescription("See if loopback is returned for localhost.");

        StringBuilder sb = new StringBuilder();

        sb.append("For communication between jflicks media system clients ");
        sb.append("and servers, it's important that the localhost NOT ");
        sb.append("resolve to the loopback IP 127.0.0.1 address.  ");
        sb.append("Servers really need to have a static IP as they are ");
        sb.append("most likely sharing drives.  Clients probably are OK ");
        sb.append("to run with a dynamic IP but we prefer to run them ");
        sb.append("statically as well.  Anyway this will see if the ");
        sb.append("loopback is returned for localhost.");

        setLongDescription(sb.toString());

        String[] array = {
            "jflicks-base.jar"
        };

        setBundles(array);
    }

    /**
     * {@inheritDoc}
     */
    public Finding analyze() {

        Finding result = new Finding();

        result.setTitle(getShortDescription());
        String ip = Hostname.getHostAddress();
        if (ip != null) {

            StringBuilder sb = new StringBuilder();
            if (Hostname.isLoopback(ip)) {

                // Not good, we need to set failure.
                result.setPassed(false);
                sb.append("Your localhost resolves to " + ip);
                sb.append(" which will cause problems.  Please add an");
                sb.append(" entry to your hosts file or resolve this in");
                sb.append(" some way.");

            } else {

                // Looks like we are OK.
                result.setPassed(true);
                sb.append("Your localhost resolves to " + ip);
                sb.append(" which should be fine.");
            }

            result.setDescription(sb.toString());

        } else {

            result.setPassed(false);
            result.setDescription("Your hostname is NOT set!!");
        }

        return (result);
    }

    /**
     * Override so we look good in UI components.
     *
     * @return A String that is the short description property.
     */
    public String toString() {
        return (getShortDescription());
    }

}

