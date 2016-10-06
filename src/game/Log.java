package game;

public class Log {

	private String mClassName;

	public Log (Object object) {
		mClassName = object.toString ();
	}

	public void e (Object object) {
		System.err.println (mClassName + ":\tERROR:\t" + object);
	}

	public void i (Object object) {
		System.out.println (mClassName + ":\tINFO:\t" + object);
	}

	public void w (Object object) {
		System.out.println (mClassName + ":\tWARNING:\t" + object);
	}
}
