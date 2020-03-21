package Group3;

import java.util.ArrayList;

import Interop.Geometry.Point;

public class CreateGrid {

	public static ArrayList<Tile> createGrid(){
		ArrayList<Tile> grid = new ArrayList<Tile>();
		
		for (int i = 0; i < MainControl.storage.getHeight(); i++) {
			for (int j = 0; j < MainControl.storage.getWidth(); j++) {
				//as there could still be a remainder, also create a tile when a bit is not on the map anymore
				if( (i+1) < (MainControl.storage.getHeight()+1) && (j+1)<(MainControl.storage.getWidth()+1)) {
					Tile tile = new Tile(new Point(i, j), new Point(i+1, j+1));
					grid.add(tile);
				}
			}
		}
		return grid;
	}
}
