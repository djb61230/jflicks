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
package org.jflicks.metadata.themoviedb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

/**
 * A job that downloads and saves an image.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DownloadSaveJob extends AbstractJob {

    private String urlString;
    private String extension;
    private File file;

    /**
     * Constructor with our three required arguments.
     *
     * @param url A URL as a string that represents an image.
     * @param ext The file type based upon extension to be saved.
     * @param file The given file to write the downloaded image data.
     */
    public DownloadSaveJob(String url, String ext, File file) {

        setUrlString(url);
        setExtension(ext);
        setFile(file);
    }

    private String getUrlString() {
        return (urlString);
    }

    private void setUrlString(String s) {
        urlString = s;
    }

    private String getExtension() {
        return (extension);
    }

    private void setExtension(String s) {
        extension = s;
    }

    private File getFile() {
        return (file);
    }

    private void setFile(File f) {
        file = f;
    }

    /**
     * @inheritDoc
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * @inheritDoc
     */
    public void run() {

        String error = null;

        try {

            URL imageURL = new URL(getUrlString());
            BufferedImage bi = ImageIO.read(imageURL);
            ImageIO.write(bi, getExtension(), getFile());

        } catch (MalformedURLException ex) {

            error = ex.getMessage();

        } catch (IOException ex) {

            error = ex.getMessage();
        }

        if (error == null) {
            fireJobEvent(JobEvent.COMPLETE);
        } else {
            fireJobEvent(JobEvent.COMPLETE, error);
        }
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}
