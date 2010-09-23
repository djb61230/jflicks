/**************************************************************************/
/*
/* LIRCMap.java -- Part of the org.lirc.util package
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

import org.lirc.LIRCClient;
import org.lirc.LIRCEvent;
import java.util.*;
import java.io.*;

/**	Maps remote control buttons to Strings.

	The mapping can be saved to and read from a file with the following format:
	(a subset of the .lircrc file format, see the LIRC documentation):
	<pre>
	# comment

	begin
		button = button_name
		[remote = remote_name]
		[repeat = positive integer]
		[config = string]
		[config = string]
		...
	end
	</pre>

	<p>Empty lines and lines starting with &quot;#&quot; are ignored.</p>

	<p><code>button</code> is the name of the button that this section concerns.</p>
	<p><code>remote</code> is the name of the remote control that this section concerns.
	If this line is omitted all remotes will be matched.</p>
	<p><code>repeat</code> specifies how repeat events will be matched. A value of 0
	means that no repeat events will be matched, i.e. only the first event is matched when
	a button is held down. A value n > 0 means that every n:th event is reported, i.e.
	every event for which <code>repeat % n == 0</code>. If the repeat line is omitted
	only the first event will be reported, i.e. repeat = 0</p>
	<p><code>config</code> lines specify the strings that will be mapped to the event. If
	several config strings are specified they will be cycled through. This lets
	you implement toggle buttons etc.
	</p>

	Example:
	<pre>
	begin
		button = power
		config = on
		config = off
	end

	begin
		button = vol+
		repeat = 1
		config = volup
	end

	begin
		button = four
		remote = RM-D90
		repeat = 5
		config = left
	end
	</pre>

	@version $Revision: 1.7 $
	@author Bjorn Bringert (bjorn@mumblebee.com)

*/
public class LIRCMap {

	/** The default repeat value. */
	private static final int DEFAULT_REPEAT = 0;

	/** Name of the program that we handle ir stuff for. */
	private String program;

	/** List of Entry objects. */
	private List list = new ArrayList();

	/** Creates a new empty LIRCMap. */
	public LIRCMap() {}

	/** Creates a new empty LIRCMap. */
	public LIRCMap(String program) {
		this.program = program;
	}

	/** Reads a LIRCMap from a file.
	 	Same as:
		<pre>
		 LIRCMap map = new LIRCMap(program);
		 map.load(file);
		</pre>
	 */
	public LIRCMap(String program, File file) throws IOException {
		this(program);
		load(file);
	}

	/** Loads settings from a file. */
	public void load(String filename) throws IOException {
		load(new File(filename));
	}

	/** Loads settings from a file. */
	public void load(File file) throws IOException {
		FileReader reader = new FileReader(file);
		load(reader);
		reader.close();
	}

	/** Loads settings from a <code>Reader</code>. */
	public void load(Reader reader) throws IOException {
		LineNumberReader in = new LineNumberReader(reader);
		while (readEntry(in, true)) { }
	}

	private boolean readEntry(LineNumberReader in, boolean addIt) throws IOException {
		while (true) { // find start of entry
			String line = in.readLine();
			if (line == null) return false; // EOF
			line = line.trim();
			if (line.equals("") || line.startsWith("#")) { // blank line or comment
				// ignore
			} else if (line.equals("begin")) {
				break; // found start of entry
			} else if (line.startsWith("begin")) { // section for some program, ignore it
				while (readEntry(in, false)) { } // skip until end of section
			} else if (line.startsWith("end")) {
				return false; // found end of section
			} else {
				throw new IOException("Error in input, expected 'begin' on line " + in.getLineNumber());
			}
		}

		String prog = null;
		String button = null;
		String remote = null;
		int repeat = DEFAULT_REPEAT;
		List values = new LinkedList();
		while (true) {
			String line = in.readLine();
			if (line == null) throw new IOException("Error in input, expected 'end' on line " + in.getLineNumber()); // EOF
			line = line.trim();
			if (line.equals("") || line.startsWith("#")) { // blank line or comment
				// ignore
			} else if (line.equals("end")) {
				if (button == null) throw new IOException("Error in input, no 'button' in block ending on line " + in.getLineNumber());
				if (addIt && (prog == null || prog.equals(program))) {
					add(new Entry(button, remote, repeat, (String[])values.toArray(new String[values.size()])));
				}
				return true;
			} else if (line.startsWith("button")) {
				button = readValue(line, in);
			} else if (line.startsWith("remote")) {
				remote = readValue(line, in);
			} else if (line.startsWith("repeat")) {
				String val = readValue(line, in);
				try {
					repeat = Integer.parseInt(val);
				} catch (NumberFormatException ex) {
					throw new IOException("Error in input '"+line+"' on line " + in.getLineNumber());
				}
			} else if (line.startsWith("config")) {
				values.add(readValue(line, in));
			} else if (line.startsWith("prog")) {
				prog = readValue(line, in);
			} else if (line.startsWith("mode")) {
				// ignore
			} else if (line.startsWith("flags")) {
				// ignore
			} else {
				//throw new IOException("Error in input '"+line+"' on line " + in.getLineNumber());
				System.err.println("Unexpected input '"+line+"' on line " + in.getLineNumber());
			}
		}
	}

	private String readValue(String line, LineNumberReader in) throws IOException {
		int pos = line.indexOf('=');
		if (pos == -1) throw new IOException("Error in input '"+line+"' on line " + in.getLineNumber());
		return line.substring(pos+1).trim();
	}

	/** Writes this LIRCMap to a file. */
	public void store(String filename) throws IOException {
		store(new File(filename));
	}

	/** Writes this LIRCMap to a file. */
	public void store(File file) throws IOException {
		FileWriter out = new FileWriter(file);
		store(out);
		out.close();
	}

	/** Writes this LIRCMap to a Writer. */
	public void store(Writer writer) throws IOException {
		PrintWriter out = new PrintWriter(writer);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Entry e = (Entry)it.next();
			out.println("begin");
			out.println("\tbutton = " + e.button);
			if (e.remote != null) {
				out.println("\tremote = " + e.remote);
			}
			if (e.repeat != DEFAULT_REPEAT) {
				out.println("\trepeat = " + e.repeat);
			}
			for (int i = 0; i < e.values.length; i++) {
				out.println("\tconfig = " + e.values[i]);
			}
			out.println("end");
		}
		out.flush();
	}

	/**
	* Gets the String that matches a LIRCEvent.
	* @param e The event
	* @return A String or null if there is no match
	*/
	public String get(LIRCEvent e) {
		int n = list.size();
		for (int i = 0; i < n; i++) {
			Entry entry = (Entry)list.get(i);
			if (entry.match(e)) {
				return entry.nextValue();
			}
		}
		return null;
	}

	/** Adds a value mapping to this map.
	* @param button The button to match
	* @param remote The remote value
	* @param repeat The repeat value
	* @param value The value to map to the parameters
	*/
	public void add(String button, String remote, int repeat, String value) {
		add(new Entry(button, remote, repeat, new String[]{ value }));
	}

	/** Adds a value mapping to this map.
	* @param button The button to match
	* @param remote The remote value
	* @param repeat The repeat value
	* @param values The values to map to the parameters
	*/
	public void add(String button, String remote, int repeat, String[] values) {
		add(new Entry(button, remote, repeat, values));
	}

	/**
	* Adds an Entry.
	* @param e The Entry to add
	*/
	protected void add(Entry e) {
		list.add(e);
	}

	/** The entries in this map. */
	protected static class Entry {
		protected final String button;
		protected final String remote;
		protected final int repeat;
		protected final String[] values;
		private int counter = -1;

		/**
		* Creates a new Entry
		* @param button The button that this entry will match, if null all buttons will be matched
		* @param remote The remote that this entry will match, if null all remotes will be matched
		* @param repeat The repeat value
		*/
		public Entry(String button, String remote, int repeat, String[] values) {
			this.button = button;
			this.remote = remote;
			this.repeat = repeat;
			this.values = values;
		}

		/**
		* Checks if this Entry matches a given button - remote combination and
		* repeat value.
		*/
		public boolean match(LIRCEvent e) {
			return (remote == null || remote.equals(e.getRemote()))
				&& (button == null || button.equals(e.getName()))
				&& ((repeat == 0) ? (e.getRepeat() == 0) : (e.getRepeat() % repeat == 0));
		}

		/** Returns the next value of this entry. */
		public String nextValue() {
			if (values.length == 0) {
				return null;
			} else {
				counter++;
				if (counter >= values.length) counter = 0;
				return values[counter];
			}
		}
	}

	/**
	* For testing. Loads a config file, dumps the setup to stdout, connects to
	* the LIRC daemon and prints the commands matched by the events received.
	*/
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
		  	System.out.println("Usage java LIRCMap <config file> [<program>]");
			System.exit(1);
		}
		String prog = (args.length >= 2) ? args[1] : null;
		final LIRCMap map = new LIRCMap(prog, new File(args[0]));
		map.store(new OutputStreamWriter(System.out));

		new LIRCClient().addLIRCListener(new org.lirc.LIRCListener() {
			public void received(LIRCEvent e) {
				String com = map.get(e);
				if (com != null){
					System.out.println(e + " => " + com);
				}
			}
		});

		try {
			while (true) { Thread.sleep(200); }
		} catch (InterruptedException ex) {}
	}

}
