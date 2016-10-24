package game.ui;

import game.server.GameConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class OpenRoomsFrame extends JFrame {

	private GameConnection mConnection;
	private DefaultListModel <RoomInfo> listModel = new DefaultListModel ();
	private ArrayList <RoomInfo> mRoomInfoList = new ArrayList <> ();

	public OpenRoomsFrame () {
		super ("Open Rooms");
		mConnection = new GameConnection (this, "152.66.180.66", "petike");

		String title = "Open Rooms";

		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent e) {
				mConnection.stopGameConnection ();
			}
		});

		JList list = new JList (listModel);

		ListCellRenderer renderer = new RoomInfoCellRenderer ();
		list.setCellRenderer (renderer);
		list.addMouseListener (new MouseAdapter () {
			public void mouseClicked (MouseEvent evt) {
				JList list = (JList) evt.getSource ();
				if (evt.getClickCount () == 2) {
					int index = list.locationToIndex (evt.getPoint ());
					mConnection.enterRoom (listModel.getElementAt (index).mRoomId);//#TODO MI VAN HA ELTUNIK KOZBEN?
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane (list);

		Container contentPane = getContentPane ();
		contentPane.add (scrollPane, BorderLayout.CENTER);

		setLocationByPlatform (true);
		setSize (640, 480);
		setVisible (true);
	}

	public static void main (String args[]) {
		OpenRoomsFrame openRoomsFrame = new OpenRoomsFrame ();
	}

	public void loadRoomInfos (Serializable[] data) {
		ArrayList <RoomInfo> newRoomInfoList = new ArrayList <> ();
		int i = 0;
		int listIndex = 0;
		boolean changed = false;
		while (i < data.length) {
			int id = (int) data[i++];
			String game = (String) data[i++];
			String map = (String) data[i++];
			int user = (int) data[i++];
			int maxuser = (int) data[i++];
			if (listIndex < mRoomInfoList.size ()) {
				RoomInfo roomInfo = mRoomInfoList.get (listIndex);
				if ((id != roomInfo.mRoomId) || !game.equals (roomInfo.mGameName) || !map.equals (roomInfo.mMapName) || user != roomInfo.mUserCount || maxuser != roomInfo.mMaxUserCount) {
					changed = true;
				}
			} else {
				changed = true;
			}
			newRoomInfoList.add (new RoomInfo (id, game, map, user, maxuser));
			listIndex++;
		}
		if (changed) {
			mRoomInfoList = newRoomInfoList;
			listModel.clear ();
			for (RoomInfo roomInfo : newRoomInfoList) {
				listModel.addElement (roomInfo);
			}
		}
	}

}
