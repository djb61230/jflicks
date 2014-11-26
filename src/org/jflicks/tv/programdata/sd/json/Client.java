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
package org.jflicks.tv.programdata.sd.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jflicks.util.Util;

/**
 * A class to communicate with the new schedules direct JSON web service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Client {

    private String baseUri;
    private String apiVersion;
    private String token;
    private Headend[] headends;
    private LineupResponse lineupResponse;

    /**
     * Simple empty constructor.
     */
    public Client() {

        setBaseUri("https://json.schedulesdirect.org");
        setApiVersion("20140530");
    }

    public String getBaseUri() {
        return (baseUri);
    }

    public void setBaseUri(String s) {
        baseUri = s;
    }

    public String getApiVersion() {
        return (apiVersion);
    }

    public void setApiVersion(String s) {
        apiVersion = s;
    }

    public String getToken() {
        return (token);
    }

    private void setToken(String s) {
        token = s;
    }

    public LineupResponse getLineupResponse() {
        return (lineupResponse);
    }

    private void setLineupResponse(LineupResponse lr) {
        lineupResponse = lr;
    }

    public Headend[] getHeadends() {
        return (headends);
    }

    public void setHeadends(Headend[] array) {
        headends = array;
    }

    private void dumpHeader(ClientResource cr) {

        if (cr != null) {

            ClientInfo ci = cr.getClientInfo();
            if (ci != null) {

                Map<String, String> map = ci.getAgentAttributes();
                if (map != null) {

                    Set<Map.Entry<String, String>> set = map.entrySet();
                    Iterator<Map.Entry<String, String>> iter = set.iterator();
                    while (iter.hasNext()) {

                        Map.Entry<String, String> me = iter.next();
                        String key = me.getKey();
                        String val = me.getValue();
                        System.out.println("dumpHeader: <" + key + "> <" + val + ">");
                    }
                }

            } else {

                System.out.println("dumpHeader: no ClientInfo!");
            }

        } else {

            System.out.println("dumpHeader: no ClientResource!");
        }
    }

    private void putTokenInHeader(ClientResource cr) {

        if (cr != null) {

            ConcurrentMap<String, Object> attrs = cr.getRequest().getAttributes();
            Series<Header> headers = (Series<Header>) cr.getRequestAttributes().get(
                "org.restlet.http.headers");

            if (headers == null) {

                headers = new Series<Header>(Header.class);
                Series<Header> prev = (Series<Header>) 
                attrs.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, headers);

                if (prev != null) {
                    headers = prev;
                }
            }
            headers.set("token", getToken());
        }
    }

    private void putAcceptInHeader(ClientResource cr) {

        if (cr != null) {

            ConcurrentMap<String, Object> attrs = cr.getRequest().getAttributes();
            Series<Header> headers = (Series<Header>) cr.getRequestAttributes().get(
                "org.restlet.http.headers");

            if (headers == null) {

                headers = new Series<Header>(Header.class);
                Series<Header> prev = (Series<Header>) 
                attrs.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, headers);

                if (prev != null) {
                    headers = prev;
                }
            }
            headers.set("Accept-Encoding", "deflate"); 
        }
    }

    private void putUserAgentInHeader(ClientResource cr) {

        if (cr != null) {

            cr.getClientInfo().setAgent("jflicks/1.0");
        }
    }

    private String getUriFromLineupName(String name) {

        String result = null;

        if (name != null) {

            UserLineup ul = getUserLineup();
            if (ul != null) {

                Lineup[] lups = ul.getLineups();
                if ((lups != null) && (lups.length > 0)) {

                    for (int j = 0; j < lups.length; j++) {

                        if (name.equals(lups[j].getName())) {

                            result = lups[j].getUri();
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private String getUriFromLineupNameAndLocation(String name, String location) {

        String result = null;

        if ((name != null) && (location != null)) {

            Headend[] array = getHeadends();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    Lineup[] lups = array[i].getLineups();
                    if ((lups != null) && (lups.length > 0)) {

                        for (int j = 0; j < lups.length; j++) {

                            if ((name.equals(lups[j].toString())) && (location.equals(array[i].getLocation()))) {

                                result = lups[j].getUri();
                                break;
                            }
                        }
                    }

                    if (result != null) {
                        break;
                    }
                }
            }
        }

        return (result);
    }

    public boolean doToken(String user, String sha1password) {

        boolean result = false;

        if ((user != null) && (sha1password != null)) {

            String json = "{\"username\":\"" + user + "\", \"password\":\"" + sha1password + "\"}";
            String uri = getBaseUri() + "/" + getApiVersion() + "/token";
            ClientResource cr = new ClientResource(uri);
            putUserAgentInHeader(cr);
            dumpHeader(cr);
            json = RestUtil.post(cr, json);

            Gson gson = new Gson();
            TokenResponse tr = gson.fromJson(json, TokenResponse.class);
            if (tr != null) {

                setToken(tr.getToken());
                result = getToken() != null;
            }
        }

        return (result);
    }

    public boolean doStatus() {

        boolean result = false;

        String uri = getBaseUri() + "/" + getApiVersion() + "/status";
        ClientResource cr = new ClientResource(uri);
        putTokenInHeader(cr);
        putUserAgentInHeader(cr);
        dumpHeader(cr);

        String json = RestUtil.get(cr);

        Gson gson = new Gson();
        Status status = gson.fromJson(json, Status.class);
        if (status != null) {

            SystemStatus[] all = status.getSystemStatus();
            if ((all != null) && (all.length > 0)) {

                // Assume one for now...
                String str = all[0].getStatus();
                if ((str != null) && (str.equalsIgnoreCase("online"))) {

                    result = true;
                }
            }
        }

        return (result);
    }

    public boolean doHeadend(String country, String zip) {

        boolean result = false;

        if ((country != null) && (zip != null)) {

            String uri = getBaseUri() + "/" + getApiVersion() + "/headends?country=" + country
                + "&postalcode=" + zip;
            ClientResource cr = new ClientResource(uri);
            putTokenInHeader(cr);
            putUserAgentInHeader(cr);
            dumpHeader(cr);

            String json = RestUtil.get(cr);
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<String, Headend>>() {}.getType();
            HashMap hm = gson.fromJson(json, mapType);
            if ((hm != null) && (hm.size() > 0)) {

                int index = 0;
                Headend[] array = new Headend[hm.size()];
                Set<Map.Entry<String, Headend>> set = hm.entrySet();
                Iterator<Map.Entry<String, Headend>> iter = set.iterator();
                while (iter.hasNext()) {

                    Map.Entry<String, Headend> me = iter.next();
                    array[index++] = me.getValue();
                }

                setHeadends(array);
                result = getHeadends() != null;
            }

        }

        return (result);
    }

    public boolean doAddLineup(String name, String location) {

        boolean result = false;

        if ((name != null) && (location != null)) {

            String uri = getUriFromLineupNameAndLocation(name, location);
            if (uri != null) {

                try {

                    setLineupResponse(null);
                    uri = getBaseUri() + uri;
                    ClientResource cr = new ClientResource(uri);
                    putTokenInHeader(cr);
                    putUserAgentInHeader(cr);
                    dumpHeader(cr);

                    String json = RestUtil.put(cr, null);

                    Gson gson = new Gson();
                    LineupResponse lr = gson.fromJson(json, LineupResponse.class);
                    if (lr != null) {

                        setLineupResponse(lr);
                        result = lr.getResponse().equalsIgnoreCase("ok");
                    }

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return (result);
    }

    public boolean doDeleteLineup(String name, String location) {

        boolean result = false;

        if ((name != null) && (location != null)) {

            String uri = getUriFromLineupNameAndLocation(name, location);
            if (uri != null) {

                try {

                    setLineupResponse(null);
                    uri = getBaseUri() + uri;
                    ClientResource cr = new ClientResource(uri);
                    putTokenInHeader(cr);
                    putUserAgentInHeader(cr);
                    dumpHeader(cr);

                    String json = RestUtil.delete(cr);

                    Gson gson = new Gson();
                    LineupResponse lr = gson.fromJson(json, LineupResponse.class);
                    if (lr != null) {

                        setLineupResponse(lr);
                        result = lr.getResponse().equalsIgnoreCase("ok");
                    }

                } catch (Exception ex) {
                }
            }
        }

        return (result);
    }

    public UserLineup getUserLineup() {

        UserLineup result = null;
        
        String tok = getToken();
        if (tok != null) {

            try {

                String uri = getBaseUri() + "/" + getApiVersion() + "/lineups";
                ClientResource cr = new ClientResource(uri);
                putTokenInHeader(cr);
                putUserAgentInHeader(cr);
                dumpHeader(cr);

                String json = RestUtil.get(cr);
                Gson gson = new Gson();
                result = gson.fromJson(json, UserLineup.class);

            } catch (Exception ex) {
            }
        }

        return (result);
    }

    public Mapping getMapping(String name) {

        Mapping result = null;

        if (name != null) {

            String uri = getUriFromLineupName(name);
            if (uri != null) {

                uri = getBaseUri() + uri;
                ClientResource cr = new ClientResource(uri);
                putTokenInHeader(cr);
                putUserAgentInHeader(cr);
                dumpHeader(cr);

                String json = RestUtil.get(cr);

                Gson gson = new Gson();
                result = gson.fromJson(json, Mapping.class);
            }
        }

        return (result);
    }

    public StationSchedule[] getGuide(GuideRequest[] array) {

        StationSchedule[] result = null;

        if (array != null) {

            String tok = getToken();
            if (tok != null) {

                try {

                    String uri = getBaseUri() + "/" + getApiVersion() + "/schedules";
                    ClientResource cr = new ClientResource(uri);
                    putTokenInHeader(cr);
                    putUserAgentInHeader(cr);
                    dumpHeader(cr);

                    Gson gson = new Gson();
                    String rjson = gson.toJson(array);
                    String json = RestUtil.post(cr, rjson);

                    String[] items = json.split("\n");
                    if ((items != null) && (items.length > 0)) {

                        result = new StationSchedule[items.length];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = gson.fromJson(items[i], StationSchedule.class);
                        }
                    }

                } catch (Exception ex) {
                }
            }
        }

        return (result);
    }

    public Program[] getPrograms(String[] array) {

        Program[] result = null;

        if (array != null) {

            String tok = getToken();
            if (tok != null) {

                try {

                    String uri = getBaseUri() + "/" + getApiVersion() + "/programs";
                    ClientResource cr = new ClientResource(uri);
                    putTokenInHeader(cr);
                    putUserAgentInHeader(cr);
                    putAcceptInHeader(cr);
                    dumpHeader(cr);

                    Gson gson = new Gson();
                    String rjson = gson.toJson(array);
                    String json = RestUtil.post(cr, rjson);

                    if (json != null) {

                        String[] items = json.split("\n");
                        if ((items != null) && (items.length > 0)) {

                            result = new Program[items.length];
                            for (int i = 0; i < result.length; i++) {
                                result[i] = gson.fromJson(items[i], Program.class);
                            }
                        }
                    }

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        }

        return (result);
    }

}

