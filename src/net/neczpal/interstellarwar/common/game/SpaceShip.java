package net.neczpal.interstellarwar.common.game;

import net.neczpal.interstellarwar.desktop.geom.Point;

import java.io.Serializable;

public class SpaceShip implements Serializable {
    public static final int SPACESHIP_TYPES = 11;
    private static final long serialVersionUID = 3683452581122892189L;
    private static final int SPACE_SHIP_SPEED = 6;

    private int mId;

    private int mUnitsNumber;
    private int mOwnedBy;
    private int mMaxTick;
    private int mCurrentTick;

    private Planet mFromPlanet;
    private Planet mToPlanet;
    private Road mRoad;

    private double mVx, mVy;

    private int mTextureIndex;

    /**
     * Erstellt ein Raumschiff
     *
     * @param fromPlanet  Der Planet woher das Raumschiff beginnt
     * @param toPlanet    Der Planet wohin das Raumschiff einfahrt
     * @param unitsNumber Die Anzahl der Einheits, die transportiert wird
     */
    SpaceShip (int id, Planet fromPlanet, Planet toPlanet,
               int unitsNumber) {
        mTextureIndex = (int) (Math.random () * SPACESHIP_TYPES);

        mId = id;
        mUnitsNumber = unitsNumber;
        mCurrentTick = 0;
        mFromPlanet = fromPlanet;
        mToPlanet = toPlanet;

        mOwnedBy = mFromPlanet.getOwnedBy ();

        double lx = toPlanet.getX () - fromPlanet.getX ();
        double ly = toPlanet.getY () - fromPlanet.getY ();
        double length = fromPlanet.distance (toPlanet);

        mMaxTick = (int) (length / SPACE_SHIP_SPEED);

        mVx = lx / length * SPACE_SHIP_SPEED;
        mVy = ly / length * SPACE_SHIP_SPEED;
    }

//	/**
//	 * Erstellt ein Raumschiff
//     *
//     * @param fromPlanet   Der Planet woher das Raumschiff beginnt
//     * @param toPlanet     Der Planet wohin das Raumschiff einfahrt
//     * @param vx           Die Geschwindigkeit von dem Raumshiff auf dem X-Achse
//     * @param vy           Die Geschwindigkeit von dem Raumshiff auf dem Y-Achse
//     * @param ownedBy      Die Nummer von Spieler, die diesem Raumschiff dominiert
//     * @param unitsNumber  Die Anzahl der Einheits, die transportiert wird
//     * @param currentTick  Die akutelle Zeitvariable
//     * @param maxTick      Die maximalle Zeitvariable
//     * @param textureIndex Das Index der Texture
//     */
//    SpaceShip (Planet fromPlanet, Planet toPlanet,
//               double vx, double vy,
//               int ownedBy, int unitsNumber,
//               int currentTick, int maxTick,
//               int textureIndex) {
//
//        mTextureIndex = textureIndex;
//        mUnitsNumber = unitsNumber;
//        mCurrentTick = currentTick;
//        mMaxTick = maxTick;
//        mFromPlanet = fromPlanet;
//        mToPlanet = toPlanet;
//        mVx = vx;
//        mVy = vy;
//        mOwnedBy = ownedBy;
//	}

    SpaceShip (int id,
               Planet fromPlanet, Planet toPlanet,
               double vx, double vy,
               int ownedBy, int unitsNumber,
               int currentTick, int maxTick,
               int textureIndex,
               Road road) {
        mId = id;
        mTextureIndex = textureIndex;
        mUnitsNumber = unitsNumber;
        mCurrentTick = currentTick;
        mMaxTick = maxTick;
        mFromPlanet = fromPlanet;
        mToPlanet = toPlanet;
        mVx = vx;
        mVy = vy;
        mOwnedBy = ownedBy;
        mRoad = road;
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

    public double getVx () {
        return mVx;
    }

    public double getVy () {
        return mVy;
    }

    public int getMaxTick () {
        return mMaxTick;
    }

    public int getTextureIndex () {
        return mTextureIndex;
    }

    public Road getRoad () {
        return mRoad;
    }

    public double getPreciseX () {
        return (mToPlanet.getX () - mFromPlanet.getX ()) + getCurrentTick () * getVx ();
    }

    public double getPreciseY () {
        return (mToPlanet.getY () - mFromPlanet.getY ()) + getCurrentTick () * getVy ();
    }

    public Point getPosition () {
        return new Point (getPreciseX (), getPreciseY ());
    }

    public boolean isCollided (SpaceShip other) {
        return mCurrentTick + other.mCurrentTick >= mMaxTick - 2 &&//# -2 so no through lagging during a fram #TODO
                this.getToPlanet ().equals (other.getFromPlanet ()) &&
                this.getFromPlanet ().equals (other.getToPlanet ());
    }

    public void setUnitsNumber (int unitsNumber) {
        this.mUnitsNumber = unitsNumber;
    }


    public void setOwnedBy (int ownedBy) {
        this.mOwnedBy = ownedBy;
    }

    public void setMaxTick (int maxTick) {
        this.mMaxTick = maxTick;
    }

    public void setCurrentTick (int currentTick) {
        this.mCurrentTick = currentTick;
    }

    public void setFromPlanet (Planet fromPlanet) {
        this.mFromPlanet = fromPlanet;
    }

    public void setToPlanet (Planet toPlanet) {
        this.mToPlanet = toPlanet;
    }

    public void setRoad (Road road) {
        this.mRoad = road;
    }

    public void setVx (double mVx) {
        this.mVx = mVx;
    }

    public void setVy (double mVy) {
        this.mVy = mVy;
    }

    public void setTextureIndex (int mTextureIndex) {
        this.mTextureIndex = mTextureIndex;
    }

    public int getId () {
        return mId;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        SpaceShip spaceShip = (SpaceShip) o;
        return mId == spaceShip.mId;// &&
//                mOwnedBy == spaceShip.mOwnedBy &&
//                mMaxTick == spaceShip.mMaxTick &&
//                mCurrentTick == spaceShip.mCurrentTick &&
//                mTextureIndex == spaceShip.mTextureIndex &&
//                Objects.equals (mFromPlanet, spaceShip.mFromPlanet) &&
//                Objects.equals (mToPlanet, spaceShip.mToPlanet);
    }

}
