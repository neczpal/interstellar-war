package game;

import game.geom.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 09..
 */
public class Util {

	public static final int DEFAULT_FONTSIZE = 14;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final Color DEFAULT_FONT_COLOR = Color.WHITE;
	private static int[] mCharacters;

	public static void loadTextures () {
		mCharacters = Loader.loadTextures ("res/pictures/font64.png", 64, 64);
	}

	public static void drawString (String string, double x, double y) {
		drawString (string, x, y, DEFAULT_FONTSIZE);
	}

	public static void drawString (String string, double x, double y, int fontSize) {
		drawString (string, x, y, fontSize, DEFAULT_FONT_COLOR);
	}

	public static void drawString (String string, double x, double y, int fontSize, Color color) {
		color.setGLColor ();
		for (int i = 0; i < string.length (); i++) {
			drawRect (x + i * fontSize, y, fontSize, fontSize, mCharacters[string.charAt (i)]);
		}
		DEFAULT_COLOR.setGLColor ();
	}

	public static void drawRect (double x, double y, double w, double h, int tex) {
		double wx = w + x, hy = h + y;

		glBindTexture (GL_TEXTURE_2D, tex);
		glBegin (GL_QUADS);
		{
			glTexCoord2f (0, 1);
			glVertex2d (x, y);
			glTexCoord2f (1, 1);
			glVertex2d (wx, y);
			glTexCoord2f (1, 0);
			glVertex2d (wx, hy);
			glTexCoord2f (0, 0);
			glVertex2d (x, hy);
		}
		glEnd ();
	}

	public static void drawCircle (double x, double y, double r) {
		glDisable (GL_TEXTURE_2D);
		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			double radian = angle * (Math.PI / 180.0f);

			double xcos = Math.cos (radian);
			double ysin = Math.sin (radian);
			glVertex2d (xcos * r + x, ysin * r + y);
		}

		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}
}
