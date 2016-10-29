package game.connection;

public class RoomServer extends Thread {

	ServerConnection mServerConnection;
	private boolean mIsRunning;

	public RoomServer (ServerConnection serverConnection) {
		this.mServerConnection = serverConnection;
		mIsRunning = false;
	}

	public void run () {
		mIsRunning = true;
		while (mIsRunning) {
			try {
				Thread.sleep (1000);
				mServerConnection.listRoom ();
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}
	}

	public void stopRoomServer () {
		mIsRunning = false;
	}
}
