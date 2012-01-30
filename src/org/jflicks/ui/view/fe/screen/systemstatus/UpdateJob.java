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
package org.jflicks.ui.view.fe.screen.systemstatus;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.update.Update;
import org.jflicks.update.UpdateState;

/**
 * A job that creates an UpdateState by using an Update service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpdateJob extends AbstractJob {

    private Update update;
    private UpdateState updateState;

    /**
     * Constructor with our required arguments.
     *
     * @param u An Update service to access.
     * @param us An UpdateState from an open.
     */
    public UpdateJob(Update u, UpdateState us) {

        setUpdate(u);
        setUpdateState(us);
    }

    private Update getUpdate() {
        return (update);
    }

    private void setUpdate(Update u) {
        update = u;
    }

    private UpdateState getUpdateState() {
        return (updateState);
    }

    private void setUpdateState(UpdateState us) {
        updateState = us;
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

        Boolean result = Boolean.FALSE;

        Update u = getUpdate();
        UpdateState us = getUpdateState();
        if ((u != null) && (us != null)) {

            result = Boolean.valueOf(u.update(us));
        }

        fireJobEvent(JobEvent.COMPLETE, result);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}
