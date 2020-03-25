package Group3;

import Group3.StaticObjects.*;
import Interop.Action.*;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.*;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.timer.Timer;

/*
 * We can start the controller from here.
 */

public class MainControl {

    ArrayList<StaticObject> staticObjects;
    ArrayList<AgentState> agentStates;

    List<Interop.Agent.Intruder> intruders;
    List<Interop.Agent.Guard> guards;

    ArrayList<Integer> targetZoneCount;
    double capturedIntruderCount = 0;

    MapReader readMap;
    public static Storage storage;
    PheromoneStorage pherStorage = new PheromoneStorage(this.mapPane);
    SoundStorage soundStorage = new SoundStorage(this.mapPane);

    //made this an object outside to use in the smellpercepts etc
    Object agent;
    int currentTurn = -1;
    //for the visualisation
    double widthBound;
    double heightBound;
    private ScenarioPercepts scenarioPercepts;

    //for the visualisation
    private MapVisualization mapVisualization;
    private static BorderPane mapPane;
    private Map map;
    private String path;
    //ArrayList<VisualAgent> visualAgents;
    StepAnimationTimer animation;

    /*
     * TODO: Implement reading objects from the map description file (in MapReader)
     * No need for StaticObject and its sublcasses, we already have the ObjectPercept class!! */

    public MainControl(String path) {

        this.path = path;
        // Read map file and settings
        readMap = new MapReader(path);
        storage = readMap.getStorage();
        scenarioPercepts = scenarioPercepts();
        staticObjects = readMap.getStaticObjects();

        // Initialize Guards and Intruders:
        intruders = AgentsFactory.createIntruders(storage.getNumIntruders());
        guards = AgentsFactory.createGuards(storage.getNumGuards());

        // Initialize the states of the agents
        // TODO: Add the correct coordinates from the map file. Actually the agents will be placed at 0,0
        agentStates = new ArrayList<AgentState>();

        for (Interop.Agent.Intruder intruder : intruders) {
            AgentState state = new AgentState(new Point(10, 10), Direction.fromDegrees(0), intruder);
            agentStates.add(state);
        }
        for (Interop.Agent.Guard guard : guards) {
            AgentState state = new AgentState(new Point(12, 12), Direction.fromDegrees(0), guard);
            agentStates.add(state);
        }

        targetZoneCount = new ArrayList<Integer>();
        //Initialize counters for Intruders in target zone
        for (int i = 0; i < intruders.size(); i++) {
            targetZoneCount.add(0);
        }
    }

    public MainControl(){
        this.path = "C:\\Users\\victo\\OneDrive\\Documents\\GitHub\\Project2.2\\e8bd358abeeacbb7ccc8c667ccd837e4\\samplemap.txt";
        // Read map file and settings
        readMap = new MapReader(path);
        storage = readMap.getStorage();
        scenarioPercepts = scenarioPercepts();
        staticObjects = readMap.getStaticObjects();
        agentStates = new ArrayList<AgentState>();
    }

    static boolean circleIntersect(Point a, Point b, Point c) {
        double agentRadius = 0.5;

        if((c.getX()+agentRadius < a.getX() && c.getX()+agentRadius < b.getX()) || (c.getX()-agentRadius > a.getX() && c.getX()-agentRadius > b.getX())){
            return false;
        }
        if((c.getY()+agentRadius < a.getY() && c.getY()+agentRadius < b.getY()) || (c.getY()-agentRadius > a.getY() && c.getY()-agentRadius > b.getY())){
            return false;
        }


        Point vectorAB = new Point (b.getX()-a.getX(), b.getY()-a.getY());
        Point vectorAC = new Point(c.getX()-a.getX(), c.getY()-a.getY());


        double dotProductABAC = vectorAB.getX()*vectorAC.getX() + vectorAB.getY()*vectorAC.getY();
        double magnitudeAB = Math.sqrt(Math.pow((b.getX() - a.getX()), 2) + Math.pow((b.getY() - a.getY()), 2));

        //closest point of Ab to C, projection of AC onto AB
        Point e = new Point(a.getX()+(dotProductABAC/(Math.pow(magnitudeAB,2)))*vectorAB.getX(), a.getY()+(dotProductABAC/(Math.pow(magnitudeAB,2)))*vectorAB.getY());
        //dist between E and C
        double euclEC = Math.sqrt(Math.pow((e.getX() - c.getX()), 2) + Math.pow((e.getY() - c.getY()), 2));

        if (euclEC <= agentRadius) {
            return true;
        } else return false;
    }

    static boolean segmentIntersect(Point p1, Point p2, Point p3, Point p4) {
        boolean abc = counterClockwise(p1, p2, p3);
        boolean abd = counterClockwise(p1, p2, p4);
        boolean cda = counterClockwise(p3, p4, p1);
        boolean cdb = counterClockwise(p3, p4, p2);

        return ((abc != abd) && (cda != cdb));
    }

    static boolean counterClockwise(Point p1, Point p2, Point p3) {
        return ((p3.getY() - p1.getY()) * (p2.getX() - p1.getX()) > (p2.getY() - p1.getY()) * (p3.getX() - p1.getX()));
    }

    public static void main(String[] args) {
        //args[0] = "C:\\Users\\victo\\OneDrive\\Documents\\GitHub\\Project2.2\\e8bd358abeeacbb7ccc8c667ccd837e4\\samplemap.txt";
        MainControl gameController = new MainControl(args[0]);


        for (int i = 0; i < 100; i++) {
            gameController.doStep();
        }
    }

    public boolean checkCollision(Point startPoint, Direction direction, Distance distance) {
        double agentRadius = 0.5;
        Point endPoint = new Point(distance.getValue() * Math.cos(direction.getRadians()) + startPoint.getX(), distance.getValue() * Math.sin(direction.getRadians()) + startPoint.getY());


        Point point1 = new Point(agentRadius * Math.cos(direction.getRadians() + (Math.PI / 2)) + startPoint.getX(), agentRadius * Math.sin(direction.getRadians() + (Math.PI / 2)) + startPoint.getY());
        Point point2 = new Point(agentRadius * Math.cos(direction.getRadians() - (Math.PI / 2)) + startPoint.getX(), agentRadius * Math.sin(direction.getRadians() - (Math.PI / 2)) + startPoint.getY());

        Point point3 = new Point(agentRadius * Math.cos(direction.getRadians() + (Math.PI / 2)) + endPoint.getX(), agentRadius * Math.sin(direction.getRadians() + (Math.PI / 2)) + endPoint.getY());
        Point point4 = new Point(agentRadius * Math.cos(direction.getRadians() - (Math.PI / 2)) + endPoint.getX(), agentRadius * Math.sin(direction.getRadians() - (Math.PI / 2)) + endPoint.getY());



        ArrayList<Point> rectangle = new ArrayList<>();
        rectangle.add(point1);
        rectangle.add(point2);
        rectangle.add(point3);
        rectangle.add(point4);

        ArrayList<StaticObject> statObj = new ArrayList<>();
        for(int i=0;i<staticObjects.size(); i++){
            if(staticObjects.get(i).getClass().getName().equals("Group3.StaticObjects.Wall")){
                statObj.add(staticObjects.get(i));
            }
        }




        for (int i = 0; i < statObj.size(); i++) {

            //for each wall check if any of its edge intersects w one of the 2 rectangle's side edge

            //point1 point3 -> edge 1
            //obj
            //p1p2 top edge
            //p1p3 left edge
            //p2p4 right edge
            //p3p4 bottom edge

            if (segmentIntersect(point1, point3, statObj.get(i).getP1(), statObj.get(i).getP2())) {
                System.out.println(statObj.get(i).toString());
                return true;
            }
            if (segmentIntersect(point1, point3, statObj.get(i).getP1(), statObj.get(i).getP3())) {
                System.out.println(statObj.get(i).toString());
                return true;
            }
            if (segmentIntersect(point1, point3, statObj.get(i).getP2(), statObj.get(i).getP4())) {
                System.out.println(statObj.get(i).toString());
                return true;
            }
            if (segmentIntersect(point1, point3, statObj.get(i).getP3(), statObj.get(i).getP4())) {
                System.out.println(statObj.get(i).toString());
                return true;
            }

            //point2 point4 -> edge 2
            //obj
            //p1p2 top edge
            //p1p3 left edge
            //p2p4 right edge
            //p3p4 bottom edge
            if (segmentIntersect(point2, point4, statObj.get(i).getP1(), statObj.get(i).getP2())) {
                return true;
            }
            if (segmentIntersect(point2, point4, statObj.get(i).getP1(), statObj.get(i).getP3())) {
                return true;
            }
            if (segmentIntersect(point2, point4, statObj.get(i).getP2(), statObj.get(i).getP4())) {
                return true;
            }
            if (segmentIntersect(point2, point4, statObj.get(i).getP3(), statObj.get(i).getP4())) {
                return true;
            }

            //check if endPos circle collides with any wall

            if (circleIntersect(statObj.get(i).getP1(), statObj.get(i).getP2(), endPoint)) {
                return true;
            }
            if (circleIntersect(statObj.get(i).getP1(), statObj.get(i).getP3(), endPoint)) {
                return true;
            }
            if (circleIntersect(statObj.get(i).getP2(), statObj.get(i).getP4(), endPoint)) {
                return true;
            }
            if (circleIntersect(statObj.get(i).getP3(), statObj.get(i).getP4(), endPoint)) {
                return true;
            }

        }

        for (int i = 0; i < agentStates.size(); i++) {
            if (!(agentStates.get(i).getCurrentPosition().getX() == startPoint.getX() && agentStates.get(i).getCurrentPosition().getY() == startPoint.getY())) {

                //check if collision with other agents is going to happen on the way to the endpoint
                if (circleIntersect(point1, point3, agentStates.get(i).getCurrentPosition())) {
                    return true;
                }
                if (circleIntersect(point2, point4, agentStates.get(i).getCurrentPosition())) {
                    return true;
                }

                //check if endpoint would collide with any other agent
                double eucl = Math.sqrt(Math.pow((endPoint.getX() - agentStates.get(i).getCurrentPosition().getX()), 2) + Math.pow((endPoint.getY() - agentStates.get(i).getCurrentPosition().getY()), 2));
                if (eucl <= 2 * agentRadius) {
                    return true;
                }
            }
        }

        return false;
    }


    public int doStep() {
    	// 1. Get the agent who does the next turn
    	agent = getAgentNextTurn();
    	AgentState state = agentStates.get(currentTurn);
    	if (agent.getClass() == Guard.class) {
    		//System.out.println("This is a Guard");
    		Guard guard = (Guard) agent;

    		// 2. Calculate the perception of the agent
    		GuardPercepts percept = new GuardPercepts(visionPercepts(state),
    				soundPercepts(state),
    				smellPercepts(state),
    				areaPercepts(state),
    				scenarioGuardPercepts(),
    				state.isLastActionExecuted());

    		// 3. Pass the perception to the agent and retrieve the action
    		//Interop.Action.GuardAction action = guard.getAction(percept);
    		Interop.Action.GuardAction action = new Interop.Action.Move(new Distance(2));

    		
    		// 4. Check if the agent is allowed to make a move
    		boolean legalAction = checkLegalGuardAction(state, action);

    		if (state.getPenalty() > 0) 
    			state.setPenalty(state.getPenalty() - 1);
    		// 6. Update the game state according to the action.
    		if (legalAction) {
    			updateAgentState(state, action);
    			state.setLastAction(action);
    			if(this.mapVisualization != null) {
    				this.mapVisualization.getAgent(currentTurn).setPosition(state.getCurrentPosition());
    				this.mapVisualization.getAgent(currentTurn).setDirection(state.getTargetDirection());
    			}
    		} else {
    			state.setLastAction(new NoAction());
    		}
    		state.setLastActionExecuted(legalAction);

    		// 7. Check the win / fininsh conditions
    		// 0 = not finished
    		// 1 = intruders win
    		// 2 = guards win
    		return (gameFinished());
    	} else if (agent.getClass() == Intruder.class) {
    		//System.out.println("This is a Intruder");
    		Intruder intruder = (Intruder) agent;
    		updateIntarget(state);

    		// 2. Calculate the perception of the agent
    		IntruderPercepts percept = new IntruderPercepts(state.getTargetDirection(),
    				visionPercepts(state),
    				soundPercepts(state),
    				smellPercepts(state),
    				areaPercepts(state),
    				scenarioIntruderPercepts(),
    				state.isLastActionExecuted());

    		// 3. Pass the perception to the agent and retrieve the action
    		//Interop.Action.IntruderAction action = intruder.getAction(percept);
    		Interop.Action.IntruderAction action = new Interop.Action.Sprint(new Distance(2));

    		// 4. Check if the agent is allowed to make a move
    		boolean legalAction = checkLegalIntruderAction(state, action);

    		if (state.getPenalty() > 0) 
    			state.setPenalty(state.getPenalty() - 1);

    		// 6. Update the game state according to the action.
    		if (legalAction) {
    			updateAgentState(state, action);
    			state.setLastAction(action);
    			if(this.mapVisualization != null) {
    				this.mapVisualization.getAgent(currentTurn).setPosition(state.getCurrentPosition());
    				this.mapVisualization.getAgent(currentTurn).setDirection(state.getTargetDirection());
    			}
    		} else {
    			state.setLastAction(new NoAction());
    		}
    		state.setLastActionExecuted(legalAction);

    		// 7. Check the win / fininsh conditions
    		// 0 = not finished
    		// 1 = intruders win
    		// 2 = guards win
    		return (gameFinished());
    	}
    	return -1;
    }

    private Object getAgentNextTurn() {
        currentTurn++;
        if (currentTurn >= agentStates.size()) {
            currentTurn = 0;
        }
        return agentStates.get(currentTurn).getAgent();
    }

    // Oskar
    private VisionPrecepts visionPercepts(AgentState state) {
        Set<ObjectPercept> objectPercepts = new HashSet<>();
        Point currentPosition = state.getCurrentPosition();

        double range;
        if (state.getAgent().getClass() == Intruder.class)
            range = storage.getViewRangeIntruderNormal();
        else range = storage.getViewRangeGuardNormal();

        FieldOfView fieldOfView = new FieldOfView(
                new Distance(range), Angle.fromDegrees(storage.getViewAngle()));

        /*
        Ray-casting
         */
        Point rayOrigin = new Point(currentPosition.getX(), currentPosition.getY());
        Point rayEnd;
        Direction rayDirection;

        if (state.getTargetDirection().getDegrees() + storage.getViewAngle() / 2 > 360)
            rayDirection = Direction.fromDegrees(
                    state.getTargetDirection().getDegrees() + storage.getViewAngle() / 2 - 360);
        else
            rayDirection = Direction.fromDegrees(
                    state.getTargetDirection().getDegrees() + storage.getViewAngle() / 2);

        for (int i = 0; i < storage.getViewRays(); i++) {
            if (rayDirection.getDegrees() - i < 0)
                rayDirection = Direction.fromDegrees(rayDirection.getDegrees() - i + 360);

            else
                rayDirection = Direction.fromDegrees(rayDirection.getDegrees() - i);

            rayEnd = new Point(
                    range * Math.sin(rayDirection.getRadians()),
                    range * Math.cos(rayDirection.getRadians()));
            double[] rayCoefficients = computeLineCoefficients(rayOrigin, rayEnd);
            Point pointOfIntersection = null;

            label:
            for (StaticObject staticObject : staticObjects) {
                ArrayList<Point> validIntersectionPoints = new ArrayList<>();

                Point pointOfIntersectionWithSegment1 = intersects(rayCoefficients,
                        computeLineCoefficients(
                                new Point(staticObject.getP1().getX(), staticObject.getP1().getY()),
                                new Point(staticObject.getP2().getX(), staticObject.getP2().getY())
                        )
                );
                if (pointOfIntersectionWithSegment1 != null)
                    validIntersectionPoints.add(pointOfIntersectionWithSegment1);

                Point pointOfIntersectionWithSegment2 = intersects(rayCoefficients,
                        computeLineCoefficients(
                                new Point(staticObject.getP2().getX(), staticObject.getP2().getY()),
                                new Point(staticObject.getP3().getX(), staticObject.getP3().getY())
                        )
                );
                if (pointOfIntersectionWithSegment2 != null)
                    validIntersectionPoints.add(pointOfIntersectionWithSegment2);

                Point pointOfIntersectionWithSegment3 = intersects(rayCoefficients,
                        computeLineCoefficients(
                                new Point(staticObject.getP3().getX(), staticObject.getP3().getY()),
                                new Point(staticObject.getP4().getX(), staticObject.getP4().getY())
                        )
                );
                if (pointOfIntersectionWithSegment3 != null)
                    validIntersectionPoints.add(pointOfIntersectionWithSegment3);

                Point pointOfIntersectionWithSegment4 = intersects(rayCoefficients,
                        computeLineCoefficients(
                                new Point(staticObject.getP4().getX(), staticObject.getP4().getY()),
                                new Point(staticObject.getP1().getX(), staticObject.getP1().getY())
                        )
                );
                if (pointOfIntersectionWithSegment4 != null)
                    validIntersectionPoints.add(pointOfIntersectionWithSegment4);

                /* Consider only the segment that is the closest to the agent (rayOrigin) */
                double minDistance = Double.MAX_VALUE;
                for (Point intersectionPoint : validIntersectionPoints) {
                    if (rayOrigin.getDistance(intersectionPoint).getValue() < minDistance) {
                        minDistance = rayOrigin.getDistance(intersectionPoint).getValue();
                        pointOfIntersection = intersectionPoint;
                    }
                }

                if (pointOfIntersection == null ||
                        ((rayOrigin.getX() < rayEnd.getX()) && (pointOfIntersection.getX() < rayOrigin.getX() || pointOfIntersection.getX() > rayEnd.getX())) ||
                        ((rayOrigin.getX() > rayEnd.getX()) && (pointOfIntersection.getX() > rayOrigin.getX() || pointOfIntersection.getX() < rayEnd.getX())) ||
                        ((rayOrigin.getY() < rayEnd.getY()) && (pointOfIntersection.getY() < rayOrigin.getY() || pointOfIntersection.getY() > rayEnd.getY())) ||
                        ((rayOrigin.getY() > rayEnd.getY()) && (pointOfIntersection.getY() > rayOrigin.getY() || pointOfIntersection.getY() < rayEnd.getY())))
                    continue;

                switch (staticObject.getClass().getName()) {
                    /*
                    Guard and Intruder do not exist as staticObjects,
                    TODO: iterate through guards[] and intruders[] instead
                    case "Guard":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Guard, pointOfIntersect));
                    case "Intruder":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Intruder, pointOfIntersect));
                    */
                    case "Group3.StaticObjects.Door":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Door, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.Wall":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Wall, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.Window":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Window, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.Teleport":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.Teleport, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.SentryTower":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.SentryTower, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.ShadedArea":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.ShadedArea, pointOfIntersection));
                        break label;
                    case "Group3.StaticObjects.TargetArea":
                        objectPercepts.add(new ObjectPercept(ObjectPerceptType.TargetArea, pointOfIntersection));
                        break label;


                }
            }
            if (pointOfIntersection == null ||
                    ((rayOrigin.getX() < rayEnd.getX()) && (pointOfIntersection.getX() < rayOrigin.getX() || pointOfIntersection.getX() > rayEnd.getX())) ||
                    ((rayOrigin.getX() > rayEnd.getX()) && (pointOfIntersection.getX() > rayOrigin.getX() || pointOfIntersection.getX() < rayEnd.getX())) ||
                    ((rayOrigin.getY() < rayEnd.getY()) && (pointOfIntersection.getY() < rayOrigin.getY() || pointOfIntersection.getY() > rayEnd.getY())) ||
                    ((rayOrigin.getY() > rayEnd.getY()) && (pointOfIntersection.getY() > rayOrigin.getY() || pointOfIntersection.getY() < rayEnd.getY()))) {
                objectPercepts.add(new ObjectPercept(ObjectPerceptType.EmptySpace, rayEnd));
            }
        }
        return new VisionPrecepts(fieldOfView, new ObjectPercepts(objectPercepts));
    }

    /**
     * Computes line's slope-intercept form coefficients given two points it goes through
     *
     * @param A
     * @param B
     * @return 'a' and 'b' coefficients
     */
    private double[] computeLineCoefficients(Point A, Point B) {
        if (A.getX() - B.getX() == 0)
            return new double[]{A.getX(), Integer.MAX_VALUE};

        double a = (A.getY() - B.getY()) / (A.getX() - B.getX());
        double b = (A.getX() * B.getY() - B.getX() * A.getY()) / (A.getX() - B.getX());

        return new double[]{a, b};
    }

    /**
     * Computes the point of intersection given two lines in their slope-intercept form
     *
     * @param coef1 Coefficients of the first line
     * @param coef2 Coefficients of the second line
     * @return Point of intersection
     */
    private Point intersects(double[] coef1, double[] coef2) {
        if (coef1[1] == Integer.MAX_VALUE && coef2[1] == Integer.MAX_VALUE)
            return new Point(coef1[0], coef2[0] * coef1[0] + coef2[1]);
        if (coef1[1] == Integer.MAX_VALUE)
            return new Point(coef1[0], coef2[0] * coef1[0] + coef2[1]);
        if (coef2[1] == Integer.MAX_VALUE)
            return new Point(coef2[0], coef1[0] * coef2[0] + coef1[1]);
        if (coef1[0] * coef2[1] - coef2[0] * coef1[1] == 0) {
            //System.out.println("Given lines do not intersect!");
            return null;
        }

        double x = (coef2[1] - coef1[1]) / (coef1[0] - coef2[0]);
        double y = coef1[0] * x + coef1[1];
        return new Point(x, y);
    }




    private SoundPercepts soundPercepts(AgentState state) {
        Set<SoundPercept> sounds = new HashSet<SoundPercept>();

        for (int i = 0; i < soundStorage.getSounds().size(); i++) {
            Point point1 = state.getCurrentPosition();
            Point point2 = soundStorage.getSounds().get(i).getLocation();

            Distance distance = new Distance(point1, point2);

            double radius = soundStorage.getSounds().get(i).getRadius();
            SoundPerceptType type = soundStorage.getSounds().get(i).getType();

            if (radius >= distance.getValue()) {
                double degrees = (10 * Math.PI) / 180;

                //calculate angle
                double rad = Math.atan(((point2.getY() - point1.getY()) / (point2.getX() - point1.getX())));
                rad = rad + ThreadLocalRandom.current().nextDouble(-degrees, degrees);

                Direction direction = null;
                direction = direction.fromRadians(rad);

                SoundPercept sound = new SoundPercept(soundStorage.getSounds().get(i).getType(), direction);
                sounds.add(sound);
            }
        }

        SoundPercepts percepts = new SoundPercepts(sounds);
        return percepts;
    }

    //pheromone expire rounds is not defined yet
    private SmellPercepts smellPercepts(AgentState state) {
        Set<SmellPercept> smells = new HashSet<SmellPercept>();

        if (agent.getClass() == Guard.class) {
            for (int i = 0; i < pherStorage.getPheromonesGuard().size(); i++) {
                Distance distance = new Distance(state.getCurrentPosition(), pherStorage.getPheromonesIntruder().get(i).getLocation());
                if (distance.getValue() <= (pherStorage.getPheromonesGuard().get(i).getTurnsLeft() / storage.getPheromoneExpireRounds()) * storage.getRadiusPheromone()) {
                    SmellPercept smell = new SmellPercept(pherStorage.getPheromonesGuard().get(i).getType(), distance);
                    smells.add(smell);
                }
            }
        } else if (agent.getClass() == Intruder.class) {
            for (int i = 0; i < pherStorage.getPheromonesIntruder().size(); i++) {
                Distance distance = new Distance(state.getCurrentPosition(), pherStorage.getPheromonesIntruder().get(i).getLocation());
                if (distance.getValue() <= (pherStorage.getPheromonesIntruder().get(i).getTurnsLeft()) / storage.getPheromoneExpireRounds() * storage.getRadiusPheromone()) {
                    SmellPercept smell = new SmellPercept(pherStorage.getPheromonesIntruder().get(i).getType(), distance);
                    smells.add(smell);
                }
            }
        }
        SmellPercepts percepts = new SmellPercepts(smells);
        return percepts;
    }

    private AreaPercepts areaPercepts(AgentState state) {
        boolean inWindow = false;
        boolean inDoor = false;
        boolean inSentryTower = false;
        boolean justTeleported = false;

        for (StaticObject staticObject : staticObjects) {
            if (staticObject instanceof Teleport) {
                if (state.getCurrentPosition().getX() == ((Teleport) staticObject).getTeleportTo().getX() &&
                        state.getCurrentPosition().getY() == ((Teleport) staticObject).getTeleportTo().getY())
                    justTeleported = true;
            } else if (staticObject.isInside(state.getCurrentPosition().getX(), state.getCurrentPosition().getY())) {
                if (staticObject instanceof Window) inWindow = true;
                else if (staticObject instanceof Door) inDoor = true;
                else if (staticObject instanceof SentryTower) inSentryTower = true;
            }
        }

        return new AreaPercepts(inWindow, inDoor, inSentryTower, justTeleported);
    }

    private ScenarioPercepts scenarioPercepts() {
        System.out.println(storage.getCaptureDistance());
        return new ScenarioPercepts(
                GameMode.CaptureOneIntruder,
                new Distance(storage.getCaptureDistance()),
                Angle.fromDegrees(storage.getMaxRotationAngle()),
                new SlowDownModifiers(storage.getSlowDownModifierWindow(), storage.getSlowDownModifierDoor(), storage.getSlowDownModifierSentryTower()),
                new Distance(storage.getRadiusPheromone()), storage.getPheromoneCoolDown());
    }

    // Oskar
    private ScenarioIntruderPercepts scenarioIntruderPercepts() {
        return new ScenarioIntruderPercepts(scenarioPercepts, storage.getWinConditionIntruderRounds(),
                storage.getMaxMoveDistanceIntruder(), storage.getMaxSprintDistanceIntruder(), storage.getSprintCoolDown()
        );

    }

    private ScenarioGuardPercepts scenarioGuardPercepts() {
        return new ScenarioGuardPercepts(scenarioPercepts, storage.getMaxMoveDistanceGuard());
    }

    // TODO: implement a function which checks if an action is legal based on the current state of the agent.
    // Victor
    private boolean checkLegalIntruderAction(AgentState state, Interop.Action.IntruderAction action) {
        if (action.getClass().getName().equals("Interop.Action.Move")) {
            if (((Move) action).getDistance().getValue() > storage.getMaxMoveDistanceIntruder().getValue()) {
                return false;
            }
            if (checkCollision(state.getCurrentPosition(), state.getTargetDirection(), ((Move) action).getDistance())) {
                return false;
            }
            if (state.getPenalty() != 0) {//if penalty can't do any action until penalty removed (=0)
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.Sprint")) {
            if (((Sprint) action).getDistance().getValue() > storage.getMaxSprintDistanceIntruder().getValue()) {
                return false;
            }
            if (state.getPenalty() != 0) {
                return false;
            }
            if (checkCollision(state.getCurrentPosition(), state.getTargetDirection(), ((Sprint) action).getDistance())) {
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.Rotate")) {
            if (Math.abs(((Rotate) action).getAngle().getRadians()) > storage.getMaxRotationAngle()) {//maxRotationAngle in radians or degrees ?
                return false;
            }
            if (state.getPenalty() != 0) {
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.DropPheromone")) {
            if (state.getPenalty() != 0) {
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.Yell")) {
            return false;
        }

        if (action.getClass().getName().equals("Interop.Action.NoAction")) {
            return true;
        }
        return true;
    }

    // TODO: implement a function which checks if an action is legal based on the current state of the agent.
    // Victor
    private boolean checkLegalGuardAction(AgentState state, Interop.Action.GuardAction action) {
        if (action.getClass().getName().equals("Interop.Action.Move")) {
            if (((Move) action).getDistance().getValue() > storage.getMaxMoveDistanceIntruder().getValue()) {
                return false;
            }
            if (checkCollision(state.getCurrentPosition(), state.getTargetDirection(), ((Move) action).getDistance())) {
                return false;
            }
            if (state.getPenalty() != 0) {//if penalty can't do any action until penalty removed (=0)
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.Sprint")) {
            return false;
        }

        if (action.getClass().getName().equals("Interop.Action.Rotate")) {
            if (Math.abs(((Rotate) action).getAngle().getRadians()) > storage.getMaxRotationAngle()) {//maxRotationAngle in radians or degrees ?
                return false;
            }
            if (state.getPenalty() != 0) {
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.DropPheromone")) {
            if (state.getPenalty() != 0) {//if penalty can't do any action until penalty removed (=0)
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.Yell")) {
            if (state.getPenalty() != 0) {
                return false;
            }
        }

        if (action.getClass().getName().equals("Interop.Action.NoAction")) {
            return true;
        }
        return true;
    }

    // TODO: implement a function, which updates the current game state based on the action of the agent.
    // Merlin
    private void updateAgentState(AgentState state, Action action) {

        StaticObject a = inAreaType(state);
        if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Window")) {
            soundStorage.addSound(SoundPerceptType.Noise, state.getCurrentPosition(), agentStates.size(), storage.getWindowSoundRadius());
        }
        if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Door")) {
            soundStorage.addSound(SoundPerceptType.Noise, state.getCurrentPosition(), agentStates.size(), storage.getDoorSoundRadius());
        }

        switch (action.getClass().getName()) {
            case "Interop.Action.DropPheromone":
                state.setPenalty(storage.getPheromoneCoolDown());
                Interop.Action.DropPheromone actPheromone = (Interop.Action.DropPheromone) action;
                // TODO: Set correct pheromone time to expire
                pherStorage.addPheromone(actPheromone.getType(), state.getCurrentPosition(), 5 * agentStates.size(), (agent.getClass() == Guard.class), this.map.getPheromoneRadius());
                state.setLastAction(actPheromone);
                if(pherStorage.getPheromonesIntruder().size() != 0 || pherStorage.getPheromonesGuard().size() != 0) {
                	pherStorage.getLast(agent.getClass().getName()).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
                	pherStorage.getLast(agent.getClass().getName()).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
                	this.mapPane.getChildren().add(pherStorage.getLast(agent.getClass().getName()).getShape());
                }
                this.updateStorages();
                break;


            case "Interop.Action.Move": {
                Interop.Action.Move actMove = (Interop.Action.Move) action;
                soundStorage.addSound(SoundPerceptType.Noise, state.getCurrentPosition(), agentStates.size(), (actMove.getDistance().getValue() / storage.getMaxSprintDistanceIntruder().getValue()) * storage.getMaxMoveSoundRadius());
                state.setCurrentPosition(new Point(actMove.getDistance().getValue() * Math.cos(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getX(), actMove.getDistance().getValue() * Math.sin(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getY()));
                state.setPenalty(storage.getSprintCoolDown());
                state.setLastAction(actMove);

                StaticObject area = inAreaType(state);
                if(area != null && area.getClass().getName().equals("Group3.StaticObjects.Teleport") && !state.isTeleported()) {
                    Teleport teleport = (Teleport)area;
                    state.setCurrentPosition(teleport.getTeleportTo());
                    state.setTeleported(true);
                }
                if(area == null) {
                    state.setTeleported(false);
                }
                else if(!area.getClass().getName().equals("Group3.StaticObjects.Teleport")) {
                    state.setTeleported(false);
                }
                this.updateStorages();

                break;
            }
            case "Interop.Action.NoAction":
                Interop.Action.NoAction actNo = (Interop.Action.NoAction) action;
                state.setLastAction(actNo);
                this.updateStorages();
                break;
            case "Interop.Action.Rotate":
                Interop.Action.Rotate actRotate = (Interop.Action.Rotate) action;
                if (actRotate.getAngle().getDegrees() <= storage.getMaxRotationAngle()) {
                	double result = state.getTargetDirection().getDegrees() + actRotate.getAngle().getDegrees();
                	if(result < 0) {
                		result = 360 + result;
                	}
                	else if(result > 360) {
                		result = 0 + (result - 360);
                	}
                	else if(result == 360) {
                		result = 0;
                	}
                    state.setTargetDirection(Direction.fromDegrees(result));
                } else {
                    state.setLastActionExecuted(false);
                }
                state.setLastAction(actRotate);
                this.updateStorages();
                break;
            case "Interop.Action.Sprint": {
                Interop.Action.Sprint actSprint = (Interop.Action.Sprint) action;
                soundStorage.addSound(SoundPerceptType.Noise, state.getCurrentPosition(), agentStates.size(), (actSprint.getDistance().getValue() / storage.getMaxSprintDistanceIntruder().getValue()) * storage.getMaxMoveSoundRadius());
                state.setCurrentPosition(new Point(actSprint.getDistance().getValue() * Math.cos(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getX(), actSprint.getDistance().getValue() * Math.sin(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getY()));
                state.setPenalty(storage.getSprintCoolDown());
                state.setLastAction(actSprint);

                StaticObject area = inAreaType(state);
                if(area != null && area.getClass().getName().equals("Group3.StaticObjects.Teleport") && !state.isTeleported()) {
                    Teleport teleport = (Teleport)area;
                    state.setCurrentPosition(teleport.getTeleportTo());
                    state.setTeleported(true);
                }
                if(area == null) {
                    state.setTeleported(false);
                }
                else if(!area.getClass().getName().equals("Group3.StaticObjects.Teleport")) {
                    state.setTeleported(false);
                }
                this.updateStorages();

                break;
            }
            case "Interop.Action.Yell":
                Interop.Action.Yell actYell = (Interop.Action.Yell) action;
                soundStorage.addSound(SoundPerceptType.Yell, state.getCurrentPosition(), agentStates.size(), storage.getYellSoundRadius());
                state.setLastAction(actYell);
                if(soundStorage.getSounds().size() > 0) {
                	soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
                	soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
                	this.mapPane.getChildren().add(soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape());
                }
                this.updateStorages();
                break;
            default:
                state.setLastActionExecuted(false);
                this.updateStorages();
        }
    }

    private StaticObject inAreaType(AgentState state) {
        for(StaticObject obj : staticObjects) {
            if(obj.getClass().getName().equals("Group3.StaticObjects.ShadedArea")) {
                Point[] area = new Point[] {obj.getP1(), obj.getP2(), obj.getP3(), obj.getP4()};
                if(inArea(state.getCurrentPosition(), area)) return obj;
            }
            if(obj.getClass().getName().equals("Group3.StaticObjects.SentryTower")) {
                Point[] area = new Point[] {obj.getP1(), obj.getP2(), obj.getP3(), obj.getP4()};
                if(inArea(state.getCurrentPosition(), area)) return obj;
            }
            if(obj.getClass().getName().equals("Group3.StaticObjects.Door")) {
                Point[] area = new Point[] {obj.getP1(), obj.getP2(), obj.getP3(), obj.getP4()};
                if(inArea(state.getCurrentPosition(), area)) return obj;
            }
            if(obj.getClass().getName().equals("Group3.StaticObjects.Window")) {
                Point[] area = new Point[] {obj.getP1(), obj.getP2(), obj.getP3(), obj.getP4()};
                if(inArea(state.getCurrentPosition(), area)) return obj;
            }
            if(obj.getClass().getName().equals("Group3.StaticObjects.Teleport")) {
                Point[] area = new Point[] {obj.getP1(), obj.getP2(), obj.getP3(), obj.getP4()};
                if(inArea(state.getCurrentPosition(), area)) return obj;
            }
        }
        return null;
    }

    private boolean inArea(Point point, Point[] area) {
        double xmax = Integer.MIN_VALUE;
        double xmin = Integer.MAX_VALUE;
        double ymax = Integer.MIN_VALUE;
        double ymin = Integer.MAX_VALUE;

        for(Point p : area) {
            double x = p.getX();
            double y = p.getY();
            if(x < xmin) xmin = x;
            else if(x > xmax) xmax = x;
            if(y < ymin) ymin = y;
            else if(y > ymax) ymax = y;
        }

        if((point.getX() >= xmin) &&(point.getX() <= xmax))
            if((point.getY() >= ymin) &&(point.getY() <= ymax))
                return true;

        return false;
    }

    // TODO: implement a function which checks if the game is finished. Take into account the current game mode.
    // Victor
    private int gameFinished() {
        if (storage.getGameMode() == 0) { //game mode 0, all intruders have to be captured
            for (int i = 0; i < agentStates.size(); i++) {
                if (agentStates.get(i).getAgent().getClass() == Intruder.class) {
                    if (agentStates.get(i).getInTarget() == storage.getWinConditionIntruderRounds()) ;
                    return 1;//intruder win
                }
            }
            for (int i = 0; i < agentStates.size(); i++) {
                if (agentStates.get(i).getAgent().getClass() == Guard.class) {
                    for (int j = 0; j < agentStates.size(); j++) {
                        if (agentStates.get(i).getAgent().getClass() == Intruder.class) {
                            if (visionPercepts(agentStates.get(i)).getFieldOfView().isInView(agentStates.get(j).getCurrentPosition()) && (agentStates.get(i).getCurrentPosition().getDistance(agentStates.get(j).getCurrentPosition()).getValue() < storage.getCaptureDistance())) {
                                intruders.remove(j);
                                agentStates.remove(j);
                                capturedIntruderCount++;
                                if (capturedIntruderCount == (storage.getNumIntruders())) { //if all intruders captured guards win
                                    return 2;//guard win
                                }
                            }
                        }
                    }
                }
            }

        } else if (storage.getGameMode() == 1) {  //game mode 1, 1 intruder has to be captured
            for (int i = 0; i < agentStates.size(); i++) {
                if (agentStates.get(i).getAgent().getClass() == Intruder.class) {
                    if (agentStates.get(i).getInTarget() == storage.getWinConditionIntruderRounds()) ;
                    return 1;//intruder win
                }
            }
            for (int i = 0; i < agentStates.size(); i++) {
                if (agentStates.get(i).getAgent().getClass() == Guard.class) {
                    for (int j = 0; j < agentStates.size(); j++) {
                        if (agentStates.get(i).getAgent().getClass() == Intruder.class) {
                            if (visionPercepts(agentStates.get(i)).getFieldOfView().isInView(agentStates.get(j).getCurrentPosition()) && (agentStates.get(i).getCurrentPosition().getDistance(agentStates.get(j).getCurrentPosition()).getValue() < storage.getCaptureDistance())) {
                                intruders.remove(j);
                                agentStates.remove(j);
                                capturedIntruderCount++;
                                return 2;//guard win
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public ArrayList<StaticObject> getStaticObjects() {
        return staticObjects;
    }

    public ArrayList<AgentState> getAgentStates() {
        return agentStates;
    }

    public void updateIntarget(AgentState state) {
        if (readMap.getTarget().isInside(state.getCurrentPosition().getX(), state.getCurrentPosition().getY())) {
            state.addInTarget(1);
        }
    }

    public MapVisualization getMapVisualization() { return this.mapVisualization; }
    public void setMap(Map map) {
        this.mapVisualization = new MapVisualization(map);
    }
    public void setWidthBound(double wB) {	this.widthBound = wB;	}
    public void setHeightBound(double hb) {	this.heightBound = hb;	}
    public void createVisualMap(String path) {
    	// visualisation of the map
    	this.map = new Map(path, this.widthBound, this.heightBound);
    	this.map.addAgents(this.agentStates);
    	this.map.addPheromones(this.pherStorage.getPheromones());
    	this.mapVisualization = new MapVisualization(this.map);
    	//this.mapVisualization.addVisualAgents(agentStates);
    	this.mapPane = this.mapVisualization.getPane();
    }
    public Map getMap() {	return this.map;}
    public BorderPane getMapPane() {	return this.mapPane;	}

    public void animationLoop() {
    	animation = new StepAnimationTimer(this.mapVisualization, this);
    	animation.start();
    }
    
    public void updateStorages() {
    	this.pherStorage.updatePheromones();
    	this.soundStorage.updateSounds();
    }

}