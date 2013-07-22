package genlab.graphstream.ui.views;

import genlab.core.commons.NotImplementedException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.meta.basics.graphs.AbstractGraphstreamBasedGraph;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.editors.IGenlabGraphicalView;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.BorderLayout;
import java.awt.Color;
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
 * TODO: do not display graphs when they are too big; alert instead.
 * @see http://www.eclipse.org/articles/Article-Swing-SWT-Integration/index.html
 * @author Samuel Thiriot
 *
 */
public class AbstractGraphView extends AbstractViewOpenedByAlgo implements IGenlabGraphicalView {


	private Graph gsGraph = null;
	private Composite parent = null;
	
	private Composite hostSwtComposite = null;
	
	private Frame awtFrame = null;
	private Panel awtPanel = null;
	
	private JRootPane awtRootPane;
	
	private View gsView = null;
	
	private Viewer gsViewer = null;
	
	private ListOfMessages messages = ListsOfMessages.getGenlabMessages();
	
	private IComputationProgress progress = null;
	

	public static final String CLASSNAME_VIEWER = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
	
	public static boolean isAvailable = true;
	
	private boolean firstDisplay = true;

	{
		// reduce flickering for Windows
		System.setProperty("sun.awt.noerasebackground", "true");

		// define the more advanced viewer as the default graphstream viewer
		//System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		messages.traceTech("init an AWT frame...", getClass());
		
		hostSwtComposite = new Composite(parent, SWT.EMBEDDED | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND | SWT.TRANSPARENT );  
		hostSwtComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		//hostSwtComposite.setRedraw(true);
		
		
		// create an AWT frame into the SWT composite
		awtFrame = SWT_AWT.new_Frame(hostSwtComposite);
		//awtFrame.setIgnoreRepaint(true);
		awtFrame.setLayout(new BorderLayout());
		awtFrame.setBackground(Color.WHITE);
		
		// create a panel that does not clears its background into the AWT frame
		awtPanel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				/* Do not erase the background */
				paint(g);
			}
		};
		awtPanel.setBackground(Color.WHITE);
		awtFrame.add(awtPanel, BorderLayout.CENTER);
		
		awtRootPane = new JRootPane(); 
		awtRootPane.setBackground(Color.WHITE);
		awtPanel.add(awtRootPane, BorderLayout.CENTER); 

		awtRootPane.getContentPane().setLayout(new BorderLayout());
		awtRootPane.getContentPane().setBackground(Color.WHITE);
		
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
		
		if (gsView == null)
			return;
		
		
		if (firstDisplay) {
			gsView.setVisible(true);
			gsView.revalidate();
			firstDisplay = false;
		}
		
		gsView.grabFocus();

		
	}
	
	protected void configureGraph(Graph gsGraph) {
		// TODO quality  from parameters
		gsGraph.addAttribute("ui.quality");
		gsGraph.addAttribute("ui.antialias");
		
	}
	
	protected void configureViewer(Viewer gsViewer) {
		gsViewer.disableAutoLayout();
		gsViewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
	}
	
	protected void startViewer(Viewer gsViewer) {
	
	}
	
	
	public void displayGraphSync() {
		
		try {
			messages.traceTech("actual display of  the graph...", getClass());
			
			configureGraph(gsGraph);
			
			messages.traceTech("create the graphstream viewer...", getClass());
			gsViewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			configureViewer(gsViewer);
			
			messages.traceTech("create the view...", getClass());
			gsView = gsViewer.addDefaultView(false); // false indicates "no JFrame".
			//gsView.setDoubleBuffered(false);
			//gsView.setIgnoreRepaint(true);
			gsView.setBackground(Color.CYAN);
			
			awtRootPane.getContentPane().add(gsView, BorderLayout.CENTER);
			
			//awtFrame.invalidate();
			//awtRootPane.invalidate();
			startViewer(gsViewer);
			

			awtFrame.doLayout();
			awtRootPane.doLayout();
			awtRootPane.invalidate();
			awtRootPane.repaint();
		
			awtPanel.setVisible(true);
			awtRootPane.setVisible(true);
			gsView.setVisible(true);
			gsView.revalidate();
			gsView.repaint();
			
			progress.setComputationState(ComputationState.FINISHED_OK);
			
			progress = null;
		} catch (RuntimeException e) {
			messages.errorTech("an error occured during the sync display of the graph: "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		}
	}

	public void displayGraph(IGenlabGraph glGraph, String stylesheetFilenmae, ListOfMessages messages, IComputationProgress progress) {
		
		this.messages = messages;
		this.progress = progress;
		
		try {
			if (glGraph instanceof AbstractGraphstreamBasedGraph) {
				
				// best case: we know the implementation relies on the good one for display :-)
				messages.traceTech("luckily, this is already a graphstream graph; it will be more efficient", getClass());
				gsGraph = ((AbstractGraphstreamBasedGraph)glGraph)._getInternalGraphstreamGraph();
				
			} else {
				throw new NotImplementedException("not yet able to transcode networks... sorry");
			}
			
			// if provided, add the stylesheet for this graph
			if (stylesheetFilenmae != null) {
				messages.debugTech("a stylesheet was provided, adding it to the graph...", getClass());
				gsGraph.setAttribute("ui.stylesheet", "url('file://"+stylesheetFilenmae+"')");
			}
			
			parent.getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					displayGraphSync();
				}
			});
			
		} catch (RuntimeException e) {
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			messages.errorTech("an error occured during the display of the graph: "+e.getMessage(), getClass(), e);
		}
	}

	@Override
	public void dispose() {
		
		messages.traceTech("disposing this view...", getClass());
		
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

	@Override
	public boolean canSnapshot() {
		return true;
	}

	@Override
	public boolean canSnapshotNow() {
		return gsGraph != null && gsViewer != null;
	}

	@Override
	public void snapshot(String filename) {
		messages.debugUser("saving a snapshot into "+filename+"...", getClass());
		gsGraph.addAttribute("ui.screenshot", filename);
	}
	
	

}
