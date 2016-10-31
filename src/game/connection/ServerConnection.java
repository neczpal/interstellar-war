package game.connection;

import game.Log;
import game.map.GameMap;
import game.map.interstellarwar.InterstellarWar;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ServerConnection extends Thread implements Connection {

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


	public ServerConnection (int port) {
		super ("ServerConnection");
		mPort = port;
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

	public void stopServerConnection () {
		mRoomServer.stopRoomServer ();
		mIsRunning = false;
		for (Client client : mClients.values ()) {
			client.stopClient ();
			try {
				client.close ();
			} catch (IOException ex) {
				log.w ("stopServerConnection Client close bibi");
			}
		}
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

	public void addClient () throws IOException {
		Client client = new Client (this, mServerSocket.accept ());
		mTemporaryClients.add (client);
		client.start ();
	}

	public void removeClient (int id) {
		mClients.remove (id);
		log.i ("Client (" + id + ") removed");
		User user = getUser (id);
		leaveRoom (user);
		mUsers.remove (user.getId ());
		log.i ("User (" + user + ") exits the connection.");
	}

	public int findClientByPort (int port) {
		for (int i = 0; i < mTemporaryClients.size (); i++) {
			if (mTemporaryClients.get (i).getPort () == port) {
				return i;
			}
		}
		return -1;
	}

	public RoomData[] getRoomData () {
		ArrayList <RoomData> roomData = mRooms.values ().stream ().map (Room::getData).collect (Collectors.toCollection (ArrayList::new));
		RoomData[] ret = new RoomData[roomData.size ()];
		roomData.toArray (ret);
		return ret;
	}

	public User getUser (Object key) {
		return mUsers.get (key);
	}

	public Room getRoom (Object key) {
		return mRooms.get (key);
	}

	// RECEIVE
	private void enterServer (String name, int port) {
		int tempIndex = findClientByPort (port);
		Client client = mTemporaryClients.get (tempIndex);
		int id = mClientIdCounter++;
		mClients.put (id, client);
		mTemporaryClients.remove (tempIndex);

		User newUser = new User (name, id);
		mUsers.put (id, newUser);

		sendToId (id, new Command (Command.Type.CONNECTION_READY, id));
		sendToId (id, Command.Type.LIST_ROOMS, getRoomData ());
	}

	private void exitServer (User user) {
		removeClient (user.getId ());
	}

	private void leaveRoom (User user) {
		int roomId = user.getRoomId ();
		if (roomId != 0) {
			Room room = getRoom (roomId);
			log.i ("User (" + user + ") is leaving the Room (" + room + ")");
			room.removeUser (user);
			if (room.isEmpty () && room.isMapRunning ()) {
				room.stopGame ();
				mRooms.remove (roomId);
			}
			user.setRoomId (0);

			listRoom ();
		} else {
			log.i ("User (" + user + ") is not in a room");
		}
	}

	private void enterRoom (User user, Room room) {
		leaveRoom (user);
		if (room != null && !room.isFull () && !room.isMapRunning ()) {
			log.i ("User (" + user + ") is connecting to the Room (" + room + ")");
			user.setRoomId (room.getRoomId ());
			room.addUser (user);
			sendToId (user.getId (), new Command (Command.Type.MAP_DATA, user.getRoomIndex (), room.getGameName (), room.getGameMap ().toData ()));

			listRoom ();
		} else {
			log.i (user + " is connecting to the Room (" + room + "), but its full/running/not existing!");
		}
	}

	private void startRoom (User user) {
		Room room = getRoom (user.getRoomId ());
		if (room != null && room.isFull () && !room.isMapRunning ()) {
			room.send (Command.Type.READY_TO_PLAY, room.getGameName (), room.getMapFantasyName ());
			room.startGame ();
			addRoom (room.getGameName (), room.getMapName ());

			listRoom ();
			log.i (user + " is starting the game in the Room (" + room + ")");
		} else {
			log.i (user + " couldn't start the game in the Room (" + room + "), because it was not full/already started/not existing");
		}
	}

	private void gameCommand (User user, Command command) {
		Room room = getRoom (user.getRoomId ());
		log.i ("User (" + user + ") in the Room (" + room + ") sent GAME_COMMAND " + command.data[1]);
		room.receive (command);
	}

	public void receive (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				enterServer ((String) command.data[1], currentPort);
				break;
			case EXIT_SERVER:
				exitServer (getUser (command.data[0]));
				break;
			case LEAVE_ROOM:
				leaveRoom (getUser (command.data[0]));
				break;
			case ENTER_ROOM:
				enterRoom (getUser (command.data[0]), getRoom (command.data[1]));
				break;
			case START_ROOM:
				startRoom (getUser (command.data[0]));
				break;
			case GAME_COMMAND:
				gameCommand (getUser (command.data[0]), command);
				break;
		}
	}

	//SEND
	public void listRoom () {
		send (Command.Type.LIST_ROOMS, getRoomData ());
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
		while (iterator.hasNext () && mIsRunning) {
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