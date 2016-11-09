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

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Logger mLogger = Logger.getLogger (ClientConnection.class.getCanonicalName ());

	public ClientConnection (String ip, String userName) throws IOException {
		super ("ClientConnection " + userName);
		mUserName = userName;
		mIsRunning = false;
		mSocket = new Socket (ip, 23233);//#TODO set port

		if (mSocket.isConnected ()) {
			mIn = new ObjectInputStream (mSocket.getInputStream ());
			mOut = new ObjectOutputStream (mSocket.getOutputStream ());
		}
		enterServer ();

		this.start ();
	}

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

	public void stopClientConnection () {
		mIsRunning = false;
		send (Command.Type.EXIT_SERVER);
		try {
			mSocket.close ();
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "Coulnd't close socket: " + ex.getMessage ());
		}
	}

	//RECEIVE
	private void connectionReady (int newConnectionId) {
		mConnectionId = newConnectionId;
		mUserInterface.connectionReady ();
		mLogger.log (Level.INFO, "-> Connected to the server. ID (" + mConnectionId + ")");
	}

	private void listRooms (ArrayList <RoomData> roomDatas) {
		mUserInterface.listRooms (roomDatas);
		mLogger.log (Level.INFO, "-> RoomDatas loaded. Size (" + roomDatas.size () + ")");
	}

	private void loadMap (int roomIndex, ArrayList <Serializable> mapData) {
		mRoomIndex = roomIndex;
		InterstellarWarCore core = new InterstellarWarCore (mapData);

		mGameClient = new InterstellarWarClient (core, this);
		mUserInterface.setIsInRoom (true);
		mLogger.log (Level.INFO, "-> MapData loaded. RoomIndex (" + mRoomIndex + ")");
	}

	private void startGame (String mapName) {
		mUserInterface.startGame (mapName);
		mGameClient.getCore ().start ();
		mLogger.log (Level.INFO, "-> Started the game with Map (" + mapName + ")");
	}

	private void gameCommand (Command command) {
		mGameClient.receive (command);
		mLogger.log (Level.INFO, "-> Received GameCommand (" + command + ")");
	}

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

	public int getRoomIndex () {
		return mRoomIndex;
	}

	//SEND
	public void enterServer () {
		send (Command.Type.ENTER_SERVER, mUserName);
		mLogger.log (Level.INFO, "<- Connecting to the server Username (" + mUserName + ")");
	}

	public void leaveRoom () {
		mUserInterface.stopGame ();
		send (Command.Type.LEAVE_ROOM);
		mUserInterface.setIsInRoom (false);
		mLogger.log (Level.INFO, "<- Leaving the Room");
	}

	public void enterRoom (int roomId) {
		send (Command.Type.ENTER_ROOM, roomId);
		mLogger.log (Level.INFO, "<- Entering the Room Id (" + roomId + ")");
	}

	public void startRoom () {
		send (Command.Type.START_ROOM);
		mLogger.log (Level.INFO, "<- Starting the Room");
	}

	public void send (Command.Type type) {
		send (new Command (type));
	}

	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	public void send (Command command) {
		command.addHeader (mConnectionId);
		try {
			if (!mSocket.isClosed ()) {
				mOut.writeObject (command);
			} else {
				mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "), because socket was closed.");
			}
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + ")" + ex.getMessage ());
		}
	}

	private void stopGame () {
		if (mGameClient != null && mGameClient.getCore ().isRunning ()) {
			mGameClient.getCore ().stopGame ();
		}
	}

	public InterstellarWarClient getGameClient () {
		return mGameClient;
	}

	public void setUserInterface (UserInterface userInterface) {
		mUserInterface = userInterface;
	}
}
