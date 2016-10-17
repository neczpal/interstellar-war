package game.geom;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Circle {
	private static final int circle_points = 120;

	private Point2D k;
	private int r;
	private int mTexture = -1;

	public Circle (int x, int y, int r) {
		this (new Point2D (x, y), r);
	}

	public Circle (Point2D k, int r) {
		this.k = k;
		this.r = r;
	}

	public void draw () {
		glBindTexture (GL_TEXTURE_2D, mTexture);

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			float radian = (float) (angle * (Math.PI / 180.0f));

			float xcos = (float) Math.cos (radian);
			float ysin = (float) Math.sin (radian);
			float x = xcos * r + k.x;
			float y = ysin * r + k.y;
			float tx = xcos * 0.5f + 0.5f;
			float ty = ysin * 0.5f + 0.5f;
			glTexCoord2f (tx, ty);
			glVertex2f (x, y);
		}

		glEnd ();
	}

	public int getTexture () {
		return mTexture;
	}
	public void setTexture (int texture) {
		mTexture = texture;
	}
	public void move (int dx, int dy) {
		k.move (dx, dy);
	}

	public void rotate (Point2D p, double angle) {
		p.rotate (p, angle);
	}

	public boolean isInside (Point2D p) {
		return Math.pow ((p.x - k.x), 2) + Math.pow ((p.y - k.y), 2) <= r * r;
	}

	public Point2D getCenter () {
		return k;
	}

	public int getRadius () {
		return r;
	}

	public void setRadius (int radius) {
		this.r = radius;
	}

	public void setPosition (int x, int y) {
		k.setPosition (x, y);
	}
}
