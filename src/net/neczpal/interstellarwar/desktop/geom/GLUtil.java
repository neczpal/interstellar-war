package net.neczpal.interstellarwar.desktop.geom;

import net.neczpal.interstellarwar.desktop.Loader;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public final class GLUtil {

	private static final int DEFAULT_FONTSIZE = 16;
	private static final int DEFAULT_ARROWSIZE = 40;

	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

	private static int[] characters;

	/**
	 * Kann nicht Instantiieren
	 */
	private GLUtil () {
	}

	/**
	 * Initalisiert die Charakter-Texturen
	 *
	 * @throws IOException falls das File kann nicht geöffnet werden
	 */
	public static void init () throws IOException {
		characters = Loader.loadTextures ("res/textures/font64.png", 64, 64);
	}

	/**
	 * Malt ein Charackterkette an dem Bildschirm aus
	 *
	 * @param string Das Charackterkette
	 * @param x      Die Position der X-Achse
	 * @param y      Die Position der Y-Achse
	 */
	public static void drawString (String string, float x, float y) {
		drawString (string, x, y, DEFAULT_FONTSIZE);
	}

	/**
	 * Malt ein Charackterkette an dem Bildschirm aus
	 *
	 * @param string   Das Charackterkette
	 * @param x        Die Position der X-Achse
	 * @param y        Die Position der Y-Achse
	 * @param fontSize Die Größe der Charackter
	 */
	public static void drawString (String string, float x, float y, int fontSize) {
		drawString (string, x, y, fontSize, DEFAULT_FONT_COLOR);
	}

	/**
	 * Malt ein Charackterkette an dem Bildschirm aus
	 *
	 * @param string Das Charackterkette
	 * @param x      Die Position der X-Achse
	 * @param y      Die Position der Y-Achse
	 * @param color  Die Farbe der Charackter
	 */
	public static void drawString (String string, float x, float y, Color color) {
		drawString (string, x, y, DEFAULT_FONTSIZE, color);
	}

	/**
	 * Malt ein Charackterkette an dem Bildschirm aus
	 *
	 * @param string   Das Charackterkette
	 * @param x        Die Position der X-Achse
	 * @param y        Die Position der Y-Achse
	 * @param fontSize Die Größe der Charackter
	 * @param color    Die Farbe der Charackter
	 */
	public static void drawString (String string, float x, float y, int fontSize, Color color) {
		color.setGLColor ();
		x -= string.length () * fontSize / 2;//TO THE CENTER
		y -= fontSize / 2;//TO THE CENTER
		for (int i = 0; i < string.length (); i++) {
			drawRect (x + i * fontSize, y, fontSize, fontSize, characters[string.charAt (i)]);
		}
	}

	/**
	 * Malt ein Rechteck aus
	 *
	 * @param x   Die Position der X-Achse
	 * @param y   Die Position der Y-Achse
	 * @param w   Die Breite der Rechteck
	 * @param h   Die Höhe der Rechteck
	 * @param tex Die Texture
	 */
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

	/**
	 * Malt ein Kreis aus
	 *
	 * @param x Die Position der X-Achse
	 * @param y Die Position der Y-Achse
	 * @param r Die Radius der Kreis
	 */
	public static void drawCircle (float x, float y, float r) {
		drawCircle (x, y, r, DEFAULT_COLOR);
	}

	/**
	 * Malt ein Kreis aus
	 *
	 * @param x     Die Position der X-Achse
	 * @param y     Die Position der Y-Achse
	 * @param r     Die Radius der Kreis
	 * @param color Die Farbe der Charackter
	 */
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

	/**
	 * Malt ein Kreis aus
	 *
	 * @param x     Die Position der X-Achse
	 * @param y     Die Position der Y-Achse
	 * @param r     Die Radius der Kreis
	 * @param color Die Farbe der Charackter
	 * @param tex   Die Texture
	 */
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

	/**
	 * Malt ein Linie aus
	 *
	 * @param a Die Position der eine Punkt
	 * @param b Die Position der andere Punkt
	 */
	public static void drawLine (Point a, Point b) {
		drawLine (a, b, DEFAULT_COLOR);
	}

	/**
	 * Malt ein Linie aus
	 *
	 * @param a     Die Position der eine Punkt
	 * @param b     Die Position der andere Punkt
	 * @param color Die Farbe der Linie
	 */
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

	/**
	 * Malt ein Deieck aus
	 *
	 * @param a Die Position der eine Gipfel
	 * @param b Die Position der zweite Gipfel
	 * @param c Die Position der dritte Gipfel
	 */
	public static void drawTriangle (Point a, Point b, Point c) {
		drawTriangle (a, b, c, DEFAULT_COLOR);
	}

	/**
	 * Malt ein Deieck aus
	 *
	 * @param a     Die Position der eine Gipfel
	 * @param b     Die Position der zweite Gipfel
	 * @param c     Die Position der dritte Gipfel
	 * @param color Die Farbe der Dreieck
	 */
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

	/**
	 * Malt ein Quadrat aus
	 *
	 * @param a Erste Gipfel
	 * @param b Zweite Gipfel
	 * @param c Dritte Gipfel
	 * @param d Vierte Gipfel
	 */
	public static void drawQuad (Point a, Point b, Point c, Point d) {
		drawQuad (a, b, c, d, DEFAULT_COLOR);
	}

	/**
	 * Malt ein Quadrat aus
	 *
	 * @param a     Erste Gipfel
	 * @param b     Zweite Gipfel
	 * @param c     Dritte Gipfel
	 * @param d     Vierte Gipfel
	 * @param color Der Farbe der Quadrat
	 */
	public static void drawQuad (Point a, Point b, Point c, Point d, Color color) {
		drawQuad (a, b, c, d, color, -1);
	}

	/**
	 * Malt ein Quadrat aus
	 *
	 * @param a     Erste Gipfel
	 * @param b     Zweite Gipfel
	 * @param c     Dritte Gipfel
	 * @param d     Vierte Gipfel
	 * @param color Der Farbe der Quadrat
	 * @param tex   Die Texture
	 */
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

	/**
	 * Malt ein Pfeil aus
	 *
	 * @param a Der Anfangspunkt
	 * @param b Der Endpunkt
	 */
	public static void drawArrow (Point a, Point b) {
		drawArrow (a, b, DEFAULT_COLOR);
	}

	/**
	 * Malt ein Pfeil aus
	 *
	 * @param a     Der Anfangspunkt
	 * @param b     Der Endpunkt
	 * @param color Der Farbe der Pfeige
	 */
	public static void drawArrow (Point a, Point b, Color color) {
		drawArrow (a, b, color, DEFAULT_ARROWSIZE);
	}

	/**
	 * Malt ein Pfeil aus
	 *
	 * @param a         Der Anfangspunkt
	 * @param b         Der Endpunkt
	 * @param color     Der Farbe der Pfeige
	 * @param arrowSize Die Größe der Pfeige
	 */
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

		drawLine (a, b, color);
		drawTriangle (ta, tb, tc, color);
	}
}
