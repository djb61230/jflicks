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
package org.jflicks.videomanager.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.jflicks.db.DbWorker;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.nms.BaseNMS;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.util.FileFind;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;
import org.jflicks.videomanager.BaseVideoManager;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

/**
 * This is our implementation of a VideoManager.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemVideoManager extends BaseVideoManager implements DbWorker {

    private static final String TV_REGEX = "S\\d\\dE\\d\\d";

    private ObjectContainer objectContainer;
    private Db4oService db4oService;
    private Pattern pattern;

    /**
     * Default empty constructor.
     */
    public SystemVideoManager() {

        setTitle("SystemVideoManager");
        setPattern(Pattern.compile(TV_REGEX));
    }

    private Pattern getPattern() {
        return (pattern);
    }

    private void setPattern(Pattern p) {
        pattern = p;
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                objectContainer = s.openFile(config, "db/video.dat");

            } else {

                LogUtil.log(LogUtil.WARNING, "SystemVideoManager: Db4oService null!");
            }
        }

        return (objectContainer);
    }

    /**
     * {@inheritDoc}
     */
    public void save(Video v) {

        addVideo(v);
    }

    private void purge(ObjectContainer db, Class c) {

        if ((db != null) && (c != null)) {

            ObjectSet result = db.queryByExample(c);
            while (result.hasNext()) {
                db.delete(result.next());
            }
        }
    }

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            LogUtil.log(LogUtil.DEBUG, "SystemVideoManager: closed " + result);
            objectContainer = null;

        } else {

            LogUtil.log(LogUtil.DEBUG, "SystemVideoManager: Tried to close "
                + "but objectContainer null.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Video getVideoById(String id) {

        Video result = null;

        ObjectContainer oc = getObjectContainer();
        if ((id != null) && (oc != null)) {

            final String vid = id;
            List<Video> vids = oc.query(new Predicate<Video>() {

                public boolean match(Video v) {
                    return (vid.equals(v.getId()));
                }
            });

            if ((vids != null) && (vids.size() > 0)) {

                result = vids.get(0);
                if (result != null) {

                    String h = getHost();
                    int p = getHttpPort();
                    if (h != null) {

                        String top = "http://" + h + ":" + p + "/"
                            + NMSConstants.HTTP_IMAGES_NAME + "/";
                        String sid = result.getId();
                        if (sid != null) {

                            result.setBannerURL(top
                                + getImageName(sid + "_banner.jpg"));
                            result.setPosterURL(top
                                + getImageName(sid + "_poster.jpg"));
                            result.setFanartURL(top
                                + getImageName(sid + "_fanart.jpg"));
                        }
                        result.setHostPort(h + ":" + p);
                    }
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

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Video> os = oc.queryByExample(Video.class);
            if (os != null) {

                result = os.toArray(new Video[os.size()]);

                // We should update the image URLs for the client.  Persisting
                // the URLs is not a good idea because the URL could change.
                // Either by config the port changes or less likely the IP
                // changes.  Either way we will update them.  We don't check
                // if they actually exist, we will just build them by rule.
                if (result != null) {

                    String h = getHost();
                    int p = getPort();
                    int hport = getHttpPort();
                    if (h != null) {

                        String hp = h + ":" + p;
                        String top = "http://" + h + ":" + hport + "/"
                            + NMSConstants.HTTP_IMAGES_NAME + "/";
                        for (int i = 0; i < result.length; i++) {

                            String sid = null;
                            if (result[i].isTV()) {

                                sid = result[i].getSubcategory();
                                if (sid != null) {

                                    sid = sid.replaceAll(" ", "_");
                                    sid = sid.replaceAll("'", "_");
                                    sid = sid.replaceAll(",", "_");
                                }

                            } else {
                                sid = result[i].getId();
                            }

                            if (sid != null) {

                                result[i].setBannerURL(top
                                    + getImageName(sid + "_banner.jpg"));
                                result[i].setPosterURL(top
                                    + getImageName(sid + "_poster.jpg"));
                                result[i].setFanartURL(top
                                    + getImageName(sid + "_fanart.jpg"));
                            }
                            result[i].setHostPort(hp);
                            result[i].setStreamURL(computeStreamURL(result[i]));

                            // We also want to check that the "added"
                            // property is set.  We added the "added"
                            // property later on in development so this
                            // is a bit of hack code to update
                            // the DB to match the file time.
                            if (result[i].getAdded() == 0L) {

                                File f = new File(result[i].getPath());
                                if ((f.exists()) && (f.isFile())) {

                                    result[i].setAdded(f.lastModified());
                                    addVideo(result[i]);
                                }
                            }
                        }
                    }

                    // Now we want to sort them by the "added" property.
                    Arrays.sort(result, new VideoSortByAdded());
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVideo(Video v) {

        if (v != null) {

            //removeVideoFromDisk(v);
            removeVideoFromDB(v);
            videoScan();
        }
    }

    /**
     * Convenience method to get a Video instance given just it's filename.
     * Remember that this is NOT the full path, it's just the name of the
     * file itself.
     *
     * @param s A given file name.
     * @return A Video instance if it is found.
     */
    public Video getVideoByFilename(String s) {

        Video result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (s != null)) {

            final String fname = s;
            List<Video> vids = oc.query(new Predicate<Video>() {

                public boolean match(Video v) {
                    return (fname.equals(v.getFilename()));
                }
            });

            if ((vids != null) && (vids.size() > 0)) {

                // We should have just one...
                result = vids.get(0);
                if (result != null) {

                    result.setHostPort(getHost() + ":" + getPort());
                }
            }
        }

        return (result);
    }

    /**
     * Add a Video to our database.
     *
     * @param v A Video to add.
     */
    public void addVideo(Video v) {

        ObjectContainer oc = getObjectContainer();
        if ((v != null) && (oc != null)) {

            removeVideoFromDB(v);
            oc.store(new Video(v));
            oc.commit();
        }

    }

    /**
     * Actually delete the file on disk.
     *
     * @param v A Video instance to use to get the path of the file to delete.
     * @return True on success.
     */
    public boolean removeVideoFromDisk(Video v) {

        boolean result = false;

        if (v != null) {

            File file = new File(v.getPath());
            result = file.delete();
        }

        return (result);
    }

    /**
     * This is a convenience method to remove a Video from the database,
     * mostly because it has been deleted from disk.  If you want to hide
     * a video without physically deleting the file, set the "hidden"
     * property on the and add it again.
     *
     * @param v A given Video to remove.
     */
    public void removeVideoFromDB(Video v) {

        ObjectContainer oc = getObjectContainer();
        if ((v != null) && (oc != null)) {

            final String id = v.getId();
            List<Video> vids = oc.query(new Predicate<Video>() {

                public boolean match(Video v) {
                    return (id.equals(v.getId()));
                }
            });

            if (vids != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < vids.size(); i++) {
                    oc.delete(vids.get(i));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void generateArtwork(Video v, int seconds) {

        NMS n = getNMS();
        if ((v != null) && (n != null)) {

            if (seconds <= 0) {
                seconds = 60;
            }

            try {

                File tmp = File.createTempFile("generate", ".jpg");
                ThumbnailerJob job =
                    new ThumbnailerJob(v.getPath(), tmp.getPath(), seconds);
                JobContainer jc = JobManager.getJobContainer(job);
                jc.start();

                boolean done = false;
                int count = 0;
                while (!done) {

                    if (!jc.isAlive()) {

                        done = true;

                    } else {

                        count += 100;
                        if (count > 9900) {

                            done = true;

                        } else {

                            JobManager.sleep(100);
                        }
                    }
                }

                // We should be done at this point...
                if ((tmp.exists()) && (tmp.isFile())) {

                    BufferedImage bi = ImageIO.read(tmp);
                    if (bi != null) {

                        LogUtil.log(LogUtil.DEBUG, "width: " + bi.getWidth());
                        LogUtil.log(LogUtil.DEBUG, "height: " + bi.getHeight());
                        if (bi.getWidth() < 1280) {

                            // We have a source video that is 4x3.  We need to
                            // cut off the bottom.
                            bi = Util.scale(bi, 1280);

                        } else if ((bi.getWidth() == 1440)
                            && (bi.getHeight() == 1080)) {

                            // We have a 1440x1080 image that needs to be
                            // resized.
                            bi = Util.resize(bi, 1280, 720);
                        }

                        LogUtil.log(LogUtil.DEBUG, "after width: " + bi.getWidth());
                        LogUtil.log(LogUtil.DEBUG, "after height: " + bi.getHeight());

                        int height = bi.getHeight();

                        String sid = null;
                        if (v.isTV()) {

                            sid = v.getSubcategory();
                            if (sid != null) {

                                sid = sid.replaceAll(" ", "_");
                                sid = sid.replaceAll("'", "_");
                                sid = sid.replaceAll(",", "_");
                            }

                        } else {
                            sid = v.getId();
                        }

                        // At this point our image should be 1280x720 which
                        // will be our fanart.  Next we make a poster by
                        // doing a center cut.  First make it an image
                        // type that works in OpenJDK.  Also should work in
                        // Oracle.
                        BufferedImage fanbi = new BufferedImage(bi.getWidth(),
                            bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                        int[] oldData = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(),
                            null, 0, bi.getWidth());
                        fanbi.setRGB(0, 0, bi.getWidth(), bi.getHeight(), oldData,
                            0, bi.getWidth());

                        File fanart = File.createTempFile("fanart", ".jpg");
                        ImageIO.write(fanbi, "jpg", fanart);

                        BufferedImage pbi = fanbi.getSubimage(360, 0, 495, height);
                        File poster = File.createTempFile("poster", ".jpg");
                        ImageIO.write(pbi, "jpg", poster);

                        String uri = fanart.toURI().toString();
                        n.save(NMSConstants.FANART_IMAGE_TYPE, uri, sid);

                        uri = poster.toURI().toString();
                        n.save(NMSConstants.POSTER_IMAGE_TYPE, uri, sid);
                    }
                }

            } catch (IOException ex) {

                LogUtil.log(LogUtil.DEBUG, ex.getMessage());
            }
        }
    }

    private long computeDuration(Video v) {

        long result = 0L;

        if (v != null) {

            MediainfoJob job = new MediainfoJob(v);
            JobContainer jc = JobManager.getJobContainer(job);
            jc.start();

            boolean done = false;
            int count = 0;
            while (!done) {

                if (!jc.isAlive()) {

                    done = true;

                } else {

                    count += 100;
                    if (count > 2900) {

                        done = true;

                    } else {

                        JobManager.sleep(100);
                    }
                }
            }

            result = (long) job.getSeconds();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void videoScan() {

        LogUtil.log(LogUtil.INFO, "Time to scan for video files...");

        // First we are going to see if the DB video has been
        // changed to an mp4 extension.  This is really hack code
        // and we should remove later.  What we will do is if the
        // file path doesn't exist, but an mp4 version does, then
        // we will update the database.
        Video[] currentVids = getVideos();
        if ((currentVids != null) && (currentVids.length > 0)) {

            for (int i = 0; i < currentVids.length; i++) {

                Video v = currentVids[i];
                String path = v.getPath();
                File vfile = new File(path);
                if (!vfile.exists()) {

                    // It's gone.  See if there is an mp4 version.
                    path = path.substring(0, path.lastIndexOf("."));
                    path = path + ".mp4";
                    vfile = new File(path);
                    if (vfile.exists()) {

                        v.setPath(path);
                        v.setFilename(vfile.getName());
                        addVideo(v);
                    }
                }
            }
        }

        String[] array = getConfiguredVideoDirectories();
        if (array != null) {

            String[] exts = getConfiguredVideoExtensions();
            for (int i = 0; i < array.length; i++) {

                File dir = new File(array[i]);
                if ((dir.exists()) && (dir.isDirectory())) {

                    FileFind ff = FileFind.getInstance();
                    File[] files = ff.find(dir, exts);
                    if (files != null) {

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
                                v.setHidden(false);
                                if (v.isTV()) {
                                    v.setSubcategory(v.getTitle() + " Season "
                                        + v.getSeason());
                                } else {
                                    v.setSubcategory(
                                        NMSConstants.UNKNOWN_GENRE);
                                }
                                v.setDuration(computeDuration(v));
                                addVideo(v);

                            } else if (path != null) {

                                if ((!path.equals(v.getPath()))
                                    || (v.isHidden())) {

                                    v.setPath(path);
                                    v.setHidden(false);
                                    addVideo(v);
                                }
                            }
                        }
                    }
                }
            }
        }

        removeMissingPaths();
    }

    private void removeMissingPaths() {

        Video[] array = getVideos();
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                String title = array[i].getTitle();
                String path = array[i].getPath();
                if ((title != null) && (path != null)) {

                    File f = new File(path);
                    if (!f.exists()) {

                        // Check to see if we should set it hidden or
                        // purge it from the DB.
                        File vpurge = new File("vpurge");
                        if (vpurge.exists()) {

                            removeVideoFromDB(array[i]);

                        } else {

                            LogUtil.log(LogUtil.INFO, "Will hide <" + title
                                + "> with path <" + path + ">");
                            array[i].setHidden(true);
                            addVideo(array[i]);
                        }
                    }
                }
            }
        }
    }

    private String guessVideoTitle(String s, boolean tv) {

        String result = s;

        if (result != null) {

            result = result.replaceAll("_", " ");
            result = result.replaceAll("-", " ");
            if (tv) {

                int index = result.lastIndexOf(" ");
                if (index != -1) {
                    result = result.substring(0, index);
                }
            }

            result = result.trim();
        }

        return (result);
    }

    private String guessVideoCategory(String s) {

        String result = NMSConstants.VIDEO_MOVIE;

        Pattern p = getPattern();
        if (p != null) {

            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {

                result = NMSConstants.VIDEO_TV;
            }
        }

        return (result);
    }

    private int guessSeason(String s) {

        int result = 1;

        Pattern p = getPattern();
        if (p != null) {

            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {

                String tmp = matcher.group();
                tmp = tmp.substring(1, 3);
                result = Util.str2int(tmp, result);
            }
        }

        return (result);
    }

    private int guessEpisode(String s) {

        int result = 1;

        Pattern p = getPattern();
        if (p != null) {

            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {

                String tmp = matcher.group();
                tmp = tmp.substring(4);
                result = Util.str2int(tmp, result);
            }
        }

        return (result);
    }

    private File getImageHome() {

        File result = null;

        NMS n = getNMS();
        if (n instanceof BaseNMS) {

            BaseNMS bn = (BaseNMS) n;
            String path = bn.getConfiguredImageHome();
            if (path != null) {

                result = new File(path);
            }
        }

        return (result);
    }

    private String getImageName(String s) {

        String result = "no_image.jpg";

        if (s != null) {

            File f = getImageHome();
            if (f != null) {

                File iname = new File(f, s);
                if ((iname.exists()) && (iname.isFile())) {

                    result = s;
                }
            }
        }

        return (result);
    }

    static class VideoSortByAdded implements Comparator<Video>, Serializable {

        public VideoSortByAdded() {
        }

        public int compare(Video v0, Video v1) {

            Long l0 = Long.valueOf(v0.getAdded());
            Long l1 = Long.valueOf(v1.getAdded());

            return (l1.compareTo(l0));
        }
    }

}

