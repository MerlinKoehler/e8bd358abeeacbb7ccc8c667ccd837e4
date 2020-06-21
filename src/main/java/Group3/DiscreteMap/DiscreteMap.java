package Group3.DiscreteMap;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;;

/**
 * A class, that represents a discrete graph map.
 * @author Margarita Naryzhnyaya, Merlin Koehler, Paula Gitu
 *
 */
public class DiscreteMap {

	// A hash map of all vertices in the graph map.
	HashMap<String, Vertice> vertices;
	
	int xMin = 0;
	int xMax = 0;
	int yMin = 0;
	int yMax = 0;
	
	/**
	 * Initialize a new discrete graph map
	 */
	public DiscreteMap() {
		this.vertices = new HashMap<String, Vertice>();
	}

	/**
	 * Gets a vertex based on given coordinates
	 * @param position The coordinates of the vertex.
	 * @return The vertex at the given position.
	 */
	public Vertice getVertice(Integer[] position) {
		String key = position[0] + " " + position[1];
		return vertices.get(key);
	}
	
	/**
	 * Returns all vertices in the map.
	 * @return All vertices.
	 */
	public List<Vertice> getAllVertices() {
		ArrayList<Vertice> verticeList = new ArrayList<Vertice>();
		for(String key: vertices.keySet()) {
			verticeList.add(vertices.get(key));
		}
		return verticeList;
	}

	/**
	 * Add a new vertex object.
	 * @param vertice The vertex to add.
	 */
	public void addVertice(Vertice vertice) {
		checkNewVertice(vertice);
		
		Integer[] xy = vertice.getCoordinate();
		if(xy[0] < xMin) {
			xMin = xy[0];
		}
		if(xy[0] > xMax) {
			xMax = xy[0];
		}
		if(xy[1] < yMin) {
			yMin = xy[1];
		}
		if(xy[1] > yMax) {
			yMax = xy[1];
		}
		
		Integer[] position =  vertice.getCoordinate();
		String key = position[0] + " " + position[1];
		this.vertices.put(key,vertice);
	}
	
	/**
	 * Checks if an vertex exists (based on the coordinates).
	 * @param coordinate The coordinate of the vertex.
	 * @return True, if vertex exists, else false.
	 */
	public boolean verticeExists(Integer[] coordinate) {
		String key = coordinate[0] + " " + coordinate[1];
		return vertices.containsKey(key);
	}
	
	
	/**
	 * Gets the absolute coordinate based on a relative position of a vertex.
	 * @param degrees The angle of the vertex.
	 * @param currentPosition The absolute current position.
	 * @return The absolute coordinate of the vertex.
	 */
	public static Integer[] getCoordinate(int degrees, Integer[] currentPosition) {
		int x = currentPosition[0];
		int y = currentPosition[1];
		
		switch(degrees) {
		case 0:
			return new Integer[] {x, y+1};
		case 45:
			return new Integer[] {x-1, y+1};
		case 90:
			return new Integer[] {x-1, y};
		case 135:
			return new Integer[] {x-1, y-1};
		case 180:
			return new Integer[] {x, y-1};
		case 225:
			return new Integer[] {x+1, y-1};
		case 270:
			return new Integer[] {x+1, y};
		case 315:
			return new Integer[] {x+1, y+1};
		default: 
			return null;
		}
	}
	
	/**
	 * String ASCII representation of the map.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int y = yMax; y >= yMin; y--) {
			for(int x = xMin; x <= xMax; x++) {
				String key = x + " " + y;
				if(vertices.containsKey(key)) {
					Vertice vertice = vertices.get(key);
					switch (vertice.type) {
					case Wall:
						sb.append("W");
						break;
					case None:
						sb.append("_");
						break;
					case SentryTower:
						sb.append("S");
						break;
					case Teleport:
						sb.append("T");
						break;
					case TargetArea:
						sb.append("!");
						break;
					case Unknown:
						sb.append("?");
						break;
					case Door:
						sb.append("D");
						break;
					case Window:
						sb.append("W");
						break;
					default:
						sb.append("O");
					}
				}
				else {
					sb.append("X");
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	/**
	 * String ASCII representation of the map.
	 * @param agentPosition: The agents current position. 
	 */
	public String toString(Integer[] agentPosition) {
		StringBuilder sb = new StringBuilder();
		for(int y = yMax; y >= yMin; y--) {
			for(int x = xMin; x <= xMax; x++) {
				String key = x + " " + y;
				
				if(x == agentPosition[0] && y == agentPosition[1]) {
					sb.append("#");
				}
				else if(vertices.containsKey(key)) {
					Vertice vertice = vertices.get(key);
					switch (vertice.type) {
					case Wall:
						sb.append("W");
						break;
					case None:
						sb.append("_");
						break;
					case SentryTower:
						sb.append("S");
						break;
					case Teleport:
						sb.append("T");
						break;
					case TargetArea:
						sb.append("!");
						break;
					case Door:
						sb.append("D");
						break;
					case Window:
						sb.append("W");
						break;
					case Unknown:
						sb.append("?");
						break;
					default:
						sb.append("O");
					}
				}
				else {
					sb.append("X");
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	/**
	 * Unmark all vertices after a BFS.
	 */
	public void unMark() {
		for(String key: vertices.keySet()) {
			Vertice vertice = vertices.get(key);
			vertice.setMarked(false);
			vertice.setParent(null);
		}
	}
	
	/**
	 * Reset the whole map.
	 */
	public void reset() {
		for(String key: vertices.keySet()) {
			vertices.get(key).setType(ObjectType.None);
		}
	}
	
	/**
	 * Checks and connects new vertices to already existing vertices in the map
	 * @param vertice: The new vertex added to the map.
	 */
	private void checkNewVertice(Vertice vertice) {
		for(int i = 0; i <= 315; i+=45) {
			Integer[] position = getCoordinate(i, vertice.coordinate);
			if(verticeExists(position)) {
				vertice.addEdge(getVertice(position), i);
			}
			
		}
			
	}

	/**
	 * Remove all fields marked as danger.
	 */
	public void removeDanger() {
		for(String key: vertices.keySet()) {
			if(vertices.get(key).getType() == ObjectType.Danger) {
				vertices.get(key).setType(ObjectType.None);
			}
		}
	}
	
	/**
	 * Remove all fields, marked as intruder.
	 */
	public void removeIntruder() {
		for(String key: vertices.keySet()) {
			if(vertices.get(key).getType() == ObjectType.Intruder) {
				vertices.get(key).setType(ObjectType.None);
			}
		}
	}
}
