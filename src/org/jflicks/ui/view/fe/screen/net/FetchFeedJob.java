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
package org.jflicks.ui.view.fe.screen.net;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.Video;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FetchFeedJob extends AbstractJob {

    private NetVideoScreen netVideoScreen;
    private String url;
    private Video[] videos;

    /**
     * Constructor with our required argument.
     *
     * @param url A feed url to access.
     */
    public FetchFeedJob(NetVideoScreen s, String url) {

        setNetVideoScreen(s);
        setUrl(url);
    }

    private NetVideoScreen getNetVideoScreen() {
        return (netVideoScreen);
    }

    private void setNetVideoScreen(NetVideoScreen s) {
        netVideoScreen = s;
    }

    private String getUrl() {
        return (url);
    }

    private void setUrl(String s) {
        url = s;
    }

    private Video[] getVideos() {
        return (videos);
    }

    private void setVideos(Video[] array) {
        videos = array;
    }

    /**
     * @inheritDoc
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * @inheritDoc
     */
    public void run() {

        NetVideoScreen screen = getNetVideoScreen();
        String s = getUrl();
        if ((screen != null) && (s != null)) {

            Video[] array = FeedUtil.toVideos(s);
            screen.applyVideo(array);
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}
