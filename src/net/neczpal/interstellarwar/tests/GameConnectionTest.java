package net.neczpal.interstellarwar.tests;

import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.server.InterstellarWarServer;
import net.neczpal.interstellarwar.server.ServerConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class GameConnectionTest {
	private ServerConnection mServerConnection;

	private ClientConnection mClientConnection1;
	private ClientConnection mClientConnection2;

	private InterstellarWarServer mInterstellarWarServer;

	private InterstellarWarClient mInterstellarWarClient1;
	private InterstellarWarClient mInterstellarWarClient2;
	private NoUserInterface mNoUserInterface = new NoUserInterface ();

	@Before
	public void setUp () throws Exception {
		sleep (100); // WAIT UNTIL SOCKET READY
		mServerConnection = new ServerConnection (23231);
		sleep (100);//WAIT FOR SERVER

		mClientConnection1 = new ClientConnection ("localhost:23231", "testUser1");
		mClientConnection1.setUserInterface (mNoUserInterface);

		mClientConnection2 = new ClientConnection ("localhost:23231", "testUser2");
		mClientConnection2.setUserInterface (mNoUserInterface);

		sleep (100); // WAIT UNTIL CONNECTION READY

		mClientConnection1.enterRoom (1);
		mClientConnection2.enterRoom (1);

		sleep (100);//WAIT FOR COMMAND

		mClientConnection1.startRoom ();
		sleep (100);//WAIT FOR COMMAND

		mInterstellarWarClient1 = mClientConnection1.getGameClient ();
		mInterstellarWarClient2 = mClientConnection2.getGameClient ();

		mInterstellarWarServer = mServerConnection.getRoom (1).getGameServer ();

	}

	@After
	public void tearDown () throws Exception {
		mInterstellarWarClient1.getCore ().stopGame ();
		mInterstellarWarClient1.getCore ().stopGame ();

		mClientConnection1.stopClientConnection ();
		mClientConnection2.stopClientConnection ();

		mServerConnection.stopServerConnection ();
	}

	@Test
	public void testMoveUnits () throws Exception {
		mInterstellarWarClient1.startMoveSpaceShip (0, 1, mInterstellarWarClient1.getCore ().getTickNumber (), 5);
		Assert.assertFalse ("A spaceship is not created until the server not aproves it", mInterstellarWarClient1.getCore ().getSpaceShips ().size () > 0);
		sleep (1000); //WAIT UNTIL SERVER RECEIVES
		Assert.assertTrue ("A spaceship is created on the server", mInterstellarWarServer.getCore ().getSpaceShips ().size () > 0);
		sleep (100); //WAIT UNTIL SERVER SENDS IT TO ALL CLIENT
		Assert.assertTrue ("A spaceship is created on the one client", mInterstellarWarClient1.getCore ().getSpaceShips ().size () > 0);
		Assert.assertTrue ("A spaceship is created on the other client", mInterstellarWarClient2.getCore ().getSpaceShips ().size () > 0);
	}

}
