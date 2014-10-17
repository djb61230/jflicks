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

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * Some handly methods to do REST client stuff using restlet.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class RestUtil {

    /**
     * Default empty constructor.
     */
    private RestUtil() {
    }

    public static String get(ClientResource cr) {

        String result = null;

        if (cr != null) {

            try {

                Representation rep = cr.get();
                if (rep != null) {

                    result = rep.getText();
                }

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    public static String get(String uri) {

        String result = null;

        if (uri != null) {

            ClientResource cr = new ClientResource(uri);
            result = get(cr);
            cr.release();
        }

        return (result);
    }

    public static String put(ClientResource cr, Object object) {

        String result = null;

        if (cr != null) {

            try {

                Representation rep = cr.put(object);
                if (rep != null) {

                    result = rep.getText();
                }

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    public static String put(String uri, Object object) {

        String result = null;

        if (uri != null) {

            ClientResource cr = new ClientResource(uri);
            result = put(cr, object);
            cr.release();
        }

        return (result);
    }

    public static String delete(ClientResource cr) {

        String result = null;

        if (cr != null) {

            try {

                Representation rep = cr.delete();
                if (rep != null) {

                    result = rep.getText();
                }

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    public static String delete(String uri) {

        String result = null;

        if (uri != null) {

            ClientResource cr = new ClientResource(uri);
            result = delete(cr);
            cr.release();
        }

        return (result);
    }

    public static String post(ClientResource cr, Object object) {

        String result = null;

        if (cr != null) {

            try {

                Representation rep = cr.post(object);
                if (rep != null) {

                    result = rep.getText();
                }

            } catch (IOException ex) {

                result = null;
            }
        }

        return (result);
    }

    public static String post(String uri, Object object) {

        String result = null;

        if (uri != null) {

            ClientResource cr = new ClientResource(uri);
            result = post(cr, object);
            cr.release();
        }

        return (result);
    }

}

