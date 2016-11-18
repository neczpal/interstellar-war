package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Point {
	float x, y;

	/**
	 * Erstellt ein Punkt
	 *
	 * @param x Die Position der X-Achse
	 * @param y Die Position der Y-Achse
	 */
	public Point (float x, float y) {
		setPosition (x, y);
	}

	/**
	 * Malt den Punkt aus
	 */
	public void draw () {
		glBegin (GL_POINTS);
		{
			glVertex2d (x, y);
		}
		glEnd ();
	}

	/**
	 * Bewegt den Punkt
	 *
	 * @param dx X-Entfernung
	 * @param dy Y-Entfernung
	 */
	public void move (float dx, float dy) {
		x += dx;
		y += dy;
	}

	/**
	 * Rotiert das Punkt um 'p' Punkt
	 *
	 * @param p     Der Punkt
	 * @param angle Der Winkel
	 */
	public void rotate (Point p, float angle) {
		float lx = p.x - x;
		float ly = p.y - y;
		float cos = (float) Math.cos (angle);
		float sin = (float) Math.sin (angle);

		x = p.x - (int) (lx * cos - ly * sin);
		y = p.y - (int) (lx * sin + ly * cos);
	}

	/**
	 * @param p Der Punkt
	 * @return Die Entfernung von 'p' Punkt
	 */
	public float distance (Point p) {
		return (float) Math.sqrt (Math.pow (p.x - x, 2) + Math.pow (p.y - y, 2));
	}

	//GETTERS, SETTERS

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
