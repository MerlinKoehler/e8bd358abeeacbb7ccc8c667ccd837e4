package Group9.gui2;

import Group9.agent.container.AgentContainer;
import Group9.agent.container.GuardContainer;
import Group9.agent.container.IntruderContainer;
import Group9.map.GameMap;
import Group9.map.dynamic.DynamicObject;
import Group9.map.dynamic.Pheromone;
import Group9.map.dynamic.Sound;
import Group9.map.objects.Window;
import Group9.map.objects.*;
import Group9.math.Vector2;
import Interop.Percept.Vision.FieldOfView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.List;

public class MainSceneTry extends Scene {

    class Settings{
        public boolean showText=false;
        public double agentScale = 5;
        public void toggleText(){
            showText = !showText;
        }
        public void toggleAgentScale(){
            if(agentScale==5){
                agentScale=1;
            }else{
                agentScale=5;
            }
        }
    }

    private StackPane mainStack;
    private Stage secondStage = new Stage();
    private StackPane canvasPane = new StackPane();
    private Canvas canvas = new Canvas(200,200);
    private Canvas canvasAgents = new Canvas(200,200);
    private double mapScale = 1;
    private Settings settings = new Settings();
    private AnimationTimer playbackAnimationTimer = null;
    private Slider animationSpeedSlider = new Slider(0,120,15);
    private Slider slider = new Slider(0.0,1,1);
    private Label play = new Label();
    private HBox mainbox = new HBox();
    private final GameMap map;
    private Gui gui;
    private boolean hasHistory = false;

    //Buttons
    private Button moveAgent;
    ///Agent
    private List<MapObject> elements;

    public MainSceneTry(StackPane mainStack, GameMap map,Gui gui) {

        super(mainStack);
        this.gui = gui;
        this.mainStack = mainStack;
        this.map = map;

        elements = map.getObjects();
        build();

    }

    public StackPane getMainStack() {
        return mainStack;
    }

    private void build(){

        calcScale();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        //add things to the root
        BorderPane root = new BorderPane();

        canvasPane.getChildren().add(canvas);
        canvasPane.getChildren().add(canvasAgents);
        mainbox.getChildren().add(canvasPane);

        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(20,20,20,0));
        rightBox.setSpacing(10);
        rightBox.getChildren().add(addLegend());
        root.setStyle("-fx-background-color: lightgray ;");
        root.setPadding(new Insets(20,20,20,20));
        moveAgent = new Button("move");
        moveAgent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            this.playbackAnimationTimer = new AnimationTimer() {

                private long lastFrame = System.nanoTime();
                private double drawFrames = 0;

                @Override
                public void handle(long now) {

                    double delta = (now - lastFrame);

                    final double frameTime = 1E9 / animationSpeedSlider.getValue();

                    if(delta >= frameTime)
                    {
                        if(gui.getMainController().getHistoryViewIndex().get() < gui.getMainController().getHistoryIndex())
                        {
                            this.lastFrame = now;
                            drawFrames += (delta / frameTime);

                            final int frames = (int) drawFrames;
                            drawFrames -= frames;
                            slider.setValue(gui.getMainController().getHistoryViewIndex().get() + frames);
                        }
                        else
                        {
                            moveAgent.setDisable(false);
                            this.stop();
                        }
                    }

                }
            };
            this.playbackAnimationTimer.start();

        });
        //set the scene
        double mapBoundWidth = bounds.getWidth();
        double mapBoundHeight = (bounds.getHeight()*9)/10;

        rightBox.getChildren().add(moveAgent);
        //rightBox.getChildren().add(pause);
        root.setRight(rightBox);
        root.setLeft(mainbox);
        draw();
        //mainStack.getChildren().add(mainbox);
        mainStack.getChildren().add(root);
    }

    public Stage getSecondStage(){
        return secondStage;
    }

    //add the legend
    public GridPane addLegend() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        javafx.scene.shape.Rectangle color;
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

    public void activateHistory(){
        hasHistory =true;
        int age = gui.getMainController().getHistoryIndex();
        slider.setMax(age);
        slider.setValue(age);
        slider.setMin(0);
        play.setDisable(false);
    }

    private void draw(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        g.setFill(GuiSettings.backgroundColor);
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        g.setFont(new Font("TimesRoman", 3*mapScale));
        for(MapObject e : elements){
            GraphicElement graphicElement = calculateGraphicElement(e);

            Vector2[] points = e.getArea().getAsPolygon().getPoints();

            final double[] xPoints = new double[points.length];
            final double[] yPoints = new double[points.length];

            for (int i = 0; i < points.length; i++) {
                Vector2 point = points[i];
                xPoints[i] = point.getX();
                yPoints[i] = point.getY();
            }

            if(graphicElement.fill){
                g.setFill(graphicElement.color);
                g.fillPolygon(scalePoints(xPoints,mapScale),scalePoints(yPoints,mapScale),4);
            }else {
                g.setStroke(graphicElement.color);
                g.setLineWidth(2);
                g.strokePolygon(scalePoints(xPoints,mapScale),scalePoints(yPoints,mapScale),4);
            }

            Vector2 center = e.getArea().getCenter();
            g.setTextAlign(TextAlignment.CENTER);
            if(settings.showText){
                g.setFill(Color.WHITE);
                g.fillText(graphicElement.text,center.getX()*mapScale,center.getY()*mapScale+1.5*mapScale);
            }
        }
    }

    public void drawMovables(List<GuardContainer> guards, List<IntruderContainer> intruders, List<DynamicObject<?>> objects){
        GraphicsContext g = canvasAgents.getGraphicsContext2D();
        g.clearRect(0,0,canvasAgents.getWidth(),canvasAgents.getHeight());
        for(DynamicObject<?> dynamicObject : objects)
        {
            if(dynamicObject instanceof Pheromone)
            {
                //g.setFill(GuiSettings.pheromoneColor);
                drawPheromone(g, (Pheromone) dynamicObject);

            }
            else if(dynamicObject instanceof Sound)
            {
                g.setFill(Color.ORCHID);
                //TODO draw sounds
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        g.setFill(GuiSettings.guardColor);
        for(GuardContainer movables : guards){
            drawAgent(g, movables);
        }

        g.setFill(GuiSettings.intruderColor);
        for(IntruderContainer movables : intruders){
            drawAgent(g, movables);
        }
    }
    //draw the pheromones
    private void drawPheromone(GraphicsContext g, Pheromone pheromone){
        Vector2 z = pheromone.getCenter();
        double radius = mapScale * pheromone.getRadius();
        double x = z.getX()*mapScale;
        double y = z.getY()*mapScale;
        switch (pheromone.getType()) {
            case Pheromone1:
                g.setFill(GuiSettings.pheromone1Color);
                break;
            case Pheromone2:
                g.setFill(GuiSettings.pheromone2Color);
                break;
            case Pheromone3:
                g.setFill(GuiSettings.pheromone3Color);
                break;
            case Pheromone4:
                g.setFill(GuiSettings.pheromone4Color);
                break;
            case Pheromone5:
                g.setFill(GuiSettings.pheromone5Color);
                break;
        }

        g.fillOval(x-radius/2,y-radius/2,radius,radius);
    }
    //for the agents
    private void drawAgent(GraphicsContext g, AgentContainer<?> agent) {

        Vector2 center = agent.getPosition().mul(mapScale);

        {
            //change the agentScale
            double radius = mapScale*5 * AgentContainer._RADIUS;
            double x = center.getX();
            double y = center.getY();
            g.fillOval(x-radius/2,y-radius/2,radius,radius);
        }

        {
            //change this to an arrow
            FieldOfView fov = agent.getFOV(map.getEffectAreas(agent));

            final double r = fov.getRange().getValue() * mapScale;
            final double alpha = fov.getViewAngle().getRadians();

            final double angle = agent.getDirection().rotated(alpha / 2).getClockDirection() - Math.PI / 2;
            g.setStroke(g.getFill());

            g.strokeArc(center.getX() - r, center.getY() - r, r*2, r*2,
                    (angle / (2 * Math.PI)) * 360,
                    fov.getViewAngle().getDegrees(), ArcType.ROUND);

        }

    }
    private void calcScale(){
        double height = canvasPane.getHeight();
        double width = canvasPane.getWidth();
        double scale = height/map.getGameSettings().getHeight();
        if(scale*map.getGameSettings().getWidth()< width){
            this.mapScale = scale*0.9;
        }else{
            this.mapScale = width/map.getGameSettings().getWidth()*0.9;
        }
    }

    protected GraphicElement calculateGraphicElement(MapObject element){
        if(element instanceof Wall){
            return new GraphicElement(GuiSettings.wallColor,"",true);
        }
        if(element instanceof TargetArea){
            return new GraphicElement(GuiSettings.targetAreaColor,"T",false);
        }
        if(element instanceof Spawn.Intruder){
            return new GraphicElement(GuiSettings.spawnIntrudersColor,"SI",false);
        }
        if(element instanceof Spawn.Guard){
            return new GraphicElement(GuiSettings.spawnGuardsColor,"SG",false);
        }
        if(element instanceof ShadedArea){
            return new GraphicElement(GuiSettings.shadedColor,"",true);
        }
        if(element instanceof Door){
            return new GraphicElement(GuiSettings.doorColor,"D",true);
        }
        if(element instanceof Window){
            return new GraphicElement(GuiSettings.windowColor,"",true);
        }
        if(element instanceof SentryTower){
            return new GraphicElement(GuiSettings.sentryColor,"",true);
        }
        if(element instanceof TeleportArea){
            return new GraphicElement(GuiSettings.teleportColor,"Tp",true);
        }
        System.out.println("Unknown");
        return new GraphicElement(Color.RED,"",false);
    }
    protected double[]  scalePoints(double[] points,double scale){
        double[] newPoints = new double[points.length];
        for(int i=0;i<points.length;i++){
            newPoints[i] = points[i]*scale;
        }
        return newPoints;
    }

    public boolean isHasHistory() {
        return hasHistory;
    }
}
