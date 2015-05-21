package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.draw2d.graph.Subgraph;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;

/**
 * Provides a basic layout of a graphiti graph.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class BasicLayoutDiagramFeature extends AbstractCustomFeature {

	/**
	 * minimal distance between nodes
	 */
	private static final int PADDING = 40;

	public BasicLayoutDiagramFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getDescription() {
		return "Layout diagram with GEF Layouter"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return "&Layout Diagram"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	/**
	 * Removes the bending points from all the connections of the whole diagram
	 */
	private void removeBendings() {
	
		for (Connection c: getDiagram().getConnections()) {
			
			FreeFormConnection cc = null;
			try {
				cc = (FreeFormConnection)c;
			} catch (ClassCastException e) {
				continue;
			}
			
			cc.getBendpoints().clear();
			
		}
	}
	
	@Override
	public void execute(ICustomContext context) {
		
		// remove bendpoints, because they will not be relevant any more
		removeBendings();
		

		int countIterationsDone = 0;
		
		// iterate several times, because the layout does not takes into account the resizing of compounds
		while (true) {
				
			// create the draw2d version of the graph 
			final CompoundDirectedGraph graph = mapDiagramToGraph();
			graph.setDefaultPadding(new Insets(8, 40, 8, 40));
			graph.setDirection(PositionConstants.EAST);
						
			// run layout	
			final CompoundDirectedGraphLayout gefLayout = new CompoundDirectedGraphLayout();
			gefLayout.visit(graph);
		
			// map back the results in the graphiti graph
			if (!mapGraphCoordinatesToDiagram(graph))
				// no more impact of layout, it is done
				break;
			
			countIterationsDone++;
			
			if (countIterationsDone > 4)
				break;
		}
		
	}

	/**
	 * Maps back the gef graph coordinates to the graphiti coordinates
	 * @param graph
	 * @return true if the coordinates changed
	 */
	private boolean mapGraphCoordinatesToDiagram(CompoundDirectedGraph graph) {
		
		boolean somethingChanged = false;
		
		// first manage containers: they have a specific resizing feature
		for (Object object : graph.subgraphs) {
			Node node = (Node) object;
			
			if (!(node.data instanceof Shape)) {
				// skip the weird data added by layout !
				continue; 
			}
			Shape shape = (Shape) node.data;
			shape.getGraphicsAlgorithm().setX(node.x);
			shape.getGraphicsAlgorithm().setY(node.y);
			
			// manage resizing if required
			if (
					(shape.getGraphicsAlgorithm().getWidth() != node.width)
					||
					(shape.getGraphicsAlgorithm().getHeight() != node.height)
					) {
				
				somethingChanged = true;
				
				shape.getGraphicsAlgorithm().setWidth(node.width);
				shape.getGraphicsAlgorithm().setHeight(node.height);
				
				LayoutContext lc = new LayoutContext(shape);
				ILayoutFeature lf = getFeatureProvider().getLayoutFeature(lc);
				if (lf != null)
					lf.execute(lc);
				
			}
		}
		
		for (Object object : graph.nodes) {
			Node node = (Node) object;
			
			if (!(node.data instanceof Shape)) {
				// skip the weird data added by layout !
				continue; 
			}
			
			// maybe there is a delta if there is a parent ? 
			// (draw2d uses absolute coordinates for children while graphiti uses relative ones)
			int deltaX = 0;
			int deltaY = 0;
			
			Shape shape = (Shape) node.data;
			
			{
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(shape);
				IAlgoInstance ai = (IAlgoInstance)bo;
				if (ai.getContainer() != null) {
					// there should be a parent !
					PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(ai.getContainer());
					deltaX = pe.getGraphicsAlgorithm().getX();
					deltaY = pe.getGraphicsAlgorithm().getY();
				}
			}
			
			int targetX = node.x - deltaX;
			int targetY = node.y - deltaY;
			
			if (shape.getGraphicsAlgorithm().getX() != targetX || shape.getGraphicsAlgorithm().getY() != targetY) {
				
				somethingChanged = true;
				
				shape.getGraphicsAlgorithm().setX(targetX);
				shape.getGraphicsAlgorithm().setY(targetY);
				
			}
			
			// manage resizing if required
			if (
					(shape.getGraphicsAlgorithm().getWidth() != node.width)
					||
					(shape.getGraphicsAlgorithm().getHeight() != node.height)
					) {
				
				somethingChanged = true;
				
				shape.getGraphicsAlgorithm().setWidth(node.width);
				shape.getGraphicsAlgorithm().setHeight(node.height);
				
				LayoutContext lc = new LayoutContext(shape);
				ILayoutFeature lf = getFeatureProvider().getLayoutFeature(lc);
				if (lf != null)
					lf.execute(lc);
				
			}
		}
		
		return somethingChanged;

	}


	/**
	 * Takes a graphiti diagram, returns it as a draw2d GEF graph
	 * @return
	 */
	private CompoundDirectedGraph mapDiagramToGraph() {
		

		Map<AnchorContainer, Node> shapeToNode = new HashMap<AnchorContainer, Node>();

		
		Diagram d = getDiagram();
		CompoundDirectedGraph dg = new CompoundDirectedGraph();
		EdgeList edgeList = new EdgeList();
		NodeList nodeList = new NodeList();
		EList<Shape> children = d.getChildren();


		Subgraph top =  new Subgraph("top");
		nodeList.add(top);
		
		LinkedList<Shape> nodesToProcess = new LinkedList<Shape>(children);
		while (!nodesToProcess.isEmpty()) {

			Shape shape = nodesToProcess.removeFirst();
			
			// if possible, this is going to become a node
			Node node = null;
			
			Object bo = getBusinessObjectForPictogramElement(shape);
			
			if (bo instanceof IAlgoContainerInstance) {
				
				Subgraph nodeGraph = null;
				// this is a graphiti container... 
				// ... so it should be translated to a subgraph draw2d
				
				// and we should explore its children ! 
				{
					IAlgoContainerInstance boContainer = (IAlgoContainerInstance)bo;
					for (IAlgoInstance ai : boContainer.getChildren()) {
						nodesToProcess.addLast((Shape) getFeatureProvider().getPictogramElementForBusinessObject(ai));		
					}
				}
				
				// should we find the parent ? 
				if (!(shape.getContainer() instanceof DiagramImpl)) {
					
					// can we find the parent ?
					Subgraph parent = (Subgraph)shapeToNode.get(shape.getContainer());
					if (parent == null) {
						// we did not found it yet;
						// lets keep it in the list of things to process
						nodesToProcess.addLast(shape);
						continue;
					} else {
						// we found the parent
						// we can now create the subgraph
						nodeGraph = new Subgraph(shape, parent);
						
					}
					
						
				} else {
					// no parent to find; let's just create the subgraph :-)
					nodeGraph  = new Subgraph(shape, top);
					
				}
				
				// configure
				//nodeGraph.setPadding(new Insets(PADDING));
				
				// use as a node
				node = nodeGraph ;
				
			} else {
				
				// this is a graphiti node...
				// ... it should be translated to a Node

				// should we find the parent ? 
				if (!(shape.getContainer() instanceof DiagramImpl)) {
					
					// can we find the parent ?
					Subgraph parent = (Subgraph)shapeToNode.get(shape.getContainer());
					if (parent == null) {
						// we did not found it yet;
						// lets keep it in the list of things to process
						nodesToProcess.addLast(shape);
						continue;
					} else {
						// we found the parent
						// we can now create the subgraph
						node = new Node(shape, parent);
						
					}
					
						
				} else {
					// no parent to find; let's just create the subgraph :-)
					node = new Node(shape, top);
					
				}
				
				
			}

			
			// let's configure this node
			GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
			node.x = ga.getX();
			node.y = ga.getY();
			node.width = ga.getWidth();
			node.height = ga.getHeight();
			node.data = (Shape)shape;
			shapeToNode.put(shape, node);
			nodeList.add(node);
			
		}
		
		// now create connections
		
		EList<Connection> connections = d.getConnections();
		for (Connection connection : connections) {
			AnchorContainer source = connection.getStart().getParent();
			AnchorContainer target = connection.getEnd().getParent();
			
			Edge edge = new Edge(shapeToNode.get(source), shapeToNode.get(target));
			edge.data = connection;
			edgeList.add(edge);
		}
		
		dg.nodes = nodeList;
		dg.edges = edgeList;
		
		return dg;
	}
}
