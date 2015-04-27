package genlab.graphstream.ui.views;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.meta.basics.graphs.AbstractGraphstreamBasedGraph;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.editors.IGenlabGraphicalView;
import genlab.gui.views.AbstractViewOpenedByAlgo;
import genlab.quality.TestResponsivity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JRootPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.Viewer.CloseFramePolicy;

/**
 * TODO: do not display graphs when they are too big; alert instead.
 * @see http://www.eclipse.org/articles/Article-Swing-SWT-Integration/index.html
 * 
 * TODO "we spotted the mysterious bug" http://grepcode.com/file/repo1.maven.org$maven2@org.graphstream$gs-core@1.1.2@org$graphstream$ui$swingViewer$basicRenderer$SwingBasicGraphRenderer.java
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractGraphView extends AbstractViewOpenedByAlgo implements IGenlabGraphicalView, IPartListener2 {



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

	public static final String SWT_THREAD_USER_ID = AbstractViewOpenedByAlgo.class.getCanonicalName();
	
	public static boolean isAvailable = true;
	
	private boolean firstDisplay = true;

	{
		// reduce flickering for Windows
		System.setProperty("sun.awt.noerasebackground", "true");

		// define the more advanced viewer as the default graphstream viewer
		// TODO find a way to use the better renderer
		// System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

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
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
		
		super.createPartControl(parent);
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
		//gsViewer.disableAutoLayout();
		gsViewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
	}
	
	protected void startViewer(Viewer gsViewer) {
	
	}
	
	
	public void displayGraphSync() {
		
		try {
			messages.traceTech("actual display of  the graph...", getClass());
			
			configureGraph(gsGraph);
			
			if (gsViewer == null) {
				messages.traceTech("create the graphstream viewer...", getClass());
				gsViewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
				configureViewer(gsViewer);
				
				messages.traceTech("create the view...", getClass());
				gsView = gsViewer.addDefaultView(false); // false indicates "no JFrame".
				gsView.setDoubleBuffered(true); // test for windows !
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
				
			}  else {
				messages.errorTech("unable to redisplay the graph", getClass());
			}
			
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
				
				messages.traceTech("had to convert the graph to a graphstream graph to display it", getClass());
				gsGraph = GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(glGraph, messages);

			}
			
			// if provided, add the stylesheet for this graph
			if (stylesheetFilenmae != null) {
				
				messages.debugTech("a stylesheet was provided, adding it to the graph...", getClass());
				gsGraph.setAttribute("ui.stylesheet", "url('file://"+stylesheetFilenmae+"')");
				
			} else {

				StringBuffer sbStylesheet = new StringBuffer();

				if (glGraph.getDirectionality() != GraphDirectionality.UNDIRECTED) 
					sbStylesheet.append("edge { arrow-shape: arrow; }\n");

				
				// manage colors for nodes
				String colorNodeAttribute = "color";
				if (colorNodeAttribute != null && glGraph.hasVertexAttribute(colorNodeAttribute)) {
					
					Map<Object,String> attributeValue2key = new HashMap<Object, String>();

					// now browser all the colors in the graph
					for (int i=0; i<gsGraph.getNodeCount(); i++) {
						
						Node n = gsGraph.getNode(i);
						Object value = n.getAttribute(colorNodeAttribute);
						String styleValue = attributeValue2key.get(value);
						
						if (styleValue == null) {
							// no style defined for this value

							
							// and store they color for the style
							Color generatedColor = Color.getHSBColor(
									(float) (Math.random()), 
									(float) (Math.random()), 
									0.9f
									);
							
							/* solution with the best viewer in scala
							sbStylesheet.append("node#").append(styleValue);
							sbStylesheet.append(" { ");
							sbStylesheet.append("fill-color: rgb(")
								.append(generatedColor.getRed()).append(",")
								.append(generatedColor.getGreen()).append(",")
								.append(generatedColor.getBlue())
								.append("); ");
							sbStylesheet.append("fill-mode: plain; ");

							sbStylesheet.append("}\n");
							
							*/
							
							StringBuffer sbNodeCss = new StringBuffer();
							sbNodeCss.append("fill-color: rgb(");
							sbNodeCss.append(generatedColor.getRed()).append(",");
							sbNodeCss.append(generatedColor.getGreen()).append(",");
							sbNodeCss.append(generatedColor.getBlue()).append(");");

							styleValue = sbNodeCss.toString();

							// store the new key for this color value
							attributeValue2key.put(value, styleValue);
							
						}
						
						n.addAttribute("ui.style", styleValue);
						
					}
					attributeValue2key.clear();
					
				}
				
				
				// if the graph has some directionality, then display it
				messages.debugTech("defining as a stylesheet: "+sbStylesheet.toString(), getClass());
				gsGraph.addAttribute("ui.stylesheet", sbStylesheet.toString());
					
			}
				

			if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
				TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID);
			
			parent.getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID);
							
					displayGraphSync();
					
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID);
							
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
		
		awtFrame.dispose();
		
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

	@Override
	public boolean isDisposed() {
		return parent != null && parent.isDisposed();
	}
	
	

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		
		if (gsViewer != null)
			gsViewer.disableAutoLayout();
		
		super.partClosed(partRef);
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		
		if (gsViewer != null)
			gsViewer.disableAutoLayout();
		
		super.partHidden(partRef);
	}
	

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		
		if (gsViewer != null)
			gsViewer.enableAutoLayout();
		
		super.partVisible(partRef);
		
	}

	@Override
	protected void refreshDisplaySync() {
		// TODO Auto-generated method stub
		
	}
	
}
