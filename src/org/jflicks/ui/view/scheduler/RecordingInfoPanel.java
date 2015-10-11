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
package org.jflicks.ui.view.scheduler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Upcoming;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingInfoPanel extends AbstractStatusPanel {

    private StateLabel totalStateLabel;
    private StateLabel ruleStateLabel;
    private StateLabel upcomingStateLabel;
    private StateLabel conflictStateLabel;

    /**
     * Default constructor.
     */
    public RecordingInfoPanel() {

        setTotalStateLabel(new StateLabel());
        setRuleStateLabel(new StateLabel());
        setUpcomingStateLabel(new StateLabel());
        setConflictStateLabel(new StateLabel());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getTotalStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRuleStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getUpcomingStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getConflictStateLabel(), gbc);

        setBorder(BorderFactory.createTitledBorder("Recordings"));
    }

    private StateLabel getTotalStateLabel() {
        return (totalStateLabel);
    }

    private void setTotalStateLabel(StateLabel l) {
        totalStateLabel = l;
    }

    private StateLabel getRuleStateLabel() {
        return (ruleStateLabel);
    }

    private void setRuleStateLabel(StateLabel l) {
        ruleStateLabel = l;
    }

    private StateLabel getUpcomingStateLabel() {
        return (upcomingStateLabel);
    }

    private void setUpcomingStateLabel(StateLabel l) {
        upcomingStateLabel = l;
    }

    private StateLabel getConflictStateLabel() {
        return (conflictStateLabel);
    }

    private void setConflictStateLabel(StateLabel l) {
        conflictStateLabel = l;
    }

    public void populate() {

        StateLabel tsl = getTotalStateLabel();
        StateLabel rsl = getRuleStateLabel();
        StateLabel usl = getUpcomingStateLabel();
        StateLabel csl = getConflictStateLabel();
        if ((tsl != null) && (rsl != null) && (usl != null) && (csl != null)) {

            NMS n = getNMS();
            if (n != null) {

                Recording[] recs = n.getRecordings();
                if ((recs != null) && (recs.length > 0)) {

                    tsl.setWarning(false);
                    tsl.setText("Total Recordings " + recs.length);

                } else {

                    tsl.setWarning(true);
                    tsl.setText("No available Recordings");
                }

                RecordingRule[] rules = n.getRecordingRules();
                if ((rules != null) && (rules.length > 0)) {

                    rsl.setWarning(false);
                    rsl.setText("Total Recording Rules " + rules.length);

                } else {

                    rsl.setWarning(true);
                    rsl.setText("No available Recording Rules");
                }

                Upcoming[] up = n.getUpcomings();
                if ((up != null) && (up.length > 0)) {

                    int concount = 0;
                    int rcount = 0;
                    for (int i = 0; i < up.length; i++) {

                        if (up[i].getRecorderName() != null) {
                            rcount++;
                        }

                        String status = up[i].getStatus();
                        if ((status != null) && (status.equals("Conflict"))) {

                            concount++;
                        }
                    }

                    if (rcount > 0) {
                        usl.setWarning(false);
                    } else {
                        usl.setWarning(true);
                    }

                    if (rcount == 0) {
                        usl.setText("No upcoming Recordings");
                    } else if (rcount == 1) {
                        usl.setText("One upcoming Recording");
                    } else {
                        usl.setText(rcount + " upcoming Recordings");
                    }

                    if (concount > 0) {
                        csl.setWarning(true);
                    } else {
                        csl.setWarning(false);
                    }

                    if (concount == 0) {
                        csl.setText("No conflicts");
                    } else if (concount == 1) {
                        csl.setText("One conflict");
                    } else {
                        csl.setText(concount + " conflicts");
                    }

                } else {

                    usl.setWarning(true);
                    usl.setText("No upcoming Recordings scheduled");
                    csl.setText(null);
                    csl.setIcon(null);
                }

            } else {

                tsl.setText(null);
                tsl.setIcon(null);
                rsl.setText(null);
                rsl.setIcon(null);
                usl.setText(null);
                usl.setIcon(null);
                csl.setText(null);
                csl.setIcon(null);
            }
        }
    }

}
