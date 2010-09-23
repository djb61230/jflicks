/**************************************************************************/
/*
/* SimpleLIRCClient.java -- Part of the org.lirc.util package
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
import org.lirc.ui.*;

import java.io.*;
import java.util.*;

/** Can be used to quickly add LIRC support to a java application. Takes care
	of loading a config file and mapping button presses to application specific strings. The
	format of this config file is decsribed in the {@link org.lirc.util.LIRCMap LIRCMap}
	documentation.

	See org.lirc.test.MoveDot for an example application.

	Code example:

	<pre>
	private SimpleLIRCClient client;

	...

	String configFile = "myConfig.lirc";
	client = new SimpleLIRCClient("myprog", configFile);
	client.addIRActionListener(new MyIRListener());

	...

	client.stopListening();
	</pre>

	@see org.lirc.util.LIRCMap

	@version $Revision: 1.4 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class SimpleLIRCClient{

	/** List of event listeners. */
	private List listeners = new ArrayList();

	/** Conection to the LIRCD socket */
	private LIRCClient client;

	/** Name of the program that we handle ir stuff for. */
	private String program;

	/** Maps IR events to commands */
	private LIRCMap map;

	/** Creates a new SimpleLIRCClient that loads it's settings from a file.
		@param filename The file to load settings from
		@throws LIRCException if there is a problem connection to the LIRC daemon
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public SimpleLIRCClient(String filename) throws LIRCException, FileNotFoundException, IOException {
		this(null, filename);
	}

	/** Creates a new SimpleLIRCClient that loads it's settings from a file.
		@param file The file to load settings from
		@throws LIRCException if there is a problem connection to the LIRC daemon
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public SimpleLIRCClient(File file) throws LIRCException, FileNotFoundException, IOException {
		this(null, file);
	}

	/** Creates a new SimpleLIRCClient that loads it's settings from a file.
		@param program The program name used in config files, can be null
		@param filename The file to load settings from
		@throws LIRCException if there is a problem connection to the LIRC daemon
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public SimpleLIRCClient(String program, String filename) throws LIRCException, FileNotFoundException, IOException {
		this(program, new File(filename));
	}

	/** Creates a new SimpleLIRCClient that loads it's settings from a file.
		@param program The program name used in config files, can be null
		@param file The file to load settings from
		@throws LIRCException if there is a problem connection to the LIRC daemon
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public SimpleLIRCClient(String program, File file) throws LIRCException, FileNotFoundException, IOException {
		this.program = program;
		loadSettings(file);
		client = new LIRCClient();
		client.addLIRCListener(new IRListener());
	}

	public SimpleLIRCClient(String program, Reader reader) throws LIRCException, FileNotFoundException, IOException {
		this.program = program;
		loadSettings(reader);
		client = new LIRCClient();
		client.addLIRCListener(new IRListener());
	}

	/** Stops listening for IR events. */
	public void stopListening() {
		client.stopListening();
	}

	/** Adds an IRActionListener */
	public void addIRActionListener(IRActionListener l) {
		listeners.add(l);
	}

	/** Removes an IRActionListener */
	public void removeIRActionListener(IRActionListener l) {
		listeners.remove(l);
	}

	/** Notify all listeners that have registered interest for
		notification on this event type.
	*/
	protected void fireIRAction(String command) {
		Iterator it = listeners.iterator();
		while (it.hasNext()){
			((IRActionListener)it.next()).action(command);
		}
	}

	/** Loads a LIRCMap from a file.
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public void loadSettings(String filename) throws FileNotFoundException, IOException {
		loadSettings(new File(filename));
	}

	/** Loads a LIRCMap from a file.
		@throws FileNotFoundException if the config file can't be found
		@throws IOException if there is a problem reading the config file
	*/
	public void loadSettings(File file) throws FileNotFoundException, IOException {
		map = new LIRCMap(program, file);
	}

	public void loadSettings(Reader reader) throws FileNotFoundException, IOException {
		map = new LIRCMap(program);
        map.load(reader);
	}

	/** Saves the LIRCMap to a file.
		@throws IOException if there is a problem writing the config file
	*/
	public void saveSettings(String filename) throws IOException {
		saveSettings(new File(filename));
	}

	/** Saves the LIRCMap to a file.
		@throws IOException if there is a problem writing the config file
	*/
	public void saveSettings(File file) throws IOException {
		map.store(file);
	}

	/** Handles LIRCEvents by loking them up in the LIRCMap and firing an IRAction
		 if there is amapping for the event.
	*/
	private class IRListener implements LIRCListener {
		public void received(LIRCEvent ev) {
			String act = map.get(ev);
			if (act != null) {
				fireIRAction(act);
			}
		}
	}

}
