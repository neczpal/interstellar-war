package net.neczpal.interstellarwar.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class InterstellarWarCore extends Thread {
	private String mMapName;
	private int mMaxUsers;

	private ArrayList <Planet> mPlanets;
	private ArrayList <Road> mRoads;
	private ArrayList <SpaceShip> mSpaceShips;

	private int mTickNumber = 0;
	private boolean mIsRunning = false;

	public InterstellarWarCore (String fileName) throws IOException {
		loadMap (fileName);
	}

	public InterstellarWarCore (ArrayList <Serializable> data) {
		setData (data);
	}

	// DATA FUNCTIONS
	private void loadMap (String fileName) throws IOException {
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

			mPlanets.add (new Planet (Double.parseDouble (params[0]), Double.parseDouble (params[1]), Double.parseDouble (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
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

	public ArrayList <Serializable> getData () {
		ArrayList <Serializable> list = new ArrayList <> ();

		list.add (mMapName);
		list.add (mMaxUsers);

		list.add (mPlanets.size ());
		list.add (mRoads.size ());
		list.add (mSpaceShips.size ());

		for (Planet planet : mPlanets) {
			list.add (planet);
		}
		for (Road road : mRoads) {
			list.add (mPlanets.indexOf (road.getFrom ()));
			list.add (mPlanets.indexOf (road.getTo ()));
		}
		for (SpaceShip spaceShip : mSpaceShips) {
			list.add (spaceShip);
		}
		return list;
	}

	public void setData (ArrayList <Serializable> data) {
		mPlanets = new ArrayList <> ();
		mRoads = new ArrayList <> ();
		mSpaceShips = new ArrayList <> ();

		int i = 0;

		mMapName = (String) data.get (i++);
		mMaxUsers = (int) data.get (i++);

		int planetNumber = (int) data.get (i++);
		int connectionNumber = (int) data.get (i++);
		int spaceShipNumber = (int) data.get (i++);

		for (int j = 0; j < planetNumber; j++) {
			Planet planet = (Planet) data.get (i++);
			mPlanets.add (planet);
		}

		for (int j = 0; j < connectionNumber; j++) {
			int fromIndex = (int) data.get (i++);
			int toIndex = (int) data.get (i++);

			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);
			from.linkTo (to);

			Road road = new Road (from, to);

			mRoads.add (road);
		}
		for (int j = 0; j < spaceShipNumber; j++) {
			SpaceShip spaceship = (SpaceShip) data.get (i++);
			mSpaceShips.add (spaceship);
		}
	}

	//GAME FUNCTION
	public void startMoveSpaceShip (int from, int to, int tickNumber, int unitNumber) {
		Planet fromPlanet = mPlanets.get (from);
		Planet toPlanet = mPlanets.get (to);

		mSpaceShips.add (new SpaceShip (fromPlanet, toPlanet, mTickNumber - tickNumber, unitNumber));

		fromPlanet.setUnitsNumber (fromPlanet.getUnitsNumber () - unitNumber);
	}

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

	private void spawnUnits () {
		for (Planet planet : mPlanets) {
			planet.spawnUnit ();
		}
	}

	//GAME LOOP
	@Override
	public void run () {
		mIsRunning = true;
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
		return mPlanets;
	}

	public ArrayList <Road> getRoads () {
		return mRoads;
	}

	public ArrayList <SpaceShip> getSpaceShips () {
		return mSpaceShips;
	}

	public int getTickNumber () {
		return mTickNumber;
	}

	public boolean isRunning () {
		return mIsRunning;
	}
}
