package game.ui;

import game.Loader;
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
public class Window extends Thread {

	private final int width, height;
	private String name;
	private Panel cPanel;

	public Window (String name, int width, int height) {
		super (name);
		this.name = name;
		this.width = width;
		this.height = height;
		this.cPanel = new Panel ();
	}

	public static void main (String[] args) {
		new Window ("Interstellar War", 640, 480).start ();
	}

	@Override
	public void run () {
		initDisplay ();
		initGL ();


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
			Loader.setUseCache (false);

		} catch (LWJGLException ex) {
			Logger.getLogger (Window.class.getName ()).log (Level.SEVERE, null, ex);
		}
	}

	private void initGL () {
		glMatrixMode (GL_PROJECTION);
		glLoadIdentity ();
		glOrtho (0, Display.getWidth (), 0, Display.getHeight (), -1, 1);
		glMatrixMode (GL_MODELVIEW);

		glClearColor (0, 0, 0, 1);

		glDisable (GL_DEPTH_TEST);
	}

	private void draw () {
		cPanel.draw ();
	}

	private void mouseEvent () {
		if (Mouse.isInsideWindow ()) {
			cPanel.mouseEvent ();
		}
	}

	private void keyboardEvent () {
		cPanel.keyboardEvent ();
	}

	private void clean () {
		cPanel.mConnection.close ();
		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();
	}

}
