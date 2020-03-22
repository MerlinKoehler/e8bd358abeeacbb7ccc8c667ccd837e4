package Group3.StaticObjects;

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

		ArrayList<Point> left = new ArrayList<>();
		ArrayList<Point> right = new ArrayList<>();

		for(int i=0; i<points.size(); i++){
			for(int j=0; j<points.size(); j++){
				if(i!=j){
					if(points.get(i).getX() < points.get(j).getX()) {
						if(!left.contains(points.get(i))){
							left.add(points.get(i));
						}
					}else if(points.get(i).getX() > points.get(j).getX()){
						if(!right.contains(points.get(i))){
							right.add(points.get(i));
						}
					}
				}
			}
		}

		if(left.get(0).getY() > left.get(1).getY()) {
			this.p1 = left.get(1);
			this.p3 = left.get(0);
		}else {
			this.p1 = left.get(0);
			this.p3 = left.get(1);
		}

		if(right.get(0).getY() > right.get(1).getY()) {
			this.p2 = right.get(1);
			this.p4 = right.get(0);
		}else {
			this.p2 = right.get(0);
			this.p4 = right.get(1);
		}

		/*
		p1 = upperleft;
		p2 = upperright;
		p3 = bottomleft;
		p4 = bottomright;
		*/
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
