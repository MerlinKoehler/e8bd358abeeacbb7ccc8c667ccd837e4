package Group3_Game_Controller;

import Interop.Geometry.Distance;

public class Storage {
	
	private static int gameMode;
	private static double height;
	private static double width;
	private static int numGuards;
	private static int numIntruders;
	private static double captureDistance;
	private static int winConditionIntruderRounds;
	private static double maxRotationAngle;
	private static Distance maxMoveDistanceIntruder;
	private static Distance maxSprintDistanceIntruder;
	private static Distance maxMoveDistanceGuard;
	private static int sprintCoolDown;
	private static int pheromoneCoolDown;
	private static double radiusPheromone;
	private static double slowDownModifierWindow;
	private static double slowDownModifierDoor;
	private static double slowDownModifierSentryTower;
	private static double viewAngle;
	private static int viewRays;
	private static double viewRangeIntruderNormal;
	private static double viewRangeIntruderShaded;
	private static double viewRangeGuardNormal;
	private static double viewRangeGuardShaded;
	private static double viewRangeSentryShort;
	private static double viewRangeSentryLong;
	private static double yellSoundRadius;
	private static double maxMoveSoundRadius;
	private static double windowSoundRadius;
	private static double doorSoundRadius;
	private static double pheromoneExpireRounds;
	
	public int getGameMode() {
		return gameMode;
	}
	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}
	
	public int getNumGuards() {
		return numGuards;
	}
	public void setNumGuards(int numGuards) {
		this.numGuards = numGuards;
	}
	
	public int getNumIntruders() {
		return numIntruders;
	}
	public void setNumIntruders(int numIntruders) {
		this.numIntruders = numIntruders;
	}
	
	public double getCaptureDistance() {
		return captureDistance;
	}
	public void setCaptureDistance(double captureDistance) {
		this.captureDistance = captureDistance;
	}
	
	public int getWinConditionIntruderRounds() {
		return winConditionIntruderRounds;
	}
	public void setWinConditionIntruderRounds(int winConditionIntruderRounds) {
		this.winConditionIntruderRounds = winConditionIntruderRounds;
	}
	
	public double getMaxRotationAngle() {
		return maxRotationAngle;
	}
	public void setMaxRotationAngle(double maxRotationAngle) {
		this.maxRotationAngle = maxRotationAngle;
	}
	
	public Distance getMaxMoveDistanceIntruder() {
		return maxMoveDistanceIntruder;
	}
	public void setMaxMoveDistanceIntruder(double maxMoveDistanceIntruder) {
		this.maxMoveDistanceIntruder = new Distance(maxMoveDistanceIntruder);
	}
	
	public Distance getMaxSprintDistanceIntruder() {
		return maxSprintDistanceIntruder;
	}
	public void setMaxSprintDistanceIntruder(double maxSprintDistanceIntruder) {
		this.maxSprintDistanceIntruder = new Distance(maxSprintDistanceIntruder);
	}
	
	public Distance getMaxMoveDistanceGuard() {
		return maxMoveDistanceGuard;
	}
	public void setMaxMoveDistanceGuard(double maxMoveDistanceGuard) {
		this.maxMoveDistanceGuard = new Distance(maxMoveDistanceGuard);
	}
	
	public int getSprintCoolDown() {
		return sprintCoolDown;
	}
	public void setSprintCoolDown(int sprintCoolDown) {
		this.sprintCoolDown = sprintCoolDown;
	}
	
	public int getPheromoneCoolDown() {
		return pheromoneCoolDown;
	}
	public void setPheromoneCoolDown(int pheromoneCoolDown) {
		this.pheromoneCoolDown = pheromoneCoolDown;
	}
	
	public double getRadiusPheromone() {
		return radiusPheromone;
	}
	public void setRadiusPheromone(double radiusPheromone) {
		this.radiusPheromone = radiusPheromone;
	}
	
	public double getSlowDownModifierWindow() {
		return slowDownModifierWindow;
	}
	public void setSlowDownModifierWindow(double slowDownModifierWindow) {
		this.slowDownModifierWindow = slowDownModifierWindow;
	}
	
	public double getSlowDownModifierDoor() {
		return slowDownModifierDoor;
	}
	public void setSlowDownModifierDoor(double slowDownModifierDoor) {
		this.slowDownModifierDoor = slowDownModifierDoor;
	}
	
	public double getSlowDownModifierSentryTower() {
		return slowDownModifierSentryTower;
	}
	public void setSlowDownModifierSentryTower(double slowDownModifierSentryTower) {
		this.slowDownModifierSentryTower = slowDownModifierSentryTower;
	}
	
	public double getViewAngle() {
		return viewAngle;
	}
	public void setViewAngle(double viewAngle) {
		this.viewAngle = viewAngle;
	}
	
	public int getViewRays() {
		return viewRays;
	}
	public void setViewRays(int viewRays) {
		this.viewRays = viewRays;
	}
	
	public double getViewRangeIntruderNormal() {
		return viewRangeIntruderNormal;
	}
	public void setViewRangeIntruderNormal(double viewRangeIntruderNormal) {
		this.viewRangeIntruderNormal = viewRangeIntruderNormal;
	}
	
	public double getViewRangeIntruderShaded() {
		return viewRangeIntruderShaded;
	}
	public void setViewRangeIntruderShaded(double viewRangeIntruderShaded) {
		this.viewRangeIntruderShaded = viewRangeIntruderShaded;
	}
	
	public double getViewRangeGuardNormal() {
		return viewRangeGuardNormal;
	}
	public void setViewRangeGuardNormal(double viewRangeGuardNormal) {
		this.viewRangeGuardNormal = viewRangeGuardNormal;
	}
	
	public double getViewRangeGuardShaded() {
		return viewRangeGuardShaded;
	}
	public void setViewRangeGuardShaded(double viewRangeGuardShaded) {
		this.viewRangeGuardShaded = viewRangeGuardShaded;
	}
	
	public double getViewRangeSentryShort() {
		return viewRangeSentryShort;
	}
	public void setViewRangeSentryShort(double viewRangeSentryShort) {
		this.viewRangeSentryShort = viewRangeSentryShort;
	}
	
	public double getViewRangeSentryLong() {
		return viewRangeSentryLong;
	}
	public void setViewRangeSentryLong(double viewRangeSentryLong) {
		this.viewRangeSentryLong = viewRangeSentryLong;
	}
	
	public double getYellSoundRadius() {
		return yellSoundRadius;
	}
	public void setYellSoundRadius(double yellSoundRadius) {
		this.yellSoundRadius = yellSoundRadius;
	}
	
	public double getMaxMoveSoundRadius() {
		return maxMoveSoundRadius;
	}
	public void setMaxMoveSoundRadius(double maxMoveSoundRadius) {
		this.maxMoveSoundRadius = maxMoveSoundRadius;
	}
	
	public double getWindowSoundRadius() {
		return windowSoundRadius;
	}
	public void setWindowSoundRadius(double windowSoundRadius) {
		this.windowSoundRadius = windowSoundRadius;
	}
	
	public double getDoorSoundRadius() {
		return doorSoundRadius;
	}
	public void setDoorSoundRadius(double doorSoundRadius) {
		this.doorSoundRadius = doorSoundRadius;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getPheromoneExpireRounds() {
		return pheromoneExpireRounds;
	}
	public void setPheromoneExpireRounds(double pheromoneExpireRounds) {
		this.pheromoneExpireRounds = pheromoneExpireRounds;
	}

}
