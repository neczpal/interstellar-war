package net.neczpal.interstellarwar.desktop;

import net.neczpal.interstellarwar.desktop.geom.Color;
import net.neczpal.interstellarwar.desktop.geom.Point;

import static org.lwjgl.opengl.GL11.*;

public class Util {

	private static final int DEFAULT_FONTSIZE = 16;
	private static final int DEFAULT_ARROWSIZE = 40;

	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

	public static void drawString (String string, float x, float y) {
		drawString (string, x, y, DEFAULT_FONTSIZE);
	}

	public static void drawString (String string, float x, float y, int fontSize) {
		drawString (string, x, y, fontSize, DEFAULT_FONT_COLOR);
	}

	public static void drawString (String string, float x, float y, Color color) {
		drawString (string, x, y, DEFAULT_FONTSIZE, color);
	}

	public static void drawString (String string, float x, float y, int fontSize, Color color) {
		color.setGLColor ();
		x -= string.length () * fontSize / 2;//TO THE CENTER
		y -= fontSize / 2;//TO THE CENTER
		for (int i = 0; i < string.length (); i++) {
			drawRect (x + i * fontSize, y, fontSize, fontSize, Textures.characters[string.charAt (i)]);
		}
	}

	public static void drawRect (float x, float y, float w, float h, int tex) {
		float wx = w + x, hy = h + y;

		glBindTexture (GL_TEXTURE_2D, tex);
		glBegin (GL_QUADS);
		{
			glTexCoord2f (0, 1);
			glVertex2f (x, y);
			glTexCoord2f (1, 1);
			glVertex2f (wx, y);
			glTexCoord2f (1, 0);
			glVertex2f (wx, hy);
			glTexCoord2f (0, 0);
			glVertex2f (x, hy);
		}
		glEnd ();
	}

	public static void drawCircle (float x, float y, float r) {
		drawCircle (x, y, r, DEFAULT_COLOR);
	}

	public static void drawCircle (float x, float y, float r, Color color) {
		//		drawCircle (x, y, r, color, -1);
		glDisable (GL_TEXTURE_2D);
		color.setGLColor ();
		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			float radian = (float) (angle * (Math.PI / 180.0f));

			float xcos = (float) Math.cos (radian);
			float ysin = (float) Math.sin (radian);
			glVertex2f (xcos * r + x, ysin * r + y);
		}

		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public static void drawCircle (float x, float y, float r, Color color, int tex) {
		glBindTexture (GL_TEXTURE_2D, tex);
		color.setGLColor ();

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			float radian = (float) (angle * (Math.PI / 180.0f));

			float xcos = (float) Math.cos (radian);
			float ysin = (float) Math.sin (radian);
			float rx = xcos * r + x;
			float ry = ysin * r + y;
			float tx = xcos * 0.5f + 0.5f;
			float ty = ysin * 0.5f + 0.5f;
			glTexCoord2f (tx, ty);
			glVertex2f (rx, ry);
		}

		glEnd ();
	}

	public static void drawLine (Point a, Point b) {
		drawLine (a, b, DEFAULT_COLOR);
	}

	public static void drawLine (Point a, Point b, Color color) {
		glDisable (GL_TEXTURE_2D);
		color.setGLColor ();
		glBegin (GL_LINES);
		{
			glVertex2f (a.getX (), a.getY ());
			glVertex2f (b.getX (), b.getY ());
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public static void drawTriangle (Point a, Point b, Point c) {
		drawTriangle (a, b, c, DEFAULT_COLOR);
	}

	public static void drawTriangle (Point a, Point b, Point c, Color color) {
		glDisable (GL_TEXTURE_2D);

		color.setGLColor ();

		glBegin (GL_TRIANGLES);
		{
			glVertex2f (a.getX (), a.getY ());
			glVertex2f (b.getX (), b.getY ());
			glVertex2f (c.getX (), c.getY ());
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public static void drawQuad (Point a, Point b, Point c, Point d) {
		drawQuad (a, b, c, d, DEFAULT_COLOR);
	}

	public static void drawQuad (Point a, Point b, Point c, Point d, Color color) {
		drawQuad (a, b, c, d, color, -1);
	}

	public static void drawQuad (Point a, Point b, Point c, Point d, Color color, int tex) {
		glBindTexture (GL_TEXTURE_2D, tex);
		color.setGLColor ();

		glBegin (GL_QUADS);
		{
			glTexCoord2f (0, 1);
			glVertex2f (a.getX (), a.getY ());
			glTexCoord2f (1, 1);
			glVertex2f (b.getX (), b.getY ());
			glTexCoord2f (1, 0);
			glVertex2f (c.getX (), c.getY ());
			glTexCoord2f (0, 0);
			glVertex2f (d.getX (), d.getY ());
		}
		glEnd ();
	}

	public static void drawArrow (Point a, Point b) {
		drawArrow (a, b, DEFAULT_COLOR);
	}

	public static void drawArrow (Point a, Point b, Color color) {
		drawArrow (a, b, color, DEFAULT_ARROWSIZE);
	}

	public static void drawArrow (Point a, Point b, Color color, int arrowSize) {
		float length = a.distance (b);
		float lx = b.getX () - a.getX ();
		float ly = b.getY () - a.getY ();
		float hx = lx / length * arrowSize;
		float hy = ly / length * arrowSize;

		Point ta = new Point (b.getX (), b.getY ());
		Point tb = new Point (b.getX () - hx / 2, b.getY () - hy / 2);
		Point tc = new Point (b.getX () - hx / 2, b.getY () - hy / 2);
		tb.rotate (b, -45);
		tc.rotate (b, 45);

		color.setGLColor ();
		drawLine (a, b);
		drawTriangle (ta, tb, tc);
	}
}
