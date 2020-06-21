package Group3.GridMap;

import Interop.Geometry.Point;
import java.util.ArrayList;

/**
 * A class for a tile of a grid map.
 * @author Janneke van Baden
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

    // Will contain maximally 8 tiles (surrounding eight directions).
    private Grid[] adjacentTo = new Grid[8];


    /**
     * Initialize the tile.
     * @param bottomRight The point at the bottom right of the tile.
     * @param topRight The point at the top right of the tile.
     * @param bottomLeft The point at the bottom left of the tile.
     * @param topLeft The point at the top left of the tile.
     * @param size The size of and edge of the tile.
     * @param type The type of the tile (1: possible to walk through, 2: wall, 3: teleport)
     */
    public Grid(Point bottomRight, Point topRight, Point bottomLeft, Point topLeft, double size, int type) {
        // Only one point needed to create a new tile in the grid
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;

        // Size is set to size, lastSeen is 0 at the start, as you see it now.
        this.size = size;
        this.lastSeen = 0;

        // 1 means 'possible to walk through', 2 means 'wall' and 3 means 'teleport area'
        this.type = type;
    }

    /**
     * Sets this tile's "seen" property to 0.
     */
    public void seen(){
        this.lastSeen = 0;
    }

    /**
     * Increases the "seen" property by 1.
     */
    public void increaseSeen(){
        this.lastSeen = this.lastSeen + 1;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    //

    /**
     * Check whether a point is inside of this tile
     * @param point The point which needs to be checked.
     * @return true if it is inside the tile, else return false.
     */
    public boolean isInsideThisTile(Point point){
        if ((point.getX() <= this.bottomRight.getX()) && (point.getX() >= this.bottomLeft.getX()) && (point.getY() >= this.bottomLeft.getY()) && (point.getY() <= this.topLeft.getY())){
            return true;
        }
        return false;
    }

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

    /**
     * Check whether one tile is adjacent to another, and set it in its list.
     * @param grid The grid to which all these adjacent grids need to be added, and vice versa.
     */
    public void addAdjacent(Grid grid){
        // 0 = top, 1 = top right, 2 = right, 3 = bottom right, 4= bottom, 5 = bottom left, 6 = left, 7 = top left
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

    /**
     * Check which direction a tile is in, if it's adjacent to this tile.
     * @param grid The tile the direction (compared to this tile) is needed for.
     * @return If top=> 0, top right=> 1, right=> 2, bottom right=> 3 bottom=> 4, bottom left=> 5, left=> 6, top left=> 7, not adjacent=> -1
     */
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

    public double getSize(){
        return this.size;
    }
}
