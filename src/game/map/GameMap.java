package game.map;

import game.server.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author neczpal
 */
public abstract class GameMap {

	private HashMap <Integer, User> mUsers;
	private String mName;
	private int mMaxUsers;
	private File mMapFile;


	public GameMap () {
		mUsers = new HashMap <> ();
	}

	public void addUser (int id, User user) {
		mUsers.put (id, user);
	}

	public ArrayList <User> getUsers () {
		return new ArrayList <> (mUsers.values ());
	}

	public User findUserByID (int id) {
		return mUsers.get (id);
	}

	public String getName () {
		return mName;
	}

	public void setName (String mName) {
		this.mName = mName;
	}

	public int getMaxUsers () {
		return mMaxUsers;
	}

	public void setMaxUsers (int mMaxUsers) {
		this.mMaxUsers = mMaxUsers;
	}

	public File getMapFile () {
		return mMapFile;
	}

	public void setMapFile (File mMapFile) {
		this.mMapFile = mMapFile;
	}

	public boolean isFull () {
		return mUsers.size () >= mMaxUsers;
	}

	public abstract void draw ();

	public abstract void loadMap (String fileName) throws NotValidMapException;

	public static class NotValidMapException extends Exception {
		public NotValidMapException (String name) {
			super (name);
		}
	}


}
