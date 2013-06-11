package genlab.core.model.meta.basics.graphs;

public interface IGenlabEdge {

	public boolean isDirected();
	
	public boolean hasVertex(String vertexId);
	
	public String getFrom();
	public String getTo();
	
	
}
