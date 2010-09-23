/**************************************************************************/
/*
/* LIRCMapSetup.java -- Part of the org.lirc.util package
/* Copyright (C) 2001 Bjorn Bringert (bjorn@mumblebee.com)
/*
/* This program is free software; you can redistribute it and/or
/* modify it under the terms of the GNU General Public License
/* as published by the Free Software Foundation; either version 2
/* of the License, or (at your option) any later version.
/*
/* This program is distributed in the hope that it will be useful,
/* but WITHOUT ANY WARRANTY; without even the implied warranty of
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/* GNU General Public License for more details.
/*
/* You should have received a copy of the GNU General Public License
/* along with this program; if not, write to the Free Software
/* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
/*
/**************************************************************************/

package org.lirc.util;

import org.lirc.*;

import java.util.*;

/** Helps setup a new {@link LIRCMap LIRCMap}.

	@version $Revision: 1.5 $
 	@author Bjorn Bringert (bjorn@mumblebee.com)
*/

public class LIRCMapSetup{

	/** The default repeat value. */
	private static final int DEFAULT_REPEAT = 0;

	/** The values that need a mapping. */
	private Set values;

	/** Iterates throught the values. */
	private Iterator it;

	/** The map the this object sets up. */
	private LIRCMap map;

	/** The value currently being set up. */
	private String currentValue;

	/** Creates a new LIRCMapSetup.

		@param values the set of values (strings) that need to be mapped
	*/
	public LIRCMapSetup(Set values) {
		this.values = values;
		it = values.iterator();
		map = new LIRCMap();
	}

	/** Gets the LIRCMap that this object sets up. */
	public LIRCMap getMap() {
		return map;
	}

	/** Returns true if there are more values that need to have a button mapped to them.
		A true value guarantees that nextName() can be called at least once.
	 */
	public boolean hasNext() {
		return it.hasNext();
	}

	/** Moves on to the next value.

		@return the name of the new value that needs a button
	*/
	public String nextName() {
		currentValue = (String)it.next();
		return currentValue;
	}

	/** Maps a button to the current value, matching any remote.
		@param button name of the button
	*/
	public void setCurrent(String button) {
		setCurrent(button, null, DEFAULT_REPEAT);
	}

	/** Maps a button to the current value.
		@param button name of the button
		@param remote name of the remote
	*/
	public void setCurrent(String button, String remote) {
		setCurrent(button, remote, DEFAULT_REPEAT);
	}

	/** Maps a button to the current value, matching any remote.
		@param button name of the button
		@param remote name of the remote
		@param repeat repeat value
	*/
	public void setCurrent(String button, String remote, int repeat) {
		map.add(button, remote, repeat, currentValue);
	}

}