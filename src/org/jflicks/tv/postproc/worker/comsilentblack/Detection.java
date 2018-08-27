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
package org.jflicks.tv.postproc.worker.comsilentblack;

import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;

import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This class contains all the properties representing a detection.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Detection implements Serializable, Comparable<Detection> {

    private Double start;
    private Double end;
    private Double duration;

    /**
     * Simple empty constructor.
     */
    public Detection() {
    }

    public Double getStart() {
        return (start);
    }

    public void setStart(Double d) {
        start = d;
    }

    public Double getEnd() {
        return (end);
    }

    public void setEnd(Double d) {
        end = d;
    }

    public Double getDuration() {
        return (duration);
    }

    public void setDuration(Double d) {
        duration = d;
    }

    public static Detection[] parseSilence(String s) {

        Detection[] result = null;

        if (s != null) {

            String[] lines = s.split("\n");
            if ((lines != null) && (lines.length > 0)) {

                // Make a first pass and just get the silence lines.
                ArrayList<String> l = new ArrayList<String>();
                for (int i = 0; i < lines.length; i++) {

                    String tmp = lines[i].trim();
                    if (tmp.startsWith("[silencedetect")) {

                        l.add(tmp);
                    }
                }

                LogUtil.log(LogUtil.DEBUG, "Silence line count: " + l.size());
                for (int i = 0; i < l.size(); i++) {

                    LogUtil.log(LogUtil.DEBUG, l.get(i));
                }

                if (l.size() > 0) {


                    if ((l.size() % 2) == 1) {

                        LogUtil.log(LogUtil.DEBUG, "Hey we should have even number of silence lines");

                    } else {

                        ArrayList<Detection> list = new ArrayList<Detection>();
                        for (int i = 0; i < l.size(); i += 2) {

                            String line0 = l.get(i);
                            String line1 = l.get(i + 1);

                            // Grab the start from line0.
                            int index = line0.indexOf(":");
                            if (index != -1) {

                                String startstr = line0.substring(index + 1);
                                startstr = startstr.trim();

                                index = line1.indexOf(":");

                                if (index != -1) {

                                    String endstr = line1.substring(index + 1);
                                    int eindex = endstr.indexOf("|");
                                    if (eindex != -1) {

                                        endstr = endstr.substring(0, eindex);
                                        endstr = endstr.trim();

                                        Detection d = new Detection();
                                        d.setStart(Util.str2Double(startstr, 0));
                                        d.setEnd(Util.str2Double(endstr, 0));
                                        d.setDuration(d.getEnd() - d.getStart());

                                        LogUtil.log(LogUtil.DEBUG, "Silence Detection: " + d);

                                        list.add(d);
                                    }
                                }
                            }
                        }

                        if (list.size() > 0) {

                            result = list.toArray(new Detection[list.size()]);
                        }
                    }
                }
            }
        }

        return (result);
    }

    public static Detection[] parseBlack(String s) {

        Detection[] result = null;

        if (s != null) {

            String[] lines = s.split("\n");
            if ((lines != null) && (lines.length > 0)) {

                LogUtil.log(LogUtil.DEBUG, "Black raw line count: " + lines.length);

                // Make a first pass and just get the black lines.
                ArrayList<String> l = new ArrayList<String>();
                for (int i = 0; i < lines.length; i++) {

                    String tmp = lines[i].trim();
                    if (tmp.indexOf("black_start") != -1) {

                        l.add(tmp);
                    }
                }

                LogUtil.log(LogUtil.DEBUG, "Black line count: " + l.size());
                for (int i = 0; i < l.size(); i++) {

                    LogUtil.log(LogUtil.DEBUG, l.get(i));
                }

                if (l.size() > 0) {

                    ArrayList<Detection> list = new ArrayList<Detection>();
                    for (int i = 0; i < l.size(); i++) {

                        String line = l.get(i);
                        line = line.trim();

                        // Grab the start from line.
                        int index = line.indexOf("black_start:");

                        if (index != -1) {

                            String startstr = line.substring(index + 12);
                            startstr = startstr.substring(0, startstr.indexOf(" "));
                            startstr = startstr.trim();

                            index = line.indexOf("black_end:");
                            if (index != -1) {

                                String endstr = line.substring(index + 10);
                                endstr = endstr.substring(0, endstr.indexOf(" "));
                                endstr = endstr.trim();

                                Detection d = new Detection();
                                d.setStart(Util.str2Double(startstr, 0));
                                d.setEnd(Util.str2Double(endstr, 0));
                                d.setDuration(d.getEnd() - d.getStart());

                                LogUtil.log(LogUtil.DEBUG, "Black Detection: " + d);

                                list.add(d);
                            }
                        }
                    }

                    if (list.size() > 0) {

                        result = list.toArray(new Detection[list.size()]);
                    }
                }
            }
        }

        return (result);
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {

        int result = 0;

        Double d = getStart();
        if (d != null) {
            result = d.hashCode();
        }

        return (result);
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Detection)) {

            result = false;

        } else {

            Detection d = (Detection) o;
            Line2D.Double dline = new Line2D.Double(d.getStart(), 0, d.getEnd(), 0);
            Line2D.Double line = new Line2D.Double(getStart(), 0, getEnd(), 0);
            result = dline.intersectsLine(line);
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param d The given Detection instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Detection d) throws ClassCastException {

        int result = 0;

        if (d == null) {

            throw new NullPointerException("Given a null Detection to Compare");
        }

        if (d == this) {

            result = 0;

        } else {

            Double start0 = getStart();
            Double start1 = d.getStart();
            if ((start0 != null) && (start1 != null)) {
                result = start0.compareTo(start1);
            }
        }

        return (result);
    }

    @Override
    public String toString() {

        return ("start: " + start + " end: " + end + " duration:" + duration);
    }

}

