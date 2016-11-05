package net.neczpal.interstellarwar.desktop.geom;

import net.neczpal.interstellarwar.desktop.Util;

public class Rect {

	private double x, y;
	private double width, height;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	public Rect (double x, double y, double w, double h) {
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

	public double getWidth () {
		return width;
	}

	public void setWidth (double width) {
		this.width = width;
	}

	public double getHeight () {
		return height;
	}

	public void setHeight (double height) {
		this.height = height;
	}

	public void setPosition (double x, double y) {
		setX (x);
		setY (y);
	}

}
