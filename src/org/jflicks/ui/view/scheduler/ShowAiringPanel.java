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
package org.jflicks.ui.view.scheduler;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.util.PromptPanel;
import org.jflicks.util.RowPanel;

/**
 * Panel that deals with ShowAiring display.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ShowAiringPanel extends JPanel implements PropertyChangeListener {

    private ShowAiring showAiring;
    private JTextField channelTextField;
    private JTextField titleTextField;
    private JTextField subtitleTextField;
    private JTextField typeTextField;
    private JTextField episodeNumberTextField;
    private JTextField originalAirDateTextField;
    private JTextField airDateTextField;
    private JTextField durationTextField;
    private JTextArea descriptionTextArea;
    private SchedulerView schedulerView;

    /**
     * Constructor with one argument.
     *
     * @param v Our View instance.
     */
    public ShowAiringPanel(SchedulerView v) {

        setSchedulerView(v);

        JTextField channeltf = new JTextField(20);
        channeltf.setEditable(false);
        setChannelTextField(channeltf);

        JTextField titletf = new JTextField(20);
        titletf.setEditable(false);
        setTitleTextField(titletf);

        JTextField subtitletf = new JTextField(20);
        subtitletf.setEditable(false);
        setSubtitleTextField(subtitletf);

        JTextField typetf = new JTextField(20);
        typetf.setEditable(false);
        setTypeTextField(typetf);

        JTextField episodeNumbertf = new JTextField(20);
        episodeNumbertf.setEditable(false);
        setEpisodeNumberTextField(episodeNumbertf);

        JTextField originalAirDatetf = new JTextField(20);
        originalAirDatetf.setEditable(false);
        setOriginalAirDateTextField(originalAirDatetf);

        JTextField airDatetf = new JTextField(20);
        airDatetf.setEditable(false);
        setAirDateTextField(airDatetf);

        JTextField durationtf = new JTextField(20);
        durationtf.setEditable(false);
        setDurationTextField(durationtf);

        JTextArea descta = new JTextArea(10, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescriptionTextArea(descta);

        JScrollPane scroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        String[] prompts = {
            "Channel", "Title", "Subtitle", "Show Type", "Episode Number",
            "Original Air Date", "Air Date", "Duration"
        };

        JComponent[] comps = {
            channeltf, titletf, subtitletf, typetf, episodeNumbertf,
            originalAirDatetf, airDatetf, durationtf
        };

        PromptPanel pp = new PromptPanel(prompts, comps);
        RowPanel descrp = new RowPanel("Description", scroller);
        RowPanel rp = new RowPanel("Program Details", pp, descrp);

        setLayout(new BorderLayout());
        add(rp, BorderLayout.CENTER);
    }

    private SchedulerView getSchedulerView() {
        return (schedulerView);
    }

    private void setSchedulerView(SchedulerView v) {
        schedulerView = v;
    }

    private String computeChannel(int id) {

        String result = null;

        SchedulerView sv = getSchedulerView();
        if (sv != null) {

            Channel c = sv.getChannelById(id);
            if (c != null) {
                result = c.toString();
            }
        }

        return (result);
    }

    /**
     * All UI components show data from a ShowAiring instance.
     *
     * @return A ShowAiring object.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * All UI components show data from a ShowAiring instance.
     *
     * @param sa A ShowAiring object.
     */
    public void setShowAiring(ShowAiring sa) {
        showAiring = sa;

        boolean clear = false;

        if (sa != null) {

            Show show = sa.getShow();
            Airing airing = sa.getAiring();
            if ((show != null) && (airing != null)) {

                apply(getChannelTextField(),
                    computeChannel(airing.getChannelId()));
                apply(getTitleTextField(), show.getTitle());
                apply(getSubtitleTextField(), show.getSubtitle());
                apply(getTypeTextField(), show.getType());
                apply(getEpisodeNumberTextField(), show.getEpisodeNumber());

                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                Date oar = show.getOriginalAirDate();
                if (oar != null) {

                    String str = df.format(show.getOriginalAirDate());
                    apply(getOriginalAirDateTextField(), str);

                } else {

                    apply(getOriginalAirDateTextField(), "");
                }

                Date ad = airing.getAirDate();
                if (ad != null) {

                    df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                        DateFormat.SHORT);
                    String str = df.format(ad);
                    apply(getAirDateTextField(), str);

                } else {

                    apply(getAirDateTextField(), "");
                }

                apply(getDurationTextField(),
                    durationToString(airing.getDuration()));
                apply(getDescriptionTextArea(), show.getDescription());

            } else {

                clear = true;
            }

        } else {

            clear = true;
        }

        if (clear) {

            apply(getChannelTextField(), null);
            apply(getTitleTextField(), null);
            apply(getSubtitleTextField(), null);
            apply(getTypeTextField(), null);
            apply(getEpisodeNumberTextField(), null);
            apply(getOriginalAirDateTextField(), null);
            apply(getAirDateTextField(), null);
            apply(getDurationTextField(), null);
            apply(getDescriptionTextArea(), null);
        }

        getDescriptionTextArea().setCaretPosition(0);
    }

    private JTextField getChannelTextField() {
        return (channelTextField);
    }

    private void setChannelTextField(JTextField tf) {
        channelTextField = tf;
    }

    private JTextField getTitleTextField() {
        return (titleTextField);
    }

    private void setTitleTextField(JTextField tf) {
        titleTextField = tf;
    }

    private JTextField getSubtitleTextField() {
        return (subtitleTextField);
    }

    private void setSubtitleTextField(JTextField tf) {
        subtitleTextField = tf;
    }

    private JTextField getTypeTextField() {
        return (typeTextField);
    }

    private void setTypeTextField(JTextField tf) {
        typeTextField = tf;
    }

    private JTextField getEpisodeNumberTextField() {
        return (episodeNumberTextField);
    }

    private void setEpisodeNumberTextField(JTextField tf) {
        episodeNumberTextField = tf;
    }

    private JTextField getOriginalAirDateTextField() {
        return (originalAirDateTextField);
    }

    private void setOriginalAirDateTextField(JTextField tf) {
        originalAirDateTextField = tf;
    }

    private JTextField getAirDateTextField() {
        return (airDateTextField);
    }

    private void setAirDateTextField(JTextField tf) {
        airDateTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JTextArea getDescriptionTextArea() {
        return (descriptionTextArea);
    }

    private void setDescriptionTextArea(JTextArea ta) {
        descriptionTextArea = ta;
    }

    private String durationToString(long l) {

        return ((l / 60) + " minutes");
    }

    private void apply(JTextComponent c, String s) {

        if (c != null) {

            if (s != null) {

                c.setText(s.trim());

            } else {

                c.setText("");
            }
        }
    }

    /**
     * Update our display based upon a ShowAiring selection update.
     *
     * @param event A given PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getPropertyName().equals("ShowAiring")) {

            setShowAiring((ShowAiring) event.getNewValue());
        }
    }

}
