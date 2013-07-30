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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id).append(": ");
		sb.append(node1id).append("->").append(node2id);
		return sb.toString();
	}
}
