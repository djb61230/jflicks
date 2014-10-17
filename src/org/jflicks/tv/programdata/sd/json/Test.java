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
package org.jflicks.tv.programdata.sd.json;

/**
 * Tester.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Test {

    /**
     * Simple empty constructor.
     */
    public Test() {
    }

    public static void main(String[] args) {

        Client c = new Client();
        if (c.doToken("djb61230@yahoo.com", "0c7c159f69d50d8444e266a5f9d59ab7de3793c1")) {

            System.out.println(c.getToken());

            if (c.doStatus()) {

                System.out.println("status ok");
                if (c.doHeadend("USA", "12095")) {

                    Headend[] array = c.getHeadends();
                    System.out.println(array);

                    //c.doAddLineup("DIRECTV Albany");
                    //c.doAddLineup("Antenna");
                    Mapping maps = c.getMapping("Antenna");

                    UserLineup ul = c.getUserLineup();
                    System.out.println(ul);

                    //c.doDeleteLineup("DIRECTV Albany");
                    //c.doDeleteLineup("Antenna");

                    ul = c.getUserLineup();
                    System.out.println(ul);
                }
            }
        }
    }

}

