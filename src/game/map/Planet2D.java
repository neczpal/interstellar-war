package game.map;

import game.Util;
import game.geom.Circle;
import game.geom.Color;
import game.geom.Point2D;

import java.util.ArrayList;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class Planet2D extends Circle {

	private int mOwnedBy;
	private int mUnitNumber;
	private ArrayList <Planet2D> mNeighbors;

	public Planet2D (int x, int y, int radius, int ownedBy, int unitNumber) {
		super (x, y, radius);
		mUnitNumber = unitNumber;
		mOwnedBy = ownedBy;
		mNeighbors = new ArrayList <> ();
	}

	public void linkTo (Planet2D other) {
		mNeighbors.add (other);
		other.mNeighbors.add (this);
	}

	public int getOwnedBy () {
		return mOwnedBy;
	}

	public int getUnitNumber () {
		return mUnitNumber;
	}

	public boolean isNeighbor (Planet2D planet2D) {
		return mNeighbors.contains (planet2D);
	}

	public int getIndex (Planet2D planet2D) {
		return mNeighbors.indexOf (planet2D);
	}

	public void moveUnitsTo (Planet2D planet2D) {
		if (planet2D.mOwnedBy == mOwnedBy) {
			planet2D.mUnitNumber += mUnitNumber;
			mUnitNumber = 0;
		} else {
			planet2D.mUnitNumber -= mUnitNumber;
			mUnitNumber = 0;
			if (planet2D.mUnitNumber < 0) {
				planet2D.mOwnedBy = mOwnedBy;
				planet2D.mUnitNumber = Math.abs (planet2D.mUnitNumber);
			}
		}
	}

	public void addUnit () {
		if (mOwnedBy != 0)
			mUnitNumber++;
	}

	@Override
	public void draw () {
		Color.values ()[mOwnedBy].setGLColor ();
		super.draw ();
		Point2D center = getCenter ();
		Util.drawString (Integer.toString (mUnitNumber), center.getX () - Util.DEFAULT_FONTSIZE / 2, center.getY () - Util.DEFAULT_FONTSIZE / 2);
	}
}
