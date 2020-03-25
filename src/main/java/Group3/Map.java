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
	public int getPheromoneCoolDown() {
		return this.pheromoneCoolDown;
	}
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
