package net.neczpal.interstellarwar.desktop.ui;

import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.desktop.ui.frames.GameFrame;
import net.neczpal.interstellarwar.desktop.ui.frames.LoginFrame;
import net.neczpal.interstellarwar.desktop.ui.frames.RoomsFrame;

import javax.swing.*;
import java.util.ArrayList;

public class DesktopUI implements UserInterface {
	private LoginFrame mLoginFrame;
	private RoomsFrame mRoomsFrame;
	private GameFrame mGameFrame;
	private ClientConnection mConnection;

	public DesktopUI () {
		openLogin ();
	}

	public static void main (String[] args) {
		new DesktopUI ();
	}

	private void openLogin () {
		mLoginFrame = new LoginFrame (this);
		mLoginFrame.setVisible (true);
	}

	@Override
	public void connectionReady () {
		mLoginFrame.dispose ();
		mRoomsFrame = new RoomsFrame (mConnection);
		mRoomsFrame.setVisible (true);
	}

	@Override
	public void connectionDropped () {
		stopGame ();

		mRoomsFrame.dispose ();
		openLogin ();
		JOptionPane.showMessageDialog (mLoginFrame, "Disconnected from the server!", "Connection lost!", JOptionPane.ERROR_MESSAGE);

	}

	@Override
	public void listRooms (ArrayList <RoomData> roomData) {
		mRoomsFrame.loadRoomData (roomData);
	}

	@Override
	public void setIsInRoom (boolean isInRoom) {
		mRoomsFrame.setIsInRoom (isInRoom);
	}

	@Override
	public void startGame (String mapName) {
		mGameFrame = new GameFrame ("Interstellar War : " + mapName, mLoginFrame.getSelectedDisplayModeIndex (), mConnection.getGameClient ());
		mGameFrame.start ();
	}

	@Override
	public void stopGame () {
		if (mGameFrame != null) {
			mGameFrame.stopGameFrame ();
			mGameFrame = null;
		}
	}

	public void setConnection (ClientConnection connection) {
		mConnection = connection;
		mConnection.setUserInterface (this);
	}
}