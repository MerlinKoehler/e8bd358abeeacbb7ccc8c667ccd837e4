package Group3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import Group3.StaticObjects.Door;
import Group3.StaticObjects.SentryTower;
import Group3.StaticObjects.StaticObject;
import Group3.StaticObjects.Teleport;
import Group3.StaticObjects.Window;
import Interop.Action.Action;
import Interop.Action.NoAction;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Scenario.ScenarioIntruderPercepts;
import Interop.Percept.Scenario.ScenarioPercepts;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.FieldOfView;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

/*
 * We can start the controller from here.
 */

public class MainControl {

	ArrayList<StaticObject> staticObjects;
	ArrayList<AgentState> agentStates;

	List<Interop.Agent.Intruder> intruders;
	List<Interop.Agent.Guard> guards;
	
	MapReader readMap;
	Storage storage;
	PheromoneStorage pherStorage = new PheromoneStorage();
	SoundStorage soundStorage = new SoundStorage();
	
	//made this an object outside to use in the smellpercepts etc
	Object agent;

	private ScenarioPercepts scenarioPercepts = scenarioPercepts();

	/*
	 * TODO: Implement reading objects from the map description file (in MapReader)
	  * No need for StaticObject and its sublcasses, we already have the ObjectPercept class!! */
	private Set<ObjectPercept> objectPercepts;

	int currentTurn = -1;
	
	public MainControl(String path) {
		
		// Read map file and settings
		readMap = new MapReader(path);
		storage = readMap.getStorage();
		staticObjects = readMap.getStaticObjects();
		
		// Initialize Guards and Intruders:
		intruders = AgentsFactory.createIntruders(storage.getNumIntruders());
		guards = AgentsFactory.createGuards(storage.getNumGuards());
		
		// Initialize the states of the agents
		// TODO: Add the correct coordinates from the map file. Actually the agents will be placed at 0,0
		agentStates = new ArrayList<AgentState>();
		
		for (Interop.Agent.Intruder intruder : intruders) 
		{ 
		    AgentState state = new AgentState(0, 0, Direction.fromDegrees(0), intruder);
		    agentStates.add(state);
		}
		for (Interop.Agent.Guard guard : guards) 
		{ 
		    AgentState state = new AgentState(0, 0, Direction.fromDegrees(0), guard);
		    agentStates.add(state);
		}
	}
	
	
	
	public int doStep() {
		// 1. Get the agent who does the next turn
		agent = getAgentNextTurn();
		AgentState state = agentStates.get(currentTurn);
		
		if (agent.getClass() == Guard.class) {
		    System.out.println("This is a Guard");
		    Guard guard = (Guard)agent;
		    
		    // 2. Calculate the perception of the agent
		    GuardPercepts percept = new GuardPercepts(visionPercepts(state),
		    		soundPercepts(state), 
		    		smellPercepts(state),
		    		areaPercepts(state),
		    		scenarioGuardPercepts(),
		    		state.isLastActionExecuted());
		    
		 	// 3. Pass the perception to the agent and retrieve the action
		    Interop.Action.GuardAction action = guard.getAction(percept);
		    
		 	// 4. Check if the agent is allowed to make a move
		    boolean legalAction = checkLegalGuardAction(state, action);
		    
		 	// 6. Update the game state according to the action. 
		    if(legalAction) {
		    	updateAgentState(state, action);
		    	state.setLastAction(action);
		    }
		    else {
		    	state.setLastAction(new NoAction());
		    }
			state.setLastActionExecuted(legalAction);
			
		 	// 7. Check the win / fininsh conditions
			// 0 = not finished
			// 1 = intruders win
			// 2 = guards win
			return(gameFinished());
		}
		
		else if (agent.getClass() == Intruder.class) {
		    System.out.println("This is a Intruder");
		    Intruder intruder = (Intruder)agent;
		    
		    // 2. Calculate the perception of the agent
		    IntruderPercepts percept = new IntruderPercepts(state.getTargetDirection(),
		    		visionPercepts(state),
		    		soundPercepts(state),
		    		smellPercepts(state),
		    		areaPercepts(state),
		    		scenarioIntruderPercepts(),
		    		state.isLastActionExecuted());
		    
		 	// 3. Pass the perception to the agent and retrieve the action
		    Interop.Action.IntruderAction action = intruder.getAction(percept);
		    
		 	// 4. Check if the agent is allowed to make a move
		    boolean legalAction = checkLegalIntruderAction(state, action);
		    
		 	// 6. Update the game state according to the action. 
		    if(legalAction) {
		    	updateAgentState(state, action);
		    	state.setLastAction(action);
		    }
		    else {
		    	state.setLastAction(new NoAction());
		    }
			state.setLastActionExecuted(legalAction);
			
		 	// 7. Check the win / fininsh conditions
			// 0 = not finished
			// 1 = intruders win
			// 2 = guards win
			return(gameFinished());
		}
		return -1;
	}
	
	private Object getAgentNextTurn() {
		currentTurn++;
		if(currentTurn >= agentStates.size()) {
			currentTurn = 0;
		}
		return agentStates.get(currentTurn).getAgent();
	}

	// TODO: implement a function which returns all vision perceptions of the agent in the current state.
	// Oskar
	private VisionPrecepts visionPercepts(AgentState state) {
		if (state.getAgent().getClass() == Intruder.class)
			return new VisionPrecepts(new FieldOfView(
					new Distance(storage.getViewRangeIntruderNormal()),
					Angle.fromDegrees(storage.getViewAngle())),
					new ObjectPercepts(objectPercepts));
		else if (state.getAgent().getClass() == Guard.class)
			return new VisionPrecepts(new FieldOfView(
					new Distance(storage.getViewRangeGuardNormal()),
					Angle.fromDegrees(storage.getViewAngle())),
					new ObjectPercepts(objectPercepts));

		return null; // shouldn't reach
	}
	
	private SoundPercepts soundPercepts(AgentState state) {
		Set<SoundPercept> sounds = new HashSet<SoundPercept>();
		
		for (int i = 0; i < sounds.size(); i++) {
			Point point1 = new Point(state.getX1(), state.getY1());
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
				Distance distance = new Distance(new Point(state.getX1(), state.getY1()), pherStorage.getPheromonesIntruder().get(i).getLocation());
				if (distance.getValue() <= (pherStorage.getPheromonesGuard().get(i).getTurnsLeft()/storage.getPheromoneExpireRounds()) * storage.getRadiusPheromone()){
					SmellPercept smell = new SmellPercept(pherStorage.getPheromonesGuard().get(i).getType(), distance);
					smells.add(smell);
				}
			}
		}
		else if (agent.getClass() == Intruder.class){
			for (int i = 0; i < pherStorage.getPheromonesIntruder().size(); i++) {
				Distance distance = new Distance(new Point(state.getX1(), state.getY1()), pherStorage.getPheromonesIntruder().get(i).getLocation());
				if (distance.getValue() <= (pherStorage.getPheromonesIntruder().get(i).getTurnsLeft())/storage.getPheromoneExpireRounds() * storage.getRadiusPheromone()) {
					SmellPercept smell = new SmellPercept(pherStorage.getPheromonesIntruder().get(i).getType(), distance);
					smells.add(smell);
				}
			}
		}
		SmellPercepts percepts = new SmellPercepts(smells);
		return percepts;
	}
	
	// TODO: implement a function which returns all area perceptions of the agent in the current state.
	// Oskar
	private AreaPercepts areaPercepts(AgentState state) {
		boolean inWindow = false;
		boolean inDoor = false;
		boolean inSentryTower = false;
		boolean justTeleported = false;

		for (StaticObject staticObject : staticObjects) {
			if (staticObject instanceof Teleport){
				if (state.getX1() == ((Teleport) staticObject).getTeleportTo().getX() &&
						state.getY1() == ((Teleport) staticObject).getTeleportTo().getY()) justTeleported = true;
			}
			else if (staticObject.isInside(state.getX1(), state.getY1())) {
				if (staticObject instanceof Window) inWindow = true;
				else if (staticObject instanceof Door) inDoor = true;
				else if (staticObject instanceof SentryTower) inSentryTower = true;
			}
		}

		return new AreaPercepts(inWindow, inDoor, inSentryTower, justTeleported);
	}

	// TODO
	private ScenarioPercepts scenarioPercepts() {
		return null;
	}

	// TODO: implement a function which returns all intruder scenario perceptions of the agent in the current state.
	// Oskar
	private ScenarioIntruderPercepts scenarioIntruderPercepts() {
		return new ScenarioIntruderPercepts(scenarioPercepts, storage.getWinConditionIntruderRounds(),
				storage.getMaxMoveDistanceIntruder(), storage.getMaxSprintDistanceIntruder(), storage.getSprintCoolDown()
				);

	}
	
	// TODO: implement a function which returns all intruder scenario perceptions of the agent in the current state.
	// Oskar
	private ScenarioGuardPercepts scenarioGuardPercepts() {
		return new ScenarioGuardPercepts(scenarioPercepts, storage.getMaxMoveDistanceGuard());
	}
	
	// TODO: implement a function which checks if an action is legal based on the current state of the agent.
	// Victor
	private boolean checkLegalIntruderAction(AgentState state, Interop.Action.IntruderAction action) {
		return false;
	}
	
	// TODO: implement a function which checks if an action is legal based on the current state of the agent.
	// Victor
	private boolean checkLegalGuardAction(AgentState state, Interop.Action.GuardAction action) {
		return false;
	}
	
	// TODO: implement a function, which updates the current game state based on the action of the agent.
	// Merlin
	private void updateAgentState(AgentState state, Action action) {
		switch(action.getClass().getName()) {
		case "Interop.Action.DropPheromone":
			state.setPenalty(storage.getPheromoneCoolDown());
			break;
		case "Interop.Action.Move":
			
			break;
		case "Interop.Action.NoAction":
			break;
		case "Interop.Action.Rotate":
			Interop.Action.Rotate rotate = (Interop.Action.Rotate)action;
			if(rotate.getAngle().getDegrees() <= storage.getMaxRotationAngle()) {
				state.setTargetDirection(Direction.fromDegrees(state.getTargetDirection().getDegrees() +  rotate.getAngle().getDegrees()));
			}
			else {
				state.setLastActionExecuted(false);
			}
			break;
		case "Interop.Action.Sprint":
			state.setPenalty(storage.getSprintCoolDown());
			break;
		case "Interop.Action.Yell":
			break;
		default:
			state.setLastActionExecuted(false);
		}
	}
	
	// TODO: implement a function which checks if the game is finished. Take into account the current game mode.
	// Victor
	private int gameFinished() {
		return 0;
	}
	
	public ArrayList<StaticObject> getStaticObjects(){
		return staticObjects;
	}
	
	public ArrayList<AgentState> getAgentStates() {
		return agentStates;
	}
	
	public static void main(String[] args) {
		MainControl gameController = new MainControl(args[0]);
    }
	
}
