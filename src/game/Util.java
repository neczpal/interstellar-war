package game;

import game.geom.Color;
import game.geom.Point;

import static game.Textures.characters;
import static org.lwjgl.opengl.GL11.*;

public class Util {

	private static final int DEFAULT_FONTSIZE = 16;
	private static final int DEFAULT_ARROWSIZE = 40;

	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

	public static void drawString (String string, double x, double y) {
		drawString (string, x, y, DEFAULT_FONTSIZE);
	}

	public static void drawString (String string, double x, double y, int fontSize) {
		drawString (string, x, y, fontSize, DEFAULT_FONT_COLOR);
	}

	public static void drawString (String string, double x, double y, Color color) {
		drawString (string, x, y, DEFAULT_FONTSIZE, color);
	}

	public static void drawString (String string, double x, double y, int fontSize, Color color) {
		color.setGLColor ();
		x -= string.length () * fontSize / 2;//TO THE CENTER
		y -= fontSize / 2;//TO THE CENTER
		for (int i = 0; i < string.length (); i++) {
			drawRect (x + i * fontSize, y, fontSize, fontSize, characters[string.charAt (i)]);
		}
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
		drawCircle (x, y, r, DEFAULT_COLOR);
	}

	public static void drawCircle (double x, double y, double r, Color color) {
		//		drawCircle (x, y, r, color, -1);
		glDisable (GL_TEXTURE_2D);
		color.setGLColor ();
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

	public static void drawCircle (double x, double y, double r, Color color, int tex) {
		glBindTexture (GL_TEXTURE_2D, tex);
		color.setGLColor ();

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			double radian = angle * (Math.PI / 180.0f);

			double xcos = Math.cos (radian);
			double ysin = Math.sin (radian);
			double rx = xcos * r + x;
			double ry = ysin * r + y;
			double tx = xcos * 0.5 + 0.5;
			double ty = ysin * 0.5 + 0.5;
			glTexCoord2d (tx, ty);
			glVertex2d (rx, ry);
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
			glVertex2d (a.getX (), a.getY ());
			glVertex2d (b.getX (), b.getY ());
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
			glVertex2d (a.getX (), a.getY ());
			glVertex2d (b.getX (), b.getY ());
			glVertex2d (c.getX (), c.getY ());
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
			glVertex2d (a.getX (), a.getY ());
			glTexCoord2f (1, 1);
			glVertex2d (b.getX (), b.getY ());
			glTexCoord2f (1, 0);
			glVertex2d (c.getX (), c.getY ());
			glTexCoord2f (0, 0);
			glVertex2d (d.getX (), d.getY ());
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
		double length = a.distance (b);
		double lx = b.getX () - a.getX ();
		double ly = b.getY () - a.getY ();
		double hx = lx / length * arrowSize;
		double hy = ly / length * arrowSize;

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
