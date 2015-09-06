package org.jflicks.restlet.servercomponent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jflicks.util.LogUtil;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.util.ServerList;

/**
 * A base ServerComponent implementation available to be extended.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public abstract class BaseServerComponent implements ServerComponent {

    private String id;
    private Component component;

    /**
     * Simple empty constructor.
     */
    public BaseServerComponent() {
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return (id);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s A given property value.
     */
    public void setId(String s) {
        id = s;
    }
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return (component);
    }

    /**
     * Convenience method to set this property.
     *
     * @param c A given property value.
     */
    public void setComponent(Component c) {
        component = c;
    }

    /**
     * {@inheritDoc}
     */
    public String getBaseURI() {

        String result = null;

        Component c = getComponent();
        if (c != null) {

            ServerList sl = c.getServers();
            if ((sl != null) && (sl.size() > 0)) {

                Server s = sl.get(0);
                String scheme = "http";
                String host = getIPAddressAsString();
                int port = s.getPort();
                List<Protocol> l = s.getProtocols();
                if ((l != null) && (l.size() > 0)) {

                    scheme = l.get(0).getSchemeName();
                }

                result = scheme + "://" + host + ":" + port;
            }
        }

        return (result);
    }

    private String getLocalHostname() {

        String result = "localhost";

        try {

            InetAddress addr = InetAddress.getLocalHost();
            result = addr.getHostName();

        } catch (UnknownHostException ex) {

            LogUtil.log(LogUtil.WARNING, ex.getMessage());
        }

        return (result);
    }

    private String getIPAddressAsString() {

        String result = getLocalHostname();

        if (result != null) {

            try {

                InetAddress addr = InetAddress.getByName(result);
                byte[] ipAddr = addr.getAddress();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ipAddr.length; i++) {
                    if (i > 0) {

                        sb.append(".");
                    }

                    sb.append(ipAddr[i] & 0xFF);
                }

                result = sb.toString();

            } catch (UnknownHostException e) {
            }
        }

        return (result);
    }

}
