package org.jflicks.restlet.admin;

import java.util.HashMap;

import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.Recording;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;

/**
 * This resource handles User requests.  The request returns info
 * about the current user.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class RecordingsResource extends BaseAdminServerResource {

    /**
     * Simple default constructor.
     */
    public RecordingsResource() {

        setName("recordings");
        setDescription("recordings");
    }

    /**
     * Get the currently defined Sources represented in json or xml.
     *
     * @return A Representation instance.
     */
    @Get
    public Representation get() {

        Representation result = null;

        NMSSupport nsup = NMSSupport.getInstance();

        Configuration c = getConfiguration();  
        if (c != null) {

            HashMap<String, Object> root = new HashMap<String, Object>();

            root.put("homeClass", "");
            root.put("configClass", "");
            root.put("vmClass", "");
            root.put("recordingsClass", "class=\"selected\"");
            root.put("upcomingClass", "");
            root.put("aboutClass", "");

            root.put("menus", getMenus());
            root.put("menu_urls", getMenuURLs());
            Recording[] recs = null;
            String title = getQuery().getValues("title");
            if (title != null) {
                recs = nsup.getRecordingsByTitle(title);
            } else {
                recs = nsup.getRecordings();
            }
            if (recs != null) {

                root.put("recordings", recs);

                // We want to compute the screenshot url here and add
                // it to the data model because computing it will be
                // a lot easier than doing it in template code.
                String[] shots = new String[recs.length];
                for (int i = 0; i < recs.length; i++) {

                    String surl = recs[i].getStreamURL();
                    if (surl != null) {

                        String iext = recs[i].getIndexedExtension();
                        if ((iext != null) && (surl.endsWith(iext))) {

                            int ilength = iext.length() + 1;
                            surl = surl.substring(0, surl.length() - ilength);
                        }

                        log(Admin.DEBUG, "streamurl  <" + surl + ">");
                        surl = surl + ".png";
                        shots[i] = surl;
                    }
                }

                root.put("screenshots", shots);

                // Now handle categories.
                String[] titles = nsup.getRecordingTitles();
                String[] urls = getRecordingTitleUrls(titles);
                root.put("category_title", "Recordings By Title");
                root.put("category_urls", urls);
                root.put("categories", titles);
            }
            Template temp =
                TemplateRepresentation.getTemplate(c, "recordings.ftl");
            TemplateRepresentation rep = new TemplateRepresentation(temp,
                root, MediaType.TEXT_HTML); 

            result = rep;
        }

        return (result);
    }

}
