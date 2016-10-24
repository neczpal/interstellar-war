package game.connection;

public class User {

	private int mId;
	private int mRoomId;
	private int mRoomIndex;

	private String mName;

	public User (String name) {
		this (name, 0);
	}

	public User (String name, int id) {
		mName = name;
		mId = id;
		mRoomId = 0;
		mRoomIndex = 0;
	}

	public int getId () {
		return mId;
	}

	public void setId (int id) {
		mId = id;
	}

	public int getRoomId () {
		return mRoomId;
	}

	public void setRoomId (int mRoomId) {
		this.mRoomId = mRoomId;
	}

	public int getRoomIndex () {
		return mRoomIndex;
	}

	public void setRoomIndex (int mRoomIndex) {
		this.mRoomIndex = mRoomIndex;
	}

	@Override
	public String toString () {
		return mName + " (" + mId + ") [" + mRoomIndex + "]";
	}
}
