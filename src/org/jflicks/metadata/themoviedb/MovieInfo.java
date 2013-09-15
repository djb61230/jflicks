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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This panel displays info about a movie.
 *
 * @author Doug Barnum
 * @version 1.0 - 27 Dec 09
 */
public class MovieInfo extends JPanel {

    private JTextField nameTextField;
    private JTextArea overviewTextArea;
    private JTextField scoreTextField;
    private JTextField popularityTextField;
    private JTextField releasedTextField;
    private Movie movie;

    /**
     * Simple empty constructor.
     */
    public MovieInfo() {

        JTextField tf = new JTextField(16);
        tf.setEditable(false);
        tf.setBorder(null);
        setNameTextField(tf);
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(BorderFactory.createTitledBorder("Name"));
        namePanel.add(tf, BorderLayout.CENTER);

        JTextArea ta = new JTextArea(22, 16);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        setOverviewTextArea(ta);
        JScrollPane listScroller = new JScrollPane(getOverviewTextArea(),
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel overviewPanel = new JPanel(new BorderLayout());
        overviewPanel.setBorder(BorderFactory.createTitledBorder("Overview"));
        overviewPanel.add(listScroller, BorderLayout.CENTER);

        tf = new JTextField(16);
        tf.setEditable(false);
        tf.setBorder(null);
        setScoreTextField(tf);
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("Score"));
        scorePanel.add(tf, BorderLayout.CENTER);

        tf = new JTextField(16);
        tf.setEditable(false);
        tf.setBorder(null);
        setPopularityTextField(tf);
        JPanel popPanel = new JPanel(new BorderLayout());
        popPanel.setBorder(BorderFactory.createTitledBorder("Popularity"));
        popPanel.add(tf, BorderLayout.CENTER);

        tf = new JTextField(16);
        tf.setEditable(false);
        tf.setBorder(null);
        setReleasedTextField(tf);
        JPanel relPanel = new JPanel(new BorderLayout());
        relPanel.setBorder(BorderFactory.createTitledBorder("Released"));
        relPanel.add(tf, BorderLayout.CENTER);

        setLayout(new GridBagLayout());

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

        add(namePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(overviewPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(scorePanel, gbc);

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

        add(popPanel, gbc);

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

        add(relPanel, gbc);
    }

    private JTextField getNameTextField() {
        return (nameTextField);
    }

    private void setNameTextField(JTextField tf) {
        nameTextField = tf;
    }

    private JTextArea getOverviewTextArea() {
        return (overviewTextArea);
    }

    private void setOverviewTextArea(JTextArea ta) {
        overviewTextArea = ta;
    }

    private JTextField getScoreTextField() {
        return (scoreTextField);
    }

    private void setScoreTextField(JTextField tf) {
        scoreTextField = tf;
    }

    private JTextField getPopularityTextField() {
        return (popularityTextField);
    }

    private void setPopularityTextField(JTextField tf) {
        popularityTextField = tf;
    }

    private JTextField getReleasedTextField() {
        return (releasedTextField);
    }

    private void setReleasedTextField(JTextField tf) {
        releasedTextField = tf;
    }

    /**
     * The currently displayed information is contained in a Movie instance.
     *
     * @return A Movie instance.
     */
    public Movie getMovie() {
        return (movie);
    }

    /**
     * The currently displayed information is contained in a Movie instance.
     *
     * @param m A Movie instance.
     */
    public void setMovie(Movie m) {

        movie = m;

        if (movie != null) {

            getNameTextField().setText(movie.getTitle());
            getOverviewTextArea().setText(movie.getOverview());
            getOverviewTextArea().setCaretPosition(0);
            getScoreTextField().setText("" + movie.getVoteAverage());
            getPopularityTextField().setText("" + movie.getPopularity());
            getReleasedTextField().setText(movie.getReleaseDate());

        } else {

            getNameTextField().setText("");
            getOverviewTextArea().setText("");
            getScoreTextField().setText("");
            getPopularityTextField().setText("");
            getReleasedTextField().setText("");
        }
    }

}
