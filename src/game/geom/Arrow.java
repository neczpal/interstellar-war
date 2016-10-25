package game.geom;


/**
 * Created by neczp on 2016. 10. 25..
 */
public class Arrow extends Line {
	private Triangle triangle;

	public Arrow (Point2D from, Point2D to, double arrowSize) {
		super (from, to);

		double length = from.distance (to);
		double lx = to.getX () - from.getX ();
		double ly = to.getY () - from.getY ();
		double hx = lx / length * arrowSize;
		double hy = ly / length * arrowSize;

		Point2D a = new Point2D (to.getX (), to.getY ());
		Point2D b = new Point2D (to.getX () - hx / 2, to.getY () - hy / 2);
		Point2D c = new Point2D (to.getX () - hx / 2, to.getY () - hy / 2);
		b.rotate (to, -45);
		c.rotate (to, 45);

		triangle = new Triangle (a, b, c);
	}

	@Override
	public void draw () {
		super.draw ();
		triangle.draw ();
	}
}
