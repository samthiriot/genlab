package genlab.gephi.utils;

import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.Workspace;

/**
 * Dealing with graphs in gephi is a bit surprising: 
 * we need at the same time a workspace, graph, attributemodel and a graph model.
 * So here it is.
 * 
 * @author Samuel Thiriot
 *
 */
public class GephiGraph {
		
	public final Workspace workspace;
	public final Graph graph;
	public final AttributeModel attributeModel;
	public final GraphModel graphModel;
	
	public GephiGraph(Workspace workspace, Graph graph,
			AttributeModel attributeModel, GraphModel graphModel) {
		this.workspace = workspace;
		this.graph = graph;
		this.attributeModel = attributeModel;
		this.graphModel = graphModel;
	}
	
	

}
