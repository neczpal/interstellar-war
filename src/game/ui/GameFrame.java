package game.ui;

import game.Textures;
import game.Util;
import game.connection.ClientConnection;
import game.map.GameMap;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author neczpal
 */
public class GameFrame extends Thread {

	private int mDisplayModeIndex;
	private int mWidth, mHeight;
	private String mName;
	private GameMap mGameMap;

	public GameFrame (String mTitle, int displayModeIndex, GameMap gameMap) {
		super (mTitle);
		this.mName = mTitle;
		this.mDisplayModeIndex = displayModeIndex;
		this.mGameMap = gameMap;
	}

	@Override
	public void run () {
		initDisplay ();
		initGL ();
		Util.loadTextures ();
		Textures.loadTextures ();
		mGameMap.initTextures ();

		while (!Display.isCloseRequested ()) {
			glClear (GL_COLOR_BUFFER_BIT);

			mouseEvent ();
			keyboardEvent ();

			draw ();

			Display.sync (80);
			Display.update ();
		}
		clean ();
		((ClientConnection) mGameMap.getConnection ()).leaveRoom ();
	}

	private void initDisplay () {
		try {
			Display.setDisplayMode (Display.getAvailableDisplayModes ()[mDisplayModeIndex]);
			mWidth = Display.getDisplayMode ().getWidth ();
			mHeight = Display.getDisplayMode ().getHeight ();
			//			Display.setFullscreen (true); //#TODO
			Display.setTitle (mName);
			Display.create ();
			Keyboard.create ();
			Mouse.create ();

		} catch (LWJGLException ex) {
			ex.printStackTrace ();
		}
	}

	private void initGL () {
		glMatrixMode (GL_PROJECTION);
		glLoadIdentity ();
		glOrtho (0, mWidth, 0, mHeight, -1, 1);
		glMatrixMode (GL_MODELVIEW);

		glClearColor (0, 0, 0, 1);

		glDisable (GL_DEPTH_TEST);
		glEnable (GL_TEXTURE_2D);
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void draw () {
		mGameMap.draw ();
	}

	private void mouseEvent () {
		if (Mouse.isInsideWindow ()) {
			mGameMap.mouseEvent ();
		}
	}

	private void keyboardEvent () {
		mGameMap.keyboardEvent ();
	}

	private void clean () {
		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();
	}

}
