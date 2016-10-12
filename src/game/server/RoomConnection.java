package game.server;

import game.map.GameMap;
import game.map.GameMap2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class RoomConnection extends Thread implements Connection {

	private int mRoomConnectionId;
	private String mGameName;
	private String mMapName;
	private int mMaxUserCount;

	private ServerConnection mServerConnection;
	private GameMap mMap;

	private List <Integer> mConnectionIds = new ArrayList <> ();// #TODO SHOULD BE HASHMAP

	private boolean mGameIsRunning = false;

	public RoomConnection (ServerConnection serverConnection, String gameName, String mapName) throws GameMap.NotValidMapException {
		mServerConnection = serverConnection;
		mGameName = gameName;
		mMapName = mapName;
		if (gameName == "2DGAME") {
			mMap = new GameMap2D ();
		}
		mMap.loadMap (mapName);
		mMaxUserCount = mMap.getMaxUsers ();
		mMap.setConnection (this);
		mRoomConnectionId = 0;
	}

	public int getRoomId () {
		return mRoomConnectionId;
	}

	public void setRoomId (int id) {
		mRoomConnectionId = id;
	}

	@Override
	public void run () {
		mGameIsRunning = true;
		while (mGameIsRunning) {
			mMap.onGameThread ();
		}
	}

	public void stopGame () {
		mGameIsRunning = false;
	}


	@Override
	public void send (Command.Type type) {
		send (new Command (type));
	}

	@Override
	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	@Override
	public void send (Command command) {
		for (Integer connectionId : mConnectionIds) {
			mServerConnection.sendToId (connectionId, command);
		}
	}

	public boolean isFull () {
		return mConnectionIds.size () >= mMaxUserCount;
	}

	public boolean isRunning () {
		return mGameIsRunning;
	}

	public void addConnection (Integer id) {
		mConnectionIds.add (id);
	}

	public void removeConnection (Integer id) {
		mConnectionIds.remove (id);
	}

	public GameMap getGameMap () {
		return mMap;
	}


	public String getGameName () {
		return mGameName;
	}

	public String getMapName () {
		return mMapName;
	}

	public int getMaxUserCount () {
		return mMaxUserCount;
	}

	public int getUserCount () {
		return mConnectionIds.size ();
	}

	public int getConnectionIndex (Integer id) {
		return mConnectionIds.indexOf (id) + 1;
	}
}
