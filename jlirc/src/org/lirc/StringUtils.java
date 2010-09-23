/**************************************************************************/
/*
/* StringUtils.java -- Part of the org.lirc package
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

/** Contains some string utilities.

	@version $Revision: 1.2 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class StringUtils{

	/** Removes last character if it is a newline. Like Perl's <code>chomp</code>. */
	public static String chomp(String str) {
		if (str.endsWith("\n")) {
			return str.substring(0, str.length() - 1);
   		}else{
		 	return str;
		}
	}

	/** Splits the string at each space. Like Perl <code>split " "</code>
		or Haskell <code>words</code>.

		@return an array of the words without spaces
	*/
	public static String[] split(String str) {
		return split(' ', str);
	}

	/** Splits the string at <code>at</code>. Like Perl split.
		@param at the character to split at.
	*/
	public static String[] split(char at, String str) {
		// count 'ats'
		int s = 0;
		for (int i = 0; i < str.length(); i++) {
		 	if (str.charAt(i) == at) s++;
		}
		// split string
		String[] ret = new String[s + 1];
		int lastSpace = 0;
		for (int i = 0; i < s; i++) {
			int nextSpace = str.indexOf(at, lastSpace + 1);
			ret[i] = str.substring(lastSpace + 1, nextSpace);
			lastSpace = nextSpace;
		}
		ret[s] = str.substring(lastSpace + 1);
		return ret;
	}

}