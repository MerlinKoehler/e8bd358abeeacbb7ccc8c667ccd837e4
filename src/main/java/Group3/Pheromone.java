package Group3;

import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pheromone {

	private SmellPerceptType type;
	private int turnsLeft;
	private Point location;
	private Circle shape;
	double scalingFactor;
	int radius;

	public Pheromone(SmellPerceptType type, Point location, int turnsLeft, double scalingFactor) {
		this.type = type;
		this.turnsLeft = turnsLeft;
		this.location = location;
		this.scalingFactor = scalingFactor;
		this.radius = 5;

		this.shape = new Circle(scalingFactor*this.radius);
		switch(type) {
			case Pheromone1: this.shape.setFill(Color.KHAKI);
			case Pheromone2: this.shape.setFill(Color.YELLOW);
			case Pheromone3: this.shape.setFill(Color.GOLD);
			case Pheromone4: this.shape.setFill(Color.GOLDENROD);
			case Pheromone5: this.shape.setFill(Color.DARKGOLDENROD);
		}
		this.shape.setCenterX(location.getX());
		this.shape.setCenterY(location.getY());
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

	public Circle getShape(){
		return this.shape;
	}

	public void updateShape() {
		if (getTurnsLeft() > 0) {
			this.radius = this.radius - this.radius / getTurnsLeft();
			this.shape.setRadius(this.radius);
		}
	}
	
}
