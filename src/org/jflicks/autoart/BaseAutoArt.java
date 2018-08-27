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
package org.jflicks.autoart;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.BaseNMS;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This class is a base implementation of the AutoArt interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseAutoArt extends BaseConfig implements AutoArt {

    public enum IdType {
        SERIES, SHOW
    }

    private String title;
    private NMS nms;
    private IdType idTypeForSearch;

    /**
     * Simple empty constructor.
     */
    public BaseAutoArt() {

        setIdTypeForSearch(IdType.SERIES);
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    public IdType getIdTypeForSearch() {
        return (idTypeForSearch);
    }

    public void setIdTypeForSearch(IdType i) {
        idTypeForSearch = i;
    }

    /**
     * The AutoArt service needs to know hoe often it should run and
     * try to check for more art to find for it's Recordings, Rules and
     * Videos.
     *
     * @return The number of minutes between runs, defaults to 20.
     */
    public int getConfiguredUpdateTimeInMinutes() {

        int result = 1;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.UPDATE_TIME_IN_MINUTES);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

    public File getImageHome() {

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

    private String getVideoArtId(Video v) {

        String result = null;

        if (v != null) {

            if (v.isTV()) {

                result = v.getSubcategory();
                if (result != null) {

                    result = result.replaceAll(" ", "_");
                    result = result.replaceAll("'", "_");
                    result = result.replaceAll(",", "_");
                }

            } else {
                result = v.getId();
            }
        }

        return (result);
    }

    private String getRecordingRuleArtId(RecordingRule rr) {

        String result = null;

        if (rr != null) {

            if (getIdTypeForSearch() == IdType.SERIES) {
                result = rr.getSeriesId();
            } else {
                result = rr.getShowId();
            }
            if (result == null) {

                ShowAiring sa = rr.getShowAiring();
                if (sa != null) {

                    Show s = sa.getShow();
                    if (s != null) {

                        if (getIdTypeForSearch() == IdType.SERIES) {
                            result = s.getSeriesId();
                        } else {
                            result = s.getId();
                        }
                    }
                }
            }
        }

        return (result);
    }

    public boolean hasArt(String id, String suffix) {

        boolean result = false;

        File ihome = getImageHome();
        if ((id != null) && (suffix != null) && (ihome != null)) {

            File f = new File(ihome, id + suffix);
            result = f.exists() && f.isFile();
        }

        return (result);
    }

    public boolean hasBannerArt(Recording r) {

        boolean result = false;

        if (r != null) {
            result = hasArt(r.getSeriesId(), "_banner.jpg");
        }

        return (result);
    }

    public boolean hasPosterArt(Recording r) {

        boolean result = false;

        if (r != null) {
            result = hasArt(r.getSeriesId(), "_poster.jpg");
        }

        return (result);
    }

    public boolean hasFanArt(Recording r) {

        boolean result = false;

        if (r != null) {
            result = hasArt(r.getSeriesId(), "_fanart.jpg");
        }

        return (result);
    }

    public boolean hasBannerArt(Video v) {

        boolean result = false;

        if (v != null) {
            result = hasArt(getVideoArtId(v), "_banner.jpg");
        }

        return (result);
    }

    public boolean hasPosterArt(Video v) {

        boolean result = false;

        if (v != null) {
            result = hasArt(getVideoArtId(v), "_poster.jpg");
        }

        return (result);
    }

    public boolean hasFanArt(Video v) {

        boolean result = false;

        if (v != null) {
            result = hasArt(getVideoArtId(v), "_fanart.jpg");
        }

        return (result);
    }

    public boolean hasMetadata(Video v) {

        boolean result = false;

        if (v != null) {
            result = v.getDescription() != null;
        }

        return (result);
    }

    public boolean hasBannerArt(RecordingRule rr) {

        boolean result = false;

        if (rr != null) {
            result = hasArt(getRecordingRuleArtId(rr), "_banner.jpg");
        }

        return (result);
    }

    public boolean hasPosterArt(RecordingRule rr) {

        boolean result = false;

        if (rr != null) {
            result = hasArt(getRecordingRuleArtId(rr), "_poster.jpg");
        }

        return (result);
    }

    public boolean hasFanArt(RecordingRule rr) {

        boolean result = false;

        if (rr != null) {
            result = hasArt(getRecordingRuleArtId(rr), "_fanart.jpg");
        }

        return (result);
    }

    public Recording[] getRecordings() {

        Recording[] result = null;

        NMS n = getNMS();
        if (n != null) {

            Recording[] array = n.getRecordings();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Recording> l = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    // We look for a complete set, otherwise we add it
                    // to our list.
                    if ((!hasBannerArt(array[i])) || (!hasPosterArt(array[i])) || (!hasFanArt(array[i]))) {

                        l.add(array[i]);
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new Recording[l.size()]);
                }
            }
        }

        return (result);
    }

    public RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        NMS n = getNMS();
        if (n != null) {

            RecordingRule[] array = n.getRecordingRules();
            if ((array != null) && (array.length > 0)) {

                ArrayList<RecordingRule> l = new ArrayList<RecordingRule>();
                for (int i = 0; i < array.length; i++) {

                    // We look for a complete set, otherwise we add it
                    // to our list.
                    if ((!hasBannerArt(array[i])) || (!hasPosterArt(array[i])) || (!hasFanArt(array[i]))) {

                        l.add(array[i]);
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new RecordingRule[l.size()]);
                }
            }
        }

        return (result);
    }

    public Video[] getVideos() {

        Video[] result = null;

        NMS n = getNMS();
        if (n != null) {

            Video[] array = n.getVideos();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Video> l = new ArrayList<Video>();
                for (int i = 0; i < array.length; i++) {

                    if (!array[i].isHome()) {

                        // We look for a complete set, otherwise we add it
                        // to our list.
                        if ((!hasPosterArt(array[i])) || (!hasFanArt(array[i])) || (!hasMetadata(array[i]))) {

                            l.add(array[i]);
                        }
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new Video[l.size()]);
                }
            }
        }

        return (result);
    }

    public SearchItem[] getSearchItems() {

        SearchItem[] result = null;

        ArrayList<SearchItem> l = new ArrayList<SearchItem>();

        Recording[] recs = getRecordings();
        if ((recs != null) && (recs.length > 0)) {

            for (int i = 0; i < recs.length; i++) {

                SearchItem si = new SearchItem();
                if (getIdTypeForSearch() == IdType.SERIES) {

                    si.setId(recs[i].getSeriesId());
                    si.setFileId(recs[i].getSeriesId());

                } else {

                    si.setId(recs[i].getShowId());
                    si.setFileId(recs[i].getShowId());
                }

                si.setTitle(recs[i].getTitle());
                si.setNeedBanner(!hasBannerArt(recs[i]));
                si.setNeedPoster(!hasPosterArt(recs[i]));
                si.setNeedFanart(!hasFanArt(recs[i]));

                l.add(si);
            }
        }

        RecordingRule[] rules = getRecordingRules();
        if ((rules != null) && (rules.length > 0)) {

            for (int i = 0; i < rules.length; i++) {

                SearchItem si = new SearchItem();
                si.setId(getRecordingRuleArtId(rules[i]));
                si.setFileId(getRecordingRuleArtId(rules[i]));
                si.setTitle(rules[i].getName());
                si.setNeedBanner(!hasBannerArt(rules[i]));
                si.setNeedPoster(!hasPosterArt(rules[i]));
                si.setNeedFanart(!hasFanArt(rules[i]));

                l.add(si);
            }
        }

        Video[] vids = getVideos();
        if ((vids != null) && (vids.length > 0)) {

            for (int i = 0; i < vids.length; i++) {

                SearchItem si = new SearchItem();
                si.setId(vids[i].getId());
                si.setFileId(getVideoArtId(vids[i]));
                si.setTitle(vids[i].getTitle());
                si.setVideoId(vids[i].getId());
                si.setSeason(vids[i].getSeason());
                si.setEpisode(vids[i].getEpisode());
                si.setNeedBanner(!hasBannerArt(vids[i]));
                si.setNeedPoster(!hasPosterArt(vids[i]));
                si.setNeedFanart(!hasFanArt(vids[i]));
                si.setNeedMetadata(vids[i].getDescription() == null);

                l.add(si);
            }
        }

        if (l.size() > 0) {

            result = l.toArray(new SearchItem[l.size()]);
        }

        return (result);
    }

    public void save(SearchItem si) {

        NMS n = getNMS();
        if ((n != null) && (si != null)) {

            LogUtil.log(LogUtil.INFO, "save title: " + si.getTitle());
            String id = si.getFileId();
            LogUtil.log(LogUtil.INFO, "id: " + id);
            if (id != null) {

                String url = si.getBannerURL();
                LogUtil.log(LogUtil.INFO, "url: " + url);
                LogUtil.log(LogUtil.INFO, "need: " + si.isNeedBanner());
                if ((url != null) && (si.isNeedBanner())) {
                    n.save(NMSConstants.BANNER_IMAGE_TYPE, url, id);
                }

                url = si.getPosterURL();
                LogUtil.log(LogUtil.INFO, "url: " + url);
                LogUtil.log(LogUtil.INFO, "need: " + si.isNeedPoster());
                if ((url != null) && (si.isNeedPoster())) {
                    n.save(NMSConstants.POSTER_IMAGE_TYPE, url, id);
                }

                url = si.getFanartURL();
                LogUtil.log(LogUtil.INFO, "url: " + url);
                LogUtil.log(LogUtil.INFO, "need: " + si.isNeedFanart());
                if ((url != null) && (si.isNeedFanart())) {
                    n.save(NMSConstants.FANART_IMAGE_TYPE, url, id);
                }

                String vid = si.getVideoId();
                LogUtil.log(LogUtil.INFO, "vid: " + vid);
                if (vid != null) {

                    // We looked up some metadata so let's see if we
                    // have some and should update it.
                    Video video = n.getVideoById(vid);
                    if (video != null) {

                        boolean update = false;

                        String overview = video.getDescription();
                        if (overview == null) {

                            overview = si.getOverview();
                            if (overview != null) {

                                update = true;
                                video.setDescription(overview);
                            }
                        }

                        String released = video.getReleased();
                        if (released == null) {

                            released = si.getReleased();
                            if (released != null) {

                                update = true;
                                video.setReleased(released);
                            }
                        }

                        if (!video.isTV()) {

                            String subcat = video.getSubcategory();
                            if ((subcat == null) || (subcat.equals(NMSConstants.UNKNOWN_GENRE))) {

                                subcat = si.getGenre();
                                if (subcat != null) {

                                    update = true;
                                    video.setSubcategory(subcat);
                                }
                            }
                        }

                        int runtime = (int) video.getDuration();
                        if (runtime == 0) {

                            runtime = si.getRuntime();
                            if (runtime > 0) {

                                update = true;
                                video.setDuration((long) runtime);
                            }
                        }

                        if (update) {

                            n.save(video);
                        }
                    }
                }
            }
        }
    }

}

