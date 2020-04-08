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
	private double radius;
	
	private double initialRadius;
	private double initialRounds;
	private double scalingFactor;

	public Pheromone(SmellPerceptType type, Point location, int turnsLeft, double radius, double scalingFactor) {
		this.type = type;
		this.turnsLeft = turnsLeft;
		this.location = location;
		this.radius = radius;
		
		this.initialRounds = turnsLeft;
		this.initialRadius = radius;
		this.scalingFactor = scalingFactor;

		this.shape = new Circle(this.radius*scalingFactor);
		this.shape.setCenterX(location.getX());
		this.shape.setCenterY(location.getY());
		switch(type) {
			case Pheromone1: 
				this.shape.setFill(Color.rgb(20, 120, 200, 0.8)); 
				break;
			case Pheromone2: 
				this.shape.setFill(Color.rgb(100, 50, 100, 0.8));	
				break;
			case Pheromone3: 
				this.shape.setFill(Color.rgb(10, 150, 130, 0.8));	
				break;
			case Pheromone4: 
				this.shape.setFill(Color.rgb(100, 70, 180, 0.8));	
				break;
			case Pheromone5: 
				this.shape.setFill(Color.rgb(180, 50, 180, 0.8));	
				break;
		}
		

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
		this.shape.setCenterX(this.location.getX());
		this.shape.setCenterY(this.location.getY());
	}

	public Circle getShape(){
		return this.shape;
	}
	
	public void updateShape() {
		this.radius = this.radius - (initialRadius/initialRounds);
		this.shape.setRadius(this.radius*scalingFactor);
	}
	
}
