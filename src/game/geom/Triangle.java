package game.geom;

import static org.lwjgl.opengl.GL11.*;

public class Triangle {
	private Point a;
	private Point b;
	private Point c;

	private Color mColor = Color.WHITE;

	public Triangle (Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public void draw () {
		glDisable (GL_TEXTURE_2D);

		mColor.setGLColor ();

		glBegin (GL_TRIANGLES);
		{
			glVertex2d (a.getX (), a.getY ());
			glVertex2d (b.getX (), b.getY ());
			glVertex2d (c.getX (), c.getY ());
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public void move (double dx, double dy) {
		a.move (dx, dy);
		b.move (dx, dy);
		c.move (dx, dy);
	}

	public void rotate (Point p, double angle) {
		a.rotate (p, angle);
		b.rotate (p, angle);
		c.rotate (p, angle);
	}

	public void setColor (Color color) {
		this.mColor = color;
	}

	public Point getA () {
		return a;
	}

	public Point getB () {
		return b;
	}

	public Point getC () {
		return c;
	}

}
