package genlab.graphstream.ui.views;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

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
		
		// specific case of 2D; if there are attributes x and y, but no xy, then define the xy
		
	}
	
	@Override
	protected void startViewer(Viewer gsViewer) {
		super.startViewer(gsViewer);
		//gsViewer.disableAutoLayout();
	}
	

}
