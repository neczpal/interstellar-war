package game.ui;

import game.Textures;
import game.Util;
import game.server.GameConnection;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author neczpal
 */
public class GameFrame extends Thread {

	private final int width, height;
	private String name;
	private GameConnection mConnection;

	public GameFrame (String name, int width, int height, GameConnection connection) {
		super (name);
		this.name = name;
		this.width = width;
		this.height = height;
		mConnection = connection;
	}

	@Override
	public void run () {
		initDisplay ();
		initGL ();
		Util.loadTextures ();
		Textures.loadTextures ();
		mConnection.getGameMap ().initTextures ();

		while (!Display.isCloseRequested ()) {
			glClear (GL_COLOR_BUFFER_BIT);

			mouseEvent ();
			keyboardEvent ();

			draw ();

			Display.sync (80);
			Display.update ();
		}
		clean ();
	}

	private void initDisplay () {
		try {
			Display.setDisplayMode (new DisplayMode (width, height));
			Display.setTitle (name);
			Display.create ();
			Keyboard.create ();
			Mouse.create ();

		} catch (LWJGLException ex) {
			Logger.getLogger (GameFrame.class.getName ()).log (Level.SEVERE, null, ex);
		}
	}

	private void initGL () {
		glMatrixMode (GL_PROJECTION);
		glLoadIdentity ();
		glOrtho (0, Display.getWidth (), 0, Display.getHeight (), -1, 1);
		glMatrixMode (GL_MODELVIEW);

		glClearColor (0, 0, 0, 1);

		glDisable (GL_DEPTH_TEST);
		glEnable (GL_TEXTURE_2D);
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void draw () {
		mConnection.getGameMap ().draw ();
	}

	private void mouseEvent () {
		if (Mouse.isInsideWindow ()) {
			mConnection.getGameMap ().mouseEvent ();
		}
	}

	private void keyboardEvent () {
		mConnection.getGameMap ().keyboardEvent ();
	}

	private void clean () {
		mConnection.leaveRoom ();
		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();
	}

}
