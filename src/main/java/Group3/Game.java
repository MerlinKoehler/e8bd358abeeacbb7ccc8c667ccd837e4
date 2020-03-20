package Group3;


import java.io.File;
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
	String path;
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
			root.setPadding(new Insets(20,50,20,20));
			root.setRight(addLegend());
			
			//set the scene
	        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			try { 
	            File f = new File("samplemap"); 
	            String absolute = f.getAbsolutePath(); 
	            this.path = absolute + ".txt";
	        } 
	        catch (Exception e) { 
	            System.err.println(e.getMessage()); 
	        } 
			double mapBoundWidth = bounds.getWidth();
			double mapBoundHeight = (bounds.getHeight()*9)/10;
			MainControl gameController = new MainControl(this.path);
	        gameController.setHeightBound(mapBoundHeight);
	        gameController.setWidthBound(mapBoundWidth);
	        gameController.createVisualMap(path);
			root.setCenter(gameController.getMapPane());
			Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
			primaryStage.setScene(scene);
			primaryStage.show();
			for (int i = 0; i < 100; i++) {
	            gameController.doStep();
	        }
			
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
