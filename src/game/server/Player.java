package game.server;

import game.graphics.Rect;

/**
 * @author neczpal
 */
public class Player extends Rect {

	private int id;

	/**
	 * @param id Melyik játékos
	 */
	public Player (int id) {
		this (id, 0, 0);
	}

	public Player (int id, int x, int y) {
		this (id, x, y, 50, 50);
	}

	public Player (int id, int x, int y, int w, int h) {
		super (x, y, w, h);
		this.id = id;
	}

	public void move (int dx, int dy) {
		setX (getX () + dx);
		setY (getY () + dy);
	}

	public int getId () {
		return id;
	}

}
