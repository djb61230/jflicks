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
package org.jflicks.ui.view.vm;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.nms.Video;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoRemoveJob extends AbstractJob {

    private NMS nms;
    private Video video;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     * @param v A Video to delete.
     */
    public VideoRemoveJob(NMS nms, Video v) {

        setNMS(nms);
        setVideo(v);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    private Video getVideo() {
        return (video);
    }

    private void setVideo(Video v) {
        video = v;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        NMS n = getNMS();
        Video v = getVideo();
        if ((n != null) && (v != null)) {

            n.removeVideo(v);
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        setTerminate(true);
    }

}
