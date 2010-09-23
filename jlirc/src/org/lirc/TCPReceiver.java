/**************************************************************************/
/*
/* TCPReceiver.java -- Part of the org.lirc package
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

import java.net.Socket;
import java.net.InetAddress;
import java.io.*;

/** Reads signals from the WINLircd TCP socket (normally port 8765).

	Don't use this class directly, use {@link ReceiverFactory ReceiverFactory} instead.

	@version $Revision: 1.6 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class TCPReceiver extends StreamReceiver {

	/** The system property used for the port number. */
	private static final String PORT_PROPERTY = "org.lirc.TCPReceiver.port";

	/** The default port number. */
	private static final int DEFAULT_PORT = 8765;

	/** The TCP socket used to listen to the daemon. */
	private Socket sock;

	/** Connects to the socket. If the system property
		<code>org.lirc.TCPReceiver.port</code> is set its value is used as
		the port number, otherwise 8765 is used.
		@throws org.lirc.LIRCException if it cannot connect, or if
		the value of <code>org.lirc.TCPReceiver.port</code> cannot be converted to an integer.
	*/
	public TCPReceiver() throws LIRCException {
		try {
			int port = Integer.parseInt(System.getProperty(PORT_PROPERTY, String.valueOf(DEFAULT_PORT)));
			connect(port);
		} catch (NumberFormatException ex) {
			throw new LIRCException("Bad value of system property " + PORT_PROPERTY + ": " + ex.getMessage());
		}
	}

	/** Connects to the socket.
		@param port The port to connect to
		@throws org.lirc.LIRCException if it cannot connect.
	*/
	public TCPReceiver(int port) throws LIRCException {
		connect(port);
	}

	/** Connects to the socket.
		@param host The host to connect to
		@param port The port to connect to
		@throws org.lirc.LIRCException if it cannot connect.
	*/
	private void connect(int port) throws LIRCException {
		try {
			sock = new Socket(InetAddress.getLocalHost(), port);
			setInput(sock.getInputStream());
		} catch (IOException ex) {
			throw new LIRCException(ex.getMessage());
		}
	}

	/**
	 * Disconnects from the socket.
	 */
	public void close() {
		super.close();
		try {
			sock.close();
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}
	}

}