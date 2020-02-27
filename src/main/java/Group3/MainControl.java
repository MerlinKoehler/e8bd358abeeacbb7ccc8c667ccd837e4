package Group3;

import java.util.ArrayList;
import java.util.List;

/*
 * We can start the controller from here.
 */

public class MainControl {

	ArrayList<StaticObject> staticObjects = new ArrayList<StaticObject>();
	List<Interop.Agent.Intruder> intruders;
	List<Interop.Agent.Guard> guards;
	
	MapReader readMap;
	Storage storage;
	
	public MainControl(String path) {
		readMap = new MapReader(path);
		storage = readMap.getStorage();
		staticObjects = readMap.getStaticObjects();
		
		// Initialize Guards and Intruders:
		// TODO: Add the right number of Guards and Intruders
		intruders = AgentsFactory.createIntruders(2);
		guards = AgentsFactory.createGuards(2);
	}
	
	public void doStep() {
		// 1. Get the agent who does the next turn
		
		
		// 2. Calculate the perception of the agent
		// 3. Pass the perception to the agent
		// 4. Check the action of the agent (legal or not) and save the result. (pass it back to the agent in the next step)
		// 5. Update the game state according to the action
	}
	
	public ArrayList<StaticObject> getStaticObjects(){
		return staticObjects;
	}
	
	// TODO: implement
	private Object getAgentNextTurn() {
		return null;
	}
	
	public static void main(String[] args) {
		MainControl gameController = new MainControl("C:\\Users\\Merlin Köhler\\Desktop\\Project 2-2\\e8bd358abeeacbb7ccc8c667ccd837e4\\samplemap.txt");
    }
	
}
