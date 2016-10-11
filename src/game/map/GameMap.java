package game.map;

import game.server.Command;
import game.server.Connection;

import java.io.File;
import java.io.Serializable;

/**
 * @author neczpal
 */
public abstract class GameMap {

	//	private HashMap <Integer, User> mUsers;
	private String mMapName;
	private int mMaxUsers;
	private File mMapFile;

	//	private int mReadyUserCount;
	private Connection mConnection;

	public GameMap () {
		//		mUsers = new HashMap <> ();
		//		mReadyUserCount = 0;
	}

	//	public void addUser (int id, User user) {
	//		mUsers.put (id, user);
	//	}
	//
	//	public void removeUser (int id) {
	//		mUsers.remove (id);
	//	}
	//
	//	public User getUser (int id) {
	//		return mUsers.get (id);
	//	}

	//	public void setUserReady (int id) {
	//		mReadyUserCount++;
		//		mUsers.get (id).ready ();
	//	}

	//	public boolean isMapReady () {
	//		return mReadyUserCount >= mMaxUsers;
	//	}

	public String getMapName () {
		return mMapName;
	}

	public void setMapName (String mName) {
		this.mMapName = mName;
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

	//	public boolean isFull () {
	//		return mUsers.size () >= mMaxUsers;
	//	}

	public abstract void mouseEvent ();

	public abstract void keyboardEvent ();

	public abstract void draw ();

	public abstract void loadMap (String fileName) throws NotValidMapException;

	public abstract void loadData (Serializable[] data);

	public abstract Serializable[] toData ();

	public void setConnection (Connection connection) {
		mConnection = connection;
	}

	public Connection getConection () {
		return mConnection;
	}


	public abstract void onGameThread ();

	public abstract void receiveClient (Command command);

	public abstract void receiveServer (Command command);

	public static class NotValidMapException extends Exception {
		public NotValidMapException (String name) {
			super (name);
		}
	}

}
