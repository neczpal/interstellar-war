package game.connection;

import java.io.Serializable;

/**
 * Created by neczp on 2016. 10. 10..
 */
public interface Connection {
	void send (Command.Type type);

	void send (Command.Type type, Serializable... data);

	void send (Command command);
}
