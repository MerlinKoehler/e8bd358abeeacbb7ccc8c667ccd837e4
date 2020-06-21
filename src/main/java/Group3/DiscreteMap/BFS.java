package Group3.DiscreteMap;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for performing a breadth first search on a graph map.
 * @author Margarita Naryzhnyaya, Merlin Koehler, Paula Gitu
 *
 */

public class BFS {
	
	/**
	 * Find the nearest not fully explored area.
	 * @param start The starting position (current position of the agent)
	 * @return A path of vertices from the starting position to the final position.
	 */
	public static Stack<Vertice> findNonCompleteVertice(Vertice start){
		Queue<Vertice> queue = new LinkedList<Vertice>();
		start.marked = true;
		queue.offer(start);
		while(queue.size() != 0) {
			Vertice v = queue.poll();
			if(v.getEdges().size() < 8) {
				Stack<Vertice> s = new Stack<Vertice>();
				while(v != null) {
					s.push(v);
					v = v.getParent();
				}
				return s;
			}
			for(DirectedEdge edge : v.getEdges()) {
				if(!edge.endVertice.isMarked()) {
					Vertice w = edge.endVertice;
					if(checkVertice(w)) {
						w.setMarked(true);
						w.setParent(v);
						queue.offer(w);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks if a given vertex is at least 2 vertices away from a teleport or wall. Necessary for path finding.
	 * @param v the vertex to check
	 * @return true if vertex is at least 2 vertices away from a teleport or wall.
	 */
	public static boolean checkVertice(Vertice v) {
		if((v.getType() != ObjectType.Wall) && (v.getType() != ObjectType.Teleport)) {
			for(DirectedEdge e : v.getEdges()) {
				Vertice w = e.getEndVertice();
				if(!((w.getType() != ObjectType.Wall) && (w.getType() != ObjectType.Teleport))) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Checks if a given vertex is at least 2 vertices away from a wall or a danger zone. Necessary for path finding.
	 * @param v the vertex to check
	 * @return true if vertex is at least 2 vertices away from a wall or a danger zone.
	 */
	public static boolean checkVertice2(Vertice v) {
		if((v.getType() != ObjectType.Wall) && (v.getType() != ObjectType.Danger)) {
			for(DirectedEdge e : v.getEdges()) {
				Vertice w = e.getEndVertice();
				if((w.getType() == ObjectType.Wall) || (w.getType() == ObjectType.Danger)) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Find a path from a starting position to the nearest vertex of a certain object type.
	 * @param start: The starting position (current position of the agent)
	 * @param type: the object type to search.
	 * @return A path of vertices from the starting position to the final position.
	 */
	public static Stack<Vertice> findPath(Vertice start, ObjectType type){
		Queue<Vertice> queue = new LinkedList<Vertice>();
		start.marked = true;
		queue.offer(start);
		while(queue.size() != 0) {
			Vertice v = queue.poll();
			if(v.getType() == type) {
				Stack<Vertice> s = new Stack<Vertice>();
				while(v != null) {
					s.push(v);
					v = v.getParent();
				}
				return s;
			}
			for(DirectedEdge edge : v.getEdges()) {
				if(!edge.endVertice.isMarked()) {
					Vertice w = edge.endVertice;
					if(checkVertice2(w)) {
						w.setMarked(true);
						w.setParent(v);
						queue.offer(w);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of all reachable vertices from the current position.
	 * @param start The starting position.
	 * @return A list of all reachable vertices and their shortest path.
	 */
	public static List<Vertice> getReachableVertices(Vertice start){
		ArrayList<Vertice> vertices = new ArrayList<Vertice>();
		
		Queue<Vertice> queue = new LinkedList<Vertice>();
		start.marked = true;
		queue.offer(start);
		while(queue.size() != 0) {
			Vertice v = queue.poll();
			for(DirectedEdge edge : v.getEdges()) {
				if(!edge.endVertice.isMarked()) {
					Vertice w = edge.endVertice;
					if(checkVertice2(w)) {
						w.setMarked(true);
						vertices.add(w);
						queue.offer(w);
					}
				}
			}
		}
		
		return vertices;
	}

	/**
	 * Find a path from a starting position to a specific vertex.
	 * @param start: The starting position (current position of the agent)
	 * @param end: The target vertex.
	 * @return A path of vertices from the starting position to the final position.
	 */
	public static Stack<Vertice> findPath(Vertice start, Vertice end){
		Queue<Vertice> queue = new LinkedList<Vertice>();
		start.marked = true;
		queue.offer(start);
		while(queue.size() != 0) {
			Vertice v = queue.poll();
			if(v == end) {
				Stack<Vertice> s = new Stack<Vertice>();
				while(v != null) {
					s.push(v);
					v = v.getParent();
				}
				return s;
			}
			for(DirectedEdge edge : v.getEdges()) {
				if(!edge.endVertice.isMarked()) {
					Vertice w = edge.endVertice;
					if(checkVertice2(w)) {
						w.setMarked(true);
						w.setParent(v);
						queue.offer(w);
					}
				}
			}
		}
		return null;
	}
}