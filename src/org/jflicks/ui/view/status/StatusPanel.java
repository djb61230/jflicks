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
package org.jflicks.ui.view.status;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import org.jflicks.nms.NMS;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StatusPanel extends AbstractStatusPanel {

    private NMS nms;
    private ProgramDataPanel programDataPanel;
    private RecordingInfoPanel recordingInfoPanel;
    private DiskPanel diskPanel;
    private RecordersPanel recordersPanel;

    /**
     * Default constructor.
     */
    public StatusPanel() {

        setProgramDataPanel(new ProgramDataPanel());
        setRecordingInfoPanel(new RecordingInfoPanel());
        setDiskPanel(new DiskPanel());
        setRecordersPanel(new RecordersPanel());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getProgramDataPanel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRecordersPanel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getDiskPanel(), gbc);

        // The second column
        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRecordingInfoPanel(), gbc);
    }

    /**
     * A refernce to NMS is needed to do the work of this UI component.
     *
     * @param n An NMS instance.
     */
    @Override
    public void setNMS(NMS n) {

        super.setNMS(n);
        programDataPanel.setNMS(n);
        recordingInfoPanel.setNMS(n);
        diskPanel.setNMS(n);
        recordersPanel.setNMS(n);
    }

    private ProgramDataPanel getProgramDataPanel() {
        return (programDataPanel);
    }

    private void setProgramDataPanel(ProgramDataPanel p) {
        programDataPanel = p;
    }

    private RecordingInfoPanel getRecordingInfoPanel() {
        return (recordingInfoPanel);
    }

    private void setRecordingInfoPanel(RecordingInfoPanel p) {
        recordingInfoPanel = p;
    }

    private DiskPanel getDiskPanel() {
        return (diskPanel);
    }

    private void setDiskPanel(DiskPanel p) {
        diskPanel = p;
    }

    private RecordersPanel getRecordersPanel() {
        return (recordersPanel);
    }

    private void setRecordersPanel(RecordersPanel p) {
        recordersPanel = p;
    }

    public void populate() {

        ProgramDataPanel pp = getProgramDataPanel();
        if (pp != null) {

            pp.setNMS(getNMS());
            pp.populate();
        }

        RecordingInfoPanel rp = getRecordingInfoPanel();
        if (rp != null) {

            rp.setNMS(getNMS());
            rp.populate();
        }

        DiskPanel dp = getDiskPanel();
        if (dp != null) {

            dp.setNMS(getNMS());
            dp.populate();
        }

        RecordersPanel recp = getRecordersPanel();
        if (recp != null) {

            recp.setNMS(getNMS());
            recp.populate();
        }
    }

}
