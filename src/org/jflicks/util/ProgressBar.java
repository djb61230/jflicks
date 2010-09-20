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
package org.jflicks.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.Jobable;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;

/**
 * A UI that displays a progress bar while a given job is running.  It
 * listens for JobEvents so it will know when the job is complete.  There
 * is also a cancel button supplied so the user can interrupt the job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ProgressBar implements JobListener, Jobable {

    private ArrayList<JobListener> jobList =
        new ArrayList<JobListener>();

    private JComponent component;
    private boolean undecorated;
    private String title;
    private JDialog dialog;
    private AbstractJob job;
    private JobContainer jobContainer;

    /**
     * Constructor with our three required arguments.  The title will be
     * displayed in the frame bar.
     *
     * @param c The Component used to center our dialog box.
     * @param title The title for the dialog box.
     * @param job the given job to start.
     */
    public ProgressBar(JComponent c, String title, AbstractJob job) {

        setUndecorated(false);
        setComponent(c);
        setTitle(title);
        setJob(job);
        init();
        job.addJobListener(this);
    }

    /**
     * Constructor with our 2 required arguments.  Since a title is not
     * suplied then the dialog window will be "undecorated".
     *
     * @param c The Component used to center our dialog box.
     * @param job the given job to start.
     */
    public ProgressBar(JComponent c, AbstractJob job) {

        setUndecorated(true);
        setTitle(null);
        setComponent(c);
        setJob(job);
        init();
        job.addJobListener(this);
    }

    private boolean isUndecorated() {
        return (undecorated);
    }

    private void setUndecorated(boolean b) {
        undecorated = b;
    }

    private JDialog getDialog() {
        return (dialog);
    }

    private void setDialog(JDialog d) {
        dialog = d;
    }

    private JComponent getComponent() {
        return (component);
    }

    private void setComponent(JComponent c) {
        component = c;
    }

    private String getTitle() {
        return (title);
    }

    private void setTitle(String s) {
        title = s;
    }

    private AbstractJob getJob() {
        return (job);
    }

    private void setJob(AbstractJob j) {
        job = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private void init() {

        JProgressBar progress = new JProgressBar(0, 600);
        Dimension prefSize = progress.getPreferredSize();
        prefSize.setSize(prefSize.getWidth() + 100, prefSize.getHeight());
        progress.setPreferredSize(prefSize);
        progress.setIndeterminate(true);
        Window window = Util.findWindow(getComponent());

        final JButton cancel = new JButton("Cancel");
        RowPanel rp = new RowPanel(cancel);
        ColumnPanel cp =
            new ColumnPanel(null, 0, progress, new JSeparator(), rp);
        JDialog dlg = null;
        if (isUndecorated()) {

            dlg = new JDialog(window);
            dlg.setUndecorated(isUndecorated());

        } else {

            dlg = new JDialog(window, getTitle());
        }

        setDialog(dlg);
        cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    JDialog dlg = getDialog();
                    dlg.setVisible(false);
                    cancel.setText("true");
                    dlg.dispose();
                    JobContainer jc = getJobContainer();
                    if (jc != null) {

                        jc.interrupt();
                        setDialog(null);
                        setJobContainer(null);
                    }
                }
            }
        );
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(BorderLayout.CENTER, cp);
        dlg.pack();
        dlg.setLocationRelativeTo(window);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
    }

    /**
     * This will start the Job.
     */
    public void execute() {

        AbstractJob aj = getJob();
        if (aj != null) {

            JobContainer jc = JobManager.getJobContainer(aj);
            jc.start();
            setJobContainer(jc);
        }
    }

    /**
     * Add a job listener.
     *
     * @param l The given job listener.
     */
    public void addJobListener(JobListener l) {
        jobList.add(l);
    }

    /**
     * Remove a job listener.
     *
     * @param l The given job listener.
     */
    public void removeJobListener(JobListener l) {
        jobList.remove(l);
    }

    private void fireJobEvent(JobEvent event) {
        processJobEvent(event);
    }

    private synchronized void processJobEvent(JobEvent event) {

        for (int i = 0; i < jobList.size(); i++) {

            JobListener l = jobList.get(i);
            l.jobUpdate(event);
        }
    }

    /**
     * We need to know the status of the running job.  If it's done
     * we finish and dispose of our dialog box.
     *
     * @param event The given job event instance.
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            JDialog dlg = getDialog();
            if (dlg != null) {

                dlg.setVisible(false);
                dlg.dispose();
            }

            String message = event.getMessage();
            if (message != null) {

                // We assume we need to alert the user on some problem.
                JOptionPane.showMessageDialog(Util.findWindow(getComponent()),
                    "Error: " + message, "alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        fireJobEvent(event);
    }

}
