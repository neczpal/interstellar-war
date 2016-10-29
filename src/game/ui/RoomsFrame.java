package game.ui;

import game.connection.RoomData;
import game.connection.UserConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomsFrame extends JFrame {
	private JTable mRoomsTable;
	private JLabel mRoomPicture;//#TODO
	private JList mRoomUserList;
	private JButton mJoinOrLeaveButton;
	private JButton mStartButton;

	private UserConnection mConnection;

	private int mSelectedRoomId = -1;
	private boolean mIsInRoom = false;

	private HashMap <Integer, ArrayList <String>> mAllRoomUsers = new HashMap <> ();

	public RoomsFrame (UserConnection userConnection) {
		super ("Rooms");

		mConnection = userConnection;

		setLayout (new BorderLayout ());
		setLocationByPlatform (true);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		setSize (720, 500);
		setResizable (false);
		addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent e) {
				mConnection.stopUserConnection ();
			}
		});

		JPanel leftPanel = new JPanel ();
		leftPanel.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 10));

		TableModel tableModel = new DefaultTableModel (0, 4);

		TableColumnModel tableColumnModel = new DefaultTableColumnModel ();
		TableColumn first = new TableColumn (1);
		first.setHeaderValue ("Game");
		tableColumnModel.addColumn (first);
		TableColumn second = new TableColumn (2);
		second.setHeaderValue ("Map");
		tableColumnModel.addColumn (second);
		TableColumn third = new TableColumn (3);
		third.setHeaderValue ("Users");
		tableColumnModel.addColumn (third);

		mRoomsTable = new JTable (tableModel, tableColumnModel) {
			public boolean isCellEditable (int row, int column) {
				return false;
			}
		};
		mRoomsTable.getTableHeader ().setReorderingAllowed (false);
		mRoomsTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
		mRoomsTable.getSelectionModel ().addListSelectionListener (e -> {
			TableModel model = mRoomsTable.getModel ();
			int selectedRow = mRoomsTable.getSelectedRow ();
			if (selectedRow != -1 && selectedRow < model.getRowCount ()) {
				try {
					mSelectedRoomId = (int) model.getValueAt (selectedRow, 0);
				} catch (ArrayIndexOutOfBoundsException ex) {//#TODO ArrayINdexOutofBoundsException
					System.out.println (ex.getMessage ());
				}
				DefaultListModel listModel = (DefaultListModel) mRoomUserList.getModel ();
				listModel.clear ();
				ArrayList <String> users = mAllRoomUsers.get (mSelectedRoomId);
				if (users != null && !users.isEmpty ()) {
					for (String userName : mAllRoomUsers.get (mSelectedRoomId)) {
						listModel.addElement (userName);
					}
				}
			}
		});
		mRoomsTable.setDefaultRenderer (Object.class, new RoomTableCellRenderer ());

		leftPanel.add (new JScrollPane (mRoomsTable));

		JPanel rightPanel = new JPanel ();
		rightPanel.setBorder (BorderFactory.createEmptyBorder (20, 10, 20, 20));
		rightPanel.setLayout (new GridLayout (3, 1));

		//		ImageIcon image = new ImageIcon ("res/interstellarwar/planet5.png");// #TODO image for every map
		//		mRoomPicture = new JLabel ("", image, JLabel.CENTER);

		DefaultListModel <String> listModel = new DefaultListModel ();

		mRoomUserList = new JList <> (listModel);
		mRoomUserList.setEnabled (false);
		mRoomUserList.setFixedCellWidth (190);

		JPanel roomButtonsPanel = new JPanel ();
		roomButtonsPanel.setLayout (new FlowLayout ());

		mJoinOrLeaveButton = new JButton ("Join");
		mJoinOrLeaveButton.addActionListener (e -> {
			if (mIsInRoom) {
				mConnection.leaveRoom ();
			} else {
				mConnection.enterRoom (mSelectedRoomId);
			}
		});

		mStartButton = new JButton ("Start");
		mStartButton.addActionListener (e -> mConnection.startRoom ());
		mStartButton.setEnabled (false);

		roomButtonsPanel.add (mJoinOrLeaveButton);
		roomButtonsPanel.add (mStartButton);

		//		rightPanel.add (mRoomPicture);
		rightPanel.add (new JScrollPane (mRoomUserList));
		rightPanel.add (roomButtonsPanel);

		add (leftPanel, BorderLayout.LINE_START);
		add (rightPanel, BorderLayout.LINE_END);
	}

	public void loadRoomData (RoomData[] data) {
		mAllRoomUsers.clear ();
		DefaultTableModel model = (DefaultTableModel) mRoomsTable.getModel ();
		model.setColumnCount (4);
		model.setRowCount (0);
		int selectedRowIndex = 0;

		for (int i = 0; i < data.length; i++) {
			model.addRow (new Object[] {data[i].getRoomId (), data[i].getGameName (), data[i].getMapName (), String.valueOf (data[i].getUsers ().size ()) + "/" + String.valueOf (data[i].getMaxUserCount ())});
			mAllRoomUsers.put (data[i].getRoomId (), data[i].getUsers ());
			if (data[i].getRoomId () == mSelectedRoomId) {
				selectedRowIndex = i;
				if (data[i].getUsers ().size () == data[i].getMaxUserCount ()) {
					mStartButton.setEnabled (true);
				} else {
					mStartButton.setEnabled (false);
				}
			}
		}
		model.fireTableDataChanged ();

		mRoomsTable.setRowSelectionInterval (selectedRowIndex, selectedRowIndex);
	}

	public void setIsInRoom (boolean isInRoom) {
		this.mIsInRoom = isInRoom;
		if (isInRoom) {
			mRoomsTable.setEnabled (false);
			//			mStartButton.setEnabled (true);
			mJoinOrLeaveButton.setText ("Leave");
		} else {
			mRoomsTable.setEnabled (true);
			//			mStartButton.setEnabled (false);
			mJoinOrLeaveButton.setText ("Join");
		}
	}


	private static class RoomTableCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
			setBorder (noFocusBorder);
			return this;
		}
	}
}
