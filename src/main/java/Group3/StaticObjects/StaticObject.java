package Group3.StaticObjects;

import Interop.Geometry.Point;

public class StaticObject {

	private Point p1;
	private Point p2;
	private Point p3;
	private Point p4;
   
	public StaticObject(Point upperleft, Point upperright, Point bottomleft, Point bottomright) {
		this.p1 = upperleft;
		this.p2 = upperright;
		this.p3 = bottomleft;
		this.p4 = bottomright;
	}
	
    /**
     * @param x
     * @param y
     * @return true if given point lies inside the rectangle object
     */
    public boolean isInside(double x, double y) {
        if (x > p1.getX() && x < p4.getX() &&
                y > p1.getY() && y < p4.getY()) return true;
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
