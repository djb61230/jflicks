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
package org.jflicks.ui.view.metadata;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.metadata.Metadata;
import org.jflicks.util.ColumnPanel;
import org.jflicks.util.RowPanel;

/**
 * Be able to select of of our Metadata suppliers.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MetadataSelectPanel extends JPanel
    implements ListSelectionListener {

    private Metadata[] metadata;
    private JList metadataList;
    private JCheckBox movieCheckBox;
    private JCheckBox tvCheckBox;

    /**
     * Constructor with one required argument.
     *
     * @param metadata An array of Metadata instances.
     */
    public MetadataSelectPanel(Metadata[] metadata) {

        JList l = new JList(metadata);
        l.setPrototypeCellValue("01234567890123456789012345");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(4);
        l.addListSelectionListener(this);
        setMetadataList(l);
        JScrollPane videolistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        RowPanel listrp = new RowPanel("Metadata", 0, GridBagConstraints.BOTH,
            videolistScroller);

        JCheckBox mcb = new JCheckBox("Supports Movies");
        setMovieCheckBox(mcb);

        JCheckBox tvcb = new JCheckBox("Supports TV");
        setTVCheckBox(tvcb);

        ColumnPanel rightcp = new ColumnPanel(mcb, tvcb);

        RowPanel rp = new RowPanel(listrp, rightcp);

        setLayout(new BorderLayout());
        add(rp, BorderLayout.CENTER);
    }

    /**
     * The user selects a Metadata to use from an array.
     *
     * @return An array of Metadata instances.
     */
    public Metadata[] getMetadata() {

        Metadata[] result = null;

        if (metadata != null) {

            result = Arrays.copyOf(metadata, metadata.length);
        }

        return (result);
    }

    /**
     * The user selects a Metadata to use from an array.
     *
     * @param array An array of Metadata instances.
     */
    public void setMetadata(Metadata[] array) {

        if (array != null) {
            metadata = Arrays.copyOf(array, array.length);
        } else {
            metadata = null;
        }

        JList l = getMetadataList();
        if ((metadata != null) && (l != null)) {

            l.setListData(metadata);
        }
    }

    private JList getMetadataList() {
        return (metadataList);
    }

    private void setMetadataList(JList l) {
        metadataList = l;
    }

    private JCheckBox getMovieCheckBox() {
        return (movieCheckBox);
    }

    private void setMovieCheckBox(JCheckBox cb) {
        movieCheckBox = cb;
    }

    private JCheckBox getTVCheckBox() {
        return (tvCheckBox);
    }

    private void setTVCheckBox(JCheckBox cb) {
        tvCheckBox = cb;
    }

    /**
     * Convenience method to get the currently selected Metadata.
     *
     * @return A Metadata instance if one is selected.
     */
    public Metadata getSelectedMetadata() {

        Metadata result = null;

        JList l = getMetadataList();
        if (l != null) {

            result = (Metadata) l.getSelectedValue();
        }

        return (result);
    }

    /**
     * We listen for selection on the video list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getMetadataList()) {

                JList l = getMetadataList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Metadata m = (Metadata) l.getSelectedValue();
                    if (m != null) {

                        getMovieCheckBox().setSelected(m.supportsMovies());
                        getTVCheckBox().setSelected(m.supportsTV());
                    }
                }
            }
        }
    }

}
