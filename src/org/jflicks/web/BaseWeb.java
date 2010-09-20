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
package org.jflicks.web;

import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.WebVideo;

/**
 * This class is a base implementation of the Web interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseWeb extends BaseConfig implements Web {

    private String title;
    private ArrayList<WebVideo> webVideoList;

    /**
     * Simple empty constructor.
     */
    public BaseWeb() {

        setWebVideoList(new ArrayList<WebVideo>());
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    private ArrayList<WebVideo> getWebVideoList() {
        return (webVideoList);
    }

    private void setWebVideoList(ArrayList<WebVideo> l) {
        webVideoList = l;
    }

    /**
     * A convenience method to add one more WebVideo to our list.
     *
     * @param wv A given WebVideo to add.
     */
    public void add(WebVideo wv) {

        ArrayList<WebVideo> l = getWebVideoList();
        if ((l != null) && (wv != null)) {

            l.add(wv);
        }
    }

    /**
     * A convenience method to remove one WebVideo from our list.
     *
     * @param wv A given WebVideo to remove.
     */
    public void remove(WebVideo wv) {

        ArrayList<WebVideo> l = getWebVideoList();
        if ((l != null) && (wv != null)) {

            l.remove(wv);
        }
    }

    /**
     * A convenience method to remove all WebVideo from our list.
     */
    public void clear() {

        ArrayList<WebVideo> l = getWebVideoList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public WebVideo[] getWebVideos() {

        WebVideo[] result = null;

        ArrayList<WebVideo> l = getWebVideoList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new WebVideo[l.size()]);
        }

        return (result);
    }

    /**
     * A convenience method to set all WebVideo with an array.  Any current
     * WebVideo is cleared first.
     *
     * @param array A given array of WebVideo.
     */
    public void setWebVideo(WebVideo[] array) {

        clear();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                add(array[i]);
            }
        }
    }

    /**
     * Convenience method to find any RSS feeds configured for this Web
     * implementation.
     *
     * @return An Array of URL strings.
     */
    public String[] getConfiguredFeeds() {

        String[] result = null;

        Configuration c = getConfiguration();
        System.out.println("Configuration: " + c);
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.WEB_RSS_FEEDS);
            System.out.println("NameValue: " + nv);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

}

