package net.neczpal.interstellarwar.common.game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandParamKey.*;

public class InterstellarWarCore extends Thread {
	public static final int BACKGROUND_TYPES = 5;

	private String mMapName;
	private int mMaxUsers;

	private ArrayList <Planet> mPlanets;
	private ArrayList <Road> mRoads;
	private ArrayList <SpaceShip> mSpaceShips;

	private int mBackgroundTextureIndex;

	private int mTickNumber;
	private volatile boolean mIsRunning = false;

    /**
     * Erstellt ein Spiel-Core durch einen File
     *
     * @param fileName der Name von der File
     * @throws IOException falls die File nicht erreichbar ist
     * @see InterstellarWarCore#loadMap(String)
     */
    public InterstellarWarCore (String fileName) throws IOException {
        loadMap (fileName);
    }

	/**
	 * Erstellt ein Spiel-Core durch einen List
	 *
	 * @param data der List
	 * @see InterstellarWarCore#setData(JSONObject)
	 */
	public InterstellarWarCore (JSONObject data) {
		setData (data);
	}

	/**
	 * Einladet ein Spiel von einem File
	 *
	 * @param fileName der Name von der File
	 * @throws IOException falls die File nicht erreichbar ist
	 */
	private void loadMap (String fileName) throws IOException {
		mBackgroundTextureIndex = (int) (Math.random () * BACKGROUND_TYPES);

		mPlanets = new ArrayList <> ();
		mRoads = new ArrayList <> ();
		mSpaceShips = new ArrayList <> ();

		File mapFile = new File (System.getProperty ("user.dir") + "/res/maps/" + fileName);

		FileReader fileReader = new FileReader (mapFile);
		BufferedReader bufferedReader = new BufferedReader (fileReader);

		mMapName = bufferedReader.readLine ();
		mMaxUsers = Integer.parseInt (bufferedReader.readLine ());

		int planetNumber = Integer.parseInt (bufferedReader.readLine ());
		int connectionNumber = Integer.parseInt (bufferedReader.readLine ());

		for (int i = 0; i < planetNumber; i++) {
            String[] params = bufferedReader.readLine ().split (" ");

            mPlanets.add (new Planet (Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt(params[4])));
		}
		for (int i = 0; i < connectionNumber; i++) {
			String[] params = bufferedReader.readLine ().split (" ");
			int fromIndex = Integer.parseInt (params[0]);
			int toIndex = Integer.parseInt (params[1]);

			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);

			from.linkTo (to);
			mRoads.add (new Road (from, to));
        }
    }

	/**
	 * @return Der List die enthält die Spieldata
	 */
	public JSONObject getData () {
		JSONObject data = new JSONObject ();

		data.put (BG_TEXTURE_INDEX_KEY, mBackgroundTextureIndex);
		data.put (MAP_NAME_KEY, mMapName);
		data.put (MAP_MAX_USER_COUNT_KEY, mMaxUsers);

		JSONArray jsonPlanets = new JSONArray ();
		for (Planet planet : mPlanets) {
			JSONObject jsonPlanet = new JSONObject ();

			jsonPlanet.put (POSITION_X_KEY, planet.getX ());
			jsonPlanet.put (POSITION_Y_KEY, planet.getY ());
			jsonPlanet.put (RADIUS_KEY, planet.getRadius ());
			jsonPlanet.put (OWNER_KEY, planet.getOwnedBy ());
			jsonPlanet.put (UNIT_NUMBER_KEY, planet.getUnitsNumber ());
			jsonPlanet.put (TEXTURE_INDEX_KEY, planet.getTextureIndex ());

			jsonPlanets.put (jsonPlanet);
		}
		data.put (PLANETS_KEY, jsonPlanets);

		JSONArray jsonRoads = new JSONArray ();
		for (Road road : mRoads) {
			JSONObject jsonRoad = new JSONObject ();

			jsonRoad.put (FROM_INDEX_KEY, mPlanets.indexOf (road.getFrom ()));
			jsonRoad.put (TO_INDEX_KEY, mPlanets.indexOf (road.getTo ()));

			jsonRoads.put (jsonRoad);
		}
		data.put (ROADS_KEY, jsonRoads);

		JSONArray jsonSpaceships = new JSONArray ();
		for (SpaceShip spaceShip : mSpaceShips) {
			JSONObject jsonSpaceship = new JSONObject ();

			jsonSpaceship.put (FROM_INDEX_KEY, mPlanets.indexOf (spaceShip.getFromPlanet ()));
			jsonSpaceship.put (TO_INDEX_KEY, mPlanets.indexOf (spaceShip.getToPlanet ()));


			jsonSpaceship.put (VELOCITY_X_KEY, spaceShip.getVx ());
			jsonSpaceship.put (VELOCITY_Y_KEY, spaceShip.getVy ());

			jsonSpaceship.put (OWNER_KEY, spaceShip.getOwnedBy ());
			jsonSpaceship.put (UNIT_NUMBER_KEY, spaceShip.getUnitsNumber ());

			jsonSpaceship.put (CURRENT_TICK_NUMBER_KEY, spaceShip.getCurrentTick ());
			jsonSpaceship.put (MAXIMUM_TICK_NUMBER_KEY, spaceShip.getMaxTick ());

			jsonSpaceship.put (TEXTURE_INDEX_KEY, spaceShip.getTextureIndex ());

			jsonSpaceships.put (jsonSpaceship);
		}
		data.put (SPACESHIPS_KEY, jsonSpaceships);

		return data;
	}

	/**
	 * Einstellt die Spieldata durch der List
	 *
	 * @param data Der List die enthält die Spieldata
	 */
	public void setData (JSONObject data) {
		mBackgroundTextureIndex = data.getInt (BG_TEXTURE_INDEX_KEY);
		mMapName = data.getString (MAP_NAME_KEY);
		mMaxUsers = data.getInt (MAP_MAX_USER_COUNT_KEY);

		mPlanets = new ArrayList<> ();
		mRoads = new ArrayList<> ();
		mSpaceShips = new ArrayList<> ();

		JSONArray jsonPlanets = data.getJSONArray (PLANETS_KEY);
		JSONArray jsonRoads = data.getJSONArray (ROADS_KEY);
		JSONArray jsonSpaceships = data.getJSONArray (SPACESHIPS_KEY);

		int planetsCount = jsonPlanets.length ();
		int roadsCount = jsonRoads.length ();
		int spaceShipsCount = jsonSpaceships.length ();

		for (int j = 0; j < planetsCount; j++) {
			JSONObject jsonPlanet = jsonPlanets.getJSONObject (j);

			int x = jsonPlanet.getInt (POSITION_X_KEY);
			int y = jsonPlanet.getInt (POSITION_Y_KEY);
			int r = jsonPlanet.getInt (RADIUS_KEY);
			int ownedBy = jsonPlanet.getInt (OWNER_KEY);
			int unitNum = jsonPlanet.getInt (UNIT_NUMBER_KEY);
			int tex = jsonPlanet.getInt (TEXTURE_INDEX_KEY);

			mPlanets.add (new Planet (x, y, r, ownedBy, unitNum, tex));
		}

		for (int j = 0; j < roadsCount; j++) {
			JSONObject jsonRoad = jsonRoads.getJSONObject (j);

			int fromIndex = jsonRoad.getInt (FROM_INDEX_KEY);
			int toIndex = jsonRoad.getInt (TO_INDEX_KEY);

			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);
			from.linkTo (to);

			Road road = new Road (from, to);

			mRoads.add (road);
		}
		for (int j = 0; j < spaceShipsCount; j++) {
			JSONObject jsonSpaceship = jsonSpaceships.getJSONObject (j);

			int fromIndex = jsonSpaceship.getInt (FROM_INDEX_KEY);
			int toIndex = jsonSpaceship.getInt (TO_INDEX_KEY);

			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);

			double vx = jsonSpaceship.getDouble (VELOCITY_X_KEY);
			double vy = jsonSpaceship.getDouble (VELOCITY_Y_KEY);

			int ownedBy = jsonSpaceship.getInt (OWNER_KEY);
			int unitsNum = jsonSpaceship.getInt (UNIT_NUMBER_KEY);
			int curTick = jsonSpaceship.getInt (CURRENT_TICK_NUMBER_KEY);
			int maxTick = jsonSpaceship.getInt (MAXIMUM_TICK_NUMBER_KEY);
			int tex = jsonSpaceship.getInt (TEXTURE_INDEX_KEY);

			mSpaceShips.add (new SpaceShip (from, to, vx, vy, ownedBy, unitsNum, curTick, maxTick, tex));
		}
	}

	//GAME FUNCTION

	/**
	 * @param from Der Planet woher das Raumschiff abfahrt
	 * @param to   Der Planet wohin das Raumschiff ankommt
	 */
	public void startMoveSpaceShip (int from, int to) {
		Planet fromPlanet = mPlanets.get (from);
		Planet toPlanet = mPlanets.get (to);
		int unitNumber = fromPlanet.getUnitsNumber ();
		if (unitNumber > 0) {
			fromPlanet.setUnitsNumber (0);
			mSpaceShips.add (new SpaceShip (fromPlanet, toPlanet, unitNumber));
		}
	}

	/**
	 * Incrementiert die Zeitvariable von allem Raumschiff, und entfernt, falls es angekommt ist.
	 */
	private void moveSpaceShips () {
		for (SpaceShip spaceShip : mSpaceShips) {
			spaceShip.tick ();
		}

		Iterator <SpaceShip> iterator = mSpaceShips.iterator ();
		while (iterator.hasNext ()) {
			SpaceShip spaceShip = iterator.next ();
			if (spaceShip.isArrived ()) {
				spaceShip.getToPlanet ().spaceShipArrived (spaceShip);
				iterator.remove ();
			}
		}
	}

	/**
	 * Schafft Einheiten auf alle Planeten
	 */
	private void spawnUnits () {
		for (Planet planet : mPlanets) {
			planet.spawnUnit ();
		}
	}


	/**
	 * Das Spiel-Thread
	 */
	@Override
	public void run () {
		mIsRunning = true;
		mTickNumber = 1;
		while (mIsRunning) {
			try {
				Thread.sleep (50);
				mTickNumber++;

				moveSpaceShips ();

				if (mTickNumber % 32 == 0) {
					spawnUnits ();
				}

			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}
	}

	/**
	 * Beendet das Spiel
	 */
	public void stopGame () {
		mIsRunning = false;
	}


	//GETTERS
	public String getMapName () {
		return mMapName;
	}

	public int getMaxUsers () {
		return mMaxUsers;
	}

	public ArrayList <Planet> getPlanets () {
		return new ArrayList<>(mPlanets);
	}

	public ArrayList <Road> getRoads () {
		return new ArrayList<>(mRoads);
	}

	public ArrayList <SpaceShip> getSpaceShips () {
		return new ArrayList<>(mSpaceShips);
	}

	public int getBackgroundTextureIndex () {
		return mBackgroundTextureIndex;
	}

	public int getTickNumber () {
		return mTickNumber;
	}

	public boolean isRunning () {
		return mIsRunning;
	}


}
