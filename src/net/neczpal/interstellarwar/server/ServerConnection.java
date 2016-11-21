package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.Command;
import net.neczpal.interstellarwar.common.connection.RoomData;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private volatile boolean mIsRunning = false;
	private Logger mLogger = Logger.getLogger (ServerConnection.class.getCanonicalName ());

	/**
	 * Erstellt ein Server-Verbindung
	 *
	 * @param port Das Port der Server
	 */
	public ServerConnection (int port) {
		super ("ServerConnection");
		mPort = port;
		mRoomServer = new RoomServer (this);

		try {
			mServerSocket = new ServerSocket (mPort);
		} catch (IOException e) {
			mLogger.log (Level.SEVERE, "Server couldn't be created", e);
		}
		this.start ();
	}

	/**
	 * Addiert die Klienten die auf den Server verbinden
	 */
	@Override
	public void run () {
		mLogger.log (Level.INFO, "Server started");

		addRoom ("map01");
		addRoom ("map02");
		addRoom ("map03");
		addRoom ("map04");
		addRoom ("map05");
		addRoom ("map06");
		addRoom ("map07");

		mRoomServer.start ();
		mIsRunning = true;
		while (mIsRunning) {
			try {
				addClient ();
			} catch (IOException ex) {
				mLogger.log (Level.WARNING, "Server couldn't accept Client: " + ex.getMessage ());
			}
		}
	}

	/**
	 * Beendet die Server-Verbindung
	 */
	public void stopServerConnection () {
		mRoomServer.stopRoomServer ();
		mIsRunning = false;
		try {
			mServerSocket.close ();
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "ServerSocket couldn't close: " + ex.getMessage ());
		}
		mClientIdCounter = 1;
		mRoomIdCounter = 1;
	}

	/**
	 * Addiert ein Zimmer zu dem Server
	 *
	 * @param mapName Der Name der Mappe-File
	 */
	public void addRoom (String mapName) {
		try {
			Room room = new Room (this, mapName);
			room.setRoomId (mRoomIdCounter);
			mRooms.put (mRoomIdCounter++, room);
			mLogger.log (Level.INFO, "New room created with the Map (" + mapName + ")");
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "Server couldn't add Room, because Map (" + mapName + ") couldn't load: " + ex.getMessage ());
		}
	}

	/**
	 * Addiert ein Klient zu dem Server
	 *
	 * @throws IOException falls Socket-Fehler
	 */
	public void addClient () throws IOException {
		Client client = new Client (this, mServerSocket.accept ());
		mTemporaryClients.add (client);
		client.start ();
	}

	/**
	 * Verlöscht das Klient von dem Server
	 *
	 * @param id Der ID der Benutzer
	 */
	public void removeClient (int id) {
		mClients.remove (id);
		mLogger.log (Level.INFO, "Client removed with the ID (" + id + ")");

		User user = getUser (id);
		if (user == null)
			return;
		leaveRoom (user);
		mUsers.remove (user.getId ());
	}

	/**
	 * Findet ein Klient durch port
	 *
	 * @param port Das Port der Klient
	 * @return Das Index der Klient
	 */
	public int findClientByPort (int port) {
		for (int i = 0; i < mTemporaryClients.size (); i++) {
			if (mTemporaryClients.get (i).getPort () == port) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return Ein Liste von dem alle Zimmer-Data auf dem Server
	 */
	public ArrayList <RoomData> getRoomData () {
		ArrayList <RoomData> roomDatas = new ArrayList <> ();
		for (Room room : mRooms.values ()) {
			roomDatas.add (room.getData ());
		}

		return roomDatas;
	}

	/**
	 * @param key Das ID des Benutzers
	 * @return Der Benutzer
	 */
	public User getUser (Object key) {
		return mUsers.get (key);
	}

	/**
	 * @param key Das ID des Zimmers
	 * @return Das Zimmer
	 */
	public Room getRoom (Object key) {
		return mRooms.get (key);
	}

	// RECEIVE

	/**
	 * Ein Klient tritt in den Server ein,
	 * bekommt eine Verbindung-ID
	 *
	 * @param name Der Benutzername
	 * @param port Das Port der Klient
	 */
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

		mLogger.log (Level.INFO, "-> User (" + newUser + ") entered the server");
	}

	/**
	 * Der Benutzer beendet die Verbindung mit dem Server
	 *
	 * @param user Der Benutzer
	 */
	private void exitServer (User user) {
		removeClient (user.getId ());
		mLogger.log (Level.INFO, "-> User (" + user + ") exits from the server");
	}

	/**
	 * Der Benutzer tirtt in dem Zimmer ein
	 *
	 * @param user Der Benutzer
	 * @param room Das Zimmer
	 */
	private void enterRoom (User user, Room room) {
		leaveRoom (user);
		if (room != null && !room.isFull () && !room.isMapRunning ()) {
			user.setRoomId (room.getRoomId ());
			room.addUser (user);
			sendToId (user.getId (), new Command (Command.Type.MAP_DATA, user.getRoomIndex (), room.getGameServer ().getCore ().getData ()));

			listRoom ();
			mLogger.log (Level.INFO, "-> User (" + user + ") connected to the Room (" + room + ")");
		} else {
			mLogger.log (Level.WARNING, "-> User (" + user + ") couldn't connect to the Room (" + room + "), because it was full/running/not existing");
		}
	}

	/**
	 * Startet das Zimmer, wo der Benutzer ist
	 *
	 * @param user Der Benutzer
	 */
	private void startRoom (User user) {
		Room room = getRoom (user.getRoomId ());
		if (room != null && room.isFull () && !room.isMapRunning ()) {
			room.send (Command.Type.READY_TO_PLAY, room.getMapFantasyName ());
			room.startGame ();
			addRoom (room.getMapName ());

			listRoom ();
			mLogger.log (Level.INFO, "-> User (" + user + ") started the game in the Room (" + room + ")");
		} else {
			mLogger.log (Level.WARNING, "-> User (" + user + ") couldn't start the game in the Room (" + room + "), because it was not yet full/running/not existing");
		}
	}

	/**
	 * Der Benutzer verlasst das Zimmer
	 *
	 * @param user Der Benutzer
	 */
	private void leaveRoom (User user) {
		int roomId = user.getRoomId ();
		if (roomId != 0) {
			Room room = getRoom (roomId);
			room.removeUser (user);
			if (room.isEmpty () && room.isMapRunning ()) {
				room.stopGame ();
				mRooms.remove (roomId);
			}
			user.setRoomId (0);

			listRoom ();
			mLogger.log (Level.INFO, "-> User (" + user + ") left the Room (" + room + ")");
		}
	}

	/**
	 * Bekommt ein Spiel-Befehl von dem Benutzer
	 *
	 * @param user    Der Benutzer
	 * @param command Der Spiel-Befehl
	 */
	private void gameCommand (User user, Command command) {
		Room room = getRoom (user.getRoomId ());
		mLogger.log (Level.INFO, "-> Received GameCommand (" + command + ")from User (" + user + ") in the Room (" + room + ")");
		room.receive (command);
	}

	/**
	 * Bekommt ein Befehl von dem Benutzer
	 *
	 * @param command     Der Befehl
	 * @param currentPort Das Port der Benutzer
	 */
	public void receive (Command command, int currentPort) {
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

	/**
	 * Sendet die Zimmer-Data zu den Benutzern
	 */
	public void listRoom () {
		ArrayList <RoomData> roomDatas = getRoomData ();
		send (Command.Type.LIST_ROOMS, getRoomData ());
		mLogger.log (Level.INFO, "<- Sending RoomDatas Size(" + roomDatas.size () + ")");
	}

	/**
	 * Sendet ein Befehl zu dem Benutzer
	 *
	 * @param id   Das ID der Benutzer
	 * @param type Das Typ der Befehl
	 */
	public void sendToId (int id, Command.Type type) {
		sendToId (id, new Command (type));
	}

	/**
	 * Sendet ein Befehl zu dem Benutzer
	 *
	 * @param id   Das ID der Benutzer
	 * @param type Das Typ der Befehl
	 * @param data Das Data der Befehl
	 */
	public void sendToId (int id, Command.Type type, Serializable... data) {
		sendToId (id, new Command (type, data));
	}

	/**
	 * Sendet ein Befehl zu dem Benutzer
	 *
	 * @param id      Das ID der Benutzer
	 * @param command Der Befehl
	 */
	public void sendToId (int id, Command command) {
		Client client = mClients.get (id);
		if (client != null && !client.send (command)) {
			removeClient (id);
		}
	}

	/**
	 * Sender ein Befehl zu den allen Benutzern
	 *
	 * @param type Das Typ der Befehl
	 */
	public void send (Command.Type type) {
		send (new Command (type));
	}

	/**
	 * Sender ein Befehl zu den allen Benutzern
	 *
	 * @param type Das Typ der Befehl
	 * @param data Das Data der Befehl
	 */
	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	/**
	 * Sender ein Befehl zu den allen Benutzern
	 *
	 * @param command Der Befehl
	 */
	public synchronized void send (Command command) {
		Iterator <HashMap.Entry <Integer, Client>> iterator = mClients.entrySet ().iterator ();
		while (iterator.hasNext () && mIsRunning) {
			HashMap.Entry <Integer, Client> entry = iterator.next ();
			if (!entry.getValue ().send (command)) {
				iterator.remove ();
				removeClient (entry.getKey ());
			}
		}
	}

	/**
	 * @return Alle Klienten in einen Hash Map
	 */
	public HashMap <Integer, Client> getClients () {
		return mClients;
	}
}