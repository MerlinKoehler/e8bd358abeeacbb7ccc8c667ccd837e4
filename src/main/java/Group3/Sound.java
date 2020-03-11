package Group3;

import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;

public class Sound {
	
	private SoundPerceptType type;
	private Point location;
	private int turnsLeft;
	private double radius;
	
	public Sound(SoundPerceptType type, Point location, int turnsLeft, double radius) {
		this.type = type;
		this.location = location;
		this.turnsLeft = turnsLeft;
		this.radius = radius;
	}
	
	public SoundPerceptType getType() {
		return type;
	}
	public void setType(SoundPerceptType type) {
		this.type = type;
	}
	
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public int getTurnsLeft() {
		return turnsLeft;
	}
	public void setTurnsLeft(int turnsLeft) {
		this.turnsLeft = turnsLeft;
	}
	
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
}
