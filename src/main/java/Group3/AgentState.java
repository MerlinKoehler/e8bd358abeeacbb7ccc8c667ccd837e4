package Group3;

import Interop.Action.Action;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;

public class AgentState {
	
	private int x1;
	private int y1;
	private boolean lastActionExecuted = false;
	private Direction targetDirection;
	private Object agent;
	private int penalty;
	private Action lastAction;
	private int inTarget;
	private Point point;

	
	public AgentState(int x1, int y1, Direction targetDirection, Object agent) {
		this.x1 = x1;
		this.y1 = y1;
		this.targetDirection = targetDirection;
		this.point = new Point(x1,y1);
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public boolean isLastActionExecuted() {
		return lastActionExecuted;
	}

	public void setLastActionExecuted(boolean lastActionExecuted) {
		this.lastActionExecuted = lastActionExecuted;
	}

	public Direction getTargetDirection() {
		return targetDirection;
	}

	public void setTargetDirection(Direction targetDirection) {
		this.targetDirection = targetDirection;
	}

	public Object getAgent() {
		return agent;
	}

	public int getPenalty() {
		return penalty;
	}

	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}

	public Action getLastAction() {
		return lastAction;
	}

	public void setLastAction(Action lastAction) {
		this.lastAction = lastAction;
	}

	public int getInTarget(){
		return inTarget;
	}

	public void addInTarget(double a){
		this.inTarget += a;
	}

	public Point getPoint(){
		return this.point;
	}

}
