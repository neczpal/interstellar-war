package game.server;

import game.Log;
import game.map.GameMap;
import game.map.GameMap2D;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Beszélget a szerverrel
 *
 * @author neczpal
 */
public class GameConnection extends Thread implements Connection {

	private GameMap mMap;
	private int mConnectionId;
	private int mRoomConnectionId;
	private int mRoomIndex;
	private String mUserName;

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Log log = new Log (this);

	public GameConnection (String ip, String userName) {
		super ("GameConnection " + userName);
		mUserName = userName;
		mIsRunning = false;
		mMap = new GameMap2D ();
		mMap.setConnection (this);
		try {
			mSocket = new Socket (ip, 23232);

			if (mSocket.isConnected ()) {
				mIn = new ObjectInputStream (mSocket.getInputStream ());
				mOut = new ObjectOutputStream (mSocket.getOutputStream ());
			}
			send (Command.Type.ENTER_SERVER);

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
					Command msg = (Command) mIn.readObject ();
					receive (msg);
				} catch (ClassNotFoundException | ClassCastException ex) {
					log.e (ex.getMessage ());
				}
			}
		} catch (IOException e) {

		}
	}

	public void close () {
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

	/**
	 * Servernek üzenet küldés
	 *
	 * @param type
	 */
	public void send (Command.Type type) {
		send (new Command (type));
	}

	public void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	public void send (Command command) {
		command.addHeader (mConnectionId, mRoomConnectionId);
		try {
			mOut.writeObject (command);
		} catch (IOException e) {
			log.e ("Nem sikerült elküldeni:" + command);
		}
	}

	/**
	 * Servertől kapott üzenet feldolgozása
	 *
	 * @param command
	 */
	public void receive (Command command) {
		log.i ("Got msg from server: " + command.type.toString ());

		switch (command.type) {
			case ACCEPT_CONNECTION:
				mConnectionId = (int) command.data[0];
				log.i ("Connection succesful id: " + mConnectionId);

				send (Command.Type.ENTER_ROOM, 1);// BELEP AZ 1. szobaba
				break;
			case DECLINE_CONNECTION:
				log.i ("Connection failed!");
				break;
			case MAP_DATA:
				log.i ("Map data received");
				mRoomConnectionId = (int) command.data[0];
				mRoomIndex = (int) command.data[1];
				mMap.loadData ((Serializable[]) command.data[2]);
				break;
			case IS_READY:
				log.i (command.data[0] + " is ready to play.");
				//				mMap.addUser (command.data[0], new User ());
				//				mMap.setUserReady ((int) command.data[0]);
				break;
			case GAME_DATA:
				log.i (command.data[0] + " game command received.");
				mMap.receiveClient (command);
		}
	}

	public int getConnectionId () {
		return mConnectionId;
	}

	public int getRoomIndex () {
		return mRoomIndex;
	}

	public GameMap getGameMap () {
		return mMap;
	}

	private void setGameMap (GameMap map) {
		mMap = map;
	}


}
