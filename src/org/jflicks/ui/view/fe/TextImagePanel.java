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
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

import org.jflicks.util.Util;

/**
 * This panel will display an array of JXLabels based upon the given
 * TextImage objects passed in the constructor.  The panel will animate
 * with zooming fonts and fade ins and fade outs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TextImagePanel extends BaseCustomizePanel
    implements ActionListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.02;
    private static final int ATIME = 250;
    private static final int RESOLUTION = 10;

    private ArrayList<ActionListener> actionList =
        new ArrayList<ActionListener>();

    private TextImage[] textImages;

    private JXPanel backgroundPanel;
    private JXPanel popupPanel;
    private JXLabel logoLabel;
    private MattePainter mattePainter;
    private JXLabel[] labels;
    private Animator[] animators;
    private Animator backgroundAnimator;
    private ClockPanel clockPanel;
    private int selectedIndex;
    private int popupSelectedIndex;
    private HashMap<TextImage, TextImage[]> childHashMap;
    private HashMap<TextImage[], JXPanel> panelHashMap;
    private HashMap<JXPanel, Animator> animatorHashMap;
    private HashMap<JXPanel, JXLabel[]> labelsHashMap;
    private ImageIcon rightButtonImageIcon;

    /**
     * Constructor with all required arguments.
     *
     * @param array The defined TextImage instances to display.
     * @param logo The logo to display.
     */
    public TextImagePanel(TextImage[] array, BufferedImage logo) {

        setFocusable(true);
        requestFocus();

        setRightButtonImageIcon(
            new ImageIcon(getClass().getResource("right_button.png")));

        if (logo != null) {

            JXLabel llabel = new JXLabel(new ImageIcon(logo));
            setLogoLabel(llabel);
        }

        setTextImages(array);
        setSelectedIndex(-1);

        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        LeftAction leftAction = new LeftAction();
        map.put(KeyStroke.getKeyStroke("LEFT"), "left");
        getActionMap().put("left", leftAction);

        RightAction rightAction = new RightAction();
        map.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        getActionMap().put("right", rightAction);

        UpAction upAction = new UpAction();
        map.put(KeyStroke.getKeyStroke("UP"), "up");
        getActionMap().put("up", upAction);

        DownAction downAction = new DownAction();
        map.put(KeyStroke.getKeyStroke("DOWN"), "down");
        getActionMap().put("down", downAction);

        EnterAction enterAction = new EnterAction(this);
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);
    }

    private ImageIcon getRightButtonImageIcon() {
        return (rightButtonImageIcon);
    }

    private void setRightButtonImageIcon(ImageIcon ii) {
        rightButtonImageIcon = ii;
    }

    private HashMap<TextImage, TextImage[]> getChildHashMap() {
        return (childHashMap);
    }

    private void setChildHashMap(HashMap<TextImage, TextImage[]> m) {
        childHashMap = m;
    }

    private void addChild(TextImage child) {

        HashMap<TextImage, TextImage[]> m = getChildHashMap();
        if ((child != null) && (m != null)) {

            TextImage parent = child.getParentTextImage();
            if (parent != null) {

                TextImage[] array = m.get(parent);
                if (array != null) {

                    boolean found = false;
                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals(child)) {

                            found = true;
                            break;
                        }
                    }

                    if (!found) {

                        TextImage[] newarray = new TextImage[array.length + 1];
                        for (int i = 0; i < array.length; i++) {

                            newarray[i] = array[i];
                        }
                        newarray[newarray.length - 1] = child;
                        m.put(parent, newarray);
                    }

                } else {

                    array = new TextImage[1];
                    array[0] = child;
                    m.put(parent, array);
                }
            }
        }
    }

    private TextImage[] getChildren(String s) {

        TextImage[] result = null;

        HashMap<TextImage, TextImage[]> m = getChildHashMap();
        if ((s != null) && (m != null)) {

            Iterator<TextImage> iterator = m.keySet().iterator();
            while (iterator.hasNext()) {

                TextImage ti = iterator.next();
                if (s.equals(ti.getText())) {

                    result = m.get(ti);
                    break;
                }
            }
        }

        return (result);
    }

    private String[] getChildren() {

        String[] result = null;

        HashMap<TextImage, TextImage[]> m = getChildHashMap();
        if (m != null) {

            Collection<TextImage[]> c = m.values();
            if (c != null) {

                ArrayList<String> list = new ArrayList<String>();
                Iterator<TextImage[]> iterator = c.iterator();
                while (iterator.hasNext()) {

                    TextImage[] array = iterator.next();
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            list.add(array[i].getText());
                        }
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new String[list.size()]);
                }
            }
        }

        return (result);
    }

    private HashMap<TextImage[], JXPanel> getPanelHashMap() {
        return (panelHashMap);
    }

    private void setPanelHashMap(HashMap<TextImage[], JXPanel> m) {
        panelHashMap = m;
    }

    private void addPanel(TextImage[] array, JXPanel p) {

        HashMap<TextImage[], JXPanel> m = getPanelHashMap();
        if ((array != null) && (p != null) && (m != null)) {

            m.put(array, p);
        }
    }

    private JXPanel getPanel(TextImage[] array) {

        JXPanel result = null;

        HashMap<TextImage[], JXPanel> m = getPanelHashMap();
        if ((array != null) && (m != null)) {

            result = m.get(array);
        }

        return (result);
    }

    private HashMap<JXPanel, JXLabel[]> getLabelsHashMap() {
        return (labelsHashMap);
    }

    private void setLabelsHashMap(HashMap<JXPanel, JXLabel[]> m) {
        labelsHashMap = m;
    }

    private void addLabels(JXPanel p, JXLabel[] array) {

        HashMap<JXPanel, JXLabel[]> m = getLabelsHashMap();
        if ((p != null) && (array != null) && (m != null)) {

            m.put(p, array);
        }
    }

    private JXLabel[] getLabels(JXPanel p) {

        JXLabel[] result = null;

        HashMap<JXPanel, JXLabel[]> m = getLabelsHashMap();
        if ((p != null) && (m != null)) {

            result = m.get(p);
        }

        return (result);
    }

    private HashMap<JXPanel, Animator> getAnimatorHashMap() {
        return (animatorHashMap);
    }

    private void setAnimatorHashMap(HashMap<JXPanel, Animator> m) {
        animatorHashMap = m;
    }

    private void addAnimator(JXPanel p, Animator a) {

        HashMap<JXPanel, Animator> m = getAnimatorHashMap();
        if ((p != null) && (a != null) && (m != null)) {

            m.put(p, a);
        }
    }

    private Animator getAnimator(JXPanel p) {

        Animator result = null;

        HashMap<JXPanel, Animator> m = getAnimatorHashMap();
        if ((p != null) && (m != null)) {

            result = m.get(p);
        }

        return (result);
    }

    /**
     * Allow access to our defined TextImage array.
     *
     * @return An array of TextImage instances.
     */
    public TextImage[] getTextImages() {

        TextImage[] result = null;

        if (textImages != null) {

            result = Arrays.copyOf(textImages, textImages.length);
        }

        return (result);
    }

    private void setTextImages(TextImage[] array) {
        textImages = array;
    }

    private int getSelectedIndex() {
        return (selectedIndex);
    }

    private void setSelectedIndex(int i) {

        int old = selectedIndex;
        selectedIndex = i;
        update(old, selectedIndex);
    }

    private int getPopupSelectedIndex() {
        return (popupSelectedIndex);
    }

    private void setPopupSelectedIndex(int i) {

        int old = popupSelectedIndex;
        popupSelectedIndex = i;
        updatePopup(old, popupSelectedIndex);
    }

    private JXPanel getBackgroundPanel() {
        return (backgroundPanel);
    }

    private void setBackgroundPanel(JXPanel p) {
        backgroundPanel = p;
    }

    private JXPanel getPopupPanel() {
        return (popupPanel);
    }

    private void setPopupPanel(JXPanel p) {
        popupPanel = p;
    }

    private JXLabel getLogoLabel() {
        return (logoLabel);
    }

    private void setLogoLabel(JXLabel l) {
        logoLabel = l;
    }

    private MattePainter getMattePainter() {
        return (mattePainter);
    }

    private void setMattePainter(MattePainter p) {
        mattePainter = p;
    }

    private JXLabel[] getLabels() {
        return (labels);
    }

    private void setLabels(JXLabel[] array) {
        labels = array;
    }

    private Animator[] getAnimators() {
        return (animators);
    }

    private void setAnimators(Animator[] array) {
        animators = array;
    }

    private Animator getBackgroundAnimator() {
        return (backgroundAnimator);
    }

    private void setBackgroundAnimator(Animator a) {
        backgroundAnimator = a;
    }

    private ClockPanel getClockPanel() {
        return (clockPanel);
    }

    private void setClockPanel(ClockPanel p) {
        clockPanel = p;
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

            // This most likely our screen size...
            double width = d.getWidth();
            double height = d.getHeight();

            // Compute our gaps...
            double hgap = width * HGAP;
            double vgap = height * VGAP;

            setChildHashMap(new HashMap<TextImage, TextImage[]>());
            setPanelHashMap(new HashMap<TextImage[], JXPanel>());
            setAnimatorHashMap(new HashMap<JXPanel, Animator>());
            setLabelsHashMap(new HashMap<JXPanel, JXLabel[]>());

            TextImage[] tarray = getTextImages();

            if ((tarray != null) && (tarray.length > 0)) {

                ArrayList<JXLabel> list = new ArrayList<JXLabel>();
                for (int i = 0; i < tarray.length; i++) {

                    if (tarray[i].getParentTextImage() == null) {

                        JXLabel label = new JXLabel(tarray[i].getText());
                        label.setFont(getLargeFont());
                        label.setForeground(getSelectedColor());
                        label.setHorizontalTextPosition(SwingConstants.CENTER);
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                        list.add(label);

                    } else {

                        // We have a child.
                        addChild(tarray[i]);
                    }
                }

                JXLabel[] labs = list.toArray(new JXLabel[list.size()]);

                FontEvaluator feval = new FontEvaluator(
                    getMediumFont(), getLargeFont(), getMediumFontSize(),
                    getLargeFontSize());

                KeyFrames.Builder<Font> kfb = new KeyFrames.Builder<Font>();
                kfb.addFrames(feval.getFonts());
                kfb.setEvaluator(feval);
                KeyFrames<Font> fontFrames = kfb.build();
                Animator[] anis = new Animator[tarray.length];
                for (int i = 0; i < labs.length; i++) {

                    TimingTarget tt =
                        PropertySetter.getTarget(labs[i], "font", fontFrames);
                    anis[i] = new Animator.Builder().setDuration(ATIME,
                        TimeUnit.MILLISECONDS).setEndBehavior(
                        Animator.EndBehavior.HOLD).addTarget(tt).build();
                }

                setLabels(labs);
                setAnimators(anis);
            }

            ClockPanel clockp = new ClockPanel(getMediumFont(), getInfoColor(),
                getPanelColor(), (float) getPanelAlpha());
            clockp.setOpaque(false);
            setClockPanel(clockp);

            // The JXLabels are configured at this point with their large
            // size.  So they need at least that much room to expand.  We
            // can get the size of the label to help us lay them out.
            JXLabel[] array = getLabels();
            double labelMaxWidth = 0.0;
            double labelMaxHeight = 0.0;
            for (int i = 0; i < array.length; i++) {

                Dimension fd = array[i].getPreferredSize();
                if (fd != null) {

                    if (fd.getWidth() > labelMaxWidth) {
                        labelMaxWidth = fd.getWidth();
                    }

                    if (fd.getHeight() > labelMaxHeight) {
                        labelMaxHeight = fd.getHeight();
                    }
                }
            }

            // We also have to take into account all the submenu items
            // to make sure the Label won't be too big for the popup panel.
            double popupPanelWidth = 0.0;
            String[] kidnames = getChildren();
            if (kidnames != null) {

                for (int i = 0; i < kidnames.length; i++) {

                    JXLabel tmp = new JXLabel(kidnames[i]);
                    tmp.setFont(getSmallFont());
                    Dimension fd = tmp.getPreferredSize();
                    if (fd != null) {

                        // Just need to check width...
                        if (fd.getWidth() > popupPanelWidth) {
                            popupPanelWidth = fd.getWidth();
                        }
                    }
                }
            }

            popupPanelWidth += hgap;

            // We need to compute the height of the labels that will
            // go into any popup panel so we can size things properly.
            double popupLabelHeight = SMALL_FONT_SIZE;
            JXLabel test = new JXLabel("this is a test");
            test.setFont(getSmallFont());
            Dimension testdim = test.getPreferredSize();
            if (testdim != null) {

                popupLabelHeight = testdim.getHeight();
            }

            // Compute the width of the "gray panel" where the labels
            // reside upon.  It has to be at least as wide as the largest
            // label width.
            double grayPanelWidth = width * 0.3;
            if (grayPanelWidth < labelMaxWidth) {
                grayPanelWidth = labelMaxWidth + hgap;
            }

            // The total pixels we need in height.
            double total = (array.length * labelMaxHeight)
                + ((array.length + 1) * vgap);

            // For now we support only an amount that "fit".  Worry
            // about scrolling in the future.
            if (total > height) {

                throw new RuntimeException("Can't fit buttons...");
            }

            // First define the "gray" area panel which will display
            // the selections.
            Color color = getPanelColor();
            color = new Color(color.getRed(), color.getGreen(),
                color.getBlue(), (int) (getPanelAlpha() * 255));
            JXPanel panel = new JXPanel();
            MattePainter mpainter = new MattePainter(color);
            panel.setBackgroundPainter(mpainter);
            setMattePainter(mpainter);
            panel.setAlpha((float) getPanelAlpha());
            panel.setBounds(0, 0, (int) grayPanelWidth, (int) height);
            pane.add(panel, Integer.valueOf(110));
            setBackgroundPainter(mpainter);

            // Next define the selections and any popup panels.
            double top = (height - total) / 2.0;
            for (int i = 0; i < array.length; i++) {

                array[i].setBounds((int) hgap, (int) top,
                    (int) labelMaxWidth, (int) labelMaxHeight);
                pane.add(array[i], Integer.valueOf(120));
                array[i].setFont(getMediumFont());
                array[i].setForeground(getUnselectedColor());

                TextImage[] kids = getChildren(array[i].getText());
                if (kids != null) {

                    JXLabel right = new JXLabel(getRightButtonImageIcon());
                    right.setBounds((int) (hgap + labelMaxWidth),
                        (int) top, 32, (int) labelMaxHeight);
                    pane.add(right, Integer.valueOf(120));

                    // Next is to pre-build this popup panel so we just
                    // have to do it once.  Plus we have size info here
                    // that makes it easier to do now.
                    Rectangle r = right.getBounds();
                    GridLayout gl = new GridLayout(0, 1, 4, 4);
                    JXPanel popup = new JXPanel(gl);
                    popup.setOpaque(false);
                    popup.setAlpha(0.0f);
                    popup.setBackgroundPainter(getMattePainter());
                    popup.setBounds((int) (r.getX() + 32),
                        (int) r.getY() + 32,
                        (int) popupPanelWidth,
                        (int) ((kids.length * popupLabelHeight)
                        + ((kids.length + 1) * 4)));

                    TimingTarget tt = PropertySetter.getTarget(popup, "alpha",
                        Float.valueOf(0.0f),
                        Float.valueOf((float) getPanelAlpha()));
                    Animator popupAnimator =
                        new Animator.Builder().setDuration(ATIME,
                            TimeUnit.MILLISECONDS).addTarget(tt).build();
                    addAnimator(popup, popupAnimator);

                    JXLabel[] kidlabels = new JXLabel[kids.length];
                    for (int j = 0; j < kids.length; j++) {

                        JXLabel kidlabel = new JXLabel(kids[j].getText());
                        kidlabel.setOpaque(false);
                        kidlabel.setFont(getSmallFont());
                        kidlabel.setForeground(getUnselectedColor());
                        popup.add(kidlabel);
                        kidlabels[j] = kidlabel;
                    }

                    addPanel(kids, popup);
                    addLabels(popup, kidlabels);
                    pane.add(popup, Integer.valueOf(130));
                }

                top += (labelMaxHeight + vgap);
            }

            // Display the time.
            ClockPanel cp = getClockPanel();
            if (cp != null) {

                Dimension cpdim = cp.getPreferredSize();
                if (cpdim != null) {

                    double x =
                        width - cpdim.getWidth() - hgap - ClockPanel.FUDGE;
                    cp.setBounds((int) x, (int) vgap,
                        (int) (cpdim.getWidth() + ClockPanel.FUDGE),
                        (int) cpdim.getHeight());
                    pane.add(cp, Integer.valueOf(120));
                }
            }

            JXLabel llabel = getLogoLabel();
            if (llabel != null) {

                Dimension logodim = llabel.getPreferredSize();
                if (logodim != null) {

                    double x = width - logodim.getWidth() - hgap;
                    double y = height - logodim.getHeight() - vgap;
                    llabel.setBounds((int) x, (int) y,
                        (int) logodim.getWidth(),
                        (int) logodim.getHeight());
                    pane.add(llabel, Integer.valueOf(120));
                }
            }

            // Make a background panel that makes the fade in of the
            // background image a little nicer since it has the same
            // color as the gray panel.
            JXPanel backPanel = new JXPanel();
            backPanel.setOpaque(false);
            setBackgroundPanel(backPanel);
            backPanel.setBounds(0, 0, (int) width, (int) height);
            pane.add(backPanel, Integer.valueOf(100));

            TimingTarget tt = PropertySetter.getTarget(backPanel, "alpha",
                Float.valueOf(0.0f), Float.valueOf(1.0f));
            Animator backAnimator =
                new Animator.Builder().setDuration(ATIME,
                    TimeUnit.MILLISECONDS).addTarget(tt).build();
            setBackgroundAnimator(backAnimator);

            // Start at the first item.
            setPopupPanel(null);
            setSelectedIndex(0);
        }
    }

    /**
     * Add a listener.
     *
     * @param l A given listener.
     */
    public void addActionListener(ActionListener l) {
        actionList.add(l);
    }

    /**
     * Remove a listener.
     *
     * @param l A given listener.
     */
    public void removeActionListener(ActionListener l) {
        actionList.remove(l);
    }
    /**
     * Send out an action event.
     *
     * @param event The event to propagate.
     */
    public void fireActionEvent(ActionEvent event) {
        processActionEvent(event);
    }

    protected synchronized void processActionEvent(ActionEvent event) {

        for (int i = 0; i < actionList.size(); i++) {

            ActionListener l = actionList.get(i);
            l.actionPerformed(event);
        }
    }

    /**
     * Just propogate all events to our listeners.
     *
     * @param event The event to send along.
     */
    public void actionPerformed(ActionEvent event) {
        fireActionEvent(event);
    }

    private void update(int old, int current) {

        JXLabel[] array = getLabels();
        Animator[] anis = getAnimators();
        if ((array != null) && (anis != null)) {

            if ((old >= 0) && (old < array.length)) {

                if (anis[old].isRunning()) {
                    anis[old].stop();
                }

                array[old].setForeground(getUnselectedColor());
                anis[old].startReverse();
            }

            if ((current >= 0) && (current < array.length)) {

                if (anis[current].isRunning()) {
                    anis[current].stop();
                }

                array[current].setForeground(getSelectedColor());
                anis[current].start();

                TextImage currentTextImage = getCurrentTextImage(current);
                if (currentTextImage != null) {

                    BufferedImage bi = currentTextImage.getImage();
                    if (bi != null) {

                        JXPanel backPanel = getBackgroundPanel();
                        if (backPanel != null) {

                            backPanel.setAlpha(0.0f);
                            ImagePainter p =
                                (ImagePainter) backPanel.getBackgroundPainter();
                            if (p != null) {

                                bi = ensureSize(backPanel, bi);
                                currentTextImage.setImage(bi);
                                p.setImage(bi);

                            } else {

                                bi = ensureSize(backPanel, bi);
                                currentTextImage.setImage(bi);
                                p = new ImagePainter(bi);
                                p.setScaleToFit(true);
                                backPanel.setBackgroundPainter(p);
                            }

                            Animator backAnimator = getBackgroundAnimator();
                            if (backAnimator != null) {

                                if (backAnimator.isRunning()) {
                                    backAnimator.stop();
                                }

                                backAnimator.start();
                            }
                        }
                    }
                }
            }
        }

    }

    private BufferedImage ensureSize(JXPanel p, BufferedImage bi) {

        BufferedImage result = bi;

        if ((p != null) && (bi != null)) {

            Dimension d = p.getSize();
            if (d != null) {

                if ((d.getWidth() != bi.getWidth()) && (d.getWidth() > 0)) {

                    result = Util.resize(bi, (int) d.getWidth(),
                        (int) d.getHeight());
                }
            }
        }

        return (result);
    }

    private void updatePopup(int old, int current) {

        JXPanel popup = getPopupPanel();
        if (popup != null) {

            JXLabel[] array = getLabels(popup);
            if (array != null) {

                if ((old >= 0) && (old < array.length)) {

                    array[old].setForeground(getUnselectedColor());
                }

                if ((current >= 0) && (current < array.length)) {

                    array[current].setForeground(getSelectedColor());
                }
            }
        }
    }

    private TextImage getCurrentTextImage(int index) {

        TextImage result = null;

        TextImage[] array = getTextImages();
        if (array != null) {

            // The current TextImage is not a 1:1 lookup into the array
            // of TextImage instances we have.  It is only the "root" or
            // "parent" TextImage instances we have to look for.
            for (int i = 0; i < array.length; i++) {

                if (array[i].getParentTextImage() == null) {

                    if (index == 0) {

                        result = array[i];
                        break;
                    }

                    index--;
                }
            }
        }

        return (result);
    }

    private JXLabel getSelectedLabel() {

        JXLabel result = null;

        JXLabel[] array = getLabels();
        if (array != null) {

            int index = getSelectedIndex();
            if ((index >= 0) && (index < array.length)) {

                result = array[index];
            }
        }

        return (result);
    }

    private TextImage[] getSelectedChildren() {

        TextImage[] result = null;

        JXLabel[] array = getLabels();
        if (array != null) {

            int index = getSelectedIndex();
            if ((index >= 0) && (index < array.length)) {

                JXLabel l = array[index];
                if (l != null) {

                    result = getChildren(l.getText());
                }
            }
        }

        return (result);
    }

    private void activatePopup() {

        TextImage[] kids = getSelectedChildren();
        JLayeredPane pane = getLayeredPane();
        if ((kids != null) && (pane != null)) {

            JXPanel popup = getPanel(kids);
            if (popup != null) {

                setPopupPanel(popup);
                JXLabel[] labs = getLabels(popup);
                if (labs != null) {

                    for (int i = 0; i < labs.length; i++) {

                        labs[i].setForeground(getUnselectedColor());
                    }
                }

                setPopupSelectedIndex(0);
                Animator a = getAnimator(popup);
                if (a != null) {

                    if (a.isRunning()) {
                        a.stop();
                    }

                    a.start();
                }
            }
        }
    }

    /**
     * Deactivate the current popup.
     */
    public void deactivatePopup() {

        JXPanel popup = getPopupPanel();
        if (popup != null) {

            JLayeredPane pane = getLayeredPane();
            if (pane != null) {

                setPopupPanel(null);
                setPopupSelectedIndex(-1);
                Animator a = getAnimator(popup);
                if (a != null) {

                    if (a.isRunning()) {
                        a.stop();
                    }

                    a.startReverse();
                }
            }
        }
    }

    /**
     * The panel is either currently showing a popup menu or it is not.
     *
     * @return True is Popup menu is being displayed.
     */
    public boolean isPopup() {
        return (getPopupPanel() != null);
    }

    private boolean isUp() {

        boolean result = false;

        if (isPopup()) {

            JXPanel popup = getPopupPanel();
            if (popup != null) {

                JXLabel[] array = getLabels(popup);
                if (array != null) {

                    int index = getPopupSelectedIndex() - 1;
                    result = (index >= 0);
                }
            }

        } else {

            JXLabel[] array = getLabels();
            if (array != null) {

                int index = getSelectedIndex() - 1;
                result = (index >= 0);
            }
        }

        return (result);
    }

    private boolean isDown() {

        boolean result = false;

        if (isPopup()) {

            JXPanel popup = getPopupPanel();
            if (popup != null) {

                JXLabel[] array = getLabels(popup);
                if (array != null) {

                    int index = getPopupSelectedIndex() + 1;
                    result = (index < array.length);
                }
            }

        } else {

            JXLabel[] array = getLabels();
            if (array != null) {

                int index = getSelectedIndex() + 1;
                result = (index < array.length);
            }
        }

        return (result);
    }

    private boolean isRight() {

        boolean result = false;

        if (!isPopup()) {

            JXLabel[] array = getLabels();
            if (array != null) {

                int index = getSelectedIndex();
                if ((index >= 0) && (index < array.length)) {

                    JXLabel l = array[index];
                    if (l != null) {

                        TextImage[] kids = getChildren(l.getText());
                        result = (kids != null) && (getPopupPanel() == null);
                    }
                }
            }
        }

        return (result);
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            deactivatePopup();
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isRight()) {

                activatePopup();
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isUp()) {

                if (isPopup()) {
                    setPopupSelectedIndex(getPopupSelectedIndex() - 1);
                } else {
                    setSelectedIndex(getSelectedIndex() - 1);
                }

            } else {

                if (isPopup()) {

                    JXPanel popup = getPopupPanel();
                    if (popup != null) {

                        JXLabel[] array = getLabels(popup);
                        if (array != null) {

                            setPopupSelectedIndex(array.length - 1);
                        }
                    }

                } else {

                    JXLabel[] array = getLabels();
                    if (array != null) {

                        setSelectedIndex(array.length - 1);
                    }
                }
            }
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isDown()) {

                if (isPopup()) {
                    setPopupSelectedIndex(getPopupSelectedIndex() + 1);
                } else {
                    setSelectedIndex(getSelectedIndex() + 1);
                }

            } else {

                if (isPopup()) {
                    setPopupSelectedIndex(0);
                } else {
                    setSelectedIndex(0);
                }
            }
        }
    }

    class EnterAction extends AbstractAction {

        private TextImagePanel textImagePanel;

        public EnterAction(TextImagePanel tip) {

            textImagePanel = tip;
        }

        public void actionPerformed(ActionEvent e) {

            JXLabel[] array = getLabels();
            if (array != null) {

                int index = getSelectedIndex();
                if ((index >= 0) && (index < array.length)) {

                    TextImage[] kids = getChildren(array[index].getText());
                    if (kids != null) {

                        // We have a parent selection item.  Check first for
                        // a popup activated.
                        JXPanel popup = getPopupPanel();
                        if (popup != null) {

                            // Ok it's visible...
                            JXLabel[] labs = getLabels(popup);
                            if (labs != null) {

                                int cindex = getPopupSelectedIndex();
                                if ((cindex >= 0) && (cindex < labs.length)) {

                                    String s = array[index].getText()
                                        + ":" + labs[cindex].getText();
                                    ActionEvent event =
                                        new ActionEvent(textImagePanel, 0, s);
                                    fireActionEvent(event);
                                }
                            }

                        } else {

                            // They have selected a parent that has kids
                            // but the kids are not visible.  Let's make
                            // them visible.
                            activatePopup();
                        }

                    } else {

                        // Just a basic selection item.
                        ActionEvent event = new ActionEvent(textImagePanel, 0,
                            array[index].getText());
                        fireActionEvent(event);
                    }
                }
            }
        }
    }

}
