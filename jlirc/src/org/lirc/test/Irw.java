/**************************************************************************/
/*
/* Irw.java -- Part of the org.lirc.test package
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

/** Listens for IR signals and prints them to System.out.
	Similar in function to the <code>irw</code> program
	in the LIRC package.

	@version $Revision: 1.3 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class Irw {

	/** Prints a few lines of info.	*/
	public static void printInstructions() {
	 	System.out.println("The program will listen for IR signals on the lircd socket and print them.");
	 	System.out.println("The lircd daemon must be running for the program to work.");
	}

	/** Runs Irw, use <code>-ins</code> to display a few lines of
		info when starting the program.
	*/
	public static void main(String[] args) {
		// check for -ins option
		if (args.length > 0 && args[0].indexOf("ins") > -1) {
		 	printInstructions();
		}

		try {
			Receiver rec = ReceiverFactory.createReceiver();
			try {
				while (true) {
					System.out.println(rec.readCode());
				}
			} finally {
				rec.close();
			}
		} catch (LIRCException ex) {
			System.err.println(ex.toString());
			System.exit(1);
		}
	}

}