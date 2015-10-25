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
package org.jflicks.ui.view.status;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Date;
import javax.swing.BorderFactory;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ProgramDataPanel extends AbstractStatusPanel {

    private StateLabel channelStateLabel;
    private StateLabel recordableStateLabel;
    private StateLabel lengthStateLabel;
    private StateLabel whenStateLabel;

    /**
     * Default constructor.
     */
    public ProgramDataPanel() {

        setChannelStateLabel(new StateLabel());
        setRecordableStateLabel(new StateLabel());
        setLengthStateLabel(new StateLabel());
        setWhenStateLabel(new StateLabel());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getChannelStateLabel(), gbc);

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
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getLengthStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getWhenStateLabel(), gbc);

        setBorder(BorderFactory.createTitledBorder("Program Data"));
    }

    private StateLabel getChannelStateLabel() {
        return (channelStateLabel);
    }

    private void setChannelStateLabel(StateLabel l) {
        channelStateLabel = l;
    }

    private StateLabel getRecordableStateLabel() {
        return (recordableStateLabel);
    }

    private void setRecordableStateLabel(StateLabel l) {
        recordableStateLabel = l;
    }

    private StateLabel getLengthStateLabel() {
        return (lengthStateLabel);
    }

    private void setLengthStateLabel(StateLabel l) {
        lengthStateLabel = l;
    }

    private StateLabel getWhenStateLabel() {
        return (whenStateLabel);
    }

    private void setWhenStateLabel(StateLabel l) {
        whenStateLabel = l;
    }

    public void populate() {

        StateLabel csl = getChannelStateLabel();
        StateLabel rsl = getRecordableStateLabel();
        StateLabel lsl = getLengthStateLabel();
        StateLabel wsl = getWhenStateLabel();
        if ((csl != null) && (rsl != null) && (lsl != null) && (wsl != null)) {

            NMS n = getNMS();
            if (n != null) {

                if (n.hasProgramData()) {

                    // Ok get the Channels.
                    Channel[] chans = n.getChannels();
                    if ((chans != null) && (chans.length > 0)) {

                        Arrays.sort(chans);
                        csl.setText("Total Number of Channel(s) "
                            + chans.length);
                        csl.setWarning(false);

                        Airing[] airs = n.getAiringsByChannel(chans[0]);
                        if ((airs != null) && (airs.length > 0)) {

                            Airing last = airs[airs.length - 1];
                            Date date = last.getAirDate();
                            Date today = new Date();
                            long ldate = date.getTime();
                            long ltoday = today.getTime();
                            long diff = ldate - ltoday;
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            lsl.setText("At least " + (diffDays + 1)
                                + " days of guide data");
                            lsl.setWarning(false);

                        } else {

                            lsl.setText("0 days of guide data");
                            lsl.setWarning(true);
                        }

                        Channel[] rchans = n.getRecordableChannels();
                        if ((rchans != null) && (rchans.length > 0)) {

                            rsl.setText("Recordable Channel(s) "
                                + rchans.length);
                            rsl.setWarning(false);

                        } else {

                            rsl.setText("No Recordable Channels available");
                            rsl.setWarning(true);
                        }

                    } else {

                        csl.setText("No Channels available");
                        csl.setWarning(true);
                        rsl.setText("No Recordable Channels available");
                        rsl.setWarning(true);
                        lsl.setText("0 days of guide data");
                        lsl.setWarning(true);
                    }

                    if (n.isProgramDataUpdatingNow()) {

                        wsl.setText("Updating right now");
                        wsl.setWarning(false);

                    } else {

                        long when = n.getProgramDataNextTimeToRun();
                        if (when != -1L) {

                            wsl.setText("Next update " + new Date(when));
                            wsl.setWarning(false);

                        } else {

                            wsl.setText("Not scheduled to update!");
                            wsl.setWarning(true);
                        }
                    }

                } else {

                    csl.setText("No ProgramData!");
                    csl.setWarning(true);
                    lsl.setText("No ProgramData!");
                    lsl.setWarning(true);
                    wsl.setText("No ProgramData!");
                    wsl.setWarning(true);
                }

            } else {

                csl.setText("");
                csl.setIcon(null);
                lsl.setText("");
                lsl.setIcon(null);
                wsl.setText("");
                wsl.setIcon(null);
            }
        }
    }

}
