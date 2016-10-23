package game.server;

import game.Log;
import game.map.GameMap;
import game.map.interstellarwar.InterstellarWar;
import game.map.rockpaperscissors.RockPaperScissors;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServerConnection extends Thread {

	private static int mClientIdCounter = 1;
	private static int mRoomIdCounter = 1;
	private int mPort;

	private ServerSocket mServerSocket;

	private HashMap <Integer, Room> mRooms = new HashMap <> ();
	private RoomServer mRoomServer;

	private HashMap <Integer, Client> mClients = new HashMap <> ();
	private List <Client> mTemporaryClients = new ArrayList <> ();

	private HashMap <Integer, User> mUsers = new HashMap <> ();

	private boolean mIsRunning = false;
	private Log log = new Log (this);


	public ServerConnection () {
		super ("ServerConnection");
		mPort = 23232;
		mRoomServer = new RoomServer (this);
		try {
			mServerSocket = new ServerSocket (mPort);
		} catch (IOException e) {
			log.e ("ServerConnection inicializási bibi: " + e.getMessage ());
		}
		this.start ();
	}

	public void run () {
		log.i ("Sever started.");
		addRoom (InterstellarWar.GAME_NAME, "map01");
		addRoom (InterstellarWar.GAME_NAME, "map02");
		addRoom (InterstellarWar.GAME_NAME, "map03");
		addRoom (InterstellarWar.GAME_NAME, "map04");
		addRoom (InterstellarWar.GAME_NAME, "map05");
		addRoom (RockPaperScissors.GAME_NAME, "");
		mRoomServer.start ();
		mIsRunning = true;
		while (mIsRunning) {
			try {
				addClient ();
			} catch (IOException e) {
				log.e ("ServerConnection futási bibi: " + e.getMessage ());
			}
		}
	}

	public void stopServer () {
		mRoomServer.stopRoomServer ();
		mIsRunning = false;
		try {
			mServerSocket.close ();
		} catch (IOException e) {
		}
	}

	public void addRoom (String gameName, String mapName) {
		try {
			Room room = new Room (this, gameName, mapName);
			room.setRoomId (mRoomIdCounter);
			mRooms.put (mRoomIdCounter++, room);
		} catch (GameMap.NotValidMapException e) {
			log.e (gameName + " : " + mapName + " is not a valid map. :" + e.getMessage ());
		}
	}

	public int findClientByPort (int port) {
		for (int i = 0; i < mTemporaryClients.size (); i++) {
			if (mTemporaryClients.get (i).getPort () == port) {
				return i;
			}
		}
		return -1;
	}

	public void addClient () throws IOException {
		Client client = new Client (this, mServerSocket.accept ());
		mTemporaryClients.add (client);
		client.start ();
	}

	public void removeClient (int id) {
		User user = mUsers.get (id);
		leaveRoom (user);

		log.i ("User (" + user + ") disconnected from the server");
		mClients.remove (id);
	}

	public Serializable[] getRoomData () {
		int i = 0;
		Serializable[] roomData = new Serializable[mRooms.size () * 5];
		Iterator <HashMap.Entry <Integer, Room>> iterator = mRooms.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			Room connection = iterator.next ().getValue ();
			roomData[i++] = connection.getRoomId ();
			roomData[i++] = connection.getGameName ();
			roomData[i++] = connection.getMapFantasyName ();
			roomData[i++] = connection.getUserCount ();
			roomData[i++] = connection.getMaxUserCount ();
		}
		return roomData;
	}

	private void enterServer (String name, int port) {
		int tempIndex = findClientByPort (port);
		Client client = mTemporaryClients.get (tempIndex);
		int id = mClientIdCounter++;
		mClients.put (id, client);
		mTemporaryClients.remove (tempIndex);

		User newUser = new User (name, id);
		mUsers.put (id, newUser);

		sendToId (id, new Command (Command.Type.ACCEPT_CONNECTION, id));
	}

	private void exitServer (User user) {
		leaveRoom (user);
		mUsers.remove (user.getId ());
		mClients.remove (user.getId ());
		log.i ("User (" + user.getId () + ") exits the server.");
	}

	private void leaveRoom (User user) {
		int userId = user.getId ();
		int roomId = user.getRoomId ();
		if (roomId != 0) {
			Room room = mRooms.get (roomId);
			log.i ("User (" + user + ") is leaving the Room (" + room + ")");
			room.removeUser (user);
			if (room.isEmpty () && room.isRunning ()) {
				room.stopGame ();
				mRooms.remove (roomId);
			}
			user.setRoomId (0);
		} else {
			log.i ("User (" + userId + ") is not in a room");
		}
	}

	private void enterRoom (User user, Room room) {
		leaveRoom (user);
		if (room != null && !room.isFull () && !room.isRunning ()) {
			log.i ("User (" + user + ") is connecting to the Room (" + room + ")");
			user.setRoomId (room.getRoomId ());
			room.addUser (user);
			sendToId (user.getId (), new Command (Command.Type.MAP_DATA, user.getRoomIndex (), room.getGameName (), room.getGameMap ().toData ()));
			if (room.isFull ()) {
				room.send (Command.Type.READY_TO_PLAY, room.getGameName (), room.getMapFantasyName ());
				room.start ();
				addRoom (room.getGameName (), room.getMapName ());
			} else {
				//				send (Command.Type.LIST_ROOMS, getRoomData ());
			}
		} else {
			log.i (user + " is connecting to the Room (" + room + "), but its full/running/not existing!");
		}
	}

	public void gameCommand (User user, Command command) {
		Room room = mRooms.get (user.getRoomId ());
		log.i ("User (" + user + ") in the Room (" + room + ") sent GAME_DATA " + command.data[1]);
		room.receive (command);
	}

	public void receive (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				enterServer ((String) command.data[1], currentPort);
				break;
			case EXIT_SERVER:
				exitServer (mUsers.get (command.data[0]));
				break;
			case LEAVE_ROOM:
				leaveRoom (mUsers.get (command.data[0]));
				break;
			case ENTER_ROOM:
				enterRoom (mUsers.get (command.data[0]), mRooms.get (command.data[1]));
				break;
			case GAME_DATA:
				gameCommand (mUsers.get (command.data[0]), command);
				break;
		}
	}

	public void sendToId (int id, Command.Type type) {
		sendToId (id, new Command (type));
	}

	public void sendToId (int id, Command.Type type, Serializable... data) {
		sendToId (id, new Command (type, data));
	}

	public void sendToId (int id, Command command) {
		Client client = mClients.get (id);
		if (client != null && !client.send (command)) {
			removeClient (id);
		}
	}


	public void send (Command.Type type) {
		send (new Command (type));
	}

	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	public void send (Command command) {
		Iterator <HashMap.Entry <Integer, Client>> iterator = mClients.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			HashMap.Entry <Integer, Client> entry = iterator.next ();
			if (!entry.getValue ().send (command)) {
				iterator.remove ();
				removeClient (entry.getKey ());
			}
		}
	}

	public HashMap <Integer, Client> getClients () {
		return mClients;
	}
}