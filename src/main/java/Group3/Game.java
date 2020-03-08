package Group3;


import Group3.StaticObjects.TargetArea;
import Group3.StaticObjects.Teleport;
import Group3.StaticObjects.Wall;
import Interop.Geometry.Point;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Game extends Application {
	
	/*
	 * Now, can just add stuff in here to get to perform the exploration 
	 */
	
	public void start(Stage primaryStage) throws Exception {
		try {
			//example
			Button randomButton = new Button("test");
			randomButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	System.out.println("you are in the gui screen :) ");
	            }
	        });
			//add things to the root
			BorderPane root = new BorderPane();
			root.setStyle("-fx-background-color: lightgray ;");
			//root.getChildren().add(addLegend());
			root.setPadding(new Insets(20,50,20,20));
			root.setRight(addLegend());
			
			//set the scene
	        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			BorderPane map = new BorderPane();
			Rectangle mapR = new Rectangle();
			mapR.setWidth((bounds.getWidth()*4)/5);
			mapR.setHeight((bounds.getHeight()*4)/5);
			Wall wa = new Wall(new Point(0,0),new Point((int)mapR.getWidth(),0),new Point((int)mapR.getWidth(),100),new Point(0,100));
			VisualObject w = new VisualObject(wa);
			
			TargetArea t = new TargetArea(new Point(20,120), new Point(70,120), new Point(70,160), new Point(20,160));
			VisualObject ta = new VisualObject(t);
			
			Teleport tel = new Teleport(new Point(100,200),new Point(180,200),new Point(180,280),new Point(100,280), new Point(300, 300));
			VisualObject tell = new VisualObject(tel);
			
			mapR.setFill(Color.BLACK);
			map.getChildren().addAll(mapR, w.getShape(), ta.getShape(), tell.getShape());
			root.setCenter(map);
						
			Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			System.out.println("Exception in creating GUI" + e.getMessage());
		}
		
	}
	
	public GridPane addLegend() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		
		Rectangle color; 
		String text = ""; 
		for(int i = 0; i < 9; i++) {
			color = new Rectangle();
			color.setWidth(50);
			color.setHeight(50);
			switch(i) {
			case 0:
				color.setFill(Color.PALEGREEN);
				text = "Wall";
				break;
			case 1:
				color.setFill(Color.MEDIUMTURQUOISE);
				text = "Window";
				break; 
			case 2:
				color.setFill(Color.TEAL);
				text = "Door"; 
				break; 
			case 3:
				color.setFill(Color.ROYALBLUE);
				text = "Teleport";
				break;
			case 4:
				color.setFill(Color.MEDIUMPURPLE);
				text = "Target Area";
				break; 
			case 5:
				color.setFill(Color.GREY);
				text = "Sentry Tower";
				break;
			case 6:
				color.setFill(Color.DARKGRAY);
				text = "Shaded Area";
				break;
			case 7:
				color.setFill(Color.CORNFLOWERBLUE);
				text = "Spawn Area Guards";
				break;
			case 8: 
				color.setFill(Color.PLUM);
				text = "Spawn Area Intruder";
				break;
			}
			grid.add(color, 0, i);
			Label label = new Label(text);
			grid.add(label, 1, i);
		}
		return grid;
	}
}
