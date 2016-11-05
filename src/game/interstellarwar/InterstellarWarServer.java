package game.interstellarwar;

import game.connection.Command;
import game.connection.Room;

public class InterstellarWarServer {
	private InterstellarWarCore mInterstellarWarCore;
	private Room mConnection;

	public InterstellarWarServer (InterstellarWarCore interstellarWarCore, Room roomConnection) {
		mInterstellarWarCore = interstellarWarCore;
		mConnection = roomConnection;
	}

	//RECEIVE

	public void receive (Command command) {
		switch ((InterstellarWarCommand) command.data[1]) {
			case START_MOVE_SPACESHIP:
				if ((int) command.data[5] > 0) {
					mInterstellarWarCore.startMoveSpaceShip ((int) command.data[2], (int) command.data[3], (int) command.data[4], (int) command.data[5]);
					mConnection.send (Command.Type.GAME_COMMAND, InterstellarWarCommand.START_MOVE_SPACESHIP, command.data[2], command.data[3], command.data[4], command.data[5]);

					mConnection.send (Command.Type.GAME_COMMAND, InterstellarWarCommand.REFRESH_WHOLE_MAP, mInterstellarWarCore.getData ());
				}
				break;
		}
	}

	public InterstellarWarCore getCore () {
		return mInterstellarWarCore;
	}
}
