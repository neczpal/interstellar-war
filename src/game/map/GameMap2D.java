package game.map;

import game.geom.Line;
import game.geom.Point2D;
import game.server.Command;
import org.lwjgl.input.Mouse;

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

	private boolean mWasMouseDown = false;

	private Planet2D mSelectedPlanetFrom = null;
	private Planet2D mSelectedPlanetTo = null;


	public GameMap2D () {
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
	}

	public void mouseEvent () {
		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

			for (Planet2D planet2D : mPlanets) {
				if (planet2D.isInside (point)) {
					mSelectedPlanetFrom = planet2D;
					break;
				}
			}
		} else if (!Mouse.isButtonDown (0) && mWasMouseDown) {
			mWasMouseDown = false;

			if (mSelectedPlanetFrom != null) {
				Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

				for (Planet2D planet2D : mPlanets) {
					if (planet2D.isInside (point) && planet2D.isNeighbor (mSelectedPlanetFrom)) {
						mSelectedPlanetTo = planet2D;
						getConection ().send (Command.Type.GAME_DATA, GameCommand.MOVE_UNITS, mPlanets.indexOf (mSelectedPlanetFrom), mPlanets.indexOf (mSelectedPlanetTo));
						break;
					}
				}

			}
			mSelectedPlanetFrom = null;
			mSelectedPlanetTo = null;
		}
	}

	public void keyboardEvent () {

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

			setMapName (bufferedReader.readLine ());
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

		setMapName ((String) data[i++]);
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

		list.add (getMapName ());
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

	//ONLY SERVER
	@Override
	public void onGameThread () {
		try {
			Thread.sleep (1000);
			getConection ().send (Command.Type.GAME_DATA, GameCommand.SPAWN_UNITS);
		} catch (InterruptedException e) {
			e.printStackTrace ();
		}
	}

	@Override
	public void receiveClient (Command command) {
		switch ((GameCommand) command.data[0]) {
			case SPAWN_UNITS:
				for (Planet2D planet2D : mPlanets) {
					planet2D.addUnit ();
				}
				break;
			case MOVE_UNITS:
				mPlanets.get ((int) command.data[1]).moveUnitsTo (mPlanets.get ((int) command.data[2]));
				break;
		}
	}

	@Override
	public void receiveServer (Command command) {
		switch ((GameCommand) command.data[0]) {
			case MOVE_UNITS:
				getConection ().send (command);
				break;
		}
	}

	enum GameCommand {
		SPAWN_UNITS, MOVE_UNITS
	}
}
