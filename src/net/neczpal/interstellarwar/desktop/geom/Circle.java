package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.*;

public class Circle extends Point {
	private float r;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	public Circle (float x, float y, float r) {
		super (x, y);
		this.r = r;
	}

	public void draw () {
		glBindTexture (GL_TEXTURE_2D, mTexture);
		mColor.setGLColor ();

		glBegin (GL_POLYGON);

		for (float angle = 0.0f; angle < 360.0f; angle += 2.0f) {
			float radian = (float) (angle * (Math.PI / 180.0f));

			float xcos = (float) Math.cos (radian);
			float ysin = (float) Math.sin (radian);
			float x = xcos * r + this.x;
			float y = ysin * r + this.y;
			float tx = xcos * 0.5f + 0.5f;
			float ty = ysin * 0.5f + 0.5f;
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

	public float getRadius () {
		return r;
	}

	public void setRadius (float radius) {
		this.r = radius;
	}

}
