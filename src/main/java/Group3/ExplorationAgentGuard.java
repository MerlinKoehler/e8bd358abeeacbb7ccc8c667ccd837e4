package Group3;

import java.util.ArrayList;

import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Action.Yell;
import Interop.Geometry.Angle;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

public class ExplorationAgentGuard implements Interop.Agent.Guard{
/*
 * An exploration class, using the methods in the Guard interface.
 */
	
	ArrayList<Tile> gridmap = CreateGrid.createGrid();
	
// create something that checks whether you have already visited a 'tile'
	
	public GuardAction getAction(GuardPercepts percepts) {
		
		
		
		/*
		//get the percepts
		boolean executed = percepts.wasLastActionExecuted();
		AreaPercepts area = percepts.getAreaPercepts();
		ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts(); 
		SmellPercepts smells = percepts.getSmells();
		SoundPercepts sounds = percepts.getSounds(); //EA is alone, so not necessary
		VisionPrecepts vision = percepts.getVision();
		ObjectPercepts objects = vision.getObjects();
		
		//to have a rule based exploration agent, have some hardcoded rules
		//coverage needs to be as big as possible
		
		//process area - highest priority
		if (area.isInDoor()) {
			//problem: slowdownmodifiers are stated in our own class
			//so when we try to slow these down, we can't make a return a move
			return new Move(scenario.getMaxMoveDistanceGuard());
		}
		else if(area.isInSentryTower()) {
			return new Move(scenario.getMaxMoveDistanceGuard());
		}
		else if(area.isInWindow()) {
			return new Move(scenario.getMaxMoveDistanceGuard());
		}
		else if (area.isJustTeleported()) {
			return new Move(scenario.getMaxMoveDistanceGuard());
		}
			
		
		//process smells - second priority
		SmellPercept[] smellArray = (SmellPercept[]) smells.getAll().toArray();
		for (int i = 0; i < smellArray.length; i++){
			if (smellArray[i].getType() == SmellPerceptType.Pheromone1) {
				return new Rotate(Angle.fromDegrees(45));	
			}
			else if (smellArray[i].getType() == SmellPerceptType.Pheromone2) {
				return new Rotate(Angle.fromDegrees(-45));	
			}
			else if (smellArray[i].getType() == SmellPerceptType.Pheromone3) {
				return new NoAction();
			}
			else if (smellArray[i].getType() == SmellPerceptType.Pheromone4) {
				return new Yell();
			}
			else if (smellArray[i].getType() == SmellPerceptType.Pheromone5) {
				return new NoAction();
			}
		}
				
		
		//process objects - last priority
		ObjectPercept[] obj = (ObjectPercept[]) objects.getAll().toArray();
		for (int i = 0; i < obj.length; i++) {
			Point location = obj[i].getPoint();
			
			if (obj[i].getType() == ObjectPerceptType.Intruder) {
				
			}
			else if (obj[i].getType() == ObjectPerceptType.Door) {
				
			}
			else if (obj[i].getType() == ObjectPerceptType.Teleport) {
				
			}
			else if (obj[i].getType() == ObjectPerceptType.Window) {
				
			}
			else if (obj[i].getType() == ObjectPerceptType.Wall) {
				
			}
			else if (obj[i].getType() == ObjectPerceptType.ShadedArea) {
				
			}
		}
		
		return new Move(scenario.getMaxMoveDistanceGuard());*/
		return null;
	}
}
