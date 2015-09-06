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

import java.io.File;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SaveVideoJob extends AbstractJob {

    private NMS nms;
    private Video video;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     * @param video The video to save.
     */
    public SaveVideoJob(NMS nms, Video video) {

        setNMS(nms);
        setVideo(video);
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

            // First save the images...
            String id = null;
            if (v.isTV()) {

                id = v.getSubcategory();
                if (id != null) {

                    id = id.replaceAll(" ", "_");
                    id = id.replaceAll("'", "_");
                    id = id.replaceAll(",", "_");
                }

            } else {
                id = v.getId();
            }
            String url = v.getBannerURL();
            LogUtil.log(LogUtil.DEBUG, "url: " + url);
            LogUtil.log(LogUtil.DEBUG, "id: " + id);
            if ((url != null) && (id != null)) {

                if (url.indexOf(id) == -1) {

                    LogUtil.log(LogUtil.DEBUG, "url <" + url + ">");
                    if (url.startsWith("file")) {

                        String path = url.substring(5);
                        File fbuf = new File(path);
                        byte[] b = Util.read(fbuf);
                        if (b != null) {
                            n.save(NMSConstants.BANNER_IMAGE_TYPE, b, id);
                        }

                    } else {
                        n.save(NMSConstants.BANNER_IMAGE_TYPE, url, id);
                    }

                } else {
                    n.save(NMSConstants.BANNER_IMAGE_TYPE, url, id);
                }
            }

            url = v.getFanartURL();
            LogUtil.log(LogUtil.DEBUG, "url: " + url);
            LogUtil.log(LogUtil.DEBUG, "id: " + id);
            if ((url != null) && (id != null)) {

                if (url.indexOf(id) == -1) {

                    LogUtil.log(LogUtil.DEBUG, "url <" + url + ">");
                    if (url.startsWith("file")) {

                        String path = url.substring(5);
                        File fbuf = new File(path);
                        LogUtil.log(LogUtil.DEBUG, "fbuf: " + fbuf);
                        byte[] b = Util.read(fbuf);
                        LogUtil.log(LogUtil.DEBUG, "b: " + b);
                        if (b != null) {
                            n.save(NMSConstants.FANART_IMAGE_TYPE, b, id);
                        }

                    } else {
                        n.save(NMSConstants.FANART_IMAGE_TYPE, url, id);
                    }
                } else {
                    n.save(NMSConstants.FANART_IMAGE_TYPE, url, id);
                }
            }

            url = v.getPosterURL();
            LogUtil.log(LogUtil.DEBUG, "url: " + url);
            LogUtil.log(LogUtil.DEBUG, "id: " + id);
            if ((url != null) && (id != null)) {

                if (url.indexOf(id) == -1) {

                    LogUtil.log(LogUtil.DEBUG, "url <" + url + ">");
                    if (url.startsWith("file")) {

                        String path = url.substring(5);
                        File fbuf = new File(path);
                        byte[] b = Util.read(fbuf);
                        LogUtil.log(LogUtil.DEBUG, "b: " + b);
                        if (b != null) {
                            n.save(NMSConstants.POSTER_IMAGE_TYPE, b, id);
                        }

                    } else {

                        n.save(NMSConstants.POSTER_IMAGE_TYPE, url, id);
                    }

                } else {

                    n.save(NMSConstants.POSTER_IMAGE_TYPE, url, id);
                }
            }

            // Now save the Video...
            n.save(v);
            Video updated = n.getVideoById(v.getId());
            v.setBannerURL(updated.getBannerURL());
            v.setFanartURL(updated.getFanartURL());
            v.setPosterURL(updated.getPosterURL());
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
