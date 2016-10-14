package game;

import game.geom.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 09..
 */
public class Util {

	public static final int DEFAULT_FONTSIZE = 24;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final Color DEFAULT_FONT_COLOR = Color.BLACK;
	private static int[] mCharacters;

	public static void loadDefaultTextures () {
		mCharacters = Loader.loadTextures ("res/pictures/font64.png", 64, 64);
	}

	public static void drawString (String string, int x, int y) {
		drawString (string, x, y, DEFAULT_FONTSIZE);
	}

	public static void drawString (String string, int x, int y, int fontSize) {
		drawString (string, x, y, fontSize, DEFAULT_FONT_COLOR);
	}

	public static void drawString (String string, int x, int y, int fontSize, Color color) {
		color.setGLColor ();
		for (int i = 0; i < string.length (); i++) {
			drawRect (x + i * fontSize, y, fontSize, fontSize, mCharacters[string.charAt (i)]);
		}
	}

	public static void drawRect (int x, int y, int w, int h, int tex) {
		int wx = w + x, hy = h + y;

		DEFAULT_COLOR.setGLColor ();

		glBindTexture (GL_TEXTURE_2D, tex);
		glBegin (GL_QUADS);
		{
			glTexCoord2f (0, 1);
			glVertex2i (x, y);
			glTexCoord2f (1, 1);
			glVertex2i (wx, y);
			glTexCoord2f (1, 0);
			glVertex2i (wx, hy);
			glTexCoord2f (0, 0);
			glVertex2i (x, hy);
		}
		glEnd ();
	}
}
