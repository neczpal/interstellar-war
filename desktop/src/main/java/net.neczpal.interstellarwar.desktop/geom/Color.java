package net.neczpal.interstellarwar.desktop.geom;

import static org.lwjgl.opengl.GL11.glColor3f;

public enum Color {

	WHITE (236, 240, 241), RED (231, 76, 60), GREEN (46, 204, 113), PURPLE (155, 89, 182), YELLOW (241, 196, 15), TEAL (26, 188, 156), BLUE (41, 128, 185), BLACK (33, 30, 27);

	private float red;
	private float green;
	private float blue;

	/**
	 * Erstellt ein Farbe
	 *
	 * @param red   Die rote Farbe 0-255
	 * @param green Die grüne Farbe 0-255
	 * @param blue  Die blaue Farbe 0-255
	 */
	Color (int red, int green, int blue) {
		this.red = red / 255.0f;
		this.green = green / 255.0f;
		this.blue = blue / 255.0f;
	}

	/**
	 * Stellt die Farbe in OpenGL ein
	 */
	public void setGLColor () {
		glColor3f (red, green, blue);
	}
}
