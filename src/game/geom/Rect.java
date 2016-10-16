package game.geom;

import game.Util;

import java.io.Serializable;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Rect implements Serializable {

	private int x, y;
	private int width, height;
	private Color color;
	private int mTexture = -1;

	public Rect (int x, int y, int w, int h) {
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

	public int getX () {
		return x;
	}

	public void setX (int x) {
		this.x = x;
	}

	public int getY () {
		return y;
	}

	public void setY (int y) {
		this.y = y;
	}

	public int getWidth () {
		return width;
	}

	public void setWidth (int width) {
		this.width = width;
	}

	public int getHeight () {
		return height;
	}

	public void setHeight (int height) {
		this.height = height;
	}

	public void setPosition (int x, int y) {
		setX (x);
		setY (y);
	}

}
