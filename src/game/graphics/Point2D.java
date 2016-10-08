package game.graphics;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Point2D {
	int x, y;

	public Point2D () {
		this (0, 0);
	}

	public Point2D (int x, int y) {
		setPosition (x, y);
	}

	public void draw () {
		glBegin (GL_POINTS);
		{
			glVertex2d (x, y);
		}
		glEnd ();
	}

	public void move (int dx, int dy) {
		x += dx;
		y += dy;
	}

	public void rotate (Point2D p, double angle) {
		double lx = p.x - x;
		double ly = p.y - y;
		double cos = Math.cos (angle);
		double sin = Math.sin (angle);

		x = p.x - (int) (lx * cos - ly * sin);
		y = p.y - (int) (lx * sin + ly * cos);
	}

	public double distance (Point2D p) {
		return Math.sqrt (Math.pow (p.x - x, 2) + Math.pow (p.y - y, 2));
	}

	public int getX () {
		return x;
	}

	public void setX (int x) {
		this.x = x;
	}

	public int getY () {
		return y;
	}

	public void setY (int y) {
		this.y = y;
	}

	public final void setPosition (int x, int y) {
		setX (x);
		setY (y);
	}
}
