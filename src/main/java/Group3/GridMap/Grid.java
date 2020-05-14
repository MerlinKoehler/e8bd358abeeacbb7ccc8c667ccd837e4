package Group3.GridMap;

import Interop.Geometry.Point;
import java.util.ArrayList;

/*
    Points are all based on the agent.
 */

public class Grid {
    // Sides of the tiles.
    private double size;
    private int type;

    // The four corner points of the tile.
    private Point topLeft;
    private Point topRight;
    private Point bottomLeft;
    private Point bottomRight;

    //keeps track of when the last time was when it was seen
    private int lastSeen;

    // Will contain maximally 4 tiles (surrounding eight directions).
    //
    private Grid[] adjacentTo = new Grid[8];

    // make keys in case it's a teleport (0 means it is not a teleport or that it hasn't found it yet)
    private int teleport_to = 0;

    public Grid(Point bottomRight, Point topRight, Point bottomLeft, Point topLeft, double size, int type) {
        // Only one point needed to create a new tile in the grid
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;

        this.size = size;
        this.lastSeen = 0;

        // 1 means 'possible to walk through', 2 means 'wall' and 3 means 'teleport area'
        this.type = type;
    }

    public void seen(){
        this.lastSeen = 0;
    }

    public void increaseSeen(){
        this.lastSeen = this.lastSeen + 1;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public boolean isInsideThisTile(Point point){
        if ((point.getX() <= this.bottomRight.getX()) && (point.getX() >= this.bottomLeft.getX()) && (point.getY() >= this.bottomLeft.getY()) && (point.getY() <= this.topLeft.getY())){
            return true;
        }
        return false;
    }

    // Get all different points in this grid.
    public Point getBottomLeft() {
        return bottomLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getTopRight() {
        return topRight;
    }

    // For the adjacent methods.
    // 0 = top, 1 = top right, 2 = right, 3 = bottom right, 4= bottom, 5 = bottom left, 6 = left, 7 = top left
    public void addAdjacent(Grid grid){
        // top
        if (grid.getBottomLeft().getX() == this.topLeft.getX() && grid.getBottomLeft().getY() == this.topLeft.getY() && grid.getBottomRight().getX() == this.topRight.getX() && grid.getBottomRight().getY() == this.topRight.getY() ){
            adjacentTo[0] = grid;
            grid.getAdjacentTo()[4] = this;
        }
        // top right
        else if (grid.getBottomLeft().getX() == this.getTopRight().getX() && grid.getBottomLeft().getY() == this.getTopRight().getY()){
            adjacentTo[1] = grid;
            grid.getAdjacentTo()[5] = this;
        }
        //right
        else if(grid.getTopLeft().getX() == this.topRight.getX() && grid.getTopLeft().getY() == this.topRight.getY() && grid.getBottomLeft().getX() == this.getBottomRight().getX() && grid.getBottomLeft().getY() == this.getBottomRight().getY()){
            adjacentTo[2] = grid;
            grid.getAdjacentTo()[6] = this;
        }
        //bottom right
        else if(grid.getTopLeft().getX() == this.getBottomRight().getX() && grid.getTopLeft().getY() == this.getBottomRight().getY()){
            adjacentTo[3] = grid;
            grid.getAdjacentTo()[7] = this;
        }
        //bottom
        else if(grid.getTopLeft().getX() == this.getBottomLeft().getX() && grid.getTopLeft().getY() == this.getBottomLeft().getY() && grid.getTopRight().getX() == this.getBottomRight().getX() && grid.getTopRight().getY() == this.getBottomRight().getY()){
            adjacentTo[4] = grid;
            grid.getAdjacentTo()[0] = this;
        }
        //bottom left
        else if(grid.getTopRight().getX() == this.getBottomLeft().getX() && grid.getTopRight().getY() == this.getBottomLeft().getY()){
            adjacentTo[5] = grid;
            grid.getAdjacentTo()[1] = this;
        }
        //left
        else if (grid.getTopRight().getX() == this.topLeft.getX() && this.topLeft.getY() == getTopRight().getY() && this.getBottomLeft().getX() == grid.getBottomRight().getX() && this.getBottomLeft().getY() == grid.getBottomRight().getY()){
            adjacentTo[6] = grid;
            grid.getAdjacentTo()[2] = this;
        }
        //top left
        else if(grid.getBottomRight().getX() == this.topLeft.getX() && grid.getBottomRight().getY() == this.topLeft.getY()){
            adjacentTo[7] = grid;
            grid.getAdjacentTo()[3] = this;
        }
    }

    public Grid[] getAdjacentTo() {
        return adjacentTo;
    }

    // returns the direction
    public int isAdjacent(Grid grid){
        if (grid == adjacentTo[0]){
            return 0;
        }
        else if (grid == adjacentTo[1]){
            return 1;
        }
        else if (grid == adjacentTo[2]){
            return 2;
        }
        else if (grid == adjacentTo[3]){
            return 3;
        }
        else if (grid == adjacentTo[4]){
            return 4;
        }
        else if (grid == adjacentTo[5]){
            return 5;
        }
        else if (grid == adjacentTo[6]){
            return 6;
        }
        else if (grid == adjacentTo[7]){
            return 7;
        }
        else{
            return -1;
        }
    }

    public void setType(int type){this.type = type;}

    public int getType(){
        return type;
    }

    // In case it is a teleport area - not implemented yet
    public void setTeleport_to(int teleport_to) {
        this.teleport_to = teleport_to;
    }

    public int getTeleport_to(){
        return teleport_to;
    }
}
