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
package org.jflicks.job;

/**
 * This runs a Job in a thread.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ThreadContainer extends JobContainer implements Runnable {

    private Thread thread;
    private int priority;

    /**
     * Given a Job create a Thread that will run it.
     *
     * @param j A given Job interface to run.
     */
    public ThreadContainer(Job j) {

        setJob(j);
        setPriority(Thread.NORM_PRIORITY);
    }

    private Thread getThread() {
        return (thread);
    }

    private void setThread(Thread t) {
        thread = t;
    }

    /**
     * The start method.
     */
    public void start() {

        Job j = getJob();
        if (j != null) {

            j.start();

            Thread t = new Thread(this);
            t.setPriority(getPriority());
            setThread(t);
            t.start();
        }

    }

    /**
     * Run the job.
     */
    public void run() {

        Job j = getJob();
        if (j != null) {

            j.run();
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean isInterrupted() {

        boolean result = false;

        Thread t = getThread();
        if (t != null) {

            result = t.isInterrupted();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAlive() {

        boolean result = false;

        Thread t = getThread();
        if (t != null) {

            result = t.isAlive();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void interrupt() {

        Thread t = getThread();
        if (t != null) {

            t.interrupt();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {

        int result = priority;

        Thread t = getThread();
        if (t != null) {

            result = t.getPriority();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setPriority(int i) {

        priority = i;
        Thread t = getThread();
        if (t != null) {

            t.setPriority(i);
        }
    }

}
