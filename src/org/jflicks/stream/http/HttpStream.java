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
package org.jflicks.stream.http;

import java.io.File;
import javax.servlet.ServletException;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.stream.BaseStream;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.LogUtil;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * This Stream (with other classes in this package) is capable of
 * using the OSGi http service to stream data.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HttpStream extends BaseStream implements JobListener,
    HttpServiceProperty {

    private HttpService httpService;
    private MimeHttpContext mimeHttpContext;
    private JobContainer soundJobContainer;

    /**
     * Default empty constructor.
     */
    public HttpStream() {

        setTitle("HttpStream");
        setType("audio");
        setMimeHttpContext(new MimeHttpContext());
    }

    private JobContainer getSoundJobContainer() {
        return (soundJobContainer);
    }

    private void setSoundJobContainer(JobContainer jc) {
        soundJobContainer = jc;
    }

    private MimeHttpContext getMimeHttpContext() {
        return (mimeHttpContext);
    }

    private void setMimeHttpContext(MimeHttpContext c) {
        mimeHttpContext = c;
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @return The OSGi HttpService instance.
     */
    public HttpService getHttpService() {
        return (httpService);
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @param hs The OSGi HttpService instance.
     */
    public void setHttpService(HttpService hs) {

        httpService = hs;
        if (httpService != null) {

            try {

                File here = new File(".");
                File web = new File(here, "www");
                httpService.registerResources("/"
                    + NMSConstants.HTTP_STREAM_NAME, web.getAbsolutePath(),
                    getMimeHttpContext());
                httpService.registerResources("/"
                    + NMSConstants.HTTP_IMAGES_NAME, web.getAbsolutePath(),
                    getMimeHttpContext());

            } catch (NamespaceException ex) {

                LogUtil.log(LogUtil.WARNING, "NamespaceException: " + ex.getMessage());
            }
        }
    }

    private String[] getRecordingDirectories() {

        String[] result = null;

        NMS nms = getNMS();
        if (nms != null) {

            Scheduler s = nms.getScheduler();
            if (s != null) {

                result = s.getConfiguredRecordingDirectories();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void startStream() {
    }

    /**
     * {@inheritDoc}
     */
    public void stopStream() {
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            stopStream();
        }
    }

}

