package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.desktop.Textures;
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

	public GameFrame (String mTitle, int displayModeIndex, InterstellarWarClient client) {
		super (mTitle);
		this.mName = mTitle;
		this.mDisplayModeIndex = displayModeIndex;
		this.mGamePanel = new InterstellarWarPanel (client);
		this.mIsGameFrameRunning = false;
	}

	@Override
	public void run () {
		initDisplay ();
		initGL ();
		try {
			Textures.loadTextures ();
		} catch (IOException ex) {
			ex.printStackTrace ();//#TODO LOG
		}
		mGamePanel.init ();
		mIsGameFrameRunning = true;
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
		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
		mGamePanel.getInterstellarWarClient ().leaveRoom ();
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

	private void clean () {
		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();
	}

	public void stopGameFrame () {
		mIsGameFrameRunning = false;
		mGamePanel.getInterstellarWarClient ().getCore ().stopGame ();
	}
}
