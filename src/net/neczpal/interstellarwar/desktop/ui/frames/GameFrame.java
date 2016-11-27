package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.desktop.geom.GLUtil;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class GameFrame extends Thread {

	private int mDisplayModeIndex;
	private boolean mFullScreen;

	private int mWidth, mHeight;
	private String mName;
	private boolean mIsGameFrameRunning;

	private InterstellarWarPanel mGamePanel;

	/**
	 * Erstellt das Spielfenster
	 *
	 * @param name             Der Name des Fensters
	 * @param displayModeIndex Das Index der Bildschirmmode
	 * @param client           Das Spiel-Client
	 */
	public GameFrame (String name, boolean fullScreen, int displayModeIndex, InterstellarWarClient client) {
		super (name);
		this.mName = name;
		this.mDisplayModeIndex = displayModeIndex;
		this.mFullScreen = fullScreen;
		this.mGamePanel = new InterstellarWarPanel (client);
		this.mIsGameFrameRunning = false;
	}

	/**
	 * Initialisiert das Bildschirm, GLContext, Texturen und Spiel und malt das Spielfenster mit 80 FPS
	 */
	@Override
	public void run () {
		mIsGameFrameRunning = true;

		initDisplay ();
		initGL ();
		initTextures ();
		mGamePanel.initGame ();

		while (!Display.isCloseRequested () && mIsGameFrameRunning) {
			glClear (GL_COLOR_BUFFER_BIT);

			if (Mouse.isInsideWindow ()) {
				mGamePanel.inputEvents ();
			}

			mGamePanel.draw ();

			Display.sync (80);
			Display.update ();
		}
		clean ();
	}

	/**
	 * Initialisiert das Bildschirm
	 */
	private void initDisplay () {
		try {
			Display.setDisplayMode (Display.getAvailableDisplayModes ()[mDisplayModeIndex]);
			mWidth = Display.getDisplayMode ().getWidth ();
			mHeight = Display.getDisplayMode ().getHeight ();

			Display.setFullscreen (mFullScreen);
			Display.setTitle (mName);

			Display.create ();
			Keyboard.create ();
			Mouse.create ();

		} catch (LWJGLException ex) {
			ex.printStackTrace ();
		}
	}

	/**
	 * Initialisiert das GLContext
	 */
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

	/**
	 * Initailisiert die Texturen
	 */
	private void initTextures () {
		try {
			GLUtil.init ();
			mGamePanel.initTextures ();
		} catch (IOException ex) {
			System.err.println ("Textures failed to load.");
		}
	}

	/**
	 * Lösch LWJGL
	 */
	private void clean () {
		mIsGameFrameRunning = false;

		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();

		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
		mGamePanel.getInterstellarWarClient ().leaveRoom ();
	}

	/**
	 * Beendet das Spielfenster
	 */
	public void stopGameFrame () {
		mIsGameFrameRunning = false;
		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
	}
}
