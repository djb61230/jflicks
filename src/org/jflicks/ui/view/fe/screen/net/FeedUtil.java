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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
 
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.jflicks.nms.Video;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class FeedUtil {

    private static SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("EEE MMM d h:mm aaa");

    /**
     * Default empty constructor.
     */
    private FeedUtil() {
    }

    private static String formatDate(Date d) {

        String result = null;

        if ((d != null) && (simpleDateFormat != null)) {

            StringBuffer sb = new StringBuffer();
            simpleDateFormat.format(d, sb, new FieldPosition(0));

            result = sb.toString();
        }

        return (result);
    }

    public static Video[] toVideos(String url) {

        Video[] result = null;

        if (url != null) {

            XmlReader reader = null;
            try {

                URL u = new URL(url);
                reader = new XmlReader(u);
                SyndFeed feed = new SyndFeedInput(false).build(reader);
                //System.out.println("Feed Title: "+ feed.getTitle());

                ArrayList<Video> vlist = new ArrayList<Video>();
                for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {

                    SyndEntry entry = (SyndEntry) i.next();
                    //System.out.println(entry.getTitle());
                    Video v = toVideo(entry);
                    if (v != null) {
                        vlist.add(v);
                    }
                }

                if (vlist.size() > 0) {

                    result = vlist.toArray(new Video[vlist.size()]);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {

                try {

                    if (reader != null) {
                        reader.close();
                    }

                } catch (IOException ex) {
                }
            }
        }

        return (result);
    }

    public static Video toVideo(SyndEntry se) {

        Video result = null;

        if (se != null) {

            result = new Video();
            result.setTitle(se.getTitle());
            SyndContent sc = se.getDescription();
            if (sc != null) {

                result.setDescription(sc.getValue());
            }
            List l = se.getEnclosures();
            if ((l != null) && (l.size() > 0)) {

                SyndEnclosure enc = (SyndEnclosure) l.get(0);
                if (enc != null) {

                    result.setStreamURL(enc.getUrl());
                }
            }

            Date pubdate = se.getPublishedDate();
            result.setReleased(formatDate(pubdate));
            List mlist = se.getModules();
            if (mlist != null) {

                System.out.println(mlist.size());
                for (int i = 0; i < mlist.size(); i++) {

                    Object obj = mlist.get(i);
                    System.out.println(obj.getClass());
                    if (obj instanceof EntryInformation) {

                        EntryInformation ei = (EntryInformation) obj;
                        Duration dur = ei.getDuration();
                        if (dur != null) {

                            result.setDuration(dur.getMilliseconds());
                        }

                    } else if (obj instanceof DCModule) {
                    } else if (obj instanceof MediaEntryModule) {

                        MediaEntryModule mec = (MediaEntryModule) obj;
                        MediaContent[] mcarray = mec.getMediaContents();
                        MediaGroup[] mgarray = mec.getMediaGroups();
                        System.out.println("mcarray: " + mcarray);
                        System.out.println("mgarray: " + mgarray);
                        if ((mcarray != null) && (mcarray.length > 0)) {

                            System.out.println("Duration: " + mcarray[0].getDuration());
                            Metadata m = mcarray[0].getMetadata();
                            if (m != null) {

                                Thumbnail[] tarray = m.getThumbnail();
                                if ((tarray != null) && (tarray.length > 0)) {

                                    URI turi = tarray[0].getUrl();
                                    if (turi != null) {
                                        result.setPosterURL(turi.toString());
                                    }
                                }
                            }
                        }
                        if (mgarray != null) {

                            System.out.println("mgarray.length: " + mgarray.length);
                        }
                    }
                }
            }
        }

        return (result);
    }

    public static void main(String[] args) {

        String url = "http://revision3.com/tekzilla/feed/MP4-hd30";
        //String url = "http://feeds.twit.tv/twit_video_hd.xml";

        Video[] array = FeedUtil.toVideos(url);
        if (array != null) {

            System.out.println(array.length);
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i].getDuration());
                System.out.println(array[i].getPosterURL());
                System.out.println(array[i].getStreamURL());
            }
        }
    }

}
