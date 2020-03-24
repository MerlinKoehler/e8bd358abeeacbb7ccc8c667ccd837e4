package Group3;

import java.util.ArrayList;

import Interop.Geometry.Point;

public class GridMapStorage {

	private ArrayList<Tile> grid;
	
	public void GridMapStorage(){
		 grid = new ArrayList<Tile>();
		
		for (int i = 0; i < MainControl.storage.getHeight(); i++) {
			for (int j = 0; j < MainControl.storage.getWidth(); j++) {
				//as there could still be a remainder, also create a tile when a bit is not on the map anymore
				if( (i+1) <= (MainControl.storage.getHeight()) && (j+1)<=(MainControl.storage.getWidth())) {
					Tile tile = new Tile(new Point(i, j), new Point(i+1, j+1));
					grid.add(tile);
				}
			}
		}
	}
	
	//update the status of visited tiles - if you passed through it, update
	public void updateGrid(Point lastPosition, Point newPosition) {
		Point current = null;
		double check = lastPosition.getX() - newPosition.getX();
		double check2 = lastPosition.getY() - newPosition.getY();
		double need = Math.max(check, check2);
		
		double iterate =0;
		double iterate2 = 0;
		
		if (need ==check) {
			if (check != 0) {
				iterate = 1;
			}
			if (check2 != 0) {
				iterate2 = check2/check;
			}
		}
		else {
			if (check != 0) {
				iterate = check/check2;
			}
			if (check2 != 0) {
				iterate2 = 1;
			}
		}
		
		for (int i = 1; i < need; i++) {
			current = new Point(lastPosition.getX() + i * iterate, lastPosition.getY() + i * iterate2);
			
			for (int j = 0; j < grid.size(); j++) {
				grid.get(j).checkVisited(current);
			}
		}
		
	}
	
	public ArrayList<Tile> getGrid() {
		return grid;
	}
}
