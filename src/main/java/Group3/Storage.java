package Group3;

public class Storage {
	
	private int gameMode;
	private int numGuards;
	private int numIntruders;
	private double captureDistance;
	private int winConditionIntruderRounds;
	private double maxRotationAngle;
	private double maxMoveDistanceIntruder;
	private double maxSprintDistanceIntruder;
	private double maxMoveDistanceGuard;
	private int sprintCoolDown;
	private int pheromoneCoolDown;
	private double radiusPheromone;
	private double slowDownModifierWindow;
	private double slowDownModifierDoor;
	private double slowDownModifierSentryTower;
	private double viewAngle;
	private int viewRays;
	private double viewRangeIntruderNormal;
	private double viewRangeIntruderShaded;
	private double viewRangeGuardNormal;
	private double viewRangeGuardShaded;
	private double viewRangeSentryShort;
	private double viewRangeSentryLong;
	private double yellSoundRadius;
	private double maxMoveSoundRadius;
	private double windowSoundRadius;
	private double doorSoundRadius;
	
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
	
	public double getMaxMoveDistanceIntruder() {
		return maxMoveDistanceIntruder;
	}
	public void setMaxMoveDistanceIntruder(double maxMoveDistanceIntruder) {
		this.maxMoveDistanceIntruder = maxMoveDistanceIntruder;
	}
	
	public double getMaxSprintDistanceIntruder() {
		return maxSprintDistanceIntruder;
	}
	public void setMaxSprintDistanceIntruder(double maxSprintDistanceIntruder) {
		this.maxSprintDistanceIntruder = maxSprintDistanceIntruder;
	}
	
	public double getMaxMoveDistanceGuard() {
		return maxMoveDistanceGuard;
	}
	public void setMaxMoveDistanceGuard(double maxMoveDistanceGuard) {
		this.maxMoveDistanceGuard = maxMoveDistanceGuard;
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

}
