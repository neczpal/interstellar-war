package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Planet implements Serializable {
	public static final int PLANET_TYPES = 18;
	private static final long serialVersionUID = 2683452581122892189L;

	private float mX;
	private float mY;
	private float mRadius;

	private int mTextureIndex;

	private int mOwnedBy;
	private int mUnitsNumber;

	private List<Planet> mNeighbors;


	/**
	 * Erstellt einen Planet
	 *
	 * @param x           Position auf dem X-Achse
	 * @param y           Position auf dem Y-Achse
	 * @param radius      Radius von dem Planet
	 * @param ownedBy     Die Nummer von Spieler, die diesem Planet dominiert
	 * @param unitsNumber Die Anzahl der Einheit auf dem Planet
	 */
	Planet (float x, float y, float radius, int ownedBy, int unitsNumber) {
		this (x, y, radius, ownedBy, unitsNumber, (int) (Math.random () * PLANET_TYPES));
	}

	/**
	 * Erstellt einen Planet
	 *
	 * @param x           Position auf dem X-Achse
	 * @param y           Position auf dem Y-Achse
	 * @param radius      Radius von dem Planet
	 * @param ownedBy     Die Nummer von Spieler, die diesem Planet dominiert
	 * @param unitsNumber Die Anzahl der Einheit auf dem Planet
	 * @param tex         Die Texture des Planets
	 */
	Planet (float x, float y, float radius, int ownedBy, int unitsNumber, int tex) {
		mTextureIndex = tex;

		mX = x;
		mY = y;
		mRadius = radius;
		mUnitsNumber = unitsNumber;
		mOwnedBy = ownedBy;
		mNeighbors = new ArrayList <> ();
	}

	/**
	 * Verbindet dieses Planet mit ein anderen Planet
	 *
	 * @param other Der andere Planet
	 */
	void linkTo (Planet other) {
		mNeighbors.add (other);
		other.mNeighbors.add (this);
	}

	/**
	 * Schafft Einheit auf dem Planet, falls es dominiert ist
	 */
	void spawnUnit () {
		if (mOwnedBy > 0)
			mUnitsNumber++;
	}

	/**
	 * Ein Raumschiff ist auf diesem Planet angekommt
	 *
	 * @param spaceShip Das Raumschiff, das angekommt ist
	 */
	void spaceShipArrived (SpaceShip spaceShip) {
		if (spaceShip.getOwnedBy () == mOwnedBy) {
			mUnitsNumber += spaceShip.getUnitsNumber ();
		} else {
			mUnitsNumber -= spaceShip.getUnitsNumber ();
			if (mUnitsNumber < 0) {
				mOwnedBy = spaceShip.getOwnedBy ();
				mUnitsNumber = Math.abs (mUnitsNumber);
			}
		}
	}

	/**
	 * @param p der andere Planet
	 * @return Die Entfernung von dem anderen Planet
	 */
	public float distance (Planet p) {
		return (float) Math.sqrt (Math.pow (p.getX () - getX (), 2) + Math.pow (p.getY () - getY (), 2));
	}

	/**
	 * @param px Position auf dem X-Achse
	 * @param py Position auf dem Y-Achse
	 * @return Entscheidet ob ein Punkt (px, py) innerhalb ist
	 */
	public boolean isInside (float px, float py) {
		return Math.pow (px - mX, 2) + Math.pow ((py - mY), 2) <= mRadius * mRadius;
	}

	/**
	 * @param planet die andere Planet
	 * @return Entscheidet ob die andere Planet Nachbar von diesem Planet ist
	 */
	public boolean isNeighbor (Planet planet) {
		return mNeighbors.contains (planet);
	}

	//SETTER, GETTERS

	public float getX () {
		return mX;
	}

	public float getY () {
		return mY;
	}

	public float getRadius () {
		return mRadius;
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public int getUnitsNumber () {
		return mUnitsNumber;
	}

	public void setUnitsNumber (int unitsNumber) {
		this.mUnitsNumber = unitsNumber;
	}

	public int getTextureIndex () {
		return mTextureIndex;
	}

	public List<Planet> getNeighbors() {
		return mNeighbors;
	}

}
