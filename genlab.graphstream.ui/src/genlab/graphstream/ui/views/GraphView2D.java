package genlab.graphstream.ui.views;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * Displays a graph 
 * 
 * @see http://www.eclipse.org/articles/Article-Swing-SWT-Integration/index.html
 * @author Samuel Thiriot
 *
 */
public class GraphView2D extends AbstractGraphView {

	public static final String VIEW_ID = "genlab.graphstream.ui.views.graphview2d";

	@Override
	protected void configureGraph(Graph gsGraph) {
		
		super.configureGraph(gsGraph);
		
	}
	
	@Override
	protected void startViewer(Viewer gsViewer) {
		super.startViewer(gsViewer);
		//gsViewer.disableAutoLayout();
	}
	

}
