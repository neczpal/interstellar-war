package game.map.rockpaperscissors;

import game.geom.Color;
import game.geom.Point2D;
import game.geom.Rect;
import game.map.GameMap;
import game.server.Command;
import game.server.GameConnection;
import org.lwjgl.input.Mouse;

import java.io.Serializable;

/**
 * Created by neczp on 2016. 10. 16..
 */
public class RockPaperScissors extends GameMap {

	public static final String GAME_NAME = "ROCK-PAPER-SCISSORS";

	//PLAYER 1
	private int mSelectedP1 = 0;
	private Rect mRockButtonP1;
	private Rect mPaperButtonP1;
	private Rect mScissorButtonP1;

	//PLAYER 2
	private int mSelectedP2 = 0;
	private Rect mRockButtonP2;
	private Rect mPaperButtonP2;
	private Rect mScissorButtonP2;

	public RockPaperScissors () {
		setMaxUsers (2);
		setMapName ("");
		mRockButtonP1 = new Rect (200, 100, 50, 50);
		mPaperButtonP1 = new Rect (300, 100, 50, 50);
		mScissorButtonP1 = new Rect (400, 100, 50, 50);

		mRockButtonP2 = new Rect (200, 300, 50, 50);
		mPaperButtonP2 = new Rect (300, 300, 50, 50);
		mScissorButtonP2 = new Rect (400, 300, 50, 50);
	}

	@Override
	public void mouseEvent () {
		if (Mouse.isButtonDown (0) && mSelectedP1 == 0) {
			Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());
			int index = ((GameConnection) getConnection ()).getRoomIndex ();

			if (mRockButtonP1.isInside (point)) {
				getConnection ().send (Command.Type.GAME_DATA, GameCommand.MY_CHOICE, index, 1);
				mSelectedP1 = 1;
				mRockButtonP1.setColor (Color.TEAL);
			} else if (mPaperButtonP1.isInside (point)) {
				getConnection ().send (Command.Type.GAME_DATA, GameCommand.MY_CHOICE, index, 2);
				mSelectedP1 = 2;
				mPaperButtonP1.setColor (Color.TEAL);
			} else if (mScissorButtonP1.isInside (point)) {
				getConnection ().send (Command.Type.GAME_DATA, GameCommand.MY_CHOICE, index, 3);
				mSelectedP1 = 3;
				mScissorButtonP1.setColor (Color.TEAL);
			}
		}
	}

	@Override
	public void keyboardEvent () {

	}

	@Override
	public void draw () {
		mRockButtonP1.draw ();
		mPaperButtonP1.draw ();
		mScissorButtonP1.draw ();
		mRockButtonP2.draw ();
		mPaperButtonP2.draw ();
		mScissorButtonP2.draw ();
	}

	@Override
	public void loadMap (String fileName) throws NotValidMapException {

	}

	@Override
	public void loadData (Serializable[] data) {

	}

	@Override
	public Serializable[] toData () {
		return new Serializable[0];
	}

	@Override
	public boolean onGameThread () {
		return false;
	}

	@Override
	public void receiveClient (Command command) {
		switch ((GameCommand) command.data[0]) {
			case OTHER_CHOICE:
				int index = ((GameConnection) getConnection ()).getRoomIndex ();
				mSelectedP2 = index == 1 ? (int) command.data[2] : (int) command.data[1];
				if (mSelectedP2 == 1) {
					mRockButtonP2.setColor (Color.TEAL);
				} else if (mSelectedP2 == 2) {
					mPaperButtonP2.setColor (Color.TEAL);
				} else if (mSelectedP2 == 3) {
					mScissorButtonP2.setColor (Color.TEAL);
				}
				break;
		}
	}

	@Override
	public void receiveServer (Command command) {
		switch ((GameCommand) command.data[2]) {
			case MY_CHOICE:
				if ((int) command.data[3] == 1) {
					mSelectedP1 = (int) command.data[4];
				} else if ((int) command.data[3] == 2) {
					mSelectedP2 = (int) command.data[4];
				}
				if (mSelectedP1 != 0 && mSelectedP2 != 0)
					getConnection ().send (Command.Type.GAME_DATA, GameCommand.OTHER_CHOICE, mSelectedP1, mSelectedP2);
				break;
		}
	}

	enum GameCommand {
		MY_CHOICE, OTHER_CHOICE
	}
}
