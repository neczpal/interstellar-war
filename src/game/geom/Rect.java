package game.geom;

import java.io.Serializable;

import static org.lwjgl.opengl.GL11.*;
/**
 * Created by neczp on 2016. 10. 06..
 */
public class Rect implements Serializable {

	private int x, y;
	private int width, height;

	public Rect (int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void draw () {
		glBegin (GL_QUADS);
		{
			glVertex2i (x, y);
			glVertex2i (x, y + height);
			glVertex2i (x + width, y + height);
			glVertex2i (x + width, y);
		}
		glEnd ();
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
