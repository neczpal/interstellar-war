package game.ui;

import game.connection.ClientConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class RoomsFrame extends JFrame {
	private JTable roomsTable;
	private JLabel roomPicture;
	private JList roomUserList;
	private JButton joinOrLeaveButton;
	private JButton startButton;

	private ClientConnection mConnection;

	private int mSelectedRoomId = -1;
	private boolean isInRoom = false;

	public RoomsFrame (ClientConnection clientConnection) {
		super ("Rooms");

		mConnection = clientConnection;

		setLayout (new FlowLayout ());
		setLocationByPlatform (true);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		setSize (700, 500);
		addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent e) {
				mConnection.stopClientConnection ();
			}
		});
		JPanel leftPanel = new JPanel ();

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

		roomsTable = new JTable (tableModel, tableColumnModel) {
			public boolean isCellEditable (int row, int column) {
				return false;
			}
		};
		roomsTable.getTableHeader ().setReorderingAllowed (false);
		roomsTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
		roomsTable.getSelectionModel ().addListSelectionListener (new ListSelectionListener () {
			@Override
			public void valueChanged (ListSelectionEvent e) {//#TODO not always refreshes the last selection
				TableModel model = roomsTable.getModel ();
				int rowIndex = e.getFirstIndex ();

				if (rowIndex < model.getRowCount ()) {
					mSelectedRoomId = (int) model.getValueAt (rowIndex, 0);//#TODO ArrayIndexOutOfBoundsException
				}
			}
		});
		roomsTable.setDefaultRenderer (Object.class, new RoomTableCellRenderer ());

		leftPanel.add (new JScrollPane (roomsTable));

		JPanel rightPanel = new JPanel ();
		rightPanel.setLayout (new GridLayout (3, 1));

		ImageIcon image = new ImageIcon ("res/rockpaperscissors/rock.png");
		roomPicture = new JLabel ("", image, JLabel.CENTER);
		roomUserList = new JList (new String[] {"era", "bsfsf", "casd", "asfaoiefjeaoi"});
		roomUserList.setEnabled (false);

		JPanel roomButtonsPanel = new JPanel ();
		roomButtonsPanel.setLayout (new FlowLayout ());

		joinOrLeaveButton = new JButton ("Join");
		joinOrLeaveButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (isInRoom) {
					mConnection.leaveRoom ();
				} else {
					mConnection.enterRoom (mSelectedRoomId);
				}
			}
		});
		startButton = new JButton ("Start");
		startButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				//				mConnection.start () #TODO
			}
		});

		roomButtonsPanel.add (joinOrLeaveButton);
		roomButtonsPanel.add (startButton);

		rightPanel.add (roomPicture);
		rightPanel.add (new JScrollPane (roomUserList));
		rightPanel.add (roomButtonsPanel);

		add (leftPanel);
		add (rightPanel);

		setVisible (true);
	}

	public void loadRoomInfos (Serializable[] data) {
		DefaultTableModel model = (DefaultTableModel) roomsTable.getModel ();
		model.setColumnCount (4);
		model.setRowCount (0);
		int i = 0;
		int selectedRowIndex = 0;

		while (i < data.length) {
			int id = (int) data[i++];
			String game = (String) data[i++];
			String map = (String) data[i++];
			int user = (int) data[i++];
			int maxuser = (int) data[i++];
			model.addRow (new Object[] {id, game, map, String.valueOf (user) + "/" + String.valueOf (maxuser)});
			if (id == mSelectedRoomId) {
				selectedRowIndex = model.getRowCount () - 1;
			}
		}
		model.fireTableDataChanged ();
		roomsTable.setRowSelectionInterval (selectedRowIndex, selectedRowIndex);
	}

	public void setIsInRoom (boolean isInRoom) {
		this.isInRoom = isInRoom;
		if (isInRoom) {
			joinOrLeaveButton.setText ("Leave");
		} else {
			joinOrLeaveButton.setText ("Join");
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
