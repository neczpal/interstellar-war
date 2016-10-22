package game.geom;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Circle {
	private static final int circle_points = 120;

	private Point2D k;
	private double r;
	private int mTexture = -1;

	public Circle (double x, double y, double r) {
		this (new Point2D (x, y), r);
	}

	public Circle (Point2D k, double r) {
		this.k = k;
		this.r = r;
	}

	public void draw () {
		glBindTexture (GL_TEXTURE_2D, mTexture);

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			double radian = angle * (Math.PI / 180.0f);

			double xcos = Math.cos (radian);
			double ysin = Math.sin (radian);
			double x = xcos * r + k.x;
			double y = ysin * r + k.y;
			double tx = xcos * 0.5 + 0.5;
			double ty = ysin * 0.5 + 0.5;
			glTexCoord2d (tx, ty);
			glVertex2d (x, y);
		}

		glEnd ();
	}

	public double distance (Circle other) {
		return getCenter ().distance (other.getCenter ());
	}

	public int getTexture () {
		return mTexture;
	}

	public void setTexture (int texture) {
		mTexture = texture;
	}

	public void move (double dx, double dy) {
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

	public double getRadius () {
		return r;
	}

	public void setRadius (double radius) {
		this.r = radius;
	}

	public void setPosition (double x, double y) {
		k.setPosition (x, y);
	}
}
