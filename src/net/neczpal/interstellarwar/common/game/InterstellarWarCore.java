package net.neczpal.interstellarwar.common.game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandParamKey.*;

public class InterstellarWarCore extends Thread {
	public static final int BACKGROUND_TYPES = 5;

	private String mMapName;
	private int mMaxUsers;

	private HashMap<Integer, Planet> mPlanets;
	private HashMap<Road.RoadKey, Road> mRoads;
	private HashMap<Integer, SpaceShip> mSpaceShips;

	private int mSpaceshipIdCounter = 0;

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
		initData (data);
	}

	/**
	 * Einladet ein Spiel von einem File
	 *
	 * @param fileName der Name von der File
	 * @throws IOException falls die File nicht erreichbar ist
	 */
	private void loadMap (String fileName) throws IOException {
		mBackgroundTextureIndex = (int) (Math.random () * BACKGROUND_TYPES);

		mPlanets = new HashMap<> ();
		mRoads = new HashMap<> ();
		mSpaceShips = new HashMap<> ();

		File mapFile = new File (System.getProperty ("user.dir") + "/res/maps/" + fileName);

		FileReader fileReader = new FileReader (mapFile);
		BufferedReader bufferedReader = new BufferedReader (fileReader);

		mMapName = bufferedReader.readLine ();
		mMaxUsers = Integer.parseInt (bufferedReader.readLine ());

		int planetNumber = Integer.parseInt (bufferedReader.readLine ());
		int connectionNumber = Integer.parseInt (bufferedReader.readLine ());

		for (int i = 0; i < planetNumber; i++) {
			String[] params = bufferedReader.readLine ().split (" ");

			mPlanets.put (i, new Planet (i, Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
		}
		for (int i = 0; i < connectionNumber; i++) {
			String[] params = bufferedReader.readLine ().split (" ");
			int fromIndex = Integer.parseInt (params[0]);
			int toIndex = Integer.parseInt (params[1]);
			Road.RoadKey key = new Road.RoadKey (fromIndex, toIndex);

			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);

			from.linkTo (to);
			mRoads.put (key, new Road (key, from, to));
		}
    }

	/**
	 * @return Der List die enthält die Spieldata
	 */
	public JSONObject getData () {
		JSONObject data = new JSONObject ();

		data.put (MAP_TICK_NUMBER_KEY, mTickNumber);
		data.put (BG_TEXTURE_INDEX_KEY, mBackgroundTextureIndex);
		data.put (MAP_NAME_KEY, mMapName);
		data.put (MAP_MAX_USER_COUNT_KEY, mMaxUsers);

		JSONArray jsonPlanets = new JSONArray ();
		for (Planet planet : mPlanets.values ()) {
			JSONObject jsonPlanet = new JSONObject ();

			jsonPlanet.put (GAME_OBJECT_ID_KEY, planet.getId ());
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
		for (Road road : mRoads.values ()) {
			JSONObject jsonRoad = new JSONObject ();

			jsonRoad.put (FROM_ID_KEY, road.getFrom ().getId ());
			jsonRoad.put (TO_ID_KEY, road.getTo ().getId ());

			jsonRoads.put (jsonRoad);
		}
		data.put (ROADS_KEY, jsonRoads);

		JSONArray jsonSpaceships = new JSONArray ();
		for (SpaceShip spaceShip : mSpaceShips.values ()) {
			JSONObject jsonSpaceship = new JSONObject ();

			jsonSpaceship.put (GAME_OBJECT_ID_KEY, spaceShip.getId ());
			jsonSpaceship.put (FROM_ID_KEY, spaceShip.getFromPlanet ().getId ());
			jsonSpaceship.put (TO_ID_KEY, spaceShip.getToPlanet ().getId ());


			jsonSpaceship.put (VELOCITY_X_KEY, spaceShip.getVx ());
			jsonSpaceship.put (VELOCITY_Y_KEY, spaceShip.getVy ());

			jsonSpaceship.put (OWNER_KEY, spaceShip.getOwnedBy ());
			jsonSpaceship.put (UNIT_NUMBER_KEY, spaceShip.getUnitsNumber ());

//			jsonSpaceship.put (ROAD_ID_KEY, spaceShip.getRoad ().getId ());

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
		if (mTickNumber != data.getInt (MAP_TICK_NUMBER_KEY)) {
			System.err.println ("IMPORTANT tick dif:" + (mTickNumber - data.getInt (MAP_TICK_NUMBER_KEY)));
		}
		mTickNumber = data.getInt (MAP_TICK_NUMBER_KEY);
		mBackgroundTextureIndex = data.getInt (BG_TEXTURE_INDEX_KEY);
		mMapName = data.getString (MAP_NAME_KEY);
		mMaxUsers = data.getInt (MAP_MAX_USER_COUNT_KEY);

		JSONArray jsonPlanets = data.getJSONArray (PLANETS_KEY);
		JSONArray jsonRoads = data.getJSONArray (ROADS_KEY);
		JSONArray jsonSpaceships = data.getJSONArray (SPACESHIPS_KEY);

		int planetsCount = jsonPlanets.length ();
		int roadsCount = jsonRoads.length ();
		int spaceShipsCount = jsonSpaceships.length ();

		for (int j = 0; j < planetsCount; j++) {
			JSONObject jsonPlanet = jsonPlanets.getJSONObject (j);

			int id = jsonPlanet.getInt (GAME_OBJECT_ID_KEY);
			Planet planet = mPlanets.get (id);

			planet.setX (jsonPlanet.getDouble (POSITION_X_KEY));
			planet.setY (jsonPlanet.getDouble (POSITION_Y_KEY));
			planet.setRadius (jsonPlanet.getDouble (RADIUS_KEY));
			planet.setOwnedBy (jsonPlanet.getInt (OWNER_KEY));
			planet.setUnitsNumber (jsonPlanet.getInt (UNIT_NUMBER_KEY));
			planet.setTextureIndex (jsonPlanet.getInt (TEXTURE_INDEX_KEY));

		}
		// # No road is changed during game # - just in case code -
//		for (int j = 0; j < roadsCount; j++) {
//			JSONObject jsonRoad = jsonRoads.getJSONObject (j);
//
////			int id = jsonRoad.getInt (GAME_OBJECT_ID_KEY);
//			int fromId = jsonRoad.getInt (FROM_ID_KEY);
//			int toId = jsonRoad.getInt (TO_ID_KEY);
//			Road.RoadKey key = new Road.RoadKey (fromId, toId);
//
//			Road road = mRoads.get (key);
//
//
//			Planet from = mPlanets.get (fromId);
//			Planet to = mPlanets.get (toId);
//
////			Road road = new Road (id, from, to);
////
////			mRoads.put (id, road);
//		}

		for (int j = 0; j < spaceShipsCount; j++) {
			JSONObject jsonSpaceship = jsonSpaceships.getJSONObject (j);

			int id = jsonSpaceship.getInt (GAME_OBJECT_ID_KEY);

			int fromId = jsonSpaceship.getInt (FROM_ID_KEY);
			int toId = jsonSpaceship.getInt (TO_ID_KEY);

			Road.RoadKey roadKey = new Road.RoadKey (fromId, toId);
			Road road = mRoads.get (roadKey);

			Planet from = mPlanets.get (fromId);
			Planet to = mPlanets.get (toId);

			double vx = jsonSpaceship.getDouble (VELOCITY_X_KEY);
			double vy = jsonSpaceship.getDouble (VELOCITY_Y_KEY);

//			int roadId = jsonSpaceship.getInt (ROAD_ID_KEY);

			int ownedBy = jsonSpaceship.getInt (OWNER_KEY);
			int unitsNum = jsonSpaceship.getInt (UNIT_NUMBER_KEY);
			int curTick = jsonSpaceship.getInt (CURRENT_TICK_NUMBER_KEY);
			int maxTick = jsonSpaceship.getInt (MAXIMUM_TICK_NUMBER_KEY);
			int tex = jsonSpaceship.getInt (TEXTURE_INDEX_KEY);

			//is spaceship created already?
			if (mSpaceShips.containsKey (id)) {
				SpaceShip spaceShip = mSpaceShips.get (id);
				spaceShip.setVx (vx);
				spaceShip.setVy (vy);
				spaceShip.setRoad (road);
				spaceShip.setUnitsNumber (unitsNum);
				spaceShip.setCurrentTick (curTick);
				spaceShip.setMaxTick (maxTick);
				spaceShip.setTextureIndex (tex);
			} else {
				SpaceShip spaceShip = new SpaceShip (id, from, to, vx, vy, ownedBy, unitsNum, curTick, maxTick, tex, road);
				road.addSpaceship (spaceShip);
				mSpaceShips.put (id, spaceShip);
			}
		}
	}

	private void initData (JSONObject data) {
		mBackgroundTextureIndex = data.getInt (BG_TEXTURE_INDEX_KEY);
		mMapName = data.getString (MAP_NAME_KEY);
		mMaxUsers = data.getInt (MAP_MAX_USER_COUNT_KEY);

		mPlanets = new HashMap<> ();
		mRoads = new HashMap<> ();
		mSpaceShips = new HashMap<> ();

		JSONArray jsonPlanets = data.getJSONArray (PLANETS_KEY);
		JSONArray jsonRoads = data.getJSONArray (ROADS_KEY);
		JSONArray jsonSpaceships = data.getJSONArray (SPACESHIPS_KEY);

		int planetsCount = jsonPlanets.length ();
		int roadsCount = jsonRoads.length ();
		int spaceShipsCount = jsonSpaceships.length ();

		for (int j = 0; j < planetsCount; j++) {
			JSONObject jsonPlanet = jsonPlanets.getJSONObject (j);

			int id = jsonPlanet.getInt (GAME_OBJECT_ID_KEY);
			double x = jsonPlanet.getDouble (POSITION_X_KEY);
			double y = jsonPlanet.getDouble (POSITION_Y_KEY);
			double r = jsonPlanet.getDouble (RADIUS_KEY);
			int ownedBy = jsonPlanet.getInt (OWNER_KEY);
			int unitNum = jsonPlanet.getInt (UNIT_NUMBER_KEY);
			int tex = jsonPlanet.getInt (TEXTURE_INDEX_KEY);

			mPlanets.put (id, new Planet (id, x, y, r, ownedBy, unitNum, tex));
		}

		for (int j = 0; j < roadsCount; j++) {
			JSONObject jsonRoad = jsonRoads.getJSONObject (j);

			int fromId = jsonRoad.getInt (FROM_ID_KEY);
			int toId = jsonRoad.getInt (TO_ID_KEY);
			Road.RoadKey roadKey = new Road.RoadKey (fromId, toId);

			Planet from = mPlanets.get (fromId);
			Planet to = mPlanets.get (toId);
			from.linkTo (to);

			Road road = new Road (roadKey, from, to);

			mRoads.put (roadKey, road);
		}

		// # initialized maps doesn't have spaceships # - just in case -
		for (int j = 0; j < spaceShipsCount; j++) {
			JSONObject jsonSpaceship = jsonSpaceships.getJSONObject (j);

			int fromId = jsonSpaceship.getInt (FROM_ID_KEY);
			int toId = jsonSpaceship.getInt (TO_ID_KEY);

			Planet from = mPlanets.get (fromId);
			Planet to = mPlanets.get (toId);

			int id = jsonSpaceship.getInt (GAME_OBJECT_ID_KEY);

			double vx = jsonSpaceship.getDouble (VELOCITY_X_KEY);
			double vy = jsonSpaceship.getDouble (VELOCITY_Y_KEY);

			int ownedBy = jsonSpaceship.getInt (OWNER_KEY);
			int unitsNum = jsonSpaceship.getInt (UNIT_NUMBER_KEY);
			int curTick = jsonSpaceship.getInt (CURRENT_TICK_NUMBER_KEY);
			int maxTick = jsonSpaceship.getInt (MAXIMUM_TICK_NUMBER_KEY);
			int tex = jsonSpaceship.getInt (TEXTURE_INDEX_KEY);

			Road.RoadKey roadKey = new Road.RoadKey (fromId, toId);
			Road road = mRoads.get (roadKey);

			SpaceShip spaceShip = new SpaceShip (id, from, to, vx, vy, ownedBy, unitsNum, curTick, maxTick, tex, road);
			road.addSpaceship (spaceShip);

			mSpaceShips.put (id, spaceShip);
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
			Road.RoadKey roadKey = new Road.RoadKey (from, to);
			Road road = mRoads.get (roadKey);

			int id = mSpaceshipIdCounter++;
			SpaceShip spaceShip = new SpaceShip (id, fromPlanet, toPlanet, unitNumber);
			spaceShip.setRoad (road);

			fromPlanet.setUnitsNumber (0);
			road.addSpaceship (spaceShip);

			mSpaceShips.put (id, spaceShip);
		}
	}

	/**
	 * Incrementiert die Zeitvariable von allem Raumschiff, und entfernt, falls es angekommt ist.
	 */
	private void moveSpaceShips () {
		for (SpaceShip spaceShip : mSpaceShips.values ()) {
			spaceShip.tick ();
		}

		Iterator<SpaceShip> iterator = mSpaceShips.values ().iterator ();
		while (iterator.hasNext ()) {
			SpaceShip spaceShip = iterator.next ();
			if (spaceShip.isArrived ()) {
				spaceShip.getRoad ().removeSpaceships (spaceShip);
				spaceShip.getToPlanet ().spaceShipArrived (spaceShip);
				iterator.remove ();
			}
		}

		List<Integer> deleteCoreSpaceShips = new ArrayList<> ();
//
		for (Road road : mRoads.values ()) {
			List<SpaceShip> spaceShips = road.getSpaceShips ();
			List<SpaceShip> deleteRoadSpaceShips = new ArrayList<> ();
			if (spaceShips.size () > 1) {
				for (int i = 0; i < spaceShips.size (); i++) {
					SpaceShip spaceShip1 = spaceShips.get (i);
					for (int j = i + 1; j < spaceShips.size (); j++) {
						SpaceShip spaceShip2 = spaceShips.get (j);
						if (spaceShip1.isCollided (spaceShip2) && spaceShip1.getOwnedBy () != spaceShip2.getOwnedBy ()) {
							if (spaceShip1.getUnitsNumber () > spaceShip2.getUnitsNumber ()) {
								spaceShip1.setUnitsNumber (spaceShip1.getUnitsNumber () - spaceShip2.getUnitsNumber ());
								deleteCoreSpaceShips.add (spaceShip2.getId ());
								deleteRoadSpaceShips.add (spaceShip2);
							} else if (spaceShip2.getUnitsNumber () > spaceShip1.getUnitsNumber ()) {
								spaceShip2.setUnitsNumber (spaceShip2.getUnitsNumber () - spaceShip1.getUnitsNumber ());
								deleteCoreSpaceShips.add (spaceShip1.getId ());
								deleteRoadSpaceShips.add (spaceShip1);
							} else {
								spaceShip2.setUnitsNumber (0);
								spaceShip1.setUnitsNumber (0);
								deleteCoreSpaceShips.add (spaceShip1.getId ());
								deleteCoreSpaceShips.add (spaceShip2.getId ());
								deleteRoadSpaceShips.add (spaceShip1);
								deleteRoadSpaceShips.add (spaceShip2);
							}
						}

					}
					road.removeSpaceships (deleteRoadSpaceShips);
				}
			}
		}
		for (Integer key : deleteCoreSpaceShips) {
			mSpaceShips.remove (key);
		}
	}

	/**
	 * Schafft Einheiten auf alle Planeten
	 */
	private void spawnUnits () {
		for (Planet planet : mPlanets.values ()) {
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

	public List<Planet> getPlanets () {
		return new ArrayList<> (mPlanets.values ());
	}

	public List<Road> getRoads () {
		return new ArrayList<> (mRoads.values ());
	}

	public List<SpaceShip> getSpaceShips () {
		return new ArrayList<> (mSpaceShips.values ());
	}

	public Planet getPlanet (Integer id) {
		return mPlanets.get (id);
	}

	public SpaceShip getSpaceShip (Integer id) {
		return mSpaceShips.get (id);
	}

	public Road getRoad (Road.RoadKey key) {
		return mRoads.get (key);
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
