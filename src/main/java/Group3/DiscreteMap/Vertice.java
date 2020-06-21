package Group3.DiscreteMap;

import java.util.ArrayList;
import Interop.Geometry.Point;

/**
 * A class representing a vertex in a graph map.
 * @author Margarita Naryzhnyaya, Merlin Koehler, Paula Gitu
 *
 */
public class Vertice {
	
	// Object type of the vertex (Wall, Sentry Tower, Teleport...)
	ObjectType type;
	
	// Connected directed edges
	ArrayList<DirectedEdge> edges;
	
	// Center coordinate
	Point center;
	
	// Radius of the vertex
	double radius;
	
	// Discrete coordinate of the vertex
	Integer coordinate[];
	
	// Flag, used for BFS.
	boolean marked = false;
	
	// Parent, used for path planning in BFS. 
	Vertice parent = null;

	/**
	 * Initializes a new vertex.
	 * @param type: Object type of the vertex.
	 * @param center: Center point of the vertex.
	 * @param radius: Radius of the vertex.
	 * @param coordinate: Discrete coordinate of the vertex.
	 */
	public Vertice(ObjectType type, Point center, double radius, Integer[] coordinate) {
		this.center = center;
		this.radius = radius;
		this.type = type;
		this.edges = new ArrayList<DirectedEdge>();
		this.coordinate = coordinate;
	}

	/**
	 * Add an edge from one vertex to another. The reverse edge is automatically added.
	 * @param endVertice: The final vertex. 
	 * @param degrees: The rotation angle from this vertex to the final vertex.
	 */
	public void addEdge(Vertice endVertice, int degrees) {
		DirectedEdge edge = new DirectedEdge(this, endVertice, degrees);
		this.edges.add(edge);
		if(degrees >= 180) {
			degrees = degrees - 180;
		}
		else {
			degrees = degrees + 180;
		}
		DirectedEdge reverseedge = new DirectedEdge(endVertice, this, degrees);
		endVertice.edges.add(reverseedge);
	}
	
	/**
	 * Checks if a continuous point is inside the vertex area.
	 * @param point The point to check.
	 * @return True if its inside the vertex, false otherwise.
	 */
	public boolean isInside(Point point) {
		double xmin = center.getX() - radius;
		double xmax = center.getX() + radius;
		double ymin = center.getY() - radius;
		double ymax = center.getY() + radius;
		double x = point.getX();
		double y = point.getY();
		if((xmin <= x) && (xmax >= x)) {
			if((ymin <= y) && (ymax >= y)) {
				return true;
			}
			else return false;
		}
		else return false;
	}

	// Getters and Setters.
	
	public Point getCenter() {
		return center;
	}

	public Integer[] getCoordinate() {
		return coordinate;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public Vertice getParent() {
		return parent;
	}

	public void setParent(Vertice parent) {
		this.parent = parent;
	}
	
	public String toString() {
		return "[" + this.coordinate[0] + "|" + this.coordinate[2] + "]";
	}
	
	public ObjectType getType() {
		return type;
	}
	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public ArrayList<DirectedEdge> getEdges() {
		return edges;
	}
}
