package net.neczpal.interstellarwar.desktop.ui.models;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MessageListModel extends AbstractListModel <String> {
	private List <String> mMessages = new ArrayList <> ();

	@Override
	public int getSize () {
		return mMessages.size ();
	}

	@Override
	public String getElementAt (int index) {
		return mMessages.get (index);
	}

	public void addMessage(String message) {
		mMessages.add(0, message);
		fireIntervalAdded (this, 0, mMessages.size () - 1);
	}
}
