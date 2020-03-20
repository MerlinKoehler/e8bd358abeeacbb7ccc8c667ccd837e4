package Group3;

import java.util.ArrayList;
import java.util.List;

import Group3.StaticObjects.StaticObject;

public class Map {
	
	static double width;
	static double height;
	final double scalingFactor;
	//initial coordinates of the objects
	private ArrayList<StaticObject> initialStaticObjects;


	public String path;
	
	public Map(String path, double screenWidth, double screenHeight) {
		this.path = path;
		MapReader mr = new MapReader(path);
		this.scalingFactor = getScalingFactor(screenWidth, screenHeight, mr.getStorage().getWidth() , mr.getStorage().getHeight());

		this.height = mr.getStorage().getHeight()*scalingFactor;
		this.width = mr.getStorage().getWidth()*scalingFactor;
		
		//for scaling the map in the visualisation
		this.initialStaticObjects = mr.getStaticObjects();
	}

	public double getWidth() {	return this.width; }
	public double getHeight() { return this.height; }
	
	public double getScalingFactor(double screenWidth, double screenHeight, double mapWidth, double mapHeight) {
		double scalingFactor;
		double heightFactor = screenHeight/mapHeight;
		double widthFactor = screenWidth/mapWidth;
		if(heightFactor >= widthFactor) {
			scalingFactor = widthFactor;
		}else { 
			scalingFactor = heightFactor;
		}
		return scalingFactor;
	}
	public double getScalingFactor() {	return this.scalingFactor;	}
	public ArrayList<StaticObject> getAllObjects() {	return this.initialStaticObjects;}
	
}
