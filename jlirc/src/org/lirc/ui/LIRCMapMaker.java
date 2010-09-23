/**************************************************************************/
/*
/* LIRCMapMaker.java -- Part of the org.lirc.ui package
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

package org.lirc.ui;

import org.lirc.*;
import org.lirc.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.OutputStreamWriter;
import java.io.IOException;

/** UI widget for making LIRCMaps.

	@see org.lirc.util.LIRCMap

	@version $Revision: 1.7 $
	@author Bjorn Bringert (bjorn@mumblebee.com)
*/
public class LIRCMapMaker extends JDialog {

	private LIRCMapSetup setter;
	private LIRCListener listener;
	private LIRCClient client;

	private JLabel out;
	private JLabel in;

	private String lastRemote;
	private String lastButton;

	public LIRCMapMaker(Set values, LIRCClient client) {
		this.client = client;
		setter = new LIRCMapSetup(values);

		setModal(true);
		setTitle("Remote button mappings");
		setContentPane(createPanel());

		LIRCListener listener = new ButtonListener();
		client.addLIRCListener(listener);

		nextValue();
		show();
	}

	private JPanel createPanel() {
		JPanel pane = new JPanel(new GridLayout(3,1));
		out = new JLabel("");
		in = new JLabel("");
		in.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel butts = new JPanel();
		butts.setLayout(new GridLayout(1,2));
		JButton ok = new JButton("OK");
		ok.addActionListener(new OKButtonListener());
		getRootPane().setDefaultButton(ok);
		JButton cancel = new JButton("Cancel");

		butts.add(ok);
		butts.add(cancel);

		pane.add(out);
		pane.add(in);
		pane.add(butts);

		return pane;
	}

	private void nextValue() {
		out.setText("Press the button for \"" + setter.nextName() + "\"");
		in.setText("");
		lastButton = null;
		lastRemote = null;
		pack();
	}

	public LIRCMap getMap() {
		return setter.getMap();
	}

	private void done() {
		client.removeLIRCListener(listener);
		dispose();
	}

	private class ButtonListener implements LIRCListener {
		public void received(LIRCEvent e) {
			lastButton = e.getName();
			lastRemote = e.getRemote();
			in.setText(lastRemote + " / " + lastButton);
		}
	}

	private class OKButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (lastRemote != null && lastButton !=null){
				setter.setCurrent(lastButton, lastRemote);
				if (setter.hasNext()) {
					nextValue();
				} else {
					done();
				}
			}
		}
	}

	/** For testing  */
	public static void main(String[] args) throws LIRCException, IOException {
		Set set = new HashSet();
		set.add("up");
		set.add("down");
		set.add("left");
		set.add("quit");

		LIRCMapMaker maker = new LIRCMapMaker(set, new LIRCClient());
		maker.getMap().store(new OutputStreamWriter(System.out));
	}
}