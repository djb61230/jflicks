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

import java.util.ArrayList;

import org.jdesktop.swingx.JXLabel;
import org.jflicks.nms.Video;

/**
 * This is a display of Video instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoListPanel extends BaseListPanel {

    private ArrayList<Video> videoList;
    private boolean useEpisode;

    /**
     * Simple empty constructor.
     */
    public VideoListPanel() {

        setVideoList(new ArrayList<Video>());
        setPropertyName("SelectedVideo");
    }

    /**
     * The button text can either be the Title property or episode title.
     * If the Video is a TV episode, the season is appended to the title.
     * If we are to use the episode title, we have to extract it from the
     * start of the description.  Unfortunately it is not it's own
     * property.
     *
     * @return True if the episode text is used.
     */
    public boolean isUseEpisode() {
        return (useEpisode);
    }

    /**
     * The button text can either be the Title property or episode title.
     * If the Video is a TV episode, the season is appended to the title.
     * If we are to use the episode title, we have to extract it from the
     * start of the description.  Unfortunately it is not it's own
     * property.
     *
     * @param b True if the episode text is used.
     */
    public void setUseEpisode(boolean b) {
        useEpisode = b;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<Video> l = getVideoList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    private ArrayList<Video> getVideoList() {
        return (videoList);
    }

    private void setVideoList(ArrayList<Video> l) {
        videoList = l;
    }

    /**
     * We list video in our panel.
     *
     * @return An array of Video instances.
     */
    public Video[] getVideos() {

        Video[] result = null;

        ArrayList<Video> l = getVideoList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Video[l.size()]);
        }

        return (result);
    }

    /**
     * We list video in our panel.
     *
     * @param array An array of Video instances.
     */
    public void setVideos(Video[] array) {

        ArrayList<Video> l = getVideoList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                setSelectedObject(null);
                setStartIndex(0);
                if (isUseEpisode()) {

                    setSelectedIndex(0);
                }
            }
        }
    }

    /**
     * Convenience method to return the selected object as a Video instance.
     *
     * @return A Video instance.
     */
    public Video getSelectedVideo() {
        return ((Video) getSelectedObject());
    }

    /**
     * Update the UI.  We override because sometimes we want to display
     * our text differently.
     */
    protected void update() {

        ArrayList<Video> l = getVideoList();
        JXLabel[] labs = getLabels();
        if ((l != null) && (labs != null)) {

            int index = getStartIndex();
            for (int i = 0; i < labs.length; i++) {

                if (index < l.size()) {

                    Video v = l.get(index);
                    if (isUseEpisode()) {

                        String tmp = v.getTitle();
                        if (v.isTV()) {

                            String desc = v.getDescription();
                            if (desc != null) {

                                desc = desc.trim();
                                desc = desc.substring(1);
                                int qindex = desc.indexOf("\"");
                                if (qindex != -1) {

                                    tmp = desc.substring(0, qindex);
                                }
                            }
                        }

                        labs[i].setText(tmp);

                    } else {

                        String tmp = v.getTitle();
                        if (v.isTV()) {

                             tmp += " (Season " + v.getSeason() + ")";
                        }

                        labs[i].setText(tmp);
                    }

                } else {

                    labs[i].setText("");
                }

                index++;
            }

            applyColor();
            int sindex = getSelectedIndex() + getStartIndex();
            if (sindex < 0) {
                sindex = 0;
            }
            if (l.size() > sindex) {
                setSelectedObject(l.get(sindex));
            }

            animate();
        }
    }

}

