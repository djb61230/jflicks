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
package org.jflicks.web.rss;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.nms.WebVideo;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSException;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSItem;
import org.gnu.stealthp.rsslib.RSSParser;

/**
 * This job maintains the RSS Media available by reading defined
 * feed URLs that represent video channels on RSS.  It also runs a
 * player service tracker so a web/flash based player is used to
 * display and play the RSS content.
 *
 * @author Doug Barnum
 * @version 1.0 - 06 Jun 06
 */
public class RSSJob extends AbstractJob {

    private static final long FETCH_SPAN = 3600000L;

    private RSS rss;
    private long lastFetch;

    /**
     * Contructor with a RSS web.
     *
     * @param rss A given rss web.
     */
    public RSSJob(RSS rss) {

        setSleepTime(300000);
        setRSS(rss);
    }

    private RSS getRSS() {
        return (rss);
    }

    private void setRSS(RSS h) {
        rss = h;
    }

    private long getLastFetch() {
        return (lastFetch);
    }

    private void setLastFetch(long l) {
        lastFetch = l;
    }

    private boolean shouldFetch() {

        boolean result = false;

        long now = System.currentTimeMillis();
        long last = getLastFetch();
        if (last == 0L) {

            result = true;
            setLastFetch(now);

        } else {

            if ((last + FETCH_SPAN) <= now) {

                setLastFetch(now);
                result = true;
            }
        }

        return (result);
    }

    private void performFetch() {

        RSS h = getRSS();
        System.out.println("h: " + h);
        if (h != null) {

            String[] feeds = h.getConfiguredFeeds();
            if (feeds != null) {

                // Let's re-populate our list of Media.
                h.clear();

                for (int i = 0; i < feeds.length; i++) {

                    try {

                        RSSHandler hand = new RSSHandler();
                        URL feedUrl = new URL(feeds[i]);
                        System.out.println("feedUrl: " + feedUrl);
                        RSSParser.parseXmlFile(feedUrl, hand, false);
                        RSSChannel ch = hand.getRSSChannel();
                        List l = ch.getItems();
                        if (l != null) {

                            for (int j = 0; j < l.size(); j++) {

                                RSSItem item = (RSSItem) l.get(j);
                                WebVideo wv = new WebVideo();
                                wv.setSource(ch.getTitle());
                                wv.setTitle(item.getTitle());
                                wv.setURL(item.getLink());
                                wv.setDescription(item.getDescription());
                                wv.setReleased(item.getDate());
                                h.add(wv);
                            }
                        }

                    } catch (MalformedURLException ex) {

                        System.out.println(ex.getMessage());

                    } catch (IOException ex) {

                        System.out.println(ex.getMessage());

                    } catch (RSSException ex) {

                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
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

        System.out.println("running...first sleep a minute");
        JobManager.sleep(60000);
        while (!isTerminate()) {

            System.out.println("check if we should fetch");
            if (shouldFetch()) {

                System.out.println("perform fetch");
                performFetch();
            }

            System.out.println("sleep");
            JobManager.sleep(getSleepTime());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

    /**
     * Simple test main method.
     *
     * @param args Ignored command line arguments.
     */
    public static void main(String[] args) {

        RSS rss = new RSS();
        RSSJob job = new RSSJob(rss);
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}
