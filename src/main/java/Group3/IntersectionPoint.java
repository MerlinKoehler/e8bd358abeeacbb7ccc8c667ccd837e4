package Group3;

import Interop.Geometry.Point;

enum ObjectType {
	  ShadedArea,
	  Wall,
	  Teleport,
	  Door,
	  Window,
	  SentryTower,
	  TargetArea,
	  SpawnAreaGuards,
	  SpawnAreaIntruders,
}


public class IntersectionPoint {
	
	Point point;
	ObjectType objecttype;
	
	public IntersectionPoint(Point point, ObjectType objecttype) {
		this.point = point;
		this.objecttype = objecttype;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public ObjectType getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(ObjectType objecttype) {
		this.objecttype = objecttype;
	}
}
