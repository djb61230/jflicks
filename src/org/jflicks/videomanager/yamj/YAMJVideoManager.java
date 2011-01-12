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
package org.jflicks.videomanager.yamj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.util.Util;
import org.jflicks.videomanager.BaseVideoManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This is our implementation of a VideoManager for YAMJ.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class YAMJVideoManager extends BaseVideoManager {

    private ArrayList<Video> videoList;

    /**
     * Default empty constructor.
     */
    public YAMJVideoManager() {

        setTitle("YAMJVideoManager");
        setVideoList(new ArrayList<Video>());
    }

    private ArrayList<Video> getVideoList() {
        return (videoList);
    }

    private void setVideoList(ArrayList<Video> l) {
        videoList = l;
    }

    private void addVideo(Video v) {

        ArrayList<Video> l = getVideoList();
        if ((l != null) && (v != null)) {

            l.add(v);
        }
    }

    private void clear() {

        ArrayList<Video> l = getVideoList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Video v) {

        log(INFO, "The YAMJ VideoManager cannot save, make changes using YAMJ");
    }

    /**
     * {@inheritDoc}
     */
    public Video getVideoById(String id) {

        Video result = null;

        ArrayList<Video> l = getVideoList();
        if ((id != null) && (l != null) && (l.size() > 0)) {

            for (int i = 0; i < l.size(); i++) {

                Video v = l.get(i);
                if (id.equals(v.getId())) {

                    result = v;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Video[] getVideos() {

        Video[] result = null;

        ArrayList<Video> l = getVideoList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Video[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVideo(Video v) {

        log(INFO, "The YAMJ VideoManager cannot remove, use YAMJ to manage");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void videoScan() {

        log(INFO, "Time to scan for video files...");

        clear();
        String[] array = getConfiguredVideoDirectories();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                File dir = new File(array[i]);
                if ((dir.exists()) && (dir.isDirectory())) {

                    File cm = new File(dir, "CompleteMovies.xml");
                    if ((cm.exists()) && (cm.isFile())) {

                        Document doc = create(cm);
                        if (doc != null) {

                            Element root = doc.getRootElement();
                            List movieList = root.getChildren("movies");
                            if (movieList != null) {

                                for (int j = 0; j < movieList.size(); j++) {

                                    Element me = (Element) movieList.get(j);
                                    if (isTV(me)) {

                                        int index = 0;
                                        boolean done = false;
                                        while (!done) {

                                            String path = getPath(me, index);
                                            if (path != null) {

                                                Video v = new Video();
                                                v.setAspectRatio(
                                                    getAspectRatio(me));
                                                v.setFilename(getFilename(me));
                                                v.setTitle(getTitle(me));
                                                v.setPath(path);
                                                v.setDescription(
                                                    getDescription(me));
                                                v.setReleased(getReleased(me));
                                                v.setCategory("TV");
                                                v.setSeason(
                                                    getSeason(me, index));
                                                v.setEpisode(
                                                    getEpisode(me, index));
                                                v.setDuration(getDuration(me));
                                                v.setPosterURL(
                                                    getPosterURL(me, array[i]));
                                                v.setFanartURL(
                                                    getFanartURL(me, array[i]));
                                                v.setBannerURL(
                                                    getBannerURL(me, array[i]));
                                                addVideo(v);
                                                index++;

                                            } else {

                                                done = true;
                                            }
                                        }

                                    } else {

                                        Video v = new Video();
                                        v.setAspectRatio(getAspectRatio(me));
                                        v.setFilename(getFilename(me));
                                        v.setTitle(getTitle(me));
                                        v.setPath(getPath(me, 0));
                                        v.setDescription(getDescription(me));
                                        v.setReleased(getReleased(me));
                                        v.setCategory("Movie");
                                        v.setSubcategory(getSubcategory(me));
                                        v.setDuration(getDuration(me));
                                        v.setPosterURL(
                                            getPosterURL(me, array[i]));
                                        v.setFanartURL(
                                            getFanartURL(me, array[i]));
                                        v.setBannerURL(
                                            getBannerURL(me, array[i]));
                                        addVideo(v);
                                    }
                                }
                            }
                        }

                        /*
                        for (int j = 0; j < files.length; j++) {

                            String name = files[j].getName();
                            String path = files[j].getPath();
                            Video v = getVideoByFilename(name);
                            if (v == null) {

                                String title = name;
                                int index = title.lastIndexOf(".");
                                if (index != -1) {

                                    title = title.substring(0, index);
                                }
                                v = new Video();
                                v.setCategory(guessVideoCategory(title));
                                v.setSeason(guessSeason(title));
                                v.setEpisode(guessEpisode(title));
                                v.setFilename(name);
                                v.setTitle(guessVideoTitle(title, v.isTV()));
                                v.setPath(path);
                                addVideo(v);

                            } else if (path != null) {

                                if (!path.equals(v.getPath())) {

                                    v.setPath(path);
                                    addVideo(v);
                                }
                            }
                        }
                        */
                    }
                }
            }
        }
    }

    private boolean isTV(Element e) {

        boolean result = false;

        if (e != null) {

            String s = getAttributeText(e, "isTV");
            if (Util.str2boolean(s, false)) {
                result = true;
            }
        }

        return (result);
    }

    private String getBannerURL(Element e, String dir) {

        String result = null;

        if ((e != null) && (dir != null)) {

            String s = getText(e, "bannerFilename");
            if (s != null) {

                s = s.trim();
                if ((s.length() > 0) && (!s.equalsIgnoreCase("unknown"))) {

                    String fileURL = "file://";
                    if (Util.isWindows()) {
                        fileURL = fileURL + "/";
                    }
                    result = fileURL + dir + File.separator + s;
                }
            }
        }

        return (result);
    }

    private String getFanartURL(Element e, String dir) {

        String result = null;

        if ((e != null) && (dir != null)) {

            String s = getText(e, "fanartFile");
            if (s != null) {

                s = s.trim();
                if ((s.length() > 0) && (!s.equalsIgnoreCase("unknown"))) {

                    String fileURL = "file://";
                    if (Util.isWindows()) {
                        fileURL = fileURL + "/";
                    }
                    result = fileURL + dir + File.separator + s;
                }
            }
        }

        return (result);
    }

    private String getPosterURL(Element e, String dir) {

        String result = null;

        if ((e != null) && (dir != null)) {

            String s = getText(e, "posterFile");
            if (s != null) {

                s = s.trim();
                if ((s.length() > 0) && (!s.equalsIgnoreCase("unknown"))) {

                    String fileURL = "file://";
                    if (Util.isWindows()) {
                        fileURL = fileURL + "/";
                    }
                    result = fileURL + dir + File.separator + s;
                }
            }
        }

        return (result);
    }

    private long getDuration(Element e) {

        long result = 0L;

        if (e != null) {

            String s = getText(e, "runtime");
            if (s != null) {

                long hours = 0L;
                long mins = 0L;
                int index = s.indexOf("h");
                if (index != -1) {

                    String tmp = s.substring(0, index);
                    if (tmp != null) {

                        tmp = tmp.trim();
                        hours = Util.str2long(tmp, hours);
                    }
                }

                if (index == -1) {
                    index = 0;
                } else {
                    index += 2;
                }
                int mindex = s.indexOf("m");
                if (mindex != -1) {

                    String tmp = s.substring(index, mindex);
                    if (tmp != null) {

                        tmp = tmp.trim();
                        mins = Util.str2long(tmp, mins);
                    }
                }

                result = (hours * 60 * 60) + (mins * 60);
            }
        }

        return (result);
    }

    private String getReleased(Element e) {

        String result = null;

        if (e != null) {

            result = getText(e, "releaseDate");
            if (result != null) {

                if (result.equalsIgnoreCase("unknown")) {
                    result = getText(e, "year");
                }

            } else {

                result = getText(e, "year");
            }
        }

        return (result);
    }

    private String getSubcategory(Element e) {

        String result = null;

        if (e != null) {

            Element genre = e.getChild("genres");
            if (genre != null) {

                List genreList = genre.getChildren("genre");
                if ((genreList != null) && (genreList.size() > 0)) {

                    Element first = (Element) genreList.get(0);
                    if (first != null) {

                        result = first.getTextTrim();
                    }
                }
            }
        }

        return (result);
    }

    private String getDescription(Element e) {

        String result = null;

        if (e != null) {

            result = getText(e, "plot");
        }

        return (result);
    }

    private String getTitle(Element e) {

        String result = null;

        if (e != null) {

            result = getText(e, "title");
        }

        return (result);
    }

    private String getPath(Element e, int index) {

        String result = null;

        if (e != null) {

            Element files = e.getChild("files");
            if (files != null) {

                List fileList = files.getChildren("file");
                if ((fileList != null) && (fileList.size() > index)) {

                    Element item = (Element) fileList.get(index);
                    if (item != null) {

                        result = getText(item, "fileURL");
                    }
                }
            }
        }

        return (result);
    }

    private int getSeason(Element e, int index) {

        int result = 1;

        if (e != null) {

            Element files = e.getChild("files");
            if (files != null) {

                List fileList = files.getChildren("file");
                if ((fileList != null) && (fileList.size() > index)) {

                    Element item = (Element) fileList.get(index);
                    if (item != null) {

                        Element info = item.getChild("info");
                        if (info != null) {

                            String s = info.getAttributeValue("season");
                            if (s != null) {

                                s = s.trim();
                                result = Util.str2int(s, result);
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private int getEpisode(Element e, int index) {

        int result = 1;

        if (e != null) {

            Element files = e.getChild("files");
            if (files != null) {

                List fileList = files.getChildren("file");
                if ((fileList != null) && (fileList.size() > index)) {

                    Element item = (Element) fileList.get(index);
                    if (item != null) {

                        String s = item.getAttributeValue("firstPart");
                        if (s != null) {

                            s = s.trim();
                            result = Util.str2int(s, result);
                        }
                    }
                }
            }
        }

        return (result);
    }

    private String getAspectRatio(Element e) {

        String result = null;

        if (e != null) {

            result = getText(e, "aspectRatio");

            if (result != null) {

                if (result.startsWith("2.")) {

                    result = NMSConstants.ASPECT_RATIO_235X1;

                } else if (result.startsWith("1.2")) {

                    result = NMSConstants.ASPECT_RATIO_4X3;

                } else if (result.startsWith("1.3")) {

                    result = NMSConstants.ASPECT_RATIO_4X3;

                } else if (result.startsWith("1.4")) {

                    result = NMSConstants.ASPECT_RATIO_4X3;

                } else if (result.startsWith("1.7")) {

                    result = NMSConstants.ASPECT_RATIO_16X9;

                } else if (result.startsWith("1.8")) {

                    result = NMSConstants.ASPECT_RATIO_16X9;

                } else {

                    result = NMSConstants.ASPECT_RATIO_16X9;
                }

            } else {

                result = NMSConstants.ASPECT_RATIO_16X9;
            }
        }

        return (result);
    }

    private String getFilename(Element e) {

        String result = null;

        if (e != null) {

            result = getText(e, "baseFilename");
        }

        return (result);
    }

    private String getText(Element e, String child) {

        String result = null;

        if ((e != null) && (child != null)) {

            Element childElement = e.getChild(child);
            if (childElement != null) {

                result = childElement.getTextTrim();
            }
        }

        return (result);
    }

    private String getAttributeText(Element e, String attr) {

        String result = null;

        if ((e != null) && (attr != null)) {

            result = e.getAttributeValue(attr);
        }

        return (result);
    }

    private Document create(File file) {

        Document result = null;

        if (file != null) {

            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setIgnoringElementContentWhitespace(true);
            try {

                result = builder.build(file);

            } catch (JDOMException ex) {

                result = null;

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

}

