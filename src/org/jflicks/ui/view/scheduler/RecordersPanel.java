/*
    This file is part of JFLICKS.

    JFLICKS is length software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Length Software Foundation, either version 3 of the License, or
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
import org.jflicks.tv.recorder.Recorder;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordersPanel extends AbstractStatusPanel {

    private StateLabel knownStateLabel;
    private StateLabel recordableStateLabel;
    private StateLabel busyStateLabel;

    /**
     * Default constructor.
     */
    public RecordersPanel() {

        setKnownStateLabel(new StateLabel());
        setRecordableStateLabel(new StateLabel());
        setBusyStateLabel(new StateLabel());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getKnownStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRecordableStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getBusyStateLabel(), gbc);

        setBorder(BorderFactory.createTitledBorder("Recorders"));
    }

    private StateLabel getKnownStateLabel() {
        return (knownStateLabel);
    }

    private void setKnownStateLabel(StateLabel l) {
        knownStateLabel = l;
    }

    private StateLabel getRecordableStateLabel() {
        return (recordableStateLabel);
    }

    private void setRecordableStateLabel(StateLabel l) {
        recordableStateLabel = l;
    }

    private StateLabel getBusyStateLabel() {
        return (busyStateLabel);
    }

    private void setBusyStateLabel(StateLabel l) {
        busyStateLabel = l;
    }

    public void populate() {

        StateLabel ksl = getKnownStateLabel();
        StateLabel rsl = getRecordableStateLabel();
        StateLabel bsl = getBusyStateLabel();
        if ((ksl != null) && (rsl != null) && (bsl != null)) {

            NMS n = getNMS();
            if (n != null) {

                Recorder[] recs = n.getRecorders();
                if ((recs != null) && (recs.length > 0)) {

                    if (recs.length == 1) {
                        ksl.setText("One Available Recorder");
                    } else {
                        ksl.setText(recs.length + " available Recorders ");
                    }
                    ksl.setWarning(false);

                    Recorder[] crecs = n.getConfiguredRecorders();
                    if ((crecs != null) && (crecs.length > 0)) {

                        if (crecs.length == 1) {
                            rsl.setText(crecs.length + " configured Recorder");
                        } else if (crecs.length == 0) {
                            rsl.setText("No configured Recorders");
                        } else {
                            rsl.setText(crecs.length + " configured Recorders");
                        }
                        rsl.setWarning(false);
                        int busy = 0;
                        for (int i = 0; i < crecs.length; i++) {

                            if (crecs[i].isRecording()) {
                                busy++;
                            }
                        }

                        if (crecs.length > 0) {

                            if (busy == 0) {
                                bsl.setText("Nothing currently recording");
                            } else if (busy == 1) {
                                bsl.setText("One of them is recording now");
                            } else {
                                bsl.setText(busy + " of them are recording now");
                            }

                            bsl.setWarning(false);

                        } else {

                            bsl.setText("Cannot record");
                            bsl.setWarning(true);
                        }

                    } else {

                        rsl.setText("No Configured Recorders");
                        rsl.setWarning(true);
                        bsl.setText("No busy Recorders");
                        bsl.setWarning(true);
                    }

                } else {

                    ksl.setText("No Recorders!");
                    ksl.setWarning(true);
                    rsl.setText(null);
                    rsl.setIcon(null);
                    bsl.setText(null);
                    bsl.setIcon(null);
                }

            } else {

                ksl.setText(null);
                ksl.setIcon(null);
                rsl.setText(null);
                rsl.setIcon(null);
                bsl.setText(null);
                bsl.setIcon(null);
            }
        }
    }

}
