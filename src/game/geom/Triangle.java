package game.geom;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 21..
 */
public class Triangle {
	private Point2D a, b, c;

	public Triangle (Point2D a, Point2D b, Point2D c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public void draw () {
		glDisable (GL_TEXTURE_2D);
		glBegin (GL_TRIANGLES);
		{
			glVertex2f (a.getX (), a.getY ());
			glVertex2f (b.getX (), b.getY ());
			glVertex2f (c.getX (), c.getY ());
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public void move (int dx, int dy) {
		a.move (dx, dy);
		b.move (dx, dy);
		c.move (dx, dy);
	}

	public void rotate (Point2D p, double angle) {
		a.rotate (p, angle);
		b.rotate (p, angle);
		c.rotate (p, angle);
	}

}
