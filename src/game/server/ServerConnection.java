package game.server;

import game.Log;
import game.map.GameMap;

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

	private HashMap <Integer, RoomConnection> mRooms = new HashMap <> ();
	private HashMap <Integer, Client> mClients = new HashMap <> ();
	private List <Client> mTemporaryClients = new ArrayList <> ();

	private boolean mIsRunning = false;
	private Log log = new Log (this);


	public ServerConnection () {
		super ("ServerConnection");
		mPort = 23232;
		try {
			mServerSocket = new ServerSocket (mPort);
		} catch (IOException e) {
			log.e ("ServerConnection inicializási bibi: " + e.getMessage ());
		}
		this.start ();
	}

	public void run () {
		log.i ("Sever started.");
		addRoom ("2DGAME", "map01.txt");
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

	private Serializable[] getRoomData () {
		int i = 0;
		Serializable[] roomData = new Serializable[mRooms.size () * 5];
		Iterator <HashMap.Entry <Integer, RoomConnection>> iterator = mRooms.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			RoomConnection connection = iterator.next ().getValue ();
			roomData[i++] = connection.getRoomId ();
			roomData[i++] = connection.getGameName ();
			roomData[i++] = connection.getMapName ();
			roomData[i++] = connection.getUserCount ();
			roomData[i++] = connection.getMaxUserCount ();
		}
		return roomData;
	}

	public void receive (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				int tempIndex = findClientByPort (currentPort);
				Client client = mTemporaryClients.get (tempIndex);
				int id = mClientIdCounter++;
				mClients.put (id, client);
				mTemporaryClients.remove (tempIndex);
				client.send (new Command (Command.Type.ACCEPT_CONNECTION, id));

				client.send (new Command (Command.Type.LIST_ROOMS, getRoomData ()));

				break;
			case EXIT_SERVER:
				log.i (command.data[0] + " exits the server... PORT:" + currentPort);
				mClients.remove (command.data[0]);
				mRooms.get (command.data[1]).removeConnection ((int) command.data[0]);

				break;
			case ENTER_ROOM:
				RoomConnection room = mRooms.get (command.data[2]);
				if (room != null && !room.isFull () && !room.isRunning ()) {
					log.i (command.data[0] + " is connecting to the room " + command.data[2]);
					sendToId ((int) command.data[0], new Command (Command.Type.MAP_DATA, room.getRoomId (), room.getGameMap ().toData ()));
					room.addConnection ((int) command.data[0]);
					if (room.isFull ()) {
						//						room.send (Command.Type.READY_TO_PLAY);
						room.start ();
					} else {
						//						room.send(); #TODO SEND ROOM DATAs +1 man
					}
				} else {
					log.i (command.data[0] + " is connecting to the room " + command.data[1] + ", but its full/running/not existing!");

					//					sendToId (Command.Type.)); #TODO CANNOT CONNECT TO ROOM
				}
				break;
			case LEAVE_ROOM:
				log.i (command.data[0] + " is leaving the room " + command.data[1]);
				mRooms.get (command.data[1]).removeConnection ((int) command.data[0]);
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
				log.i (command.data[0] + " in " + command.data[1] + " sent GAME_DATA " + command.data[2]);
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
		mClients.get (id).send (command);
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
			iterator.next ().getValue ().send (command);
		}
	}

}