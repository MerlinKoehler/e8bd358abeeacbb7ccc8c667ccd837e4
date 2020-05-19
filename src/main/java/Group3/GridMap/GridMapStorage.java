package Group3.GridMap;

import Group3.MainControl;
import Group3.GridMap.Grid;
import Interop.Action.Sprint;
import Interop.Geometry.*;
import java.util.ArrayList;

// This is a discrete representation of this map.
// Grid get added to it when they are currently not in the grid map and the intruder passes through it, or sees it.
// Some of these grids might be partially out of the map, if the gridsize is big

public class GridMapStorage {

    //Set the arraylist, containing separate grids, the size of one, and which one you're currently in
    private ArrayList<Grid> grid;
    private double size;
    private Grid current; // which box you're in
    // Again, create grids with: bottom right, top right, bottom left, top right

    // Normally, without teleporting, so at the start of the program
    public GridMapStorage(double size){
        grid = new ArrayList<Grid>();
        int type = 1; //normally
        this.size = size;

        Grid tile = new Grid(new Point((this.size/2.0) ,-(this.size/2.0)), new Point((this.size/2.0), (this.size/2.0)), new Point((-this.size/2.0), -(this.size/2.0)), new Point((-this.size/2.0), (this.size/2.0)), size, type);
        //System.out.println("initial tile " + tile.getBottomLeft() + " " + tile.getTopRight());

        current = tile;
        // The tile where the agent starts.
        grid.add(tile);
    }

    //If you just teleported, create a new one
    public GridMapStorage(double size, int teleportNr){
        grid = new ArrayList<Grid>();
        int type = 3;
        this.size = size;

        Grid tile = new Grid(new Point((this.size/2) ,-(this.size/2)), new Point((this.size/2), (this.size/2)), new Point((-this.size/2), -(this.size/2)), new Point((-this.size/2), (this.size/2)), size, type);
        tile.setTeleport_to(teleportNr);
        //System.out.println("teleport tile " + tile.getBottomLeft() + " " + tile.getTopRight());

        current = tile;
        // The tile where the agent starts.
        grid.add(tile);
    }

    // Finds the grid the agent is currently in
    public Grid findCurrentTile(Point current){
        for (int i = 0; i < grid.size(); i++){
            if (grid.get(i).isInsideThisTile(current)){
                return grid.get(i);
            }
        }
        return null;
    }

    // Automatically updates the Grid.
    // Between two points, adds every grid
    public void updateGrid(Point lastPosition, Point newPosition, int type) { // type will be of the last tile
        // create grids for every place we walk through (adjacent to the ones which already exist)
        current = findCurrentTile(lastPosition); //should already be in there

        double currentX = lastPosition.getX();
        double currentY = lastPosition.getY();

        // Could be set to different "step sizes", which is what the 5 signifies
        double distance = (new Interop.Geometry.Distance(lastPosition, newPosition)).getValue();
        double scalingFactor = distance * 2;
        int integerFactor = (int) scalingFactor;

        if (current != null) {
            // Loop through the whole line with small steps.
            for (int i = 0; i < (int) integerFactor; i++) {
                currentX = currentX + (newPosition.getX() - lastPosition.getX()) / scalingFactor;
                currentY = currentY + (newPosition.getY() - lastPosition.getY()) / scalingFactor;

                Grid temp = this.findCurrentTile(new Point(currentX, currentY));
                if (temp == null) {
                    temp = addTile(currentX, currentY, current, 1);

                    // Update adjacency list
                    /*for (int j = 0; j < this.getGrid().size(); j++) {
                        temp.addAdjacent(this.getGrid().get(j));
                    }*/

                    current = temp;
                } else {
                    current = temp;
                    temp.seen();
                }
            }


            // Then, set it in the right position.
            // Also, at the end of this (so eg when fov is used), set whether it's a wall or teleportation.
            if (scalingFactor > integerFactor) {
                currentX = currentX + (newPosition.getX() - lastPosition.getX()) / scalingFactor;
                currentY = currentY + (newPosition.getY() - lastPosition.getY()) / scalingFactor;

                Grid temp = this.findCurrentTile(new Point(currentX, currentY));
                if (temp == null) {
                    // here we can see, the type changes, according to what the last grid's type was supposed to be
                    temp = addTile(currentX, currentY, current, type);
                    //System.out.println("currentX: " + currentX + " " + " currentY: " + currentY);

                    // Update adjacency list
                    for (int j = 0; j < this.getGrid().size(); j++) {
                        temp.addAdjacent(this.getGrid().get(j));
                    }
                } else {
                    if (temp.getType() == 1) {
                        temp.setType(type);
                    }
                    temp.seen();
                }
            }
        }
    }

    // Add a tile
    // This implementation is based on the old grid, and the point it is in now
    // Checks for the direction compared to the old grid
    private Grid addTile(double currentX, double currentY, Grid current, int type) {
        Grid temp = null;

        // Now, add a new tile
        // top
        if (currentX >= this.current.getTopLeft().getX() && currentX <= this.current.getTopRight().getX() && currentY >= this.current.getTopLeft().getY()){
            temp = new Grid(this.current.getTopRight(), new Point(this.current.getTopRight().getX(), size + this.current.getTopRight().getY())
                        , this.current.getTopLeft(), new Point(this.current.getTopLeft().getX(), this.current.getTopLeft().getY() + size), this.size, type);
        }

        // top right
        else if (currentX >= this.current.getTopRight().getX() && currentY > this.current.getTopRight().getY()){
            temp = new Grid(new Point(this.current.getTopRight().getX() + size, this.current.getTopRight().getY()), new Point(this.current.getTopRight().getX()+size, this.current.getTopRight().getY() + this.size)
            , this.current.getTopRight(), new Point(this.current.getTopRight().getX(), this.current.getTopRight().getY() + size), this.size, type);
        }

        // right
        else if (currentX >= this.current.getTopRight().getX() && currentY >= this.current.getBottomRight().getY() && currentY <= this.current.getTopRight().getY()){
           temp = new Grid(new Point(this.current.getBottomRight().getX() + size, this.current.getBottomRight().getY()), new Point (this.current.getTopRight().getX() + size, this.current.getTopRight().getY())
                   , this.current.getBottomRight(), this.current.getTopRight(), this.size, type);
        }

        // bottom right
        else if (this.current.getBottomRight().getX() <= currentX && currentY <= this.current.getBottomRight().getY()){
            temp = new Grid(new Point(this.current.getBottomRight().getX() + size, this.current.getBottomRight().getY() - size), new Point(this.current.getBottomRight().getX() + size, this.current.getBottomRight().getY()),
                    new Point(this.current.getBottomRight().getX(), this.current.getBottomRight().getY() - size), this.current.getBottomRight(), this.size, type);
        }

        // bottom
        else if (currentX >= this.current.getBottomLeft().getX() && currentX <= this.current.getBottomRight().getX() && currentY<= this.current.getBottomLeft().getY()){
                temp = new Grid(new Point(this.current.getBottomRight().getX(), this.current.getBottomRight().getY() - size), this.current.getBottomRight(),
                        new Point(this.current.getBottomLeft().getX(), this.current.getBottomLeft().getY() - size) ,this.current.getBottomLeft() ,this.size, type);
        }

        // bottom left
        else if(currentX <= this.current.getBottomLeft().getX() && this.current.getBottomLeft().getY() >= currentY ){
            temp = new Grid(new Point(this.current.getBottomLeft().getX(), this.current.getBottomLeft().getY() - size), this.current.getBottomLeft(),
                    new Point(this.current.getBottomLeft().getX() - size, this.current.getBottomLeft().getY() - size), new Point(this.current.getBottomLeft().getX() - size, this.current.getBottomLeft().getY()), this.size, type);
        }

        // left
        else if(currentX <= this.current.getBottomLeft().getX() && currentY >= this.current.getBottomLeft().getY() && currentY <= this.current.getTopLeft().getY()){
            temp = new Grid(this.current.getBottomLeft(), this.current.getTopLeft(),
                    new Point(this.current.getBottomLeft().getX() - size, this.current.getBottomLeft().getY()), new Point(this.current.getTopLeft().getX() - size, this.current.getTopLeft().getY()), this.size, type);
        }

        // top left
        else if (currentX <= this.current.getTopLeft().getX() && currentY>= this.current.getTopLeft().getY()){
            temp = new Grid(this.current.getTopLeft(), new Point(this.current.getTopLeft().getX(), this.current.getTopLeft().getY() + size),
                    new Point(this.current.getTopLeft().getX() - size, this.current.getTopLeft().getY()), new Point(this.current.getTopLeft().getX() - size, this.current.getTopLeft().getY() + size), this.size, type);
        }

        for (int i = 0; i < this.getGrid().size(); i++){
            temp.addAdjacent(grid.get(i));
            grid.get(i).addAdjacent(temp);
        }

        // Add it to the grid
        //System.out.println("Added a grid. " + temp.getBottomLeft() + " " + temp.getTopRight());
        grid.add(temp);
        return temp;
    }

    public ArrayList<Grid> getGrid() {
        return grid;
    }

    // Add 1 to every grid tile - keeps track of when a certain tile was seen.
    public void updateSeen() {
        for (int i = 0; i < grid.size(); i++){
            grid.get(i).increaseSeen();
        }
    }
}
