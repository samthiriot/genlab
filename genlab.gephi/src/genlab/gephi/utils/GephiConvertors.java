package genlab.gephi.utils;

import genlab.core.commons.ProgramException;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class GephiConvertors {

	/**
	 * A static locker that avoids collisions at the project/workspace level
	 */
	public static Object gephiStaticLocker = new Object();
	
	/**
	 * Project controller is required too
	 */
	private static ProjectController gephiProjectController = null;
	
	public static final String FIELD_EDGE_ID = "id" ;


	public static ProjectController getGephiProjectController () {
		
		synchronized (gephiStaticLocker) {
			if (gephiProjectController == null) {
				gephiProjectController = Lookup.getDefault().lookup(ProjectController.class);
				gephiProjectController.startup();
			}
			return gephiProjectController;
		}
		
		
	}
	
	public static GraphModel getGraphModel(Workspace workspace) {
		synchronized (gephiStaticLocker) {
			return Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
		}
	}
	

	public static AttributeModel getAttributeModel(Workspace workspace) {
		synchronized (gephiStaticLocker) {
			return Lookup.getDefault().lookup(AttributeController.class).getModel(workspace);
		}
		
	}
	
	
	
	/**
	 * Converts a genlab graph to a gephi one
	 * @param gamaGraph
	 * @return
	 */
	public static GephiGraph loadIntoAGephiWorkspace(
			IGenlabGraph glGraph, 
			ListOfMessages messages,
			boolean copyGraphAttributes, 
			boolean copyNodeAttributes, 
			boolean copyEdgeAttributes
			) {
		
		// TODO is this true ?
		if (glGraph.isMultiGraph())
			messages.warnUser(
					"the conversion of a multiplex graph to Gephi will not take into account the redondant links", 
					GephiConvertors.class
					);
		
		Project gephiCurrentProject = null;
		Workspace gephiCurrentWorkspace = null;
		GraphModel graphModel ;
		AttributeModel attributeModel;
		
		// init gephi project
		synchronized (gephiStaticLocker) {
			
			ProjectController pc = getGephiProjectController();
			pc.newProject();
			gephiCurrentProject = pc.getCurrentProject();

			if (gephiCurrentProject == null)
				throw new ProgramException("unable to init a novel Gephi project, sorry.");
			
			gephiCurrentWorkspace = pc.newWorkspace(gephiCurrentProject);

			if (gephiCurrentWorkspace == null)
				throw new ProgramException("unable to init a novel Gephi workspace, sorry.");
			
		}
		
		// init attributes
		synchronized (gephiStaticLocker) {
			graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(gephiCurrentWorkspace);
			attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(gephiCurrentWorkspace);
		}
         
		// configure the resulting graph
		Graph gephiGraph = null;
		switch (glGraph.getDirectionality()) {
		case DIRECTED:
			gephiGraph = graphModel.getDirectedGraph();
			break;
		case UNDIRECTED:
			gephiGraph = graphModel.getUndirectedGraph();
			break;
		case MIXED:
			gephiGraph = graphModel.getMixedGraph();
			break;
		default:
			throw new ProgramException("unknown directionnality constant "+glGraph.getDirectionality());
		}
		
		
		// TODO attrivutes !
		
		/*
		// ... explore the gama graph vertices, and create columns for their attribute
		Map<Object,AttributeColumn> vertexGamaId2gephiColumn = new java.util.HashMap<Object, AttributeColumn>();
		for (Object gamaV : gamaGraph._internalVertexMap().keySet() ) {
			_Vertex gamaVertex = (_Vertex)gamaGraph._internalVertexMap().get(gamaV);
			
			if (gamaV instanceof IAgent) {
				IAgent gamaAgent = (IAgent)gamaV;
				for (Object gamaAttributeName : gamaAgent.getAttributes().keySet()) {
					AttributeColumn gephiColumn = vertexGamaId2gephiColumn.get(gamaAttributeName.toString());
					if (gephiColumn == null) {
				        gephiColumn = attributeModel.getNodeTable().addColumn(gamaAttributeName.toString(), AttributeType.STRING);
				        // TODO everything is string ???
				        vertexGamaId2gephiColumn.put(gamaAttributeName.toString(),gephiColumn);
					}
				}
				
			} else {
				AttributeColumn gephiColumn = vertexGamaId2gephiColumn.get(FIELD_VALUE);
				if (gephiColumn == null) {
			        gephiColumn = attributeModel.getNodeTable().addColumn(FIELD_VALUE, AttributeType.STRING);
			        vertexGamaId2gephiColumn.put(FIELD_VALUE,gephiColumn);
				}
				
			}

		}
		
		// ...configure edges columns
		// ... always add an edge id (that would raise errors during reading in some formats
        // not required: does already exists implicitely : 
        // attributeModel.getEdgeTable().addColumn(FIELD_EDGE_ID, AttributeType.STRING);


        // ... and the attributes of agents in edges
		Map<Object,AttributeColumn> edgeGamaId2gephiColumn = new java.util.HashMap<Object, AttributeColumn>();
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			if (edgeObj instanceof IAgent) {
				IAgent gamaAgent = (IAgent)edgeObj;
				for (Object gamaAttributeName : gamaAgent.getAttributes().keySet()) {
					AttributeColumn gephiColumn = edgeGamaId2gephiColumn.get(gamaAttributeName.toString());
					if (gephiColumn == null) {
				        gephiColumn = attributeModel.getEdgeTable().addColumn(gamaAttributeName.toString(), AttributeType.STRING);
				        // TODO everything is string ???
				        edgeGamaId2gephiColumn.put(gamaAttributeName.toString(),gephiColumn);
					}
				}
			} else {
				AttributeColumn gephiColumn = edgeGamaId2gephiColumn.get(FIELD_VALUE);
				if (gephiColumn == null) {
			        gephiColumn = attributeModel.getEdgeTable().addColumn(FIELD_VALUE, AttributeType.STRING);
			        edgeGamaId2gephiColumn.put(FIELD_VALUE,gephiColumn);
				}
				
			}
			
		}
		*/
		
		// now add nodes
		for (String nameId : glGraph.getVertices() ) {
						
			// create node
	        Node gephiNode = graphModel.factory().newNode(nameId);
	        
	        // set node properties
	        gephiNode.getNodeData().setLabel(nameId);
	        
			// actually add the node to the network
			gephiGraph.addNode(gephiNode);
	
			// TODO copy attributes !

		}
		
		// add edges
		for (String edgeId : glGraph.getEdges()) {
			
			Edge createdEdge = graphModel.factory().newEdge(
					gephiGraph.getNode(glGraph.getEdgeVertexFrom(edgeId)),
					gephiGraph.getNode(glGraph.getEdgeVertexTo(edgeId))
					);
			
			// always add an id for the edge (required for some formats)
	        createdEdge.getAttributes().setValue(FIELD_EDGE_ID, edgeId);
			
	        // TODO attributes
	        
			// actually add the edge to the network
			if (!gephiGraph.addEdge(createdEdge)) {
				messages.warnUser(
						"an edge was ignored, probably because if was added twice: "+edgeId, 
						GephiConvertors.class
						);
				continue;
			}

		}
       
		
        return new GephiGraph(gephiCurrentWorkspace, gephiGraph, attributeModel, graphModel, gephiCurrentProject);
	}
	
	public static void clearGraph(GephiGraph graph) {

		// we know this is not OK; there are plenty of memory leaks and threads;
		// we wait for the next version
		//GLLogger.debugTech("freeing a gephi graph", GephiConvertors.class);
		try {
			synchronized (gephiStaticLocker) {
				
				ProjectController pc = getGephiProjectController();
				pc.removeProject(graph.project);
			
				//pc.cleanWorkspace(graph.workspace);
	
				//graph.graph.clear();
				//graph.graphModel.clear();
				
			}
		} catch (RuntimeException e) {
			GLLogger.warnTech("error while freeing a gephi graph: "+e.getMessage(), GephiConvertors.class, e);
		}
	}
	
	private GephiConvertors() {
		
	}

}
