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
package org.jflicks.trailer.apple;

import java.util.Date;
import java.util.List;

import org.jflicks.db.DbWorker;
import org.jflicks.trailer.BaseTrailer;
import org.jflicks.trailer.Download;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

/**
 * This class is an implementation of the Trailer interface using
 * Apple's web site.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AppleTrailer extends BaseTrailer implements DbWorker {

    private ObjectContainer objectContainer;
    private Db4oService db4oService;

    /**
     * Simple empty constructor.
     */
    public AppleTrailer() {

        setTitle("Apple Trailer");
    }

    /**
     * We use the Db4oService to persist the program data.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the program data.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    /**
     * Convenience method to get a Download instance given it's title.
     *
     * @param title The tile of the Download.
     * @return A Download instance if it exists.
     */
    public Download getDownloadByTitle(String title) {

        Download result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final String stitle = title;
            List<Download> downs = oc.query(new Predicate<Download>() {

                public boolean match(Download d) {
                    return (stitle.equals(d.getTitle()));
                }
            });

            if ((downs != null) && (downs.size() > 0)) {
                result = downs.get(0);
            }
        }

        return (result);
    }

    /**
     * Add a Download to our data store.
     *
     * @param d A given Download instance.
     */
    public void addDownload(Download d) {

        ObjectContainer oc = getObjectContainer();
        if ((d != null) && (oc != null)) {

            oc.store(d);
            oc.commit();
        }
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                Configuration config = s.newConfiguration();
                config.objectClass(
                    Download.class).objectField("title").indexed(true);
                objectContainer = s.openFile(config, "db/apple.dat");
            }
        }

        return (objectContainer);
    }

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            log(INFO, "AppleTrailer: closed " + result);
            objectContainer = null;

        } else {

            log(INFO, "AppleTrailer: Tried to close "
                + "but objectContainer null.");
        }
    }

    private void purge(ObjectContainer db, Class c) {

        if ((db != null) && (c != null)) {

            ObjectSet result = db.queryByExample(c);
            while (result.hasNext()) {
                db.delete(result.next());
            }
        }
    }

    /**
     * We compute if it's time to update data from Schedules Direct.  We
     * should update once a day.
     *
     * @return True if it's time to get more data.
     */
    public boolean isTimeToUpdate() {

        boolean result = false;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            Status status = null;

            ObjectSet<Status> os = oc.queryByExample(Status.class);
            if (os != null) {
                if (os.size() > 0) {

                    status = os.next();
                    long now = System.currentTimeMillis();
                    long next = status.getNextUpdate();
                    if (now > next) {

                        log(DEBUG, "Time to update! Now is newer!");
                        updateStatus();
                        result = true;

                    } else {

                        log(DEBUG, "Not time to update: " + new Date(next));
                    }

                } else {

                    log(DEBUG, "Time to update! No history...");
                    updateStatus();
                    result = true;
                }

            } else {

                log(DEBUG, "Time to update! No history...");
                updateStatus();
                result = true;
            }
        }

        return (result);
    }

    private void updateStatus() {

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Status> os = oc.queryByExample(Status.class);
            if (os.size() > 0) {

                Status status = os.next();
                status.setLastUpdate(status.getNextUpdate());
                status.setNextUpdate(System.currentTimeMillis()
                    + (1000 * 3600 * 24));
                purge(oc, Status.class);
                oc.store(status);
                oc.commit();

            } else {

                Status status = new Status();
                status.setLastUpdate(System.currentTimeMillis());
                status.setNextUpdate(System.currentTimeMillis()
                    + (1000 * 3600 * 24));
                oc.store(status);
                oc.commit();
            }
        }
    }

}

