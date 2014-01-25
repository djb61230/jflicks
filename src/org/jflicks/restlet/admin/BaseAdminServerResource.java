package org.jflicks.restlet.admin;

import java.util.ArrayList;
import java.util.Map;

import org.jflicks.nms.NMS;
import org.jflicks.restlet.BaseServerResource;

import freemarker.template.Configuration;

/**
 * This base class has a been of methods that interact with the Admin
 * class, which in turn interacts with OSGi and our IOTA modules.  This
 * insulates extensions from worrying all about those things so they can
 * just concentrate on doing the right RESTlet stuff.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public abstract class BaseAdminServerResource extends BaseServerResource {

    private static Admin admin;

    /**
     * Simple empty constructor.
     */
    public BaseAdminServerResource() {
    }

    /**
     * We use the Admin instance to do the real work.
     *
     * @return A Admin instance.
     */
    public static Admin getAdmin() {
        return (admin);
    }

    /**
     * We use the Admin instance to do the real work.
     *
     * @param a An Admin instance.
     */
    public static void setAdmin(Admin a) {
        admin = a;
    }

    public Configuration getConfiguration() {

        Configuration result = null;

        Admin a = getAdmin();
        if (a != null) {

            result = a.getConfiguration();
        }

        return (result);
    }

    public String getBaseURI() {

        String result = null;

        if (admin != null) {

            result = admin.getBaseURI();
        }

        return (result);
    }

    public String[] getMenuURLs() {

        String[] result = null;

        String buri = getBaseURI();
        if (buri != null) {

            result = new String[2];
            result[0] = buri + "/1.0/home.html";
            result[1] = buri + "/1.0/recordings.html";
        }

        return (result);
    }

    public String[] getMenus() {

        String[] result = null;

        result = new String[2];
        result[0] = "Home";
        result[1] = "Recordings";

        return (result);
    }

    public String[] getRecordingTitleUrls(String[] array) {

        String[] result = null;

        String buri = getBaseURI();
        if ((buri != null) && (array != null) && (array.length > 0)) {

            result = new String[array.length];

            for (int i = 0; i < result.length; i++) {

                result[i] = buri + "/1.0/recordings.html?title="
                    + encode(array[i]);
            }
        }

        return (result);
    }

}
