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

	public int getUnitsNumber () {
		return mUnitsNumber;
	}

	public void setUnitsNumber (int unitsNumber) {
		mUnitsNumber = unitsNumber;
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public void setOwnedBy (int ownedBy) {
		mOwnedBy = ownedBy;
	}

	public int getMaxTick () {
		return mMaxTick;
	}

	public void setMaxTick (int maxTick) {
		mMaxTick = maxTick;
	}

	public int getCurrentTick () {
		return mCurrentTick;
	}

	public void setCurrentTick (int currentTick) {
		mCurrentTick = currentTick;
	}

	public Planet getFromPlanet () {
		return mFromPlanet;
	}

	public void setFromPlanet (Planet fromPlanet) {
		mFromPlanet = fromPlanet;
	}

	public Planet getToPlanet () {
		return mToPlanet;
	}

	public void setToPlanet (Planet toPlanet) {
		mToPlanet = toPlanet;
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

	public void tick () {
		mCurrentTick++;
	}

	public boolean isArrived () {
		return mCurrentTick >= mMaxTick;
	}

}
