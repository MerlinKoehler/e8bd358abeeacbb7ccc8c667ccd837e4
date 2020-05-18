package Group3.Intruder;

import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

import Group3.DiscreteMap.BFS;
import Group3.DiscreteMap.DirectedEdge;
import Group3.DiscreteMap.DiscreteMap;
import Group3.DiscreteMap.Vertice;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.VisionPrecepts;


public class Dummy_Intruder implements Interop.Agent.Intruder {

	@Override
	public IntruderAction getAction(IntruderPercepts percepts) {
		return new NoAction();
	}
	
	
}
