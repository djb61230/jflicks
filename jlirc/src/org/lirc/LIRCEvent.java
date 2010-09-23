/**************************************************************************/
/*
/* LIRCEvent.java -- Part of the org.lirc package
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

package org.lirc;

import java.util.EventObject;

/** An event that represents a recevied IR signal (a button press
	or repeat).

	@version $Revision: 1.2 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class LIRCEvent extends EventObject{

	/** The repeat count of this signal. */
	private int repeat;

	/** The name of the remote button that was pressed. */
	private String name;

	/** The name of the remote. */
	private String remote;

	/** Creates a new LIRCEvent.

		@param source the object that created this event
		@param repeat the repeat count
		@param name name of the button press
		@param remote name of the remote
	*/
	public LIRCEvent(Object source, int repeat, String name, String remote) {
		super(source);
		this.repeat = repeat;
		this.name = name;
		this.remote = remote;
	}

	/** Gets the repeat count of this signal.
		@return 0 for the first signal, 1 for the second etc.
	*/
	public int getRepeat() {
		return repeat;
	}

	/** Gets name of the remote button that was pressed. */
	public String getName() {
		return name;
	}

	/** Gets name of the remote control. */
	public String getRemote(){
		return remote;
	}

	/** Gets a string representation of this event. */
	public String toString() {
	 	return "[name: " + name + ", remote: " + remote + ", repeat: " + repeat + "]";
	}

}