package Group3_Game_Controller;

import java.util.ArrayList;

import Group3_Game_Controller.StaticObjects.*;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapVisualization {

	BorderPane pane = new BorderPane();
	Rectangle shape;
	Map map;
	ArrayList<VisualAgent> visualAgents = null;
	public MapVisualization(Map map) {
		this.map = map;
		this.shape = new Rectangle(this.map.getWidth(), this.map.getHeight(), Color.BLACK);
		this.pane.getChildren().add(shape);

		for (StaticObject so : this.map.getAllObjects()) 
		{ 
			VisualObject obj = new VisualObject(so, this.map.getScalingFactor());
			this.pane.getChildren().add(obj.getShape());
		}
		addVisualAgents();
	}
	public BorderPane getPane() {	
		return this.pane; 
	}
	public void addVisualAgents() {
		this.visualAgents = new ArrayList<VisualAgent>();
		for(AgentState s : this.map.getAgents()) {
			VisualAgent agent = new VisualAgent(s, this.map.scalingFactor);
			visualAgents.add(agent);
			this.pane.getChildren().add(agent.getShape());
			this.pane.getChildren().add(agent.getDirection());
		}
	}

	public VisualAgent getAgent(int i) {
		return this.visualAgents.get(i);
	}
	public ArrayList<VisualAgent> getVisualAgents(){
		return this.visualAgents;
	}

	public void moveAgentX(int turn, double x) {
		this.visualAgents.get(turn).getShape().setTranslateX(x * this.map.scalingFactor);
	}
	public void moveAgentY(int turn, double y) {
		this.visualAgents.get(turn).getShape().setTranslateY(y * this.map.scalingFactor);
	}
	public void setAgentPosition(int turn, Point p) {
		this.visualAgents.get(turn).setPosition(new Point(p.getX() * this.map.scalingFactor,	
				p.getY() * this.map.scalingFactor));
	}
}
