package Group3.DiscreteMap;

public class DirectedEdge {
	Vertice startVertice;
	Vertice endVertice;
	int degrees;
	public DirectedEdge(Vertice startVertice, Vertice endVertice, int degrees) {
		this.startVertice = startVertice;
		this.endVertice = endVertice;
		this.degrees = degrees;
	}
	public int getDegrees() {
		return degrees;
	}
}
