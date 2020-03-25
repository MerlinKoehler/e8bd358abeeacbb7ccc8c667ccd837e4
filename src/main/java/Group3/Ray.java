package Group3;

import java.util.ArrayList;

import Interop.Geometry.Point;

public class Ray {
	
	private ArrayList<IntersectionPoint> IntersectionPoints;
	
	public Ray() {
		IntersectionPoints = new ArrayList<IntersectionPoint>();
	}
	
	public void setPoint(IntersectionPoint point) {
		IntersectionPoints.add(point);
	}
	
	public ArrayList<IntersectionPoint> getPoints(){
		return IntersectionPoints;
	}
}
