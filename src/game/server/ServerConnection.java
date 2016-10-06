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
	private GameMap mMap;
	private int mPort;
	private ServerSocket mServerSocket;
	private HashMap <Integer, Client> mClients = new HashMap <> ();
	private List <Client> mTemporaryClients = new ArrayList <> ();
	private boolean running = false;
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
		mMap = new GameMap ();
		running = true;
		while (running) {
			try {
				addClient ();
			} catch (IOException e) {
				log.e ("ServerConnection futási bibi: " + e.getMessage ());
			}
		}
	}

	public void stopServer () {
		running = false;
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

	public void action (Command command, int currentPort) {
		log.i ("Got msg from client: " + command.type.toString () + " : " + currentPort);
		switch (command.type) {
			case ENTER_SERVER:
				int id = mClientIdCounter++;
				mClients.put (id, mTemporaryClients.get (findClientByPort (currentPort)));
				mClients.get (id).setClientId (id);
				mTemporaryClients.remove (findClientByPort (currentPort));

				log.i (currentPort + " is connecting to the server, got id:" + id);

				Player newPlayer = new Player (id);
				mMap.addPlayer (id, newPlayer);
				sendToId (Command.Type.ACCEPT_CONNECTION, new Serializable[] {id}, id);

				break;
			case EXIT_SERVER:

				log.i (command.data[0] + " exits the server... PORT:" + currentPort);
				mClients.remove (command.data[0]);

				break;
			case MOVE:

				Player movingPlayer = mMap.findPlayerById ((int) command.data[0]);
				movingPlayer.move ((int) command.data[1], (int) command.data[2]);
				log.i (movingPlayer.getId () + " is moving, sending the Map...");
				Integer[] player_data = new Integer[mMap.getPlayers ().size () * 3 + 1];
				int i = 0;
				player_data[i++] = mMap.getPlayers ().size ();
				for (Player player : mMap.getPlayers ()) {
					player_data[i++] = player.getId ();
					player_data[i++] = player.getX ();
					player_data[i++] = player.getY ();
					log.i ("PLAYER (" + player.getId () + ") position : " + player.getX () + ", " + player.getY ());
				}
				sendToAll (Command.Type.SYNC_MAP, player_data);

				break;
			case NEED_SYNC:

				log.i (currentPort + " needs sync, sending the MapData");
				sendToId (Command.Type.SYNC_MAP, new Serializable[] {mMap.getPlayers ()}, (int) command.data[0]);

				break;
		}
	}

	public void sendToId (Command.Type type, Serializable[] data, int id) {
		sendToId (new Command (type, data), id);
	}

	public void sendToId (Command cmd, int id) {
		mClients.get (id).send (cmd);
	}

	public void sendToAll (Command.Type type) {
		sendToAll (new Command (type));
	}

	public void sendToAll (Command.Type type, Serializable[] data) {
		sendToAll (new Command (type, data));
	}

	public void sendToAll (Command command) {
		Iterator <HashMap.Entry <Integer, Client>> iterator = mClients.entrySet ().iterator ();
		while (iterator.hasNext ()) {
			iterator.next ().getValue ().send (command);
		}
	}

}