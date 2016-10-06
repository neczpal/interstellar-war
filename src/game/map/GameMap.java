package game.map;

import game.server.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author neczpal
 */
public class GameMap implements Serializable {
	static final long serialVersionUID = 42L;

	private HashMap <Integer, Player> mPlayers;

	public GameMap () {
		mPlayers = new HashMap <> ();
	}


	public void addPlayer (int id, Player player) {
		mPlayers.put (id, player);
	}

	public ArrayList <Player> getPlayers () {
		return new ArrayList <> (mPlayers.values ());
	}

	public Player findPlayerById (int id) {
		return mPlayers.get (id);
	}

	public void update (Integer[] player_data) {//#TODO UNDORITO
		for (int i = 1; i <= player_data[0] * 3; i += 3) {
			if (mPlayers.containsKey (player_data[i])) {
				mPlayers.get (player_data[i]).setPosition (player_data[i + 1], player_data[i + 2]);
			} else {
				mPlayers.put (player_data[i], new Player (player_data[i], player_data[i + 1], player_data[i + 2]));
			}
		}
	}

}
