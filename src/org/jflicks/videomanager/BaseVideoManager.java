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
package org.jflicks.videomanager;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;

/**
 * A base implementation of the VideoManager interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseVideoManager extends BaseConfig
    implements VideoManager {

    private String title;
    private NMS nms;

    /**
     * Simple constructor.
     */
    public BaseVideoManager() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method tp set the title property.
     *
     * @param s A given title as a String.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    /**
     * Convenience method to get the host property from our NMS.
     *
     * @return A String instance.
     */
    public String getHost() {

        String result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getHost();
        }

        return (result);
    }

    /**
     * Convenience method to get the Stream Paths property from our NMS.
     *
     * @return A String array.
     */
    public String[] getStreamPaths() {

        String[] result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getStreamPaths();
        }

        return (result);
    }

    /**
     * Convenience method to get the port property from our NMS.
     *
     * @return A int value.
     */
    public int getPort() {

        int result = 8080;

        NMS n = getNMS();
        if (n != null) {

            result = n.getPort();
        }

        return (result);
    }

    /**
     * Convenience method to get the http port property from our NMS.
     *
     * @return A int value.
     */
    public int getHttpPort() {

        int result = 8080;

        NMS n = getNMS();
        if (n != null) {

            result = n.getHttpPort();
        }

        return (result);
    }

    /**
     * Convenience method to get the configured set of directories that
     * house video files.
     *
     * @return An array of String objects.
     */
    public String[] getConfiguredVideoDirectories() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.VIDEO_DIRECTORIES);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured set of file extensions that
     * identify supported video files.
     *
     * @return An array of String objects.
     */
    public String[] getConfiguredVideoExtensions() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.VIDEO_EXTENSIONS);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    public String computeStreamURL(Video v) {

        String result = null;

        String h = getHost();
        String[] array = getStreamPaths();
        if ((h != null) && (array != null) && (v != null)) {

            boolean found = false;
            String path = v.getPath();
            if (path != null) {

                path = path.replace("\\", "/");
                for (int i = 0; i < array.length; i++) {

                    if (path.startsWith(array[i])) {

                        path = path.substring(array[i].length());
                        found = true;
                        break;
                    }
                }
            }

            if ((found) && (path != null)) {

                if (path.startsWith("/")) {
                    result = "http://" + h + path;
                } else {
                    result = "http://" + h + "/" + path;
                }
            }
        }

        return (result);
    }

}
