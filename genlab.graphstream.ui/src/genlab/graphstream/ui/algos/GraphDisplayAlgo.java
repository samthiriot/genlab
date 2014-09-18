package genlab.graphstream.ui.algos;

import genlab.graphstream.ui.views.GraphView;

/**
 * The algorithm that takes a graph as input
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphDisplayAlgo extends AbstractGraphDisplayAlgo {

	public GraphDisplayAlgo() {
		super(
				"graph display (graphstream)", 
				"displays a graph on an abstract space" 
				);
		
	}
	
	protected String getViewId() {
		return GraphView.VIEW_ID;
	}
	

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// add a penalty, because the 2d display is less intuitive than the non-2d with auto layout
		return super.getPriorityForIntuitiveCreation()+15;
	}
	

	
}
