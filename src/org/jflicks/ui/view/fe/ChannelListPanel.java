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
package org.jflicks.ui.view.fe;

import java.util.ArrayList;

import org.jflicks.tv.Channel;

/**
 * This class displays Channel objects in a list panel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChannelListPanel extends BaseListPanel {

    private ArrayList<Channel> channelList;

    /**
     * Simple empty constructor.
     */
    public ChannelListPanel() {

        setChannelList(new ArrayList<Channel>());
        setPropertyName("SelectedChannel");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<Channel> l = getChannelList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    private ArrayList<Channel> getChannelList() {
        return (channelList);
    }

    private void setChannelList(ArrayList<Channel> l) {
        channelList = l;
    }

    /**
     * We list channel in our panel.
     *
     * @return An array of Channel instances.
     */
    public Channel[] getChannels() {

        Channel[] result = null;

        return (result);
    }

    /**
     * We list channel in our panel.
     *
     * @param array An array of Channel instances.
     */
    public void setChannels(Channel[] array) {

        ArrayList<Channel> l = getChannelList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                setSelectedObject(null);
                setStartIndex(0);
            }
        }
    }

}

