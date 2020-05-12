package Group9.gui2;

import Group9.agent.container.GuardContainer;
import Group9.agent.container.IntruderContainer;
import Group9.map.dynamic.DynamicObject;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Gui extends Application {
    private File mapFile = new File("./src/main/java/Group9/map/maps/test_2.map");
    private MainController mainController = new MainController(this,mapFile,true);
    private MainSceneTry scene;
    private Stage primary = new Stage();

    public static void main(String[] args) {
        launch(args);
    }

    public Gui returnGUI(){
        return this;
    }

    @Override
    public void start(Stage primaryStage) {

        //it's set to your screen size
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primary = primaryStage;

        //create the title, decide on its design
        Text title = new Text("Multi-agent surveillance");
        title.setCache(true);
        title.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 75));

        //create buttons with different functionalities
        Button enterGame = new Button();
        enterGame.setStyle("-fx-background-color: white");
        enterGame.setAlignment(Pos.CENTER);
        enterGame.setText("Explore!");
        enterGame.setMaxSize(200, 10);
        enterGame.setFont(Font.font(null, FontWeight.BOLD, 30));

        //add all the components to make one single screen
        VBox vert = new VBox();
        vert.setAlignment(Pos.CENTER);
        vert.setSpacing(100);
        HBox hor = new HBox();
        hor.setSpacing(125);
        hor.setAlignment(Pos.CENTER);

        hor.getChildren().addAll();
        vert.getChildren().addAll(title, hor, enterGame);

        StackPane root = new StackPane();
        root.getChildren().add(vert);
        root.setStyle("-fx-background-color: lightgray ;");

        //create scene and stage
        Scene sceneInitial = new Scene(root, bounds.getWidth(), bounds.getHeight());
        primaryStage.setTitle("Project 2.2 - Multi-agent surveillance");
        primaryStage.setScene(sceneInitial);
        primaryStage.show();

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
                primary.close();
                scene = new MainSceneTry(new StackPane(), mainController.getGame().getGameMap(),returnGUI());
                Stage stage2 = new Stage();
                stage2.initModality(Modality.APPLICATION_MODAL);
                stage2.initOwner(primary);
                stage2.setScene(scene);
                stage2.setTitle("Project 2.2 - Multi-agent surveillance");
                stage2.show();
            }
        });

        //Thread thread = new Thread(mainController);
        //thread.start();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("See Ya");
            mainController.kill();
        });
    }

    //change this with visual agent + visual object?
    public void drawMovables(List<GuardContainer> guards, List<IntruderContainer> intruders, List<DynamicObject<?>> objects){
        scene.drawMovables(guards, intruders, objects);
    }
    public void activateHistory(){
        if(!scene.isHasHistory()){
            scene.activateHistory();
        }
    }
    public Stage getPrimary() {
        return primary;
    }

    public MainController getMainController() {
        return mainController;
    }
    public void restartGame(boolean generateHistory){
        mainController.kill();
        mainController = new MainController(this,mapFile, generateHistory);
        Thread thread = new Thread(mainController);
        thread.start();
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public File getMapFile() {
        return mapFile;
    }
}
