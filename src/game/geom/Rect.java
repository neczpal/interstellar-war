package game.geom;

import game.Util;

import java.io.Serializable;

public class Rect implements Serializable {

	private double x, y;
	private double width, height;
	private Color color;
	private int mTexture = -1;

	public Rect (double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.color = Color.WHITE;
	}

	public void draw () {
		color.setGLColor ();
		Util.drawRect (x, y, width, height, mTexture);
	}

	public boolean isInside (Point2D point) {
		return x <= point.x && point.x <= x + width && y <= point.y && point.y <= y + height;
	}

	public void setColor (Color color) {
		this.color = color;
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
