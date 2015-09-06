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
package org.jflicks.tv.recorder;

import java.io.File;

import org.jflicks.tv.Channel;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecorderBean extends BaseRecorder {

    private boolean scan;

    /**
     * Simple default constructor.
     */
    public RecorderBean() {
    }

    public RecorderBean(Recorder r) {

        if (r != null) {

            setDevice(r.getDevice());
            setDuration(r.getDuration());
            setExtension(r.getExtension());
            setHost(r.getHost());
            setPort(r.getPort());
            setQuickTunable(r.isQuickTunable());
            setRecording(r.isRecording());
            setRecordingLiveTV(r.isRecordingLiveTV());
            setTitle(r.getTitle());
            setSupportsScan(r.supportsScan());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(Channel c, long duration, File destination, boolean live) {
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() {
    }

    /**
     * {@inheritDoc}
     */
    public void startStreaming(Channel c, String host, int port) {
    }

    /**
     * {@inheritDoc}
     */
    public void stopStreaming() {
    }

    /**
     * {@inheritDoc}
     */
    public void quickTune(Channel c) {
    }

    /**
     * {@inheritDoc}
     */
    public void performScan(Channel[] array, String type) {
    }

    public void setSupportsScan(boolean b) {
        scan = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsScan() {
        return (scan);
    }

}

