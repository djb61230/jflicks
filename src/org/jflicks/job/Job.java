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
 * Simple interface for implementations that need to get stuff done.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Job {

    /**
     * A Job needs to be started.  Implementers should do any one time
     * start up code in this method.
     */
    void start();

    /**
     * Once started, a Job needs to run.  Implementers should do their
     * main work here.  If they mean to "poll" they should execute a
     * "while" structure.  The job is complete when this method is complete.
     */
    void run();

    /**
     * Stop the job whether it is complete or not.  Clean up all resources
     * used.
     */
    void stop();

    /**
     * Signifies whether a Job implementation like this class should
     * exit their run method.  By doing so, the job will clean-up properly.
     *
     * @return True if it's time to terminate.
     */
    boolean isTerminate();

    /**
     * Signifies whether a Job implementation like this class should
     * exit their run method.  By doing so, the job will clean-up properly.
     *
     * @param b True if it's time to terminate.
     */
    void setTerminate(boolean b);

    /**
     * A job that does repeated "tasks" generally will sleep between
     * iterations.
     *
     * @return The sleep time in milliseconds.
     */
    long getSleepTime();

    /**
     * A job that does repeated "tasks" generally will sleep between
     * iterations.
     *
     * @param l The sleep time in milliseconds.
     */
    void setSleepTime(long l);
}

