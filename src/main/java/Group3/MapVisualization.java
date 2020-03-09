package Group3;

import java.util.ArrayList;

import Group3.StaticObjects.StaticObject;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapVisualization {

	BorderPane pane = new BorderPane();
	Rectangle shape;
	Map map;
	public MapVisualization(Map map) {
		this.map = map;
		this.shape = new Rectangle(this.map.getWidth(), this.map.getHeight(), Color.BLACK);
		this.pane.getChildren().add(shape);

		for (StaticObject so : this.map.getAllObjects()) 
		{ 
			VisualObject obj = new VisualObject(so, this.map.getScalingFactor());
			this.pane.getChildren().add(obj.getShape());
		}
	}	
	public BorderPane getPane() {	return this.pane; 	}
}
