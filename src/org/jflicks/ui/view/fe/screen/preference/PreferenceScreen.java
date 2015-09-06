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
package org.jflicks.ui.view.fe.screen.preference;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.LabelPanel;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.TextIcon;
import org.jflicks.ui.view.fe.Theme;
import org.jflicks.ui.view.fe.ThemeDetailPanel;
import org.jflicks.ui.view.fe.ThemeListPanel;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.ExtensionsFilter;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This class supports setting user preferences in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PreferenceScreen extends Screen implements ParameterProperty,
    PropertyChangeListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.05;
    private static final int SHRINK = 100;

    private static final String THEME = "Theme";

    private String[] parameters;
    private String selectedParameter;
    private boolean updatedParameter;
    private ThemeListPanel themeListPanel;
    private LabelPanel labelPanel;
    private ThemeDetailPanel themeDetailPanel;

    /**
     * Simple empty constructor.
     */
    public PreferenceScreen() {

        setTitle("Preference");
        BufferedImage bi = getImageByName("Preference");
        setDefaultBackgroundImage(bi);

        setFocusable(true);
        requestFocus();

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

        PageUpAction pageUpAction = new PageUpAction();
        map.put(KeyStroke.getKeyStroke("PAGE_UP"), "pageup");
        getActionMap().put("pageup", pageUpAction);

        PageDownAction pageDownAction = new PageDownAction();
        map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "pagedown");
        getActionMap().put("pagedown", pageDownAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);

        String[] array = {

            THEME
        };

        setParameters(array);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameters() {

        String[] result = null;

        if (parameters != null) {

            result = Arrays.copyOf(parameters, parameters.length);
        }

        return (result);
    }

   private void setParameters(String[] array) {

        if (array != null) {
            parameters = Arrays.copyOf(array, array.length);
        } else {
            parameters = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectedParameter() {
        return (selectedParameter);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedParameter(String s) {

        if ((s != null) && (selectedParameter != null)) {
            setUpdatedParameter(!s.equals(selectedParameter));
        } else {
            setUpdatedParameter(true);
        }

        selectedParameter = s;
    }

    private boolean isUpdatedParameter() {
        return (updatedParameter);
    }

    private void setUpdatedParameter(boolean b) {
        updatedParameter = b;
    }

    private boolean isParameterTheme() {
        return (THEME.equals(getSelectedParameter()));
    }

    private ThemeListPanel getThemeListPanel() {
        return (themeListPanel);
    }

    private void setThemeListPanel(ThemeListPanel p) {
        themeListPanel = p;
    }

    private LabelPanel getLabelPanel() {
        return (labelPanel);
    }

    private void setLabelPanel(LabelPanel p) {
        labelPanel = p;
    }

    private ThemeDetailPanel getThemeDetailPanel() {
        return (themeDetailPanel);
    }

    private void setThemeDetailPanel(ThemeDetailPanel p) {
        themeDetailPanel = p;
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
    }

    /**
     * {@inheritDoc}
     */
    public void commandReceived(String command) {
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            if (isParameterTheme()) {

                ThemeListPanel tlp = getThemeListPanel();
                if (tlp != null) {

                    tlp.setThemes(getThemes());
                }
            }

            updateLayout();
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

            int wspan = (int) (width * 0.03);
            int half = (int) ((width - (3 * wspan)) / 2);
            int listwidth = half / 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (3 * hspan)) / 1.5);

            int labelpanelwidth = (int) (listheight * 1.78);

            int detailwidth = (int) (width - (2 * wspan));
            int detailheight = (height - (4 * hspan)) - listheight;

            ThemeListPanel tlp = new ThemeListPanel();
            tlp.setControl(true);
            tlp.addPropertyChangeListener("SelectedTheme", this);
            tlp.setBounds(wspan, hspan, listwidth, listheight);
            setThemeListPanel(tlp);

            LabelPanel lp = new LabelPanel(1, "Images");
            lp.setSelectedHighlighted(false);
            lp.setControl(false);
            lp.setAspectRatio(1.78);
            lp.setBounds(wspan + wspan + listwidth, hspan, labelpanelwidth,
                listheight);
            setLabelPanel(lp);

            ThemeDetailPanel tdp = new ThemeDetailPanel();
            tdp.setAlpha(alpha);
            tdp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
                detailheight);
            setThemeDetailPanel(tdp);

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));
        }
    }

    private void updateLayout() {

        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            pane.removeAll();
            if (isParameterTheme()) {

                pane.add(getThemeListPanel(), Integer.valueOf(100));
                pane.add(getLabelPanel(), Integer.valueOf(100));
                pane.add(getThemeDetailPanel(), Integer.valueOf(100));
            }

            repaint();
        }
    }

    private ImageIcon getNoScreenImageIcon() {

        ImageIcon result = null;

        LabelPanel lp = getLabelPanel();
        if (lp != null) {

            try {

                BufferedImage bi =
                    ImageIO.read(getClass().getResource("noscreen.png"));
                if (bi != null) {

                    bi = Util.resize(bi, lp.getLabelWidth() - SHRINK,
                        lp.getLabelHeight() - SHRINK);
                    result = new ImageIcon(bi);
                }

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }

        return (result);
    }

    private ImageIcon getImageIcon(File f) {

        ImageIcon result = null;

        LabelPanel lp = getLabelPanel();
        if ((lp != null) && (f != null) && (f.exists()) && (f.isFile())) {

            try {

                BufferedImage bi = ImageIO.read(f);
                if (bi != null) {

                    bi = Util.resize(bi, lp.getLabelWidth() - SHRINK,
                        lp.getLabelHeight() - SHRINK);
                    result = new ImageIcon(bi);
                }

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }

        return (result);
    }

    private Theme[] getThemes() {

        Theme[] result = null;

        File theme = getThemeDirectory();
        if (theme != null) {

            File[] files = theme.listFiles();
            if (files != null) {

                ArrayList<TextIcon> tilist = new ArrayList<TextIcon>();
                ArrayList<Theme> list = new ArrayList<Theme>();
                for (int i = 0; i < files.length; i++) {

                    if (files[i].isDirectory()) {

                        Theme t = new Theme();
                        t.setTitle(files[i].getName());
                        File desc = new File(files[i], "description.txt");
                        if ((desc.exists()) && (desc.isFile())) {

                            String[] lines = Util.readTextFile(desc);
                            if (lines != null) {

                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < lines.length; j++) {

                                    sb.append(lines[j]);
                                    sb.append(" ");
                                }

                                t.setDescription(sb.toString().trim());
                            }
                        }

                        // Setup our TextImage instances.
                        tilist.clear();

                        String[] exts = {
                            "png"
                        };
                        ExtensionsFilter ef = new ExtensionsFilter(exts);
                        File[] images = files[i].listFiles(ef);
                        if ((images != null) && (images.length > 0)) {

                            for (int j = 0; j < images.length; j++) {

                                ImageIcon ii = getImageIcon(images[j]);
                                if (ii != null) {

                                    String iname = images[j].getName();
                                    int index = iname.lastIndexOf(".");
                                    if (index != -1) {
                                        iname = iname.substring(0, index);
                                    }

                                    TextIcon ti = new TextIcon(iname, ii);
                                    tilist.add(ti);
                                }
                            }

                        } else {

                            TextIcon ti = new TextIcon("No Screens",
                                getNoScreenImageIcon());
                            tilist.add(ti);
                        }

                        if (tilist.size() > 0) {

                            t.setTextIcons(tilist.toArray(
                                new TextIcon[tilist.size()]));
                        }

                        PropertyInfo pi = new PropertyInfo();
                        pi.loadProperties(files[i], "default.properties");

                        t.setHighlightColor(pi.getHighlightColor());
                        t.setInfoColor(pi.getInfoColor());
                        t.setSelectedColor(pi.getSelectedColor());
                        t.setUnselectedColor(pi.getUnselectedColor());
                        t.setPanelColor(pi.getPanelColor());
                        t.setPanelAlpha(pi.getPanelAlpha());
                        t.setSmallFontSize(pi.getSmallFontSize());
                        t.setMediumFontSize(pi.getMediumFontSize());
                        t.setLargeFontSize(pi.getLargeFontSize());
                        t.setSmallFont(pi.getSmallFont());
                        t.setMediumFont(pi.getMediumFont());
                        t.setLargeFont(pi.getLargeFont());
                        t.setSmallFontFamily(pi.getSmallFontFamily());
                        t.setMediumFontFamily(pi.getMediumFontFamily());
                        t.setLargeFontFamily(pi.getLargeFontFamily());
                        t.setSmallFontStyle(pi.getSmallFontStyle());
                        t.setMediumFontStyle(pi.getMediumFontStyle());
                        t.setLargeFontStyle(pi.getLargeFontStyle());

                        list.add(t);
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new Theme[list.size()]);
                    Arrays.sort(result);
                }
            }
        }

        return (result);
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getPropertyName().equals("SelectedTheme")) {

            ThemeDetailPanel tdp = getThemeDetailPanel();
            LabelPanel lp = getLabelPanel();
            if ((tdp != null) && (lp != null)) {

                Theme t = (Theme) event.getNewValue();
                tdp.setTheme(t);
                if (t != null) {

                    lp.setTextIcons(t.getTextIcons());
                }
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if (tlp != null) {
                tlp.setControl(true);
            }

            LabelPanel lp = getLabelPanel();
            if (lp != null) {
                lp.setControl(false);
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if (tlp != null) {
                tlp.setControl(false);
            }

            LabelPanel lp = getLabelPanel();
            if (lp != null) {
                lp.setControl(true);
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if ((tlp != null) && (tlp.isControl())) {
                tlp.moveUp();
            }

            LabelPanel lp = getLabelPanel();
            if ((lp != null) && (lp.isControl())) {
                lp.prev();
            }
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if ((tlp != null) && (tlp.isControl())) {
                tlp.moveDown();
            }

            LabelPanel lp = getLabelPanel();
            if ((lp != null) && (lp.isControl())) {
                lp.next();
            }
        }
    }

    class PageUpAction extends AbstractAction {

        public PageUpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if ((tlp != null) && (tlp.isControl())) {
                tlp.movePageUp();
            }

            LabelPanel lp = getLabelPanel();
            if ((lp != null) && (lp.isControl())) {
                lp.prev();
            }
        }
    }

    class PageDownAction extends AbstractAction {

        public PageDownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            ThemeListPanel tlp = getThemeListPanel();
            if ((tlp != null) && (tlp.isControl())) {
                tlp.movePageDown();
            }

            LabelPanel lp = getLabelPanel();
            if ((lp != null) && (lp.isControl())) {
                lp.next();
            }
        }
    }

    class EnterAction extends AbstractAction implements JobListener {

        public EnterAction() {
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                //setDone(true);
            }
        }

        public void actionPerformed(ActionEvent e) {

            String title = getCurrentThemeTitle();
            ThemeListPanel tlp = getThemeListPanel();
            if ((title != null) && (tlp != null)) {

                Theme theme = tlp.getSelectedTheme();
                if (theme != null) {

                    String newtitle = theme.getTitle();
                    if (!title.equals(newtitle)) {

                        if (getView() instanceof FrontEndView) {

                            setCurrentThemeTitle(newtitle);
                            FrontEndView few = (FrontEndView) getView();
                            few.restartUI();
                            setDone(true);
                        }
                    }
                }
            }
        }
    }

}
