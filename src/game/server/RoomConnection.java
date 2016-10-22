package game.server;

import game.map.GameMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

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

	private HashMap <Integer, Integer> mConnectionIds = new HashMap <> ();
	private int mIndexes;

	private boolean mGameIsRunning = false;

	public RoomConnection (ServerConnection serverConnection, String gameName, String mapName) throws GameMap.NotValidMapException {
		mServerConnection = serverConnection;
		mGameName = gameName;
		mMap = GameMap.createGameMap (gameName);
		mMap.loadMap (mapName);
		mMapName = mapName;
		mMaxUserCount = mMap.getMaxUsers ();
		mMap.setConnection (this);
		mRoomConnectionId = 0;
		mIndexes = 1;
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
			if (!mMap.onGameThread ()) {
				//				stopGame ();
			}
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
		HashMap <Integer, Client> clients = mServerConnection.getClients ();
		Iterator <HashMap.Entry <Integer, Client>> iterator = clients.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			HashMap.Entry <Integer, Client> entry = iterator.next ();
			if (mConnectionIds.containsKey (entry.getKey ())) {
				if (!entry.getValue ().send (command)) {
					iterator.remove ();
					mServerConnection.removeClient (entry.getKey ());
				}
			}
		}
	}

	public boolean isInside (int id) {
		return mConnectionIds.containsKey (id);
	}

	public boolean isEmpty () {
		return mConnectionIds.isEmpty ();
	}

	public boolean isFull () {
		return mConnectionIds.size () >= mMaxUserCount;
	}

	public boolean isRunning () {
		return mGameIsRunning;
	}

	public void addConnection (Integer id) {
		mConnectionIds.put (id, mIndexes++);
	}

	public void removeConnection (Integer id) {
		mConnectionIds.remove (id);
		mIndexes--;
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

	public String getMapFantasyName () {
		return mMap.getMapName ();
	}

	public int getMaxUserCount () {
		return mMaxUserCount;
	}

	public int getUserCount () {
		return mConnectionIds.size ();
	}

	public int getConnectionIndex (Integer id) {
		return mConnectionIds.get (id);
	}
}
