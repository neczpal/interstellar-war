package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.Command;
import net.neczpal.interstellarwar.common.InterstellarWarCore;
import net.neczpal.interstellarwar.common.RoomData;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Room {

	private int mRoomId;

	private String mMapFileName;

	private HashMap <Integer, Integer> mConnectionIds = new HashMap <> ();
	private int mIndexes;

	private ServerConnection mServerConnection;
	private InterstellarWarServer mGameServer;

	public Room (ServerConnection serverConnection, String mapFileName) throws IOException {
		mServerConnection = serverConnection;
		mMapFileName = mapFileName;
		InterstellarWarCore core = new InterstellarWarCore (mapFileName);
		mGameServer = new InterstellarWarServer (core, this);
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
		return " (" + getMapFantasyName () + ") " + getUserCount () + "/" + getMaxUserCount () + " [" + mRoomId + "] ";
	}

	public void startGame () {
		mGameServer.getCore ().start ();
	}

	public void stopGame () {
		mGameServer.getCore ().stopGame ();
	}

	public void receive (Command command) {
		mGameServer.receive (command);
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

	public RoomData getData () {
		ArrayList <String> mUsers = mConnectionIds.keySet ().stream ().map (id -> mServerConnection.getUser (id).getName ()).collect (Collectors.toCollection (ArrayList::new));
		return new RoomData (mRoomId, getMapFantasyName (), mUsers, getMaxUserCount (), isMapRunning ());
	}

	public void send (Command.Type type) {
		send (new Command (type));
	}

	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	public void send (Command command) {
		HashMap <Integer, Client> clients = mServerConnection.getClients ();
		Iterator <HashMap.Entry <Integer, Client>> iterator = clients.entrySet ().iterator ();
		while (iterator.hasNext () && mServerConnection.isAlive ()) {
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
