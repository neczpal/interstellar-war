package game.server;

import game.Log;
import game.map.GameMap;
import game.map.GameMap2D;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServerConnection extends Thread implements Connection {

	private static int mClientIdCounter = 1;
	private GameMap mMap;
	private int mPort;
	private ServerSocket mServerSocket;
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
		mMap = new GameMap2D ();
		mMap.setConnection (this);
		try {
			mMap.loadMap ("map01.txt");
		} catch (GameMap2D.NotValidMapException e) {
			log.e (e.getMessage ());
		}
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

	public void receive (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				int tempIndex = findClientByPort (currentPort);
				Client client = mTemporaryClients.get (tempIndex);
				if (mMap.isFull ()) {
					log.i (currentPort + " is connecting to the server, but its full!");
					client.send (new Command (Command.Type.DECLINE_CONNECTION));
				} else {
					int id = mClientIdCounter++;
					mClients.put (id, client);
					mTemporaryClients.remove (tempIndex);

					log.i (currentPort + " is connecting to the server, got id:" + id);

					User newUser = new User ((String) command.data[1], id);
					mMap.addUser (id, newUser);

					client.send (new Command (Command.Type.ACCEPT_CONNECTION, id, mMap.toData ()));
				}
				break;
			case EXIT_SERVER:

				log.i (command.data[0] + " exits the server... PORT:" + currentPort);
				mClients.remove (command.data[0]);
				mMap.removeUser ((int) command.data[0]);

				break;
			case READY_TO_PLAY:
				log.i (command.data[0] + " is ready to play!");
				mMap.setUserReady ((int) command.data[0]);
				send (Command.Type.IS_READY, command.data[0]);
				if (mMap.isMapReady ()) {
					mMap.setConnection (this);
					mMap.start ();
				}
				break;
			case GAME_DATA:
				mMap.receiveServer (command);
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