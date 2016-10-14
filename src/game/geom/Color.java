package game.geom;

import static org.lwjgl.opengl.GL11.glColor3f;

/**
 * Created by neczp on 2016. 10. 09..
 */
public enum Color {

	WHITE (236, 240, 241), BLUE (41, 128, 185), RED (231, 76, 60), GREEN (46, 204, 113), PURLE (155, 89, 182), YELLOW (241, 196, 15), TEAL (26, 188, 156), BLACK (33, 30, 27);

	private float red;
	private float green;
	private float blue;

	Color (int red, int green, int blue) {
		this.red = red / 255.0f;
		this.green = green / 255.0f;
		this.blue = blue / 255.0f;
	}

	public float getRed () {
		return red;
	}

	public float getGreen () {
		return green;
	}

	public float getBlue () {
		return blue;
	}

	public void setGLColor () {
		glColor3f (red, green, blue);
	}
}
