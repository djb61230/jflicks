/**************************************************************************/
/*
/* Irexec.java -- Part of the org.lirc.test package
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

package org.lirc.test;

import org.lirc.*;
import org.lirc.util.*;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/** Executes commands from remote contol button presses.
	Similar in function to the <code>irexec</code> program.

	@version $Revision: 1.1 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class Irexec {

	private SimpleLIRCClient client;

	public Irexec(File configFile) throws LIRCException, IOException, FileNotFoundException {
		client = new SimpleLIRCClient("irexec", configFile);
		client.addIRActionListener(new RemoteListener());
	}

	/** Exits this program. */
	public void quit(){
		client.stopListening();
		System.exit(0);
	}

	private class RemoteListener implements IRActionListener{
		public void action(String command){
			try {
				System.out.println("Executing: '" + command + "'");
				Runtime.getRuntime().exec(command);
			} catch (IOException ex) {
				System.err.println(ex.toString());
			}
		}
	}

	public static void main(String[] args) {
		try {
			File config = (args.length > 0) ? new File(args[0]) : new File(System.getProperty("user.home"), ".lircrc");
			Irexec p = new Irexec(config);
			while(true) { Thread.currentThread().sleep(50); } // loop until Ctrl-C
		} catch (LIRCException ex) {
			System.err.println(ex.toString());
		} catch (IOException ex) {
			System.err.println(ex.toString());
		} catch (InterruptedException ex) {
			System.err.println(ex.toString());
		}
	}

}