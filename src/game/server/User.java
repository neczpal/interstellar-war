package game.server;

public class User {

	private int mId;
	private int mRoomId;

	private String mName;
	private boolean mReadyToPlay;

	public User (String name) {
		this (name, 0);
	}

	public User (String name, int id) {
		mName = name;
		mId = id;
		mReadyToPlay = false;
	}

	public void ready () {
		mReadyToPlay = true;
	}

	public int getId () {
		return mId;
	}

	public void setId (int id) {
		mId = id;
	}

}
