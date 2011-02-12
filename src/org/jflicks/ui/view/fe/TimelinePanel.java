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
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLayeredPane;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.ShapePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TimelinePanel extends BaseCustomizePanel {

    private static final double VGAP = 0.01;
    private static final double HGAP = 0.01;
    private static final double ARC = 10.0;

    private JXLabel seenLabel;
    private JXLabel unseenLabel;
    private JXLabel currentLabel;
    private JXLabel lengthLabel;
    private int value;
    private int current;
    private int length;

    /**
     * Simple empty constructor.
     */
    public TimelinePanel() {
    }

    private JXLabel getSeenLabel() {
        return (seenLabel);
    }

    private void setSeenLabel(JXLabel l) {
        seenLabel = l;
    }

    private JXLabel getUnseenLabel() {
        return (unseenLabel);
    }

    private void setUnseenLabel(JXLabel l) {
        unseenLabel = l;
    }

    private JXLabel getCurrentLabel() {
        return (currentLabel);
    }

    private void setCurrentLabel(JXLabel l) {
        currentLabel = l;
    }

    private JXLabel getLengthLabel() {
        return (lengthLabel);
    }

    private void setLengthLabel(JXLabel l) {
        lengthLabel = l;
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

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            Color panelc = getPanelColor();
            panelc = new Color(panelc.getRed(), panelc.getGreen(),
                panelc.getBlue(), (int) (getPanelAlpha() * 255));
            setPanelColor(panelc);
            MattePainter painter = new MattePainter(panelc);
            setBackgroundPainter(painter);

            // For now we will split the timeline.
            double halfHeight = height / 2.0;
            double halfWidth = width / 2.0;

            JXLabel seen = new JXLabel();
            RoundRectangle2D.Double seenrect = new RoundRectangle2D.Double(
                0, 0, halfWidth, halfHeight, ARC, ARC);
            ShapePainter sp = new ShapePainter(seenrect, getSelectedColor());
            seen.setBackgroundPainter(sp);
            setSeenLabel(seen);

            // Make a gradient paint based upon the selected color
            // and White.
            GradientPaint gp = new GradientPaint(0, 0, getHighlightColor(),
                0, (int) (halfHeight / 2), Color.WHITE, true);

            JXLabel unseen = new JXLabel();
            RoundRectangle2D.Double unseenrect = new RoundRectangle2D.Double(
                0, 0, halfWidth, halfHeight, ARC, ARC);
            sp = new ShapePainter(unseenrect, gp);
            unseen.setBackgroundPainter(sp);
            setUnseenLabel(unseen);

            JXLabel currentLab = new JXLabel("0:00");
            currentLab.setFont(getSmallFont());
            currentLab.setForeground(getSelectedColor());
            setCurrentLabel(currentLab);

            JXLabel lengthLab = new JXLabel("0:00");
            lengthLab.setFont(getSmallFont());
            lengthLab.setForeground(getSelectedColor());
            setLengthLabel(lengthLab);

            currentLab.setBounds(0, (int) halfHeight, 100, (int) halfHeight);
            lengthLab.setBounds((int) (width - 100), (int) halfHeight, 100,
                (int) halfHeight);
            seen.setBounds(0, 0, (int) halfWidth, (int) halfHeight);
            unseen.setBounds((int) halfWidth, 0, (int) halfWidth,
                (int) halfHeight);

            pane.add(seen, Integer.valueOf(90));
            pane.add(unseen, Integer.valueOf(100));
            pane.add(currentLab, Integer.valueOf(100));
            pane.add(lengthLab, Integer.valueOf(100));
        }
    }

    /**
     * The percentage complete is our value.
     *
     * @return An int reprecenting a percentage 0-100.
     */
    public int getValue() {
        return (value);
    }

    /**
     * The percentage complete is our value.
     *
     * @param i An int reprecenting a percentage 0-100.
     */
    public void setValue(int i) {

        value = i;

        // We need to resize our labels and their rectangles.
        Dimension d = getSize();
        JXLabel seen = getSeenLabel();
        JXLabel unseen = getUnseenLabel();
        if ((seen != null) && (unseen != null) && (d != null)) {

            double width = d.getWidth();
            double height = d.getHeight();
            double halfHeight = height / 2.0;
            double seenWidth = width * ((double) value) / 100.0;
            double unseenWidth = width - seenWidth;

            ShapePainter sp = (ShapePainter) seen.getBackgroundPainter();
            RoundRectangle2D.Double rrect =
                (RoundRectangle2D.Double) sp.getShape();
            rrect.setRoundRect(0.0, 0.0, width, halfHeight, ARC, ARC);
            sp.setShape(rrect);

            sp = (ShapePainter) unseen.getBackgroundPainter();
            rrect = (RoundRectangle2D.Double) sp.getShape();
            rrect.setRoundRect(0.0, 0.0, unseenWidth, halfHeight, ARC, ARC);
            sp.setShape(rrect);

            seen.setBounds(0, 0, (int) width, (int) halfHeight);
            unseen.setBounds((int) seenWidth, 0, (int) unseenWidth,
                (int) halfHeight);
        }
    }

    /**
     * The current time in seconds.
     *
     * @return The current seconds.
     */
    public int getCurrent() {
        return (current);
    }

    /**
     * The current time in seconds.
     *
     * @param i The current seconds.
     */
    public void setCurrent(int i) {

        current = i;

        JXLabel l = getCurrentLabel();
        JXLabel unseen = getUnseenLabel();
        String s = secondsToTime(current);
        Dimension d = getSize();
        if ((d != null) && (unseen != null) && (l != null) && (s != null)) {

            l.setText(s);
            Dimension ld = l.getPreferredSize();
            Dimension usd = unseen.getSize();
            if ((ld != null) && (usd != null)) {

                double width = d.getWidth();
                double height = d.getHeight();
                double halfHeight = height / 2.0;
                double lwidth = ld.getWidth();
                double x = 0.0;

                // Don't let it go off the screen.
                if (((width - usd.getWidth()) - lwidth) > x) {

                    x = (width - usd.getWidth()) - lwidth;
                    if (x < 0.0) {
                        x = 0.0;
                    }
                }

                // Don't let it overwrite the length label.
                if (x > (width - (2.5 * lwidth))) {
                    x = width - (2.5 * lwidth);
                }

                l.setBounds((int) x, (int) halfHeight, (int) lwidth,
                    (int) halfHeight);
            }
        }
    }

    /**
     * The length of the timeline.  This can change over time and they
     * will be reflected in the UI.
     *
     * @return The length in seconds.
     */
    public int getLength() {
        return (length);
    }

    /**
     * The length of the timeline.  This can change over time and they
     * will be reflected in the UI.
     *
     * @param i The length in seconds.
     */
    public void setLength(int i) {

        length = i;

        JXLabel l = getLengthLabel();
        String s = secondsToTime(length);
        Dimension d = getSize();
        if ((d != null) && (l != null) && (s != null)) {

            l.setText(s);
            Dimension ld = l.getPreferredSize();
            if (ld != null) {

                double width = d.getWidth();
                double height = d.getHeight();
                double halfHeight = height / 2.0;
                double lwidth = ld.getWidth();
                double x = width - lwidth;
                l.setBounds((int) x, (int) halfHeight, (int) lwidth,
                    (int) halfHeight);
            }
        }
    }

    private String secondsToTime(int seconds) {

        String result = null;

        StringBuilder sb = new StringBuilder();
        if (sb != null) {

            int hours = seconds / 3600;
            seconds -= (hours * 3600);
            int minutes = seconds / 60;
            seconds -= (minutes * 60);
            if (hours > 0) {
                sb.append(hours + ":");
            }

            if ((minutes < 10) && (hours > 0)) {
                sb.append("0" + minutes + ":");
            } else {
                sb.append(minutes + ":");
            }

            if (seconds > 9) {
                sb.append(seconds);
            } else {
                sb.append("0" + seconds);
            }

            result = sb.toString();
        }

        return (result);
    }

}

