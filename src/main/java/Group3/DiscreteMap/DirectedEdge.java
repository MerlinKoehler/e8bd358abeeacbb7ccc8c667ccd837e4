package Group3.DiscreteMap;

/**
 * A class that represents an directed edge.
 * @author Margarita Naryzhnyaya, Merlin Köhler, Paula Gitu
 *
 */
public class DirectedEdge {
	Vertice startVertice;
	Vertice endVertice;
	int degrees;
	
	/**
	 * Initializes a new directed edge
	 * @param startVertice The start vertex
	 * @param endVertice The end vertex
	 * @param degrees The absolute number of degrees from the start to the end vertex.
	 */
	public DirectedEdge(Vertice startVertice, Vertice endVertice, int degrees) {
		this.startVertice = startVertice;
		this.endVertice = endVertice;
		this.degrees = degrees;
	}
	public int getDegrees() {
		return degrees;
	}
	public Vertice getStartVertice() {
		return startVertice;
	}
	public void setStartVertice(Vertice startVertice) {
		this.startVertice = startVertice;
	}
	public Vertice getEndVertice() {
		return endVertice;
	}
	public void setEndVertice(Vertice endVertice) {
		this.endVertice = endVertice;
	}
}
