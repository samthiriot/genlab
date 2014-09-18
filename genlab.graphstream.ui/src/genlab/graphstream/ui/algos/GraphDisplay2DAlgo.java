package genlab.graphstream.ui.algos;

import genlab.graphstream.ui.views.GraphView2D;

/**
 * The algorithm that takes a graph as input
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphDisplay2DAlgo extends AbstractGraphDisplayAlgo {


	
	public GraphDisplay2DAlgo() {
		super(
				"2D graph display (graphstream)", 
				"displays a graph in a two dimensionnal space" 
				);
		
	}

	@Override
	protected String getViewId() {
		return GraphView2D.VIEW_ID;
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// add a penalty, because the 2d display is less intuitive than the non-2d with auto layout
		return super.getPriorityForIntuitiveCreation()+10;
	}
	
	

	
}
