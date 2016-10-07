package game.server;

public class User {

	private int mId;
	private String mName;

	public User (String name) {
		this (name, 0);
	}

	public User (String name, int id) {
		mName = name;
		mId = id;
	}

	public int getId () {
		return mId;
	}

	public void setId (int id) {
		mId = id;
	}

}
