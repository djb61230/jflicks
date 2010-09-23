/**************************************************************************/
/*
/* LinuxReceiver.java -- Part of the org.lirc package
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

import org.lirc.socket.UnixSocket;
import java.io.IOException;

/**
 * Used to read signals from the lircd Unix Socket.
 * Don't use this class directly, use {@link ReceiverFactory ReceiverFactory} instead.
 *
 * @version $Revision: 1.6 $
 * @author Bjorn Bringert (bjorn@mumblebee.com)
 */
public class LinuxReceiver extends StreamReceiver {

	/** System property for setting the socket name. */
	private static final String SOCKET_PROPERTY = "org.lirc.LinuxReceiver.socketName";

	/** Default socket name. */
	private static final String DEFAULT_SOCKET = "/dev/lircd";

	/** The unix socket used to listen to the daemon. */
	private UnixSocket sock;

	/** Connects to a unix socket. If the system property
		<code>org.lirc.LinuxReceiver.socketName</code> is set its value is used as
		the socket name, otherwise "/dev/lircd" is used.
		@throws org.lirc.LIRCException if it cannot connect.
	*/
	public LinuxReceiver() throws LIRCException {
		connect(System.getProperty(SOCKET_PROPERTY, DEFAULT_SOCKET));
	}

	/**
	 * Connects to a unix socket.
	 * @param socketName name of the socket (e.g. '/dev/lircd')
	 * @throws org.lirc.LIRCException if it cannot connect.
	 */
	public LinuxReceiver(String socketName) throws LIRCException {
		connect(socketName);
	}

	/**
	 * Connects to the socket.
	 * @param path The socket to connect to
	 * @throws org.lirc.LIRCException if it cannot connect.
	 */
	private void connect(String path) throws LIRCException {
		try {
			sock = new UnixSocket(path);
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