package game.ui;

import game.server.GameConnection;
import org.lwjgl.input.Mouse;

/**
 * @author neczpal
 */
public class Panel {
	GameConnection mConnection;

	public Panel () {
		mConnection = new GameConnection ("localhost", "petike");
	}

	public void draw () {
		mConnection.getGameMap ().draw ();
	}

	public void mouseEvent () {
		if (Mouse.isInsideWindow ()) {
			mConnection.getGameMap ().mouseEvent ();
		}
	}

	public void keyboardEvent () {
		mConnection.getGameMap ().keyboardEvent ();
	}

}
