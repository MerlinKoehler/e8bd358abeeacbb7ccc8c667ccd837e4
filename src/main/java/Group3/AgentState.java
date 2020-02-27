package Group3;

import Interop.Geometry.Direction;

public class AgentState {
	
	private int x1;
	private int y1;
	private boolean lastActionExecuted = false;
	private Direction targetDirection;
	private Object agent;
	private int penalty; 
	
	public AgentState(int x1, int y1, Direction targetDirection, Object agent) {
		this.x1 = x1;
		this.y1 = y1;
		this.targetDirection = targetDirection;
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
}
