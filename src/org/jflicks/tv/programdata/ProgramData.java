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
package org.jflicks.tv.programdata;

import org.jflicks.configure.Config;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Listing;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;

/**
 * This interface defines the methods that allow for the creation of recording
 * services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface ProgramData extends Config, DataUpdateable {

    /**
     * The Recorder interface needs a title property.
     */
    String TITLE_PROPERTY = "ProgramData-Title";

    /**
     * The title of this program data service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * An array of Listing instances.  A Listing is a set of channels from
     * a particular provider.  The provider might be a cable or satellite
     * company or a Listing could be a set of OTA channels for an area.
     * A user could have more than one providers or listings.
     *
     * @return An array od Channel objects.
     */
    Listing[] getListings();

    /**
     * Acquire the one and only one Listing using the given name.
     *
     * @param name A Listing name.
     * @return A Listing with the given name if it exists.
     */
    Listing getListingByName(String name);

    /**
     * An array of Channel instances that define the channels that can be
     * recorded.  These will be all the defined channels for all listings.
     *
     * @return An array of Channel objects.
     */
    Channel[] getChannels();

    /**
     * Sometimes one just wants the channels for a particular listing.  Usually
     * a recorder has access to only one listing so it's handy to just get the
     * channels it could actually record.
     *
     * @param l A given listing.
     * @return The channels associated with the given listing.
     */
    Channel[] getChannelsByListing(Listing l);

    /**
     * Find a Channel given its Id.
     *
     * @param id A given Id.
     * @param lid A given listing Id.
     * @return A Channel instance if it is found.
     */
    Channel getChannelById(int id, String lid);

    /**
     * Find a Show given its Id.
     *
     * @param id A given Id.
     * @return A Show instance if it is found.
     */
    Show getShowById(String id);

    /**
     * We keep track of all shows.  This can be quite large so it's perhaps
     * not a great idea to get them all but this method allows for just that.
     * This method is here for debugging purposes right now and will probably
     * go away at some point.
     *
     * @return An array of Show instances.
     */
    Show[] getShows();

    /**
     * It is needed to get the particular Show given one knows when it is
     * Airing.  An Airing instance has a show ID as a property so it can
     * be used to fetch it.
     *
     * @param a A given Airing instance.
     * @return The one and only Show instance.
     */
    Show getShowByAiring(Airing a);

    /**
     * Acquire all the Airings of a particular Channel.
     *
     * @param c A given Channel Instance.
     * @return An array of Airing instances.
     */
    Airing[] getAiringsByChannel(Channel c);

    /**
     * Acquire all the Airings of a particular Show.
     *
     * @param s A given Show Instance.
     * @return An array of Airing instances.
     */
    Airing[] getAiringsByShow(Show s);

    /**
     * Given a Channel, and a series Id, find all the ShowAiring instances
     * that match.  For example, using this method one could find all
     * the times "Seinfeld" was in the schedule for a particular Channel.
     *
     * @param c A given Channel to look for.
     * @param seriesId Eash series type has a unique Id.
     * @return An array of ShowAiring instances.
     */
    ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c, String seriesId);

    /**
     * Search the current set of ShowAiring instances using the given pattern.
     *
     * @param pattern A String query.
     * @param searchType Either SEARCH_TITLE, SEARCH_DESCRIPTION,
     * or SEARCH_TITLE_AND_DESCRIPTION.
     * @return An array of ShowAiring instances that conform to the
     * given pattern.
     */
    ShowAiring[] getShowAirings(String pattern, int searchType);
}

