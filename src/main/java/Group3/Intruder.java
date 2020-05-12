package Group3;

import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
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
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.VisionPrecepts;

public class Intruder implements Interop.Agent.Intruder {

	private enum Action{
		Left,
		Right,
		Move
	}
	
	Action lastAction = null;
	DiscreteMap map;
	Vertice currentPosition;
	double viewAngle;
	double radius;
	Angle angle;
	
	@Override
	public IntruderAction getAction(IntruderPercepts percepts) {
		// TODO Auto-generated method stub
		
		if(this.map == null || percepts.getAreaPercepts().isJustTeleported()) {
			viewAngle = percepts.getVision().getFieldOfView().getViewAngle().getDegrees();
			radius = Math.sqrt(Math.pow(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue(),2)/2)/2;
			angle = Angle.fromDegrees(0);
			this.map = new DiscreteMap();
			currentPosition = new Vertice(ObjectType.None, new Point(0, 0), radius, new Integer[] {0,0});
			map.addVertice(currentPosition);
		}
		if(percepts.wasLastActionExecuted()) {
			updateState();
		}
		
		createNewVerticesInSight(percepts.getVision());
		evaluateVision(percepts.getVision());
		
		System.out.println("");
		System.out.println(map.toString(currentPosition.getCoordinate()));
		
		return getNextAction(percepts);
	}
	
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
			int l = 1;
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
				break;
			case Teleport:
				inVertice.setType(ObjectType.Teleport);
				break;
			case Wall:
				inVertice.setType(ObjectType.Wall);
				break;
			case Window:
				inVertice.setType(ObjectType.Window);
				break;
			default:
				inVertice.setType(ObjectType.Unknown);
				break;
			}
		}
	}
	
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
		List<Action> actionSpace = new ArrayList<Action>();
		actionSpace.add(Action.Left);
		actionSpace.add(Action.Right);
		Integer[] nextPosition = DiscreteMap.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
		Vertice nextVertice = map.getVertice(nextPosition);
		ObjectType objType = nextVertice.getType();
		if(objType != ObjectType.Wall && objType != ObjectType.Teleport && objType != ObjectType.SentryTower) {
			actionSpace.add(Action.Move);
		}
		
		double actionValue = 0;
		Action selectedAction = null;
		for(Action action : actionSpace) {
			double value = simulateAction(action, percepts.getVision());
			if(value > actionValue) {
				actionValue = value;
				selectedAction = action;
			}
		}
		if(selectedAction == null) {
			return forward();
		}
		switch(selectedAction) {
		case Left:
			return turnLeft(); 
		case Right:
			return turnRight();
		case Move:
			return forward();
		default:
			return new NoAction();
		}
	}
	
	public double simulateAction(Action action, VisionPrecepts percepts) {
		int vertexMultiply = 1;
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
		
		
		double result = vertexMultiply*numberNewVerticesInSight(angle,currentPosition, percepts);
		return result;
	}
	
	private int numberNewVerticesInSight(Angle angle, Vertice currentPosition, VisionPrecepts percepts) {
		int count = 0;
		int samples = 10;
		double viewRange = percepts.getFieldOfView().getRange().getValue();
		double step = viewRange / samples;
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
	
	private Rotate turnLeft() {
		lastAction = Action.Left;
		return new Rotate(Angle.fromDegrees(-45));
	}
	
	private Rotate turnRight() {
		lastAction = Action.Right;
		return new Rotate(Angle.fromDegrees(+45));
	}
	
	private Move forward() {
		this.lastAction = Action.Move;
		
		if(this.angle.getDegrees() % 90 == 0) {
			return new Move(new Distance(2*radius));
		}
		else {
			return new Move(new Distance(Math.sqrt(2*Math.pow(2*radius, 2))));
		}
	}
	
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
	
	private void createNewVerticesInSight(VisionPrecepts percepts) {
		int samples = 10;
		double viewRange = percepts.getFieldOfView().getRange().getValue();
		double step = viewRange / samples;
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
					map.addVertice(new Vertice(ObjectType.None, new Point(2*radius*position[0], 2*radius*position[1]), radius, position));
				}
			}
			currentAngle = currentAngle -1;
		}
	}
	
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
	
	private Vertice getRelativeVertice(Point point) {
		Integer[] position = getRelativeVerticeCoordinate(point);
		//System.out.println(position[0] + ";" + position[1]);
		return map.getVertice(position);
	}
	
	
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
}
