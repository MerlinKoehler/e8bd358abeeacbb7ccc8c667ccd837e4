package GUI;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
 
public class MainScreen extends Application {
    
    public void start(Stage primaryStage) {       
        //it's set to your screen size
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();  
        
        //create the title, decide on its design
        Text title = new Text("Multi-agent surveillance");
        title.setCache(true);
        title.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 75));
        
        //create buttons with different functionalities
        Button enterGame = new Button();
        enterGame.setStyle("-fx-background-color: white");
        enterGame.setAlignment(Pos.CENTER);
        enterGame.setText("Explore!");
        
        enterGame.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   enterGame.setStyle("-fx-background-color: lightblue");
                  }
        });
        enterGame.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   enterGame.setStyle("-fx-background-color: white");
                  }
        });
        
        enterGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               Game game = new Game();
               try {
				game.start(primaryStage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        });
        
    
        enterGame.setMaxSize(200, 10);
        enterGame.setFont(Font.font(null, FontWeight.BOLD, 30));
        
        Button credits = new Button();
        credits.setStyle("-fx-background-color: white");
        credits.setAlignment(Pos.CENTER);
        credits.setText("Credits");
        credits.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   credits.setStyle("-fx-background-color: lightblue");
                  }
        });
        credits.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   credits.setStyle("-fx-background-color: white");
                  }
        });
        
        credits.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               //goes to a screen with all the credits of our group
            }
        });
        credits.setMaxSize(150, 75);
        credits.setFont(Font.font(null, FontWeight.BOLD, 25));
        
        
        //idk if we need it, but in case we want to change things
        Button settings = new Button();
        
        settings.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   settings.setStyle("-fx-background-color: lightblue");
                  }
        });
        settings.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent e) {
                   settings.setStyle("-fx-background-color: white");
                  }
        });
        
        settings.setStyle("-fx-background-color: white");
        settings.setAlignment(Pos.CENTER);
        settings.setText("Rules");
        settings.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               //goes to a screen with all the different settings we have
            }
        });
        settings.setMaxSize(150, 75);
        settings.setFont(Font.font(null, FontWeight.BOLD, 25));
        
        
        
        //add all the components to make one single screen
        VBox vert = new VBox();
        vert.setAlignment(Pos.CENTER);
        vert.setSpacing(100);
        HBox hor = new HBox();
        hor.setSpacing(125);
        hor.setAlignment(Pos.CENTER);
        
        hor.getChildren().addAll(settings, credits);
        vert.getChildren().addAll(title, hor, enterGame); 
        
        StackPane root = new StackPane();
        root.getChildren().add(vert);
        root.setStyle("-fx-background-color: lightgray ;");

        
        //create scene and stage
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        primaryStage.setTitle("Project 2.2 - Multi-agent surveillance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    //start the game
    public static void main(String[] args) {
        launch(args);
    }
}