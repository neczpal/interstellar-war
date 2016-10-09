package game.map;

import game.geom.Line;
import game.geom.Point2D;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class GameMap2D extends GameMap {

	private int mConnectionNumber;
	private int mPlanetNumber;

	private ArrayList <Planet2D> mPlanets;
	private ArrayList <Integer[]> mConnections;

	public GameMap2D () {
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
	}

	@Override
	public void draw () {
		mPlanets.forEach (Planet2D::draw);
		for (Integer[] integers : mConnections) {
			Planet2D planet1 = mPlanets.get (integers[0]);
			Planet2D planet2 = mPlanets.get (integers[1]);
			Line line = new Line (planet1.getCenter (), planet2.getCenter ());
			line.draw ();
		}
	}

	@Override
	public void loadMap (String fileName) throws NotValidMapException {
		try {
			File mapFile = new File (System.getProperty ("user.dir") + "/res/maps2d/" + fileName);

			FileReader fileReader = new FileReader (mapFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			setName (bufferedReader.readLine ());
			setMaxUsers (Integer.parseInt (bufferedReader.readLine ()));

			mPlanetNumber = Integer.parseInt (bufferedReader.readLine ());
			mConnectionNumber = Integer.parseInt (bufferedReader.readLine ());

			for (int i = 0; i < mPlanetNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");

				mPlanets.add (new Planet2D (Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
			}
			for (int i = 0; i < mConnectionNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");
				mConnections.add (new Integer[] {Integer.parseInt (params[0]), Integer.parseInt (params[1])});

				Planet2D from = mPlanets.get (Integer.parseInt (params[0]));
				Planet2D to = mPlanets.get (Integer.parseInt (params[1]));

				from.linkTo (to);
			}

		} catch (FileNotFoundException exception) {
			throw new NotValidMapException ("Map is not found! : " + exception.toString ());
		} catch (IOException exception) {
			throw new NotValidMapException ("Map file cannot be read! : " + exception.toString ());
		} catch (IndexOutOfBoundsException | NullPointerException exception) {
			exception.printStackTrace ();
			throw new NotValidMapException ("Map format is not valid : " + exception.toString ());
		}
	}

	@Override
	public void loadData (Serializable[] data) {
		int i = 0;

		setName ((String) data[i++]);
		setMaxUsers ((int) data[i++]);

		mPlanetNumber = (int) data[i++];
		mConnectionNumber = (int) data[i++];

		for (int j = 0; j < mPlanetNumber; j++) {
			mPlanets.add (new Planet2D ((int) data[i++], (int) data[i++], (int) data[i++], (int) data[i++], (int) data[i++]));
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			int fromIndex = (int) data[i++];
			int toIndex = (int) data[i++];
			Planet2D from = mPlanets.get (fromIndex);
			Planet2D to = mPlanets.get (toIndex);
			from.linkTo (to);
			mConnections.add (new Integer[] {fromIndex, toIndex});
		}
	}

	@Override
	public Serializable[] toData () {
		ArrayList <Serializable> list = new ArrayList <> ();

		list.add (getName ());
		list.add (getMaxUsers ());
		list.add (mPlanetNumber);
		list.add (mConnectionNumber);

		for (int j = 0; j < mPlanetNumber; j++) {
			Planet2D planet2D = mPlanets.get (j);
			Point2D center = planet2D.getCenter ();
			list.add (center.getX ());
			list.add (center.getY ());
			list.add (planet2D.getRadius ());
			list.add (planet2D.getOwnedBy ());
			list.add (planet2D.getUnitNumber ());
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			Integer[] pair = mConnections.get (j);
			list.add (pair[0]);
			list.add (pair[1]);
		}
		Serializable[] data = new Serializable[list.size ()];
		return list.toArray (data);
	}
}
