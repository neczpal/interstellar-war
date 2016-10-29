package game.map;

import game.connection.Command;
import game.connection.Connection;
import game.map.interstellarwar.InterstellarWar;

import java.io.Serializable;

public abstract class GameMap extends Thread {
	private String mMapName;
	private int mMaxUsers;
	private Connection mConnection;

	private boolean mGameIsRunning = false;

	public static GameMap createGameMap (String gameName) {
		GameMap gameMap = null;
		if (gameName.equals (InterstellarWar.GAME_NAME)) {
			gameMap = new InterstellarWar ();
		}
		return gameMap;
	}

	@Override
	public void run () {
		mGameIsRunning = true;
		while (mGameIsRunning) {
			onGameThread ();
		}
	}

	public void stopGame () {
		mGameIsRunning = false;
	}

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

	public abstract void init ();

	public abstract void mouseEvent ();

	public abstract void keyboardEvent ();

	public abstract void draw ();

	public abstract void loadMap (String fileName) throws NotValidMapException;

	public abstract void loadData (Serializable[] data);

	public abstract Serializable[] toData ();

	public Connection getConnection () {
		return mConnection;
	}

	public void setConnection (Connection connection) {
		mConnection = connection;
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
