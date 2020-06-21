package Group3.Intruder;

import Interop.Action.IntruderAction;
import Interop.Action.NoAction;
import Interop.Percept.IntruderPercepts;


/**
 * Just a dummy intruder doing nothing. 
 * @author Merlin Koehler
 *
 */
public class Dummy_Intruder implements Interop.Agent.Intruder {

	@Override
	public IntruderAction getAction(IntruderPercepts percepts) {
		return new NoAction();
	}
	
	
}
