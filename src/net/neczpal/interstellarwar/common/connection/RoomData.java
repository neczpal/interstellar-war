package net.neczpal.interstellarwar.common.connection;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomData implements Serializable {
	static final long serialVersionUID = 42123L;

	private int mRoomId;
	private String mMapName;
	private ArrayList <String> mUsers;
	private int mMaxUserCount;
	private boolean mIsRunning;

	/**
	 * Erstellt ein Zimmerdatei
	 *
	 * @param mRoomId       Die ID von Zimmer
	 * @param mMapName      Der Name von der Mappe
	 * @param mUsers        Die Namen der Benutzer
	 * @param mMaxUserCount Die maximum Anzahl der Benutzer
	 * @param mIsRunning    Ob dieses Zimmer schon beginnt hat
	 */
	public RoomData (int mRoomId, String mMapName, ArrayList <String> mUsers, int mMaxUserCount, boolean mIsRunning) {
		this.mRoomId = mRoomId;
		this.mMapName = mMapName;
		this.mUsers = mUsers;
		this.mMaxUserCount = mMaxUserCount;
		this.mIsRunning = mIsRunning;
	}

	//GETTERS

	public int getRoomId () {
		return mRoomId;
	}

	public String getMapName () {
		return mMapName;
	}

	public ArrayList <String> getUsers () {
		return mUsers;
	}

	public int getMaxUserCount () {
		return mMaxUserCount;
	}

	public boolean isRunning () {
		return mIsRunning;
	}
}
