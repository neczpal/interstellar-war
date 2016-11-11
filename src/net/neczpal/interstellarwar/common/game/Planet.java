package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;
import java.util.ArrayList;

public class Planet implements Serializable {
	public static final int PLANET_TYPES = 18;
	private static final long serialVersionUID = 2683452581122892189L;

	private float mX;
	private float mY;
	private float mRadius;

	private int mTextureIndex;

	private int mOwnedBy;
	private int mUnitsNumber;
	private ArrayList <Planet> mNeighbors;

	public Planet (float x, float y, float radius, int ownedBy, int unitsNumber) {
		mTextureIndex = (int) (Math.random () * PLANET_TYPES);

		mX = x;
		mY = y;
		mRadius = radius;
		mUnitsNumber = unitsNumber;
		mOwnedBy = ownedBy;
		mNeighbors = new ArrayList <> ();
	}

	public void linkTo (Planet other) {
		mNeighbors.add (other);
		other.mNeighbors.add (this);
	}

	public float getX () {
		return mX;
	}

	public void setX (float x) {
		mX = x;
	}

	public float getY () {
		return mY;
	}

	public void setY (float y) {
		mY = y;
	}

	public float getRadius () {
		return mRadius;
	}

	public void setRadius (float radius) {
		mRadius = radius;
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public void setOwnedBy (int ownedBy) {
		this.mOwnedBy = ownedBy;
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

	public float distance (Planet p) {
		return (float) Math.sqrt (Math.pow (p.getX () - getX (), 2) + Math.pow (p.getY () - getY (), 2));
	}

	public boolean isInside (float px, float py) {
		return Math.pow (px - mX, 2) + Math.pow ((py - mY), 2) <= mRadius * mRadius;
	}


	public boolean isNeighbor (Planet planet) {
		return mNeighbors.contains (planet);
	}

	public void spawnUnit () {
		if (mOwnedBy > 0)
			mUnitsNumber++;
	}

	public void spaceShipArrived (SpaceShip spaceShip) {
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

}
