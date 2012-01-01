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
package org.jflicks.trailer.apple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.trailer.Download;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This job supports the Apple Trailer service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AppleTrailerJob extends AbstractJob implements JobListener {

    private AppleTrailer appleTrailer;
    private ArrayList<SystemJob> systemJobList;
    private JobContainer jobContainer;

    /**
     * This job supports the AppleTrailer plugin.
     *
     * @param d A AppleTrailer instance.
     */
    public AppleTrailerJob(AppleTrailer d) {

        setAppleTrailer(d);
        setSleepTime(5 * 60000);
        setSystemJobList(new ArrayList<SystemJob>());
    }

    private AppleTrailer getAppleTrailer() {
        return (appleTrailer);
    }

    private void setAppleTrailer(AppleTrailer t) {
        appleTrailer = t;
    }

    private ArrayList<SystemJob> getSystemJobList() {
        return (systemJobList);
    }

    private void setSystemJobList(ArrayList<SystemJob> l) {
        systemJobList = l;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
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

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());

            AppleTrailer at = getAppleTrailer();
            if (at != null) {

                if (at.isTimeToUpdate()) {

                    ArrayList<SystemJob> list = getSystemJobList();
                    if ((list != null) && (list.size() == 0)) {

                        Download[] array = getDownloads();
                        if ((array != null) && (array.length > 0)) {

                            System.out.println("Trailers to download: "
                                + array.length);
                            for (int i = 0; i < array.length; i++) {

                                File file = getDownloadFile(array[i]);
                                String url = array[i].getUrl();
                                if ((file != null) && (url != null)) {

                                    SystemJob job = SystemJob.getInstance(
                                        "wget --quiet -U QuickTime/7.6.2 -c -O "
                                        + file.getPath() + " " + url);
                                    job.addJobListener(this);
                                    list.add(job);
                                }
                            }
                        }

                        if (list.size() > 0) {

                            SystemJob job = list.get(0);
                            list.remove(0);
                            JobContainer jc = JobManager.getJobContainer(job);
                            setJobContainer(jc);
                            jc.start();
                        }
                    }

                    at.autoExpire();
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc  = getJobContainer();
        if (jc != null) {

            jc.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            setJobContainer(null);
            ArrayList<SystemJob> list = getSystemJobList();
            if ((list != null) && (list.size() > 0)) {

                SystemJob job = list.get(0);
                list.remove(0);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();
            }
        }
    }

    private Document create(String url) {

        Document result = null;

        if (url != null) {

            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setIgnoringElementContentWhitespace(true);
            try {

                result = builder.build(url);

            } catch (JDOMException ex) {

                result = null;

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    private File getDownloadFile(Download d) {

        File result = null;

        if (d != null) {

            result = getDownloadFile(d.getTitle(), d.getUrl());
        }

        return (result);
    }

    private File getDownloadFile(String title, String url) {

        File result = null;

        AppleTrailer at = getAppleTrailer();
        if ((at != null) && (title != null) && (url != null)) {

            String path = at.getTrailerHome();
            String fname = url.substring(url.lastIndexOf("/") + 1);
            File dir = new File(path);
            if ((dir.exists()) && (dir.isDirectory())) {

                result = new File(dir, fname);
            }
        }

        return (result);
    }

    private boolean isNewTrailer(String title, String url) {

        boolean result = false;

        AppleTrailer at = getAppleTrailer();
        if ((at != null) && (title != null) && (url != null)) {

            Download d = at.getDownloadByTitle(title);
            if (d == null) {

                // Next see if the file is local, if so we will mark this
                // as downloaded.
                File file = getDownloadFile(title, url);
                if (file != null) {

                    if (!file.exists()) {

                        result = true;

                    } else {

                        d = new Download();
                        d.setTitle(title);
                        d.setUrl(url);
                        at.addDownload(d);
                    }
                }
            }
        }

        return (result);
    }

    private Download[] getDownloads() {

        Download[] result = null;

        AppleTrailer at = getAppleTrailer();
        if (at != null) {

            String[] feeds = at.getConfiguredFeeds();
            if (feeds != null) {

                ArrayList<Download> dlist = new ArrayList<Download>();
                for (int i = 0; i < feeds.length; i++) {

                    Document doc = create(feeds[i]);
                    if (doc != null) {

                        Element root = doc.getRootElement();
                        List movieinfoList = root.getChildren("movieinfo");
                        if (movieinfoList != null) {

                            for (int j = 0; j < movieinfoList.size(); j++) {

                                Element title = null;
                                Element url = null;

                                Element mielement =
                                    (Element) movieinfoList.get(j);
                                if (mielement != null) {

                                    Element info = mielement.getChild("info");
                                    if (info != null) {

                                        title = info.getChild("title");
                                    }

                                    Element preview =
                                        mielement.getChild("preview");
                                    if (preview != null) {

                                        url = preview.getChild("large");
                                    }
                                }

                                if ((title != null) && (url != null)) {

                                    String stitle = title.getTextTrim();
                                    String surl = url.getTextTrim();
                                    if (isNewTrailer(stitle, surl)) {

                                        Download d = new Download();
                                        d.setTitle(stitle);
                                        d.setUrl(surl);
                                        dlist.add(d);
                                    }
                                }
                            }
                        }
                    }
                }

                if (dlist.size() > 0) {

                    result = dlist.toArray(new Download[dlist.size()]);
                }
            }
        }

        return (result);
    }

}
