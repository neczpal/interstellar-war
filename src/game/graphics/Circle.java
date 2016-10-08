package game.graphics;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Circle {
	private static final int circle_points = 120;

	private Point2D k;
	private int r;

	public Circle (int x, int y, int r) {
		this (new Point2D (x, y), r);
	}

	public Circle (Point2D k, int r) {
		this.k = k;
		this.r = r;
	}

	public void draw () {
		//        glMatrixMode(GL_MODELVIEW);
		glPushMatrix ();
		glLoadIdentity ();
		glTranslated (k.x, k.y, 0.0d);
		final double angle = 2.0 * Math.PI / circle_points;

		glBegin (GL_POLYGON);
		double angle1 = 0.0;
		glVertex2d (r * Math.cos (0.0), r * Math.sin (0.0));
		int i;
		for (i = 0; i < circle_points; i++) {
			glVertex2d (r * Math.cos (angle1), r * Math.sin (angle1));
			angle1 += angle;
		}
		glEnd ();
		glPopMatrix ();
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
