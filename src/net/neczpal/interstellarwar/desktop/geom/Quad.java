package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Quad {
	private Point a;
	private Point b;
	private Point c;
	private Point d;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	public Quad (Point[] points) {
		this (points[0], points[1], points[2], points[3]);
	}

	public Quad (Point a, Point b, Point c, Point d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public void draw () {
		glBindTexture (GL_TEXTURE_2D, mTexture);
		mColor.setGLColor ();

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

	public void move (float dx, float dy) {
		a.move (dx, dy);
		b.move (dx, dy);
		c.move (dx, dy);
		d.move (dx, dy);
	}

	public void rotate (Point p, float angle) {
		a.rotate (p, angle);
		b.rotate (p, angle);
		c.rotate (p, angle);
		d.rotate (p, angle);
	}

	public void setColor (Color color) {
		this.mColor = color;
	}

	public void setTexture (int texture) {
		this.mTexture = texture;
	}


	public Point getA () {
		return a;
	}

	public void setA (Point a) {
		this.a = a;
	}

	public Point getB () {
		return b;
	}

	public void setB (Point b) {
		this.b = b;
	}

	public Point getC () {
		return c;
	}

	public void setC (Point c) {
		this.c = c;
	}

	public Point getD () {
		return d;
	}

	public void setD (Point d) {
		this.d = d;
	}

	public Point getCenter () {
		return new Point ((a.getX () + b.getX () + c.getX () + d.getX ()) / 4, (a.getY () + b.getY () + c.getY () + d.getY ()) / 4);
	}
}
