package game.connection;

import java.io.Serializable;

public interface Connection {
	void send (Command.Type type);

	void send (Command.Type type, Serializable... data);

	void send (Command command);
}
