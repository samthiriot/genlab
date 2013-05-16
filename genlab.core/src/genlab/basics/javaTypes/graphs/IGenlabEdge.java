package genlab.basics.javaTypes.graphs;

public interface IGenlabEdge {

	public boolean isDirected();
	
	public boolean hasVertex(String vertexId);
	
	public String getFrom();
	public String getTo();
	
	
}
