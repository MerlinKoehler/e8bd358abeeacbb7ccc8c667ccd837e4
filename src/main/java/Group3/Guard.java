package Group3;

import Interop.Action.GuardAction;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

public class Guard implements Interop.Agent.Guard {
	GuardAction lastAction = null;

	// Create some sort of matrix, the agent starts in the middle of this.
	// Gets updated while the agent walks.
	int[][] gridMatrix = new int[101][101];

	//General structure guard
	public GuardAction getAction(GuardPercepts percepts) {
		// Update the map, is the action was performed
		if (percepts.wasLastActionExecuted()){
			updateInternalMap();
		}
		else{
			// An alternative when the actual action that hit something; rotate?

		}

		// First, check whether a intruder is seen at the moment.
		ObjectPercept[] vision = (ObjectPercept[]) percepts.getVision().getObjects().getAll().toArray();
		for (int i = 0; i < vision.length; i++) {
			if (vision[i].getType() == ObjectPerceptType.Intruder) {
				return chaseIntruder();
			}
		}

		//For now, no pheromones are used (can be used in the other guard), just what an agent sees and hears.

		//Check for yells (let a guard yell when it sees an intruder).
		SoundPercept[] sounds = (SoundPercept[]) percepts.getSounds().getAll().toArray();
		for (int i = 0; i < sounds.length; i++){
			if (sounds[i].getType() == SoundPerceptType.Yell){
				return goTowardsYell(sounds[i]);
			}
		}

		// Else, explore, try to see and cover as much as possible.
		return explore(percepts);
	}

	// Update the map according to the action that was performed.
	// Codes: 0 = not searched yet, 1 = empty, 2 = wall, 3= window, 4 = door, 5 = sentry tower, 6 = teleport, 7 = shaded
	public void updateInternalMap(){


	}

	// Use the map and a general exploring procedure to explore as much as possible.
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
