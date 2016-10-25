package game.connection;

import game.map.GameMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class Room implements Connection {

	private int mRoomId;

	private String mGameName;
	private String mMapName;
	private int mMaxUserCount;
	private HashMap <Integer, Integer> mConnectionIds = new HashMap <> ();
	private int mIndexes;

	private ServerConnection mServerConnection;
	private GameMap mMap;

	public Room (ServerConnection serverConnection, String gameName, String mapName) throws GameMap.NotValidMapException {
		mServerConnection = serverConnection;
		mGameName = gameName;
		mMap = GameMap.createGameMap (gameName);
		mMap.loadMap (mapName);
		mMapName = mapName;
		mMaxUserCount = mMap.getMaxUsers ();
		mMap.setConnection (this);
		mRoomId = 0;
		mIndexes = 1;
	}

	public int getRoomId () {
		return mRoomId;
	}

	public void setRoomId (int id) {
		mRoomId = id;
	}

	@Override
	public String toString () {
		return mGameName + " (" + mMapName + ")" + getUserCount () + " / " + getMaxUserCount () + " [" + mRoomId + "] ";
	}

	public void startGame () {
		mMap.start ();
	}

	public void stopGame () {
		mMap.stopGame ();
	}

	public void receive (Command command) {
		mMap.receiveServer (command);
	}

	public boolean isEmpty () {
		return mConnectionIds.isEmpty ();
	}

	public boolean isFull () {
		return mConnectionIds.size () >= mMaxUserCount;
	}

	public boolean isMapRunning () {
		return mMap.isAlive ();
	}

	public void addUser (User user) {
		mConnectionIds.put (user.getId (), mIndexes);
		user.setRoomIndex (mIndexes);
		mIndexes++;
	}

	public void removeUser (User user) {
		mConnectionIds.remove (user.getId ());
		user.setRoomIndex (0);
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

	public RoomData getData () {
		ArrayList <String> mUsers = mConnectionIds.keySet ().stream ().map (id -> mServerConnection.getUser (id).getName ()).collect (Collectors.toCollection (ArrayList::new));
		return new RoomData (mRoomId, mGameName, mMapName, mUsers, mMaxUserCount);
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
}
