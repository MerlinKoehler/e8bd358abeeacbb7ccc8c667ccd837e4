package Group3.Intruder;

import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import Group3.Agent.Action;
import Group3.DiscreteMap.BFS;
import Group3.DiscreteMap.DirectedEdge;
import Group3.DiscreteMap.DiscreteMap;
import Group3.DiscreteMap.ObjectType;
import Group3.DiscreteMap.Vertice;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.VisionPrecepts;

/**
 * Intruder Class.
 * @author Margarita Naryzhnyaya, Merlin Koehler, Paula Gitu
 *
 */
public class Intruder implements Interop.Agent.Intruder {
	
	// An object number, to number the intruders.
	private static int serialNumber = 1;
	
	/**
	 * Create a new instance of the intruder object.
	 */
	public Intruder() {
		ownSerialNumber = Intruder.serialNumber;
		Intruder.serialNumber ++;
	}

	// The number of the intruder
	private int ownSerialNumber;
	
	// The last action performed by the intruder.
	Action lastAction = null;
	
	// The discrete map build up by the intruder (its memory).
	DiscreteMap map;
	
	// The current vertex position of the intruder in the discrete graph map.
	Vertice currentPosition;
	
	// The view angle as defined in the map file.
	double viewAngle;
	
	// The vertex radius.
	double radius;
	
	// The current angle of the intruder.
	Angle angle;
	
	// An action list (queue of actions) used for path planning.
	Queue<Action> actionList = new LinkedList<Action>();
	
	// To perform moves in smaller steps (like in doors, windows or sentry towers) 
	// one need to perform multiple moves for going from one vertex to another.
	// The distance counter stores the move distances needed. 
	Queue<Double> distanceCounter = new LinkedList<Double>();
	
	// If the target area is found, this flag is set to true.
	boolean foundTarget = false;
	
	int foundTeleport = 0;
	
	// If flag is set, the intruder goes into escape mode to escape from a guard it has spotted.
	boolean escape = false;

	/** Returns the next move of the intruder.
	 * @param percepts: The current perception of the intruder.
	 */
	@Override
	public IntruderAction getAction(IntruderPercepts percepts) {
		// TODO Auto-generated method stub

		// If the agent is newly initialized or is just teleported initialize all variables and a new map object.
		if(this.map == null || percepts.getAreaPercepts().isJustTeleported()) {
			viewAngle = percepts.getVision().getFieldOfView().getViewAngle().getDegrees();
			radius = Math.sqrt(Math.pow(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue(),2)/2)/2;
			angle = Angle.fromDegrees(0);
			this.map = new DiscreteMap();
			currentPosition = new Vertice(ObjectType.None, new Point(0, 0), radius, new Integer[] {0,0});
			map.addVertice(currentPosition);
			lastAction = null;
			if(percepts.wasLastActionExecuted()) {
				actionList.offer(Action.Move);
			}
			else {
				actionList.offer(Action.Right);
			}
		}
		// If the agent is moving in smaller steps (e.g. in a sentry tower / door), return a new move.
		if(distanceCounter.size() != 0) {
			return new Move(new Distance(distanceCounter.poll()));
		}
		
		// Update the agent state (position / rotation) if the last action was executed.
		if(percepts.wasLastActionExecuted()) {
			updateState();
		}
		// Else turn left and move (prevent agent getting stuck).
		else {
			System.out.println("Illegal Move Intruder!!!");
			actionList = new LinkedList<Action>();
			actionList.offer(Action.Left);
			actionList.offer(Action.Move);
		}
		
		// Create new empty vertices in the view range of the intruder.
		createNewVerticesInSight(percepts.getVision());
		//System.out.println(map.toString(currentPosition.getCoordinate()));
		// Fill the vertices with objects out of the vision percepts.
		evaluateVision(percepts.getVision());

		//System.out.println("");
		//System.out.println(map.toString(currentPosition.getCoordinate()));

		// If no action is left in the action queue, get the next action.
		if(actionList.size() == 0) {
			getNextAction(percepts);
			
			// If there is a conflict, just add a move.
			if(actionList.size() == 0) {
				actionList.offer(Action.Move);
			}
			
			// return the next action of the action list.
			return returnAction(actionList.poll(), percepts);
		}
		else {
			// return the next action of the action list.
			return returnAction(actionList.poll(), percepts);
		}
	}

	/**
	 * Converts a discrete action to a continuous action.
	 * @param action: The discrete action (Left / Right / Move)
	 * @param percepts: The scenario perception (Needed for the maximum move distance).
	 * @return A continuous IntruderAction.
	 */
	private IntruderAction returnAction(Action action, IntruderPercepts percepts) {
		switch(action) {
			case Left:
				//System.out.println("Left");
				return turnLeft();
			case Right:
				//System.out.println("Right");
				return turnRight();
			case Move:
				//System.out.println("Move");
				double maxMoveDistance = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();
				if(percepts.getAreaPercepts().isInSentryTower()) {
					maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
				}
				else if(percepts.getAreaPercepts().isInDoor()) {
					maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
				}
				else if(percepts.getAreaPercepts().isInWindow()) {
					maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
				}
				else {
					maxMoveDistance = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();
				}
				return forward(maxMoveDistance);
			default:
				return new NoAction();
		}
	}
	
	/**
	 * A continuous rotate left by 45 degrees action.
	 * @return: A continuous rotate left by 45 degrees action.
	 */
	private Rotate turnLeft() {
		lastAction = Action.Left;
		return new Rotate(Angle.fromDegrees(-45));
	}

	/**
	 * A continuous rotate right by 45 degrees action.
	 * @return: A continuous rotate right by 45 degrees action.
	 */
	private Rotate turnRight() {
		lastAction = Action.Right;
		return new Rotate(Angle.fromDegrees(+45));
	}

	/**
	 * Returns a new forward action.
	 * @param maxDistance The maximum move distance of the agent.
	 * @return One or more move forward actions.
	 */
	private Move forward(double maxDistance) {
		this.lastAction = Action.Move;
		double distance;

		if(this.angle.getDegrees() % 90 == 0) {
			distance = 2*radius;
		}
		else {
			distance = Math.sqrt(2*Math.pow(2*radius, 2));
		}
		while(distance > maxDistance) {
			distanceCounter.offer(maxDistance);
			distance = distance - maxDistance;
		}
		return new Move(new Distance(distance));
	}

	/**
	 * Update the agent state based on the last executed action.
	 */
	private void updateState() {
		if(lastAction != null) {
			switch(lastAction) {
				case Left:
					this.angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +45));
					break;
				case Right:
					this.angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() -45));
					break;
				case Move:
					Integer[] newPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
					currentPosition = map.getVertice(newPosition);
					break;
			}
		}
	}

	/**
	 * A method for evaluation of the current vision perception of the agent. All objects perceived get stored into the discrete graph map.
	 * @param vision The current vision perception of the agent.
	 */
	private void evaluateVision(VisionPrecepts vision) {
		Set<ObjectPercept> objectPercepts = vision.getObjects().getAll();
		for(ObjectPercept percept : objectPercepts) {
			//double radianAngle = Angle.fromDegrees(angle.getDegrees() + 90).getRadians();
			//double x=percept.getPoint().getX()*Math.cos(radianAngle) + currentPosition.getCenter().getX();
			//double y=percept.getPoint().getY()*Math.sin(radianAngle) + currentPosition.getCenter().getY();
			double addX = currentPosition.getCenter().getX();
			double addY = currentPosition.getCenter().getY();
			Point rotatePoint = truePoint(percept.getPoint());
			Point truePoint = new Point(rotatePoint.getX() + addX, rotatePoint.getY() + addY);
			// TODO: Add x and y
			Vertice inVertice = getRelativeVertice(truePoint);
			switch(percept.getType()) {
				case Door:
					inVertice.setType(ObjectType.Door);
					break;
				case EmptySpace:
					inVertice.setType(ObjectType.None);
					break;
				case SentryTower:
					inVertice.setType(ObjectType.SentryTower);
					break;
				case ShadedArea:
					inVertice.setType(ObjectType.ShadedArea);
					break;
				case TargetArea:
					inVertice.setType(ObjectType.TargetArea);
					foundTarget = true;
					break;
				case Teleport:
					inVertice.setType(ObjectType.Teleport);
					if(foundTeleport == 0) foundTeleport = 1;
					break;
				case Wall:
					inVertice.setType(ObjectType.Wall);
					break;
				case Window:
					inVertice.setType(ObjectType.Window);
					break;
				case Guard:
					map.removeDanger();
					//System.out.println("Danger");
					escape = true;
					inVertice.setType(ObjectType.Danger);
					break;
				case Intruder:
					Random rand = new Random();
					int r = rand.nextInt(2);
					if(r == 0) {
						actionList.add(Action.Left);
						actionList.add(Action.Move);
					}
					else {
						actionList.add(Action.Right);
						actionList.add(Action.Move);
					}
					break;
				default:
					inVertice.setType(ObjectType.Unknown);
					break;
			}
		}
	}

	/**
	 * A method for finding an escape path, in case a guard is seen.
	 */
	private void findEscape() {
		//Vertice candidate = map.getVertice(new Integer[] {0,0});
		//double maxDistance = 0;
		//int x = currentPosition.getCoordinate()[0];
		//int y = currentPosition.getCoordinate()[1];

		actionList = new LinkedList<Action>();

		for (int i = 0; i < 10; i++) {
			actionList.offer(Action.Left);
			actionList.offer(Action.Left);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
			actionList.offer(Action.Right);
			actionList.offer(Action.Right);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
			actionList.offer(Action.Move);
		}
		return;

		/* Angle angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +135));
		Integer[] nextPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
		Vertice nextVertice = map.getVertice(nextPosition);
		for (int i = 0; i < 3; i++) {
			if(BFS.checkVertice(nextVertice)) {
				Vertice currentVertex = nextVertice;
				nextPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
				nextVertice = map.getVertice(nextPosition);
			}
		} */

		/* for(Vertice v : BFS.getReachableVertices(currentPosition)) {
			if(BFS.checkVertice2(v) && v.getEdges().size() < 8) {
				int xp = v.getCoordinate()[0];
				int yp = v.getCoordinate()[1];
				double distance = Math.sqrt(Math.pow(xp-x, 2)+Math.pow(yp-y, 2));
				if(distance > maxDistance) {
					maxDistance = distance;
					candidate = v;
				}
			}
		} */
	}

	/**
	 * Gets the next action(s) of the intruder.
	 * @param percepts The perception of the intruder. 
	 */
	private void getNextAction(IntruderPercepts percepts) {
		// If a teleport is in sight and there are severa intruders in the map, goto the teleport.
		if(foundTeleport == 1 && (ownSerialNumber % 2 == 0)) {
			foundTeleport = 2;
			map.unMark();
			Stack<Vertice> path = BFS.findPath(currentPosition, ObjectType.Teleport);
			generateActionList(path);
			return;
		}
		// If a guard is seen, find an escape path.
		if(escape) {
			//map.unMark();
			escape = false;
			findEscape();
			//map.unMark();
			return;
			/* Stack<Vertice> target = BFS.findPath(currentPosition, escape);
			if(target != null) {
				actionList = new LinkedList<Action>();
				generateActionList(target);
				return;
			} */
		}
		// If the target area is found, calculate the shortest path to the target area.
		if(foundTarget) {
			map.unMark();
			Stack<Vertice> target = BFS.findPath(currentPosition, ObjectType.TargetArea);
			if(target != null) {
				generateActionList(target);
				return;
			}
		}

		// Else, perform a Monte-Carlo-Simulation, to maximize the information gain.
		List<Action> actionSpace = new ArrayList<Action>();
		actionSpace.add(Action.Left);
		actionSpace.add(Action.Right);
		Integer[] nextPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
		Vertice nextVertice = map.getVertice(nextPosition);
		if(BFS.checkVertice(nextVertice)) {
			actionSpace.add(Action.Move);
		}

		double actionValue = 0;
		Action selectedAction = null;
		for(Action action : actionSpace) {
			Angle angle = Angle.fromRadians(this.angle.getRadians());
			Vertice currentPosition = this.currentPosition;
			switch(action) {
				case Left:
					angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +45));
					break;
				case Right:
					angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() -45));
					break;
				case Move:
					Integer[] newPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
					currentPosition = map.getVertice(newPosition);
					break;
			}
			double value = simulateAction(percepts.getVision(), angle, currentPosition);
			if(value > actionValue) {
				actionValue = value;
				selectedAction = action;
			}
		}

		// If there is no action, which leads to new information about the map, find the next unexplored vertex in the room.
		if(selectedAction == null) {
			map.unMark();
			Stack<Vertice> path = BFS.findNonCompleteVertice(currentPosition);
			// If all vertices in the room have been explored: move to a teleport
			if(path == null) {
				map.unMark();
				path = BFS.findPath(currentPosition, ObjectType.Teleport);
			}
			generateActionList(path);
		}
		else {
			actionList.offer(selectedAction);
		}
	}

	/**
	 * A function, that converts a path of vertices to a list of discrete actions, to follow the path.
	 * @param path The path of vertices.
	 */
	private void generateActionList(Stack<Vertice> path) {
		if(path == null) {
			actionList.add(Action.Move);
			return;
		}
		Vertice start = path.pop();
		int currentDegrees = (int)angle.getDegrees();
		while(path.size() != 0) {
			Vertice end = path.pop();
			for(DirectedEdge e : start.getEdges()) {
				if(e.getEndVertice() == end) {
					int degrees = e.getDegrees();
					// Some inaccuracies
					double add;
					if(currentDegrees > 180) {
						add = 360 - currentDegrees;
					}
					else {
						add = -currentDegrees;
					}
					while(Math.abs(currentDegrees - degrees)>2) {
						if(getTrueAngle(degrees + add) < 180) {
							actionList.add(Action.Left);
							currentDegrees = (int)getTrueAngle(currentDegrees +45);
						}
						else {
							actionList.add(Action.Right);
							currentDegrees = (int)getTrueAngle(currentDegrees -45);
						}
					}
					break;
				}
			}
			start = end;
			actionList.add(Action.Move);
		}
	}

	/*
	private IntruderAction getNextAction(IntruderPercepts percepts) {
		if(!percepts.wasLastActionExecuted()) {
			int r = new Random().nextInt(2);
			if(r == 0) {
				return turnLeft();
			}
			else {
				return turnRight();
			}
		}

		int steps = 3;
		int[] counter = new int[steps];
		double[] result = new double[3];
		while(!checkCounter(counter)) {
			Angle angle = Angle.fromRadians(this.angle.getRadians());
			Vertice currentPosition = this.currentPosition;
			for(int i = 0; i < steps; i++) {
				switch(counter[i]) {
				case 0:
					angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +45));
					break;
				case 1:
					angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() -45));
					break;
				case 2:
					Integer[] newPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
					currentPosition = new Vertice(null, new Point(2*radius*newPosition[0], 2*radius*newPosition[1]), 0, newPosition);
					break;
				}
				result[counter[0]] += simulateAction(percepts.getVision(), angle, currentPosition);
			}
			increaseCounter(counter);
		}

		int max = 0;
		for(int i = 1; i < 3; i++) {
			if(result[i] > result[max]) {
				max = i;
			}
		}

		switch(max) {
		case 0:
			return turnLeft();
		case 1:
			return turnRight();
		case 2:
			return forward();
		default:
			return new NoAction();
		}
	}

	private void increaseCounter(int[] counter) {
		int increase = 1;
		for(int i = 0; i < counter.length; i++) {
			if(counter[i] + increase > 2) {
				counter[i] = 0;
			}
			else {
				counter[i] += increase;
				increase = 0;
			}
		}
	}

	private boolean checkCounter(int[] counter) {
		for(int i = 0; i < counter.length; i++) {
			if(counter[i] != 2) return false;
		}
		return true;
	}
	*/

	/**
	 * A function for estimating the information gain of an action.
	 * @param percepts: The agents perception.
	 * @param angle: The view angle of the agent in the new state.
	 * @param currentPosition: The position of the agent in the new state.
	 * @return Currently: The number of new vertices explored in the new state. Can be extended with more parameters, for example cost of the action.
	 */
	public double simulateAction(VisionPrecepts percepts, Angle angle, Vertice currentPosition) {
		double result = numberNewVerticesInSight(angle,currentPosition, percepts);
		return result;
	}

	/**
	 * Returns the number of new vertices in sight of the agent. 
	 * @param angle The rotation of the agent.
	 * @param currentPosition The position of the agent.
	 * @param percepts The perception of the agent.
	 * @return The number of newly explored vertices.
	 */
	private int numberNewVerticesInSight(Angle angle, Vertice currentPosition, VisionPrecepts percepts) {
		int count = 0;
		int samples = 10;
		double viewRange = percepts.getFieldOfView().getRange().getValue();
		double step = viewRange / samples;
		if(step > radius) {
			samples = (int)(viewRange / radius);
			step = radius;
		}
		double currentAngle = (viewAngle+4)/2;
		while(currentAngle >= -viewAngle/2) {
			double finalAngle = 0;
			if(angle.getDegrees() > 180) finalAngle = getTrueAngle(currentAngle + (angle.getDegrees()-360) + 90);
			else finalAngle = getTrueAngle(currentAngle + angle.getDegrees() + 90);

			Angle pAngle = Angle.fromDegrees(finalAngle);
			for(int i=1; i < samples+2; i++) {
				double x=i*step*Math.cos(pAngle.getRadians());
				double y=i*step*Math.sin(pAngle.getRadians());
				x += currentPosition.getCenter().getX();
				y += currentPosition.getCenter().getY();
				Integer[] position = getRelativeVerticeCoordinate(new Point(x,y));
				if(!map.verticeExists(position)) {
					count ++;
				}
			}
			currentAngle = currentAngle -1;
		}
		return count;
	}

	/**
	 * Create new graph map vertices in sight of the agent.
	 * @param percepts The current perception of the agent.
	 */
	private void createNewVerticesInSight(VisionPrecepts percepts) {
		int samples = 10;
		double viewRange = percepts.getFieldOfView().getRange().getValue();
		double step = viewRange / samples;
		if(step > radius) {
			samples = (int)(viewRange / radius);
			step = radius;
		}
		double currentAngle = (viewAngle+4)/2;
		while(currentAngle >= -(viewAngle+4)/2) {
			double finalAngle = 0;
			if(angle.getDegrees() > 180) finalAngle = getTrueAngle(currentAngle + (angle.getDegrees()-360) + 90);
			else finalAngle = getTrueAngle(currentAngle + angle.getDegrees() + 90);

			Angle pAngle = Angle.fromDegrees(finalAngle);
			for(int i=1; i < samples+2; i++) {
				double x=i*step*Math.cos(pAngle.getRadians());
				double y=i*step*Math.sin(pAngle.getRadians());
				x += currentPosition.getCenter().getX();
				y += currentPosition.getCenter().getY();
				Integer[] position = getRelativeVerticeCoordinate(new Point(x,y));
				if(!map.verticeExists(position)) {
					map.addVertice(new Vertice(ObjectType.None, new Point(2*radius*position[0], 2*radius*position[1]), radius, position));
				}
			}
			if(radius < 0.05) {
				currentAngle = currentAngle -.1;
			}
			else {
				currentAngle = currentAngle -1;
			}
		}
	}

	/**
	 * A method that returns the true angle (0 - 360) from an angle. Example: (-40 == 320) 
	 * @param angle The angle (can be negative).
	 * @return The true angle.
	 */
	private double getTrueAngle(double angle) {
		if(angle < 0) {
			angle = 360 + angle;
		}
		else if(angle > 360) {
			angle = 0 + (angle - 360);
		}
		else if(angle == 360) {
			angle = 0;
		}
		return angle;
	}

	/**
	 * A method that returns the vertex of a point based on a relative coordinate. Used for vision evaluation.
	 * @param point The point relative to the agent.
	 * @return The vertex containing the location of the relative point.
	 */
	private Vertice getRelativeVertice(Point point) {
		Integer[] position = getRelativeVerticeCoordinate(point);
		//System.out.println(position[0] + ";" + position[1]);
		return map.getVertice(position);
	}
	

	/**
	 * Gets the vertex, which contains the given relative point.
	 * @param point The point, relative to the agent.
	 * @return The vertex containing the point. 
	 */
	private Integer[] getRelativeVerticeCoordinate(Point point) {
		//Point truePoint = truePoint(point);
		//double x = truePoint.getX();
		//double y = truePoint.getY();
		double x = point.getX();
		double y = point.getY();
		int xVertice = 0;
		int yVertice = 0;
		if(!(Math.abs(x)<=radius)) {
			if(x<0) {
				xVertice--;
				x = x + radius;
			}
			else {
				xVertice++;
				x = x - radius;
			}
			while(Math.abs(x) >= 2*radius) {
				if(x<0) {
					xVertice--;
					x = x + 2*radius;
				}
				else {
					xVertice++;
					x = x - 2*radius;
				}
			}
		}
		if(!(Math.abs(y)<=radius)) {
			if(y<0) {
				yVertice--;
				y = y + radius;
			}
			else {
				yVertice++;
				y = y - radius;
			}
			while(Math.abs(y) >= 2*radius) {
				if(y<0) {
					yVertice--;
					y = y + 2*radius;
				}
				else {
					yVertice++;
					y = y - 2*radius;
				}
			}
		}
		return new Integer[]{xVertice, yVertice};
	}

	/**
	 * A method for calculating the true point based on a relative point to the agent.
	 * @param point The point relative to the agent.
	 * @return The true point, taking into account the rotation and position of the agent.
	 */
	private Point truePoint(Point point) {
		double x = -point.getX();
		double y = point.getY();
		/*
		if((int)angle.getDegrees() == 180) {
			x = -x;
		}
		else if((int)angle.getDegrees() == 0) {
			x = -x;
		}
		*/
		double x_ = x*Math.cos(angle.getRadians()) - y * Math.sin(angle.getRadians());
		double y_ = y*Math.cos(angle.getRadians()) + x * Math.sin(angle.getRadians());
		return new Point(x_, y_);
	}

	/*
	private Point getCenterPoint(int degrees) {
		double x = currentPosition.getCenter().getX();
		double y = currentPosition.getCenter().getY();

		switch(degrees) {
			case 0:
				return new Point(x, y+2*radius);
			case 45:
				return new Point(x+2*radius, y+2*radius);
			case 90:
				return new Point(x+2*radius, y);
			case 135:
				return new Point(x+2*radius, y-2*radius);
			case 180:
				return new Point(x, y-2*radius);
			case 225:
				return new Point(x-2*radius, y-2*radius);
			case 270:
				return new Point(x-2*radius, y);
			case 315:
				return new Point(x-2*radius, y+2*radius);
			default:
				return null;
		}
	}
	*/
}
