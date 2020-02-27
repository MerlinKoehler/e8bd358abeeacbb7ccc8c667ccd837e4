package Group3;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class VisualObject {
	
	/*
	 * 			x	y 
	 * one		x1  y1
	 * two		x2  y2
	 * three    x3  y3
	 * four 	x4  y4
	 */
	StaticObject object;
	Polygon shape;
	
	public VisualObject(StaticObject object) {
		this.object = object;
		this.shape = new Polygon(); 
		this.shape.getPoints().addAll(new Double[] {
			(double)object.getX1(), (double)object.getY1(), 
			(double)object.getX2(), (double)object.getY2(),
			(double)object.getX3(), (double)object.getY3(),
			(double)object.getX4(), (double)object.getY4()
		});
		if(object instanceof Wall) {
			shape.setFill(Color.CHOCOLATE);
		}else if(object instanceof Window) {
			shape.setFill(Color.SKYBLUE);
		}else if(object instanceof Teleport) {
			shape.setFill(Color.MEDIUMORCHID);
		}else if(object instanceof TargetArea) {
			shape.setFill(Color.MEDIUMPURPLE);
		}else if(object instanceof Door) {
			shape.setFill(Color.DARKRED);
		}else if(object instanceof SentryTower) {
			shape.setFill(Color.GREY);
		}else if(object instanceof ShadedArea) {
			shape.setFill(Color.DARKGRAY);
		}else if(object instanceof SpawnAreaGuards) {
			shape.setFill(Color.CORNFLOWERBLUE);
		}else if(object instanceof SpawnAreaIntruder) {
			shape.setFill(Color.LIGHTCYAN);
		}
	}
}
