package Group3;

import Group3.GridMap.Grid;
import Group3.GridMap.GridMapStorage;
import Interop.Action.*;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Geometry.Direction;
import Interop.Utils.Utils;
import java.util.ArrayList;
import java.util.Random;

public class Guard implements Interop.Agent.Guard {
    private GuardAction lastAction = null; // null if could not be executed

    //Start in the middle of the first tile - with the coordinates and angle set to 0
    private double currentX = 0;
    private double currentY = 0;
    private double currentAngleInRads = 0;

    // Choose the type of exploration, 1 being advanced random, 2 being the tile-to-tile exploration method.
    private int exploringType = 1;

    // Keep track of the number of actions performed, and of the number of agents (only to assign a serial number to the agent)
    private int turnNumber = 0;
    private static int numberOfAgents;
    private int currentAgent = -1;

    // To track and take into account yells and pheromones
    private boolean yellsAndPheromones = false;
    private boolean runningFromPher = false;
    private boolean chasingYell = false;
    private final int pheromonesDroppedTurns = 50;

    // GRID MAP SETTINGS
    private double gridSize = 10;
    private GridMapStorage currentMap = new GridMapStorage(gridSize, false);

    // Create a Random object for rotations.
    private Random random = new Random();

    //When it is chased, keep track of this to decide on an action.
    private int lastSeenIntruder = Integer.MAX_VALUE;
    private boolean chasing = false;
    boolean chasing2 = false;
    boolean chasing3 = false;

    // Settings for the advanced random exploration.
    private int how_long_exploring = 0;
    private final int maxExplorationTime = 100;

    // Keep track of how much the angle needs to be changed.
    private double directionChangeNeeded = 0;
    private double targetDirection = -1;

    // Settings for the second exploration method.
    private Point explorationTarget = null;
    private int rotateInPlace = 8;

    // Give a guard a certain number, depending on this, it is a certain "type" - regarding pheromones.
    public Guard(){
        // Give every Guard a certain number, will be the "serial number" of this guard.
        numberOfAgents++;
        currentAgent = numberOfAgents;
    }

    //General structure guard - finds the action that needs to be performed.
    public GuardAction getAction(GuardPercepts percepts) {

        // Increase the turn number (this is a total).
        turnNumber++;

        // In case the guard has just teleported; reset the internal system.
        if (percepts.getAreaPercepts().isJustTeleported()) {
            reset();
        }

        // The maximum distance a guard can currently be moves is found:
        double maximumDistance = this.getCurrentmaxDist(percepts);

        //--------------------------------------------------------------------------------------------------------------
        // Update the map and the agent's state, if the action was performed
        if (lastAction != null && percepts.wasLastActionExecuted()) {
            updateInternalMap(percepts);
        }

        // First, check whether a intruder is seen at the moment - overrides other actions.
        Object[] vision = percepts.getVision().getObjects().getAll().toArray();
        for (int i = 0; i < vision.length; i++) {
            if (((ObjectPercept) vision[i]).getType() == ObjectPerceptType.Intruder) {
                // This finds the direction in which the guard needs to turn.
                startChasing((ObjectPercept) vision[i], percepts);

                //chaseIntruder((ObjectPercept) vision[i], percepts);
            }
        }

        // Override the chasing of the yell and the running away from pheromones.
        if (chasing){
            chasingYell = false;
            runningFromPher = false;
        }

        // Check whether there are yells and/or pheromones, handles this accordingly.
        if (yellsAndPheromones && !chasing){
            // Store the information of the yells, which can later be used to determine an action.
            boolean yellFound = false;
            ArrayList<SoundPercept> yells = new ArrayList<>();
            Object[] sounds = percepts.getSounds().getAll().toArray();

            // Finds and stored yells.
            for (int i = 0; i < sounds.length; i++) {
                if (((SoundPercept) sounds[i]).getType() == SoundPerceptType.Yell) {
                    yellFound = true;
                    yells.add((SoundPercept) sounds[i]);
                }
            }

            // For pheromones, also store the information, in a similar way - only check the pheromones that the current guard doesn't drop.
            boolean pheroFound = false;
            ArrayList<SmellPercept> phers = new ArrayList<>();

            // Pheromones have the second priority.
            // Can show whether another guard is nearby.
            Object[] smells = percepts.getSmells().getAll().toArray();
            for (int i = 0; i < smells.length; i++) {
                if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone1 && this.currentAgent %5 != 1){
                    pheroFound = true;
                    phers.add((SmellPercept) smells[i]);
                }
                else if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone2 && this.currentAgent %5 != 2){
                    pheroFound = true;
                    phers.add((SmellPercept) smells[i]);
                }
                else if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone3 && this.currentAgent %5 != 3){
                    pheroFound = true;
                    phers.add((SmellPercept) smells[i]);
                }
                else if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone4 && this.currentAgent %5 != 4){
                    pheroFound = true;
                    phers.add((SmellPercept) smells[i]);
                }
                else if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone5 && this.currentAgent %5 != 0){
                    pheroFound = true;
                    phers.add((SmellPercept) smells[i]);
                }
            }

            // In case any of these was found, use this in the choice of action.
            if (yellFound || pheroFound){
                if (yellFound){
                    chasingYell = true;
                }
                else if (pheroFound){
                    runningFromPher = true;
                }
                handleYellAndPheromones(yells, phers);
            }
        }

        // Check whether a direction change is needed for the chasing of yells, if so do this, and then run until the wall is hit.
        if (Math.abs(directionChangeNeeded) > 0 && (chasingYell || chasing || runningFromPher)){
            how_long_exploring = 0;
            if (directionChangeNeeded > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                lastAction = new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
            }
            else if (-directionChangeNeeded > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                lastAction = new Rotate(Angle.fromRadians(-percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
            }
            else{
                new Rotate(Angle.fromRadians(directionChangeNeeded));
            }
            return lastAction;
        }
        // If the direction is good, then walk in a straight line (until it needs to be adjusted again, or an action is not executed.)
        else if ((chasingYell || chasing || runningFromPher)&& percepts.wasLastActionExecuted() ){
            how_long_exploring = 0;
            return new Move(new Distance(maximumDistance));
        }
        else{
            chasingYell = false;
            chasing = false;
            runningFromPher = false;
        }

        // Drop a pheromone every n amount of turns, to signal where the guard is.
        if (turnNumber % pheromonesDroppedTurns == 0){
            //System.out.println("droppher" + currentAgent);
            if (this.currentAgent %5 != 1){
               lastAction = new DropPheromone(SmellPerceptType.Pheromone1);
            }
            if (this.currentAgent %5 != 2){
                lastAction = new DropPheromone(SmellPerceptType.Pheromone2);
            }
            if (this.currentAgent %5 != 3){
                lastAction = new DropPheromone(SmellPerceptType.Pheromone3);
            }
            if (this.currentAgent %5 != 4){
                lastAction = new DropPheromone(SmellPerceptType.Pheromone4);
            } if (this.currentAgent %5 != 0){
                lastAction = new DropPheromone(SmellPerceptType.Pheromone5);
            }
            return lastAction;
        }

        // If none of these conditions is met, then explore as normally.
        if (exploringType == 1) {
            lastAction = advancedRandomExploring(percepts, maximumDistance);
            return lastAction;
        }

        /*
        NOTE: I did not change anything about Oskar's code, only the code above this part and below it.
         */

        //for (Grid grid : currentMap.getGrid()) {
        //    System.out.println(grid);
        //}
        // Lower priority than chasing a guard.

        /*
        Other way of exploration - currently not used.
         */
        /* EXPLORATION */

        if (false)
            return new NoAction();
        if (rotateInPlace > 0) {
            lastAction = new Rotate(Angle.fromDegrees(45));
            rotateInPlace--;
            return lastAction;
        }

        if (explorationTarget != null) {
            //System.out.println("DISTANCE: " + new Distance(new Point(currentX, currentY), explorationTarget).getValue());
            //System.out.println("TILE LOCATION: " + explorationTarget.getX() + " : " + explorationTarget.getY());
            //System.out.println("AGENT LOCATION: " + currentX + " : " + currentY);
            if (new Distance(new Point(currentX, currentY), explorationTarget).getValue() < 1) {
                explorationTarget = null; // target reached
                rotateInPlace = 8;
            } else if (explorationTarget.getClockDirection().getDistance(
                    Angle.fromRadians(currentAngleInRads - Math.PI)).getDegrees() > 5) {
                Angle rotateBy = Angle.fromRadians(
                        Utils.getSignedDistanceBetweenAngles(
                                currentAngleInRads, explorationTarget.getClockDirection().getRadians() - Math.PI
                        )
                );
                if (Math.abs(rotateBy.getDegrees()) >
                        percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees())
                    rotateBy = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle();
                System.out.println("ROTATE BY: " + rotateBy.getDegrees());
                lastAction = new Rotate(rotateBy);
                return lastAction;
            } else {
                Distance moveBy = new Distance(new Point(currentX, currentY), explorationTarget);
                if (moveBy.getValue() > percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue())
                    moveBy = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
                //System.out.println(moveBy.getValue());
                lastAction = new Move(moveBy);
                return lastAction;
            }
        }


        if (lastAction == null) {
            lastAction = new Move(new Distance(0.1));
            return lastAction;
        }

        int notSeenFor = -1;
        Grid g = null;
        for (Grid grid : this.currentMap.getGrid()) {
            if (grid.getType() == 1 && grid.getLastSeen() > notSeenFor) {
                g = grid; // Grid that hasn't been visited for the largest amount of turns
                notSeenFor = grid.getLastSeen();
            }
        }
        System.out.println(notSeenFor);

        /* Determine the nearest corner point belonging the grid tile */

        Distance distanceToGridCornerPoint = new Distance(Integer.MAX_VALUE);
        Point[] cornerPoints = {
                g.getBottomLeft(),
                g.getBottomRight(),
                g.getTopLeft(),
                g.getTopRight()
        };
        for (Point cornerPoint : cornerPoints) {
            Distance tempDistance = new Distance(cornerPoint, new Point(currentX, currentY));
            if (tempDistance.getValue() < distanceToGridCornerPoint.getValue()) {
                distanceToGridCornerPoint = tempDistance;
                explorationTarget = new Point(cornerPoint.getX(), -cornerPoint.getY());
            }
        }

        Angle rotateBy = Angle.fromDegrees(explorationTarget.getClockDirection().getDegrees() - 180);
        if (rotateBy.getDegrees() > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees())
            rotateBy = Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees());
        lastAction = new Rotate(rotateBy);
        //lastAction = new NoAction();
        return lastAction;

    }

    // Update the map according to the action that was performed.
    // Also update the state of the agent.
    // Codes: 1 = possible to walk through, 2 = wall, 3 = teleport
    public void updateInternalMap(GuardPercepts percepts) {
        // Add '1' to the lastSeen variable. (The ones we saw will be set to 0 anyways.)
        currentMap.updateSeen();

        // Create a new map when it has just teleported.
        if (percepts.getAreaPercepts().isJustTeleported()) {
            currentMap = new GridMapStorage(gridSize, true);
            updateMapSight(percepts);
        }

        // ELse, if it simply rotated, update the map according to what the agent can see at the moment.
        else if (lastAction instanceof Interop.Action.Rotate) {
            // Adjust the current angle.
            currentAngleInRads = currentAngleInRads + ((Rotate) lastAction).getAngle().getRadians();

            // Adjust the change in direction needed.
            if (directionChangeNeeded > 0) {
                directionChangeNeeded = directionChangeNeeded - ((Rotate) lastAction).getAngle().getRadians();
            }

            // Make sure the angle is between 0 and 2pi.
            if (currentAngleInRads > Math.PI * 2) {
                currentAngleInRads = currentAngleInRads % (2 * Math.PI);
            }
            // Now, update the part of the map that you see
            updateMapSight(percepts);
        }
        // Can check the places in front of it.
        else if (lastAction instanceof Interop.Action.Move) {
            // First the starting place.
            double oldX = currentX;
            double oldY = currentY;

            // Update this accordingly
            currentX = oldX + Math.cos(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();
            currentY = oldY + Math.sin(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();

            //Update the gridmap, and the part of the map that the agent sees after the move.
            currentMap.updateGrid(new Point(oldX, oldY), new Point(currentX, currentY), 1);
            updateMapSight(percepts);
        }
    }

    //Updates the sight of a map (creates grids), given the points of the vision
    public void updateMapSight(GuardPercepts percepts) {
        // The same as before, but use the special properties. (Such as 'wall' and 'teleport')

        // Uses all the things it sees.
        Object[] vision = percepts.getVision().getObjects().getAll().toArray();
        for (int i = 0; i < vision.length; i++) {
            // For two points (current and the one of the object), tiles can be determined for everything in between.
            Point objectPoint = ((ObjectPercept) vision[i]).getPoint();
            Point currentPoint = new Point(currentX, currentY);

            // Types depend on the types of tiles there are.
            int type = 0;
            if (((ObjectPercept) vision[i]).getType() == ObjectPerceptType.Teleport) {
                type = 3;
            } else if (((ObjectPercept) vision[i]).getType() == ObjectPerceptType.Wall) {
                type = 2;
            } else {
                type = 1;
            }

            // Sets the last grid it adds to have a special property (as it was possible to walk through the rest of them)
            this.currentMap.updateGrid(currentPoint, objectPoint, type);
        }
    }

    // When yells and/or pheromones are spotted, set the direction change that is needed.
    public void handleYellAndPheromones(ArrayList<SoundPercept> yells, ArrayList<SmellPercept> pher){
            // For yells, the direction is already given.
            Direction goInto = null;
            if (yells.size() != 0){
                goInto = yells.get(0).getDirection();
                directionChangeNeeded = goInto.getRadians();
            }
            // If there are no yells, but there are pheromones, then another guard is nearby - completely turn around and walk away.
            else if (pher.size() != 0){
                directionChangeNeeded = Math.PI;
            }
    }

    // This determines the acion when it is randomly exploring, but needs to return to certain areas in between.
    public GuardAction advancedRandomExploring(GuardPercepts percepts, double maximumDistance) {
        // Check whether it has been exploring for long enough.
        if (how_long_exploring <= maxExplorationTime) {
            targetDirection = -1;
            how_long_exploring++;

            //If it's randomly exploring, walk around and change its direction sometimes.
            if (random.nextDouble()> 0.99 || !percepts.wasLastActionExecuted()){
                if (random.nextDouble()> 0.5 || !percepts.wasLastActionExecuted()) {
                    lastAction = new Rotate(Angle.fromDegrees(-percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
                }
                else{
                    lastAction = new Rotate(Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
                }
                return lastAction;
            }
            else if (percepts.wasLastActionExecuted()) {
            lastAction = new Move(new Distance(maximumDistance));
            return lastAction;
            }
        }

        // After a certain amount of time, walk in the direction of an area that hasn't been seen for the longest time.
        else if (how_long_exploring > maxExplorationTime) {

            // Firstly, find the direction to go in. (This will always hold true if it has first explored.)
            if (targetDirection == -1) {
                int max = -1;
                Grid target = null;

                // Find the tile that has not been seen for the longest time.
                for (int i = 0; i < currentMap.getGrid().size(); i++) {
                    max = Math.max(currentMap.getGrid().get(i).getLastSeen(), max);

                    // Set the target tile.
                    if (max == currentMap.getGrid().get(i).getLastSeen()) {
                        target = currentMap.getGrid().get(i);
                    }
                }

                // Find the coordinates of the target tile's middle point.
                double xcoor = target.getTopRight().getX() - target.getSize();
                double ycoor = target.getTopRight().getY() - target.getSize();

                // Find angles between vectors
                double yvec = ycoor - currentY;
                double xvec = xcoor - currentX;
                double magnvec = Math.sqrt(xvec*xvec + yvec*yvec);

                // Calculate the angle differences
                if (currentAngleInRads <= Math.atan(yvec/xvec)){
                    targetDirection =  currentAngleInRads + Math.acos((xvec * Math.cos(currentAngleInRads*1)+ yvec * Math.sin(currentAngleInRads*1))/magnvec);
                }
                else{
                    targetDirection = currentAngleInRads - Math.acos((xvec * Math.cos(currentAngleInRads*1)+ yvec * Math.sin(currentAngleInRads*1))/magnvec);
                }
            }

            // Now, start rotating in a certain direction.
            if (currentAngleInRads != targetDirection) {
                double needToMove = targetDirection - currentAngleInRads;
                if (needToMove > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                    lastAction = new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
                } else if (needToMove < -percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                    lastAction = new Rotate(Angle.fromRadians(-percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
                } else {
                    lastAction = new Rotate(Angle.fromRadians(needToMove));
                }
            } else {
                // If it bumps into something
                if (!percepts.wasLastActionExecuted()) {
                    targetDirection = -1;
                    how_long_exploring = 0;
                    lastAction = new Rotate(Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
                } else {
                    lastAction = new Move(new Distance(maximumDistance));
                }
            }
        }
        return lastAction;
    }

    // Calculates the direction change that is needed to chase after the intruder.
    public void startChasing(ObjectPercept intruder, GuardPercepts percepts){
        directionChangeNeeded = intruder.getPoint().getClockDirection().getRadians();
    }

    // I used another method for chasing, so currently, this is not used.
    // Method that returns actions which make the guard agent chase the intruder agent
	public GuardAction chaseIntruder(ObjectPercept intruder, GuardPercepts percepts){
        GuardAction action = null;
        if(!chasing){
            Point a = intruder.getPoint();
            Direction b = a.getClockDirection();
            action = new Rotate(Angle.fromRadians(b.getRadians()));
            chasing = true;
            chasing2 = true;
        }else if(chasing2){
            double maxDist = this.getCurrentmaxDist(percepts);
            action = new Move(new Distance(maxDist/2));
            chasing2 = false;
            chasing3 = true;
        }else if(chasing3) {
            Point a = intruder.getPoint();
            Direction b = a.getClockDirection();
            action = new Rotate(Angle.fromRadians(b.getRadians()));
        }else {
            double maxDist = this.getCurrentmaxDist(percepts);
            action = new Move(new Distance(maxDist));
            chasing3 = false;
            chasing = false;
        }
        return action;
    }

    // Resets the coordinates and other properties.
    public void reset(){
        currentX = 0;
        currentY = 0;
        currentAngleInRads = 0;

        // Reset all the parameters which concern the exploration as well.
        how_long_exploring = 0;
        chasingYell = false;
        chasing = false;
        runningFromPher = false;
    }

    // Finds the maximum distance in which can be moved.
    public double getCurrentmaxDist(GuardPercepts percepts) {
        // First, find the maximum distance in which the guard can currently move.
        double currentSlowDownModifier = 1;

        // Check for certain percepts whether they hold or not, adjust the maximum distance accordingly.
        if (percepts.getAreaPercepts().isInDoor()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
        } else if (percepts.getAreaPercepts().isInSentryTower()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
        } else if (percepts.getAreaPercepts().isInWindow()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
        }

        // The maximum distance a guard can currently be moved is found:
        double maximumDistance = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * currentSlowDownModifier;
        return maximumDistance;
    }
}
