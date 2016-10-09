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

	@Override
	public void draw () {
		Color.values ()[mOwnedBy].setGLColor ();
		super.draw ();
		Point2D center = getCenter ();
		Util.drawString (Integer.toString (mUnitNumber), center.getX (), center.getY ());
	}
}
