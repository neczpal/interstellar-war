package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Point {
	float x, y;

	public Point (float x, float y) {
		setPosition (x, y);
	}

	public void draw () {
		glBegin (GL_POINTS);
		{
			glVertex2d (x, y);
		}
		glEnd ();
	}

	public void move (float dx, float dy) {
		x += dx;
		y += dy;
	}

	public void rotate (Point p, float angle) {
		float lx = p.x - x;
		float ly = p.y - y;
		float cos = (float) Math.cos (angle);
		float sin = (float) Math.sin (angle);

		x = p.x - (int) (lx * cos - ly * sin);
		y = p.y - (int) (lx * sin + ly * cos);
	}

	public float distance (Point p) {
		return (float) Math.sqrt (Math.pow (p.x - x, 2) + Math.pow (p.y - y, 2));
	}

	public float getX () {
		return x;
	}

	public void setX (float x) {
		this.x = x;
	}

	public float getY () {
		return y;
	}

	public void setY (float y) {
		this.y = y;
	}

	public final void setPosition (float x, float y) {
		setX (x);
		setY (y);
	}
}
