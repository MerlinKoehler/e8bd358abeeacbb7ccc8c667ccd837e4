package Group3;

import Interop.Action.DropPheromone;
import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Yell;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;

public class Guard implements Interop.Agent.Guard {

	@Override
	public GuardAction getAction(GuardPercepts percepts) {
		// TODO Auto-generated method stub
		//return new Yell();
		//return new Move(new Distance(2));
		return null;
	}
}
