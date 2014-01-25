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
package org.jflicks.nms;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import javax.imageio.ImageIO;

import org.jflicks.autoart.AutoArt;
import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.PhotoManager;
import org.jflicks.photomanager.Tag;
import org.jflicks.trailer.Trailer;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Listing;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.ondemand.StreamSession;
import org.jflicks.tv.programdata.DataUpdateEvent;
import org.jflicks.tv.programdata.DataUpdateListener;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.tv.recorder.BaseRecorder;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.RecordedShow;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.EventSender;
import org.jflicks.util.StartsWithFilter;
import org.jflicks.util.Util;
import org.jflicks.videomanager.VideoManager;

/**
 * This class is a base implementation of the NMS interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseNMS extends BaseConfig implements NMS,
    DataUpdateListener {

    private String title;
    private String host;
    private String groupName;
    private int port;
    private int httpPort;
    private Scheduler scheduler;
    private PostProc postProc;
    private Live live;
    private PhotoManager photoManager;
    private VideoManager videoManager;
    private AutoArt autoArt;
    private ArrayList<Recorder> recorderList;
    private ArrayList<ProgramData> programDataList;
    private ArrayList<OnDemand> onDemandList;
    private ArrayList<Trailer> trailerList;
    private ArrayList<Recording> removeRecordingList;
    private EventSender eventSender;
    private HashMap<Channel, ArrayList<ShowAiring>> showAiringCacheMap;

    /**
     * Simple empty constructor.
     */
    public BaseNMS() {

        setRecorderList(new ArrayList<Recorder>());
        setProgramDataList(new ArrayList<ProgramData>());
        setOnDemandList(new ArrayList<OnDemand>());
        setTrailerList(new ArrayList<Trailer>());
        setRemoveRecordingList(new ArrayList<Recording>());
        setShowAiringCacheMap(new HashMap<Channel, ArrayList<ShowAiring>>());
    }

    /**
     * {@inheritDoc}
     */
    public Scheduler getScheduler() {
        return (scheduler);
    }

    /**
     * Convenience method to set the Scheduler property.
     *
     * @param s A given Scheduler instance.
     */
    public void setScheduler(Scheduler s) {

        scheduler = s;
        if (s != null) {

            s.setNMS(this);
            Configuration def = s.getDefaultConfiguration();
            save(def, false);
            s.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Live getLive() {
        return (live);
    }

    /**
     * Convenience method to set the Live property.
     *
     * @param l A given Live instance.
     */
    public void setLive(Live l) {

        live = l;
        if (l != null) {

            l.setNMS(this);
            Configuration def = l.getDefaultConfiguration();
            save(def, false);
            l.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public PhotoManager getPhotoManager() {
        return (photoManager);
    }

    /**
     * Convenience method to set the PhotoManager property.
     *
     * @param p A given PhotoManager instance.
     */
    public void setPhotoManager(PhotoManager p) {

        photoManager = p;
        if (p != null) {

            p.setNMS(this);
            Configuration def = p.getDefaultConfiguration();
            save(def, false);
            p.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public VideoManager getVideoManager() {
        return (videoManager);
    }

    /**
     * Convenience method to set the VideoManager property.
     *
     * @param vm A given VideoManager instance.
     */
    public void setVideoManager(VideoManager vm) {

        videoManager = vm;
        if (vm != null) {

            vm.setNMS(this);
            Configuration def = vm.getDefaultConfiguration();
            save(def, false);
            vm.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public AutoArt getAutoArt() {
        return (autoArt);
    }

    /**
     * Convenience method to set the AutoArt property.
     *
     * @param aa A given AutoArt instance.
     */
    public void setAutoArt(AutoArt aa) {

        autoArt = aa;
        if (aa != null) {

            aa.setNMS(this);
            Configuration def = aa.getDefaultConfiguration();
            save(def, false);
            aa.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public PostProc getPostProc() {
        return (postProc);
    }

    /**
     * Convenience method to set the PostProc property.
     *
     * @param pp A given PostProc instance.
     */
    public void setPostProc(PostProc pp) {

        postProc = pp;
        if (pp != null) {

            pp.setNMS(this);
            Configuration def = pp.getDefaultConfiguration();
            save(def, false);
            pp.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Task[] getTasks() {

        Task[] result = null;

        PostProc pp = getPostProc();
        if (pp != null) {

            Worker[] array = pp.getWorkers();
            if (array != null) {

                result = new Task[array.length];
                for (int i = 0; i < result.length; i++) {

                    result[i] = new Task();
                    result[i].setTitle(array[i].getTitle());
                    result[i].setDescription(array[i].getDescription());
                    result[i].setDefaultRun(array[i].isDefaultRun());
                    result[i].setRun(array[i].isDefaultRun());
                    result[i].setSelectable(array[i].isUserSelectable());
                    result[i].setIndexer(array[i].isIndexer());
                    result[i].setCommercialDetector(
                        array[i].isCommercialDetector());
                }
            }
        }

        return (result);
    }

    private ArrayList<Recorder> getRecorderList() {
        return (recorderList);
    }

    private void setRecorderList(ArrayList<Recorder> l) {
        recorderList = l;
    }

    private ArrayList<ProgramData> getProgramDataList() {
        return (programDataList);
    }

    private void setProgramDataList(ArrayList<ProgramData> l) {
        programDataList = l;
    }

    private ArrayList<Trailer> getTrailerList() {
        return (trailerList);
    }

    private void setTrailerList(ArrayList<Trailer> l) {
        trailerList = l;
    }

    /**
     * We have a current list of Recordings to be removed.
     *
     * @return A List of Recording instances.
     */
    public ArrayList<Recording> getRemoveRecordingList() {
        return (removeRecordingList);
    }

    private void setRemoveRecordingList(ArrayList<Recording> l) {
        removeRecordingList = l;
    }

    private HashMap<Channel, ArrayList<ShowAiring>> getShowAiringCacheMap() {
        return (showAiringCacheMap);
    }

    private void setShowAiringCacheMap(
        HashMap<Channel, ArrayList<ShowAiring>> m) {

        showAiringCacheMap = m;
    }

    private void clearShowAiringCacheMap() {

        HashMap<Channel, ArrayList<ShowAiring>> m = getShowAiringCacheMap();
        if (m != null) {

            m.clear();
        }
    }

    private ArrayList<ShowAiring> getShowAiringListByChannel(Channel c) {

        ArrayList<ShowAiring> result = null;

        HashMap<Channel, ArrayList<ShowAiring>> m = getShowAiringCacheMap();
        if ((c != null) && (m != null)) {

            result = m.get(c);
        }

        return (result);
    }

    private void addShowAiringListByChannel(Channel c,
        ArrayList<ShowAiring> l) {

        HashMap<Channel, ArrayList<ShowAiring>> m = getShowAiringCacheMap();
        if ((c != null) && (l != null) && (m != null)) {

            m.put(c, l);
        }
    }

    private ArrayList<OnDemand> getOnDemandList() {
        return (onDemandList);
    }

    private void setOnDemandList(ArrayList<OnDemand> l) {
        onDemandList = l;
    }

    /**
     * Convenience method for extensions to add a recorder instance.
     *
     * @param r A Recorder to add.
     */
    public void addRecorder(Recorder r) {

        ArrayList<Recorder> l = getRecorderList();
        if ((l != null) && (r != null)) {

            l.add(r);
            Collections.sort(l, new RecorderSortByTitleDevice());

            Configuration def = r.getDefaultConfiguration();
            save(def, false);
            r.setConfiguration(getConfigurationBySource(def.getSource()));

            if (r instanceof BaseRecorder) {

                BaseRecorder br = (BaseRecorder) r;
                br.setNMS(this);
            }
        }
    }

    /**
     * Convenience method for extensions to remove a recorder instance.
     *
     * @param r A Recorder to remove.
     */
    public void removeRecorder(Recorder r) {

        ArrayList<Recorder> l = getRecorderList();
        if ((l != null) && (r != null)) {
            l.remove(r);
        }
    }

    /**
     * Convenience method for extensions to add a ProgramData instance.
     *
     * @param pd A ProgramData to add.
     */
    public void addProgramData(ProgramData pd) {

        ArrayList<ProgramData> l = getProgramDataList();
        if ((l != null) && (pd != null)) {

            pd.addDataUpdateListener(this);
            l.add(pd);

            Configuration def = pd.getDefaultConfiguration();
            save(def, false);
            pd.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * Convenience method for extensions to remove a ProgramData instance.
     *
     * @param pd A ProgramData to remove.
     */
    public void removeProgramData(ProgramData pd) {

        ArrayList<ProgramData> l = getProgramDataList();
        if ((l != null) && (pd != null)) {
            l.remove(pd);
        }
    }

    /**
     * Convenience method for extensions to add a OnDemand instance.
     *
     * @param o A OnDemand to add.
     */
    public void addOnDemand(OnDemand o) {

        ArrayList<OnDemand> l = getOnDemandList();
        if ((l != null) && (o != null)) {

            l.add(o);

            o.setNMS(this);
            Configuration def = o.getDefaultConfiguration();
            save(def, false);
            o.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * Convenience method for extensions to remove a OnDemand instance.
     *
     * @param o A OnDemand to remove.
     */
    public void removeOnDemand(OnDemand o) {

        ArrayList<OnDemand> l = getOnDemandList();
        if ((l != null) && (o != null)) {
            l.remove(o);
        }
    }

    /**
     * {@inheritDoc}
     */
    public OnDemand[] getOnDemands() {

        OnDemand[] result = null;

        ArrayList<OnDemand> l = getOnDemandList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new OnDemand[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method for extensions to add a Trailer instance.
     *
     * @param t A Trailer to add.
     */
    public void addTrailer(Trailer t) {

        ArrayList<Trailer> l = getTrailerList();
        if ((l != null) && (t != null)) {

            l.add(t);

            t.setNMS(this);
            Configuration def = t.getDefaultConfiguration();
            save(def, false);
            t.setConfiguration(getConfigurationBySource(def.getSource()));
        }
    }

    /**
     * Convenience method for extensions to remove a Trailer instance.
     *
     * @param t A Trailer to remove.
     */
    public void removeTrailer(Trailer t) {

        ArrayList<Trailer> l = getTrailerList();
        if ((l != null) && (t != null)) {
            l.remove(t);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Trailer[] getTrailers() {

        Trailer[] result = null;

        ArrayList<Trailer> l = getTrailerList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Trailer[l.size()]);
        }

        return (result);
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
    public Recorder[] getRecorders() {

        Recorder[] result = null;

        ArrayList<Recorder> l = getRecorderList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Recorder[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Recorder getRecorderByDevice(String s) {

        Recorder result = null;

        ArrayList<Recorder> l = getRecorderList();
        if ((l != null) && (s != null)) {

            log(DEBUG, "getRecorderByDevice: <" + s + ">");
            log(DEBUG, "getRecorderByDevice: " + l.size());
            for (int i = 0; i < l.size(); i++) {

                Recorder tmp = l.get(i);
                log(DEBUG, "getRecorderByDevice: <" + tmp.getDevice() + ">");
                if (s.equals(tmp.getDevice())) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ProgramData[] getProgramData() {

        ProgramData[] result = null;

        ArrayList<ProgramData> l = getProgramDataList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new ProgramData[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestProgramDataUpdate() {

        boolean result = false;

        ProgramData[] array = getProgramData();
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                boolean willdo = array[i].requestUpdate();
                if (willdo) {
                    result = true;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        return (host);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given host value.
     */
    public void setHost(String s) {
        host = s;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return (port);
    }

    /**
     * Convenience method to set this property.
     *
     * @param i The given port value.
     */
    public void setPort(int i) {
        port = i;
    }

    /**
     * {@inheritDoc}
     */
    public int getHttpPort() {
        return (httpPort);
    }

    /**
     * Convenience method to set this property.
     *
     * @param i The given http port value.
     */
    public void setHttpPort(int i) {
        httpPort = i;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTrailerURLs() {

        String[] result = null;

        String thome = getTrailerHome();
        String tintro = getTrailerIntro();
        if ((thome != null) && (tintro != null)) {

            File dir = new File(thome);
            File[] all = dir.listFiles();
            if ((all != null) && (all.length > 0)) {

                Arrays.sort(all, new FileSort());
                result = new String[all.length + 1];
                result[0] = computeStreamURL(tintro);
                for (int i = 1; i < result.length; i++) {

                    result[i] = computeStreamURL(all[i - 1].getPath());
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getTrailerHome() {
        return (getConfiguredTrailerHome());
    }

    /**
     * {@inheritDoc}
     */
    public String getTrailerIntro() {
        return (getConfiguredTrailerIntro());
    }

    /**
     * {@inheritDoc}
     */
    public String getFeatureIntro169() {
        return (getConfiguredFeatureIntro169());
    }

    /**
     * {@inheritDoc}
     */
    public String getFeatureIntro235() {
        return (getConfiguredFeatureIntro235());
    }

    /**
     * {@inheritDoc}
     */
    public String getFeatureIntro43() {
        return (getConfiguredFeatureIntro43());
    }

    /**
     * {@inheritDoc}
     */
    public String getGroupName() {
        return (getConfiguredGroupName());
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStreamPaths() {
        return (getConfiguredStreamPaths());
    }

    /**
     * {@inheritDoc}
     */
    public String getDocumentRoot() {
        return (getConfiguredDocumentRoot());
    }

    /**
     * {@inheritDoc}
     */
    public int getStreamPort() {
        return (getConfiguredStreamPort());
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfigurationBySource(String s) {

        Configuration result = null;

        Configuration[] array = getConfigurations();
        if ((s != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (array[i].isSource(s)) {

                    result = array[i];
                    break;
                }
            }
        }
        return (result);
    }

    /**
     * Convenience method to get the configured value of IMAGE_HOME, the
     * directory where metadata images will be stored.
     *
     * @return A String object.
     */
    public String getConfiguredImageHome() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.IMAGE_HOME);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of TRAILER_HOME, the
     * directory where downloaded trailers will be stored.
     *
     * @return A String object.
     */
    public String getConfiguredTrailerHome() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.TRAILER_HOME);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of TRAILER_INTRO, the
     * file that is an intro to the trailers.
     *
     * @return A String object.
     */
    public String getConfiguredTrailerIntro() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.TRAILER_INTRO);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of FEATURE_INTRO_169,
     * the file that is an intro to a feature in 16:9 aspect ratio.
     *
     * @return A String object.
     */
    public String getConfiguredFeatureIntro169() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.FEATURE_INTRO_169);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of FEATURE_INTRO_235,
     * the file that is an intro to a feature in 2.35:1 aspect ratio.
     *
     * @return A String object.
     */
    public String getConfiguredFeatureIntro235() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.FEATURE_INTRO_235);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of FEATURE_INTRO_43,
     * the file that is an intro to a feature in 4:3 aspect ratio.
     *
     * @return A String object.
     */
    public String getConfiguredFeatureIntro43() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.FEATURE_INTRO_43);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of GROUP_NAME.
     *
     * @return A String object.
     */
    public String getConfiguredGroupName() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.GROUP_NAME);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of STREAM_PORT.
     *
     * @return An int value.
     */
    public int getConfiguredStreamPort() {

        int result = 80;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.STREAM_PORT);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of HTTP_DOCUMENT_ROOT.
     *
     * @return A String object.
     */
    public String getConfiguredDocumentRoot() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.HTTP_DOCUMENT_ROOT);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of STREAM_PATHS.
     *
     * @return A String array.
     */
    public String[] getConfiguredStreamPaths() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.STREAM_PATHS);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getRecordableChannels() {

        Channel[] result = null;

        Scheduler s = getScheduler();
        if (s != null) {

            result = s.getRecordableChannels();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAiringsByChannel(Channel c) {

        ShowAiring[] result = null;

        ArrayList<ShowAiring> list = getShowAiringListByChannel(c);

        if (list == null) {

            ProgramData[] array = getProgramData();
            if ((array != null) && (c != null)) {

                list = new ArrayList<ShowAiring>();
                for (int i = 0; i < array.length; i++) {

                    Airing[] airs = array[i].getAiringsByChannel(c);
                    if (airs != null) {

                        String hp = getHost() + ":" + getPort();
                        for (int j = 0; j < airs.length; j++) {

                            Show show = array[i].getShowByAiring(airs[j]);
                            if (show != null) {

                                ShowAiring sa = new ShowAiring(show, airs[j]);
                                sa.setHostPort(hp);
                                list.add(sa);
                            }
                        }
                    }
                }

                if (list.size() > 0) {

                    addShowAiringListByChannel(c, list);
                }
            }
        }

        if ((list != null) && (list.size() > 0)) {

            Collections.sort(list);
            long now = System.currentTimeMillis();
            int count = 0;
            for (int i = 0; i < list.size(); i++) {

                Airing a = list.get(i).getAiring();
                if (a != null) {

                    Date d = a.getAirDate();
                    if (d != null) {

                        if (d.getTime() > now) {
                            break;
                        } else {
                            count++;
                        }

                    } else {
                        count++;
                    }

                } else {
                    count++;
                }
            }

            count--;
            for (int i = 0; i < count; i++) {
                list.remove(0);
            }
            result = list.toArray(new ShowAiring[list.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c,
        String seriesId) {

        ShowAiring[] result = null;

        ProgramData[] array = getProgramData();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                result =
                    array[i].getShowAiringsByChannelAndSeriesId(c, seriesId);
                if (result != null) {

                    Arrays.sort(result);

                    String hp = getHost() + ":" + getPort();
                    for (int j = 0; j < result.length; j++) {

                        result[j].setHostPort(hp);
                    }
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAirings(String pattern, int searchType) {

        ShowAiring[] result = null;

        ProgramData[] array = getProgramData();
        Channel[] chans = getRecordableChannels();
        if ((array != null) && (chans != null)) {

            Arrays.sort(chans);
            for (int i = 0; i < array.length; i++) {

                result = array[i].getShowAirings(pattern, searchType);
                if (result != null) {

                    result = substituteFromCache(result, chans);
                    Arrays.sort(result);

                    String hp = getHost() + ":" + getPort();
                    for (int j = 0; j < result.length; j++) {

                        result[j].setHostPort(hp);
                    }
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getChannelsByListingName(String s) {

        Channel[] result = null;

        ProgramData[] array = getProgramData();
        if ((array != null) && (s != null)) {

            for (int i = 0; i < array.length; i++) {

                Listing listing = array[i].getListingByName(s);
                if (listing != null) {

                    result = array[i].getChannelsByListing(listing);
                    if (result != null) {

                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel getChannelById(int id, String lid) {

        Channel result = null;

        ProgramData[] array = getProgramData();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                result = array[i].getChannelById(id, lid);
                if (result != null) {

                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Show getShowById(String id) {

        Show result = null;

        ProgramData[] array = getProgramData();
        if ((array != null) && (id != null)) {

            for (int i = 0; i < array.length; i++) {

                result = array[i].getShowById(id);
                if (result != null) {

                    break;
                }
            }
        }

        return (result);
    }

    private ShowAiring[] substituteFromCache(ShowAiring[] array,
        Channel[] chans) {

        ShowAiring[] result = array;

        if ((array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> l = new ArrayList<ShowAiring>();
            for (int i = 0; i < result.length; i++) {

                ShowAiring sa = substituteFromCache(array[i], chans);
                if (sa != null) {

                    l.add(sa);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new ShowAiring[l.size()]);
            }
        }

        return (result);
    }

    private ShowAiring substituteFromCache(ShowAiring sa, Channel[] chans) {

        ShowAiring result = null;

        if ((sa != null) && (chans != null)) {

            Airing a = sa.getAiring();
            if (a != null) {

                Channel c = getChannelById(a.getChannelId(), a.getListingId());
                if ((c != null) && (Arrays.binarySearch(chans, c) >= 0)) {

                    ShowAiring[] array = getShowAiringsByChannel(c);
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            if (a.equals(array[i].getAiring())) {

                                result = array[i];
                                break;
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private Task[] reconcile(Task[] array) {

        Task[] result = null;

        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                String mytitle = array[i].getTitle();
                if (mytitle != null) {

                    if (mytitle.startsWith("Comskip")) {

                        array[i].setSelectable(true);
                    }
                }
            }

            result = Arrays.copyOf(array, array.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        Scheduler s = getScheduler();
        if (s != null) {

            result = s.getRecordingRules();
            if ((result != null) && (result.length > 0)) {

                String hp = getHost() + ":" + getPort();
                for (int i = 0; i < result.length; i++) {

                    result[i].setHostPort(hp);

                    // We should sync up the PostProc Workers with the
                    // lightweight task instances here so the user gets
                    // the most recent info.  But we won't delete old
                    // workers in case it is just temporarily not deployed.
                    /*
                    Task[] update = reconcile(result[i].getTasks());
                    if (update != null) {

                        result[i].setTasks(update);
                        s.addRecordingRule(result[i]);
                    }
                    */
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Recording getRecordingById(String id) {

        Recording result = null;

        Recording[] array = getRecordings();
        if ((id != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (id.equals(array[i].getId())) {

                    result = array[i];
                    result.setHostPort(getHost() + ":" + getPort());
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Recording[] getRecordings() {

        Recording[] result = null;

        Scheduler s = getScheduler();
        log(DEBUG, "getRecordings: scheduler <" + s + ">");
        if (s != null) {

            result = s.getRecordings();
            log(DEBUG, "getRecordings: result from scheduler <" + result + ">");

            // We should update the image URLs for the client.  Persisting
            // the URLs is not a good idea because the URL could change.
            // Either by config the port changes or less likely the IP
            // changes.  Either way we will update them.  We don't check
            // if they actually exist, we will just build them by rule.
            if (result != null) {

                String h = getHost();
                int p = getHttpPort();
                if (h != null) {

                    String hp = h + ":" + port;
                    String top = "http://" + h + ":" + p + "/"
                        + NMSConstants.HTTP_IMAGES_NAME + "/";
                    for (int i = 0; i < result.length; i++) {

                        String sid = result[i].getSeriesId();
                        if (sid != null) {

                            result[i].setBannerURL(top + sid + "_banner.jpg");
                            result[i].setPosterURL(top + sid + "_poster.jpg");
                            result[i].setFanartURL(top + sid + "_fanart.jpg");
                        }
                        result[i].setHostPort(hp);
                        result[i].setStreamURL(computeStreamURL(result[i]));
                    }
                }
            }
        }

        return (result);
    }

    private String computeStreamURL(String path) {

        String result = null;

        String h = getHost();
        int streamport = getStreamPort();
        String[] array = getConfiguredStreamPaths();
        if ((h != null) && (array != null) && (path != null)) {

            boolean found = false;
            path = path.replace("\\", "/");
            for (int i = 0; i < array.length; i++) {

                if (path.startsWith(array[i])) {

                    path = path.substring(array[i].length());
                    found = true;
                    break;
                }
            }

            if ((found) && (path != null)) {

                if (path.startsWith("/")) {
                    result = "http://" + h + ":" + streamport + path;
                } else {
                    result = "http://" + h + ":" + streamport + "/" + path;
                }
            }
        }

        return (result);
    }

    private String computeStreamURL(Photo p) {

        String result = null;

        String h = getHost();
        int streamport = getStreamPort();
        String[] array = getConfiguredStreamPaths();
        if ((h != null) && (array != null) && (p != null)) {

            boolean found = false;
            String path = p.getPath();
            if (path != null) {

                path = path.replace("\\", "/");
                for (int i = 0; i < array.length; i++) {

                    if (path.startsWith(array[i])) {

                        path = path.substring(array[i].length());
                        found = true;
                        break;
                    }
                }
            }

            if ((found) && (path != null)) {

                if (path.startsWith("/")) {
                    result = "http://" + h + ":" + streamport + path;
                } else {
                    result = "http://" + h + ":" + streamport + "/" + path;
                }
            }
        }

        return (result);
    }

    private String computeStreamURL(LiveTV l) {

        String result = null;

        String h = getHost();
        int streamport = getStreamPort();
        String[] array = getConfiguredStreamPaths();
        if ((h != null) && (array != null) && (l != null)) {

            boolean found = false;
            String path = l.getPath();
            if (path != null) {

                path = path.replace("\\", "/");
                for (int i = 0; i < array.length; i++) {

                    if (path.startsWith(array[i])) {

                        path = path.substring(array[i].length());
                        found = true;
                        break;
                    }
                }
            }

            if ((found) && (path != null)) {

                if (path.startsWith("/")) {
                    result = "http://" + h + ":" + streamport + path;
                } else {
                    result = "http://" + h + ":" + streamport + "/" + path;
                }
            }
        }

        return (result);
    }

    private String computeStreamURL(Recording r) {

        String result = null;

        String h = getHost();
        int streamport = getStreamPort();
        if ((h != null) && (r != null)) {

            // We have a recording that is finished, we will let
            // apache serve it up.
            String[] array = getConfiguredStreamPaths();
            if (array != null) {

                String path = r.getPath();
                if (path != null) {

                    String iext = r.getIndexedExtension();

                    // First see if we have an mp4 file.
                    File mp4tmp = new File(path + ".mp4");
                    if (mp4tmp.exists()) {

                        // We do so override the extension in the DB.
                        // We are doing this because we are in the process
                        // of going h264/mp4 exclusively and we can update
                        // old recordings when we have a chance.  Yeah this
                        // is hackish.
                        iext = "mp4";
                        r.setIndexedExtension(iext);
                    }

                    if ((iext != null) && (iext.length() > 0)) {

                        File tmp = new File(path + "." + iext);
                        if (tmp.exists()) {

                            path = tmp.getPath();
                        }

                    } else {

                        String trunk = path.substring(0, path.lastIndexOf("."));
                        File tmp = new File(trunk + ".m3u8");
                        if (tmp.exists()) {

                            path = tmp.getPath();
                        }
                    }
                }

                boolean found = false;
                if (path != null) {

                    path = path.replace("\\", "/");
                    for (int i = 0; i < array.length; i++) {

                        if (path.startsWith(array[i])) {

                            path = path.substring(array[i].length());
                            found = true;
                            break;
                        }
                    }
                }

                if ((found) && (path != null)) {

                    if (path.startsWith("/")) {
                        result = "http://" + h + ":" + streamport + path;
                    } else {
                        result = "http://" + h + ":" + streamport + "/" + path;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Really remove the recording files.
     *
     * @param r The Recording to remove.
     */
    public void performRemoval(Recording r) {

        log(DEBUG, "Recording to physically remove: <" + r + ">");
        if (r != null) {

            String path = r.getPath();
            if (path != null) {

                File file = new File(path);
                String iext = r.getIndexedExtension();

                // Of course delete the file.
                if (!file.delete()) {

                    log(WARNING, file.getPath() + " delete fail");
                }

                // The screenshot is a "filename.png" file.
                File pngfile = new File(file.getPath() + ".png");
                if (!pngfile.delete()) {

                    log(WARNING, pngfile.getPath() + " del fail");
                }

                // The index file is "filename.iext".
                if (iext != null) {

                    File iextfile = new File(file.getPath() + "." + iext);
                    if (!iextfile.delete()) {

                        log(WARNING, iextfile.getPath() + " del fail");
                    }
                }

                // Other recorders or processes might add other files
                // with the rule of a different extension.  Let's find
                // them and delete them.
                File parent = file.getParentFile();
                String fname = file.getName();
                if ((parent != null) && (fname != null)) {

                    // This really should get everything as it
                    // includes the show ID and time up to the
                    // minute.
                    fname = fname.substring(0, fname.lastIndexOf("_"));
                    File[] array =
                        parent.listFiles(new StartsWithFilter(fname));
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            if (!array[i].delete()) {

                                log(WARNING, array[i].getPath() + " del fail");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeRecording(Recording r, boolean allowRerecord) {

        log(DEBUG, "removeRecording: allowRerecord: " + allowRerecord);
        Scheduler s = getScheduler();
        if ((s != null) && (r != null)) {

            // Clients can muck with the properties of a Recording.  We
            // need to have an instance that is meaningful to us so lets
            // look it up by Id.
            r = getRecordingById(r.getId());

            s.removeRecording(r);
            if (allowRerecord) {

                s.removeRecordedShow(new RecordedShow(r.getShowId()));
                s.requestRescheduling();
            }

            ArrayList<Recording> l = getRemoveRecordingList();
            if (l != null) {

                synchronized (l) {

                    log(DEBUG, "Adding Recording to remove when we are "
                        + "not busy. <" + r + ">");
                    l.add(r);
                }
            }
            /*
            String path = r.getPath();
            if (path != null) {

                final File file = new File(path);
                final String iext = r.getIndexedExtension();
                ActionListener taskPerformer = new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {

                        // Of course delete the file.
                        if (!file.delete()) {

                            log(WARNING, file.getPath() + " delete fail");
                        }

                        // The screenshot is a "filename.png" file.
                        File pngfile = new File(file.getPath() + ".png");
                        if (!pngfile.delete()) {

                            log(WARNING, pngfile.getPath() + " del fail");
                        }

                        // The index file is "filename.iext".
                        if (iext != null) {

                            File iextfile =
                                new File(file.getPath() + "." + iext);
                            if (!iextfile.delete()) {

                                log(WARNING, iextfile.getPath() + " del fail");
                            }
                        }

                        // Other recorders or processes might add other files
                        // with the rule of a different extension.  Let's find
                        // them and delete them.
                        File parent = file.getParentFile();
                        String fname = file.getName();
                        if ((parent != null) && (fname != null)) {

                            // This really should get everything as it
                            // includes the show ID and time up to the
                            // minute.
                            fname = fname.substring(0, fname.lastIndexOf("_"));
                            File[] array =
                                parent.listFiles(new StartsWithFilter(fname));
                            if (array != null) {

                                for (int i = 0; i < array.length; i++) {

                                    if (!array[i].delete()) {

                                        log(WARNING, array[i].getPath()
                                            + " del fail");
                                    }
                                }
                            }
                        }
                    }
                };
                Timer timer = new Timer(1000, taskPerformer);
                timer.setRepeats(false);
                timer.start();
            }
            */

            sendMessage(NMSConstants.MESSAGE_RECORDING_REMOVED);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording(Recording r) {

        log(INFO, "stopRecording <" + r + ">");
        Scheduler s = getScheduler();
        if ((s != null) && (r != null)) {

            // Clients can muck with the properties of a Recording.  We
            // need to have an instance that is meaningful to us so lets
            // look it up by Id.
            r = getRecordingById(r.getId());

            boolean found = false;
            Recorder[] array = s.getConfiguredRecorders();
            if ((array != null) && (r != null)) {

                for (int i = 0; i < array.length; i++) {

                    if (array[i].isRecording(r)) {

                        log(DEBUG, "Stopping <" + array[i] + ">");
                        array[i].stopRecording();
                        found = true;
                        break;
                    }
                }
            }

            // Now if we didn't find the Recorder, we may be in a funky
            // mode.  So let's be trustful and flag the recording as stopped.
            if (!found) {

                r.setCurrentlyRecording(false);
                long now = System.currentTimeMillis();
                r.setDuration((now - r.getRealStart()) / 1000L);
                s.updateRecording(r);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        Scheduler s = getScheduler();
        if (s != null) {

            result = s.getUpcomings();

            // We should update the image URLs for the client.  Persisting
            // the URLs is not a good idea because the URL could change.
            // Either by config the port changes or less likely the IP
            // changes.  Either way we will update them.  We don't check
            // if they actually exist, we will just build them by rule.
            if (result != null) {

                String h = getHost();
                int p = getHttpPort();
                if (h != null) {

                    String hp = h + ":" + port;
                    String top = "http://" + h + ":" + p + "/"
                        + NMSConstants.HTTP_IMAGES_NAME + "/";
                    for (int i = 0; i < result.length; i++) {

                        String sid = result[i].getSeriesId();
                        if (sid != null) {

                            result[i].setBannerURL(top + sid + "_banner.jpg");
                            result[i].setPosterURL(top + sid + "_poster.jpg");
                            result[i].setFanartURL(top + sid + "_fanart.jpg");
                        }
                        result[i].setHostPort(hp);
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void overrideUpcoming(Upcoming u) {

        Scheduler s = getScheduler();
        if ((u != null) && (s != null)) {

            String sid = u.getShowId();
            if (sid != null) {

                RecordedShow rs = new RecordedShow(sid);
                if (NMSConstants.PREVIOUSLY_RECORDED.equals(u.getStatus())) {

                    // This means we want to forget the old recording.
                    s.removeRecordedShow(rs);

                } else {

                    // Anything here means pretend we already recorded it.
                    s.addRecordedShow(rs);
                }

                s.requestRescheduling();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void save(int imageType, byte[] data, String id) {

        if ((data != null) && (id != null)) {

            String imageHome = getConfiguredImageHome();
            if (imageHome == null) {
                imageHome = ".";
            }

            String name = null;
            String rokusdname = null;
            String rokuhdname = null;
            switch (imageType) {

            default:
            case NMSConstants.BANNER_IMAGE_TYPE:
                name = imageHome + "/" + id + "_banner.jpg";
                break;

            case NMSConstants.FANART_IMAGE_TYPE:
                name = imageHome + "/" + id + "_fanart.jpg";
                //rokuhdname = imageHome + "/" + id + "_fanart_roku_hd.jpg";
                //rokusdname = imageHome + "/" + id + "_fanart_roku_sd.jpg";
                break;

            case NMSConstants.POSTER_IMAGE_TYPE:
                name = imageHome + "/" + id + "_poster.jpg";
                break;

            }
            try {

                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                BufferedImage bi = ImageIO.read(bais);
                if (bi != null) {

                    ImageIO.write(bi, "jpg", new File(name));

                    if (rokuhdname != null) {

                        BufferedImage hd = createImage(bi, 388);
                        hd = hd.getSubimage(49, 0, 290, 218);
                        ImageIO.write(hd, "jpg", new File(rokuhdname));

                        BufferedImage sd = createImage(bi, 256);
                        sd = sd.getSubimage(21, 0, 214, 144);
                        ImageIO.write(sd, "jpg", new File(rokusdname));
                    }

                } else {

                    log(WARNING, "can't load <" + data + ">");
                }

            } catch (IOException ex) {

                log(WARNING, "save image: " + ex.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(int imageType, String url, String id) {

        if ((url != null) && (id != null)) {

            String imageHome = getConfiguredImageHome();
            if (imageHome == null) {
                imageHome = ".";
            }

            String name = null;
            String rokusdname = null;
            String rokuhdname = null;
            switch (imageType) {

            default:
            case NMSConstants.BANNER_IMAGE_TYPE:
                name = imageHome + "/" + id + "_banner.jpg";
                break;

            case NMSConstants.FANART_IMAGE_TYPE:
                name = imageHome + "/" + id + "_fanart.jpg";
                //rokuhdname = imageHome + "/" + id + "_fanart_roku_hd.jpg";
                //rokusdname = imageHome + "/" + id + "_fanart_roku_sd.jpg";
                break;

            case NMSConstants.POSTER_IMAGE_TYPE:
                name = imageHome + "/" + id + "_poster.jpg";
                break;

            }
            try {

                BufferedImage bi = ImageIO.read(new URL(url));
                if (bi != null) {

                    ImageIO.write(bi, "jpg", new File(name));

                    if (rokuhdname != null) {

                        BufferedImage hd = createImage(bi, 388);
                        hd = hd.getSubimage(49, 0, 290, 218);
                        ImageIO.write(hd, "jpg", new File(rokuhdname));

                        BufferedImage sd = createImage(bi, 256);
                        sd = sd.getSubimage(21, 0, 214, 144);
                        ImageIO.write(sd, "jpg", new File(rokusdname));
                    }

                } else {

                    log(WARNING, "can't load <" + url + ">");
                }

            } catch (IOException ex) {

                log(WARNING, "save image: " + ex.getMessage());
            }
        }
    }

    private BufferedImage createImage(BufferedImage bi, int scalex) {

        BufferedImage result = null;

        if (bi != null) {

            Image scaled = bi.getScaledInstance(388, -1, Image.SCALE_DEFAULT);
            if (scaled instanceof BufferedImage) {

                log(INFO, "It's a BufferedImage already...");
                result = (BufferedImage) scaled;

            } else {

                log(INFO, "Have to Make a BufferedImage!");
                result = new BufferedImage(scaled.getWidth(null),
                    scaled.getHeight(null), bi.getType());
                Graphics2D g2d = result.createGraphics();
                g2d.drawImage(scaled, 0, 0, null);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void schedule(RecordingRule rr) {

        Scheduler s = getScheduler();
        if ((s != null) && (rr != null)) {

            if (rr.getType() != RecordingRule.DO_NOT_RECORD_TYPE) {
                s.addRecordingRule(rr);
            } else {
                s.removeRecordingRule(rr);
            }

            s.requestRescheduling();
            sendMessage(NMSConstants.MESSAGE_RULE_UPDATE);
        }
    }

    /**
     * The EventSender instance allows implementations to send messages
     * via the EventAdmin.
     *
     * @return An EventSender instance.
     */
    public EventSender getEventSender() {
        return (eventSender);
    }

    /**
     * The EventSender instance allows implementations to send messages
     * via the EventAdmin.
     *
     * @param es An EventSender instance.
     */
    public void setEventSender(EventSender es) {
        eventSender = es;
    }

    /**
     * {@inheritDoc}
     */
    public void sendMessage(String s) {

        EventSender es = getEventSender();
        if ((s != null) && (es != null)) {

            es.sendMessage(s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dataUpdate(DataUpdateEvent event) {

        // Let's clear out our cache of ShowAirings per Channel since
        // we have fresh guide data.
        clearShowAiringCacheMap();

        Scheduler s = getScheduler();
        if (s != null) {

            s.rebuildCache();
            s.requestRescheduling();
        }
    }

    /**
     * {@inheritDoc}
     */
    public LiveTV openSession() {

        LiveTV result = null;

        Live lve = getLive();
        if (lve != null) {

            result = lve.openSession();
            if (result != null) {

                result.setHostPort(getHost() + ":" + getPort());
                result.setStreamURL(computeStreamURL(result));
                log(DEBUG, "STREAM URL SET TO: " + result.getStreamURL());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public LiveTV openSession(String channelNumber) {

        LiveTV result = null;

        Live lve = getLive();
        if (lve != null) {

            result = lve.openSession(channelNumber);
            if (result != null) {

                result.setHostPort(getHost() + ":" + getPort());
                result.setStreamURL(computeStreamURL(result));
                log(DEBUG, "STREAM URL SET TO: " + result.getStreamURL());
                System.out.println("STREAM URL SET TO: " + result.getStreamURL());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public LiveTV openSession(String host, int port) {

        LiveTV result = null;

        Live lve = getLive();
        if (lve != null) {

            log(DEBUG, "request Host " + host);
            log(DEBUG, "request Port " + port);
            result = lve.openSession(host, port);
            if (result != null) {

                result.setHostPort(getHost() + ":" + getPort());
                log(DEBUG, "DestinationHost " + result.getDestinationHost());
                log(DEBUG, "DestinationPort " + result.getDestinationPort());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public LiveTV openSession(String host, int port, String channelNumber) {

        LiveTV result = null;

        Live lve = getLive();
        if (lve != null) {

            log(DEBUG, "request Host " + host);
            log(DEBUG, "request Port " + port);
            result = lve.openSession(host, port, channelNumber);
            if (result != null) {

                result.setHostPort(getHost() + ":" + getPort());
                log(DEBUG, "DestinationHost " + result.getDestinationHost());
                log(DEBUG, "DestinationPort " + result.getDestinationPort());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public LiveTV changeChannel(LiveTV l, Channel c) {

        Live lve = getLive();
        if (lve != null) {

            lve.changeChannel(l, c);
            l.setStreamURL(computeStreamURL(l));
        }

        return (l);
    }

    /**
     * {@inheritDoc}
     */
    public void closeSession(LiveTV l) {

        Live lve = getLive();
        if (lve != null) {

            lve.closeSession(l);
        }
    }

    private OnDemand getOnDemandByName(String name) {

        OnDemand result = null;

        if (name != null) {

            OnDemand[] array = getOnDemands();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (name.equals(array[i].getTitle())) {

                        result = array[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOnDemandNames() {

        String[] result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public StreamSession openSession(String name, String host, int port) {

        StreamSession result = null;

        if ((name != null) && (host != null)) {

            OnDemand od = getOnDemandByName(name);
            if (od != null) {

                result = od.openSession(host, port);
            }

        } else {

            log(WARNING, "Trying to do OnDemand from wrong NMS");
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void command(StreamSession ss, int type) {

        if (ss != null) {

            String hp = ss.getHostPort();
            if (hp.equals(getHost() + ":" + getPort())) {

                OnDemand od = getOnDemandByName(ss.getName());
                if (od != null) {

                    od.command(ss, type);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void closeSession(StreamSession ss) {

        if (ss != null) {

            String hp = ss.getHostPort();
            if (hp.equals(getHost() + ":" + getPort())) {

                OnDemand od = getOnDemandByName(ss.getName());
                if (od != null) {

                    od.closeSession(ss);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Tag getRootTag() {

        Tag result = null;

        PhotoManager pm = getPhotoManager();
        if (pm != null) {

            result = pm.getRootTag();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Photo[] getPhotos() {

        Photo[] result = null;

        PhotoManager pm = getPhotoManager();
        if (pm != null) {

            result = pm.getPhotos();
            if ((result != null) && (result.length > 0)) {

                for (int i = 0; i < result.length; i++) {

                    String url = computeStreamURL(result[i]);
                    if (url != null) {
                        result[i].setPath(url);
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void photoScan() {

        PhotoManager pm = getPhotoManager();
        if (pm != null) {

            pm.photoScan();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Video v) {

        VideoManager vm = getVideoManager();
        if ((vm != null) && (v != null)) {

            vm.save(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Video getVideoById(String id) {

        Video result = null;

        VideoManager vm = getVideoManager();
        if ((vm != null) && (id != null)) {

            result = vm.getVideoById(id);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Video[] getVideos() {

        Video[] result = null;

        VideoManager vm = getVideoManager();
        if (vm != null) {

            result = vm.getVideos();

            // Let's eliminate the hidden.
            if ((result != null) && (result.length > 0)) {

                boolean found = false;
                ArrayList<Video> list = new ArrayList<Video>();
                for (int i = 0; i < result.length; i++) {

                    if (!result[i].isHidden()) {

                        list.add(result[i]);

                    } else {

                        found = true;
                    }
                }

                if (found) {

                    result = list.toArray(new Video[list.size()]);
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVideo(Video v) {

        VideoManager vm = getVideoManager();
        if ((vm != null) && (v != null)) {

            vm.removeVideo(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void videoScan() {

        VideoManager vm = getVideoManager();
        if (vm != null) {

            vm.videoScan();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void generateArtwork(Video v, int seconds) {

        VideoManager vm = getVideoManager();
        if (vm != null) {

            vm.generateArtwork(v, seconds);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean performChannelScan(String recorderSource, String type) {

        boolean result = false;

        log(DEBUG, "performChannelScan using <" + recorderSource + ">");
        if (recorderSource != null) {

            int index = recorderSource.indexOf(" ");
            if (index != -1) {

                String device = recorderSource.substring(index);
                device = device.trim();
                log(DEBUG, "parse device <" + device + ">");
                Recorder[] array = getRecorders();
                if ((array != null) && (array.length > 0)) {

                    log(DEBUG, "recorder count " + array.length);
                    Recorder r = null;
                    for (int i = 0; i < array.length; i++) {

                        log(DEBUG, "rec dev: <" + array[i].getDevice() + ">");
                        if (device.equals(array[i].getDevice())) {

                            r = array[i];
                            break;
                        }
                    }

                    Scheduler s = getScheduler();
                    if ((s != null) && (r != null) && (r.supportsScan())) {

                        String ln = s.getListingNameByRecorder(r);
                        if (ln != null) {

                            Channel[] chans = getChannelsByListingName(ln);
                            chans = r.getCustomChannels(chans);
                            if (chans != null) {

                                result = true;
                                r.performScan(chans, type);
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public State getState() {
        return (new State(this));
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsLiveTV() {
        return (getLive() != null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsOnDemand() {
        return (getOnDemands() != null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsOnDemand(String name) {

        boolean result = false;

        if ((name != null) && (supportsOnDemand())) {

            OnDemand[] array = getOnDemands();
            for (int i = 0; i < array.length; i++) {

                if (name.equals(array[i].getTitle())) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Override and use the title property.
     *
     * @return The title property.
     */
    public String toString() {
        return (getTitle());
    }

    static class RecorderSortByTitleDevice implements Comparator<Recorder>,
        Serializable {

        public int compare(Recorder r0, Recorder r1) {

            String r0str = r0.getTitle() + " " + r0.getDevice();
            String r1str = r1.getTitle() + " " + r1.getDevice();
            return (r0str.compareTo(r1str));
        }
    }

    static class FileSort implements Comparator<File>, Serializable {

        public int compare(File f0, File f1) {

            Long l0 = Long.valueOf(f0.lastModified());
            Long l1 = Long.valueOf(f1.lastModified());

            return (l1.compareTo(l0));
        }
    }

}

