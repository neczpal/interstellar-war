package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Point {
	double x, y;

	/**
	 * Erstellt ein Punkt
	 *
	 * @param x Die Position der X-Achse
	 * @param y Die Position der Y-Achse
	 */
	public Point (double x, double y) {
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
	public void move (double dx, double dy) {
		x += dx;
		y += dy;
	}

	/**
	 * Rotiert das Punkt um 'p' Punkt
	 *
	 * @param p     Der Punkt
	 * @param angle Der Winkel
	 */
	public void rotate (Point p, double angle) {
		double lx = p.x - x;
		double ly = p.y - y;
		double cos = Math.cos (angle);
		double sin = Math.sin (angle);

		x = p.x - (lx * cos - ly * sin);
		y = p.y - (lx * sin + ly * cos);
	}

	/**
	 * @param p Der Punkt
	 * @return Die Entfernung von 'p' Punkt
	 */
	public double distance (Point p) {
		return Math.sqrt (Math.pow (p.x - x, 2) + Math.pow (p.y - y, 2));
	}

	//GETTERS, SETTERS

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
