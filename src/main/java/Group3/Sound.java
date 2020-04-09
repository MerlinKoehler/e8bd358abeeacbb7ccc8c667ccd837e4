package Group3;

import Interop.Action.Yell;
import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Sound {
	
	private SoundPerceptType type;
	private Point location;
	private int turnsLeft;
	private double radius;
	private double initialRadius;
	private double initialRounds;
	private Circle shape;
	private double scalingFactor;

	
	public Sound(SoundPerceptType type, Point location, int turnsLeft, double radius, double scalingFactor) {

		this.type = type;
		this.location = location;
		this.turnsLeft = turnsLeft;
		this.radius = radius;

		this.initialRounds = turnsLeft;
		this.initialRadius = radius;
		this.scalingFactor = scalingFactor;

		switch (type) {
			case Noise:
				this.shape = new Circle(this.radius*scalingFactor);
				this.shape.setOpacity(0.5);
				this.shape.setFill(Color.LIGHTYELLOW);
				this.shape.setCenterX(location.getX());
				this.shape.setCenterY(location.getY());
				break;
			case Yell:
				this.shape = new Circle(this.radius*scalingFactor);
				this.shape.setOpacity(0.5);
				this.shape.setFill(Color.INDIANRED);
				this.shape.setCenterX(location.getX());
				this.shape.setCenterY(location.getY());
				break;
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

	public Circle getShape() {
		return this.shape;
	}

}
