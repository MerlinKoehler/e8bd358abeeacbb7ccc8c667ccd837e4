package Group3;


import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

public class VisualAgent{

	private AgentState agent;
	private Ellipse shape;
	//private Line arrow;
	double scalingFactor;

	public VisualAgent(AgentState agent, double scalingFactor) {
		this.agent = agent;
		this.scalingFactor = scalingFactor;
		//this.arrow = this.agent.getTargetDirection();
		this.shape = new Ellipse(scalingFactor/2, scalingFactor/2);
		Point p = randomPosition(scalingFactor);
		// arrow direction
		double x2 = p.getX() + (scalingFactor)*Math.cos(agent.getTargetDirection().getRadians());
		double y2 = p.getY() + (scalingFactor)*Math.sin(agent.getTargetDirection().getRadians());
		Point p2 = new Point(x2, y2);
//		this.arrow = new Line(p.getX(), p.getY(), p2.getX(), p2.getY());
//		this.arrow.setStrokeWidth(2);
//		this.arrow.setStroke(Color.WHITE);
		this.shape.setCenterX(this.agent.getCurrentPosition().getX() * this.scalingFactor);
		this.shape.setCenterY(this.agent.getCurrentPosition().getY() * this.scalingFactor);
		//add check condition for checking if there is already an object
		if(agent.getAgent().getClass() == Guard.class) {
			this.shape.setFill(Color.BLUE);
		}else if(agent.getAgent().getClass() == Intruder.class){
			this.shape.setFill(Color.DEEPPINK);
		}else{
			System.out.println("not intruder nor guard");
		}
	}
	public void move(){
		if(this.agent != null) {
			//this.agen
		}
	}
	public Ellipse getShape() {	return this.shape;	}
//	public Line getDirection() {	return this.arrow;	}
//	public void setDirection(Direction d) {	
//		this.arrow = new Line();
//		this.arrow.setStartX(this.shape.getCenterX());
//		this.arrow.setStartY(this.shape.getCenterY());
//		this.arrow.setEndX(this.shape.getCenterX() + this.scalingFactor * Math.cos(d.getRadians()));
//		this.arrow.setEndY(this.shape.getCenterY() + this.scalingFactor * Math.sin(d.getRadians()));
//	}
	public Point getPosition() {	
		return new Point(this.getShape().getCenterX(), 
						this.getShape().getCenterY());	
	}
	public void setPosition(Point p) {
		this.shape.setCenterX(p.getX());;
		this.shape.setCenterY(p.getY());
	}
	public Point randomPosition(double scalingFactor) {
		double x = (scalingFactor/2)  + Math.random()*(Map.width - scalingFactor);
		double y = (scalingFactor/2) + Math.random()*(Map.height - scalingFactor);
		Point p = new Point(x, y);
		return p;
	}
}
