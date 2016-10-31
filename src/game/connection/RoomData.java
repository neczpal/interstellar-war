package game.connection;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomData implements Serializable {
	static final long serialVersionUID = 42123L;

	private int mRoomId;
	private String mGameName;
	private String mMapName;
	private ArrayList <String> mUsers;
	private int mMaxUserCount;
	private boolean mIsRunning;

	public RoomData (int mRoomId, String mGameName, String mMapName, ArrayList <String> mUsers, int mMaxUserCount, boolean mIsRunning) {
		this.mRoomId = mRoomId;
		this.mGameName = mGameName;
		this.mMapName = mMapName;
		this.mUsers = mUsers;
		this.mMaxUserCount = mMaxUserCount;
		this.mIsRunning = mIsRunning;
	}

	public int getRoomId () {
		return mRoomId;
	}

	public String getGameName () {
		return mGameName;
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
