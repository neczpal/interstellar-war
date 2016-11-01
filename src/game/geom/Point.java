package game.geom;

import static org.lwjgl.opengl.GL11.*;

public class Point {
	double x, y;

	public Point (double x, double y) {
		setPosition (x, y);
	}

	public void draw () {
		glBegin (GL_POINTS);
		{
			glVertex2d (x, y);
		}
		glEnd ();
	}

	public void move (double dx, double dy) {
		x += dx;
		y += dy;
	}

	public void rotate (Point p, double angle) {
		double lx = p.x - x;
		double ly = p.y - y;
		double cos = Math.cos (angle);
		double sin = Math.sin (angle);

		x = p.x - (int) (lx * cos - ly * sin);
		y = p.y - (int) (lx * sin + ly * cos);
	}

	public double distance (Point p) {
		return Math.sqrt (Math.pow (p.x - x, 2) + Math.pow (p.y - y, 2));
	}

	public double getX () {
		return x;
	}

	public void setX (double x) {
		this.x = x;
	}

	public double getY () {
		return y;
	}

	public void setY (double y) {
		this.y = y;
	}

	public final void setPosition (double x, double y) {
		setX (x);
		setY (y);
	}
}
