package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.connection.Command;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection extends Thread {

	private UserInterface mUserInterface;
	private InterstellarWarClient mGameClient;



	private int mConnectionId;
	private int mRoomIndex;

	private String mUserName;

	private volatile boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Logger mLogger = Logger.getLogger (ClientConnection.class.getCanonicalName ());

	/**
	 * Erstellt eine Verbindung mit dem Server
	 *
	 * @param adresse  IP-Adresse des Servers mit Port
	 * @param userName Benutzername der Klient
	 * @throws IOException falls die Verbindung kann nicht aufbauen
	 */
	public ClientConnection (String adresse, String userName) throws IOException {
		super ("ClientConnection " + userName);
		mUserName = userName;
		mIsRunning = false;
		String[] ipAndPort = adresse.split (":");

		mSocket = (ipAndPort.length > 1) ? new Socket (ipAndPort[0], Integer.parseInt (ipAndPort[1])) : new Socket (ipAndPort[0], 23233);

		if (mSocket.isConnected ()) {
			mIn = new ObjectInputStream (mSocket.getInputStream ());
			mOut = new ObjectOutputStream (mSocket.getOutputStream ());
		}
		enterServer ();

		this.start ();
	}

	/**
	 * Lest die Befehle, die Server geschickt hat
	 */
	@Override
	public void run () {
		mIsRunning = true;

		try {
			while (mIsRunning) {
				try {
					Object object = mIn.readObject ();
					if (object instanceof Command) {
						Command msg = (Command) object;
						receive (msg);
					} else {
						mLogger.log (Level.WARNING, "-> Couldn't read " + object + ", because it wasn't a " + Command.class.getSimpleName ());
					}
				} catch (ClassNotFoundException ex) {
					mLogger.log (Level.SEVERE, "-> Couldn't read an object: " + ex.getMessage ());
				}
			}
		} catch (IOException ex2) {
			mLogger.log (Level.WARNING, "Client stopped: " + ex2.getMessage ());

			stopClientConnection ();
			stopGame ();
			mUserInterface.connectionDropped ();
		}
	}

	/**
	 * Abbaut die Verbindung
	 */
	public void stopClientConnection () {
		mIsRunning = false;
		exitServer ();
		try {
			mSocket.close ();
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "Coulnd't close socket: " + ex.getMessage ());
		}
	}

	//RECEIVE

	/**
	 * Die Verbindung ist aufgebaut
	 *
	 * @param newConnectionId die bekommte Verbindung-ID
	 */
	private void connectionReady (int newConnectionId) {
		mConnectionId = newConnectionId;
		mUserInterface.connectionReady ();
		mLogger.log (Level.INFO, "-> Connected to the server. ID (" + mConnectionId + ")");
	}

	/**
	 * Die Zimmerdata sind bekommen.
	 *
	 * @param roomDatas Die Zimmerdata
	 */
	private void listRooms (ArrayList <RoomData> roomDatas) {
		mUserInterface.listRooms (roomDatas);
		mLogger.log (Level.INFO, "-> RoomDatas loaded. Size (" + roomDatas.size () + ")");
	}

	/**
	 * Ladet die Mappe ein
	 *
	 * @param roomIndex Die Benutzerindex in dem Zimmer
	 * @param mapData   Die Mapdata
	 */
	private void loadMap (int roomIndex, ArrayList <Serializable> mapData) {
		mRoomIndex = roomIndex;
		InterstellarWarCore core = new InterstellarWarCore (mapData);
		mGameClient = new InterstellarWarClient (core, this);
		mUserInterface.setIsInRoom (true);
		mLogger.log (Level.INFO, "-> MapData loaded. RoomIndex (" + mRoomIndex + ")");
	}

	/**
	 * Startet das Spiel
	 *
	 * @param mapName Der Name der Mappe
	 */
	private void startGame (String mapName) {
		mUserInterface.startGame (mapName);
		mGameClient.getCore ().start ();
		mLogger.log (Level.INFO, "-> Started the game with Map (" + mapName + ")");
	}

	/**
	 * Bekommt ein Spielbefehl
	 *
	 * @param command Der Spielbefehl
	 */
	private void gameCommand (Command command) {
		mGameClient.receive (command);
		mLogger.log (Level.INFO, "-> Received GameCommand (" + command + ")");
	}

	/**
	 * Bekommt ein Befehl
	 *
	 * @param command Der Befehl
	 */
	public void receive (Command command) {
		switch (command.type) {
			case CONNECTION_READY:
				connectionReady ((int) command.data[0]);
				break;
			case LIST_ROOMS:
				listRooms ((ArrayList <RoomData>) command.data[0]);
				break;
			case MAP_DATA:
				loadMap ((int) command.data[0], (ArrayList <Serializable>) command.data[1]);
				break;
			case READY_TO_PLAY:
				startGame ((String) command.data[0]);
				break;
			case GAME_COMMAND:
				gameCommand (command);
				break;
		}
	}

	//SEND

	/**
	 * Eintritt in dem Server
	 */
	public void enterServer () {
		send (Command.Type.ENTER_SERVER, mUserName);
		mLogger.log (Level.INFO, "<- Connecting to the server Username (" + mUserName + ")");
	}

	/**
	 * Verlasst dem Server
	 */
	public void exitServer () {
		send (Command.Type.EXIT_SERVER);
	}

	/**
	 * Eintitt in das Zimmer
	 *
	 * @param roomId Die Zimmer-ID
	 */
	public void enterRoom (int roomId) {
		send (Command.Type.ENTER_ROOM, roomId);
		mLogger.log (Level.INFO, "<- Entering the Room Id (" + roomId + ")");
	}

	/**
	 * Beginnt das Spiel in dem Zimmer
	 */
	public void startRoom () {
		send (Command.Type.START_ROOM);
		mLogger.log (Level.INFO, "<- Starting the Room");
	}

	/**
	 * Verlasst das Zimmer
	 */
	public void leaveRoom () {
		mUserInterface.stopGame ();
		send (Command.Type.LEAVE_ROOM);
		mUserInterface.setIsInRoom (false);
		mLogger.log (Level.INFO, "<- Leaving the Room");
	}

	/**
	 * Sendet ein Befehl
	 *
	 * @param type Typ des Befehls
	 */
	public void send (Command.Type type) {
		send (new Command (type));
	}

	/**
	 * Sendet ein Befehl
	 *
	 * @param type Der Typ des Befehls
	 * @param data Der Data des Befehls
	 */
	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	/**
	 * Sendet ein Befehl
	 *
	 * @param command Der Befehl
	 */
	public void send (Command command) {
		command.addHeader (mConnectionId);
		try {
			if (!mSocket.isClosed ()) {
				mOut.writeObject (command);
			} else {
				mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "), because socket was closed.");
			}
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "): " + ex.getMessage ());
		}
	}

	/**
	 * Beendet das Spiel, falls es läuft
	 */
	private void stopGame () {
		if (mGameClient != null && mGameClient.getCore ().isRunning ()) {
			mGameClient.getCore ().stopGame ();
		}
	}

	//GETTERS, SETTERS

	public int getRoomIndex () {
		return mRoomIndex;
	}

	public InterstellarWarClient getGameClient () {
		return mGameClient;
	}

	public void setUserInterface (UserInterface userInterface) {
		mUserInterface = userInterface;
	}

	public int getConnectionId() {
		return mConnectionId;
	}
}
