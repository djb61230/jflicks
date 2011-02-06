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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * This is a display of a selection of actions for the user.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ButtonPanel extends BaseCustomizePanel {

    private BufferedImage bufferedImage;
    private TextListPanel textListPanel;
    private ArrayList<ActionListener> actionList =
        new ArrayList<ActionListener>();

    /**
     * Simple empty constructor.
     */
    public ButtonPanel() {

        TextListPanel tlp = new TextListPanel();
        tlp.setMediumFont(tlp.getLargeFont());
        tlp.setSmallFont(tlp.getLargeFont());
        setTextListPanel(tlp);

        EscapeAction escapeAction = new EscapeAction();
        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        getActionMap().put("escape", escapeAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);

        setAlpha(0.0f);

        RectanglePainter rp = new RectanglePainter(getPanelColor(),
            Color.BLACK);
        rp.setRounded(true);
        rp.setRoundWidth(30);
        rp.setRoundHeight(30);
        rp.setBorderWidth(5.0f);
        CompoundPainter cp = new CompoundPainter(rp);
        setBackgroundPainter(cp);
    }

    private TextListPanel getTextListPanel() {
        return (textListPanel);
    }

    private void setTextListPanel(TextListPanel p) {
        textListPanel = p;
    }

    /**
     * We support displaying an image in our background.  We choose
     * to display the image on the lower right side so keep this in
     * mind when setting the image.  It will also be inset by 10 pixels.
     *
     * @return A BufferedImage instance.
     */
    public BufferedImage getBufferedImage() {
        return (bufferedImage);
    }

    /**
     * We support displaying an image in our background.  We choose
     * to display the image on the lower right side so keep this in
     * mind when setting the image.  It will also be inset by 10 pixels.
     *
     * @param bi A BufferedImage instance.
     */
    public void setBufferedImage(BufferedImage bi) {

        CompoundPainter cp = (CompoundPainter) getBackgroundPainter();
        if (cp != null) {

            Painter[] array = cp.getPainters();
            if (bi != null) {

                if (array.length == 1) {

                    // We need to add an ImagePainter...
                    ImagePainter ip = new ImagePainter(bi,
                        HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM);
                    ip.setInsets(new Insets(10, 10, 10, 10));
                    cp.setPainters(array[0], ip);

                } else {

                    // We should have two painters and we just update
                    // the image in the second.
                    ImagePainter ip = (ImagePainter) array[1];
                    ip.setImage(bi);
                }

            } else {

                // We may have to take out our ImagePainter....
                if (array.length == 2) {
                    cp.setPainters(array[0]);
                }
            }
        }

        bufferedImage = bi;
    }

    private int getImageHeight() {

        int result = 0;

        BufferedImage bi = getBufferedImage();
        if (bi != null) {

            result = bi.getHeight();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setControl(boolean b) {

        super.setControl(b);
        TextListPanel p = getTextListPanel();
        if (p != null) {

            p.setControl(b);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {

        TextListPanel p = getTextListPanel();
        if (p != null) {

            p.performControl();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            float alpha = (float) getPanelAlpha();

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            TextListPanel p = getTextListPanel();
            if (p != null) {

                d = p.getPreferredSize();
                int pwidth = (int) d.getWidth();
                int pheight = (int) d.getHeight();
                int x = (width - pwidth) / 2;
                int y = (height - (pheight + getImageHeight())) / 2;
                p.setBounds(x, y, pwidth, pheight);
                pane.add(p, Integer.valueOf(100));

                Animator fadein = PropertySetter.createAnimator(300,
                    this, "alpha", 0.0f, (float) getPanelAlpha());
                fadein.start();
            }
        }
    }

    /**
     * Override so we can supply a reasonable size needed.  Be aware that
     * this should be called after you have set the BufferedImage and button
     * selection values as the size we need are depe=ndent on them.
     *
     * @return A Dimension instance.
     */
    public Dimension getPreferredSize() {

        int width = 0;
        int height = 0;

        BufferedImage bi = getBufferedImage();
        if (bi != null) {

            if (width < bi.getWidth()) {
                width = bi.getWidth();
            }

            if (height < bi.getHeight()) {
                height = bi.getHeight();
            }
        }

        String[] array = getButtons();
        if (array != null) {

            int tmp = (int) getMaxWidth(array);
            if (tmp > width) {

                width = tmp;
            }

            // Add to the height as our image goes on the bottom.
            height += (int) (getMaxHeight() * (array.length + 1));
        }

        return (new Dimension(width + 50, height + 50));
    }

    private double getMaxWidth(String[] array) {

        double result = 0.0;

        TextListPanel p = getTextListPanel();
        if (p != null) {

            result = p.getMaxWidth(array);
        }

        return (result);
    }

    private double getMaxHeight() {

        double result = 0.0;

        TextListPanel p = getTextListPanel();
        if (p != null) {

            result = p.getMaxHeight();
        }

        return (result);
    }

    /**
     * We pass on requests to move up to a TextListPanel we are using
     * to delegate this behavior.
     */
    public void moveUp() {

        TextListPanel p = getTextListPanel();
        if (p != null) {

            p.moveUp();
        }
    }

    /**
     * We pass on requests to move down to a TextListPanel we are using
     * to delegate this behavior.
     */
    public void moveDown() {

        TextListPanel p = getTextListPanel();
        if (p != null) {

            p.moveDown();
        }
    }

    /**
     * We list button names in our panel.
     *
     * @return An array of String instances.
     */
    public String[] getButtons() {

        String[] result = null;

        TextListPanel p = getTextListPanel();
        if (p != null) {

            result = p.getTexts();
        }

        return (result);
    }

    /**
     * We list button names in our panel.
     *
     * @param array An array of String instances.
     */
    public void setButtons(String[] array) {

        TextListPanel p = getTextListPanel();
        if (p != null) {

            p.setTexts(array);
        }
    }

    /**
     * Convenience method to return the selected object as a String instance.
     *
     * @return A String instance.
     */
    public String getSelectedButton() {

        String result = null;

        TextListPanel p = getTextListPanel();
        if (p != null) {

            result = p.getSelectedText();
        }

        return (result);
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

    class EscapeAction extends AbstractAction {

        public EscapeAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TextListPanel p = getTextListPanel();
            if (p != null) {

                String[] array = p.getTexts();
                if (array != null) {

                    String val = array[array.length - 1];
                    p.setSelectedObject(val);
                    fireActionEvent(new ActionEvent(ButtonPanel.this, 1, val));
                }
            }
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            TextListPanel p = getTextListPanel();
            if (p != null) {

                String[] array = p.getTexts();
                if ((array != null) && (array.length > 0)) {

                    String val = p.getSelectedText();
                    fireActionEvent(new ActionEvent(ButtonPanel.this, 1, val));
                }
            }
        }
    }

}

