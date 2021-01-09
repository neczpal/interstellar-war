package net.neczpal.interstellarwar.desktop.geom;

public class Rect {

	private float x, y;
	private float width, height;

	private Color mColor = Color.WHITE;
	private int mTexture = -1;

	/**
	 * Erstellt ein Rechteck
	 *
	 * @param x Die Position der X-Achse
	 * @param y Die Position der Y-Achse
	 * @param w Die Breite
	 * @param h Die Höhe
	 */
	public Rect (float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	/**
	 * Malt das Rechteck aus
	 */
	public void draw () {
		mColor.setGLColor ();
		GLUtil.drawRect (x, y, width, height, mTexture);
	}

	/**
	 * @param point Der Punkt
	 * @return Entscheidet ob der Punkt innerhalb ist
	 */
	public boolean isInside (Point point) {
		return x <= point.x && point.x <= x + width && y <= point.y && point.y <= y + height;
	}

	//GETTERS, SETTERS

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
