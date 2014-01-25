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
package org.jflicks.restlet;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.jflicks.configure.Configuration;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.State;
import org.jflicks.nms.Video;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.util.Util;

import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class is a base implementation of a restlet ServerResource.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseServerResource extends WadlServerResource {

    public static final String JSON = "json";
    public static final String XML = "xml";

    private String version;
    private String lang;
    private String format;
    private Gson gson;
    private XStream xstream;
    private String recordingId;
    private boolean allowRerecord;
    private String ruleId;
    private String channelId;
    private String showId;
    private String term;
    private String title;
    private boolean unique;

    /**
     * Simple empty constructor.
     */
    public BaseServerResource() {

        setVersion("1.0");
        setLang("en");
        setFormat("xml");
        setGson(new Gson());
        setXStream(new XStream(new DomDriver()));
    }

    public String getVersion() {
        return (version);
    }

    public void setVersion(String s) {
        version = s;
    }

    public String getLang() {
        return (lang);
    }

    public void setLang(String s) {
        lang = s;
    }

    public String getFormat() {
        return (format);
    }

    public void setFormat(String s) {
        format = s;
    }

    public String getRecordingId() {
        return (recordingId);
    }

    public void setRecordingId(String s) {
        recordingId = s;
    }

    public boolean isAllowRerecord() {
        return (allowRerecord);
    }

    public void setAllowRerecord(boolean b) {
        allowRerecord = b;
    }

    public String getRuleId() {
        return (ruleId);
    }

    public void setRuleId(String s) {
        ruleId = s;
    }

    public String getChannelId() {
        return (channelId);
    }

    public void setChannelId(String s) {
        channelId = s;
    }

    public String getShowId() {
        return (showId);
    }

    public void setShowId(String s) {
        showId = s;
    }

    public String getTerm() {
        return (term);
    }

    public void setTerm(String s) {
        term = s;
    }

    public String getTitle() {
        return (title);
    }

    public void setTitle(String s) {
        title = s;
    }

    public boolean isUnique() {
        return (unique);
    }

    public void setUnique(boolean b) {
        unique = b;
    }

    public Gson getGson() {
        return (gson);
    }

    private void setGson(Gson g) {
        gson = g;
    }

    public XStream getXStream() {
        return (xstream);
    }

    private void setXStream(XStream x) {
        xstream = x;
    }

    public boolean isFormatJson() {
        return (JSON.equalsIgnoreCase(getFormat()));
    }

    public boolean isFormatXml() {
        return (XML.equalsIgnoreCase(getFormat()));
    }

    @Override
    protected void doInit() throws ResourceException {

        Map<String, Object> map = getRequestAttributes();
        if (map != null) {

            setVersion(decode((String) map.get("version")));
            setFormat(decode((String) map.get("format")));

            setRecordingId(decode((String) map.get("recordingId")));
            setAllowRerecord(
                Util.str2boolean(decode((String) map.get("allowRerecord")),
                    false));
            setRuleId(decode((String) map.get("ruleId")));
            setChannelId(decode((String) map.get("channelId")));
            setShowId(decode((String) map.get("showId")));
            setTerm(decode((String) map.get("term")));
            setTitle(decode((String) map.get("title")));
            setUnique(Util.str2boolean(decode((String) map.get("unique")), false));
        }
    }

    /**
     * Convenience method to decode URLs and other Strings that have been
     * encoded for web traffic.
     *
     * @param s A given String to decode.
     * @return The decoded String.
     */
    public String decode(String s) {

        String result = null;

        if (s != null) {

            try {

                result = URLDecoder.decode(s, "UTF-8");

            } catch (Exception ex) {
                result = s;
            }
        }

        return (result);
    }

    /**
     * Convenience method to encode URLs and other Strings for web traffic.
     *
     * @param s A given String to encode.
     * @return The encoded String.
     */
    public String encode(String s) {

        String result = null;

        if (s != null) {

            try {

                result = URLEncoder.encode(s, "UTF-8");

            } catch (Exception ex) {
                result = s;
            }
        }

        return (result);
    }

    public boolean contains(String s, String sub) {

        boolean result = false;

        if ((s != null) && (sub != null)) {

            result = s.indexOf(sub) != -1;
        }

        return (result);
    }

    public void log(int level, String message) {

        NMSSupport nsup = NMSSupport.getInstance();
        nsup.log(level, message);
    }

}

