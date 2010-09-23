/**************************************************************************/
/*
/* LIRCClient.java -- Part of the org.lirc package
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

import java.util.EventListener;

/** Reads IR signals and fires LIRCEvents. A LIRCClient runs as
	a daemon thread and invokes all LIRCListeners that have been
	registered with it.

	Usage:

	<pre>

		LIRCClient c = new LIRClient();
		LIRCListener l = new IRListener();
		c.addLIRCListener(l);

		...

		c.stopListening();

		...

		private class IRListener implements LIRCListener{
			public void IRReceived(LIRCEvent ir){
		// do something with the LIRCEvent
			}
		}

	</pre>

	@version $Revision: 1.6 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class LIRCClient {

	/** The receiver used to get the IRSignals. */
	private Receiver rec;

	/** List of event listeners. */
	private EventListenerList listeners = new EventListenerList();

	/** Whether the thread should keep running. */
	private boolean keepRunning = true;

	/** Connects to the lircd socket and starts listening for signals.  */
	public LIRCClient() throws LIRCException {
		rec = ReceiverFactory.createReceiver();
		Thread readThread = new ReadThread();
		readThread.setDaemon(true);
		readThread.start();
	}

	/** Stop listening for signals. You can't restart it, create a new instead. */
	public void stopListening() {
		keepRunning = false;
	}

	/** Adds an LIRCListener */
	public void addLIRCListener(LIRCListener l) {
		listeners.add(LIRCListener.class, l);
	}

	/** Removes an LIRCListener */
	public void removeLIRCListener(LIRCListener l) {
		listeners.remove(LIRCListener.class, l);
	}

	/** Notify all listeners that have registered interest for
		notification on this event type.

		@param code a String like the ones returned by readCode()
	*/
	protected void fireIRReceived(String code) {
		String[] strs = StringUtils.split(code);
		int repeat = Integer.parseInt(strs[1], 16);
		String name = strs[2];
		String remote = StringUtils.chomp(strs[3]);
		fireIRReceived(repeat, name, remote);
	}

	/** Notify all listeners that have registered interest for
		notification on this event type.
	*/
	protected void fireIRReceived(int repeat, String name, String remote) {
		LIRCEvent e = null;
		EventListener[] ls = listeners.getListeners(LIRCListener.class);
		for (int j = ls.length-1; j >= 0; j--) {
			if (e == null) e = new LIRCEvent(this, repeat, name, remote);
			((LIRCListener)ls[j]).received(e);
		}
	}

	/** Listens for signals and fires LIRCEvents. */
	private class ReadThread extends Thread {
		public void run() {
			try {
				while (keepRunning) {
					String code = rec.readCode();
					if (keepRunning) { // don't fire an event if told to stop listening while reading
						fireIRReceived(code);
					}
				}
			} catch (LIRCException ex) {
				System.err.println(ex.toString());
			} finally {
				rec.close();
			}
		}
	}

}