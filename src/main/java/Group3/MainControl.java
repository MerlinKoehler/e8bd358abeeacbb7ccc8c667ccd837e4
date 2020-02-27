package Group3;

import java.util.ArrayList;

/*
 * We can start the controller from here.
 */

public class MainControl {

	ArrayList<StaticObject> staticObjects = new ArrayList<StaticObject>();
	MapReader readMap;
	Storage storage;
	
	public MainControl(String path) {
		readMap = new MapReader(path);
		storage = readMap.getStorage();
		staticObjects = readMap.getStaticObjects();
	}
	
	public void doStep() {
		// do one step for one agent
	}
	
	public ArrayList<StaticObject> getStaticObject(){
		return staticObjects;
	}
}
