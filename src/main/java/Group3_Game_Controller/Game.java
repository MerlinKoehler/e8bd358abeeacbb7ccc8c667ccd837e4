package Group3_Game_Controller;


import java.io.File;
import java.util.Arrays;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Game extends Application {
	
	/*
	 * Now, can just add stuff in here to get to perform the exploration 
	 */
	String path;
	public void start(Stage primaryStage) throws Exception {
		try {
			//add things to the root
			BorderPane root = new BorderPane();
			VBox rightBox = new VBox();
			rightBox.setPadding(new Insets(20,20,20,0));
			rightBox.setSpacing(10);
			rightBox.getChildren().add(addLegend());
			root.setStyle("-fx-background-color: lightgray ;");
			root.setPadding(new Insets(20,20,20,20));
			
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
	        gameController.pherStorage = new PheromoneStorage(gameController.getMapPane(), gameController.getMap().getScalingFactor());
	        gameController.soundStorage = new SoundStorage(gameController.getMapPane(), gameController.getMap().getScalingFactor());
	        gameController.getMap().addPheromones(gameController.pherStorage.getPheromones());
			Button moveAgent = new Button("move");
			moveAgent.setOnAction(e -> {
				gameController.animationLoop();
			});
//			Button pause = new Button("Pause");
//			pause.setOnAction(e -> {
//				gameController.pauseAnimation();
//			});
		
			rightBox.getChildren().add(moveAgent);
			//rightBox.getChildren().add(pause);
			root.setRight(rightBox);
			root.setCenter(gameController.getMapPane());			
			   
			Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			System.out.println("Exception in creating GUI" + Arrays.toString(e.getStackTrace()));
		}
	}
	
	public GridPane addLegend() {
		GridPane grid = new GridPane();
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
		
		GridPane circles = new GridPane();
		circles.setHgap(15);
		circles.setVgap(15);
		circles.setPadding(new Insets(10, 0, 0, 30));
		Circle circle; 
		String text2 = ""; 
		for(int i = 0; i < 7; i++) {
			circle = new Circle();
			circle.setRadius(30);
			switch(i) {
				case 0:
					circle.setFill(Color.rgb(20, 120, 200, 0.8));
					text2 = "Pheromone 1";
					break;
				case 1:
					circle.setFill(Color.rgb(100, 50, 100, 0.8));
					text2 = "Pheromone 2";
					break;
				case 2:
					circle.setFill(Color.rgb(10, 150, 130, 0.8));
					text2 = "Pheromone 3";
					break;
				case 3:
					circle.setFill(Color.rgb(100, 70, 180, 0.8));
					text2 = "Pheromone 4";
					break;
				case 4:
					circle.setFill(Color.rgb(180, 50, 180, 0.8));
					text2 = "Pheromone 5";
					break;
				case 5:
					circle.setFill(Color.INDIANRED);
					text2 = "Yell";
					break;
				case 6:
					circle.setFill(Color.LIGHTYELLOW);
					text2 = "Noise";
					break;
			}
			circles.add(circle, 0, i);
			Label label = new Label(text2);
			circles.add(label, 1, i);
		}
		
		GridPane all = new GridPane();
		all.add(grid, 0, 0);
		all.add(circles, 1, 0);
		return all;
	}
}
