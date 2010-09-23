/**************************************************************************/
/*
/* EventListenerList.java -- Part of the org.lirc package
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

import java.lang.reflect.Array;
import java.util.EventListener;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
* Simliar to javax.swing.EventListener, but easier to use.
* Usage:
* <pre>
* EventListenerList listenerList = new EventListenerList();
* FooEvent fooEvent = null;
*
* public void addFooListener(FooListener l) {
*     listenerList.add(FooListener.class, l);
* }
*
* public void removeFooListener(FooListener l) {
*     listenerList.remove(FooListener.class, l);
* }
*
* protected void fireFooXXX() {
*     FooEvent e = null;
*     EventListener[] ls = listenerList.getListeners(FooListener.class);
*     for (int j = ls.length-1; j >= 0; j--) {
*        FooListener l = (FooListener)ls[j];
*        if (e == null) e = new FooEvent();
*        l.fireFooXXX(e);
*     }
* }
*
* @version $Revision: 1.1 $
* @author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class EventListenerList implements Serializable {

	/** Shared empty array for empty <tt>EventListenerList</tt>s. */
	private static final Object[][] EMPTY_LISTENER_LIST = new Object[0][2];

	/** Shared empty listener returned when there are no listeners for a type. */
	private static final EventListener[] NO_LISTENERS = new EventListener[0];

	/**
	* Internal storage for event listeners. The first column (listeners[x][0])
	* contains Class objects, the second contains arrays of EventListeners.
	*/
	private transient Object[][] listeners = EMPTY_LISTENER_LIST;

	/**
	* Creates a new empty EventListenerList.
	*/
	public EventListenerList() {

	}

	/**
	* Returns the total number of listeners for this listener list.
	* @return a non-negative integer
	*/
	public int getListenerCount() {
		Object[][] ls = listeners;
		int total = 0;
		for (int i = ls.length-1; i >= 0; i--) {
			total += ((EventListener[])ls[i][1]).length;
		}
		return total;
	}

	/**
	* Returns the total number of listeners of the supplied type
	* for this listener list.
	* @return a non-negative integer
	*/
	public int getListenerCount(Class t) {
		Object[][] ls = listeners;
		for (int i = ls.length-1; i >= 0; i--) {
			if (listeners[i][0] == t) {
				return ((EventListener[])ls[i][1]).length;
			}
		}
		return 0;
	}

	/**
	* Return an array of all the listeners of the given type.
	* @return An array containing all of the listeners of the specified type.
	*	The component type of the returned array only guaranteed to
	*	be <tt>EventListener</tt>
	* @throws ClassCastException if the supplied class
	*		is not assignable to EventListener
	*/
	public EventListener[] getListeners(Class t) {
		Object[][] ls = listeners;
		for (int i = ls.length-1; i >= 0; i--) {
			if (ls[i][0] == t) {
				return (EventListener[])ls[i][1];
			}
		}
		return NO_LISTENERS;
	}

	/**
	* Adds the listener as a listener of the specified type.
	* @param t the type of the listener to be added
	* @param l the listener to be added
	* @throws IllegalArgumentException if <tt>l</tt> is not an instance of <tt>t</tt>
	*/
	public synchronized void add(Class t, EventListener l) {
		if (l == null)
			return;
		if (!t.isInstance(l))
			throw new IllegalArgumentException(l + " is not an instance of " + t);

		// if there already is an array for this class, copy that and add this listener
		for (int i = listeners.length-1; i >= 0; i--) {
			if (listeners[i][0] == t) {
				EventListener[] oldArray = (EventListener[])listeners[i][1];
				int ol = oldArray.length;
				EventListener[] newArray = (EventListener[])Array.newInstance(t, ol+1);
				System.arraycopy(oldArray, 0, newArray, 0, ol);
				newArray[ol] = l;
				listeners[i][1] = newArray;
				return;
			}
		}

		// there are no listeners of this type, add new array
		int ll = listeners.length;
		Object[][] newListeners = new Object[ll+1][0];
		System.arraycopy(listeners, 0, newListeners, 0, ll);
		EventListener[] newArray = (EventListener[])Array.newInstance(t, 1);
		newArray[0] = l;
		newListeners[ll] = new Object[]{ t, newArray };
		listeners = newListeners;
	}

	/**
	* Removes the listener as a listener of the specified type.
	* @param t the type of the listener to be removed
	* @param l the listener to be removed
	* @throws IllegalArgumentException if <tt>l</tt> is not an instance of <tt>t</tt>
	*/
	public synchronized void remove(Class t, EventListener l) {
		if (l == null)
			return;
		if (!t.isInstance(l))
			throw new IllegalArgumentException(l + " is not an instance of " + t);

		// find array of listeners of this type
		int ll = listeners.length;
		for (int i = listeners.length-1; i >= 0; i--) {
			if (listeners[i][0] == t) {
				// find index of this listener
				EventListener[] oldArray = (EventListener[])listeners[i][1];
				int ol = oldArray.length;
				for (int j = ol-1; j >= 0; j--) {
					if (oldArray[j] == l) {
						if (ol == 1) { // check if this is the only listener of this type
							if (ll == 1) { // check if this is the only type of listener
								listeners = EMPTY_LISTENER_LIST;
							} else {
								Object[][] newListeners = new Object[ll-1][2];
								System.arraycopy(listeners, 0, newListeners, 0, i);
								int cl = i+1;
								System.arraycopy(listeners, cl, newListeners, i, ll-cl);
								listeners = newListeners;
							}
						} else {
							// create new array
							EventListener[] newArray = (EventListener[])Array.newInstance(t, ol-1);
							// copy all but this listener to the new array
							System.arraycopy(oldArray, 0, newArray, 0, j);
							int cl = j+1;
							System.arraycopy(oldArray, cl, newArray, j, ol-cl);
							listeners[i][1] = newArray;
						}
					}
				}
				return;
			}
		}
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		Object[][] ls = listeners;
		s.defaultWriteObject();

		for (int i = 0; i < ls.length; i++) {
			Class t = (Class)ls[i][0];
			EventListener[] as = (EventListener[])ls[i][1];
			for (int j = 0; j < as.length; j++) {
				EventListener l = (EventListener)as[j];
				if (l instanceof Serializable) {
					s.writeObject(t.getName());
					s.writeObject(l);
				}
			}
		}
		s.writeObject(null);
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		listeners = EMPTY_LISTENER_LIST;
		s.defaultReadObject();
		Object listenerTypeOrNull;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		while ((listenerTypeOrNull = s.readObject()) != null) {
			Class t = Class.forName((String)listenerTypeOrNull, true, cl);
			EventListener l = (EventListener)s.readObject();
			add(t, l);
		}
	}

	/**
	* Returns a string representation of the EventListenerList.
	*/
	public String toString() {
		Object[][] ls = listeners;
		StringBuffer sb = new StringBuffer(" listeners: ");
		int total = 0;
		for (int i = 0; i < ls.length; i++) {
			EventListener[] as = (EventListener[])ls[i][1];
			for (int j = 0; j < as.length; j++) {
				total++;
				sb.append(((Class)ls[i][0]).getName()).append(": ");
				sb.append(as[j]).append(", ");
			}
		}
		return "EventListenerList: " + total + sb.delete(sb.length()-2,sb.length()).toString();
	}

}