package genlab.gephi.utils;

import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.Project;
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
	public final GraphModel graphModel;
	public final AttributeModel attributeModel;
	public final Project project;
	
	public GephiGraph(Workspace workspace, Graph graph,
			GraphModel graphModel, AttributeModel attributeModel, Project project) {
		this.workspace = workspace;
		this.graph = graph;
		this.graphModel = graphModel;
		this.attributeModel = attributeModel;
		this.project = project;
	}
	
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("workspace: ").append(workspace);
		sb.append(", graph: ").append(graph);
		sb.append(", graph mmodel: ").append(graphModel);
		
		return sb.toString();
	}
	

}
