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
package org.jflicks.tv.programdata.sd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.xtvdclient.gui.XTVDClient;
import net.sf.xtvdclient.xtvd.DataDirectException;
import net.sf.xtvdclient.xtvd.datatypes.Xtvd;
import net.sf.xtvdclient.xtvd.parser.Parser;
import net.sf.xtvdclient.xtvd.parser.ParserFactory;

/**
 * This class gets data from Schedules Direct.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirect extends XTVDClient {

    private File workingFile;

    static {
        System.setProperty("propertiesDirectory", "conf");
    }

    /**
     * Simple empty constructor.
     *
     * @throws DataDirectException on any error.
     */
    public SchedulesDirect() throws DataDirectException {
    }

    /**
     * The XML data is first downloaded and then parsed for information.
     *
     * @return A File instance.
     */
    public File getWorkingFile() {
        return (workingFile);
    }

    private void setWorkingFile(File f) {
        workingFile = f;
    }

    /**
     * Here is an opportunity to override the settings in conf/XTVD.xml.  We
     * just set the outputfileName to a temporary file.
     *
     * @throws Exception on any error.
     */
    public void buildInterface() throws Exception {

        File f = File.createTempFile("schedDirect", ".xml");
        //f.deleteOnExit();
        setWorkingFile(f);
        outputFileName = f.getPath();
    }

    /**
     * The major work method here will trigger a call to the web service
     * and parse the resulting data into the Xtvd object model.
     *
     * @return A Xtvd instance.
     */
    public Xtvd getXtvd() {

        Xtvd result = null;

        try {

            buildInterface();
            getData();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                    new FileInputStream(getWorkingFile()), "UTF-8"));

            result = new Xtvd();
            Parser parser = ParserFactory.getXtvdParser(reader, result);
            parser.parseXTVD();
            reader.close();

        } catch (DataDirectException ex) {
        } catch (FileNotFoundException ex) {
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        } catch (Exception ex) {
        }

        return (result);
    }

    /**
     * Simple test main method.
     *
     * @param args Ignored arguments.
     * @throws Exception on any error.
     */
    public static void main(String[] args) throws Exception {

        SchedulesDirect sd = new SchedulesDirect();
        sd.getXtvd();
    }

}

