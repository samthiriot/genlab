package genlab.graphstream.ui.views;

import org.graphstream.ui.view.Viewer;

import genlab.core.usermachineinteraction.GLLogger;


/**
 * @see http://www.eclipse.org/articles/Article-Swing-SWT-Integration/index.html
 * @author Samuel Thiriot
 *
 */
public class GraphView extends AbstractGraphView {

	public static final String VIEW_ID = "genlab.graphstream.ui.views.graphview";

	
	protected void configureViewer(Viewer gsViewer) {
		super.configureViewer(gsViewer);
	}
	
	@Override
	protected void startViewer(Viewer gsViewer) {
		super.startViewer(gsViewer);
		GLLogger.traceTech("start layout...", getClass());
		gsViewer.enableAutoLayout();
	}
	

	

}
