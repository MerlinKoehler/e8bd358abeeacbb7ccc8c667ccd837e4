package Group3_deprecated.StaticObjects;

import Interop.Geometry.Point;

import java.util.ArrayList;

public class StaticObject {

	private Point p1;//upper left
	private Point p2;//upper right
	private Point p3;//bottom left
	private Point p4;//bottom right
   
	public StaticObject(Point a, Point b, Point c, Point d) {

		ArrayList<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);

		double maxXCoor = Double.NEGATIVE_INFINITY;
		double maxYCoor = Double.NEGATIVE_INFINITY;
		double minXCoor = Double.POSITIVE_INFINITY;
		double minYCoor = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < points.size(); i++) {
			maxXCoor = Math.max(maxXCoor, points.get(i).getX());
			minXCoor = Math.min(minXCoor, points.get(i).getX());
			maxYCoor = Math.max(maxYCoor, points.get(i).getY());
			minYCoor = Math.min(minYCoor, points.get(i).getY());
		}
		
		this.p1 = new Point(minXCoor, maxYCoor);
		this.p2 = new Point(maxXCoor, maxYCoor);
		this.p3 = new Point(minXCoor, minYCoor);
		this.p4 = new Point(maxXCoor, minYCoor);
	}
	
    /**
     * @param x
     * @param y
     * @return true if given point lies inside the rectangle object
     */
    public boolean isInside(double x, double y) {
        if (x > p1.getX() && x < p4.getX() &&
                y < p1.getY() && y > p4.getY()) return true;
        return false;
    }

    public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	public Point getP3() {
		return p3;
	}

	public void setP3(Point p3) {
		this.p3 = p3;
	}
	
	public Point getP4() {
		return p4;
	}

	public void setP4(Point p4) {
		this.p4 = p4;
	}
}
