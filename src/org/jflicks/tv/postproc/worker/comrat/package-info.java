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
/**
 * The Comrat worker will run the ffmpeg program on a recording and
 * get a screenshot every five seconds.  Then we process the image files
 * to try to look for the TV Rating Symbol in the upper left corner since
 * these are displayed after a commercial break.
 */
package org.jflicks.tv.postproc.worker.comrat;
