/**************************************************************************/
/*
/* ReceiverFactory.java -- Part of the org.lirc package
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

	@see Receiver

	@version $Revision: 1.4 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class ReceiverFactory {

	/** System property used for setting the receiver class to use. */
	private static final String RECEIVER_PROPERTY = "org.lirc.ReceiverFactory.receiver";

	/** Gets an appropriate receiver for this platform.
		If the system property <code>org.lirc.ReceiverFactory.receiver</code> is set
		its value is treated as a fully qualified class name and the receiver is
		created from that class.

		Otherwise the default receiver for the platform is loaded.

		@throws org.lirc.LIRCException if there is a problem loading the
			receiver class or connecting to the lircd daemon
	*/
	public static Receiver createReceiver() throws LIRCException {
		// Get the receiver from the system property
		String recName = System.getProperty(RECEIVER_PROPERTY);

		if (recName == null) {
			// Get receiver for this platform
			String os = System.getProperty("os.name");
			if (os.indexOf("Linux") > -1) {
				recName = "org.lirc.LinuxReceiver";
			} else {
				recName = "org.lirc.TCPReceiver";
			}
		}

		try {
			Class recClass = Class.forName(recName);
	 		return (Receiver)recClass.newInstance();
		} catch (Exception ex) {
			throw new LIRCException("Error loading receiver (" + recName + "): " + ex.toString());
		}
	}
}