package Group3;

import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Sound {
	
	private SoundPerceptType type;
	private Point location;
	private int turnsLeft;
	private double radius; //30 for yell
	private Circle shape;
	
	public Sound(SoundPerceptType type, Point location, int turnsLeft, double radius) {

		this.type = type;
		this.location = location;
		this.turnsLeft = turnsLeft;
		this.radius = radius;

		if (type.getClass().getName().equals("Yell")) {
			this.shape = new Circle(radius);
			this.shape.setOpacity(0.3);
			this.shape.setFill(Color.INDIANRED);
			this.shape.setCenterX(location.getX());
			this.shape.setCenterY(location.getY());
		}
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

	//only for yell
	public Circle getShape() {
		if (type.getClass().getName().equals("Yell")) return this.shape;
		else return null;
	}

	public void updateShape() {
		if (type.getClass().getName().equals("Yell")) {
			if (getTurnsLeft() > 0) {
				this.radius = this.radius - this.radius / getTurnsLeft();
				this.shape.setRadius(this.radius);
			}
		}
	}
}
