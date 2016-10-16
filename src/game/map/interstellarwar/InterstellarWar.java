package game.map.interstellarwar;

import game.geom.Line;
import game.geom.Point2D;
import game.map.GameMap;
import game.server.Command;
import game.server.GameConnection;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by neczp on 2016. 10. 06..
 */
public class InterstellarWar extends GameMap {
	public static final String GAME_NAME = "Interstellar War";

	private int mPlanetNumber;
	private int mConnectionNumber;

	private ArrayList <Planet> mPlanets;
	private ArrayList <Integer[]> mConnections;

	private boolean mWasMouseDown = false;

	private Planet mSelectedPlanetFrom = null;
	private Planet mSelectedPlanetTo = null;


	public InterstellarWar () {
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
	}

	public void mouseEvent () {
		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

			for (Planet planet : mPlanets) {
				if (planet.isInside (point)) {
					mSelectedPlanetFrom = planet;
					break;
				}
			}
		} else if (!Mouse.isButtonDown (0) && mWasMouseDown) {
			mWasMouseDown = false;

			if (mSelectedPlanetFrom != null) {
				Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

				for (Planet planet : mPlanets) {
					if (planet.isInside (point) && planet.isNeighbor (mSelectedPlanetFrom)) {
						mSelectedPlanetTo = planet;
						int id = ((GameConnection) getConnection ()).getRoomIndex ();
						if (mSelectedPlanetFrom.getOwnedBy () == id) {
							getConnection ().send (Command.Type.GAME_DATA, GameCommand.MOVE_UNITS, mPlanets.indexOf (mSelectedPlanetFrom), mPlanets.indexOf (mSelectedPlanetTo));
						}
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
		mPlanets.forEach (Planet::draw);
		for (Integer[] integers : mConnections) {
			Planet planet1 = mPlanets.get (integers[0]);
			Planet planet2 = mPlanets.get (integers[1]);
			Line line = new Line (planet1.getCenter (), planet2.getCenter ());
			line.draw ();
		}
	}

	@Override
	public void loadMap (String fileName) throws NotValidMapException {
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
		try {
			File mapFile = new File (System.getProperty ("user.dir") + "/res/interstellarwar/" + fileName);

			FileReader fileReader = new FileReader (mapFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			setMapName (bufferedReader.readLine ());
			setMaxUsers (Integer.parseInt (bufferedReader.readLine ()));

			mPlanetNumber = Integer.parseInt (bufferedReader.readLine ());
			mConnectionNumber = Integer.parseInt (bufferedReader.readLine ());

			for (int i = 0; i < mPlanetNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");

				mPlanets.add (new Planet (Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
			}
			for (int i = 0; i < mConnectionNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");
				mConnections.add (new Integer[] {Integer.parseInt (params[0]), Integer.parseInt (params[1])});

				Planet from = mPlanets.get (Integer.parseInt (params[0]));
				Planet to = mPlanets.get (Integer.parseInt (params[1]));

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
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
		int i = 0;

		setMapName ((String) data[i++]);
		setMaxUsers ((int) data[i++]);

		mPlanetNumber = (int) data[i++];
		mConnectionNumber = (int) data[i++];

		for (int j = 0; j < mPlanetNumber; j++) {
			mPlanets.add (new Planet ((int) data[i++], (int) data[i++], (int) data[i++], (int) data[i++], (int) data[i++]));
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			int fromIndex = (int) data[i++];
			int toIndex = (int) data[i++];
			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);
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
			Planet planet = mPlanets.get (j);
			Point2D center = planet.getCenter ();
			list.add (center.getX ());
			list.add (center.getY ());
			list.add (planet.getRadius ());
			list.add (planet.getOwnedBy ());
			list.add (planet.getUnitNumber ());
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
			Thread.sleep (1500);
			for (Planet planet : mPlanets) {
				planet.addUnit ();
			}
			getConnection ().send (Command.Type.GAME_DATA, GameCommand.SPAWN_UNITS);
		} catch (InterruptedException e) {
			e.printStackTrace ();
		}
	}

	@Override
	public void receiveClient (Command command) {
		switch ((GameCommand) command.data[0]) {
			case SPAWN_UNITS:
				for (Planet planet : mPlanets) {
					planet.addUnit ();
				}
				break;
			case MOVE_UNITS:
				mPlanets.get ((int) command.data[1]).moveUnitsTo (mPlanets.get ((int) command.data[2]));
				break;
		}
	}

	@Override
	public void receiveServer (Command command) {
		switch ((GameCommand) command.data[2]) {
			case MOVE_UNITS:
				mPlanets.get ((int) command.data[3]).moveUnitsTo (mPlanets.get ((int) command.data[4]));
				getConnection ().send (new Command (Command.Type.GAME_DATA, GameCommand.MOVE_UNITS, command.data[3], command.data[4]));
				break;
		}
	}

	enum GameCommand {
		SPAWN_UNITS, MOVE_UNITS
	}
}
