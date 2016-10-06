package game.ui;

import game.graphics.Rect;
import game.server.GameConnection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author neczpal
 */
public class Panel {
	GameConnection mConnection;

	public Panel () {
		mConnection = new GameConnection ("localhost");
	}

	public void draw () {
		mConnection.getGameMap ().getPlayers ().forEach (Rect::draw);
	}

	public void mouseEvent () {
		if (Mouse.isButtonDown (0) && Mouse.isInsideWindow ()) {
			//            Player p = players.get(0);
			//            Line l = new Line(p.getX()+p.getWidth()/2, p.getY()+p.getY()/2, Mouse.getX(), Mouse.getY());
			//            tickings.add(new Bullet(l, 10));
		}
	}

	public void keyboardEvent () {
		if (Keyboard.isKeyDown (Keyboard.KEY_W)) {
			mConnection.sendMove (0, 1);
		}
		if (Keyboard.isKeyDown (Keyboard.KEY_S)) {
			mConnection.sendMove (0, -1);
		}
		if (Keyboard.isKeyDown (Keyboard.KEY_A)) {
			mConnection.sendMove (-1, 0);
		}
		if (Keyboard.isKeyDown (Keyboard.KEY_D)) {
			mConnection.sendMove (1, 0);
		}

	}

}
