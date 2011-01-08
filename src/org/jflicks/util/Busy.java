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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.Jobable;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

/**
 * A UI that displays a progress bar while a given job is running.  It
 * listens for JobEvents so it will know when the job is complete.  There
 * is also a cancel button supplied so the user can interrupt the job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Busy implements JobListener, Jobable {

    private ArrayList<JobListener> jobList =
        new ArrayList<JobListener>();

    private JLayeredPane layeredPane;
    private AbstractJob job;
    private JobContainer jobContainer;
    private JXBusyLabel busyLabel;

    /**
     * Constructor with our 2 required arguments.
     *
     * @param p The LayeredPane used to center our busy label.
     * @param job the given job to start.
     */
    public Busy(JLayeredPane p, AbstractJob job) {

        setLayeredPane(p);
        setJob(job);
        init();
        job.addJobListener(this);
    }

    private JLayeredPane getLayeredPane() {
        return (layeredPane);
    }

    private void setLayeredPane(JLayeredPane p) {
        layeredPane = p;
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

    private JXBusyLabel getBusyLabel() {
        return (busyLabel);
    }

    private void setBusyLabel(JXBusyLabel bl) {
        busyLabel = bl;
    }

    private void init() {

        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            // We will size it to 1/1o the width.
            Dimension pd = pane.getSize();
            int width = (int) pd.getWidth();
            int height = (int) pd.getHeight();
            int size = width / 5;
            JXBusyLabel bl = new JXBusyLabel(new Dimension(size, size));
            BusyPainter painter = new BusyPainter(size);
            painter.setTrailLength(5);
            painter.setPoints(10);
            painter.setFrame(1);
            bl.setPreferredSize(new Dimension(size, size));
            bl.setIcon(new EmptyIcon(size, size));
            bl.setBusyPainter(painter);
            bl.setBusy(true);
            setBusyLabel(bl);

            int x = (width - size) / 2;
            int y = (height - size) / 2;
            bl.setBounds(x, y, size, size);

            pane.add(bl, JLayeredPane.POPUP_LAYER);
        }

        /*
        JBusy progress = new JBusy(0, 600);
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
        */
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

            JXBusyLabel bl = getBusyLabel();
            if (bl != null) {

                bl.setBusy(false);
                JLayeredPane p = getLayeredPane();
                if (p != null) {

                    p.remove(p.getIndexOf(bl));
                }
            }

            String message = event.getMessage();
            if (message != null) {

                // We assume we need to alert the user on some problem.
                JOptionPane.showMessageDialog(Util.findWindow(getLayeredPane()),
                    "Error: " + message, "alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        fireJobEvent(event);
    }

}
