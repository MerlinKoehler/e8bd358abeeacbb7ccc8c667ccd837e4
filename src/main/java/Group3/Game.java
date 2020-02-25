package Group3;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
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
			StackPane root = new StackPane();
			root.getChildren().add(randomButton);
			root.setStyle("-fx-background-color: lightgray ;");
			
			
			//set the scene
	        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();  
			Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			System.out.println("Exception in creating GUI" + e.getMessage());
		}
		
	}
}
