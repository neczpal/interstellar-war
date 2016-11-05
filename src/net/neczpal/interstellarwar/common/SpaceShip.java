package net.neczpal.interstellarwar.common;

import java.io.Serializable;

public class SpaceShip implements Serializable {
	private static final long serialVersionUID = 3683452581122892189L;
	private static final int SPACE_SHIP_SPEED = 6;

	private int mUnitsNumber;
	private int mOwnedBy;
	private int mMaxTick;
	private int mCurrentTick;

	private Planet mFromPlanet;
	private Planet mToPlanet;

	private double vx, vy;

	public SpaceShip (Planet fromPlanet, Planet toPlanet, int currentTick, int unitsNumber) {
		mUnitsNumber = unitsNumber;
		mCurrentTick = currentTick;
		mFromPlanet = fromPlanet;
		mToPlanet = toPlanet;

		mOwnedBy = mFromPlanet.getOwnedBy ();

		double lx = toPlanet.getX () - fromPlanet.getX ();
		double ly = toPlanet.getY () - fromPlanet.getY ();
		double length = fromPlanet.distance (toPlanet);

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

	public double getVx () {
		return vx;
	}

	public double getVy () {
		return vy;
	}

	public void tick () {
		mCurrentTick++;
	}

	public boolean isArrived () {
		return mCurrentTick >= mMaxTick;
	}
}
