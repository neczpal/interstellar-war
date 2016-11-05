package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Circle extends Point {
	private double r;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	public Circle (double x, double y, double r) {
		super (x, y);
		this.r = r;
	}

	public void draw () {
		glBindTexture (GL_TEXTURE_2D, mTexture);
		mColor.setGLColor ();

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			double radian = angle * (Math.PI / 180.0f);

			double xcos = Math.cos (radian);
			double ysin = Math.sin (radian);
			double x = xcos * r + this.x;
			double y = ysin * r + this.y;
			double tx = xcos * 0.5 + 0.5;
			double ty = ysin * 0.5 + 0.5;
			glTexCoord2d (tx, ty);
			glVertex2d (x, y);
		}

		glEnd ();
	}

	public void setColor (Color color) {
		this.mColor = color;
	}

	public void setTexture (int texture) {
		this.mTexture = texture;
	}

	public boolean isInside (Point p) {
		return Math.pow ((p.x - x), 2) + Math.pow ((p.y - y), 2) <= r * r;
	}

	public double getRadius () {
		return r;
	}

	public void setRadius (double radius) {
		this.r = radius;
	}

}
