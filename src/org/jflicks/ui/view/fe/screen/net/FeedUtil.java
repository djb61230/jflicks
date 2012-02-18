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
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
 
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jflicks.nms.Video;
import org.jflicks.util.Util;

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

            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setFeature("http://apache.org/xml/features/"
                + "nonvalidating/load-external-dtd", false);
            try {

                Document doc = builder.build(url);
                Element root = doc.getRootElement();
                Element channel = root.getChild("channel");
                List items = channel.getChildren("item");
                if ((items != null) && (items.size() > 0)) {

                    ArrayList<Video> vlist = new ArrayList<Video>();
                    for (int i = 0; i < items.size(); i++) {

                        Element item = (Element) items.get(i);
                        Video v = toVideo(channel, item);
                        if (v != null) {
                            vlist.add(v);
                        }
                    }

                    if (vlist.size() > 0) {

                        result = vlist.toArray(new Video[vlist.size()]);
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            } catch (JDOMException ex) {
                System.out.println(ex.getMessage());
            }
        }

        return (result);
    }

    public static Video toVideo(Element parent, Element e) {

        Video result = null;

        if ((parent != null) && (e != null)) {

            result = new Video();
            result.setTitle(e.getChildText("title"));

            // This will be overwritten later if itunes:summary exists.
            result.setDescription(e.getChildText("description"));

            // Our first possible poster image.
            result.setPosterURL(getGrandValue(parent, "image", "url"));
            if (result.getPosterURL() == null) {

                result.setPosterURL(getAttrValue(parent, "image", "href"));
            }

            Element enc = e.getChild("enclosure");
            if (enc != null) {

                result.setStreamURL(enc.getAttributeValue("url"));
            }

            result.setReleased(e.getChildText("pubDate"));

            List all = e.getChildren();
            for (int i =0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                if (name != null) {

                    if (name.equals("duration")) {

                        int index = value.indexOf(":");
                        if (index == -1) {

                            result.setDuration(Util.str2long(value, 0L));

                        } else {

                            result.setDuration(parseDuration(value));
                        }

                    } else if (name.equals("image")) {

                        value = tmp.getAttributeValue("href");
                        //result.setFanartURL(value);

                    } else if (name.equals("thumbnail")) {

                        String thumb = tmp.getAttributeValue("url");
                        if ((thumb != null) && (thumb.length() > 0)) {

                            result.setPosterURL(thumb);
                        }

                    } else if (name.equals("content")) {

                        String thumb = getThumbnail(tmp);
                        if ((thumb != null) && (thumb.length() > 0)) {

                            result.setPosterURL(thumb);
                        }

                    } else if (name.equals("summary")) {

                        result.setDescription(value);
                    }
                }
            }
        }

        return (result);
    }

    private static long parseDuration(String s) {

        long result = 0L;

        if (s != null) {

            StringTokenizer st = new StringTokenizer(s, ":");
            int hours = 0;
            int mins = 0;
            int secs = 0;
            if (st.countTokens() == 3) {
                hours = Util.str2int(st.nextToken(), 0);
                mins = Util.str2int(st.nextToken(), 0);
                secs = Util.str2int(st.nextToken(), 0);
            } else if (st.countTokens() == 2) {
                mins = Util.str2int(st.nextToken(), 0);
                secs = Util.str2int(st.nextToken(), 0);
            }

            result = (long) (hours * 60 * 60 + mins * 60 + secs);
        }

        return (result);
    }

    private static String getThumbnail(Element e) {

        String result = null;

        if (e != null) {

            List all = e.getChildren();
            for (int i = 0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                if (name != null) {

                    if (name.equals("thumbnail")) {

                        result = tmp.getAttributeValue("url");
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private static String getAttrValue(Element e, String child, String attr) {

        String result = null;

        if ((e != null) && (child != null) && (attr != null)) {

            List all = e.getChildren();
            for (int i = 0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                if (name != null) {

                    if (name.equals(child)) {

                        result = tmp.getAttributeValue(attr);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private static String getGrandValue(Element e, String child, String grand) {

        String result = null;

        if ((e != null) && (child != null) && (grand != null)) {

            List all = e.getChildren();
            for (int i = 0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                if (name != null) {

                    if (name.equals(child)) {

                        result = tmp.getChildText(grand);
                        //System.out.println("<" + name + "><" + child + ">");
                        //System.out.println("<" + grand + "><" + result + ">");
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private static String getValue(Element e, String child) {

        String result = null;

        if ((e != null) && (child != null)) {

            List all = e.getChildren();
            for (int i = 0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                if (name != null) {

                    if (name.equals(child)) {

                        result = value;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private static void dump(Element e) {

        if (e != null) {

            List all = e.getChildren();
            for (int i = 0; i < all.size(); i++) {

                Element tmp = (Element) all.get(i);
                String name = tmp.getName();
                String value = tmp.getTextTrim();
                System.out.println("<" + name + "><" + value +">");
            }
        }
    }

    public static void main(String[] args) {

        //String url = "http://revision3.com/tekzilla/feed/MP4-hd30";
        //String url = "http://feeds.twit.tv/twit_video_hd.xml";
        String url = "http://feeds.feedburner.com/computeractionshowvideo?format=xml";

        Video[] array = FeedUtil.toVideos(url);
        if (array != null) {

            System.out.println(array.length);
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i].getDuration());
                System.out.println(array[i].getFanartURL());
                System.out.println(array[i].getPosterURL());
                System.out.println(array[i].getStreamURL());
            }
        }
    }

}
