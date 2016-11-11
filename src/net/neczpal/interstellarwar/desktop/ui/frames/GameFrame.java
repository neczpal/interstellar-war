package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.desktop.GLUtil;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class GameFrame extends Thread {

	private int mDisplayModeIndex;
	private int mWidth, mHeight;
	private String mName;
	private boolean mIsGameFrameRunning;

	private InterstellarWarPanel mGamePanel;

	public GameFrame (String name, int displayModeIndex, InterstellarWarClient client) {
		super (name);
		this.mName = name;
		this.mDisplayModeIndex = displayModeIndex;
		this.mGamePanel = new InterstellarWarPanel (client);
		this.mIsGameFrameRunning = false;
	}

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

	private void initTextures () {
		try {
			GLUtil.init ();
			mGamePanel.initTextures ();
		} catch (IOException ex) {
			System.err.println ("Textures failed to load.");
		}
	}

	private void clean () {
		mIsGameFrameRunning = false;

		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();

		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
		mGamePanel.getInterstellarWarClient ().leaveRoom ();
	}

	public void stopGameFrame () {
		mIsGameFrameRunning = false;
		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
	}
}
