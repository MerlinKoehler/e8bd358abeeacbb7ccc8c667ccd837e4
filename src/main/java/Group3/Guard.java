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

    /* Start in the middle of the map, facing DOWN (0 degrees) */
    private double currentX = 0;
    private double currentY = 0;
    private double currentAngleInRads = 0;
    private int exploringType = 1; // 1 is advanced random, other is tile to tile
    private boolean yellsAndPheromones = false;

    /* GRID MAP SETTINGS */
    private double gridSize = 10;
    private GridMapStorage currentMap = new GridMapStorage(gridSize, false);

    private Random random = new Random(); // used for random rotations and movement

    /* When it is chased, keep track of this to decide on an action */
    private int lastSeenIntruder = Integer.MAX_VALUE;
    private boolean chasing = false;
    boolean chasing2 = false;
    boolean chasing3 = false;

    /* For the exploration */
    private boolean randomly_exploring = true;

    // If not, then retrace a bit in the map.
    private int how_long_exploring = 0;
    private final int maxExplorationTime = 100;
    private double targetDirection = -1;

    // For chasing the yell.
    private double directionChangeNeeded = 0;
    private boolean chasingYell = false;

    // Second searching method.
    private Point explorationTarget = null;
    private int rotateInPlace = 8;

    public double getCurrentmaxDist(GuardPercepts percepts) {
        // First, find the maximum distance in which the guard can currently move.
        double currentSlowDownModifier = 1;

        // Check for certain percepts whether they hold or not.
        if (percepts.getAreaPercepts().isInDoor()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
        } else if (percepts.getAreaPercepts().isInSentryTower()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
        } else if (percepts.getAreaPercepts().isInWindow()) {
            currentSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
        }

        // The maximum distance a guard can currently be moves is found:
        double maximumDistance = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * currentSlowDownModifier;
        return maximumDistance;
    }

    //General structure guard - finds action
    public GuardAction getAction(GuardPercepts percepts) {
        // In case it has just teleported; reset the internal system.
        if (percepts.getAreaPercepts().isJustTeleported()) {
            currentX = 0;
            currentY = 0;
            currentAngleInRads = 0;

            // Needed for advanced random exploration
            how_long_exploring = 0;
            randomly_exploring = true;
        }

        // The maximum distance a guard can currently be moves is found:
        double maximumDistance = this.getCurrentmaxDist(percepts);

        //--------------------------------------------------------------------------------------------------------------
        // Update the map, if the action was performed
        if (lastAction != null && percepts.wasLastActionExecuted()) {
            updateInternalMap(percepts); // will also update the agent's current  state
        }

        // First, check whether a intruder is seen at the moment - overrides other actions.
        Object[] vision = percepts.getVision().getObjects().getAll().toArray();
        for (int i = 0; i < vision.length; i++) {
            if (((ObjectPercept) vision[i]).getType() == ObjectPerceptType.Intruder) {
                // for testing
                //if (true) {
                //    lastAction = new Yell();
                //   return lastAction;
                //}
                // end of testing statement
                lastAction = chaseIntruder((ObjectPercept) vision[i], percepts);
                return lastAction;
            }
        }

        /*
        // If it is already chasing one of these.
        if (chasingYell){
            if (directionChangeNeeded > 0){
                // rotate
            }
            else{

            }
        }*/

        // Add some rules for yells and pheromones, and how they are handled.
        if (yellsAndPheromones){
            // Yells have the highest priority, these signify an agent has seen an intruder.
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

            // For pheromones, also store the information, in a similar way.
            // Only two types of pheromones are used.
            boolean pheroFound = false;
            ArrayList<SmellPercept> phers1 = new ArrayList<>();
            ArrayList<SmellPercept> phers2 = new ArrayList<>();

            // Pheromones have the second priority Signify various things, depending on pheromone.
            Object[] smells = percepts.getSmells().getAll().toArray();
            for (int i = 0; i < smells.length; i++) {
                pheroFound = true;
                if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone1) {
                    phers1.add((SmellPercept) smells[i]);
                }
                else if (((SmellPercept) smells[i]).getType() == SmellPerceptType.Pheromone2){
                    phers2.add((SmellPercept) smells[i]);
                }
            }

            // In case any of these was found, use this in the choice of action.
            if (yellFound || pheroFound){
                if (yellFound){
                    chasingYell = true;
                }
                lastAction = handleYellAndPheromones(yells, phers1, phers2);
                return lastAction;
            }
        }

        if (exploringType == 1) {
            lastAction = advancedRandomExploring(percepts, maximumDistance);
            return lastAction;
        }

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
            //System.out.println("teleported");
        }

        // ELse, if it simply rotated, update the map according to what the agent can see at the moment.
        else if (lastAction instanceof Interop.Action.Rotate) {
            currentAngleInRads = currentAngleInRads + ((Rotate) lastAction).getAngle().getRadians();
            if (currentAngleInRads > Math.PI * 2) {
                currentAngleInRads = currentAngleInRads % (2 * Math.PI);
            }
            updateMapSight(percepts);
            //System.out.println("rotate");
        }
        // Can check the places in front of it.
        else if (lastAction instanceof Interop.Action.Move) {
            //System.out.println("move");
            // First the starting place.
            double oldX = currentX;
            double oldY = currentY;

            // Update this accordingly
            currentX = oldX + Math.cos(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();
            currentY = oldY + Math.sin(currentAngleInRads) * ((Move) lastAction).getDistance().getValue();

            //System.out.println(" Current x is " + currentX);
            //System.out.println(" Current y is " + currentY);

            currentMap.updateGrid(new Point(oldX, oldY), new Point(currentX, currentY), 1);
            //System.out.println(currentMap.findCurrentTile(new Point(currentX,currentY)).getBottomLeft());
            //System.out.println(currentMap.findCurrentTile(new Point(currentX,currentY)).getTopRight());
            updateMapSight(percepts);
        }
    }

    //Updates the sight of a map (creates grids), given the points of the vision
    public void updateMapSight(GuardPercepts percepts) {
        // The same as before, but use the special properties. (Such as 'wall' and 'teleport')

        Object[] vision = percepts.getVision().getObjects().getAll().toArray();
        for (int i = 0; i < vision.length; i++) {
            Point objectPoint = ((ObjectPercept) vision[i]).getPoint();
            Point currentPoint = new Point(currentX, currentY);
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

    public GuardAction handleYellAndPheromones(ArrayList<SoundPercept> yells, ArrayList<SmellPercept> pher1, ArrayList<SmellPercept> pher2){
            // For yell: go directly towards it- choose the first one to go to (as there is no further information about the yells.)

            // I removed most of this code for now to not mess up our code
            Direction goInto;
            if (yells.size() != 0){
                goInto = yells.get(0).getDirection();
            }



            // Pheromone 1: Signifies that a teleport is nearby-- be more cautious.

            // Pheromone 2: Signifies that another guard is searching through this area- dropped every once in a while
            // So-> turn around and go into another direction.

            // Both pheromone 1 and yell-> walk certain distance to target direction-> stage some actions.

            return null;
    }

    public GuardAction advancedRandomExploring(GuardPercepts percepts, double maximumDistance) {
        if (how_long_exploring <= maxExplorationTime) {
            targetDirection = -1;
            how_long_exploring++;

            //Random exploration at first - simply walking around
            if (!percepts.wasLastActionExecuted() || currentMap.getGrid().isEmpty()) {
                // Rotate randomly in case the exploration is not working (IDK if we'll need it)
                lastAction = new Rotate(Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
                return lastAction;
            } else if (percepts.wasLastActionExecuted()) {
                lastAction = new Move(new Distance(maximumDistance));
                return lastAction;
            }
        }

        // After a certain amount of time, walk in the direction of an area that hasn't been seen for the longest time.
        else if (how_long_exploring > maxExplorationTime) {

            // Firstly, find the direction to go in.
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
                // if it bumps into something
                if (!percepts.wasLastActionExecuted()) {
                    targetDirection = -1;
                    how_long_exploring = 0;
                    randomly_exploring = true;
                    lastAction = new Rotate(Angle.fromDegrees(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble()));
                } else {
                    lastAction = new Move(new Distance(maximumDistance));
                }
            }
        }
        return lastAction;
    }

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
}
