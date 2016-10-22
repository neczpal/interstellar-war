package game.map.interstellarwar;

import game.geom.Color;
import game.geom.Point2D;
import game.geom.Triangle;

/**
 * Created by neczp on 2016. 10. 21..
 */
public class SpaceShip extends Triangle {
	public static final int SPACE_SHIP_SIZE = 10;

	private int mUnitsNum;
	private int mOwnedBy;
	private int mMaxTick;
	private int mCurrentTick;
	private int vx, vy;
	private int mToPlanet;

	public SpaceShip (Point2D a, Point2D b, Point2D c, int ownedBy, int unitNum, int maxtick, int vx, int vy, int to) {
		super (a, b, c);
		mUnitsNum = unitNum;
		mOwnedBy = ownedBy;
		mMaxTick = maxtick;
		mCurrentTick = 0;
		mToPlanet = to;
		this.vx = vx;
		this.vy = vy;
	}

	public boolean tick () {
		move (vx, vy);
		mCurrentTick++;
		return mCurrentTick >= mMaxTick;
	}

	@Override
	public void draw () {
		Color.values ()[mOwnedBy].setGLColor ();
		super.draw ();
	}

	public int getmUnitsNum () {
		return mUnitsNum;
	}

	public int getmOwnedBy () {
		return mOwnedBy;
	}

	public int getToPlanet () {
		return mToPlanet;
	}

	public void moveUnitsTo (Planet planet) {
		if (planet.getOwnedBy () == mOwnedBy) {
			planet.addUnit (mUnitsNum);
		} else {
			planet.addUnit (-mUnitsNum);
			if (planet.getUnitNumber () < 0) {
				planet.setOwnedBy (mOwnedBy);
				planet.setUnitNumber (Math.abs (planet.getUnitNumber ()));
			}
		}
	}

}
