package org.jflicks.util.feature;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

/**
 * Be able to generate a properly formatted feature.xml file for the
 * deployed OSGi bundles.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class FeatureGenerate {

    private FeatureGenerate() {
    }

    /**
     * Simple main to generate a feature xml from the command line.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

        File out = null;
        File deploy = null;
        String repositoryName = "JflicksRepository";
        String featureName = "jflicks";

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-o")) {

                out = new File(args[i + 1]);

            } else if (args[i].equalsIgnoreCase("-dir")) {

                deploy = new File(args[i + 1]);

            } else if (args[i].equalsIgnoreCase("-repository")) {

                repositoryName = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-feature")) {

                featureName = args[i + 1];
            }
        }

        if ((out != null) && (deploy != null)) {


            String[] exts = {
                "xml",
                "jar"
            };

            Collection<File> bundles = FileUtils.listFiles(deploy, exts, true);
            if ((bundles != null) && (bundles.size() > 0)) {

                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                sb.append("\n");
                sb.append("<features name=\"" + repositoryName + "\">");
                sb.append("\n");
                sb.append("    <feature name=\"" + featureName + "\">");
                sb.append("\n");
                Iterator<File> iter = bundles.iterator();
                while (iter.hasNext()) {

                     File f = iter.next();
                     URI uri = f.toURI();
                     if (uri != null) {

                         if (f.getName().endsWith(".xml")) {

                             sb.append("        <bundle>blueprint:");
                             sb.append(uri.toString());
                             sb.append("</bundle>");
                             sb.append("\n");

                         } else {

                             sb.append("        <bundle>");
                             sb.append(uri.toString());
                             sb.append("</bundle>");
                             sb.append("\n");
                         }
                     }
                }
                sb.append("    </feature>");
                sb.append("\n");
                sb.append("</features>");
                sb.append("\n");

                try {

                    FileUtils.writeStringToFile(out, sb.toString());

                } catch (IOException ex) {

                    System.out.println(ex.getMessage());
                }

            } else {

                System.out.println("No bundles found!");
            }
        }

    }

}
