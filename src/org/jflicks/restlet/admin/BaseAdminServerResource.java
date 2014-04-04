package org.jflicks.restlet.admin;

import java.util.ArrayList;
import java.util.Map;

import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;

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
            result[0] = buri + "/home";
            result[1] = buri + "/recordings";
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

                result[i] = buri + "/recordings?title="
                    + encode(array[i]);
            }
        }

        return (result);
    }

    public String[] getNMSDescriptions() {

        String[] result = null;

        NMSSupport nsup = NMSSupport.getInstance();
        NMS[] array = nsup.getNMS();
        if ((array != null) && (array.length > 0)) {

            result = new String[array.length];
            for (int i = 0; i < result.length; i++) {

                result[i] = array[i].getHost() + ":" + array[i].getPort()
                    + " - " + array[i].getGroupName();
            }
        }

        return (result);
    }

    /**
     * We are baking in here just the NameValue instances we want
     * the user to be able to edit in a web admin.  This is much
     * less than what is really configurable.  Some settings just
     * should not be changed from the default.  Makes it easier to
     * deal with too.
     *
     * @param hostPort A host port property so we can get the NMS instance.
     * @return An array of NameValue instances.
     */
    public NameValue[] getNameValues(String hostPort) {

        NameValue[] result = null;

        return (result);
    }

}
