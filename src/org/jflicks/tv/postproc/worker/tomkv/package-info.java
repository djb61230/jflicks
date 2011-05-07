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
 * The ToMkv worker will run the projectx and ffmpeg programs on a Recording
 * and create an mkv version of the file.  This is designed for OTA recordings
 * because they often have errors that the projectx can fix.  Without the
 * fix ffmpeg usually craps out on the file.
 */
package org.jflicks.tv.postproc.worker.tomkv;
