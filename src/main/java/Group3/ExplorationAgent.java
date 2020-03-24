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
		
		Distance distWalk = null;
		
		//the maximum distance we want our agent to walk in - either the max distance, or the max we can see
		if (rangeSee.getValue() < maxDist.getValue()) {
			distWalk = rangeSee;
		}
		else {
			distWalk = maxDist;
		}
		   
		//create a few arrays with the different kind of objects we can see
		//then it's easier to organize these inputs
		ArrayList<ObjectPercept> windows = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> doors = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> teleports = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> shadedArea = new ArrayList<ObjectPercept>();
		ArrayList<ObjectPercept> walls = new ArrayList<ObjectPercept>();
		
		
		//in case the last action can't be executed, try another angle - 1/4th of the max angle
		if(!executed) {
			return new Rotate(Angle.fromRadians(maxRot.getRadians()/4));
		}
		
		//find what the maximum is that we can walk without bumping into something
		//also, sort the objects according to type in the same loop
		for(ObjectPercept o: objects.getAll()) {
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
			else if(o.getType() == ObjectPerceptType.Wall) {
				walls.add(o);
			}
			
			//adjust our max walking distance so we don't bump into something
			//if it's in our agent diameter (keep 0.1 distance)
			//only for walls and shaded areas! (which we want to avoid)
			if ((o.getPoint().getX()<= 0.6 || o.getPoint().getX()>= -0.6) && (o.getType() == ObjectPerceptType.Wall && o.getType() == ObjectPerceptType.ShadedArea)) {
				//if our walking distance now, is bigger than where the wall or shaded area is, shorten this distance
				if (distWalk.getValue() > o.getPoint().getY()) {
					distWalk = new Distance(o.getPoint().getY() - 0.6);
				}
			}
		}
		
		//it's important to first check if we are in something, as we want to leave that firstly
		if (area.isInDoor() || area.isInSentryTower() || area.isInWindow() || area.isJustTeleported()) {
			return new Move(distWalk);
		}
		
		// then, continue with the objects (which have been put into arraylists before
		// hierarchy of importance:
		// teleport (go towards) -> doors (go towards) -> windows (go towards) -> shaded area (avoid) -> walls (only if very close)
		
		//try to see if we can move towards a teleport, or need to rotate to reach it
		//helps us cover a lot of land
		double minX = Double.POSITIVE_INFINITY;
		double minY = 0;
		
		for (int i = 0; i < teleports.size(); i++) {
			//determine the smallest x -> so the minimum angle we would need to turn, to walk there
			//firstly, check whether we could reach it we walk in that direction (X = 0)
			if (teleports.get(i).getPoint().getX() == 0) {
				if (teleports.get(i).getPoint().getY() > distWalk.getValue()) {
					//walk towards it, the max we can walk
					return new Move(distWalk);
				}
				else {
					//walk till you actually reach it
					return new Move(new Distance(teleports.get(i).getPoint().getY()));
				}
			}
			else {
				double current = teleports.get(i).getPoint().getX();
				//calculate what the teleport place is with the two x's, the  smallest
				if (Math.abs(current) == Math.min(Math.abs(minX), Math.abs(current))){
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
			if (Math.abs(angleToTurn.getRadians()) > maxRot.getRadians()) {
				if (angleToTurn.getRadians() > 0) {
					angleToTurn = maxRot;
				}
				else {
					angleToTurn = Angle.fromRadians(-maxRot.getRadians());
				}
			}
			return new Rotate(angleToTurn);
		}
		
		
		//then, try to see if we can automatically move towards a door, or need to rotate to reach it
		//try to aim to go to the 'middle' of the door
		//try to find a middle here
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = 0;
		
		for (int i = 0; i < doors.size(); i++) {
			if (doors.get(i).getPoint().getX() == 0) {
				if (doors.get(i).getPoint().getY() > distWalk.getValue()) {
					return new Move(distWalk);
				}
				else {
					return new Move(new Distance(doors.get(i).getPoint().getY()));
				}
			}
			else {
				double current = doors.get(i).getPoint().getX();
				//calculate what the teleport place is with the two x's, the  smallest
				if (Math.abs(current) == Math.min(Math.abs(minX), Math.abs(current))){
					minX = current;
					minY = doors.get(i).getPoint().getY();
				}
				if (Math.abs(current) == Math.max(Math.abs(maxX),  Math.abs(current))) {
					maxX = current;
					maxY = doors.get(i).getPoint().getY();
				}
			}
		}
		
		//try to get to the 'middle' of this
		double xNeeded = (maxX + minX) /2;
		double yNeeded = (maxY + minY) /2;
		
		if(minX != Double.POSITIVE_INFINITY && maxX != Double.NEGATIVE_INFINITY) {
			double angleNeeded = Math.atan((xNeeded/yNeeded));
			Angle angleToTurn = Angle.fromRadians(angleNeeded);
			
			if (Math.abs(angleToTurn.getRadians()) >maxRot.getRadians()) {
				if (angleToTurn.getRadians() > 0) {
					angleToTurn = maxRot;
				}
				else {
					angleToTurn = Angle.fromRadians(-maxRot.getRadians());
				}
			}
			return new Rotate(angleToTurn);
		}
		
		
		//do the same for the windows
		for(int i = 0; i < windows.size(); i++) {
			if (windows.get(i).getPoint().getX() == 0) {
				if (windows.get(i).getPoint().getY() > distWalk.getValue()) {
					return new Move(distWalk);
				}
				else {
					return new Move(new Distance(windows.get(i).getPoint().getY()));
				}
			}
			else {
				double current = windows.get(i).getPoint().getX();
				//calculate what the teleport place is with the two x's, the  smallest
				if (Math.abs(current) == Math.min(Math.abs(minX), Math.abs(current))){
					minX = current;
					minY = windows.get(i).getPoint().getY();
				}
				
				if (Math.abs(current) == Math.max(Math.abs(maxX),  Math.abs(current))) {
					maxX = current;
					maxY = windows.get(i).getPoint().getY();
				}
			}
		}
			
		//try to get to the 'middle' of this
		xNeeded = (maxX + minX) /2;
		yNeeded = (maxY + minY) /2;
		if(minX != Double.POSITIVE_INFINITY && maxX != Double.NEGATIVE_INFINITY) {
			double angleNeeded = Math.atan((xNeeded/yNeeded));
			Angle angleToTurn = Angle.fromRadians(angleNeeded);
			if (Math.abs(angleToTurn.getRadians()) >maxRot.getRadians()) {
				if (angleToTurn.getRadians() > 0) {
					angleToTurn =  maxRot;
				}
				else {
					angleToTurn = Angle.fromRadians(-maxRot.getRadians());
				}
			}
			return new Rotate(angleToTurn);
		}
		
		//if there's nothing else to do, try to avoid these as much as possible -> turn in the opposite direction
		for(int i = 0; i < shadedArea.size(); i++) {
				double current = Math.abs(shadedArea.get(i).getPoint().getX());
				
				if (Math.abs(current) == Math.max(Math.abs(maxX),  Math.abs(current))) {
					maxX = current;
					maxY = shadedArea.get(i).getPoint().getY();
				}
		}
		
		if(maxX != Double.NEGATIVE_INFINITY) {
			double angleNeeded = Math.atan((maxX/maxY));
			Angle angleToTurn = Angle.fromRadians(angleNeeded);
			if (Math.abs(angleToTurn.getRadians()) >maxRot.getRadians()) {
				if (angleToTurn.getRadians() > 0) {
					angleToTurn =  maxRot;
				}
				else {
					angleToTurn = Angle.fromRadians(-maxRot.getRadians());
				}
			}
			//want to turn in the opposite direction!!
			return new Rotate(Angle.fromRadians(-angleToTurn.getRadians()));
		}
		
		//if a wall is close, try turn around a bit, so it won't get stuck, trying to walk into it
		for (int i = 0; i < walls.size(); i++) {
			Point wall = walls.get(i).getPoint();
			if (wall.getY() < 1 && wall.getX() < 0.6) {
				Angle newAn = Angle.fromRadians(maxRot.getRadians() /2);
				return new Rotate(newAn);
			}
		}
		
		//if there's nothing else to do, just walk forward:
		return new Move(distWalk);
	}
}
