package Group3;

import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;

public class Pheromone {

	private SmellPerceptType type;
	private int turnsLeft;
	private Point location;

	public Pheromone(SmellPerceptType type, Point location, int turnsLeft) {
		this.type = type;
		this.turnsLeft = turnsLeft;
		this.location = location;
	}
	
	public SmellPerceptType getType() {
		return type;
	}

	public void setType(SmellPerceptType type) {
		this.type = type;
	}

	public int getTurnsLeft() {
		return turnsLeft;
	}

	public void setTurnsLeft(int turnsLeft) {
		this.turnsLeft = turnsLeft;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}
	
}
