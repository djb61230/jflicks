package org.jflicks.util;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

/**
 * Some handy OSGi util methods, especially to bridge
 * that space between non-OSGi and OSGi.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class BundleUtil {

    private BundleUtil() {
    }

    /**
     * Find the Bundle associated with a given Class.  This uses the OSGi
     * FrameworkUtil class which must be fairly recent.
     *
     * @param c A Class instance.
     * @return A Bundle if in fact one exists.
     */
    public static Bundle getBundle(Class c) {
        return (FrameworkUtil.getBundle(c));
    }

    /**
     * Find the BundleContext associated with a given Class.
     *
     * @param c A Class instance.
     * @return A BundleContext if in fact one exists.
     */
    public static BundleContext getBundleContext(Class c) {

        BundleContext result = null;

        Bundle b = getBundle(c);
        if (b != null) {

            result = b.getBundleContext();
        }

        return (result);
    }

    /**
     * Find all ServiceReference instances given the class of the service.
     *
     * @param c The class instance.
     * @return An array of ServiceReference instances matching the given
     * name.
     */
    public static ServiceReference[] getServiceReferences(Class c) {

        ServiceReference[] result = null;

        BundleContext bc = getBundleContext(c);
        if (bc != null) {

            try {

                result = bc.getServiceReferences(c.getName(), null);

            } catch (InvalidSyntaxException ex) {
            }
        }

        return (result);
    }

    /**
     * Find all ServiceReference instances given a BundleContext and the
     * name of the service.
     *
     * @param bc A BundleContext instance.
     * @param name The name of a service.
     * @return An array of ServiceReference instances matching the given
     * name.
     */
    public static ServiceReference[] getServiceReferences(BundleContext bc,
        String name) {

        ServiceReference[] result = null;

        if (bc != null) {

            try {

                result = bc.getServiceReferences(name, null);

            } catch (InvalidSyntaxException ex) {
            }
        }

        return (result);
    }

    /**
     * Given a Class instance, find all the deployed services that
     * advertise themselves as that Class.
     *
     * @param c A Class instance.
     * @return An array of Objects.
     */
    public static Object[] getServices(Class c) {

        Object[] result = null;

        BundleContext bc = getBundleContext(c);
        if ((bc != null) && (c != null)) {

            ServiceReference[] array = getServiceReferences(bc, c.getName());
            if ((array != null) && (array.length > 0)) {

                result = new Object[array.length];
                for (int i = 0; i < result.length; i++) {

                    result[i] = bc.getService(array[i]);
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to get an EventAdmin service.
     *
     * @return An EventAdmin if one is currently deployed.
     */
    public static EventAdmin getEventAdmin() {

        EventAdmin result = null;

        Object[] array = getServices(EventAdmin.class);
        if ((array != null) && (array.length > 0)) {

            result = (EventAdmin) array[0];
        }

        return (result);
    }

    /**
     * Convenience method to get an LogService service.
     *
     * @return An LogService if one is currently deployed.
     */
    public static LogService getLogService() {

        LogService result = null;

        Object[] array = getServices(LogService.class);
        if ((array != null) && (array.length > 0)) {

            result = (LogService) array[0];
        }

        return (result);
    }

}
