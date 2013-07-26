package genlab.igraph.natjna;

public class IGraphEdge {

	public final int id;
	public final int node1id;
	public final int node2id;
	
	public IGraphEdge(int id, int a, int b) {
		this.id = id;
		this.node1id = a;
		this.node2id = b;
	}

}
