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
 * The RokuMp4 worker will run the HandBrakeCLI program on a Recording
 * and create an mp4 version of the file.  This is done because the
 * Roku can only play certain types of video files and the HandBrakeCLI
 * does a great job transcoding to a proper format.
 */
package org.jflicks.tv.postproc.worker.rokump4;
