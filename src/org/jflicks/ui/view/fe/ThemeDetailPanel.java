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
package org.jflicks.ui.view.fe;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ThemeDetailPanel extends BaseCustomizePanel {

    private static final String SMALL_TEXT = "Small Font - Selected Color";
    private static final String MEDIUM_TEXT = "Medium Font - Unselected Color";
    private static final String LARGE_TEXT = "Large Font - Highlight Color";

    private Theme theme;
    private JXLabel titleLabel;
    private JXLabel descriptionLabel;
    private JXLabel smallLabel;
    private JXLabel mediumLabel;
    private JXLabel largeLabel;

    /**
     * Simple empty constructor.
     */
    public ThemeDetailPanel() {

        JXLabel title = new JXLabel();
        title.setFont(getLargeFont());
        title.setTextAlignment(JXLabel.TextAlignment.LEFT);
        title.setForeground(getSelectedColor());
        setTitleLabel(title);

        JXLabel description = new JXLabel();
        description.setFont(getSmallFont());
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(getSelectedColor());
        description.setLineWrap(true);
        setDescriptionLabel(description);

        JXLabel small = new JXLabel();
        small.setTextAlignment(JXLabel.TextAlignment.LEFT);
        setSmallLabel(small);

        JXLabel medium = new JXLabel();
        medium.setTextAlignment(JXLabel.TextAlignment.LEFT);
        setMediumLabel(medium);

        JXLabel large = new JXLabel();
        large.setTextAlignment(JXLabel.TextAlignment.LEFT);
        setLargeLabel(large);
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JXLabel title = getTitleLabel();
        JXLabel description = getDescriptionLabel();
        JXLabel small = getSmallLabel();
        JXLabel medium = getMediumLabel();
        JXLabel large = getLargeLabel();
        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (title != null) && (description != null)
            && (small != null) && (medium != null)
            && (large != null) && (pane != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            double halfWidth = width / 2.0;

            double titleHeight = height * 0.2;
            double descriptionHeight = height * 0.6;

            title.setBounds(0, 0, (int) halfWidth, (int) titleHeight);
            description.setBounds(0, (int) titleHeight, (int) halfWidth,
                (int) descriptionHeight);
            small.setBounds((int) halfWidth, 0, (int) halfWidth,
                (int) titleHeight);
            medium.setBounds((int) halfWidth,
                (int) titleHeight, (int) halfWidth,
                (int) titleHeight);
            large.setBounds((int) halfWidth,
                (int) (titleHeight + titleHeight), (int) halfWidth,
                (int) titleHeight);

            pane.add(title, Integer.valueOf(100));
            pane.add(description, Integer.valueOf(100));
            pane.add(small, Integer.valueOf(100));
            pane.add(medium, Integer.valueOf(100));
            pane.add(large, Integer.valueOf(100));

            Color back = getPanelColor();
            back = new Color(back.getRed(), back.getGreen(),
                back.getBlue(), (int) (getPanelAlpha() * 255));
            setPanelColor(back);
            MattePainter mpainter = new MattePainter(getPanelColor());
            setBackgroundPainter(mpainter);
        }
    }

    /**
     * We display information about a Theme.
     *
     * @return A Theme instance.
     */
    public Theme getTheme() {
        return (theme);
    }

    /**
     * We display information about a Theme.
     *
     * @param t A Theme instance.
     */
    public void setTheme(Theme t) {

        theme = t;
        if (theme != null) {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText(t.getTitle());
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText(t.getDescription());
            }

            l = getSmallLabel();
            if (l != null) {

                l.setText(SMALL_TEXT);
                l.setFont(t.getSmallFont());
                l.setForeground(t.getSelectedColor());
            }

            l = getMediumLabel();
            if (l != null) {

                l.setText(MEDIUM_TEXT);
                l.setFont(t.getMediumFont());
                l.setForeground(t.getUnselectedColor());
            }

            l = getLargeLabel();
            if (l != null) {

                l.setText(LARGE_TEXT);
                l.setFont(t.getLargeFont());
                l.setForeground(t.getHighlightColor());
            }

        } else {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText("");
            }

            l = getSmallLabel();
            if (l != null) {

                l.setText("");
            }

            l = getMediumLabel();
            if (l != null) {

                l.setText("");
            }

            l = getLargeLabel();
            if (l != null) {

                l.setText("");
            }
        }
    }

    private JXLabel getTitleLabel() {
        return (titleLabel);
    }

    private void setTitleLabel(JXLabel l) {
        titleLabel = l;
    }

    private JXLabel getDescriptionLabel() {
        return (descriptionLabel);
    }

    private void setDescriptionLabel(JXLabel l) {
        descriptionLabel = l;
    }

    private JXLabel getSmallLabel() {
        return (smallLabel);
    }

    private void setSmallLabel(JXLabel l) {
        smallLabel = l;
    }

    private JXLabel getMediumLabel() {
        return (mediumLabel);
    }

    private void setMediumLabel(JXLabel l) {
        mediumLabel = l;
    }

    private JXLabel getLargeLabel() {
        return (largeLabel);
    }

    private void setLargeLabel(JXLabel l) {
        largeLabel = l;
    }

}

