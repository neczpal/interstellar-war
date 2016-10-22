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

	private HashMap <Integer, Integer> mUserToRoomHashMap = new HashMap <> ();

	private HashMap <Integer, RoomConnection> mRooms = new HashMap <> ();
	private RoomServer mRoomServer;

	private HashMap <Integer, Client> mClients = new HashMap <> ();
	private List <Client> mTemporaryClients = new ArrayList <> ();

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
			RoomConnection roomConnection = new RoomConnection (this, gameName, mapName);
			roomConnection.setRoomId (mRoomIdCounter);
			mRooms.put (mRoomIdCounter++, roomConnection);
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
		leaveRoom (id, mUserToRoomHashMap.get (id));
		mUserToRoomHashMap.remove (id);

		log.i ("User (" + id + ") disconnected from the server");
		mClients.remove (id);
	}

	public Serializable[] getRoomData () {
		int i = 0;
		Serializable[] roomData = new Serializable[mRooms.size () * 5];
		Iterator <HashMap.Entry <Integer, RoomConnection>> iterator = mRooms.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			RoomConnection connection = iterator.next ().getValue ();
			roomData[i++] = connection.getRoomId ();
			roomData[i++] = connection.getGameName ();
			roomData[i++] = connection.getMapFantasyName ();
			roomData[i++] = connection.getUserCount ();
			roomData[i++] = connection.getMaxUserCount ();
		}
		return roomData;
	}

	private void enterServer (Command command, int port) {
		int tempIndex = findClientByPort (port);
		Client client = mTemporaryClients.get (tempIndex);
		int id = mClientIdCounter++;
		mClients.put (id, client);
		mTemporaryClients.remove (tempIndex);
		mUserToRoomHashMap.put (id, 0);
		client.send (new Command (Command.Type.ACCEPT_CONNECTION, id));
	}

	private void exitServer (Command command) {
		log.i ("User (" + command.data[0] + ") exits the server.");
		mClients.remove (command.data[0]);
		mRooms.get (command.data[1]).removeConnection ((int) command.data[0]);
	}

	private void leaveRoom (int userId, int roomId) {
		if (roomId != 0) {
			log.i ("User (" + userId + ") is leaving the Room (" + roomId + ")");
			mUserToRoomHashMap.put (userId, 0);
			RoomConnection leaveRoom = mRooms.get (roomId);
			leaveRoom.removeConnection (userId);
			if (leaveRoom.isEmpty () && leaveRoom.isRunning ()) {
				leaveRoom.stopGame ();
				mRooms.remove (leaveRoom.getRoomId ());
			}
		} else {
			log.i ("User (" + userId + ") is not in a room");
		}
	}

	private void enterRoom (Command command) {
		leaveRoom ((int) command.data[0], (int) command.data[1]);
		RoomConnection room = mRooms.get (command.data[2]);
		if (room != null && !room.isFull () && !room.isRunning ()) {
			log.i ("User (" + command.data[0] + ") is connecting to the Room (" + command.data[2] + ")");
			mUserToRoomHashMap.put ((int) command.data[0], (int) command.data[2]);
			room.addConnection ((int) command.data[0]);
			sendToId ((int) command.data[0], new Command (Command.Type.MAP_DATA, room.getRoomId (), room.getConnectionIndex ((int) command.data[0]), room.getGameName (), room.getGameMap ().toData ()));
			if (room.isFull ()) {
				room.send (Command.Type.READY_TO_PLAY, room.getGameName (), room.getMapFantasyName ());
				room.start ();
				addRoom (room.getGameName (), room.getMapName ());
			} else {
				//				send (Command.Type.LIST_ROOMS, getRoomData ());
			}
		} else {
			log.i (command.data[0] + " is connecting to the Room (" + command.data[2] + "), but its full/running/not existing!");

			//					sendToId (Command.Type.)); #TODO CANNOT CONNECT TO ROOM
		}
	}

	public void receive (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				enterServer (command, currentPort);
				break;
			case EXIT_SERVER:
				exitServer (command);
				break;
			case LEAVE_ROOM:
				leaveRoom ((int) command.data[0], (int) command.data[1]);
				break;
			case ENTER_ROOM:
				enterRoom (command);
				break;
			case READY_TO_PLAY:
				//				log.i (command.data[0] + " is ready to play!");
				//				mMap.setUserReady ((int) command.data[0]);
				//				send (Command.Type.IS_READY, command.data[0]);
				//				if (mMap.isMapReady ()) {
				//					mMap.setConnection (this);
				//					mMap.start ();
				//				}
				break;
			case GAME_DATA:
				log.i ("User (" + command.data[0] + ") in the Room (" + command.data[1] + ") sent GAME_DATA " + command.data[2]);
				mRooms.get (command.data[1]).getGameMap ().receiveServer (command);
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