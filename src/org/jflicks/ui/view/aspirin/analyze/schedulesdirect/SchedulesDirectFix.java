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
package org.jflicks.ui.view.aspirin.analyze.schedulesdirect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jflicks.job.JobEvent;
import org.jflicks.ui.view.aspirin.analyze.BaseFix;
import org.jflicks.util.PromptPanel;
import org.jflicks.util.Util;

/**
 * Get the user/password from the user and generate an XTVD.xml
 * file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirectFix extends BaseFix {

    private static final String WEBSERVICE = "http://webservices.schedules"
        + "direct.tmsdatadirect.com/schedulesdirect/tvlistings/xtvdService";

    private File file;
    private PromptPanel promptPanel;
    private JTextField userTextField;
    private JPasswordField firstPasswordField;
    private JPasswordField secondPasswordField;

    /**
     * Simple one argument constructor.
     *
     * @param file The file path to the proper XTVD.xml.
     */
    public SchedulesDirectFix(File file) {

        setFile(file);

        JTextField tf = new JTextField(20);
        setUserTextField(tf);
        JPasswordField pf1 = new JPasswordField(20);
        setFirstPasswordField(pf1);
        JPasswordField pf2 = new JPasswordField(20);
        setSecondPasswordField(pf2);

        String[] prompts = {
            "User", "Password", "Retype Password"
        };

        JComponent[] comps = {
            tf, pf1, pf2
        };

        setPromptPanel(new PromptPanel(prompts, comps));
    }

    private File getFile() {
        return (file);
    }

    private void setFile(File f) {
        file = f;
    }

    private PromptPanel getPromptPanel() {
        return (promptPanel);
    }

    private void setPromptPanel(PromptPanel pp) {
        promptPanel = pp;
    }

    private JTextField getUserTextField() {
        return (userTextField);
    }

    private void setUserTextField(JTextField tf) {
        userTextField = tf;
    }

    private JPasswordField getFirstPasswordField() {
        return (firstPasswordField);
    }

    private void setFirstPasswordField(JPasswordField pf) {
        firstPasswordField = pf;
    }

    private JPasswordField getSecondPasswordField() {
        return (secondPasswordField);
    }

    private void setSecondPasswordField(JPasswordField pf) {
        secondPasswordField = pf;
    }

    private void generateSchedulesDirect(File f, String user, String password) {

        if ((f != null) && (user != null) && (password != null)) {

            BufferedWriter bw = null;
            try {

                bw = new BufferedWriter(new FileWriter(f));
                String line = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "<properties>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "    <userName>";
                bw.write(line, 0, line.length());
                bw.write(user, 0, user.length());
                line = "</userName>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "    <password>";
                bw.write(line, 0, line.length());
                bw.write(password, 0, password.length());
                line = "</password>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "    <numberOfDays>14</numberOfDays>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "    <webserviceURI>" + WEBSERVICE
                    + "</webserviceURI>";
                bw.write(line, 0, line.length());
                bw.newLine();
                line = "</properties>";
                bw.write(line, 0, line.length());
                bw.newLine();

            } catch (IOException ex) {

                System.out.println("WARNING: Failed to create config.");

            } finally {

                try {

                    if (bw != null) {

                        bw.close();
                        bw = null;
                    }

                } catch (IOException ex) {

                    bw = null;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        PromptPanel pp = getPromptPanel();
        File f = getFile();
        if ((pp != null) && (f != null)) {

            boolean done = false;

            while (!done) {

                if (Util.showDialog(null, "Schedules Direct Login", pp)) {

                    String user = getUserTextField().getText();
                    char[] first = getFirstPasswordField().getPassword();
                    char[] second = getSecondPasswordField().getPassword();
                    if (Arrays.equals(first, second)) {

                        generateSchedulesDirect(f, user.trim(),
                            new String(first));
                        done = true;

                    } else {

                        JOptionPane.showMessageDialog(null,
                            "Passwords don't match", "alert",
                            JOptionPane.ERROR_MESSAGE);
                    }

                } else {

                    done = true;
                }
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}
