package Group3;

import java.util.ArrayList;

import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

public class ExplorationAgent implements Interop.Agent.Guard {

	/*
	 * An exploration class, using the methods in the Guard interface.
	 * This is a rule-based agent.
	 * The only things this exploration agent can do: Move, Rotate.
	 * Tries to cover an area as large as possible.
	 */
	
	public GuardAction getAction(GuardPercepts percepts) {
		
		boolean executed = percepts.wasLastActionExecuted();
		AreaPercepts area = percepts.getAreaPercepts();
		ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts(); 
		VisionPrecepts vision = percepts.getVision();
		ObjectPercepts objects = vision.getObjects();
		
		Angle maxRot = scenario.getScenarioPercepts().getMaxRotationAngle();
		Distance maxDist = scenario.getMaxMoveDistanceGuard();
		Distance rangeSee = vision.getFieldOfView().getRange();
		Distance distWalk = rangeSee;
		   
		//create a few arrays with the different kind of objects we can see
		//we don't need one for empty space or for walls (we don't want to walk towards walls)
		//then it's easier to organize these inputs
		
		ArrayList<ObjectPercept> windows = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> doors = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> teleports = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> shadedArea = new ArrayList<ObjectPercept>();
		
		//find what the maximum is that we can walk without bumping into something
		for(ObjectPercept o: objects.getAll()) {
			//sort them according to type
			//i don't know if there is a more efficient way to do this, but this should work as well
			if (o.getType() == ObjectPerceptType.Door) {
				doors.add(o);
			}
			else if(o.getType() == ObjectPerceptType.Window) {
				windows.add(o);
			}
			else if(o.getType() == ObjectPerceptType.Teleport) {
				teleports.add(o);
			}
			else if(o.getType() == ObjectPerceptType.ShadedArea) {
				shadedArea.add(o);
			}
			
			//if it's in our agent diameter (keep 0.1 distance)
			//if it isn't just an area (empty space, teleport)
			if ((o.getPoint().getX()<= 1.1 || o.getPoint().getX()>= 1.1) && (o.getType() == ObjectPerceptType.Wall && o.getType() == ObjectPerceptType.ShadedArea)) {
				if (distWalk.getValue() < o.getPoint().getY()) {
					//leave some room between the agent and the object
					distWalk = new Distance(o.getPoint().getY() - 0.6);
				}
			}
		}
		
		//so, we can never (normally) walk further than the value we determined earlier
		//exceptions if something is between two walls! such as a door
		if (distWalk.getValue() > maxDist.getValue()) {
			distWalk = maxDist;
		}
		
		//it's important to first check if we are in something, as we want to leave that firstly
		//use the distance we calculated before for this!
		if (area.isInDoor() || area.isInSentryTower() || area.isInWindow() || area.isJustTeleported()) {
			return new Move(distWalk);
		}
		
		// then, continue with the objects (which have been put into arraylists before
		// hierarchy of importance:
		// teleports -> doors -> windows -> walls (ignore) -> shadedArea (avoid)
		
		//try to see if we can move towards a teleport, or need to rotate to reach it
		//helps us cover a lot of land
		double minX = Double.POSITIVE_INFINITY;
		double minY = 0;
		
		for (int i = 0; i < teleports.size(); i++) {
			//determine the smallest x -> so the minimum angle we would need to turn, to walk there
			//firstly, check whether we could reach it we walk in that direction (X = 0)
			if (teleports.get(i).getPoint().getX() == 0) {
				if (teleports.get(i).getPoint().getY() > distWalk.getValue()) {
					return new Move(distWalk);
				}
				else {
					return new Move(new Distance(teleports.get(i).getPoint().getY()));
				}
			}
			else {
				double current = teleports.get(i).getPoint().getX();
				//calculate what the teleport place is with the two x's, the  smallest
				if (current == Math.min(minX, current)){
					minX = current;
					minY = teleports.get(i).getPoint().getY();
				}
				
			}
		}
		//now, rotate to get to the middle of this, so we can walk there in the next turn
		//as we know coordinates, we can just use the tan function
		if(minX != Double.POSITIVE_INFINITY) {
			double angleNeeded = Math.atan((minX/minY));
			Angle angleToTurn = Angle.fromRadians(angleNeeded);
			if (Math.abs(angleToTurn.getRadians()) >scenario.getScenarioPercepts().getMaxRotationAngle().getRadians()) {
				if (angleToTurn.getRadians() > 0) {
					angleToTurn = scenario.getScenarioPercepts().getMaxRotationAngle();
				}
				else {
					angleToTurn = Angle.fromRadians(-scenario.getScenarioPercepts().getMaxRotationAngle().getRadians());
				}
			}
			return new Rotate(angleToTurn);
		}
		
		
		
		//then, try to see if we can automatically move towards a door, or need to rotate to reach it
		//try to aim to go to the middle of the door
		for (int i = 0; i < doors.size(); i++) {
			
		}
		
		//do the same for the windows
		for(int i = 0; i < windows.size(); i++) {
			
		}
		
		//if there's nothing else to do, try to avoid these
		for(int i = 0; i < shadedArea.size(); i++) {
			
		}
		
		//if there's nothing else to do, just walk forward:
		return new Move(distWalk);
	}
}
