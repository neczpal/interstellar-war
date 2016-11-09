package net.neczpal.interstellarwar.desktop.ui.models;

import net.neczpal.interstellarwar.common.RoomData;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RoomsDataTableModel extends AbstractTableModel {
	private List <RoomData> mRoomDatas = new ArrayList <> ();

	@Override
	public int getRowCount () {
		return mRoomDatas.size ();
	}

	@Override
	public int getColumnCount () {
		return 3;
	}

	@Override
	public Object getValueAt (int rowIndex, int columnIndex) {
		RoomData roomData = mRoomDatas.get (rowIndex);
		switch (columnIndex) {
			case 0:
				return roomData.getMapName ();
			case 1:
				return roomData.getUsers ().size () + "/" + roomData.getMaxUserCount ();
			default: // case 3:
				return roomData.isRunning ();
		}
	}

	@Override
	public String getColumnName (int columnIndex) {
		switch (columnIndex) {
			case 0:
				return "Map name";
			case 1:
				return "Users";
			default: // case 3:
				return "Running";
		}
	}

	@Override
	public Class <?> getColumnClass (int columnIndex) {
		switch (columnIndex) {
			case 0:
			case 1:
				return String.class;
			default: // case 3:
				return Boolean.class;
		}
	}

	@Override
	public boolean isCellEditable (int rowIndex, int columnIndex) {
		return false;
	}

	public void setRoomDatas (List <RoomData> roomDatas) {
		mRoomDatas = roomDatas;
		fireTableDataChanged ();
	}

	public int getId (int rowIndex) {
		return mRoomDatas.get (rowIndex).getRoomId ();
	}
}
