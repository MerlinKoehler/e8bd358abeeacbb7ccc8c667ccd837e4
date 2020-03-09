package Group3;

import Group3.StaticObjects.*;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
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
	
	public VisualObject(StaticObject object, double scalingFactor) {
		this.object = object;
		this.shape = new Polygon(); 
		this.shape.getPoints().addAll(new Double[] {
			(double)object.getP1().getX()*scalingFactor, (double)object.getP1().getY()*scalingFactor, 
			(double)object.getP2().getX()*scalingFactor, (double)object.getP2().getY()*scalingFactor,
			(double)object.getP3().getX()*scalingFactor, (double)object.getP3().getY()*scalingFactor,
			(double)object.getP4().getX()*scalingFactor, (double)object.getP4().getY()*scalingFactor
		});
		if(object instanceof Wall) {
			shape.setFill(Color.PALEGREEN);
		}else if(object instanceof Window) {
			shape.setFill(Color.MEDIUMTURQUOISE);
		}else if(object instanceof Door) {
			shape.setFill(Color.TEAL);
		}else if(object instanceof Teleport) {
			shape.setFill(Color.ROYALBLUE);
		}else if(object instanceof TargetArea) {
			shape.setFill(Color.MEDIUMPURPLE);
		}else if(object instanceof SentryTower) {
			shape.setFill(Color.GREY);
		}else if(object instanceof ShadedArea) {
			shape.setFill(Color.DARKGRAY);
		}else if(object instanceof SpawnAreaGuards) {
			shape.setFill(Color.CORNFLOWERBLUE);
		}else if(object instanceof SpawnAreaIntruders) {
			shape.setFill(Color.PLUM);
		}
		this.shape.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
//            	ScrollPane scroll = new ScrollPane();
//            	scroll.setContent(new Label("here is the " + object.getClass().toString()));
            	System.out.println("here is the " + object.getClass().toString());
            }
  });
	}
	public Polygon getShape(){	return  this.shape;	}
}
