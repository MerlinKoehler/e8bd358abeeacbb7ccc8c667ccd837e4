package Group3;

import Group3.GridMap.Grid;
import Group3.GridMap.GridMapStorage;
import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.VisionPrecepts;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Guard implements Interop.Agent.Guard {
	// Store the last action, for updating the map and making decisions
	GuardAction lastAction = null;

	// Start in the middle of the map, current angle being 0
	double currentX = 0;
	double currentY = 0;
	double currentAngleInRads = 0;
	double gridSize = 0.5;

	ArrayList<GridMapStorage> maps = new ArrayList<>();
	//Make a start map.
	GridMapStorage currentMap = new GridMapStorage(gridSize);

	// To add a bit of randomness, when the normal algorithm does not help anymore:
	Random random = new Random();

	// When it is chased, keep track of this, to decide on an action
	int lastSeenIntruder = Integer.MAX_VALUE;
	boolean chasing = false;

	//General structure guard - finds action
	public GuardAction getAction(GuardPercepts percepts) {
		// First, find the maximum distance in which the guard can currently move.
		double currentSlowDownModifier = 1;

		if (percepts.getAreaPercepts().isInDoor()){
			currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
		}
		else if (percepts.getAreaPercepts().isInSentryTower()){
			currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
		}
		else if (percepts.getAreaPercepts().isInWindow()){
			currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
		}

		// The maximum distance a guard can currently be moves is found:
		double maximumDistance = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * currentSlowDownModifier;

		//--------------------------------------------------------------------------------------------------------------
		// Update the map, is the action was performed
		if (percepts.wasLastActionExecuted()){
			updateInternalMap(percepts); // will also update the agent's current  state
		}

		// First, check whether a intruder is seen at the moment.
		ObjectPercept[] vision = (ObjectPercept[]) percepts.getVision().getObjects().getAll().toArray();
		for (int i = 0; i < vision.length; i++) {
			if (vision[i].getType() == ObjectPerceptType.Intruder) {
				lastAction = chaseIntruder();
				return lastAction;
			}
		}

		// Lower priority than chasing a guard.
		if (!percepts.wasLastActionExecuted()){
			// Rotate randomly in case the exploration is not working (IDK if we'll need it)
			lastAction = new Rotate(Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
			return lastAction;
		}

		// Else, explore, try to see and cover as much as possible.
		lastAction = explore(percepts);
		return lastAction;
	}

	// Update the map according to the action that was performed.
	// Also update the state of the agent.
	// Codes: 1 = possible to walk through, 2 = wall, 3 = teleport
	public void updateInternalMap(GuardPercepts percepts){
		for (int i = 0; i < currentMap.getGrid().size(); i++){
			// Add '1' to the lastSeen variable. (The ones we saw will be set to 0 anyways.)
			currentMap.updateSeen();
		}

		// Create a new map when it has just teleported.
		if (percepts.getAreaPercepts().isJustTeleported()){
			maps.add(currentMap);

			// Links to the old map that has been stored in the arraylist.
			currentMap = new GridMapStorage(gridSize, (maps.size()-1));
			updateMapSight(percepts);
		}
		else if (lastAction instanceof Interop.Action.Rotate){
			currentAngleInRads = currentAngleInRads + ((Rotate) lastAction).getAngle().getRadians();
			updateMapSight(percepts);
		}
		// Can check the places in front of it.
		else if (lastAction instanceof Interop.Action.Move){
			// First the starting place.
			double oldX = currentX;
			double oldY = currentY;

			// Update this accordingly
			double currentX = oldX + Math.cos(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();
			double currentY = oldY - Math.sin(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();

			currentMap.updateGrid(new Point(oldX, oldY), new Point(currentX, currentY), 1);
			updateMapSight(percepts);
		}
	}

	public void updateMapSight(GuardPercepts percepts){
		// The same as before, but use the special properties. (Such as 'wall' and 'teleport')
		ObjectPercept[] vision = (ObjectPercept[]) percepts.getVision().getObjects().getAll().toArray();
		for (int i = 0; i < vision.length; i++) {
			Point objectPoint = vision[i].getPoint();
			Point currentPoint = new Point(currentX, currentY);
			int type = 0;
			if (vision[i].getType() == ObjectPerceptType.Teleport){
				type = 3;
			}
			else if (vision[i].getType() == ObjectPerceptType.Wall){
				type = 2;
			}
			else{
				type = 1;
			}
			// Sets the last grid it adds to have a special property (as it was possible to walk through the rest of them)
			this.currentMap.updateGrid(currentPoint, objectPoint, type);
		}
	}

	// Use the map and a general exploring procedure to explore as much as possible.
	// Try to discover the intruder.
	public GuardAction explore(GuardPercepts percepts){
		GuardAction action = null;

		return action;
	}

	public GuardAction goTowardsYell(SoundPercept goTo){
		GuardAction action = null;


		return action;
	}

	public GuardAction chaseIntruder(){
		GuardAction action = null;



		return action;
	}
}
