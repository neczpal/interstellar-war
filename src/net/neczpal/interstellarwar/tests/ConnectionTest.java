package net.neczpal.interstellarwar.tests;


import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.server.ServerConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ConnectionTest {

	private ServerConnection mServerConnection;
	private ClientConnection mClientConnection;
	private NoUserInterface mNoUserInterface = new NoUserInterface ();

	@Before
	public void setUp () throws Exception {
		sleep (100); // WAIT UNTIL SOCKET READY
		mServerConnection = new ServerConnection (23232);
		mClientConnection = new ClientConnection ("localhost:23232", "testUser1");
		mClientConnection.setUserInterface (mNoUserInterface);
		sleep (100); // WAIT UNTIL CONNECTION READY
	}

	@After
	public void tearDown () throws Exception {
		mClientConnection.stopClientConnection ();
		mServerConnection.stopServerConnection ();
	}

	@Test
	public void testLogin () {
		Assert.assertEquals ("User is connected", "testUser1", mServerConnection.getUser (1).getName ());
	}

	@Test
	public void testEnterRoom () {
		mClientConnection.enterRoom (3);
		sleep (100);
		Assert.assertEquals ("User enters a Room", 3, mServerConnection.getUser (1).getRoomId ());
	}

	@Test
	public void testExit () throws Exception {
		mClientConnection.exitServer ();
		sleep (100);//WAIT UNTIL COMMAND RECEIVED
		Assert.assertNull ("User exits the Server", mServerConnection.getUser (1));
	}

	@Test
	public void testStartRoomNotFull () throws Exception {
		mClientConnection.enterRoom (3);
		mClientConnection.startRoom ();
		sleep (100);//WAIT UNTIL COMMAND RECEIVED
		Assert.assertFalse ("Room is not started because room is not full", mServerConnection.getRoom (3).isMapRunning ());
	}

	@Test
	public void testStartRoom () throws Exception {
		mClientConnection.enterRoom (3);

		ClientConnection clientConnection2 = new ClientConnection ("localhost:23232", "testUser2");
		clientConnection2.setUserInterface (mNoUserInterface);
		sleep (100);//WAIT UNTIL SECOND CLIENT CONNECT

		clientConnection2.enterRoom (3);
		sleep (100);//WAIT UNTIL SECOND CLIENT ENTERS THE ROOM

		mClientConnection.startRoom ();
		sleep (100);//WAIT UNTIL COMMAND RECEIVED
		Assert.assertTrue ("Room is started", mServerConnection.getRoom (3).isMapRunning ());

		mServerConnection.getRoom (3).stopGame ();
	}

	@Test
	public void testLeaveRoom () throws Exception {
		mClientConnection.enterRoom (3);
		sleep (100);//WAIT UNTIL COMMAND RECEIVED
		mClientConnection.leaveRoom ();
		sleep (100);//WAIT UNTIL COMMAND RECEIVED
		Assert.assertEquals ("User left Room, got roomId 0.", 0, mServerConnection.getUser (1).getRoomId ());
	}
}
