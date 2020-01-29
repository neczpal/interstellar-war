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

    private double mVx, mVy;

    private int mTextureIndex;

    /**
     * Erstellt ein Raumschiff
     *
     * @param fromPlanet  Der Planet woher das Raumschiff beginnt
     * @param toPlanet    Der Planet wohin das Raumschiff einfahrt
     * @param unitsNumber Die Anzahl der Einheits, die transportiert wird
     */
	SpaceShip (Planet fromPlanet, Planet toPlanet, int unitsNumber) {
		mTextureIndex = (int) (Math.random () * SPACESHIP_TYPES);

		mUnitsNumber = unitsNumber;
		mCurrentTick = 0;
		mFromPlanet = fromPlanet;
		mToPlanet = toPlanet;

		mOwnedBy = mFromPlanet.getOwnedBy ();

		float lx = toPlanet.getX () - fromPlanet.getX ();
		float ly = toPlanet.getY () - fromPlanet.getY ();
		float length = fromPlanet.distance (toPlanet);

		mMaxTick = (int) (length / SPACE_SHIP_SPEED);

		mVx = lx / length * SPACE_SHIP_SPEED;
		mVy = ly / length * SPACE_SHIP_SPEED;
	}

	/**
	 * Erstellt ein Raumschiff
     *
     * @param fromPlanet   Der Planet woher das Raumschiff beginnt
     * @param toPlanet     Der Planet wohin das Raumschiff einfahrt
     * @param vx           Die Geschwindigkeit von dem Raumshiff auf dem X-Achse
     * @param vy           Die Geschwindigkeit von dem Raumshiff auf dem Y-Achse
     * @param ownedBy      Die Nummer von Spieler, die diesem Raumschiff dominiert
     * @param unitsNumber  Die Anzahl der Einheits, die transportiert wird
     * @param currentTick  Die akutelle Zeitvariable
     * @param maxTick      Die maximalle Zeitvariable
     * @param textureIndex Das Index der Texture
     */
    SpaceShip (Planet fromPlanet, Planet toPlanet, double vx, double vy, int ownedBy, int unitsNumber, int currentTick, int maxTick, int textureIndex) {
        mTextureIndex = textureIndex;
        mUnitsNumber = unitsNumber;
        mCurrentTick = currentTick;
        mMaxTick = maxTick;
        mFromPlanet = fromPlanet;
        mToPlanet = toPlanet;
        mVx = vx;
        mVy = vy;
        mOwnedBy = ownedBy;
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

}
