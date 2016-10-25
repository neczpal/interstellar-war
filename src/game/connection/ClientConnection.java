package game.connection;

import game.Log;
import game.map.GameMap;
import game.ui.GameFrame;
import game.ui.LoginFrame;
import game.ui.RoomsFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * @author neczpal
 */
public class ClientConnection extends Thread implements Connection {

	private LoginFrame mLoginFrame;
	private RoomsFrame mRoomsFrame;
	private GameFrame mGameFrame;
	private GameMap mGameMap;

	private int mConnectionId;
	private int mRoomIndex;

	private String mUserName;

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Log log = new Log (this);

	public ClientConnection (String ip, String userName) {
		super ("ClientConnection " + userName);
		mUserName = userName;
		mIsRunning = false;
		try {
			mSocket = new Socket (ip, 23232);//#TODO set port

			if (mSocket.isConnected ()) {
				mIn = new ObjectInputStream (mSocket.getInputStream ());
				mOut = new ObjectOutputStream (mSocket.getOutputStream ());
			}
			enterServer ();

			this.start ();
		} catch (IOException e) {
			log.e ("Hibás ip cím: " + ip);
		}
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
						log.w (object + " was not a Command");
					}
				} catch (ClassNotFoundException | ClassCastException ex) {
					log.e (ex.getMessage ());
					ex.printStackTrace ();
				}
			}
		} catch (IOException e) {
			log.e ("gameconnection bibi" + e.getMessage ());
		}
	}

	public void stopClientConnection () {
		mIsRunning = false;
		send (Command.Type.EXIT_SERVER);
		try {
			mOut.close ();
			mIn.close ();
			mSocket.close ();
		} catch (IOException e) {
			log.e (e.getMessage ());
		}
	}


	//RECEIVE
	private void connectionReady (int newConnectionId) {
		mConnectionId = newConnectionId;
		mLoginFrame.openRoomsFrame ();
		log.i ("Connection succesful id: " + mConnectionId);
	}

	private void listRooms (Serializable[] roomData) {
		mRoomsFrame.loadRoomData ((RoomData[]) roomData);
		log.i ("Room datas loaded");
	}

	private void loadMap (int roomIndex, String gameName, Serializable[] mapData) {
		mRoomIndex = roomIndex;
		mGameMap = GameMap.createGameMap (gameName);
		mGameMap.setConnection (this);
		mGameMap.loadData (mapData);
		mRoomsFrame.setIsInRoom (true);
		log.i ("Map data received");
	}

	private void startGame (String gameName, String mapName) {
		mGameFrame = new GameFrame (gameName + " : " + mapName, mLoginFrame.getSelectedDisplayModeIndex (), mGameMap);
		mGameFrame.start ();
		mGameMap.start ();
		log.i (gameName + " (" + mapName + ") is ready to play.");
	}

	private void gameCommand (Command command) {
		mGameMap.receiveClient (command);
		log.i (command.data[0] + " game command received.");
	}

	public void receive (Command command) {
		log.i ("Got msg from connection: " + command.type.toString ());

		switch (command.type) {
			case CONNECTION_READY:
				connectionReady ((int) command.data[0]);
				break;
			case LIST_ROOMS:
				listRooms (command.data);
				break;
			case MAP_DATA:
				loadMap ((int) command.data[0], (String) command.data[1], (Serializable[]) command.data[2]);
				break;
			case READY_TO_PLAY:
				startGame ((String) command.data[0], (String) command.data[1]);
				break;
			case GAME_COMMAND:
				gameCommand (command);
		}
	}

	public int getRoomIndex () {
		return mRoomIndex;
	}

	//SEND
	public void enterServer () {
		send (Command.Type.ENTER_SERVER, mUserName);
	}

	public void leaveRoom () {
		send (Command.Type.LEAVE_ROOM);
		mRoomsFrame.setIsInRoom (false);
	}

	public void enterRoom (int roomId) {
		send (Command.Type.ENTER_ROOM, roomId);
	}

	public void startRoom () {
		send (Command.Type.START_ROOM);
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
			mOut.writeObject (command);
		} catch (IOException e) {
			log.e ("Nem sikerült elküldeni:" + command);
		}
	}

	public void setLoginFrame (LoginFrame loginFrame) {
		mLoginFrame = loginFrame;
	}

	public void setRoomsFrame (RoomsFrame roomsFrame) {
		mRoomsFrame = roomsFrame;
	}

	public void setGameFrame (GameFrame gameFrame) {
		mGameFrame = gameFrame;
	}
}
