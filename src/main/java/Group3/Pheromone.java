package Group3;

import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Pheromone {

	private SmellPerceptType type;
	private int turnsLeft;
	private Point location;
	private Ellipse shape;
	private double radius;
	private Storage storage = new Storage();

	public Pheromone(SmellPerceptType type, Point location, int turnsLeft, double radiusScaled) {
		this.type = type;
		this.turnsLeft = turnsLeft;
		this.location = location;
		this.radius = radiusScaled;

		this.shape = new Ellipse(this.radius, this.radius);
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

	public Ellipse getShape(){
		return this.shape;
	}
	
	public void updateShape() {
		this.radius = this.radius - (1 / ((storage.getPheromoneExpireRounds()) * storage.getRadiusPheromone()));
		this.shape.setRadiusX(this.radius);
		this.shape.setRadiusY(this.radius);
	}
	
}
