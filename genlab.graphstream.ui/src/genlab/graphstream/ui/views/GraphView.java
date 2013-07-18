package genlab.graphstream.ui.views;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.meta.basics.graphs.AbstractGraphstreamBasedGraph;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JRootPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.Viewer.CloseFramePolicy;

/**
 * @see http://www.eclipse.org/articles/Article-Swing-SWT-Integration/index.html
 * @author Samuel Thiriot
 *
 */
public class GraphView extends AbstractViewOpenedByAlgo {

	public static final String VIEW_ID = "genlab.graphstream.ui.views.graphview";

	private Graph gsGraph = null;
	private Composite parent = null;
	
	private Composite hostSwtComposite = null;
	
	private Frame awtFrame = null;
	private Panel awtPanel = null;
	
	private JRootPane awtRootPane;
	
	private View gsView = null;
	
	private Viewer gsViewer = null;
	
	{
		// reduce flickering for Windows
		System.setProperty("sun.awt.noerasebackground", "true");
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		GLLogger.traceTech("init an AWT frame...", getClass());
		
		hostSwtComposite = new Composite(parent, SWT.EMBEDDED | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND | SWT.TRANSPARENT );  
		hostSwtComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		//hostSwtComposite.setRedraw(true);
		
		// create an AWT frame into the SWT composite
		awtFrame = SWT_AWT.new_Frame(hostSwtComposite);
		//awtFrame.setIgnoreRepaint(true);
		
		// create a panel that does not clears its background into the AWT frame
		awtPanel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				/* Do not erase the background */
				paint(g);
			}
		};
		awtFrame.add(awtPanel);
		
		awtRootPane = new JRootPane(); 
		awtPanel.add(awtRootPane); 


		
	}

	@Override
	public void setFocus() {
		
		/*
		awtRootPane.requestFocus();
		awtRootPane.getContentPane().invalidate();
		awtRootPane.invalidate();
		awtPanel.invalidate();
		awtFrame.invalidate();
		*/
	}
	
	public void displayGraphSync() {
		
		
		GLLogger.traceTech("actual display of  the graph...", getClass());
		
		// TODO quality  from parameters
		gsGraph.addAttribute("ui.quality");
		gsGraph.addAttribute("ui.antialias");
		
		GLLogger.traceTech("create the graphstream viewer...", getClass());
		gsViewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		gsViewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
		
		GLLogger.traceTech("create the view...", getClass());
		gsView = gsViewer.addDefaultView(false); // false indicates "no JFrame".
		//gsView.setDoubleBuffered(false);
		//gsView.setIgnoreRepaint(true);
		
		
		awtRootPane.getContentPane().add(gsView);
		awtRootPane.setVisible(true);
		
		GLLogger.traceTech("start layout...", getClass());
		//awtFrame.invalidate();
		//awtRootPane.invalidate();
		gsViewer.enableAutoLayout();
		
		
	}

	public void displayGraph(IGenlabGraph glGraph) {
		
		if (glGraph instanceof AbstractGraphstreamBasedGraph) {
			// best case: we know the implementation relies on the good one for display :-)
			GLLogger.traceTech("luckily, this is already a graphstream graph; it will be more efficient", getClass());

			gsGraph = ((AbstractGraphstreamBasedGraph)glGraph)._getInternalGraphstreamGraph();
		} else {
			throw new NotImplementedException("not yet able to transcode networks... sorry");
		}
		
		parent.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				displayGraphSync();
			}
		});
	}

	@Override
	public void dispose() {
		
		GLLogger.traceTech("disposing this view...", getClass());
		
		if (gsViewer != null) {
			gsViewer.disableAutoLayout();
			gsViewer.close();
		}
		if (gsView != null) {
			// TODO ?
		}
		if (hostSwtComposite != null && !hostSwtComposite.isDisposed())
			hostSwtComposite.dispose();
		
		super.dispose();
	}
	
	

}
