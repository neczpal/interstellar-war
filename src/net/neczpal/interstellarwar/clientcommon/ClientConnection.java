package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.connection.CommandParamKey;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.neczpal.interstellarwar.common.connection.CommandParamKey.*;
import static net.neczpal.interstellarwar.common.connection.CommandType.*;

public class ClientConnection extends Thread {

	private UserInterface mUserInterface;
	private InterstellarWarClient mGameClient;

	private int mConnectionId;
	private int mRoomIndex;

	private String mUserName;

	private volatile boolean mIsRunning;
	private Socket mSocket;

	private BufferedReader mIn;
	private BufferedWriter mOut;

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
			mOut = new BufferedWriter (new OutputStreamWriter (mSocket.getOutputStream (), StandardCharsets.UTF_8));
			mIn = new BufferedReader (new InputStreamReader (mSocket.getInputStream (), StandardCharsets.UTF_8));
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
					String line = mIn.readLine ();

					if (line != null) {
						mLogger.log (Level.INFO, "Read line: " + line);
						JSONObject jsonObject = new JSONObject (line);
						receive (jsonObject);
					}
				} catch (JSONException ex) {
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
	 * @param allRoomData Die Zimmerdata
	 */
	private void listRooms (JSONArray allRoomData) {
		mUserInterface.listRooms (allRoomData);
		mLogger.log (Level.INFO, "-> RoomDatas loaded. Size (" + allRoomData.length () + ")");
	}

	/**
	 * Ladet die Mappe ein
	 *
	 * @param roomIndex Die Benutzerindex in dem Zimmer
	 * @param mapData   Die Mapdata
	 */
	private void loadMap (int roomIndex, JSONArray mapData) {
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
	private void gameCommand (JSONObject command) {
		mGameClient.receive (command);
		mLogger.log (Level.INFO, "-> Received GameCommand (" + command + ")");
	}

	/**
	 * Bekommt ein Befehl
	 *
	 * @param command Der Befehl
	 */
	public void receive (JSONObject command) {
		String type = command.getString (CommandParamKey.COMMAND_TYPE_KEY);

		switch (type) {
			case CONNECTION_READY: {
				Integer userId = command.getInt (CommandParamKey.USER_ID_KEY);
				connectionReady (userId);
				break;
			}
			case LIST_ROOMS: {
				JSONArray allRoomData = command.getJSONArray (ALL_ROOM_DATA_KEY);
				listRooms (allRoomData);
				break;
			}
			case GET_MAP_DATA: {
				Integer userId = command.getInt (USER_ID_KEY);
				JSONArray mapData = command.getJSONArray (MAP_DATA_KEY);
				loadMap (userId, mapData);
				break;
			}
			case READY_TO_PLAY: {
				String mapName = command.getString (MAP_NAME_KEY);
				startGame (mapName);
				break;
			}
			case GAME_COMMAND: {
				gameCommand (command);
				break;
			}
		}
	}

	//SEND

	/**
	 * Eintritt in dem Server
	 */
	public void enterServer () {
		JSONObject command = new JSONObject ();
		command.put (COMMAND_TYPE_KEY, ENTER_SERVER);
		command.put (USER_NAME_KEY, mUserName);

		send (command);

		mLogger.log (Level.INFO, "<- Connecting to the server Username (" + mUserName + ")");
	}

	/**
	 * Verlasst dem Server
	 */
	public void exitServer () {
		send (EXIT_SERVER);
	}

	/**
	 * Eintitt in das Zimmer
	 *
	 * @param roomId Die Zimmer-ID
	 */
	public void enterRoom (int roomId) {
		JSONObject command = new JSONObject ();
		command.put (COMMAND_TYPE_KEY, ENTER_ROOM);
		command.put (ROOM_ID_KEY, roomId);

		send (command);

		mLogger.log (Level.INFO, "<- Entering the Room Id (" + roomId + ")");
	}

	/**
	 * Beginnt das Spiel in dem Zimmer
	 */
	public void startRoom () {
		send (START_ROOM);
		mLogger.log (Level.INFO, "<- Starting the Room");
	}

	/**
	 * Verlasst das Zimmer
	 */
	public void leaveRoom () {
		mUserInterface.stopGame ();

		send (LEAVE_ROOM);

		mUserInterface.setIsInRoom (false);
		mLogger.log (Level.INFO, "<- Leaving the Room");
	}

	/**
	 * Sendet ein Befehl
	 *
	 * @param type Typ des Befehls
	 */
	public void send (String type) {
		JSONObject command = new JSONObject ();
		command.put (COMMAND_TYPE_KEY, type);

		send (command);
	}

	/**
	 * Sendet ein Befehl
	 *
	 * @param command Der Befehl
	 */
	public void send (JSONObject command) {
		command.put (USER_ID_KEY, mConnectionId);

		try {
			if (!mSocket.isClosed ()) {
				mOut.write (command.toString () + "\n");
				mOut.flush ();
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
}
