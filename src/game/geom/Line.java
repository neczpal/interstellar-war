package game.geom;

import static org.lwjgl.opengl.GL11.*;

public class Line {
	private Point a, b;

	private Color mColor = Color.WHITE;

	public Line (Point a, Point b) {
		this.a = a;
		this.b = b;
	}

	public void draw () {
		glDisable (GL_TEXTURE_2D);
		mColor.setGLColor ();
		glBegin (GL_LINES);
		{
			glVertex2d (a.x, a.y);
			glVertex2d (b.x, b.y);
		}
		glEnd ();
		glEnable (GL_TEXTURE_2D);
	}

	public void setColor (Color color) {
		mColor = color;
	}

	public void move (int dx, int dy) {
		a.move (dx, dy);
		b.move (dx, dy);
	}

	public void rotate (Point p, double angle) {
		a.rotate (p, angle);
		b.rotate (p, angle);
	}

	public double getLength () {
		return a.distance (b);
	}

	public Point getA () {
		return a;
	}

	public void setA (Point p) {
		a.setPosition (p.x, p.y);
	}

	public Point getB () {
		return b;
	}

	public void setB (Point p) {
		b.setPosition (p.x, p.y);
	}

	public void setA (int a1, int a2) {
		a.setPosition (a1, a2);
	}

	public void setB (int b1, int b2) {
		b.setPosition (b1, b2);
	}


}


