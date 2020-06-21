package Group3_Game_Controller;

import java.util.ArrayList;
import java.util.List;

import Group3_Game_Controller.StaticObjects.*;
import Interop.Geometry.Point;

public class Map {
	
	static double width;
	static double height;
	final double scalingFactor;
	//initial coordinates of the objects
	private ArrayList<StaticObject> initialStaticObjects;
	private ArrayList<AgentState> agents;
	private ArrayList<Pheromone> pheromones;

	public String path;
	private int pheromoneCoolDown;
	private double radiusPheromone;
	
	public Map(String path, double screenWidth, double screenHeight) {
		this.path = path;
		MapReader mr = new MapReader(path);
		this.scalingFactor = getScalingFactor(screenWidth, screenHeight, mr.getStorage().getWidth() , mr.getStorage().getHeight());

		height = mr.getStorage().getHeight()*scalingFactor;
		width = mr.getStorage().getWidth()*scalingFactor;
		this.pheromoneCoolDown = mr.getStorage().getPheromoneCoolDown();
		this.radiusPheromone = mr.getStorage().getRadiusPheromone() * this.scalingFactor;
		//for scaling the map in the visualisation
		this.initialStaticObjects = mr.getStaticObjects();
		this.agents = new ArrayList<AgentState>();
		this.pheromones = new ArrayList<Pheromone>();
		
		
		double h = mr.getStorage().getHeight();
		double w =mr.getStorage().getWidth();
		double thickness = 0.1;
		Wall border1 = new Wall(new Point(0,h), new Point(thickness, h), new Point(0,0), new Point(thickness,0));
		Wall border2 = new Wall(new Point(0,thickness), new Point(w, thickness), new Point(0,0), new Point(w,0));
		Wall border3 = new Wall(new Point(0,h), new Point(w, h), new Point(0,h-thickness), new Point(w,h-thickness));
		Wall border4 = new Wall(new Point(w-thickness,h), new Point(w,h), new Point(w-thickness,0), new Point(w,0));
		initialStaticObjects.add(border1);
		initialStaticObjects.add(border2);
		initialStaticObjects.add(border3);
		initialStaticObjects.add(border4);
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
	public double getScalingFactor() {	
		return this.scalingFactor;
	}
	public ArrayList<StaticObject> getAllObjects() {	
		return this.initialStaticObjects;
	}
	public void addAgents(ArrayList<AgentState> agents) {	
		this.agents = agents;
	}
	public ArrayList<AgentState> getAgents(){	
		return this.agents;
	}
	//public int getPheromoneCoolDown() {
	//	return this.pheromoneCoolDown;
	//}
	public double getPheromoneRadius() {
		return this.radiusPheromone;
	}
	public void addPheromones(ArrayList<Pheromone> pheromones) { 
		this.pheromones = pheromones; 
	}
	public ArrayList<Pheromone> getPheromones() { 
		return this.pheromones; 
	}
}
