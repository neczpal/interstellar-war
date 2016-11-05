package net.neczpal.interstellarwar.common;

import java.io.Serializable;
import java.util.ArrayList;

public class Planet implements Serializable {
	private static final long serialVersionUID = 2683452581122892189L;

	private double mX;
	private double mY;
	private double mRadius;

	private int mOwnedBy;
	private int mUnitsNumber;
	private ArrayList <Planet> mNeighbors;

	public Planet (double x, double y, double radius, int ownedBy, int unitsNumber) {
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

	public double getX () {
		return mX;
	}

	public void setX (double x) {
		mX = x;
	}

	public double getY () {
		return mY;
	}

	public void setY (double y) {
		mY = y;
	}

	public double getRadius () {
		return mRadius;
	}

	public void setRadius (double radius) {
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

	public double distance (Planet p) {
		return Math.sqrt (Math.pow (p.getX () - getX (), 2) + Math.pow (p.getY () - getY (), 2));
	}

	public boolean isInside (double px, double py) {
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
