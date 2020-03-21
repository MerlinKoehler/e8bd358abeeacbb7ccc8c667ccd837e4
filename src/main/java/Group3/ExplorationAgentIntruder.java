package Group3;

import java.util.ArrayList;

import Interop.Action.IntruderAction;
import Interop.Percept.IntruderPercepts;

public class ExplorationAgentIntruder implements Interop.Agent.Intruder {
/*
 * An exploration agent, using the interface of an intruder.
 */
	
	ArrayList<Tile> gridmap = CreateGrid.createGrid();
	
	public IntruderAction getAction(IntruderPercepts percepts) {
		
		return null;
	}
}
