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
package org.jflicks.imagecache.system;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import javax.imageio.ImageIO;

import org.jflicks.imagecache.BaseImageCache;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This is our implementation of an ImageCache.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemImageCache extends BaseImageCache {

    private HashMap<String, BufferedImage> hashMap;
    private File directoryFile;

    /**
     * Default empty constructor.
     */
    public SystemImageCache() {

        setTitle("SystemImageCache");
        setHashMap(new HashMap<String, BufferedImage>());

        File home = new File(".");
        File f = new File(home, "imagecache");
        if (!f.exists()) {

            try {

                f.mkdir();

            } catch (SecurityException ex) {

                throw new RuntimeException(ex);
            }
        }

        setDirectoryFile(f);
    }

    private HashMap<String, BufferedImage> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<String, BufferedImage> m) {
        hashMap = m;
    }

    private File getHome() {

        String dir = System.getProperty("FE_HOME");
        if (dir == null) {

            dir = ".";
        }

        return (new File(dir));
    }

    private File getDirectoryFile() {
        return (directoryFile);
    }

    private void setDirectoryFile(File f) {
        directoryFile = f;
    }

    private BufferedImage find(String hash) {

        BufferedImage result = null;

        HashMap<String, BufferedImage> m = getHashMap();
        if (m != null) {

            result = m.get(hash);
        }

        return (result);
    }

    private void place(String hash, BufferedImage bi) {

        HashMap<String, BufferedImage> m = getHashMap();
        if (m != null) {

            m.put(hash, bi);
        }
    }

    private String getExtension(String s) {

        String result = "";

        if (s != null) {

            result = s.substring(s.lastIndexOf(".") + 1);
        }

        return (result);
    }

    private boolean isNewerURL(File f, String url) {

        boolean result = false;

        if ((f != null) && (url != null)) {

            result = (f.lastModified() < Util.lastModifiedURL(url));
        }

        return (result);
    }

    private BufferedImage readURL(String urlstr) {

        BufferedImage result = null;

        if (urlstr != null) {

            try {

                URL url = new URL(urlstr);
                result = ImageIO.read(url);

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage getImage(String url) {
        return (getImage(url, true));
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage getImage(String url, boolean keepInMemory) {

        BufferedImage result = null;

        if (url != null) {

            String hash = Util.toMD5(url);
            if (hash != null) {

                String ext = getExtension(url);
                hash = hash + "." + ext;
                result = find(hash);
                if (result == null) {

                    File dir = getDirectoryFile();
                    if (dir != null) {

                        File f = new File(dir, hash);
                        if ((f.exists()) && (!isNewerURL(f, url))) {

                            try {

                                result = ImageIO.read(f);
                                if (keepInMemory) {
                                    place(hash, result);
                                }

                            } catch (IOException ex) {

                                LogUtil.log(LogUtil.WARNING, ex.getMessage());
                            }

                        } else {

                            result = readURL(url);
                            if (result != null) {

                                if (keepInMemory) {
                                    place(hash, result);
                                }
                                try {

                                    ImageIO.write(result, ext, f);

                                } catch (IOException ex) {

                                    LogUtil.log(LogUtil.WARNING, ex.getMessage());
                                }
                            }
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
    public long getLastModified(String url) {

        long result = -1L;

        if (url != null) {

            String hash = Util.toMD5(url);
            if (hash != null) {

                String ext = getExtension(url);
                hash = hash + "." + ext;
                File dir = getDirectoryFile();
                if (dir != null) {

                    File f = new File(dir, hash);
                    if (f.exists()) {
                        result = f.lastModified();
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void putImage(String url, BufferedImage bi) {

        File dir = getDirectoryFile();
        if ((url != null) && (bi != null) && (dir != null)) {

            String hash = Util.toMD5(url);
            String ext = getExtension(url);
            File f = new File(dir, hash + "." + ext);
            try {

                ImageIO.write(bi, ext, f);

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }
    }

}

