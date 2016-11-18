package net.neczpal.interstellarwar.desktop.ui.models;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UserListModel extends AbstractListModel <String> {
	private List <String> mUserNames = new ArrayList <> ();

	@Override
	public int getSize () {
		return mUserNames.size ();
	}

	@Override
	public String getElementAt (int index) {
		return mUserNames.get (index);
	}

	/**
	 * Stellt die Benutzernamen ein
	 *
	 * @param userNames Die Liste der Benutzernamen
	 */
	public void setUserNames (List <String> userNames) {
		mUserNames = userNames;
		fireContentsChanged (this, 0, mUserNames.size () - 1);
	}
}
