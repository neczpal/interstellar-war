package net.neczpal.interstellarwar.desktop.geom;

import net.neczpal.interstellarwar.desktop.Util;

public class Rect {

	private float x, y;
	private float width, height;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	public Rect (float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void draw () {
		mColor.setGLColor ();
		Util.drawRect (x, y, width, height, mTexture);
	}

	public boolean isInside (Point point) {
		return x <= point.x && point.x <= x + width && y <= point.y && point.y <= y + height;
	}

	public void setColor (Color color) {
		this.mColor = color;
	}

	public void setTexture (int texture) {
		this.mTexture = texture;
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

	public float getWidth () {
		return width;
	}

	public void setWidth (float width) {
		this.width = width;
	}

	public float getHeight () {
		return height;
	}

	public void setHeight (float height) {
		this.height = height;
	}

	public void setPosition (float x, float y) {
		setX (x);
		setY (y);
	}

}
