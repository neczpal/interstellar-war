package game.map.interstellarwar;

import game.Util;
import game.geom.Circle;
import game.geom.Color;

import java.util.ArrayList;

public class Planet extends Circle {

	private int mOwnedBy;
	private int mUnitNumber;
	private ArrayList <Planet> mNeighbors;

	public Planet (double x, double y, double radius, int ownedBy, int unitNumber) {
		super (x, y, radius);
		mUnitNumber = unitNumber;
		mOwnedBy = ownedBy;
		mNeighbors = new ArrayList <> ();
	}

	public void linkTo (Planet other) {
		mNeighbors.add (other);
		other.mNeighbors.add (this);
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public void setOwnedBy (int ownedBy) {
		this.mOwnedBy = ownedBy;
	}

	public int getUnitNumber () {
		return mUnitNumber;
	}

	public void setUnitNumber (int unitNumber) {
		this.mUnitNumber = unitNumber;
	}

	public boolean isNeighbor (Planet planet) {
		return mNeighbors.contains (planet);
	}

	public int getIndex (Planet planet) {
		return mNeighbors.indexOf (planet);
	}

	public void addUnit () {
		if (mOwnedBy != 0)
			mUnitNumber++;
	}

	@Override
	public void draw () {
		Color.values ()[mOwnedBy].setGLColor ();
		super.draw ();

		Util.drawString (Integer.toString (mUnitNumber), getX (), getY (), Util.DEFAULT_FONTSIZE, Color.values ()[mOwnedBy]);
	}

	public void addUnit (int mUnitsNum) {
		mUnitNumber += mUnitsNum;
	}
}
