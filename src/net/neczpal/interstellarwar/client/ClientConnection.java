package net.neczpal.interstellarwar.client;

import net.neczpal.interstellarwar.common.Command;
import net.neczpal.interstellarwar.common.InterstellarWarCore;
import net.neczpal.interstellarwar.common.Log;
import net.neczpal.interstellarwar.common.RoomData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

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

	private Log log = new Log (this);

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
						log.w (object + " was not a Command");
					}
				} catch (ClassNotFoundException | ClassCastException ex) {
					log.e (ex.getMessage ());
					ex.printStackTrace ();
				}
			}
		} catch (IOException e) {
			log.e ("gameconnection bibi" + e.getMessage ());
			stopClientConnection ();
			stopGame ();
			mUserInterface.connectionDropped ();
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
		mUserInterface.connectionReady ();
		log.i ("Connection succesful id: " + mConnectionId);
	}

	private void listRooms (Serializable[] roomData) {
		mUserInterface.listRooms ((RoomData[]) roomData);
		log.i ("Room datas loaded");
	}

	private void loadMap (int roomIndex, ArrayList <Serializable> mapData) {
		mRoomIndex = roomIndex;
		InterstellarWarCore core = new InterstellarWarCore (mapData);

		mGameClient = new InterstellarWarClient (core, this);
		mUserInterface.setIsInRoom (true);
		log.i ("Map data received");
	}

	private void startGame (String mapName) {
		mUserInterface.startGame (mapName);
		mGameClient.getCore ().start ();
		log.i (" (" + mapName + ") is ready to play.");
	}

	private void gameCommand (Command command) {
		mGameClient.receive (command);
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
	}

	public void leaveRoom () {
		mUserInterface.stopGame ();
		send (Command.Type.LEAVE_ROOM);
		mUserInterface.setIsInRoom (false);
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
