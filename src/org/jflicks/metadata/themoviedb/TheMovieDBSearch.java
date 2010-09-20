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
package org.jflicks.metadata.themoviedb;

import java.awt.BorderLayout;
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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.metadata.Hit;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchPanel;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 * This panel simply allows the user to search themoviedb.org and acquire
 * images.
 *
 * @author Doug Barnum
 * @version 1.0 - 28 Dec 09
 */
public class TheMovieDBSearch extends SearchPanel implements ActionListener,
    ListSelectionListener, JobListener {

    private JTextField searchTextField;
    private JButton searchButton;
    private JList movieList;
    private JList imageList;
    private JComboBox posterComboBox;
    private JComboBox fanartComboBox;
    private JXPanel thumbPanel;
    private MovieInfo movieInfo;
    private BufferedImage previewBufferedImage;
    private JButton applyButton;
    private HashMap<String, BufferedImage> hashMap;
    private String searchTerms;

    /**
     * Simple constructor.
     */
    public TheMovieDBSearch() {

        setHashMap(new HashMap<String, BufferedImage>());

        JTextField tf = new JTextField(16);
        tf.addActionListener(this);
        setSearchTextField(tf);

        JButton b = new JButton("Search");
        b.addActionListener(this);
        setSearchButton(b);

        JList moviel = new JList();
        moviel.setPrototypeCellValue("01234567890123456789012345678901234567");
        moviel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moviel.setVisibleRowCount(5);
        moviel.addListSelectionListener(this);
        setMovieList(moviel);
        JScrollPane movielistScroller = new JScrollPane(moviel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel moviesPanel = new JPanel(new BorderLayout());
        moviesPanel.setBorder(BorderFactory.createTitledBorder("Movies Found"));
        moviesPanel.add(movielistScroller, BorderLayout.CENTER);

        JList imagel = new JList();
        imagel.setPrototypeCellValue("01234567890123456789012345678901234567");
        imagel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagel.setVisibleRowCount(5);
        imagel.addListSelectionListener(this);
        setImageList(imagel);
        JScrollPane imagelistScroller = new JScrollPane(imagel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel imagesPanel = new JPanel(new BorderLayout());
        imagesPanel.setBorder(
            BorderFactory.createTitledBorder("View Images by TYPE - ID"));
        imagesPanel.add(imagelistScroller, BorderLayout.CENTER);

        JComboBox poster = new JComboBox();
        poster.addActionListener(this);
        setPosterComboBox(poster);
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBorder(
            BorderFactory.createTitledBorder("Select Poster Image"));
        posterPanel.add(poster, BorderLayout.CENTER);

        JComboBox fanart = new JComboBox();
        fanart.addActionListener(this);
        setFanartComboBox(fanart);
        JPanel fanartPanel = new JPanel(new BorderLayout());
        fanartPanel.setBorder(
            BorderFactory.createTitledBorder("Select Fanart Image"));
        fanartPanel.add(fanart, BorderLayout.CENTER);

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

        MovieInfo mi = new MovieInfo();
        setMovieInfo(mi);

        b = new JButton("Apply");
        b.addActionListener(this);
        setApplyButton(b);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(
            BorderFactory.createTitledBorder("Search themoviedb.org site"));

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

        add(moviesPanel, gbc);

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

        add(posterPanel, gbc);

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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(mi, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
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

    private JList getMovieList() {
        return (movieList);
    }

    private void setMovieList(JList l) {
        movieList = l;
    }

    private JList getImageList() {
        return (imageList);
    }

    private void setImageList(JList l) {
        imageList = l;
    }

    private JComboBox getPosterComboBox() {
        return (posterComboBox);
    }

    private void setPosterComboBox(JComboBox cb) {
        posterComboBox = cb;
    }

    private JComboBox getFanartComboBox() {
        return (fanartComboBox);
    }

    private void setFanartComboBox(JComboBox cb) {
        fanartComboBox = cb;
    }

    private JXPanel getThumbPanel() {
        return (thumbPanel);
    }

    private void setThumbPanel(JXPanel p) {
        thumbPanel = p;
    }

    private BufferedImage getPreviewBufferedImage() {
        return (previewBufferedImage);
    }

    private void setPreviewBufferedImage(BufferedImage bi) {
        previewBufferedImage = bi;
    }

    private MovieInfo getMovieInfo() {
        return (movieInfo);
    }

    private void setMovieInfo(MovieInfo mi) {
        movieInfo = mi;
    }

    private JButton getApplyButton() {
        return (applyButton);
    }

    private void setApplyButton(JButton b) {
        applyButton = b;
    }

    private HashMap<String, BufferedImage> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<String, BufferedImage> hm) {
        hashMap = hm;
    }

    private BufferedImage fetchBufferedImage(Image image) {

        BufferedImage result = null;

        HashMap<String, BufferedImage> hm = getHashMap();
        if ((hm != null) && (image != null)) {

            String urlstr = image.getUrl();
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

                SearchJob sj = new SearchJob(terms);
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

    private void imageAction() {

        JList movielist = getMovieList();
        if (movielist != null) {

            Movie m = (Movie) movielist.getSelectedValue();
            RetrieveJob rj = new RetrieveJob(m.getId());
            ProgressBar pbar = new ProgressBar(this, "Fetching Images...", rj);
            pbar.addJobListener(this);
            pbar.execute();
        }
    }

    private void imageDisplayAction() {

        JList imagel = getImageList();
        JXPanel panel = getThumbPanel();
        if ((panel != null) && (imagel != null)) {

            Image image = (Image) imagel.getSelectedValue();
System.out.println("imageDisplayAction image: " + image);

            BufferedImage bi = fetchBufferedImage(image);
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
     * {@inheritDoc}
     */
    public void searchUpdate(SearchEvent event) {

        if (event.getSearchType() == SearchEvent.SEARCH_MOVIE) {

            setSearchTerms(event.getTerms());
        }
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
        } else if (event.getSource() == getPosterComboBox()) {
            updateHit();
        } else if (event.getSource() == getFanartComboBox()) {
            updateHit();
        } else if (event.getSource() == getApplyButton()) {
            applyAction();
        }
    }

    /**
     * We listen for selection on the movie list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getMovieList()) {

                JList l = getMovieList();
                MovieInfo mi = getMovieInfo();
                if ((l != null) && (mi != null)) {

                    int index = l.getSelectedIndex();
                    if (index != -1) {

                        mi.setMovie((Movie) l.getSelectedValue());
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
     * We need to know the status of the running job.  If it's done
     * we update our UI based upon the event state.
     *
     * @param event The given job event instance.
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Serializable s = event.getState();
            if (s instanceof Search) {

                Search search = (Search) s;
                JList l = getMovieList();
                if (l != null) {

                    Movie[] array = search.getMovies();
                    if (array != null) {
                        l.setListData(array);
                    } else {
                        l.setModel(new DefaultListModel());
                    }

                    JList imagel = getImageList();
                    imagel.setModel(new DefaultListModel());
                }

            } else if (s instanceof Movie) {

                Movie movie = (Movie) s;
                JList imagel = getImageList();
                if ((imagel != null) && (movie.hasThumbnails())) {

                    Image[] thumbs = movie.getThumbnailImages();
                    if (thumbs != null) {

                        JComboBox poster = getPosterComboBox();
                        JComboBox fanart = getFanartComboBox();
                        if ((poster != null) && (fanart != null)) {

                            imagel.setModel(new DefaultListModel());
                            poster.removeAllItems();
                            fanart.removeAllItems();
                            ArrayList<Image> ilist = new ArrayList<Image>();
                            for (int i = 0; i < thumbs.length; i++) {

                                ilist.add(thumbs[i]);
                                if (thumbs[i].isPosterType()) {
                                    poster.addItem(thumbs[i]);
                                } else if (thumbs[i].isBackdropType()) {
                                    fanart.addItem(thumbs[i]);
                                }
                            }

                            if (ilist.size() > 0) {

                                Image[] array =
                                    ilist.toArray(new Image[ilist.size()]);
                                imagel.setListData(array);
                            }
                        }

                    } else {

                        imagel.setModel(new DefaultListModel());
                    }

                } else {

                    if (imagel != null) {

                        imagel.setModel(new DefaultListModel());
                    }

                    JOptionPane.showMessageDialog(Util.findFrame(this),
                        "Sorry no images available", "alert",
                        JOptionPane.INFORMATION_MESSAGE);
                }

                JXPanel panel = getThumbPanel();
                if (panel != null) {

                    ImagePainter painter =
                        new ImagePainter(getPreviewBufferedImage());
                    panel.setBackgroundPainter(painter);
                }
            }
        }
    }

    private void updateHit() {

        Hit h = getHit();
        if (h == null) {

            h = new Hit();
            setHit(h);
        }
        MovieInfo mi = getMovieInfo();
        if (mi != null) {

            Movie m = mi.getMovie();
            if (m != null) {

                h.setId(m.getImdbId());
                h.setMetadataTitle("TheMovieDB.org");
                h.setTitle(m.getName());
                h.setDescription(m.getOverview());
                h.setReleased(m.getReleased());

                JComboBox cb = getPosterComboBox();
                if (cb != null) {

                    Image img = (Image) cb.getSelectedItem();
                    if (img != null) {

                        String id = img.getId();
                        //Image real = m.getCoverSizeImageById(id);
                        Image real = m.getOriginalSizeImageById(id);
                        if (real != null) {
                            h.setPosterURL(real.getUrl());
                        } else {

                            real = m.getMidSizeImageById(id);
                            if (real != null) {
                                h.setPosterURL(real.getUrl());
                            } else {
                                h.setPosterURL(img.getUrl());
                            }
                        }
                    }
                }

                cb = getFanartComboBox();
                if (cb != null) {

                    Image img = (Image) cb.getSelectedItem();
                    if (img != null) {

                        String id = img.getId();
                        Image real = m.getOriginalSizeImageById(id);
                        if (real != null) {
                            h.setFanartURL(real.getUrl());
                        } else {

                            real = m.getPosterSizeImageById(id);
                            if (real != null) {
                                h.setFanartURL(real.getUrl());
                            } else {
                                h.setFanartURL(img.getUrl());
                            }
                        }
                    }
                }
            }
        }

    }

}
