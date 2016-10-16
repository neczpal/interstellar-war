package game.map;

import game.map.interstellarwar.InterstellarWar;
import game.map.rockpaperscissors.RockPaperScissors;
import game.server.Command;
import game.server.Connection;

import java.io.Serializable;

/**
 * @author neczpal
 */
public abstract class GameMap {
	private String mMapName;
	private int mMaxUsers;
	private Connection mConnection;

	public static GameMap createGameMap (String gameName) {
		GameMap gameMap = null;
		if (gameName.equals (InterstellarWar.GAME_NAME)) {
			gameMap = new InterstellarWar ();
		} else if (gameName.equals (RockPaperScissors.GAME_NAME)) {
			gameMap = new RockPaperScissors ();
		}
		return gameMap;
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

	public abstract boolean onGameThread ();

	public abstract void receiveClient (Command command);

	public abstract void receiveServer (Command command);

	public static class NotValidMapException extends Exception {
		public NotValidMapException (String name) {
			super (name);
		}
	}

}
