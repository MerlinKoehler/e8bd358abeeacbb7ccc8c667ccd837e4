package Group3;

import Interop.Geometry.Point;

public class Tile {
	private Point topLeft;
	private Point bottomRight;
	private boolean visited = false;
	
	public Tile(Point topLeft, Point bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	public void checkVisited(Point coor) {
		if (coor.getX() >= topLeft.getX() && coor.getX() <= bottomRight.getX()) {
			if (coor.getY() <= topLeft.getY() && coor.getY() >= bottomRight.getY()) {
			this.visited = true;
			}
		}
	}
	
	public boolean getVisited() {
		return visited;
	}
}
