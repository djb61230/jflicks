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
package org.jflicks.metadata.thetvdb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.metadata.Hit;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchPanel;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Banner;
import com.moviejukebox.thetvdb.model.Episode;
import com.moviejukebox.thetvdb.model.Series;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 * This panel simply allows the user to search thetvdb.com and acquire
 * images.
 *
 * @author Doug Barnum
 * @version 1.0 - 28 Dec 09
 */
public class TheTVDBSearch extends SearchPanel implements ActionListener,
    ListSelectionListener, JobListener {

    private static final String KEY = "DD342FB4D55DF7BB";

    private TheTVDB theTVDB;

    private JTextField searchTextField;
    private JButton searchButton;
    private JList seriesList;
    private JList imageList;
    private JSpinner seasonSpinner;
    private JSpinner episodeSpinner;
    private JButton episodeButton;
    private SeriesInfo seriesInfo;
    private JComboBox bannerComboBox;
    private JComboBox posterComboBox;
    private JComboBox fanartComboBox;
    private JXPanel thumbPanel;
    private BufferedImage previewBufferedImage;
    private JButton applyButton;
    private HashMap<String, BufferedImage> hashMap;
    private String searchTerms;
    private Banner[] banners;

    /**
     * Simple constructor.
     */
    public TheTVDBSearch() {

        setTheTVDB(new TheTVDB(KEY));
        setHashMap(new HashMap<String, BufferedImage>());

        JTextField tf = new JTextField(16);
        tf.addActionListener(this);
        setSearchTextField(tf);

        JButton b = new JButton("Search");
        b.addActionListener(this);
        setSearchButton(b);

        JList l = new JList();
        l.setPrototypeCellValue("01234567890123456789012345678901234567");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(5);
        l.addListSelectionListener(this);
        l.setCellRenderer(new SeriesCellRenderer());
        setSeriesList(l);
        JScrollPane serieslistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel seriesPanel = new JPanel(new BorderLayout());
        seriesPanel.setBorder(BorderFactory.createTitledBorder("Series Found"));
        seriesPanel.add(serieslistScroller, BorderLayout.CENTER);

        JList imagel = new JList();
        imagel.setPrototypeCellValue("01234567890123456789012345678901234567");
        imagel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagel.setVisibleRowCount(5);
        imagel.addListSelectionListener(this);
        imagel.setCellRenderer(new BannerCellRenderer());
        setImageList(imagel);
        JScrollPane imagelistScroller = new JScrollPane(imagel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel imagesPanel = new JPanel(new BorderLayout());
        imagesPanel.setBorder(
            BorderFactory.createTitledBorder("View Images by TYPE - ID"));
        imagesPanel.add(imagelistScroller, BorderLayout.CENTER);

        try {

            BufferedImage bi =
                ImageIO.read(getClass().getResource("thumb_preview.png"));
            setPreviewBufferedImage(bi);

        } catch (IOException ex) {

            setPreviewBufferedImage(null);
        }

        JXPanel thumb = new JXPanel();
        thumb.setPreferredSize(new Dimension(300, 200));
        ImagePainter painter = new ImagePainter(getPreviewBufferedImage());
        thumb.setBackgroundPainter(painter);
        setThumbPanel(thumb);

        SeriesInfo si = new SeriesInfo();
        setSeriesInfo(si);

        JComboBox banner = new JComboBox();
        banner.addActionListener(this);
        banner.setRenderer(new BannerCellRenderer());
        setBannerComboBox(banner);
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBorder(
            BorderFactory.createTitledBorder("Select Banner Image"));
        bannerPanel.add(banner, BorderLayout.CENTER);

        JComboBox fanart = new JComboBox();
        fanart.addActionListener(this);
        fanart.setRenderer(new BannerCellRenderer());
        setFanartComboBox(fanart);
        JPanel fanartPanel = new JPanel(new BorderLayout());
        fanartPanel.setBorder(
            BorderFactory.createTitledBorder("Select Fanart Image"));
        fanartPanel.add(fanart, BorderLayout.CENTER);

        JComboBox poster = new JComboBox();
        poster.addActionListener(this);
        poster.setRenderer(new BannerCellRenderer());
        setPosterComboBox(poster);
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBorder(
            BorderFactory.createTitledBorder("Select Poster Image"));
        posterPanel.add(poster, BorderLayout.CENTER);

        b = new JButton("Apply");
        b.addActionListener(this);
        setApplyButton(b);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner seasonsp = new JSpinner(model);
        setSeasonSpinner(seasonsp);

        model = new SpinnerNumberModel(1, 1, 40, 1);
        JSpinner episodesp = new JSpinner(model);
        setEpisodeSpinner(episodesp);

        b = new JButton("Find Episode");
        b.addActionListener(this);
        b.setEnabled(false);
        setEpisodeButton(b);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(
            BorderFactory.createTitledBorder("Search thetvdb.com site"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        searchPanel.add(getSearchTextField(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        searchPanel.add(getSearchButton(), gbc);

        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(searchPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(seriesPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(imagesPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(thumb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(bannerPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(fanartPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(posterPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(si, gbc);

        JPanel episodePanel = new JPanel(new GridBagLayout());
        episodePanel.setBorder(
            BorderFactory.createTitledBorder("Episode Search"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        episodePanel.add(new JLabel("Season"), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        episodePanel.add(getSeasonSpinner(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        episodePanel.add(new JLabel("Episode"), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        episodePanel.add(getEpisodeSpinner(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        episodePanel.add(getEpisodeButton(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(episodePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getApplyButton(), gbc);

        setHit(new Hit());
    }

    /**
     * {@inheritDoc}
     */
    public String getSearchTerms() {
        return (searchTerms);
    }

    /**
     * {@inheritDoc}
     */
    public void setSearchTerms(String s) {

        searchTerms = s;
        JTextField tf = getSearchTextField();
        if (tf != null) {
            tf.setText(searchTerms);
        }
    }

    private TheTVDB getTheTVDB() {
        return (theTVDB);
    }

    private void setTheTVDB(TheTVDB tvdb) {
        theTVDB = tvdb;
    }

    private JTextField getSearchTextField() {
        return (searchTextField);
    }

    private void setSearchTextField(JTextField tf) {
        searchTextField = tf;
    }

    private JButton getSearchButton() {
        return (searchButton);
    }

    private void setSearchButton(JButton b) {
        searchButton = b;
    }

    private JList getSeriesList() {
        return (seriesList);
    }

    private void setSeriesList(JList l) {
        seriesList = l;
    }

    private JList getImageList() {
        return (imageList);
    }

    private void setImageList(JList l) {
        imageList = l;
    }

    private JXPanel getThumbPanel() {
        return (thumbPanel);
    }

    private void setThumbPanel(JXPanel p) {
        thumbPanel = p;
    }

    private SeriesInfo getSeriesInfo() {
        return (seriesInfo);
    }

    private void setSeriesInfo(SeriesInfo mi) {
        seriesInfo = mi;
    }

    private JComboBox getBannerComboBox() {
        return (bannerComboBox);
    }

    private void setBannerComboBox(JComboBox cb) {
        bannerComboBox = cb;
    }

    private JComboBox getFanartComboBox() {
        return (fanartComboBox);
    }

    private void setFanartComboBox(JComboBox cb) {
        fanartComboBox = cb;
    }

    private JComboBox getPosterComboBox() {
        return (posterComboBox);
    }

    private void setPosterComboBox(JComboBox cb) {
        posterComboBox = cb;
    }

    private BufferedImage getPreviewBufferedImage() {
        return (previewBufferedImage);
    }

    private void setPreviewBufferedImage(BufferedImage bi) {
        previewBufferedImage = bi;
    }

    private JButton getApplyButton() {
        return (applyButton);
    }

    private void setApplyButton(JButton b) {
        applyButton = b;
    }

    private JSpinner getSeasonSpinner() {
        return (seasonSpinner);
    }

    private void setSeasonSpinner(JSpinner s) {
        seasonSpinner = s;
    }

    private JSpinner getEpisodeSpinner() {
        return (episodeSpinner);
    }

    private void setEpisodeSpinner(JSpinner s) {
        episodeSpinner = s;
    }

    private JButton getEpisodeButton() {
        return (episodeButton);
    }

    private void setEpisodeButton(JButton b) {
        episodeButton = b;
    }

    private HashMap<String, BufferedImage> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<String, BufferedImage> hm) {
        hashMap = hm;
    }

    private Banner[] getBanners() {
        return (banners);
    }

    private void setBanners(Banner[] array) {
        banners = array;
    }

    private int findBanner(Banner b) {

        int result = 0;

        Banner[] array = getBanners();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                if (b == array[i]) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    private BufferedImage fetchBufferedImage(Banner banner) {

        BufferedImage result = null;

        HashMap<String, BufferedImage> hm = getHashMap();
        if ((hm != null) && (banner != null)) {

            String urlstr = banner.getThumb();
            if (urlstr == null) {
                urlstr = banner.getUrl();
            }
            if (urlstr != null) {

                result = hm.get(urlstr);
                if (result == null) {

                    try {

                        URL url = new URL(urlstr);
                        result = ImageIO.read(url);
                        hm.put(urlstr, result);

                    } catch (MalformedURLException ex) {

                        JOptionPane.showMessageDialog(Util.findFrame(this),
                            "Error: " + ex.getMessage(), "alert",
                            JOptionPane.ERROR_MESSAGE);

                    } catch (IOException ex) {

                        JOptionPane.showMessageDialog(Util.findFrame(this),
                            "Error: " + ex.getMessage(), "alert",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        return (result);
    }

    private void searchAction() {

        JTextField tf = getSearchTextField();
        if (tf != null) {

            String terms = tf.getText().trim();
            if ((terms != null) && (terms.length() > 0)) {

                SearchJob sj = new SearchJob(getTheTVDB(), terms);
                ProgressBar pbar = new ProgressBar(this, "Searching...", sj);
                pbar.addJobListener(this);
                pbar.execute();

            } else {

                JOptionPane.showMessageDialog(Util.findFrame(this),
                    "Nothing to search for!!", "alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void episodeAction() {

        TheTVDB tvdb = getTheTVDB();
        JList list = getSeriesList();
        JSpinner sspin = getSeasonSpinner();
        JSpinner espin = getEpisodeSpinner();
        if ((list != null) && (tvdb != null) && (sspin != null)
            && (espin != null)) {

            Integer sobj = (Integer) sspin.getValue();
            Integer eobj = (Integer) espin.getValue();
            Series series = (Series) list.getSelectedValue();
            if ((series != null) && (sobj != null) && (eobj != null)) {

                EpisodeJob ej = new EpisodeJob(tvdb, series.getId(),
                    sobj.intValue(), eobj.intValue());
                ProgressBar pbar =
                    new ProgressBar(this, "Fetching Episode...", ej);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    private void imageAction() {

        TheTVDB tvdb = getTheTVDB();
        JList list = getSeriesList();
        if ((list != null) && (tvdb != null)) {

            Series series = (Series) list.getSelectedValue();
            if (series != null) {

                getEpisodeButton().setEnabled(true);
                BannersJob bj = new BannersJob(tvdb, series.getId());
                ProgressBar pbar =
                    new ProgressBar(this, "Fetching Images...", bj);
                pbar.addJobListener(this);
                pbar.execute();

            } else {

                getEpisodeButton().setEnabled(false);
            }
        }
    }

    private void imageDisplayAction() {

        JList imagel = getImageList();
        JXPanel panel = getThumbPanel();
        if ((panel != null) && (imagel != null)) {

            Banner banner = (Banner) imagel.getSelectedValue();

            BufferedImage bi = fetchBufferedImage(banner);
            if (bi != null) {

                ImagePainter painter = new ImagePainter(bi);
                panel.setBackgroundPainter(painter);

            } else {

                ImagePainter painter =
                    new ImagePainter(getPreviewBufferedImage());
                panel.setBackgroundPainter(painter);
            }
        }
    }

    private void applyAction() {

        fireSearchEvent(SearchEvent.UPDATE, getHit());
    }

    /**
     * We listen for button events which include the search button and
     * the user typing the enter key on the search text field.
     *
     * @param event The given action event.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getSearchButton()) {
            searchAction();
        } else if (event.getSource() == getSearchTextField()) {
            searchAction();
        } else if (event.getSource() == getBannerComboBox()) {
            updateHit();
        } else if (event.getSource() == getFanartComboBox()) {
            updateHit();
        } else if (event.getSource() == getPosterComboBox()) {
            updateHit();
        } else if (event.getSource() == getApplyButton()) {
            applyAction();
        } else if (event.getSource() == getEpisodeButton()) {
            episodeAction();
        }
    }

    /**
     * We listen for selection on the movie list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getSeriesList()) {

                JList l = getSeriesList();
                SeriesInfo si = getSeriesInfo();
                if ((l != null) && (si != null)) {

                    int index = l.getSelectedIndex();
                    if (index != -1) {

                        si.setSeries((Series) l.getSelectedValue());
                        imageAction();
                        updateHit();
                    }
                }
            } else if (event.getSource() == getImageList()) {

                imageDisplayAction();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void searchUpdate(SearchEvent event) {

        if (event.getSearchType() == SearchEvent.SEARCH_TV) {

            setSearchTerms(event.getTerms());
        }
    }

    /**
     * We need to know the status of the running job.  If it's done
     * we update our UI based upon the event state.
     *
     * @param event The given job event instance.
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Serializable s = event.getState();
            if (s instanceof Series[]) {

                Series[] array = (Series[]) s;
                JList l = getSeriesList();
                if (l != null) {
                    l.setListData(array);
                }

                JList imagel = getImageList();
                JComboBox banner = getBannerComboBox();
                JComboBox poster = getPosterComboBox();
                JComboBox fanart = getFanartComboBox();
                if ((imagel != null) && (banner != null) && (poster != null)
                    && (fanart != null)) {

                    imagel.setModel(new DefaultListModel());
                    banner.removeAllItems();
                    poster.removeAllItems();
                    fanart.removeAllItems();
                }

            } else if (s instanceof Banner[]) {

                Banner[] array = (Banner[]) s;
                setBanners(array);
                JList imagel = getImageList();
                if (imagel != null) {

                    JComboBox banner = getBannerComboBox();
                    JComboBox poster = getPosterComboBox();
                    JComboBox fanart = getFanartComboBox();
                    if ((banner != null) && (poster != null)
                        && (fanart != null)) {

                        imagel.setModel(new DefaultListModel());
                        banner.removeAllItems();
                        poster.removeAllItems();
                        fanart.removeAllItems();
                        ArrayList<Banner> blist = new ArrayList<Banner>();
                        for (int i = 0; i < array.length; i++) {

                            blist.add(array[i]);
                            if (isBannerSeriesType(array[i])) {
                                banner.addItem(array[i]);
                            } else if (isBannerFanartType(array[i])) {
                                fanart.addItem(array[i]);
                            } else if (isBannerPosterType(array[i])) {
                                poster.addItem(array[i]);
                            }
                        }

                        if (blist.size() > 0) {

                            Banner[] barray =
                                blist.toArray(new Banner[blist.size()]);
                            imagel.setListData(barray);
                        }
                    }
                }

            } else if (s instanceof Episode[]) {

                // Really just have one....
                Episode[] array = (Episode[]) s;
                SeriesInfo si = getSeriesInfo();
                if (si != null) {

                    Series old = si.getSeries();
                    Series series = new Series();
                    series.setSeriesName(old.getSeriesName());
                    series.setOverview("\"" + array[0].getEpisodeName()
                        + "\" " + array[0].getOverview());
                    series.setFirstAired(array[0].getFirstAired());
                    si.setSeries(series);
                    updateHit();
                }
            }
        }
    }

    private boolean isBannerFanartType(Banner b) {

        boolean result = false;

        if (b != null) {

            result = Banner.TYPE_FANART.equals(b.getBannerType());
        }

        return (result);
    }

    private boolean isBannerPosterType(Banner b) {

        boolean result = false;

        if (b != null) {

            result = Banner.TYPE_POSTER.equals(b.getBannerType());
        }

        return (result);
    }

    private boolean isBannerSeasonType(Banner b) {

        boolean result = false;

        if (b != null) {

            result = Banner.TYPE_SEASON.equals(b.getBannerType());
        }

        return (result);
    }

    private boolean isBannerSeriesType(Banner b) {

        boolean result = false;

        if (b != null) {

            result = (Banner.TYPE_SERIES.equals(b.getBannerType())
                && "graphical".equals(b.getBannerType2()));
        }

        return (result);
    }

    private void updateHit() {

        Hit h = getHit();
        if (h == null) {

            h = new Hit();
            setHit(h);
        }
        SeriesInfo si = getSeriesInfo();
        if (si != null) {

            Series s = si.getSeries();
            if (s != null) {

                h.setId(s.getImdbId());
                h.setMetadataTitle("TheTVDB.com");
                h.setTitle(s.getSeriesName());
                h.setDescription(s.getOverview());
                h.setReleased(s.getFirstAired());

                JComboBox cb = getBannerComboBox();
                if (cb != null) {

                    Banner banner = (Banner) cb.getSelectedItem();
                    if (banner != null) {

                        h.setBannerURL(banner.getUrl());
                    }
                }

                cb = getFanartComboBox();
                if (cb != null) {

                    Banner banner = (Banner) cb.getSelectedItem();
                    if (banner != null) {

                        h.setFanartURL(banner.getUrl());
                    }
                }

                cb = getPosterComboBox();
                if (cb != null) {

                    Banner banner = (Banner) cb.getSelectedItem();
                    if (banner != null) {

                        h.setPosterURL(banner.getUrl());
                    }
                }
            }
        }

    }

    static class SeriesCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object val,
            int index, boolean iss, boolean chf) {

            if (val instanceof Series) {

                Series series = (Series) val;
                String tmp = series.getSeriesName();
                super.getListCellRendererComponent(list, tmp, index, iss, chf);

            } else {
                super.getListCellRendererComponent(list, val, index, iss, chf);
            }

            return (this);
        }
    }

    class BannerCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object val,
            int index, boolean iss, boolean chf) {

            if (val instanceof Banner) {

                Banner banner = (Banner) val;
                String tmp = banner.getBannerType() + " "
                    + banner.getBannerType2() + "[" + findBanner(banner) + "]";
                super.getListCellRendererComponent(list, tmp, index, iss, chf);

            } else {
                super.getListCellRendererComponent(list, val, index, iss, chf);
            }

            return (this);
        }
    }

}
