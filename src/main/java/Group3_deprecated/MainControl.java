package Group3_deprecated;

import Group3.Intruder.Intruder;
import Group3_deprecated.StaticObjects.*;
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
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.*;
import javafx.scene.layout.BorderPane;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    PheromoneStorage pherStorage = new PheromoneStorage();
    SoundStorage soundStorage = new SoundStorage();

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
    private Map map = null;
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
        //intruders = AgentsFactory.createIntruders(storage.getNumIntruders());
        //guards = AgentsFactory.createGuards(storage.getNumGuards());

        // Initialize the states of the agents
        // TODO: Add the correct coordinates from the map file. Actually the agents will be placed at 0,0
        agentStates = new ArrayList<AgentState>();

        Point intruderSpawn = new Point(0,0);
        Point guardSpawn = new Point(0,0);
        
        for(int i = 0; i < staticObjects.size(); i++) {
        	StaticObject obj = staticObjects.get(i);
        	if(obj.getClass().getName() == "Group3.StaticObjects.SpawnAreaIntruders") {
        		intruderSpawn = obj.getP3();
        	}
        	if(obj.getClass().getName() == "Group3.StaticObjects.SpawnAreaGuards") {
        		guardSpawn = obj.getP3();
        	}
        }
        
        double c = 0;
        for (Interop.Agent.Intruder intruder : intruders) {
            AgentState state = new AgentState(new Point(intruderSpawn.getX() + 0.7 + c, intruderSpawn.getY() + 0.55), Direction.fromDegrees(90), intruder);
			//AgentState state = new AgentState(new Point(4, 4), Direction.fromDegrees(180), intruder);
			agentStates.add(state);
            c = c + 1.1;
        }
        c = 0;
        for (Interop.Agent.Guard guard : guards) {
        	AgentState state = new AgentState(new Point(guardSpawn.getX() + 0.7 + c, guardSpawn.getY() + 0.55), Direction.fromDegrees(90), guard);
			//AgentState state = new AgentState(new Point(2, 2), Direction.fromDegrees(180), guard);
            agentStates.add(state);
            c = c + 1.1;
        }
        
        double h = storage.getHeight();
		double w = storage.getWidth();
		double thickness = 0.1;
		Wall border1 = new Wall(new Point(0,h), new Point(thickness, h), new Point(0,0), new Point(thickness,0));
		Wall border2 = new Wall(new Point(0,thickness), new Point(w, thickness), new Point(0,0), new Point(w,0));
		Wall border3 = new Wall(new Point(0,h), new Point(w, h), new Point(0,h-thickness), new Point(w,h-thickness));
		Wall border4 = new Wall(new Point(w-thickness,h), new Point(w,h), new Point(w-thickness,0), new Point(w,0));
		staticObjects.add(border1);
		staticObjects.add(border2);
		staticObjects.add(border3);
		staticObjects.add(border4);

        targetZoneCount = new ArrayList<Integer>();
        //Initialize counters for Intruders in target zone
        for (int i = 0; i < intruders.size(); i++) {
            targetZoneCount.add(0);
        }
    }

    public MainControl(){
        this.path = "C:\\Users\\victo\\OneDrive\\Documents\\GitHub\\Project2.2\\e8bd358abeeacbb7ccc8c667ccd837e4\\samplemap.txt";
    	//this.path = "C://Users//janneke//Documents//e8bd358abeeacbb7ccc8c667ccd837e4//samplemap.txt";
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
        MainControl gameController = new MainControl("samplemap.txt");
        
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

    private ArrayList<Ray> getRays(Point agentPosition, Direction agentDirection, boolean guard){
    	
    	ArrayList<Ray> rays = new ArrayList<Ray>();
    	
    	double num_rays = storage.getViewRays();
    	double viewAngle = storage.getViewAngle();
    	
    	double angleDegreeDistance = viewAngle / num_rays;
    	
    	if((num_rays%2)==1) {
    		angleDegreeDistance = viewAngle / (num_rays-1);
    	}
    	
    	double start = -viewAngle / 2;
    	
    	double viewRange = 0;
    	
    	// TODO: Add shaded
    	if(guard) {
    		viewRange = storage.getViewRangeGuardNormal();
    	}
    	else {
    		viewRange = storage.getViewRangeIntruderNormal();
    	}
    	
    	for(int i = 0; i < num_rays; i++) {
    		Ray ray = new Ray();
    		
    		double direction = i * angleDegreeDistance + start;
    		direction = direction + agentDirection.getDegrees();
    		
    		if(direction < 0) {
    			direction = 360 + direction;
        	}
        	else if(direction > 360) {
        		direction = 0 + (direction - 360);
        	}
        	else if(direction == 360) {
        		direction = 0;
        	}
    		
    		Direction dirRad = Direction.fromDegrees(direction);
    		
    		Point endPoint = new Point(viewRange * Math.cos(dirRad.getRadians()) + agentPosition.getX(), 
    				viewRange * Math.sin(dirRad.getRadians()) + agentPosition.getY());
    		
    		for(StaticObject obj : staticObjects) {
    			
    			ObjectType type = null;
    			switch (obj.getClass().getName()) {
	    			case "Group3.StaticObjects.Door":
	    				type = ObjectType.Door;
	    				break;
	    			case "Group3.StaticObjects.SentryTower":
	    				type = ObjectType.SentryTower;
	    				break;
	    			case "Group3.StaticObjects.ShadedArea":
	    				type = ObjectType.ShadedArea;
	    				break;
	    			case "Group3.StaticObjects.SpawnAreaGuards":
	    				type = ObjectType.SpawnAreaGuards;
	    				break;
	    			case "Group3.StaticObjects.SpawnAreaIntruders":
	    				type = ObjectType.SpawnAreaIntruders;
	    				break;
	    			case "Group3.StaticObjects.TargetArea":
	    				type = ObjectType.TargetArea;
	    				break;
	    			case "Group3.StaticObjects.Teleport":
	    				type = ObjectType.Teleport;
	    				break;
	    			case "Group3.StaticObjects.Wall":
	    				type = ObjectType.Wall;
	    				break;
	    			case "Group3.StaticObjects.Window":
	    				type = ObjectType.Window;
	    				break;
    			}
    			
    			IntersectionPoint intersectionPoint = intersectPoints(agentPosition, endPoint, obj.getP1(), obj.getP2(), type);
    			if(intersectionPoint != null) {
    				ray.setPoint(intersectionPoint);
    			}
    			intersectionPoint = intersectPoints(agentPosition, endPoint, obj.getP1(), obj.getP3(), type);
    			if(intersectionPoint != null) {
    				ray.setPoint(intersectionPoint);
    			}
    			intersectionPoint = intersectPoints(agentPosition, endPoint, obj.getP2(), obj.getP4(), type);
    			if(intersectionPoint != null) {
    				ray.setPoint(intersectionPoint);
    			}
    			intersectionPoint = intersectPoints(agentPosition, endPoint, obj.getP3(), obj.getP4(), type);
    			if(intersectionPoint != null) {
    				ray.setPoint(intersectionPoint);
    			}
    		}
    		rays.add(ray);
    		int j = 1;
    	}
    	
    	return rays;
    }
    
    
    public IntersectionPoint intersectPoints(Point a1, Point a2, Point b1, Point b2, ObjectType objectType) {
    	
    	double[] line1 = getLineFunction(a1, a2);
    	double[] line2 = getLineFunction(b1, b2);
    	
    	double axmin = Math.min(a1.getX(),a2.getX());
    	double axmax = Math.max(a1.getX(),a2.getX());
    	double aymin = Math.min(a1.getY(),a2.getY());
    	double aymax = Math.max(a1.getY(),a2.getY());
    	double bxmin = Math.min(b1.getX(),b2.getX());
    	double bxmax = Math.max(b1.getX(),b2.getX());
    	double bymin = Math.min(b1.getY(),b2.getY());
    	double bymax = Math.max(b1.getY(),b2.getY());
    	
    	if(Math.abs(line1[0]) == Math.abs(line2[0])) {
    		return null;
    	}
    	else {
    		double x = (line2[1]-line1[1])/(line1[0]-line2[0]);
    		double y = line1[0] * x + line1[1];
    		
    		if(((axmin <= x) && (axmax >= x)) && ((bxmin <= x) && (bxmax >= x))) {
    			if(((aymin <= y) && (aymax >= y)) && ((bymin <= y) && (bymax >= y))){
    				return new IntersectionPoint(new Point(x, y), objectType);
    			}
    			else return null;
    		}
    		else return null;
    	}
    	
    }
    
    public double[] getLineFunction(Point a, Point b) {
    	// y = a*x+b
    	double[] ab = new double[2];
    	
    	double num = a.getY()-b.getY();
    	double denom = a.getX()-b.getX();
    	
    	if(denom == 0) {
    		denom = 0.000000000000000001;
    	}
    	
    	ab[0] = (num)/(denom);
    	ab[1] = a.getY() - (num)/(denom) * a.getX();
    	return ab;
    	
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
    		Interop.Action.GuardAction action = getRandomGuardAction();
    		
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
    		Interop.Action.IntruderAction action = getRandomIntruderAction();

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
    	} else if (agent.getClass() == ExplorationAgent.class) {
    		//System.out.println("This is a Guard");
    		ExplorationAgent guard = (ExplorationAgent)agent;

    		// 2. Calculate the perception of the agent
    		GuardPercepts percept = new GuardPercepts(visionPercepts(state),
    				soundPercepts(state),
    				smellPercepts(state),
    				areaPercepts(state),
    				scenarioGuardPercepts(),
    				state.isLastActionExecuted());

    		// 3. Pass the perception to the agent and retrieve the action
    		//Interop.Action.GuardAction action = guard.getAction(percept);
    		Interop.Action.GuardAction action = getRandomGuardAction();
    		
    		
    		
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
    	}
    	return -1;
    }
    
    private IntruderAction getRandomIntruderAction() {
    	Interop.Action.IntruderAction action = new NoAction();
		
		Random random = new Random(); 
		int ri = random. nextInt(5);
		
		switch(ri) {
		case 0:
			action = new Interop.Action.Move(new Distance(1));
			break;
		case 1:
			int rt = random. nextInt(90);
			if(random. nextInt(2) == 1) rt = rt * -1;
			action = new Interop.Action.Rotate(Angle.fromDegrees(rt));
			break;
		case 2:
			action = new Interop.Action.DropPheromone(SmellPerceptType.Pheromone1);
			break;
		case 4:
			action = new Interop.Action.Sprint(new Distance(2));
			break;
		}
		return action;
    }
    
    private GuardAction getRandomGuardAction() { 
    	
    	Interop.Action.GuardAction action = new Interop.Action.NoAction();
    	
    	Random random = new Random(); 
		int ri = random. nextInt(5);
		
		switch(ri) {
		case 0:
			action = new Interop.Action.Move(new Distance(1));
			break;
		case 1:
			int rt = random. nextInt(90);
			if(random. nextInt(2) == 1) rt = rt * -1;
			action = new Interop.Action.Rotate(Angle.fromDegrees(rt));
			break;
		case 2:
			action = new Interop.Action.DropPheromone(SmellPerceptType.Pheromone1);
			break;
		case 4:
			action = new Interop.Action.Yell();
			break;
		}
		return action;
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

		if (state.getTargetDirection().getDegrees() + (storage.getViewAngle() / 2) + 1 > 360)
			rayDirection = Direction.fromDegrees(
					state.getTargetDirection().getDegrees() + (storage.getViewAngle() / 2) - 360 + 1);
		else
			rayDirection = Direction.fromDegrees(
					state.getTargetDirection().getDegrees() + (storage.getViewAngle() / 2) + 1);

		for (int i = 0; i < storage.getViewRays(); i++) {
			if (rayDirection.getDegrees() - 1 < 0)
				rayDirection = Direction.fromDegrees(rayDirection.getDegrees() - 1 + 360);
			else
				rayDirection = Direction.fromDegrees(rayDirection.getDegrees() - 1);

			rayEnd = new Point(
					rayOrigin.getX() + range * Math.cos(rayDirection.getRadians()),
					rayOrigin.getY() + range * Math.sin(rayDirection.getRadians()));

			double[] rayCoefficients = computeLineCoefficients(rayOrigin, rayEnd);
			Point pointOfIntersection = null;

			label:
			for (StaticObject staticObject : staticObjects) {
				ArrayList<Point> validIntersectionPoints = new ArrayList<>();

				Point pointOfIntersectionWithSegment1 = intersects(rayCoefficients,
						computeLineCoefficients(
								new Point(staticObject.getP1().getX(), staticObject.getP1().getY()),
								new Point(staticObject.getP2().getX(), staticObject.getP2().getY())));
				if (pointOfIntersectionWithSegment1 != null &&
						(pointOfIntersectionWithSegment1.getX() < staticObject.getP2().getX()) &&
						(pointOfIntersectionWithSegment1.getX() > staticObject.getP1().getX()))
					validIntersectionPoints.add(pointOfIntersectionWithSegment1);

				Point pointOfIntersectionWithSegment2 = intersects(rayCoefficients,
						computeLineCoefficients(
								new Point(staticObject.getP2().getX(), staticObject.getP2().getY()),
								new Point(staticObject.getP4().getX(), staticObject.getP4().getY())));
				if (pointOfIntersectionWithSegment2 != null &&
						(pointOfIntersectionWithSegment2.getY() < staticObject.getP2().getY()) &&
						(pointOfIntersectionWithSegment2.getY() > staticObject.getP4().getY()))
					validIntersectionPoints.add(pointOfIntersectionWithSegment2);

				Point pointOfIntersectionWithSegment3 = intersects(rayCoefficients,
						computeLineCoefficients(
								new Point(staticObject.getP3().getX(), staticObject.getP3().getY()),
								new Point(staticObject.getP4().getX(), staticObject.getP4().getY())));
				if (pointOfIntersectionWithSegment3 != null &&
						(pointOfIntersectionWithSegment3.getX() < staticObject.getP4().getX()) &&
						(pointOfIntersectionWithSegment3.getX() > staticObject.getP3().getX()))
					validIntersectionPoints.add(pointOfIntersectionWithSegment3);

				Point pointOfIntersectionWithSegment4 = intersects(rayCoefficients,
						computeLineCoefficients(
								new Point(staticObject.getP3().getX(), staticObject.getP3().getY()),
								new Point(staticObject.getP1().getX(), staticObject.getP1().getY())));
				if (pointOfIntersectionWithSegment4 != null &&
						(pointOfIntersectionWithSegment4.getY() < staticObject.getP1().getY()) &&
						(pointOfIntersectionWithSegment4.getY() > staticObject.getP3().getY()))
					validIntersectionPoints.add(pointOfIntersectionWithSegment4);

				/* Domain check (so that we don't take an intersection point that is 'behind' the agent) */
				Iterator<Point> iterator = validIntersectionPoints.iterator();
				while (iterator.hasNext()) {
					Point p = iterator.next();
					if (
							((rayOrigin.getX() < rayEnd.getX()) && (p.getX() < rayOrigin.getX() || p.getX() > rayEnd.getX())) ||
							((rayOrigin.getX() > rayEnd.getX()) && (p.getX() > rayOrigin.getX() || p.getX() < rayEnd.getX())) ||
							((rayOrigin.getY() < rayEnd.getY()) && (p.getY() < rayOrigin.getY() || p.getY() > rayEnd.getY())) ||
							((rayOrigin.getY() > rayEnd.getY()) && (p.getY() > rayOrigin.getY() || p.getY() < rayEnd.getY()))) {
						iterator.remove();
					}
					}


				/* Consider only the segment that is the closest to the agent (rayOrigin) */
				double minDistance = range;
				for (Point intersectionPoint : validIntersectionPoints) {
					if (rayOrigin.getDistance(intersectionPoint).getValue() < minDistance) {
						minDistance = rayOrigin.getDistance(intersectionPoint).getValue();
						pointOfIntersection = intersectionPoint;
					}
				}

				/* If no intersection points with given static object were found proceed to the next static object */
				if (pointOfIntersection == null ||
						(pointOfIntersection.getX() == currentPosition.getX() && pointOfIntersection.getY() == currentPosition.getY())) {
					continue;
				}

				//Translate

				Point pointOfIntersectionTranslated = new Point(
						0.999 * (pointOfIntersection.getX() - currentPosition.getX()),
						0.999 * (pointOfIntersection.getY() - currentPosition.getY()));
				//Rotate
				pointOfIntersectionTranslated = new Point(
					pointOfIntersectionTranslated.getX() * Math.cos(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()) - pointOfIntersectionTranslated.getY() * Math.sin(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()),
					pointOfIntersectionTranslated.getX() * Math.sin(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()) + pointOfIntersectionTranslated.getY() * Math.cos(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()));
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
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.Door, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.Wall":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.Wall, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.Window":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.Window, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.Teleport":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.Teleport, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.SentryTower":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.SentryTower, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.ShadedArea":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.ShadedArea, pointOfIntersectionTranslated));
						break label;
					case "Group3.StaticObjects.TargetArea":
						objectPercepts.add(new ObjectPercept(ObjectPerceptType.TargetArea, pointOfIntersectionTranslated));
						break label;
				}
			}
			if (pointOfIntersection == null) {
				//Translate
				Point pointOfEmptySpaceTranslated = new Point(
						0.999 * (rayEnd.getX() - currentPosition.getX()),
						0.999 * (rayEnd.getY() - currentPosition.getY()));
				//Rotate
				pointOfEmptySpaceTranslated = new Point(
						pointOfEmptySpaceTranslated.getX() * Math.cos(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()) - pointOfEmptySpaceTranslated.getY() * Math.sin(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()),
						pointOfEmptySpaceTranslated.getX() * Math.sin(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()) + pointOfEmptySpaceTranslated.getY() * Math.cos(Angle.fromDegrees(450).getRadians() - rayDirection.getRadians()));
				objectPercepts.add(new ObjectPercept(ObjectPerceptType.EmptySpace, new Point(pointOfEmptySpaceTranslated.getX(), pointOfEmptySpaceTranslated.getY())));
			}
		}
		return new VisionPrecepts(fieldOfView, new ObjectPercepts(objectPercepts));
	}

    /**
     * Computes line's slope-intercept form coefficients given two points it goes through
     *
     * @param A point
     * @param B point
     * @return 'a' and 'b' coefficients
     */
    private double[] computeLineCoefficients(Point A, Point B) {
		if (A.getX() == B.getX())
			return new double[]{A.getX(), Integer.MAX_VALUE};
		if (A.getY() == B.getY())
			return new double[]{Integer.MAX_VALUE, A.getY()};

		double a = (B.getY() - A.getY()) / (B.getX() - A.getX());
		double b = (B.getX() * A.getY() - A.getX() * B.getY()) / (B.getX() - A.getX());

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
		if (coef1[0] * coef2[1] - coef2[0] * coef1[1] == 0) {
			return null;
		}
		if (coef1[1] == Integer.MAX_VALUE && coef2[1] == Integer.MAX_VALUE) {
			return null;
		}
		if (coef1[0] == Integer.MAX_VALUE && coef2[0] == Integer.MAX_VALUE) {
			return null;
		}

		if (coef1[1] == Integer.MAX_VALUE && coef2[0] == Integer.MAX_VALUE) {
			return new Point(coef1[0] / coef2[1], coef2[1]);
		}

		if (coef1[0] == Integer.MAX_VALUE && coef2[1] == Integer.MAX_VALUE) {
			return new Point(coef1[1] / coef2[0], coef2[0]);
		}



		if (coef1[1] == Integer.MAX_VALUE) {
			return new Point(coef1[0], coef2[0] * coef1[0] + coef2[1]);
		}
		if (coef2[1] == Integer.MAX_VALUE) {

			return new Point(coef2[0], coef1[0] * coef2[0] + coef1[1]);
		}

		if (coef1[0] == Integer.MAX_VALUE) {
			return new Point((coef1[1] - coef2[1]) / coef2[0], coef1[1]);
		}

		if (coef2[0] == Integer.MAX_VALUE) {

			return new Point((coef2[1] - coef1[1]) / coef1[0], coef2[1]);
		}

		double x = (coef2[1] - coef1[1]) / (coef1[0] - coef2[0]);
		double y = coef1[0] * x + coef1[1];
		return new Point(x, y);
    }




    private SoundPercepts soundPercepts(AgentState state) {
        Set<SoundPercept> sounds = new HashSet<SoundPercept>();

        for (int i = 0; i < soundStorage.getSounds().size(); i++) {
        	//point 1 is the agent
        	//point 2 is the sound source
            Point point1 = state.getCurrentPosition();
            Point point2 = soundStorage.getSounds().get(i).getLocation();
            
            //the direction the agent is facing, is known, as well as the distance
            Direction viewDirec = state.getTargetDirection();
            Distance distance = new Distance(point1, point2);

            //use these to see whether you can observe it
            double radius = soundStorage.getSounds().get(i).getRadius();
            SoundPerceptType type = soundStorage.getSounds().get(i).getType();

            if (radius >= distance.getValue()) {
            	//at the end, the maximum difference you observe (random)
                double totalRadMinMax = 10 * (Math.PI / 180);

                //calculate angle between the sound and the direction the agent is facing
                double rad = 0;
                double radSound;
                
                //calculate the angle the sound is in
                if(point1.getX() > point2.getX()) {
                	//left side of x axis in circle
                	if(point1.getY() == point2.getY()) {
                		radSound = Math.PI;
                	}
                	//bottom left of circle -> add pi, and then do the arctan, with y as opposite
                	else if(point1.getY() > point2.getY()) {
                		radSound = Math.PI + Math.atan((point1.getY() - point2.getY())/(point1.getX() - point2.getX()));
                	}
                	//Y2 > Y1
                	//top left of the circle -> add 1/2 pi, and then do the arctan with x as opposite
                	else {
                		radSound = 1/2 * Math.PI + Math.atan((point1.getX()-point2.getX())/(point2.getY() - point1.getY()));
                	}
                }
                else if(point2.getX() > point1.getX()) {
                	//right side of x axis in circle
                	if(point1.getY() == point2.getY()) {
                		radSound = 0;
                	}
                	//bottom right of circle -> add 1 1/2 pi and then do the arctan, with x as opposite
                	else if(point1.getY() > point2.getY()) {
                		radSound = 3/2 *Math.PI + Math.atan((point2.getX()-point1.getX())/(point1.getY() - point2.getY()));
                	}
                	//Y2 >Y1
                	//top right of circle - don't add anything, and then to arctan, y as opposite
                	else {
                		radSound = Math.atan((point2.getY() - point1.getY()) / (point2.getX() - point1.getX()));
                	}
                }
                // X1 == X2
                else {
                	//at the place of the sound
                	if(point1.getY() == point2.getY()) {
                		radSound = 0;
                	}
                	//bottom side of y axis
                	else if(point1.getY() > point2.getY()) {
                		radSound = 3/2 * Math.PI;
                	}
                	//Y2 >Y1
                	//top side of y axis
                	else {
                		radSound = 1/2 * Math.PI;
                	}
                }
                
                //then, calculate what the angle (clockwise) would be between these angles
                if (radSound <viewDirec.getRadians()) {
                	rad = viewDirec.getRadians() - radSound;
                }
                else if (radSound > viewDirec.getRadians()) {
                	rad = 2 * Math.PI + viewDirec.getRadians() - radSound;
                }
                else {
                	rad = 0;
                }
                
                //add a random amount of radians (degrees between -10 and 10)		
                rad = rad + ThreadLocalRandom.current().nextDouble(-totalRadMinMax, totalRadMinMax);
                
                //adjust in case the randomness made it go out of bounds
                if (rad >= 2*Math.PI) {
                	rad = rad - totalRadMinMax;
                }
                else if (rad < 0) {
                	rad = rad + totalRadMinMax;
                }

                //create the direction - use rads
                Direction direction = null;
                direction = direction.fromRadians(rad);

                //make and add the soundpercept
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
                Distance distance = new Distance(state.getCurrentPosition(), pherStorage.getPheromonesGuard().get(i).getLocation());
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
            if (Math.abs(((Rotate) action).getAngle().getDegrees()) > storage.getMaxRotationAngle()) {//maxRotationAngle in radians or degrees ?
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
                pherStorage.addPheromone(actPheromone.getType(), state.getCurrentPosition(), storage.getPheromoneCoolDown(), (agent.getClass() == Guard.class), storage.getRadiusPheromone());
                state.setLastAction(actPheromone);

                if(pherStorage.getPheromonesIntruder().size() != 0 || pherStorage.getPheromonesGuard().size() != 0) {
                	if(map != null) {
                		pherStorage.getPheromones().get(pherStorage.getPheromones().size() - 1).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
                		pherStorage.getPheromones().get(pherStorage.getPheromones().size() - 1).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
                	}
                	if(mapPane != null) {
                		mapPane.getChildren().add(pherStorage.getPheromones().get(pherStorage.getPheromones().size() - 1).getShape());
                	}
                }
                this.updateStorages();
                break;
            case "Interop.Action.Move": {
                Interop.Action.Move actMove = (Interop.Action.Move) action;
                soundStorage.addSound(SoundPerceptType.Noise, state.getCurrentPosition(), agentStates.size(), (actMove.getDistance().getValue() / storage.getMaxSprintDistanceIntruder().getValue()) * storage.getMaxMoveSoundRadius());
				if(soundStorage.getSounds().size() > 0) {
					if(map != null) {
						soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
						soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
					}
					if(mapPane != null) {
						mapPane.getChildren().add(soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape());
					}
				}

                double distance = actMove.getDistance().getValue();
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Window")) {
                    distance = distance * storage.getSlowDownModifierWindow();
                }
                
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Door")) {
                	distance = distance * storage.getSlowDownModifierDoor();
                }
                
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.SentryTower")) {
                	distance = distance * storage.getSlowDownModifierSentryTower();
                }
                
                state.setCurrentPosition(new Point(distance * Math.cos(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getX(), distance * Math.sin(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getY()));
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

                if(soundStorage.getSounds().size() > 0) {
					if(map != null) {
						soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
						soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
					}
					if(mapPane != null) {
						mapPane.getChildren().add(soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape());
					}
				}
                
                double distance = actSprint.getDistance().getValue();
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Window")) {
                    distance = distance * storage.getSlowDownModifierWindow();
                }
                
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.Door")) {
                	distance = distance * storage.getSlowDownModifierDoor();
                }
                
                if(a != null && a.getClass().getName().equals("Group3.StaticObjects.SentryTower")) {
                	distance = distance * storage.getSlowDownModifierSentryTower();
                }
                
                state.setCurrentPosition(new Point(distance * Math.cos(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getX(), distance * Math.sin(state.getTargetDirection().getRadians()) + state.getCurrentPosition().getY()));
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
                	if(map != null) {
                		soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterX(this.agentStates.get(currentTurn).getCurrentPosition().getX() * this.map.scalingFactor);
                		soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape().setCenterY(this.agentStates.get(currentTurn).getCurrentPosition().getY() * this.map.scalingFactor);
                	}
                	if(mapPane != null) {
                		mapPane.getChildren().add(soundStorage.getSounds().get(soundStorage.getSounds().size()-1).getShape());
                	}
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
    	this.mapVisualization = new MapVisualization(this.map);
    	//this.mapVisualization.addVisualAgents(agentStates);
    	mapPane = this.mapVisualization.getPane();
    }
    public Map getMap() {	return this.map;}
    public BorderPane getMapPane() {	return mapPane;	}

    public void animationLoop() {
    	animation = new StepAnimationTimer(this.mapVisualization, this ,25);
    	animation.start();
    }
    
    public void updateStorages() {
    	this.pherStorage.updatePheromones();
    	this.soundStorage.updateSounds();
    }

    public BorderPane getPane() {
    	return mapPane;
    }
}
