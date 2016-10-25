package game.geom;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 08..
 */
public class Line {
	private Point2D a, b;

	public Line (Point2D a, Point2D b) {
		this.a = a;
		this.b = b;
	}

	public void draw () {
		glDisable (GL_TEXTURE_2D);
		glBegin (GL_LINES);
		{
			glVertex2d (a.x, a.y);
			glVertex2d (b.x, b.y);
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public void move (int dx, int dy) {
		a.move (dx, dy);
		b.move (dx, dy);
	}

	public void rotate (Point2D p, double angle) {
		a.rotate (p, angle);
		b.rotate (p, angle);
	}

	public double getLength () {
		return a.distance (b);
	}

	public Point2D getA () {
		return a;
	}

	public void setA (Point2D p) {
		a.setPosition (p.x, p.y);
	}

	public Point2D getB () {
		return b;
	}

	public void setB (Point2D p) {
		b.setPosition (p.x, p.y);
	}

	public void setA (int a1, int a2) {
		a.setPosition (a1, a2);
	}

	public void setB (int b1, int b2) {
		b.setPosition (b1, b2);
	}


}


