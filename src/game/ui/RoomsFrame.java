package game.ui;

import game.connection.ClientConnection;
import game.connection.RoomData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class RoomsFrame extends JFrame {
	private JTable mRoomsTable;
	private JLabel mRoomPicture;
	private JList mRoomUserList;
	private JButton mJoinOrLeaveButton;
	private JButton mStartButton;

	private ClientConnection mConnection;

	private int mSelectedRoomId = -1;
	private boolean mIsInRoom = false;

	private HashMap <Integer, ArrayList <String>> mAllRoomUsers = new HashMap <> ();

	public RoomsFrame (ClientConnection clientConnection) {
		super ("Rooms");

		mConnection = clientConnection;

		setLayout (new BorderLayout ());
		setLocationByPlatform (true);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		setSize (720, 500);
		setResizable (false);
		addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent e) {
				mConnection.stopClientConnection ();
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
		mRoomsTable.getSelectionModel ().addListSelectionListener (new ListSelectionListener () {
			@Override
			public void valueChanged (ListSelectionEvent e) {//#TODO not always refreshes the last selection
				TableModel model = mRoomsTable.getModel ();
				int selectedRow = mRoomsTable.getSelectedRow ();
				if (selectedRow != -1) {
					mSelectedRoomId = (int) model.getValueAt (selectedRow, 0);//#TODO ArrayIndexOutOfBoundsException

					DefaultListModel listModel = (DefaultListModel) mRoomUserList.getModel ();
					listModel.clear ();
					ArrayList <String> users = mAllRoomUsers.get (mSelectedRoomId);
					if (users != null && !users.isEmpty ()) {
						for (String userName : mAllRoomUsers.get (mSelectedRoomId)) {
							listModel.addElement (userName);
						}
					}
				}
			}
		});
		mRoomsTable.setDefaultRenderer (Object.class, new RoomTableCellRenderer ());

		leftPanel.add (new JScrollPane (mRoomsTable));

		JPanel rightPanel = new JPanel ();
		rightPanel.setBorder (BorderFactory.createEmptyBorder (20, 10, 20, 20));
		rightPanel.setLayout (new GridLayout (3, 1));

		ImageIcon image = new ImageIcon ("res/rockpaperscissors/rock.png");
		mRoomPicture = new JLabel ("", image, JLabel.CENTER);

		DefaultListModel <String> listModel = new DefaultListModel ();

		mRoomUserList = new JList <> (listModel);
		mRoomUserList.setEnabled (false);
		mRoomUserList.setFixedCellWidth (190);

		JPanel roomButtonsPanel = new JPanel ();
		roomButtonsPanel.setLayout (new FlowLayout ());

		mJoinOrLeaveButton = new JButton ("Join");
		mJoinOrLeaveButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (mIsInRoom) {
					mConnection.leaveRoom ();
					setIsInRoom (false);
					setVisible (true);//#TODO somehting room vs. game
				} else {
					mConnection.enterRoom (mSelectedRoomId);
				}
			}
		});

		mStartButton = new JButton ("Start");
		mStartButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				//				mConnection.start () #TODO
			}
		});

		roomButtonsPanel.add (mJoinOrLeaveButton);
		roomButtonsPanel.add (mStartButton);

		rightPanel.add (mRoomPicture);
		rightPanel.add (new JScrollPane (mRoomUserList));
		rightPanel.add (roomButtonsPanel);

		add (leftPanel, BorderLayout.LINE_START);
		add (rightPanel, BorderLayout.LINE_END);

		setVisible (true);
	}

	public void loadRoomInfos (RoomData[] data) {
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
			}
		}
		model.fireTableDataChanged ();

		mRoomsTable.setRowSelectionInterval (selectedRowIndex, selectedRowIndex);
		//		mSelectedRoomId = (int) mRoomsTable.getModel ().getValueAt (selectedRowIndex, 0);
	}

	public void setIsInRoom (boolean isInRoom) {
		this.mIsInRoom = isInRoom;
		if (isInRoom) {
			mJoinOrLeaveButton.setText ("Leave");
		} else {
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
