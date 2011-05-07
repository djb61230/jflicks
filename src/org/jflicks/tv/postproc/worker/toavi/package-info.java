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
 * The ToAvi worker will run the mencoder program on a Recording
 * and create an avi version of the file.  This is done because seeking
 * on it will be much more accurate or even possible using the players
 * we have.  We also hope this fixes audio sync issues and the like.
 */
package org.jflicks.tv.postproc.worker.toavi;
