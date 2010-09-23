/**************************************************************************/
/*
/* IRActionListener.java -- Part of the org.lirc.util package
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

import java.util.EventListener;

/** 	Listens for application specific (and remote control independent) strings. In
	 a SimpleLIRCClient.

	 @version $Revision: 1.2 $
	 @author Bjorn Bringert (bjorn@mumblebee.com)
*/
public interface IRActionListener extends EventListener {

	/** Called when an IR event has neen received that is mapped to a string. */
	public void action(String command);

}