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
package org.jflicks.ui.view.fe.screen.video;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.JLayeredPane;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.MattePainter;

import org.jflicks.nms.Video;
import org.jflicks.ui.view.fe.BaseCustomizePanel;
import org.jflicks.util.Util;

/**
 * This is a display of video posters in a panel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PosterPanel extends BaseCustomizePanel {

    /**
     * The default aspect ratio is 0.68 which will size each to the shape
     * of a movie poster in portrait mode.
     */
    public static final double DEFAULT_ASPECT_RATIO = 0.68;

    private static final double VERTICAL_GAP = 0.05;
    private static final double HORIZONTAL_GAP = 0.05;
    private static final int EXTRA_PIXEL = 10;

    private int visibleCount;
    private Point[] anchorPoints;
    private Point[] positionPoints;
    private Animator[] animators;
    private ImageTimingTarget[] imageTimingTargets;
    private ArrayList<Video> videoList;
    private ArrayList<BufferedImage> bufferedImageList;
    private BufferedImage[] drawImages;
    private BufferedImage centerImage;
    private double growCount;
    private Point centerImagePoint;
    private Point originalCenterImagePoint;
    private Animator springAnimator;
    private Video selectedVideo;
    private int posterWidth;
    private int posterHeight;
    private double aspectRatio;

    /**
     * Simple empty constructor.
     */
    public PosterPanel() {

        this(3);
    }

    /**
     * Constructor with the number of desired visible posters.
     *
     * @param count The number of visible posters displayed at once.
     */
    public PosterPanel(int count) {

        setDoubleBuffered(true);
        setVisibleCount(count);
        setAspectRatio(DEFAULT_ASPECT_RATIO);
        setVideoList(new ArrayList<Video>());
        setBufferedImageList(new ArrayList<BufferedImage>());
    }

    /**
     * We act upon a set of Video instances to allow the user
     * to select.
     *
     * @return An array of Video objects.
     */
    public Video[] getVideos() {

        Video[] result = null;

        ArrayList<Video> l = getVideoList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Video[l.size()]);
        }

        return (result);
    }

    /**
     * We act upon a set of Video instances to allow the user
     * to select.
     *
     * @param array An array of Video objects.
     */
    public void setVideos(Video[] array) {

        ArrayList<Video> l = getVideoList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
                setSelectedVideo(l.get(0));
            }
        }
    }

    /**
     * We have a open method so the glow animation can be started when
     * we are to be displayed.
     */
    public void open() {
    }

    /**
     * We have a close method so the glow animation can be stopped.
     */
    public void close() {
    }

    /**
     * We act upon a set of BufferedImage instances to display
     * posters.
     *
     * @return An array of BufferedImage objects.
     */
    public BufferedImage[] getBufferedImages() {

        BufferedImage[] result = null;

        ArrayList<BufferedImage> l = getBufferedImageList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new BufferedImage[l.size()]);
        }

        return (result);
    }

    /**
     * We act upon a set of BufferedImage instances to display
     * posters.
     *
     * @param array An array of BufferedImage objects.
     */
    public void setBufferedImages(BufferedImage[] array) {

        ArrayList<BufferedImage> l = getBufferedImageList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                BufferedImage bi = GraphicsUtilities.toCompatibleImage(
                    array[0]);
                setCenterImage(bi);
                setCenterImagePoint(getOriginalCenterImagePoint());

                SpringTimingTarget stt = new SpringTimingTarget(getGrowCount(),
                    bi.getWidth(), bi.getHeight());
                Animator sani = new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(stt).build();
                setSpringAnimator(sani);
                sani.start();
            }
        }
    }

    private BufferedImage getCenterImage() {
        return (centerImage);
    }

    private void setCenterImage(BufferedImage bi) {
        centerImage = bi;
    }

    private Point getOriginalCenterImagePoint() {
        return (originalCenterImagePoint);
    }

    private void setOriginalCenterImagePoint(Point p) {
        originalCenterImagePoint = p;
    }

    private double getGrowCount() {
        return (growCount);
    }

    private void setGrowCount(double d) {
        growCount = d;
    }

    private Point getCenterImagePoint() {
        return (centerImagePoint);
    }

    private void setCenterImagePoint(Point p) {
        centerImagePoint = p;
    }

    private Animator getSpringAnimator() {
        return (springAnimator);
    }

    private void setSpringAnimator(Animator a) {
        springAnimator = a;
    }

    /**
     * The currently selected Video.
     *
     * @return A Video instance.
     */
    public Video getSelectedVideo() {
        return (selectedVideo);
    }

    /**
     * The currently selected Video.
     *
     * @param v A Video instance.
     */
    public void setSelectedVideo(Video v) {

        Video old = selectedVideo;
        selectedVideo = v;
        firePropertyChange("SelectedVideo", old, v);
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            double width = d.getWidth();
            double height = d.getHeight();
            int count = getVisibleCount();

            double hgap = width * HORIZONTAL_GAP;
            double vgap = height * VERTICAL_GAP;
            double labwidth = (width - (((count + 1) * hgap))) / count;
            double labheight = labwidth + (labwidth * (1.0 - getAspectRatio()));
            double labspan = labwidth + hgap;
            double labtop = (height - labheight) / 4;

            setPosterWidth((int) labwidth);
            setPosterHeight((int) labheight);

            // Do the background.
            Color color = getPanelColor();
            color = new Color(color.getRed(), color.getGreen(),
                color.getBlue(), (int) (getPanelAlpha() * 255));
            MattePainter mpainter = new MattePainter(color);
            setBackgroundPainter(mpainter);
            setAlpha((float) getPanelAlpha());

            // When NOT animating we need to have defined points
            // that represent "anchor" points.  These points are
            // when the images are when not animating.  The also
            // should help us know when the animation is over
            // because the destination has been reached.  We keep
            // two extra for the images off the edge on both sides.
            Point[] array = new Point[count + 2];
            for (int i = 0; i < array.length; i++) {

                array[i] = new Point();
                array[i].y = (int) labtop;
                if (i == 0) {

                    // Our point is off on the left.
                    array[i].x = (int) (labspan * -1.0);

                } else if ((i + 1) == array.length) {

                    // Our point is off on the right.
                    array[i].x = (int) (width + hgap);

                } else {

                    // We can see our image.
                    array[i].x = (int) ((labspan * (i - 1)) + hgap);
                }
            }

            setAnchorPoints(array);

            // At any one point we should be (at most) animating count + 2
            // posters.  Worse case is that the fourth is clipped.
            Point[] parray = new Point[array.length];
            for (int i = 0; i < parray.length; i++) {

                parray[i] = new Point(array[i]);
            }

            setPositionPoints(parray);

            Animator[] anis = new Animator[array.length];
            ImageTimingTarget[] itts = new ImageTimingTarget[array.length];
            for (int i = 0; i < anis.length; i++) {

                int leftIndex = i - 1;
                int rightIndex = i + 1;
                Point start = array[i];
                Point left = null;
                if (leftIndex >= 0) {
                    left = array[leftIndex];
                }
                Point right = null;
                if (rightIndex < array.length) {
                    right = array[rightIndex];
                }

                itts[i] = new ImageTimingTarget(start, left, right, parray[i]);
                anis[i] = new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(itts[i]).build();

                if (i == (array.length / 2)) {

                    setOriginalCenterImagePoint(new Point(start));
                    setCenterImagePoint(new Point(start));
                    setGrowCount(labtop - 2);
                    anis[i].addTarget(
                        new ShiftTimingTarget(getGrowCount(), itts[i]));
                }
            }

            setImageTimingTargets(itts);
            setAnimators(anis);
        }
    }

    private Point[] getPositionPoints() {
        return (positionPoints);
    }

    private void setPositionPoints(Point[] array) {
        positionPoints = array;
    }

    private Point[] getAnchorPoints() {
        return (anchorPoints);
    }

    private void setAnchorPoints(Point[] array) {
        anchorPoints = array;
    }

    private ImageTimingTarget[] getImageTimingTargets() {
        return (imageTimingTargets);
    }

    private void setImageTimingTargets(ImageTimingTarget[] array) {
        imageTimingTargets = array;
    }

    private Animator[] getAnimators() {
        return (animators);
    }

    private void setAnimators(Animator[] array) {
        animators = array;
    }

    private int getVisibleCount() {
        return (visibleCount);
    }

    private void setVisibleCount(int i) {
        visibleCount = i;
    }

    private double getAspectRatio() {
        return (aspectRatio);
    }

    private void setAspectRatio(double d) {
        aspectRatio = d;
    }

    /**
     * We set our poster to a particular width.
     *
     * @return The width in pixels as an int.
     */
    public int getPosterWidth() {
        return (posterWidth);
    }

    private void setPosterWidth(int i) {
        posterWidth = i;
    }

    /**
     * We set our poster to a particular height.
     *
     * @return The height in pixels as an int.
     */
    public int getPosterHeight() {
        return (posterHeight);
    }

    private void setPosterHeight(int i) {
        posterHeight = i;
    }

    private ArrayList<Video> getVideoList() {
        return (videoList);
    }

    private void setVideoList(ArrayList<Video> l) {
        videoList = l;
    }

    private ArrayList<BufferedImage> getBufferedImageList() {
        return (bufferedImageList);
    }

    private void setBufferedImageList(ArrayList<BufferedImage> l) {
        bufferedImageList = l;
    }

    private BufferedImage[] getDrawImages() {

        BufferedImage[] result = null;

        if (drawImages != null) {

            result = drawImages;

        } else {

            result = getCurrentBufferedImages();
        }

        return (result);
    }

    private void setDrawImages(BufferedImage[] array) {
        drawImages = array;
    }

    private BufferedImage[] getCurrentBufferedImages() {

        BufferedImage[] result = null;

        ArrayList<BufferedImage> l = getBufferedImageList();
        if (l != null) {

            result = new BufferedImage[getVisibleCount() + 2];
            if (getVisibleCount() > l.size()) {

                int index = (result.length / 2);
                for (int i = 0; i < l.size(); i++) {

                    result[index++] = l.get(i);
                }

            } else {

                // We have at least one circle....
                int index = l.size() - (result.length / 2);
                for (int i = 0; i < result.length; i++) {

                    if ((index >= 0) && (index < l.size())) {
                        result[i] = l.get(index);
                    }
                    index++;
                    if (index == l.size()) {
                        index = 0;
                    }
                }
            }
        }

        return (result);
    }

    private void stopAll() {

        Animator[] array = getAnimators();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                array[i].stop();
            }
        }

        Animator sani = getSpringAnimator();
        if (sani != null) {

            sani.stop();
        }

        setCenterImage(null);
    }

    private void startAll() {

        Animator[] array = getAnimators();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                array[i].start();
            }
        }
    }

    private void applyDirection(boolean left) {

        ImageTimingTarget[] array = getImageTimingTargets();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                array[i].setGoLeft(left);
            }
        }
    }

    private boolean shouldAnimate() {

        boolean result = false;

        ArrayList<BufferedImage> l = getBufferedImageList();
        if (l != null) {

            result = l.size() >= getVisibleCount();
        }

        return (result);
    }

    /**
     * Override so we can paint our images.
     *
     * @param g A given Graphics object.
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // We have to paint our images...
        Graphics2D g2d = (Graphics2D) g.create();
        Point[] array = getPositionPoints();
        BufferedImage[] images = getDrawImages();
        if ((array != null) && (images != null)
            && (array.length == images.length)) {

            int center = array.length / 2;
            for (int i = 0; i < array.length; i++) {

                if (images[i] != null) {

                    if (i == center) {

                        BufferedImage bi = getCenterImage();
                        Point p = getCenterImagePoint();
                        if ((bi != null) && (p != null)) {

                            g2d.drawImage(bi, p.x, p.y, null);

                        } else {

                            g2d.drawImage(images[i], array[i].x, array[i].y,
                                null);
                        }

                    } else {

                        // Just draw normally...
                        g2d.drawImage(images[i], array[i].x, array[i].y, null);
                    }
                }
            }

        } else {

            System.out.println("gern on else");
            BufferedImage bi = getCenterImage();
            Point p = getCenterImagePoint();
            if ((bi != null) && (p != null)) {

                g2d.drawImage(bi, p.x, p.y, null);
            }
        }
    }

    /**
     * Go to the next video.
     */
    public void next() {

        stopAll();
        if (shouldAnimate()) {

            setDrawImages(getCurrentBufferedImages());
            applyDirection(false);
            startAll();
        }
        ArrayList<BufferedImage> l = getBufferedImageList();
        ArrayList<Video> vl = getVideoList();
        if ((l != null) && (vl != null)) {

            BufferedImage last = l.remove(l.size() - 1);
            l.add(0, last);
            Video vlast = vl.remove(vl.size() - 1);
            vl.add(0, vlast);
            setSelectedVideo(vl.get(0));

            if (!shouldAnimate()) {

                BufferedImage bi = GraphicsUtilities.toCompatibleImage(
                    l.get(0));
                setCenterImage(bi);
                setCenterImagePoint(getOriginalCenterImagePoint());

                SpringTimingTarget stt = new SpringTimingTarget(getGrowCount(),
                    bi.getWidth(), bi.getHeight());
                Animator sani = new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(stt).build();
                setSpringAnimator(sani);
                sani.start();
            }
        }
    }

    /**
     * Go to the previous video.
     */
    public void prev() {

        stopAll();
        if (shouldAnimate()) {

            setDrawImages(getCurrentBufferedImages());
            applyDirection(true);
            startAll();
        }
        ArrayList<BufferedImage> l = getBufferedImageList();
        ArrayList<Video> vl = getVideoList();
        if ((l != null) && (vl != null)) {

            BufferedImage first = l.remove(0);
            l.add(first);
            Video vfirst = vl.remove(0);
            vl.add(vfirst);
            setSelectedVideo(vl.get(0));

            if (!shouldAnimate()) {

                BufferedImage bi = GraphicsUtilities.toCompatibleImage(
                    l.get(0));
                setCenterImage(bi);
                setCenterImagePoint(getOriginalCenterImagePoint());

                SpringTimingTarget stt = new SpringTimingTarget(getGrowCount(),
                    bi.getWidth(), bi.getHeight());
                Animator sani = new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(stt).build();
                setSpringAnimator(sani);
                sani.start();
            }
        }
    }

    private BufferedImage createCompatibleImage(BufferedImage bi) {

        BufferedImage result = null;

        if (bi != null) {

            GraphicsConfiguration gc =
                GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration();
            if (gc != null) {

                int w = bi.getWidth();
                int h = bi.getHeight();
                int t = bi.getTransparency();
                result = gc.createCompatibleImage(w, h, t);
            }
        }

        return (result);
    }

    class ShiftTimingTarget extends TimingTargetAdapter {

        private ImageTimingTarget imageTimingTarget;
        private double growCount;

        public ShiftTimingTarget(double grow, ImageTimingTarget itt) {

            imageTimingTarget = itt;
            growCount = grow;
        }

        public void end(Animator source) {

            // Now we want to Spring the centerImage.
            BufferedImage[] array = getCurrentBufferedImages();
            if (array != null) {

                BufferedImage bi = GraphicsUtilities.toCompatibleImage(
                    array[array.length / 2]);
                setCenterImage(bi);
                setCenterImagePoint(getOriginalCenterImagePoint());

                SpringTimingTarget stt = new SpringTimingTarget(growCount,
                    bi.getWidth(), bi.getHeight());
                Animator sani = new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(stt).build();
                setSpringAnimator(sani);
                sani.start();
            }
        }
    }

    class SpringTimingTarget extends TimingTargetAdapter {

        private Point savePoint;
        private Point workPoint;
        private double growCount;
        private int width;
        private int height;

        public SpringTimingTarget(double grow, int w, int h) {

            savePoint = new Point(getOriginalCenterImagePoint());
            growCount = grow;
            width = w;
            height = h;
        }

        public void timingEvent(Animator source, double fraction) {

            int pixels = (int) (growCount * fraction);
            workPoint = new Point(savePoint);
            workPoint.x = workPoint.x - pixels;
            workPoint.y = workPoint.y - pixels;
            setCenterImagePoint(workPoint);

            int newwidth = width + (2 * pixels);
            int newheight = height + (2 * pixels);

            BufferedImage cbi = getCenterImage();
            if (cbi != null) {

                cbi = Util.resize(cbi, newwidth, newheight);
                setCenterImage(cbi);
            }

            repaint();
        }
    }

    class ImageTimingTarget extends TimingTargetAdapter {

        private Point start;
        private Point left;
        private Point right;
        private Point current;
        private boolean goLeft;

        public ImageTimingTarget(Point start, Point left, Point right,
            Point current) {

            setStart(start);
            setLeft(left);
            setRight(right);
            setCurrent(current);
        }

        public boolean isGoLeft() {
            return (goLeft);
        }

        public void setGoLeft(boolean b) {
            goLeft = b;
        }

        public Point getStart() {
            return (start);
        }

        private void setStart(Point p) {
            start = p;
        }

        private Point getLeft() {
            return (left);
        }

        private void setLeft(Point p) {
            left = p;
        }

        private Point getRight() {
            return (right);
        }

        private void setRight(Point p) {
            right = p;
        }

        public Point getCurrent() {
            return (current);
        }

        private void setCurrent(Point p) {
            current = p;
        }

        public void end(Animator source) {

            current.x = start.x;
            current.y = start.y;
            setDrawImages(null);
        }

        public void timingEvent(Animator source, double fraction) {

            if (isGoLeft()) {

                if ((current != null) && (left != null) && (start != null)) {
                    current.x = (int) (start.x + (left.x - start.x) * fraction);
                }

            } else {

                if ((current != null) && (right != null) && (start != null)) {
                    current.x =
                        (int) (start.x + (right.x - start.x) * fraction);
                }
            }

            repaint();
        }

    }

}

