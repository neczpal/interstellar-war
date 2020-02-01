package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.CommandParamKey;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Room {

	private int mRoomId;

	private String mMapFileName;

	private HashMap <Integer, Integer> mConnectionIds = new HashMap <> ();
	private int mIndexes;

	private ServerConnection mServerConnection;
	private InterstellarWarServer mGameServer;

	/**
	 * Erstellt ein Zimmer auf dem Server
	 *
	 * @param serverConnection Die Server-Verbindung
	 * @param mapFileName      Der Name des Mappes
	 * @throws IOException falls das File nicht existiert
	 */
	public Room (ServerConnection serverConnection, String mapFileName) throws IOException {
		mServerConnection = serverConnection;
		mMapFileName = mapFileName;
		InterstellarWarCore core = new InterstellarWarCore (mapFileName);
		mGameServer = new InterstellarWarServer (core, this);
		mRoomId = 0;
		mIndexes = 1;
	}

	/**
	 * Startet das Spiel
	 */
	public void startGame () {
		mGameServer.getCore ().start ();
	}

	/**
	 * Beendet das Spiel
	 */
	public void stopGame () {
		mGameServer.getCore ().stopGame ();
	}

	/**
	 * Bekommt ein Befehle von Klient
	 *
	 * @param command Der Befehl
	 */
	public void receive (JSONObject command) {
		mGameServer.receive (command);
	}

	/**
	 * Stellt ein Benutzer in dem Zimmer
	 *
	 * @param user Der Benutzer
	 */
	public void addUser (User user) {
		mConnectionIds.put (user.getId (), mIndexes);
		user.setRoomIndex (mIndexes);
		mIndexes++;
	}

	/**
	 * Ausnimmt ein Benutzer von dem Zimmer
	 *
	 * @param user Der Benutzer
	 */
	public void removeUser (User user) {
		mConnectionIds.remove (user.getId ());
		user.setRoomIndex (0);
		mIndexes--;
	}

	/**
	 * @return Das Zimmer-Data des Zimmers
	 */
	public JSONObject getData () {
		List<String> mUsers = new ArrayList<> ();
		for (Integer key : mConnectionIds.keySet ()) {
			mUsers.add (mServerConnection.getUser (key).getName ());
		}

		JSONObject roomData = new JSONObject ();
		roomData.put (CommandParamKey.ROOM_ID_KEY, mRoomId);
		roomData.put (CommandParamKey.MAP_NAME_KEY, getMapFantasyName ());
		roomData.put (CommandParamKey.USER_LIST_KEY, mUsers);
		roomData.put (CommandParamKey.MAX_USER_COUNT_KEY, getMaxUserCount ());
		roomData.put (CommandParamKey.IS_ROOM_RUNNING_KEY, isMapRunning ());

		return roomData;
	}

	/**
	 * Sendet ein Befehl zu den Klient des Zimmers
	 *
	 * @param command Der Befehl
	 */
	public void send (JSONObject command) {
		HashMap<Integer, Client> clients = mServerConnection.getClients ();
		Iterator<HashMap.Entry<Integer, Client>> iterator = clients.entrySet ().iterator ();
		while (iterator.hasNext () && mServerConnection.isAlive ()) {
			HashMap.Entry<Integer, Client> entry = iterator.next ();
			if (mConnectionIds.containsKey (entry.getKey ())) {
				if (!entry.getValue ().send (command)) {
					iterator.remove ();
					mServerConnection.removeClient (entry.getKey ());
				}
			}
		}
	}


	//GETTERS, SETTERS

	public int getRoomId () {
		return mRoomId;
	}

	public void setRoomId (int id) {
		mRoomId = id;
	}

	public InterstellarWarServer getGameServer () {
		return mGameServer;
	}

	public String getMapName () {
		return mMapFileName;
	}

	public String getMapFantasyName () {
		return mGameServer.getCore ().getMapName ();
	}

	public int getMaxUserCount () {
		return mGameServer.getCore ().getMaxUsers ();
	}

	public int getUserCount () {
		return mConnectionIds.size ();
	}

	public boolean isEmpty () {
		return mConnectionIds.isEmpty ();
	}

	public boolean isFull () {
		return mConnectionIds.size () >= getMaxUserCount ();
	}

	public boolean isMapRunning () {
		return mGameServer.getCore ().isRunning ();
	}

	@Override
	public String toString () {
		return " (" + getMapFantasyName () + ") " + getUserCount () + "/" + getMaxUserCount () + " [" + mRoomId + "] ";
	}
}
