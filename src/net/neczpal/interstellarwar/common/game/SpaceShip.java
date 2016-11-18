package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;

public class SpaceShip implements Serializable {
	public static final int SPACESHIP_TYPES = 11;
	private static final long serialVersionUID = 3683452581122892189L;
	private static final int SPACE_SHIP_SPEED = 6;

	private int mUnitsNumber;
	private int mOwnedBy;
	private int mMaxTick;
	private int mCurrentTick;

	private Planet mFromPlanet;
	private Planet mToPlanet;

	private float vx, vy;

	private int mTextureIndex;

	/**
	 * Erstellt ein Raumschiff
	 *
	 * @param fromPlanet  Der Planet woher das Raumschiff beginnt
	 * @param toPlanet    Der planet wohin das Raumschiff einfahrt
	 * @param currentTick Die aktuelle Zeitvariable des Raumschiffs
	 * @param unitsNumber Die Anzahl der Einheits, die transportiert wird
	 */
	public SpaceShip (Planet fromPlanet, Planet toPlanet, int currentTick, int unitsNumber) {
		mTextureIndex = (int) (Math.random () * SPACESHIP_TYPES);

		mUnitsNumber = unitsNumber;
		mCurrentTick = currentTick;
		mFromPlanet = fromPlanet;
		mToPlanet = toPlanet;

		mOwnedBy = mFromPlanet.getOwnedBy ();

		float lx = toPlanet.getX () - fromPlanet.getX ();
		float ly = toPlanet.getY () - fromPlanet.getY ();
		float length = fromPlanet.distance (toPlanet);

		mMaxTick = (int) (length / SPACE_SHIP_SPEED);

		vx = lx / length * SPACE_SHIP_SPEED;
		vy = ly / length * SPACE_SHIP_SPEED;
	}

	/**
	 * Incrementiert die Zeitvariable
	 */
	void tick () {
		mCurrentTick++;
	}

	/**
	 * @return Entscheidet ob das Raumschiff angekommt ist.
	 */
	boolean isArrived () {
		return mCurrentTick >= mMaxTick;
	}

	// GETTERS

	public int getUnitsNumber () {
		return mUnitsNumber;
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public int getCurrentTick () {
		return mCurrentTick;
	}

	public Planet getFromPlanet () {
		return mFromPlanet;
	}

	public Planet getToPlanet () {
		return mToPlanet;
	}

	public float getVx () {
		return vx;
	}

	public float getVy () {
		return vy;
	}

	public int getTextureIndex () {
		return mTextureIndex;
	}

}
