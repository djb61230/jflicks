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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JLayeredPane;

import org.jflicks.log.Log;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Base class that implements the Customize properties and some basic stuff
 * that all UI extensions need to do.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseCustomizePanel extends JXPanel implements Customize,
    Log {

    private ServiceTracker logServiceTracker;
    private double smallFontSize;
    private double mediumFontSize;
    private double largeFontSize;
    private Font smallFont;
    private Font mediumFont;
    private Font largeFont;
    private String smallFontFamily;
    private String mediumFontFamily;
    private String largeFontFamily;
    private int smallFontStyle;
    private int mediumFontStyle;
    private int largeFontStyle;
    private Color unselectedColor;
    private Color selectedColor;
    private Color highlightColor;
    private Color infoColor;
    private Color panelColor;
    private double panelAlpha;
    private boolean effects;

    private JLayeredPane layeredPane;
    private boolean layoutDone;
    private boolean control;

    /**
     * An extension needs to wait until it's real size has been determined
     * before it can layout it's components.  All extensions will use the
     * LayeredPane property to place all of it's components.
     *
     * @param size The real size of the component.
     */
    public abstract void performLayout(Dimension size);

    /**
     * An extension might need to perform some tasks when it's Control
     * property changes.
     */
    public abstract void performControl();

    /**
     * Simple empty constructor.
     */
    public BaseCustomizePanel() {

        initialize();
    }

    private void initialize() {

        // First we set defaults to stuff we like :)
        setUnselectedColor(UNSELECTED_COLOR);
        setSelectedColor(SELECTED_COLOR);
        setHighlightColor(HIGHLIGHT_COLOR);
        setInfoColor(INFO_COLOR);
        setPanelColor(PANEL_COLOR);
        setPanelAlpha(PANEL_ALPHA);
        setSmallFontSize(SMALL_FONT_SIZE);
        setMediumFontSize(MEDIUM_FONT_SIZE);
        setLargeFontSize(LARGE_FONT_SIZE);
        setSmallFontFamily(SMALL_FONT_FAMILY);
        setMediumFontFamily(MEDIUM_FONT_FAMILY);
        setLargeFontFamily(LARGE_FONT_FAMILY);
        setSmallFontStyle(SMALL_FONT_STYLE);
        setMediumFontStyle(MEDIUM_FONT_STYLE);
        setLargeFontStyle(LARGE_FONT_STYLE);
        setEffects(true);

        // Load up the current theme preferences.
        loadCurrent();

        // At this point we compute our fonts...
        setSmallFont(new Font(getSmallFontFamily(), getSmallFontStyle(),
            (int) getSmallFontSize()));
        setMediumFont(new Font(getMediumFontFamily(), getMediumFontStyle(),
            (int) getMediumFontSize()));
        setLargeFont(new Font(getLargeFontFamily(), getLargeFontStyle(),
            (int) getLargeFontSize()));

        // Apply our transparency to our panel color.
        Color panelc = getPanelColor();
        panelc = new Color(panelc.getRed(), panelc.getGreen(),
            panelc.getBlue(), (int) (getPanelAlpha() * 255));
        setPanelColor(panelc);

        setOpaque(false);

        JLayeredPane pane = new JLayeredPane();
        setLayeredPane(pane);

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    /**
     * {@inheritDoc}
     */
    public void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
    }

    /**
     * {@inheritDoc}
     */
    public void log(int level, String message) {

        ServiceTracker st = getLogServiceTracker();
        if ((st != null) && (message != null)) {

            LogService ls = (LogService) st.getService();
            if (ls != null) {

                ls.log(level, message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEffects() {
        return (effects);
    }

    /**
     * {@inheritDoc}
     */
    public void setEffects(boolean b) {
        effects = b;
    }

    /**
     * {@inheritDoc}
     */
    public Font getSmallFont() {
        return (smallFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFont(Font f) {
        smallFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public Font getMediumFont() {
        return (mediumFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFont(Font f) {
        mediumFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public Font getLargeFont() {
        return (largeFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFont(Font f) {
        largeFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public double getSmallFontSize() {
        return (smallFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontSize(double d) {
        smallFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public double getMediumFontSize() {
        return (mediumFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontSize(double d) {
        mediumFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public double getLargeFontSize() {
        return (largeFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontSize(double d) {
        largeFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public String getSmallFontFamily() {
        return (smallFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontFamily(String s) {
        smallFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getMediumFontFamily() {
        return (mediumFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontFamily(String s) {
        mediumFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getLargeFontFamily() {
        return (largeFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontFamily(String s) {
        largeFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public int getSmallFontStyle() {
        return (smallFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontStyle(int i) {
        smallFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public int getMediumFontStyle() {
        return (mediumFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontStyle(int i) {
        mediumFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public int getLargeFontStyle() {
        return (largeFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontStyle(int i) {
        largeFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public Color getUnselectedColor() {
        return (unselectedColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setUnselectedColor(Color c) {
        unselectedColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getSelectedColor() {
        return (selectedColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedColor(Color c) {
        selectedColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getHighlightColor() {
        return (highlightColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setHighlightColor(Color c) {
        highlightColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getInfoColor() {
        return (infoColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setInfoColor(Color c) {
        infoColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getPanelColor() {
        return (panelColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelColor(Color c) {
        panelColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public double getPanelAlpha() {
        return (panelAlpha);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelAlpha(double d) {
        panelAlpha = d;
    }

    protected JLayeredPane getLayeredPane() {
        return (layeredPane);
    }

    protected void setLayeredPane(JLayeredPane p) {
        layeredPane = p;
    }

    protected boolean isLayoutDone() {
        return (layoutDone);
    }

    protected void setLayoutDone(boolean b) {
        layoutDone = b;
    }

    private void layoutComponents() {

        JLayeredPane pane = getLayeredPane();
        if ((!isLayoutDone()) && (pane != null)) {

            performLayout(getSize());
            setLayoutDone(true);
        }
    }

    /**
     * Flag our layout as invalid, remove all components from our pane and
     * then restart a fresh layout.
     */
    public void restartUI() {

        setLayoutDone(false);
        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            pane.removeAll();
            initialize();
            layoutComponents();
        }
    }

    /**
     * Override to layout our components after we know the size of
     * our screen.  This way we will look the same regardless of
     * the screen size - 720p, 1080i/p  or some other computer
     * resolution.
     *
     * @param x The start X.
     * @param y The start Y.
     * @param width The width in pixels.
     * @param height The height in pixels.
     */
    public void setBounds(int x, int y, int width, int height) {

        super.setBounds(x, y, width, height);
        layoutComponents();
    }

    /**
     * When this panel in control it might behave differently.  Control
     * is similar to "focus".
     *
     * @return True if in control.
     */
    public boolean isControl() {
        return (control);
    }

    /**
     * When this panel in control it might behave differently.  Control
     * is similar to "focus".
     *
     * @param b True if in control.
     */
    public void setControl(boolean b) {

        control = b;
        performControl();
    }

    /**
     * Convenience method so extensions can find the theme directory.
     *
     * @return A File instance.
     */
    public File getThemeDirectory() {

        File result = null;

        File home = new File(".");
        File theme = new File(home, "theme");
        if ((theme.exists()) && (theme.isDirectory())) {

            result = theme;
        }

        return (result);
    }

    /**
     * Convenience method so extensions can find the current theme title.
     *
     * @return A String instance.
     */
    public String getCurrentThemeTitle() {

        String result = null;

        File f = getCurrentThemeDirectory();
        if (f != null) {

            result = f.getName();
        }

        return (result);
    }

    /**
     * Convenience method to write out the current theme title.  A directory
     * of the same name should exists in the theme directory.
     *
     * @param s The theme title name.
     */
    public void setCurrentThemeTitle(String s) {

        File f = getThemeDirectory();
        if ((s != null) && (f != null)) {

            FileWriter fw = null;
            try {

                Properties p = new Properties();
                p.setProperty("current", s);

                File prop = new File(f, "current.properties");
                fw = new FileWriter(prop);
                p.store(fw, "Updated by FE");
                fw.close();
                fw = null;

            } catch (IOException ex) {

                log(WARNING, ex.getMessage());

            } finally {

                try {

                    if (fw != null) {

                        fw.close();
                        fw = null;
                    }

                } catch (IOException ex) {

                    fw = null;
                }
            }
        }
    }

    /**
     * Convenience method so extensions can find the current theme directory.
     *
     * @return A File instance.
     */
    public File getCurrentThemeDirectory() {

        File result = null;

        File home = new File(".");
        File theme = new File(home, "theme");
        if ((theme.exists()) && (theme.isDirectory())) {

            FileReader fr = null;
            try {

                File prop = new File(theme, "current.properties");
                if ((prop.exists()) && (prop.isFile())) {

                    Properties p = new Properties();
                    fr = new FileReader(prop);
                    p.load(fr);
                    fr.close();
                    fr = null;

                    String currentDir = p.getProperty("current");
                    if (currentDir == null) {
                        currentDir = "dino";
                    }

                    result = new File(theme, currentDir);

                } else {

                    result = new File(theme, "dino");
                }

            } catch (IOException ex) {
            } finally {

                if (fr != null) {

                    try {

                        fr.close();
                        fr = null;

                    } catch (IOException ex) {

                        fr = null;
                    }
                }
            }
        }

        return (result);
    }

    private void loadCurrent() {

        File current = getCurrentThemeDirectory();
        if ((current.exists()) && (current.isDirectory())) {

            // Now load up any defaults that the user wants over our
            // hard-wired ones.  These are their preferences for
            // and only will be overrided by specific classes.
            loadProperties(current, "default.properties");

            // Next load an override properties (if it exists of
            // course) for this particular extension.  The file
            // name must be "classname.properties".
            loadProperties(current, getClass().getName() + ".properties");
        }
    }

    /**
     * Allow extensions to load whatever properties file they desire.
     *
     * @param dir A directory to look in.
     * @param name A file name to use.
     */
    public void loadProperties(File dir, String name) {

        if ((dir.exists()) && (dir.isDirectory())) {

            FileReader fr = null;
            try {

                File prop = new File(dir, name);
                if ((prop.exists()) && (prop.isFile())) {

                    Properties p = new Properties();
                    fr = new FileReader(prop);
                    p.load(fr);
                    fr.close();
                    fr = null;

                    setUnselectedColor(Util.str2Color(p.getProperty(
                        "UnselectedColor"), getUnselectedColor()));
                    setSelectedColor(Util.str2Color(p.getProperty(
                        "SelectedColor"), getSelectedColor()));
                    setHighlightColor(Util.str2Color(p.getProperty(
                        "HighlightColor"), getHighlightColor()));
                    setInfoColor(Util.str2Color(p.getProperty(
                        "InfoColor"), getInfoColor()));
                    setPanelColor(Util.str2Color(p.getProperty(
                        "PanelColor"), getPanelColor()));
                    setPanelAlpha(Util.str2double(p.getProperty(
                        "PanelAlpha"), getPanelAlpha()));
                    setSmallFontSize(Util.str2double(p.getProperty(
                        "SmallFontSize"), getSmallFontSize()));
                    setMediumFontSize(Util.str2double(p.getProperty(
                        "MediumFontSize"), getMediumFontSize()));
                    setLargeFontSize(Util.str2double(p.getProperty(
                        "LargeFontSize"), getLargeFontSize()));

                    String tmp = p.getProperty("SmallFontFamily");
                    if (tmp != null) {
                        setSmallFontFamily(tmp);
                    }

                    tmp = p.getProperty("MediumFontFamily");
                    if (tmp != null) {
                        setMediumFontFamily(tmp);
                    }

                    tmp = p.getProperty("LargeFontFamily");
                    if (tmp != null) {
                        setLargeFontFamily(tmp);
                    }

                    setSmallFontStyle(Util.str2FontStyle(p.getProperty(
                        "SmallFontStyle"), getSmallFontStyle()));
                    setMediumFontStyle(Util.str2FontStyle(p.getProperty(
                        "MediumFontStyle"), getMediumFontStyle()));
                    setLargeFontStyle(Util.str2FontStyle(p.getProperty(
                        "LargeFontStyle"), getLargeFontStyle()));
                }

            } catch (IOException ex) {

                log(WARNING, ex.getMessage());

            } finally {

                try {

                    if (fr != null) {

                        fr.close();
                        fr = null;
                    }

                } catch (IOException ex) {

                    fr = null;
                }
            }
        }
    }

}

