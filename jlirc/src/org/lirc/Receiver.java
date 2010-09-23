/**************************************************************************/
/*
/* Receiver.java -- Part of the org.lirc package
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

/** Reads signals from the lircd socket. The actual work is done
	by a platform-specific Receiver.

	Most applications wouldn't use this class directly, use
	{@link LIRCClient LIRCClient} instead.

	Usage:

	<pre>
	Receiver rec = ReceiverFactory.createReceiver();

	while (keepListening){
	  	String code = rec.readCode();
        // do something with the code
	}

	rec.close();

	</pre>

	@see ReceiverFactory

	@version $Revision: 1.3 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public interface Receiver {

	/** Disconnects from the socket. */
	public void close();

	/** Reads a string from the daemon. Blocks if there is nothing to read.
	 * @return 'code' 'repeat count' 'button name' 'remote control name'
	 * @throws LIRCException if there was a problem reading
	 */
	public String readCode() throws LIRCException;

}