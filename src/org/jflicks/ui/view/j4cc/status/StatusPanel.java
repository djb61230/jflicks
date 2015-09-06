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
package org.jflicks.ui.view.j4cc.status;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import org.jflicks.ui.view.j4cc.AbstractPanel;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StatusPanel extends AbstractPanel {

    private ProgramDataPanel programDataPanel;
    private RecordingsPanel recordingsPanel;
    private DiskPanel diskPanel;
    private RecordersPanel recordersPanel;

    /**
     * Default constructor.
     */
    public StatusPanel() {

        setProgramDataPanel(new ProgramDataPanel());
        setRecordingsPanel(new RecordingsPanel());
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

        add(getRecordingsPanel(), gbc);
    }

    private ProgramDataPanel getProgramDataPanel() {
        return (programDataPanel);
    }

    private void setProgramDataPanel(ProgramDataPanel p) {
        programDataPanel = p;
    }

    private RecordingsPanel getRecordingsPanel() {
        return (recordingsPanel);
    }

    private void setRecordingsPanel(RecordingsPanel p) {
        recordingsPanel = p;
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
        }

        RecordingsPanel rp = getRecordingsPanel();
        if (rp != null) {

            rp.setNMS(getNMS());
        }

        DiskPanel dp = getDiskPanel();
        if (dp != null) {

            dp.setNMS(getNMS());
        }

        RecordersPanel recp = getRecordersPanel();
        if (recp != null) {

            recp.setNMS(getNMS());
        }
    }

}
