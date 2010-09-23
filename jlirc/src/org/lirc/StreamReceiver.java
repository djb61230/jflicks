/**************************************************************************/
/*
/* StreamReceiver.java -- Part of the org.lirc package
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

import java.io.*;

/**
 * Superclass for receviers that receive lirc data on an input stream.
 * @version $Revision: 1.1 $
 * @author Bjorn Bringert (bjorn@mumblebee.com)
 */
public class StreamReceiver implements Receiver {

	/** The Reader used to read from the lirc daemon. */
	private BufferedReader in;

	/**
	 * Creates a new StreamReceiver
	 */
	public StreamReceiver() throws LIRCException {

	}

	/**
	 * Creates a new StreamReceiver
	 */
	public StreamReceiver(InputStream inStream) throws LIRCException {
		setInput(inStream);
	}

	/**
	 * Sets the input stream that this recevier reads from.
	 */
	protected void setInput(InputStream inStream) {
		in = new BufferedReader(new InputStreamReader(inStream));
	}

	/**
	 * Closes the input stream.
	 */
	public void close() {
		try {
			in.close();
		} catch (IOException ex) {
			System.err.println(ex.toString());
		}
	}

	public void finalize(){
		close();
	}

	/** Reads a string from the daemon. Blocks if there is nothing to read.
	 * @return 'code' 'repeat count' 'button name' 'remote control name'
	 * @throws LIRCException if there was a problem reading
	 */
	public String readCode() throws LIRCException {
		try {
			String line = in.readLine();
			if (line == null) throw new LIRCException("End of file");
            System.out.println("read: <" + line + ">");
			return line;
		} catch (IOException ex) {
			throw new LIRCException(ex.getMessage());
		}
	}

}
