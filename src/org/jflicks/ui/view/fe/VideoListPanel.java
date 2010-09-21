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

import org.jflicks.nms.Video;

/**
 * This is a display of Video instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoListPanel extends BaseListPanel {

    private ArrayList<Video> videoList;

    /**
     * Simple empty constructor.
     */
    public VideoListPanel() {

        setVideoList(new ArrayList<Video>());
        setPropertyName("SelectedVideo");
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

}

