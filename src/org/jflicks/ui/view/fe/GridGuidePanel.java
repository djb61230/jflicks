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
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.ShapePainter;

import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;

/**
 * This class supports a grid style guide component.  The idea is that
 * the component is populated with a HashMap of ShowAiring instances mapped
 * by Channel instances.  Then the user can set a subset of Channel instances
 * to filter the HashMap by.  The component will fire SelectedShowAiring 
 * property events so listeners might want to do stuff like show full details.
 * It will also fire SelectedChannel when the user moves to a new Channel
 * row in the grid.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GridGuidePanel extends BaseCustomizePanel
    implements ActionListener {

    private static final long JUMP_MILLIS = 30L * 60L * 1000L;

    private ArrayList<Channel> channelList;
    private HashMap<Channel, ShowAiring[]> guideMap;
    private JXLabel dateLabel;
    private JXLabel[] timeLabels;
    private RowDisplay[] rowDisplays;
    private Channel selectedChannel;
    private ShowAiring selectedShowAiring;
    private long timeOffset;
    private long lastTime;
    private SimpleDateFormat daySimpleDateFormat;
    private SimpleDateFormat timeSimpleDateFormat;
    private int selectedRow;
    private int selectedColumn;
    private int channelIndex;
    private int visibleRowCount;
    private Timer displayTimer;

    /**
     * Simple empty constructor.
     */
    public GridGuidePanel() {

        setChannelList(new ArrayList<Channel>());
        setVisibleRowCount(4);

        setDaySimpleDateFormat(new SimpleDateFormat("MMM d"));
        setTimeSimpleDateFormat(new SimpleDateFormat("h:mm a"));

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);
        setAlpha((float) getPanelAlpha());

        Timer t = new Timer(60000, this);
        setDisplayTimer(t);
        t.start();
    }

    public int getVisibleRowCount() {
        return (visibleRowCount);
    }

    public void setVisibleRowCount(int i) {
        visibleRowCount = i;
    }

    private Timer getDisplayTimer() {
        return (displayTimer);
    }

    private void setDisplayTimer(Timer t) {
        displayTimer = t;
    }

    private ArrayList<Channel> getChannelList() {
        return (channelList);
    }

    private void setChannelList(ArrayList<Channel> l) {
        channelList = l;
    }

    private int getChannelCount() {

        int result = 0;

        ArrayList<Channel> l = getChannelList();
        if (l != null) {

            result = l.size();
        }

        return (result);
    }

    private SimpleDateFormat getDaySimpleDateFormat() {
        return (daySimpleDateFormat);
    }

    private void setDaySimpleDateFormat(SimpleDateFormat sdf) {
        daySimpleDateFormat = sdf;
    }

    private SimpleDateFormat getTimeSimpleDateFormat() {
        return (timeSimpleDateFormat);
    }

    private void setTimeSimpleDateFormat(SimpleDateFormat sdf) {
        timeSimpleDateFormat = sdf;
    }

    public HashMap<Channel, ShowAiring[]> getGuideMap() {
        return (guideMap);
    }

    public void setGuideMap(HashMap<Channel, ShowAiring[]> m) {
        guideMap = m;
    }

    private JXLabel getDateLabel() {
        return (dateLabel);
    }

    private void setDateLabel(JXLabel l) {
        dateLabel = l;
    }

    private JXLabel[] getTimeLabels() {
        return (timeLabels);
    }

    private void setTimeLabels(JXLabel[] array) {
        timeLabels = array;
    }

    private RowDisplay[] getRowDisplays() {
        return (rowDisplays);
    }

    private void setRowDisplays(RowDisplay[] array) {
        rowDisplays = array;
    }

    private long getTimeOffset() {
        return (timeOffset);
    }

    private void setTimeOffset(long l) {
        timeOffset = l;
    }

    private long getLastTime() {
        return (lastTime);
    }

    private void setLastTime(long l) {
        lastTime = l;
    }

    private int getSelectedRow() {
        return (selectedRow);
    }

    private void setSelectedRow(int i) {
        selectedRow = i;
    }

    private int getSelectedColumn() {
        return (selectedColumn);
    }

    private void setSelectedColumn(int i) {
        selectedColumn = i;
    }

    private int getChannelIndex() {
        return (channelIndex);
    }

    private void setChannelIndex(int i) {
        channelIndex = i;
    }

    public boolean pageUp() {

        boolean result = false;

        for (int i = 0; i < getVisibleRowCount() - 1; i++) {

            boolean action = up();
            if (!action) {
                break;
            } else {
                result = true;
            }
        }

        return (result);
    }

    public boolean pageDown() {

        boolean result = false;

        for (int i = 0; i < getVisibleRowCount() - 1; i++) {

            boolean action = down();
            if (!action) {
                break;
            } else {
                result = true;
            }
        }

        return (result);
    }

    public boolean up() {

        boolean result = false;

        int newrow = getSelectedRow() - 1;
        if (isRowVisible(newrow)) {

            applyHighlight(false);
            int index = computeGridIndex(getSelectedRow(), newrow,
                getSelectedColumn());
            setSelectedRow(newrow);
            setSelectedColumn(index);
            applyHighlight(true);
            result = true;

        } else {

            // We can shift the rows if we can update the ChannelIndex.
            int newcindex = getChannelIndex() - 1;
            if (newcindex >= 0) {

                setChannelIndex(newcindex);
                applyHighlight(false);
                applyChannels();
                int index = computeGridIndex(getSelectedRow() + 1,
                    getSelectedRow(),
                    getSelectedColumn());
                setSelectedColumn(index);
                applyHighlight(true);
                result = true;
            }
        }

        return (result);
    }

    public boolean down() {

        boolean result = false;

        int newrow = getSelectedRow() + 1;
        if (isRowVisible(newrow)) {

            applyHighlight(false);
            int index = computeGridIndex(getSelectedRow(), newrow,
                getSelectedColumn());
            setSelectedRow(newrow);
            setSelectedColumn(index);
            applyHighlight(true);
            result = true;

        } else {

            // We can shift the rows if we can update the ChannelIndex.
            int newcindex = getChannelIndex() + 1;
            int total = getChannelCount();
            int rows = getVisibleRowCount();
            if ((newcindex + rows) <= total) {

                setChannelIndex(newcindex);
                applyHighlight(false);
                applyChannels();
                int index = computeGridIndex(getSelectedRow() - 1,
                    getSelectedRow(), getSelectedColumn());
                setSelectedColumn(index);
                applyHighlight(true);
                result = true;
            }
        }

        return (result);
    }

    public boolean left() {

        boolean result = false;

        int newcol = getSelectedColumn() - 1;
        if (isColumnVisible(newcol)) {

            applyHighlight(false);
            setSelectedColumn(newcol);
            applyHighlight(true);
            result = true;

        } else {

            // We can shift to the left if our timeoff is non-zero.
            if (getTimeOffset() > 0L) {

                applyHighlight(false);
                setTimeOffset(getTimeOffset() - JUMP_MILLIS);
                applyTimeLabels();
                if (!isColumnVisible(getSelectedColumn())) {
                    setSelectedColumn(getSelectedColumn() - 1);
                }
                applyChannels();
                applyHighlight(true);
            }
        }

        return (result);
    }

    public boolean right() {

        boolean result = false;

        int newcol = getSelectedColumn() + 1;
        if (isColumnVisible(newcol)) {

            applyHighlight(false);
            setSelectedColumn(newcol);
            applyHighlight(true);
            result = true;

        } else {

            // We have to shift the time offset.
            applyHighlight(false);
            setTimeOffset(getTimeOffset() + JUMP_MILLIS);
            applyTimeLabels();
            applyChannels();
            setSelectedColumn(getGreatestVisible());
            applyHighlight(true);
        }

        return (result);
    }

    /**
     * We list channel in our panel.
     *
     * @return An array of Channel instances.
     */
    public Channel[] getChannels() {

        Channel[] result = null;

        ArrayList<Channel> l = getChannelList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Channel[l.size()]);
        }

        return (result);
    }

    /**
     * We list channel in our panel.
     *
     * @param array An array of Channel instances.
     */
    public void setChannels(Channel[] array) {

        ArrayList<Channel> l = getChannelList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
            }

            if (getChannelIndex() + getVisibleRowCount() >= l.size()) {
                setChannelIndex(l.size() - (getVisibleRowCount() + 1));
            }
            applyChannels();
            applyTimeLabels();
            applyHighlight(true);
        }
    }

    public Channel getSelectedChannel() {
        return (selectedChannel);
    }

    public void setSelectedChannel(Channel c) {

        Channel old = selectedChannel;
        selectedChannel = c;
        firePropertyChange("SelectedChannel", old, selectedChannel);
    }

    public ShowAiring getSelectedShowAiring() {
        return (selectedShowAiring);
    }

    public void setSelectedShowAiring(ShowAiring sa) {

        ShowAiring old = selectedShowAiring;
        selectedShowAiring = sa;
        firePropertyChange("SelectedShowAiring", old, selectedShowAiring);
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

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();
            int top = (int) (getSmallFontSize() * 1.5);
            int rowCount = getVisibleRowCount();

            int halfhourwidth = (width / 11) * 2;
            int channelwidth = width - (halfhourwidth * 5);
            int colheight = height - top;
            int cellheight = colheight / rowCount;
            if (colheight % rowCount != 0) {

                int left = colheight % rowCount;
                top += left;
                colheight = height - top;
            }

            JXLabel channelLabel = new JXLabel();
            channelLabel.setBorder(BorderFactory.createLineBorder(
                getSelectedColor(), 1));
            channelLabel.setBounds(0, top, channelwidth, colheight);
            pane.add(channelLabel, Integer.valueOf(100));

            JXLabel dlabel = new JXLabel("DATE");
            dlabel.setFont(getSmallFont());
            dlabel.setForeground(getSelectedColor());
            dlabel.setBounds(0, 0, channelwidth, top);
            pane.add(dlabel, Integer.valueOf(100));
            setDateLabel(dlabel);

            JXLabel[] larray = new JXLabel[5];
            int x = channelwidth;
            for (int i = 0; i < larray.length; i++) {

                JXLabel halfHourLabel = new JXLabel();
                halfHourLabel.setBorder(BorderFactory.createLineBorder(
                    getUnselectedColor(), 1));
                halfHourLabel.setBounds(x, top, halfhourwidth, colheight);
                pane.add(halfHourLabel, Integer.valueOf(100));

                larray[i] = new JXLabel("TIME");
                larray[i].setFont(getSmallFont());
                larray[i].setForeground(getSelectedColor());
                larray[i].setBounds(x, 0, halfhourwidth, top);
                pane.add(larray[i], Integer.valueOf(100));
                x += halfhourwidth;
            }
            setTimeLabels(larray);

            RowDisplay[] rdarray = new RowDisplay[rowCount];
            int y = top;
            for (int i = 0; i < rowCount; i++) {

                JXLabel rowLabel = new JXLabel();
                rowLabel.setBorder(BorderFactory.createLineBorder(
                    getUnselectedColor(), 1));
                rowLabel.setBounds(0, y, width, cellheight);
                pane.add(rowLabel, Integer.valueOf(100));
                y += cellheight;

                rdarray[i] = new RowDisplay(pane, rowLabel.getBounds(),
                    channelwidth, halfhourwidth);
            }

            setRowDisplays(rdarray);
        }
    }

    private long getCurrentRounded() {

        Calendar c = Calendar.getInstance();
        int min = c.get(Calendar.MINUTE);
        if (min < 30) {
            min = 0;
        } else {
            min = 30;
        }
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);

        return (c.getTimeInMillis() + getTimeOffset());
    }

    private ShowAiring[] stripDone(ShowAiring[] array) {

        ShowAiring[] result = array;

        if (array != null) {

            if (array.length > 0) {

                int skip = 0;
                for (int i = 0; i < array.length; i++) {

                    if (!array[i].isOver()) {

                        skip = i;
                        break;
                    }
                }

                if (skip > 0) {

                    result = Arrays.copyOfRange(array, skip, array.length);
                }
            }
        }

        return (result);
    }

    private boolean isOver(ShowAiring sa, long when) {

        boolean result = false;

        if (sa != null) {

            Airing a = sa.getAiring();
            if (a != null) {

                Date d = a.getAirDate();
                if (d != null) {

                    long end = d.getTime() + (a.getDuration() * 1000);
                    result = (end < when);
                }
            }
        }

        return (result);
    }

    public boolean isOn(ShowAiring sa) {

        boolean result = false;

        if (sa != null) {

            Airing a = sa.getAiring();
            if (a != null) {

                Date d = a.getAirDate();
                if (d != null) {

                    long airstart = d.getTime();
                    long airend = airstart + (a.getDuration() * 1000);
                    long now = System.currentTimeMillis();

                    result = ((airstart < now) && (now < airend));
                }
            }
        }

        return (result);
    }

    private boolean isOn(ShowAiring sa, long start, long end) {

        boolean result = false;

        if (sa != null) {

            Airing a = sa.getAiring();
            if (a != null) {

                Date d = a.getAirDate();
                if (d != null) {

                    long airstart = d.getTime();
                    long airend = airstart + (a.getDuration() * 1000);

                    result = (((airstart > start) && (airstart < end))
                        || ((airend > start) && (airend < end))
                        || ((airstart <= start) && (airend >= end)));
                }
            }
        }

        return (result);
    }

    private ShowAiring[] stripViaTime(ShowAiring[] array, long when) {

        ShowAiring[] result = array;

        if (array != null) {

            if (array.length > 0) {

                int skip = 0;
                for (int i = 0; i < array.length; i++) {

                    if (!isOver(array[i], when)) {

                        skip = i;
                        break;
                    }
                }

                if (skip > 0) {

                    result = Arrays.copyOfRange(array, skip, array.length);
                }
            }
        }

        return (result);
    }

    private String formatDay(long when) {

        String result = null;

        SimpleDateFormat sdf = getDaySimpleDateFormat();
        if (sdf != null) {

            StringBuffer sb = new StringBuffer();
            sdf.format(new Date(when), sb, new FieldPosition(0));
            result = sb.toString();
        }

        return (result);
    }

    private String formatTime(long when) {

        String result = null;

        SimpleDateFormat sdf = getTimeSimpleDateFormat();
        if (sdf != null) {

            StringBuffer sb = new StringBuffer();
            sdf.format(new Date(when), sb, new FieldPosition(0));
            result = sb.toString();
        }

        return (result);
    }

    private synchronized void applyChannels() {

        Channel[] array = getChannels();
        RowDisplay[] rdarray = getRowDisplays();
        HashMap<Channel, ShowAiring[]> m = getGuideMap();
        if ((array != null) && (rdarray != null) && (m != null)) {

            int index = getChannelIndex();
            long rounded = getCurrentRounded();
            for (int i = 0; i < rdarray.length; i++) {

                if (index < array.length) {

                    Channel chan = array[index];
                    ShowAiring[] sarray = m.get(chan);
                    sarray = stripViaTime(sarray, rounded);
                    rdarray[i].apply(chan, sarray, rounded);

                } else {

                    rdarray[i].apply(null, null, rounded);
                }
                index++;
            }

            repaint();
        }
    }

    private void applyTimeLabels() {

        long l = getCurrentRounded();
        JXLabel label = getDateLabel();
        if (label != null) {

            label.setText(formatDay(l));
        }

        JXLabel[] array = getTimeLabels();
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                array[i].setText(formatTime(l));
                l += 30 * 60 * 1000;
            }
        }
    }

    private void applyHighlight(boolean b) {

        RowDisplay[] array = getRowDisplays();
        if ((array != null) && (array.length > 0)) {

            int row = getSelectedRow();
            if ((row >= array.length) || (row < 0)) {
                row = 0;
            }

            int column = getSelectedColumn();
            array[row].select(b, column);
            if (b) {

                // Set our selected channel when we highlight.
                setSelectedChannel(array[row].getChannel());
            }
        }
    }

    private int getIndex(Channel[] array, Channel c) {

        int result = 0;

        if ((array != null) && (c != null)) {

            for (int i = 0; i < array.length; i++) {

                if (c.equals(array[i])) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    public void setCurrentChannel(Channel c) {

        if (c != null) {

            setChannelIndex(getIndex(getChannels(), c));
            applyChannels();
            setSelectedColumn(0);
            setSelectedRow(0);
            applyHighlight(true);
        }
    }

    private int computeGridIndex(int oldrow, int newrow, int defaultColumn) {

        int result = defaultColumn;

        RowDisplay[] array = getRowDisplays();
        if ((array != null) && (oldrow >= 0) && (newrow >= 0)
            && (oldrow != newrow) && (oldrow < array.length)
            && (newrow < array.length)) {

            RowDisplay oldrd = array[oldrow];
            RowDisplay newrd = array[newrow];
            ShowAiring sa = oldrd.getShowAiringByIndex(defaultColumn);
            if (sa != null) {

                result = newrd.match(sa, defaultColumn);
            }
        }

        return (result);
    }

    private int getGreatestVisible() {

        int result = 0;

        int row = getSelectedRow();
        RowDisplay[] array = getRowDisplays();
        if ((array != null) && (array.length > 0) && (row < array.length)) {

            result = array[row].getGreatestVisible();
        }

        return (result);
    }

    private boolean isColumnVisible(int column) {

        boolean result = false;

        RowDisplay[] array = getRowDisplays();
        if ((array != null) && (array.length > 0) && (column >= 0)) {

            int row = getSelectedRow();
            if (row >= array.length) {
                row = 0;
            }

            result = array[row].isColumnVisible(column);
        }

        return (result);
    }

    private boolean isRowVisible(int row) {

        boolean result = false;

        RowDisplay[] array = getRowDisplays();
        if ((array != null) && (array.length > 0) && (row >= 0)
            && (row < array.length)) {

            result = true;
        }

        return (result);
    }

    public void actionPerformed(ActionEvent event) {

        if (getGuideMap() != null) {

            long last = getLastTime();
            if (last != 0L) {

                long current = getCurrentRounded() - getTimeOffset();
                if ((Math.abs(current - last)) > 1000) {

                    // The start of our time line has changed.  We should
                    // update.  This can be unsettling to the user if they
                    // are interacting right now.  But this should happen
                    // only every 30 minutes.
                    int row = getSelectedRow();
                    int col = getSelectedColumn();
                    setLastTime(current);
                    applyTimeLabels();
                    applyChannels();
                    setSelectedColumn(col);
                    setSelectedRow(row);
                    repaint();
                }

            } else {

                setLastTime(getCurrentRounded() - getTimeOffset());
            }
        }
    }

    class RowDisplay {

        private JLayeredPane layeredPane;
        private Rectangle rectangle;
        private int channelWidth;
        private int halfHourWidth;
        private JXLabel channelLabel;
        private JXLabel[] labels;
        private ShowAiring[] showAirings;
        private Channel channel;

        public RowDisplay(JLayeredPane p, Rectangle r, int cwidth, int swidth) {

            setLayeredPane(p);
            setRectangle(r);
            setChannelWidth(cwidth);
            setHalfHourWidth(swidth);

            // Make 10 labels and put them all at (-1, -1, 0, 0)
            JXLabel[] array = new JXLabel[10];
            for (int i = 0; i < array.length; i++) {

                array[i] = new JXLabel();
                array[i].setTextAlignment(JXLabel.TextAlignment.CENTER);
                array[i].setLineWrap(true);
                array[i].setForeground(getSelectedColor());
                array[i].setFont(getSmallFont());
                array[i].setBorder(BorderFactory.createLineBorder(
                    getSelectedColor(), 1));
                array[i].setBounds(-1, -1, 0, 0);
                p.add(array[i], Integer.valueOf(110));
            }
            setLabels(array);

            // We keep an associated array of ShowAiring so we can easily
            // update the SelectedShowAiring.  We set this array values on
            // an apply().
            ShowAiring[] showArray = new ShowAiring[10];
            setShowAirings(showArray);

            JXLabel clabel = new JXLabel();
            clabel.setTextAlignment(JXLabel.TextAlignment.CENTER);
            clabel.setFont(getSmallFont());
            clabel.setLineWrap(true);
            clabel.setForeground(getSelectedColor());
            clabel.setBounds(r.x, r.y, cwidth, r.height);
            setChannelLabel(clabel);
            p.add(clabel, Integer.valueOf(110));
        }

        private JLayeredPane getLayeredPane() {
            return (layeredPane);
        }

        private void setLayeredPane(JLayeredPane p) {
            layeredPane = p;
        }

        private Rectangle getRectangle() {
            return (rectangle);
        }

        private void setRectangle(Rectangle r) {
            rectangle = r;
        }

        private int getChannelWidth() {
            return (channelWidth);
        }

        private void setChannelWidth(int i) {
            channelWidth = i;
        }

        private int getHalfHourWidth() {
            return (halfHourWidth);
        }

        private void setHalfHourWidth(int i) {
            halfHourWidth = i;
        }

        private JXLabel getChannelLabel() {
            return (channelLabel);
        }

        private void setChannelLabel(JXLabel l) {
            channelLabel = l;
        }

        private JXLabel[] getLabels() {
            return (labels);
        }

        private void setLabels(JXLabel[] array) {
            labels = array;
        }

        private ShowAiring[] getShowAirings() {
            return (showAirings);
        }

        private void setShowAirings(ShowAiring[] array) {
            showAirings = array;
        }

        public Channel getChannel() {
            return (channel);
        }

        private void setChannel(Channel c) {
            channel = c;
        }

        public ShowAiring getShowAiringByIndex(int index) {

            ShowAiring result = null;

            ShowAiring[] array = getShowAirings();
            if ((array != null) && (index < array.length) && (index >= 0)) {

                result = array[index];
            }

            return (result);
        }

        private long getShowAiringStart(ShowAiring sa) {

            long result = 0L;

            if (sa != null) {

                Airing a = sa.getAiring();
                if (a != null) {

                    Date d = a.getAirDate();
                    if (d != null) {

                        result = d.getTime();
                    }
                }
            }

            return (result);
        }

        private long getShowAiringEnd(ShowAiring sa) {

            long result = 0L;

            if (sa != null) {

                Airing a = sa.getAiring();
                if (a != null) {

                    result = getShowAiringStart(sa) + (a.getDuration() * 1000);
                }
            }

            return (result);
        }

        public int match(ShowAiring sa, int defaultIndex) {

            int result = defaultIndex;

            ShowAiring[] array = getShowAirings();
            if ((sa != null) && (array != null) && (array.length > 0)) {

                long most = 0L;
                double start = (double) getShowAiringStart(sa);
                double end = (double) getShowAiringEnd(sa);
                Line2D.Double line =
                    new Line2D.Double(start + 10, 0, end - 10, 0);
                for (int i = 0; i < array.length; i++) {

                    start = (double) getShowAiringStart(array[i]);
                    end = (double) getShowAiringEnd(array[i]);
                    if (line.intersectsLine(start, 0, end, 0)) {

                        result = i;
                        break;
                    }
                }
            }

            return (result);
        }

        private int computeX(long startTime, long current) {

            int result = 0;

            long offset = current - startTime;
            if (offset > 0) {

                // Figure the minutes first.
                double doffset = (double) offset;
                double sec = doffset / 1000;
                double min = sec / 60;
                double halfHour = (double) getHalfHourWidth();
                double permin = halfHour / 30;

                result = (int) (min * permin);
            }

            return (result);
        }

        private Painter createPainter(Rectangle r, Color main, Color c) {

            Painter result = null;

            Color mcopy = new Color(main.getRed(), main.getGreen(),
                main.getBlue(), (int) (getAlpha() * 255));

            Color copy = new Color(c.getRed(), c.getGreen(),
                c.getBlue(), (int) (getAlpha() * 255));

            GradientPaint gp = new GradientPaint(r.width / 2, 0, copy,
                r.width / 2, (int) (r.getHeight() * 0.1), mcopy, false);

            int inset = 8;
            double arc = 10.0;
            RoundRectangle2D.Double rr = new RoundRectangle2D.Double(inset,
                inset, r.width - (inset * 2), r.height - (inset * 2), arc, arc);
            ShapePainter sp = new ShapePainter(rr, gp);
            MattePainter mp = new MattePainter(gp);
            result = mp;

            return (result);
        }

        public void apply(Channel c, ShowAiring[] array, long startTime) {

            long endTime = startTime + 149 * 60 * 1000;
            setChannel(c);

            // First set the Channel text.
            JXLabel clabel = getChannelLabel();
            JXLabel[] larray = getLabels();
            if ((clabel != null) && (larray != null)) {

                if (c != null) {

                    clabel.setText(c.toString());

                } else {

                    clabel.setText("");
                }

                JXLabel[] labs = getLabels();
                ShowAiring[] showArray = getShowAirings();
                if ((labs != null) && (showArray != null)) {

                    for (int i = 0; i < labs.length; i++) {
                        labs[i].setBounds(-1, -1, 0, 0);
                    }

                    int count = 0;
                    for (int i = 0; i < labs.length; i++) {

                        if (array != null) {

                            if (isOn(array[i], startTime, endTime)) {

                                count++;
                            }
                        }
                    }

                    int x = 0;
                    int lastx = 0;
                    int w = 0;
                    for (int i = 0; i < labs.length; i++) {

                        if (array != null) {
                            showArray[i] = array[i];
                        } else {
                            showArray[i] = null;
                        }
                        if (i < count) {

                            Airing air = array[i].getAiring();
                            Show show = array[i].getShow();
                            if ((air != null) && (show != null)) {

                                Date d = air.getAirDate();
                                if (d != null) {

                                    long start = d.getTime();
                                    long end = start
                                        + (air.getDuration() * 1000);

                                    if (i == 0) {

                                        x = computeX(startTime, start)
                                            + getChannelWidth();

                                    } else {

                                        x = lastx;
                                    }
                                    lastx = computeX(startTime, end)
                                        + getChannelWidth();
                                    w = lastx - x;
                                    labs[i].setBounds(x, rectangle.y, w,
                                        rectangle.height);
                                    labs[i].setText(show.getTitle());

                                    Painter p = createPainter(
                                        labs[i].getBounds(),
                                        getPanelColor(),
                                        getSelectedColor());
                                    labs[i].setBackgroundPainter(p);
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean isColumnVisible(int column) {

            boolean result = false;

            JXLabel[] labs = getLabels();
            if ((labs != null) && (column < labs.length)) {

                Rectangle r = labs[column].getBounds();
                result = (r.x != -1);
            }

            return (result);
        }

        public int getGreatestVisible() {

            int result = 0;

            JXLabel[] labs = getLabels();
            if (labs != null) {

                for (int i = labs.length - 1; i >= 0; i--) {

                    Rectangle r = labs[i].getBounds();
                    if (r.x != -1) {

                        result = i;
                        break;
                    }
                }
            }

            return (result);
        }

        public void select(boolean b, int column) {

            JXLabel[] labs = getLabels();
            ShowAiring[] showArray = getShowAirings();
            if ((labs != null) && (column < labs.length)
                && (showArray != null)) {

                Color c = null;
                Painter p = null;
                if (b) {
                    c = getHighlightColor();
                    p = createPainter(labs[column].getBounds(),
                        getHighlightColor(), getSelectedColor());
                } else {
                    c = getSelectedColor();
                    p = createPainter(labs[column].getBounds(),
                        getPanelColor(), getSelectedColor());
                }
                labs[column].setBorder(BorderFactory.createLineBorder(c, 1));
                labs[column].setBackgroundPainter(p);

                if (b) {
                    setSelectedShowAiring(showArray[column]);
                }
            }
        }

    }

}

