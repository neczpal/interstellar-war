package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.desktop.Loader;
import net.neczpal.interstellarwar.desktop.ui.models.RoomsDataTableModel;
import net.neczpal.interstellarwar.desktop.ui.models.UserListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LobbyFrame extends JFrame {
	private JTable mRoomsTable;
	private JList mRoomUserList;
	private JButton mJoinOrLeaveButton;
	private JButton mStartButton;
	private JButton mFillRoomWithAIButton;

	private RoomsDataTableModel mRoomsDataTableModel;
	private UserListModel mUserListModel;

	private ClientConnection mConnection;

	private int mSelectedRoomId = -1;
	private boolean mIsInRoom = false;

	private HashMap<Integer, List<String>> mAllRoomUsers = new HashMap<> ();

    /**
     * Erstellt das Zimmer
     *
     * @param clientConnection Die Client-Verbindung
     */
    public LobbyFrame (ClientConnection clientConnection) {
	    super ("Rooms");

	    mConnection = clientConnection;

	    try {
		    setIconImage (Loader.loadImage ("res/icon.png"));
	    } catch (IOException e) {
		    e.printStackTrace ();
	    }

	    setLayout (new BorderLayout ());
	    setLocationByPlatform (true);
	    setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
	    setSize (820, 500);
	    setResizable (false);
	    addWindowListener (new WindowAdapter () {
		    @Override
		    public void windowClosing (WindowEvent e) {
			    mConnection.stopClientConnection ();
		    }
	    });

	    JPanel leftPanel = new JPanel ();
	    leftPanel.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 10));

	    mRoomsDataTableModel = new RoomsDataTableModel ();

	    mRoomsTable = new JTable (mRoomsDataTableModel);
	    mRoomsTable.getTableHeader ().setReorderingAllowed (false);
	    mRoomsTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
	    mRoomsTable.getSelectionModel ().addListSelectionListener (new ListSelectionListener () {
		    @Override
		    public void valueChanged (ListSelectionEvent e) {
			    int selectedRow = mRoomsTable.getSelectedRow ();
				if (selectedRow != -1) {
					mSelectedRoomId = mRoomsDataTableModel.getId (selectedRow);
					List<String> userNames = mAllRoomUsers.get (mSelectedRoomId);
					if (userNames != null) {
						mUserListModel.setUserNames (userNames);
						String[] userCount = mRoomsDataTableModel.getValueAt (selectedRow, 1).toString ().split ("/");
						if ((userCount[0].equals (userCount[1]) || (boolean) mRoomsDataTableModel.getValueAt (selectedRow, 2)) && !mIsInRoom) {
							mJoinOrLeaveButton.setEnabled (false);
						} else {
							mJoinOrLeaveButton.setEnabled (true);
						}
					} else {
						mSelectedRoomId = -1;
					}
				}
			}
		});
		mRoomsTable.setDefaultRenderer (Boolean.class, new RoomTableCellRenderer (mRoomsTable.getDefaultRenderer (Boolean.class)));
		mRoomsTable.setDefaultRenderer (Object.class, new RoomTableCellRenderer (mRoomsTable.getDefaultRenderer (Object.class)));

		leftPanel.add (new JScrollPane (mRoomsTable));

		JPanel rightPanel = new JPanel ();
		rightPanel.setBorder (BorderFactory.createEmptyBorder (20, 10, 20, 20));
		rightPanel.setLayout (new GridLayout (3, 1));

		mUserListModel = new UserListModel ();

		mRoomUserList = new JList <> (mUserListModel);
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
				} else {
					mConnection.enterRoom (mSelectedRoomId);
				}
			}
		});

	    mStartButton = new JButton ("Start");
	    mStartButton.addActionListener (new ActionListener () {
		    @Override
		    public void actionPerformed (ActionEvent e) {
			    mConnection.startRoom ();
		    }
	    });
	    mStartButton.setEnabled (false);

	    mFillRoomWithAIButton = new JButton ("Fill AI");

	    mFillRoomWithAIButton.addActionListener (new ActionListener () {
		    @Override
		    public void actionPerformed (ActionEvent e) {
			    mConnection.fillRoomWithAi ();
		    }
	    });
	    mFillRoomWithAIButton.setEnabled (false);

	    roomButtonsPanel.add (mJoinOrLeaveButton);
	    roomButtonsPanel.add (mStartButton);
	    roomButtonsPanel.add (mFillRoomWithAIButton);

	    //		rightPanel.add (mRoomPicture);
	    rightPanel.add (new

			    JScrollPane (mRoomUserList));
	    rightPanel.add (roomButtonsPanel);

	    add (leftPanel, BorderLayout.LINE_START);

	    add (rightPanel, BorderLayout.LINE_END);
    }

	/**
	 * Ladet die Zimmerdaten
	 *
	 * @param roomDataList Die Zimmerdaten
	 */
	public void loadRoomData (List <RoomData> roomDataList) {
		if (roomDataList.isEmpty ()) {
			return;
		}

		int newSelection = 0;
		boolean isSelected = false;
		for (int i = 0; i < roomDataList.size () && !isSelected; i++) {
			RoomData roomData = roomDataList.get (i);

			mAllRoomUsers.put (roomData.getRoomId (), roomData.getUsers ());

			if (roomData.getRoomId () == mSelectedRoomId) {
				newSelection = i;

				if (roomData.getUsers ().size () < roomData.getMaxUserCount () && mIsInRoom && !roomData.isRunning ()) {
					mFillRoomWithAIButton.setEnabled (true);
				} else {
					mFillRoomWithAIButton.setEnabled (false);
				}

				if (roomData.getUsers ().size () == roomData.getMaxUserCount () && mIsInRoom && !roomData.isRunning ()) {
					mStartButton.setEnabled (true);
				} else {
					mStartButton.setEnabled (false);
				}

				isSelected = true;
			}

		}
		if (!isSelected) {
			mSelectedRoomId = roomDataList.get (0).getRoomId ();
		}
		mRoomsDataTableModel.setRoomDatas (roomDataList);
		mRoomsTable.setRowSelectionInterval (newSelection, newSelection);
	}

	/**
	 * Stellt ein, ob der Benutzer in einem Zimmer ist
	 *
	 * @param isInRoom Ist es in einem Zimmer?
	 */
	public void setIsInRoom (boolean isInRoom) {
		this.mIsInRoom = isInRoom;
		if (isInRoom) {
			mRoomsTable.setEnabled (false);
			mJoinOrLeaveButton.setText ("Leave");
		} else {
			mRoomsTable.setEnabled (true);
			mJoinOrLeaveButton.setText ("Join");
		}
	}

	private class RoomTableCellRenderer extends DefaultTableCellRenderer {
		private TableCellRenderer mRenderer;

		/**
		 * Erstellt ein Zimmertaballerenderer
		 *
		 * @param renderer Der Tabellerenderer
		 */
		RoomTableCellRenderer (TableCellRenderer renderer) {
			mRenderer = renderer;
		}

		@Override
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component component = mRenderer.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);//#TODO throws ClassCastException
			if (component instanceof JComponent) {
				JComponent jComponent = (JComponent) component;
				jComponent.setBorder (noFocusBorder);
				if (jComponent instanceof JLabel) {
					JLabel jLabel = (JLabel) jComponent;
					jLabel.setHorizontalAlignment (CENTER);
				}
			}
			return component;
		}
	}
}
