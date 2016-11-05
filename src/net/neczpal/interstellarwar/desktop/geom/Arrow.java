package net.neczpal.interstellarwar.desktop.geom;


public class Arrow extends Line {
	private Triangle triangle;

	public Arrow (Point from, Point to, double arrowSize) {
		super (from, to);

		double length = from.distance (to);
		double lx = to.getX () - from.getX ();
		double ly = to.getY () - from.getY ();
		double hx = lx / length * arrowSize;
		double hy = ly / length * arrowSize;

		Point a = new Point (to.getX (), to.getY ());
		Point b = new Point (to.getX () - hx / 2, to.getY () - hy / 2);
		Point c = new Point (to.getX () - hx / 2, to.getY () - hy / 2);
		b.rotate (to, -45);
		c.rotate (to, 45);

		triangle = new Triangle (a, b, c);
	}

	@Override
	public void draw () {
		super.draw ();
		triangle.draw ();
	}

	@Override
	public void setColor (Color color) {
		super.setColor (color);
		triangle.setColor (color);
	}
}
