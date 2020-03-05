package Group3;

import java.util.ArrayList;
import java.util.List;

import Interop.Action.Action;
import Interop.Action.NoAction;
import Interop.Geometry.*;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Scenario.ScenarioIntruderPercepts;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.FieldOfView;
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
		Object agent = getAgentNextTurn();
		AgentState state = agentStates.get(currentTurn);

		if (agent.getClass() == Guard.class) {
			System.out.println("This is a Guard");
			Guard guard = (Guard)agent;

			// 2. Calculate the perception of the agent
			GuardPercepts percept = new GuardPercepts(visionPercepts(state),
					soundPercepts(state),
					smellPercepts(state),
					areaPercepts(state),
					scenarioGuardPercepts(state),
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
					scenarioIntruderPercepts(state),
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
	/**
	 Cannot tell the difference between the guard and the intruder, assume the view range is the same for both.
	 Same for what penalty should be applied to range - it seems to be a binary choice (normal, shaded), however
	 @see Group3.AgentState, penalty is an integer!

	 disclaimer: there should probably be an Agent class that holds this information (AgentState would then be its
	 subclass and we could get rid of the generic Object type that serves no purpose..)
	 */
	private VisionPrecepts visionPercepts(AgentState state) {


		/* FOV */
		FieldOfView fieldOfView = new FieldOfView(
				new Distance(storage.getViewRangeGuardNormal()),
				Angle.fromDegrees(storage.getViewAngle()));

		/* ObjectPercepts */


		// return new VisionPrecepts(fieldOfView, objectPercepts)
		return null;
	}

	// TODO: implement a function which returns all sound perceptions of the agent in the current state.
	// Janneke
	private SoundPercepts soundPercepts(AgentState state) {
		return null;
	}

	// TODO: implement a function which returns all smell perceptions of the agent in the current state.
	// Janneke
	private SmellPercepts smellPercepts(AgentState state) {
		return null;
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
				if (state.getX1() == ((Teleport) staticObject).getTx() &&
						state.getY1() == ((Teleport) staticObject).getTy()) justTeleported = true;
			}
			else if (staticObject.isInside(state.getX1(), state.getY1())) {
				if (staticObject instanceof Window) inWindow = true;
				else if (staticObject instanceof Door) inDoor = true;
				else if (staticObject instanceof SentryTower) inSentryTower = true;
			}
		}

		return new AreaPercepts(inWindow, inDoor, inSentryTower, justTeleported);
	}

	// TODO: implement a function which returns all intruder scenario perceptions of the agent in the current state.
	// Oskar
	private ScenarioIntruderPercepts scenarioIntruderPercepts(AgentState state) {
		return null;
	}

	// TODO: implement a function which returns all intruder scenario perceptions of the agent in the current state.
	// Oskar
	private ScenarioGuardPercepts scenarioGuardPercepts(AgentState state) {
		return null;
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
		return;
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
