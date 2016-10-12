package game.ui;

import game.server.Command;
import game.server.GameConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class OpenRoomsFrame {

	private GameConnection mConnection;
	private DefaultListModel <RoomInfo> listModel = new DefaultListModel ();
	private GameFrame gameFrame;

	public OpenRoomsFrame () {
		mConnection = new GameConnection (this, "localhost", "petike");

		String title = "Open Rooms";

		JFrame f = new JFrame (title);
		f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JList list = new JList (listModel);
		ListCellRenderer renderer = new RoomInfoCellRenderer ();
		list.setCellRenderer (renderer);
		list.addMouseListener (new MouseAdapter () {
			public void mouseClicked (MouseEvent evt) {
				JList list = (JList) evt.getSource ();
				if (evt.getClickCount () == 2) {
					int index = list.locationToIndex (evt.getPoint ());
					mConnection.send (Command.Type.ENTER_ROOM, listModel.getElementAt (index).mRoomId);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane (list);

		Container contentPane = f.getContentPane ();
		contentPane.add (scrollPane, BorderLayout.CENTER);

		f.setSize (640, 480);
		f.setVisible (true);
	}

	public static void main (String args[]) {
		OpenRoomsFrame openRoomsFrame = new OpenRoomsFrame ();
	}

	public void loadRoomInfos (Serializable[] data) {
		listModel.clear ();
		int i = 0;
		while (i < data.length) {
			int id = (int) data[i++];
			String game = (String) data[i++];
			String map = (String) data[i++];
			int user = (int) data[i++];
			int maxuser = (int) data[i++];
			listModel.addElement (new RoomInfo (id, game, map, user, maxuser));
		}
	}

	public void startGame () {
		gameFrame = new GameFrame ("valami", 640, 480, mConnection);
		gameFrame.start ();
	}

	@Override
	protected void finalize () throws Throwable {
		mConnection.close ();
	}
}
