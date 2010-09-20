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

import org.jflicks.nms.WebVideo;

import org.jdesktop.swingx.JXLabel;

/**
 * Display WebVideo instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WebVideoListPanel extends BaseListPanel {

    private ArrayList<WebVideo> webVideoList;
    private boolean sourceName;

    /**
     * Simple empty constructor.
     */
    public WebVideoListPanel() {

        setWebVideoList(new ArrayList<WebVideo>());
        setPropertyName("SelectedWebVideo");
    }

    /**
     * The source name is the text displayed.  If not set just the title
     * is displayed.
     *
     * @return True if just the source name is wanted.
     */
    public boolean isSourceName() {
        return (sourceName);
    }

    /**
     * The source name is the text displayed.  If not set just the title
     * is displayed.
     *
     * @param b True if just the source name is wanted.
     */
    public void setSourceName(boolean b) {
        sourceName = b;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<WebVideo> l = getWebVideoList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    /**
     * Retrieve the currently selected WebVideo.
     *
     * @return A WebVideo instance.
     */
    public WebVideo getSelectedWebVideo() {
        return ((WebVideo) getSelectedObject());
    }

    private ArrayList<WebVideo> getWebVideoList() {
        return (webVideoList);
    }

    private void setWebVideoList(ArrayList<WebVideo> l) {
        webVideoList = l;
    }

    /**
     * We list webVideo in our panel.
     *
     * @return An array of WebVideo instances.
     */
    public WebVideo[] getWebVideos() {

        WebVideo[] result = null;

        return (result);
    }

    /**
     * We list webVideo in our panel.
     *
     * @param array An array of WebVideo instances.
     */
    public void setWebVideos(WebVideo[] array) {

        ArrayList<WebVideo> l = getWebVideoList();
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
     * Be able to update just one of the WebVideo instances we are
     * using.  It has likely updated in one or more of it's properties
     * and we need to ensure we have the changes.
     *
     * @param r A given WebVideo to update.
     */
    public void updateWebVideo(WebVideo r) {

        if (r != null) {

            // First thing is to update the WebVideo in our list.
            int index = getWebVideoById(r.getId());
            if (index != -1) {

                ArrayList<WebVideo> l = getWebVideoList();
                if (l != null) {

                    synchronized (l) {

                        if (l.size() > index) {

                            l.remove(index);
                            l.add(index, r);
                        }
                    }
                }
            }
        }
    }

    private int getWebVideoById(String s) {

        int result = -1;

        ArrayList<WebVideo> l = getWebVideoList();
        if ((s != null) && (l != null) && (l.size() > 0)) {

            for (int i = 0; i < l.size(); i++) {

                if (s.equals(l.get(i).getId())) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Update the UI.  We override because we need to sometimes display
     * different text.
     */
    protected void update() {

        ArrayList<WebVideo> l = getWebVideoList();
        JXLabel[] labs = getLabels();
        if ((l != null) && (labs != null)) {

            int index = getStartIndex();
            for (int i = 0; i < labs.length; i++) {

                if (index < l.size()) {

                    if (isSourceName()) {

                        labs[i].setText(l.get(index).getSource());

                    } else {

                        labs[i].setText(l.get(index).getTitle());
                    }

                } else {

                    labs[i].setText("");
                }

                index++;
            }

            applyColor();
            int sindex = getSelectedIndex() + getStartIndex();
            if (l.size() > sindex) {
                setSelectedObject(l.get(sindex));
            }

            animate();
        }
    }

}

