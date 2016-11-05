package net.neczpal.interstellarwar.common;

public class Log {

	private String mClassName;

	public Log (Object object) {
		mClassName = object.toString ();
	}

	public void e (Object object) {
		System.err.println (getTimeStamp () + ":\t" + mClassName + ":\tERROR:\t" + object);
	}

	public void i (Object object) {
		System.out.println (getTimeStamp () + ":\t" + mClassName + ":\tINFO:\t" + object);
	}

	public void w (Object object) {
		System.out.println (getTimeStamp () + ":\t" + mClassName + ":\tWARNING:\t" + object);
	}

	private String fillZeros (String s, int length) {
		while (s.length () < length) {
			s = "0" + s;
		}
		return s;
	}

	private String getTimeStamp () {
		long time = System.currentTimeMillis ();
		String milis = fillZeros (Long.toString (time % 1000), 3);
		time /= 1000;
		String secs = fillZeros (Long.toString (time % 60), 2);
		time /= 60;
		String mins = fillZeros (Long.toString (time % 60), 2);
		time /= 60;
		String hours = fillZeros (Long.toString (time % 24), 2);
		return hours + ":" + mins + ":" + secs + "." + milis;
	}
}
