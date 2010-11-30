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
package org.jflicks.photomanager.digikam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.jflicks.db.DbWorker;
import org.jflicks.photomanager.BasePhotoManager;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.Tag;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.osgi.Db4oService;

/**
 * Class that implements the PhotoManager service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DigikamPhotoManager extends BasePhotoManager implements DbWorker {

    private static final String TAGS_SQL = "select id, pid, name from TAGS";
    private static final String ALBUMROOTS_SQL =
        "select id, specificPath from ALBUMROOTS";
    private static final String ALBUMS_SQL =
        "select id, albumRoot, relativePath from ALBUMS";
    private static final String IMAGES_SQL =
        "select id, album, name from IMAGES";
    private static final String IMAGETAGS_SQL =
        "select imageid, tagid from IMAGETAGS";

    private ObjectContainer objectContainer;
    private Db4oService db4oService;

    /**
     * Simple default constructor.
     */
    public DigikamPhotoManager() {

        setTitle("Digikam Photo Manager");
    }

    /**
     * We use the Db4oService to persist the recording settings.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the recording settings.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                config.objectClass(Tag.class).objectField("id").indexed(true);
                config.objectClass(Photo.class).objectField("id").indexed(true);
                config.objectClass(Tag.class).cascadeOnUpdate(true);
                config.objectClass(Photo.class).cascadeOnUpdate(true);
                objectContainer = s.openFile(config, "db/pm-digikam.dat");
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
            log(INFO, "DigikamPhotoManager: closed " + result);
            objectContainer = null;

        } else {

            log(WARNING, "DigikamPhotoManager: Tried to close "
                + "but objectContainer null.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Tag getRootTag() {

        Tag result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Tag> os = oc.queryByExample(Tag.class);
            if (os != null) {

                Tag[] array = os.toArray(new Tag[os.size()]);
                if (array != null) {

                    for (int i = 0; i < array.length; i++) {

                        if (array[i].isRoot()) {

                            result = array[i];
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Photo[] getPhotos() {

        Photo[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Photo> os = oc.queryByExample(Photo.class);
            if (os != null) {

                result = os.toArray(new Photo[os.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void photoScan() {

        String s = getConfiguredPhotoManagerURL();
        if (s != null) {

            try {

                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection(s);
                if (conn != null) {

                    clean();

                    ArrayList<Tag> allList = new ArrayList<Tag>();

                    Tag root = new Tag();
                    root.setId(0);
                    root.setName("Root");
                    allList.add(root);
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(TAGS_SQL);
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        int pid = rs.getInt("pid");
                        String name = rs.getString("name");
                        Tag item = new Tag();
                        item.setId(id);
                        item.setParentId(pid);
                        item.setName(name);
                        allList.add(item);
                    }

                    // Now we need to hook up all the parents and kids.
                    if (allList.size() > 0) {

                        Tag[] all = allList.toArray(new Tag[allList.size()]);
                        for (int i = 1; i < all.length; i++) {

                            Tag t = findTagById(all, all[i].getParentId());
                            if (t != null) {

                                all[i].setParent(t);
                                t.addChild(all[i]);
                            }
                        }
                    }

                    ArrayList<AlbumRoot> roots = new ArrayList<AlbumRoot>();
                    rs = st.executeQuery(ALBUMROOTS_SQL);
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        String specificPath = rs.getString("specificPath");

                        AlbumRoot tmp = new AlbumRoot();
                        tmp.setId(id);
                        tmp.setSpecificPath(specificPath);
                        roots.add(tmp);
                    }

                    ArrayList<Album> albums = new ArrayList<Album>();
                    rs = st.executeQuery(ALBUMS_SQL);
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        int rootId = rs.getInt("albumRoot");
                        String relativePath = rs.getString("relativePath");

                        Album tmp = new Album();
                        tmp.setId(id);
                        tmp.setRootId(rootId);
                        tmp.setRelativePath(relativePath);
                        albums.add(tmp);
                    }

                    ArrayList<Image> images = new ArrayList<Image>();
                    rs = st.executeQuery(IMAGES_SQL);
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        int aid = rs.getInt("album");
                        String name = rs.getString("name");

                        Image tmp = new Image();
                        tmp.setId(id);
                        tmp.setAlbumId(aid);
                        tmp.setName(name);
                        images.add(tmp);
                    }

                    for (int i = 0; i < images.size(); i++) {

                        Image tmp = images.get(i);
                        if (tmp != null) {

                            String ppath = computePhotoPath(tmp.getName(),
                                tmp.getAlbumId(), albums, roots);
                            if (ppath != null) {

                                Photo p = new Photo();
                                p.setPath(ppath);

                                ArrayList<Tag> ptags = new ArrayList<Tag>();
                                rs = st.executeQuery(IMAGETAGS_SQL);
                                while (rs.next()) {

                                    int imageid = rs.getInt("imageid");
                                    if (imageid == tmp.getId()) {

                                        int tagid = rs.getInt("tagid");
                                        Tag ttmp = findTagById(root, tagid);
                                        if (ttmp != null) {

                                            ptags.add(ttmp);
                                        }
                                    }
                                }

                                if (ptags.size() > 0) {

                                    String[] sarray = new String[ptags.size()];
                                    for (int j = 0; j < sarray.length; j++) {

                                        sarray[j] = ptags.get(j).toPath();
                                    }

                                    p.setTagPaths(sarray);
                                }

                                log(DEBUG, "adding photo with path "
                                    + p.getPath());
                                if (p.getTagPaths() != null) {

                                    log(DEBUG, "    photo with tags "
                                        + p.getTagPaths().length);

                                } else {

                                    log(DEBUG, "    photo with tags 0");
                                }

                                addPhoto(p);
                            }
                        }
                    }

                    st.close();
                    conn.close();

                    root.dump();

                    addTag(root);
                }

            } catch (ClassNotFoundException ex) {

                log(WARNING, "photoScan: " + ex.getMessage());

            } catch (SQLException ex) {

                log(WARNING, "photoScan: " + ex.getMessage());
            }
        }
    }

    private Tag findTagById(Tag[] array, int id) {

        Tag result = null;

        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                log(DEBUG, "Check id <" + id + "> with <" + array[i].getId());
                if (id == array[i].getId()) {

                    result = array[i];
                    break;
                }
            }

            if (result == null) {

                log(DEBUG, "Did not find by id: " + id);
            }
        }

        return (result);
    }

    private Tag findTagById(Tag t, int id) {

        Tag result = null;

        if (t != null) {

            if (t.getId() == id) {
                return (t);
            } else {

                // Check the kids...
                Tag[] kids = t.getChildren();
                if (kids != null) {

                    for (int i = 0; i < kids.length; i++) {

                        result = findTagById(kids[i], id);
                        if (result != null) {

                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private String computePhotoPath(String name, int albumId,
        ArrayList<Album> albums, ArrayList<AlbumRoot> roots) {

        String result = null;

        if ((name != null) && (albums != null) && (roots != null)) {

            String apath = computeAlbumPath(albumId, albums, roots);
            if (apath != null) {

                result = apath + "/" + name;
                result = result.replaceAll("//", "/");
            }
        }

        return (result);
    }

    private String computeAlbumPath(int albumId, ArrayList<Album> albums,
        ArrayList<AlbumRoot> roots) {

        String result = null;

        if ((albums != null) && (roots != null)) {

            for (int i = 0; i < albums.size(); i++) {

                Album tmp = albums.get(i);
                if (tmp != null) {

                    int id = tmp.getId();
                    if (id == albumId) {

                        int rid = tmp.getRootId();
                        String rpath = computeRootPath(rid, roots);
                        if (rpath != null) {

                            result = rpath + "/" + tmp.getRelativePath();
                        }

                        break;
                    }
                }
            }
        }

        return (result);
    }

    private String computeRootPath(int rootId, ArrayList<AlbumRoot> roots) {

        String result = null;

        if (roots != null) {

            for (int i = 0; i < roots.size(); i++) {

                AlbumRoot tmp = roots.get(i);
                if (tmp != null) {

                    int id = tmp.getId();
                    if (id == rootId) {

                        result = tmp.getSpecificPath();
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private void addTag(Tag t) {

        ObjectContainer oc = getObjectContainer();
        if ((t != null) && (oc != null)) {

            oc.store(t);
            oc.commit();
        }
    }

    private void addPhoto(Photo p) {

        ObjectContainer oc = getObjectContainer();
        log(DEBUG, "addPhoto: " + p + " " + oc);
        if ((p != null) && (oc != null)) {

            oc.store(p);
            oc.commit();
        }
    }

    private void clean() {

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            purge(oc, Tag.class);
            purge(oc, Photo.class);
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

    static class AlbumRoot {

        private int id;
        private String specificPath;

        public AlbumRoot() {
        }

        public int getId() {
            return (id);
        }

        public void setId(int i) {
            id = i;
        }

        public String getSpecificPath() {
            return (specificPath);
        }

        public void setSpecificPath(String s) {
            specificPath = s;
        }

    }

    static class Album {

        private int id;
        private int rootId;
        private String relativePath;

        public Album() {
        }

        public int getId() {
            return (id);
        }

        public void setId(int i) {
            id = i;
        }

        public int getRootId() {
            return (rootId);
        }

        public void setRootId(int i) {
            rootId = i;
        }

        public String getRelativePath() {
            return (relativePath);
        }

        public void setRelativePath(String s) {
            relativePath = s;
        }

    }

    static class Image {

        private int id;
        private int albumId;
        private String name;

        public Image() {
        }

        public int getId() {
            return (id);
        }

        public void setId(int i) {
            id = i;
        }

        public int getAlbumId() {
            return (albumId);
        }

        public void setAlbumId(int i) {
            albumId = i;
        }

        public String getName() {
            return (name);
        }

        public void setName(String s) {
            name = s;
        }

    }

}

