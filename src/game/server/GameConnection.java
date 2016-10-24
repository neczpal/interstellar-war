package game.server;

import game.Log;
import game.map.GameMap;
import game.ui.GameFrame;
import game.ui.OpenRoomsFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * @author neczpal
 */
public class GameConnection extends Thread implements Connection {

	private OpenRoomsFrame mOpenRoomsFrame;
	private GameFrame gameFrame;
	private GameMap mGameMap;

	private int mConnectionId;
	private int mRoomIndex;

	private String mUserName;

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Log log = new Log (this);

	public GameConnection (OpenRoomsFrame openRoomsFrame, String ip, String userName) {
		super ("GameConnection " + userName);
		mOpenRoomsFrame = openRoomsFrame;
		mUserName = userName;
		mIsRunning = false;
		try {
			mSocket = new Socket (ip, 23232);

			if (mSocket.isConnected ()) {
				mIn = new ObjectInputStream (mSocket.getInputStream ());
				mOut = new ObjectOutputStream (mSocket.getOutputStream ());
			}
			send (Command.Type.ENTER_SERVER, mUserName);

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

	public void stopGameConnection () {
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
		log.i ("Connection succesful id: " + mConnectionId);
	}

	private void listRooms (Serializable[] roomData) {
		mOpenRoomsFrame.loadRoomInfos (roomData);
		log.i ("Room datas loaded");
	}

	private void loadMap (int roomIndex, String gameName, Serializable[] mapData) {
		mRoomIndex = roomIndex;
		mGameMap = GameMap.createGameMap (gameName);
		mGameMap.setConnection (this);
		mGameMap.loadData (mapData);
		log.i ("Map data received");
	}

	private void startGame (String gameName, String mapName) {
		gameFrame = new GameFrame (gameName + " : " + mapName, 640, 480, mGameMap);
		gameFrame.start ();
		mGameMap.start ();
		mOpenRoomsFrame.setVisible (false);
		log.i (gameName + " (" + mapName + ") is ready to play.");
	}

	private void gameData (Command command) {
		mGameMap.receiveClient (command);
		log.i (command.data[0] + " game command received.");
	}

	public void receive (Command command) {
		log.i ("Got msg from server: " + command.type.toString ());

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
			case GAME_DATA:
				gameData (command);
		}
	}

	public int getRoomIndex () {
		return mRoomIndex;
	}

	//SEND
	public void leaveRoom () {
		send (Command.Type.LEAVE_ROOM);
		mOpenRoomsFrame.setVisible (true);
	}

	public void enterRoom (int roomId) {
		send (Command.Type.ENTER_ROOM, roomId);
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
}
