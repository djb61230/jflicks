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
package org.jflicks.ui.view.fe.screen.livetv;

import java.awt.Rectangle;
import javax.swing.SwingUtilities;

import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.player.Player;
import org.jflicks.transfer.Transfer;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.ui.view.fe.ChannelInfoWindow;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.RecordingInfoWindow;
import org.jflicks.util.Hostname;
import org.jflicks.util.Util;

/**
 * This class supports Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LiveTVScreen extends BaseLiveTVScreen {

    /**
     * Simple empty constructor.
     */
    public LiveTVScreen() {

        setTitle("Live TV");
    }

    public void startPlayer(LiveTV l) {

        Player p = getPlayer();
        if ((p != null) && (l != null)) {

            if (p.isPlaying()) {

                p.removePropertyChangeListener("Completed", this);
                p.stop();
            }

            View v = getView();
            if (v instanceof FrontEndView) {

                FrontEndView fev = (FrontEndView) v;
                p.setRectangle(fev.getPosition());
            }

            p.setFrame(Util.findFrame(this));

            RecordingInfoWindow riw = getRecordingInfoWindow();
            if (riw != null) {
                riw.setPlayer(p);
            }

            String hostaddr = Hostname.getHostAddress();
            System.out.println("udp://@" + hostaddr + ":1234");
            p.play("udp://@" + hostaddr + ":1234");
        }
    }

    @Override
    public void close() {

        log(DEBUG, "Yep at close!!!!");
        super.close();
    }

    public LiveTV openSession(NMS n, String number) {

        LiveTV result = null;

        if (n != null) {

            String hostaddr = Hostname.getHostAddress();
            result = n.openSession(hostaddr, 1234, number);
        }

        return (result);
    }

    public void changeChannel(LiveTV l, Channel c) {

        if ((l != null) && (c != null)) {

            if (!c.equals(l.getCurrentChannel())) {

                Player p = getPlayer();
                if (p != null) {

                    ChannelInfoWindow cw = getChannelInfoWindow();
                    if (cw != null) {

                        cw.setVisible(false);
                    }

                    if (p.isPlaying()) {

                        p.stop();
                    }
                    p.setSize(p.getRectangle());
                    repaint();

                    NMS n = NMSUtil.select(getNMS(), l.getHostPort());
                    if (n != null) {

                        setWatchingStartTime(System.currentTimeMillis());
                        l = n.changeChannel(l, c);
                        if (l.getMessageType() == LiveTV.MESSAGE_TYPE_NONE) {

                            setLastChannel(c);
                            setLiveTV(l);
                            updateInfoWindow();
                            controlKeyboard(false);
                            final LiveTV fl = l;
                            Runnable doRun = new Runnable() {

                                public void run() {

                                    log(DEBUG, "Starting player...");
                                    startPlayer(fl);
                                }
                            };
                            SwingUtilities.invokeLater(doRun);

                        } else {

                            n.closeSession(l);
                            setLiveTV(null);
                            setDone(true);
                        }

                    } else {

                        setLiveTV(null);
                        setDone(true);
                    }
                }
            }
        }
    }

}

