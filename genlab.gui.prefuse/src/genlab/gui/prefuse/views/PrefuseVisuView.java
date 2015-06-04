package genlab.gui.prefuse.views;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.ColorRGBParameterValue;
import genlab.core.parameters.DoubleParameter;
import genlab.gui.Utils;
import genlab.gui.actions.ShowParametersAction;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.editors.IGenlabGraphicalView;
import genlab.gui.parameters.RGBParameter;
import genlab.gui.prefuse.PrefuseUtils;
import genlab.gui.prefuse.algos.PrefuseVisuAlgo;
import genlab.gui.prefuse.parameters.ColorContinuumParameter;
import genlab.gui.prefuse.parameters.ParamValueContinuum;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.ToolTipManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.actions.PartEventAction;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.StrokeLib;
import prefuse.util.display.PaintListener;
import prefuse.visual.VisualItem;

/**
 * 
 * The eclipse View which displays a Prefuse display of the graph. 
 * 
 * TODO manage enumerated datatypes
 * TODO manage edge attributes   
 * TODO add legend
 * TODO add screen capture
 * 
 * @author Samuel Thiriot
 *
 */
public class PrefuseVisuView 
									extends AbstractViewOpenedByAlgo<IGenlabGraph> 
									implements IGenlabGraphicalView, IPartListener2, IParametersListener {

	public static final String VIEW_ID = "genlab.gui.prefuse.views.PrefuseVisuView";
	
	protected final static ColorRGBParameterValue defaultRGBColor = new ColorRGBParameterValue(100, 100, 100);
	protected final static Double defaultWeight = new Double(0.0000001);


	/**
	 * Duration of animations for zoom
	 */
	protected final static long PREFUSE_ZOOM_DURATION = 500;
	
	
	private Composite hostSwtComposite = null;

	private Visualization vis = null;
	private MyPrefuseDisplay prefuseDisplay = null;
		
	private Graph prefuseGraph;	
	
	/**
	 * Prefuse actions: colors for items
	 */
	private ActionList color;


	/**
	 * The action which draws edges
	 */
	private StrokeAction saEdges;
	
	/**
	 * Prefuse actions: layout
	 */
	private ActionList layout = null;

	/**
	 * In charge of the layout with different weights for multiplex nets
	 */
	protected MultiplexForceDirectedLayout theLayout = null;
	
	/**
	 * The prefuse control in charge of autofit
	 */
	private MyZoomToFitControl zoomToFitControl = null;
	

	/**
	 * The controler which detects if the users clicks into the legend.
	 */
	//private LegendMouseClickDetector legendMouseClickDetector; 
	
	protected MyToolTipControl tooltipcontrol = null;


	/**
	 * Colors for nodes
	 */
	private int[] paletteNodeColor = null;
	private String fieldForNodeColoring = "colorid";

	protected boolean shouldBeRunning = false;
	/**
	 * Maps linktypes to the Stroke that will be used to draw them
	 */
	private Map<String,BasicStroke> linktype2stroke;

	/**
	 * In charge of displaying a legend in the display
	 */
	//  TODO private Legend legend;
	
	/**
	 * Renders labels
	 */
	private MyLabelRenderer labelRendered;
	
	/**
	 * Parameters reconstructed from the genlab parameters machinery, with a quick access.
	 */
	private Map<Integer,LinktypeParameters> linktype2parameters = new HashMap<Integer,LinktypeParameters>();
	
	/**
	 * Maps each Genlab edge type (String) to the corresponding index in Prefuse
	 */
	private Map<String,Integer> edgetypeId2idx = new HashMap<String,Integer>();
	
	
	static {
	
		// remove the (way too) numerous prefuse debug lines. Is also better for performance.
		initPrefuseLoggers();
	
		// efficiency
		try {
			System.setProperty("sun.awt.noerasebackground", "true");
		} catch (NoSuchMethodError e) {
		}
		try {
			System.setProperty("swing.aatext", "true");
		} catch (NoSuchMethodError e) {
		}
	}
	
	/**
	 * From the graph properties, do declare the required parameters
	 */
	private void updateParametersFromContent() {
		
		// declare node parameters
		
		// ... one can choose the attributes for coloring based on the nodes vertices in the network
		{
			List<String> verticesAttributes = new LinkedList<String>(lastVersionDataToDisplay.getDeclaredVertexAttributes());
			Collections.sort(verticesAttributes);
			PrefuseVisuAlgo.PARAM_ATTRIBUTE_VERTEX_COLORING.setItems(verticesAttributes);
			// TODO ! Non ! should not be shared between many instances o_O
		}
		
		// declare linktypes
		for (String linktypeId : lastVersionDataToDisplay.getEdgeTypes()) {
			
			final String parameterId = algoInstance.getId()+".parameters.linktypes."+linktypeId;
			
			if (!algoInstance.hasParameter(parameterId+".color")) {
				algoInstance.declareParameter(
						new RGBParameter(
								parameterId+".color", 
								"color for links "+linktypeId, 
								"define the color for links of type "+linktypeId, 
								defaultRGBColor
								)
						);
				algoInstance.declareParameter(
						new DoubleParameter(
								parameterId+".thickness", 
								"thickness for links "+linktypeId, 
								"define the thickness for links of type "+linktypeId, 
								1.0,
								0.1,
								20.0,
								0.1
								)
						);
				algoInstance.declareParameter(
						new BooleanParameter(
								parameterId+".weightAuto", 
								"automatic weight for links "+linktypeId, 
								"defines automatically the weight for links of type "+linktypeId, 
								Boolean.TRUE
								)
						);
				DoubleParameter paramWeight = new DoubleParameter(
						parameterId+".weight", 
						"weight for links "+linktypeId, 
						"define the weight for links of type "+linktypeId, 
						defaultWeight,
						0.0,
						1.0,
						0.0000004
						);
				paramWeight.precision = 8;

				algoInstance.declareParameter(paramWeight);
			}
			
		}
		
		// declare node color
		{			
			final String idVertexColor = algoInstance.getId()+".parameters.verticesColor.test";
			if (!algoInstance.hasParameter(idVertexColor)) {
				algoInstance.declareParameter(new ColorContinuumParameter(
						idVertexColor, 
						"color vertex test", 
						"choose the color for this vertex", 
						PrefuseUtils.getContinumCool()
						)
				);
			}
		}

		// TODO remove useless params ? 
	}
	
	private final Integer getOrCreatePrefuseIdxForLinkType(String genlabLinktype) {
		Integer res = edgetypeId2idx.get(genlabLinktype);
		if (res == null) {
			res =  edgetypeId2idx.size()+1;
			edgetypeId2idx.put(genlabLinktype, res);
		}
		return res;
	}
	/**
	 * From the algo instance, laod the parameters values
	 */
	private void loadParametersFromAlgoInstance() {

		// load parameters for quality
		if (lastVersionDataToDisplay != null && prefuseDisplay != null) {
		
			final Integer thresholdLowQuality = (Integer) algoInstance.getValueForParameter(PrefuseVisuAlgo.PARAM_LOW_QUALITY_THRESHOLD);
			
			prefuseDisplay.setHighQuality(lastVersionDataToDisplay.getVerticesCount() + lastVersionDataToDisplay.getEdgesCount() <= thresholdLowQuality);
			
		}
		
		// load parameters for displaying the vertices
		{
			Integer idxVertexName = (Integer)algoInstance.getValueForParameter(PrefuseVisuAlgo.PARAM_ATTRIBUTE_VERTEX_COLORING);
			String[] items = PrefuseVisuAlgo.PARAM_ATTRIBUTE_VERTEX_COLORING.getItemsAsArray();
			if (idxVertexName >= items.length) {
				// ignore that parameter... nothing defined here.
				// TODO set up default color
			} else {
				// TODO use this vertex for coloring
				fieldForNodeColoring = items[idxVertexName];
			}
			
			// TODO test load color
			ParamValueContinuum c =  (ParamValueContinuum)algoInstance.getValueForParameter(algoInstance.getId()+".parameters.verticesColor.test");
			paletteNodeColor = PrefuseUtils.getPalette(c);
			
		}
		
		// load parameters for displaying the link types
		for (String linktypeId: lastVersionDataToDisplay.getEdgeTypes()) {
			
			final String parameterId = algoInstance.getId()+".parameters.linktypes."+linktypeId;
			
			ColorRGBParameterValue color1 = (ColorRGBParameterValue)algoInstance.getValueForParameter(parameterId+".color");
			Boolean weightAuto = (Boolean)algoInstance.getValueForParameter(parameterId+".weightAuto");
			Double weight = (Double)algoInstance.getValueForParameter(parameterId+".weight");
			Double thickness = (Double)algoInstance.getValueForParameter(parameterId+".thickness");
			
			LinktypeParameters p = linktype2parameters.get(getOrCreatePrefuseIdxForLinkType(linktypeId));
			if (p == null) {
				p = new LinktypeParameters(
						Utils.getRGB(color1), 
						thickness, 
						weightAuto,
						weightAuto ? null: weight
						);
				linktype2parameters.put(getOrCreatePrefuseIdxForLinkType(linktypeId), p);
			} else {
				p.color = Utils.getRGB(color1);
				p.weightAuto = weightAuto;
				p.weight = weightAuto ? null: weight;
				p.width = thickness;
			}
			
		}
		
		// TODO remove useless parameters !
		
	}
	
	public PrefuseVisuView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canSnapshot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSnapshotNow() {
		return lastVersionDataToDisplay != null && vis != null;
	}

	@Override
	public void snapshot(String filename) {

		FileOutputStream fileOutPutStream;
		try {
			fileOutPutStream = new FileOutputStream(filename);
			saveImage(fileOutPutStream, "png", 10.0);
		} catch (FileNotFoundException e) {
			messages.errorUser("unable to export the image in "+filename, getClass(), e);
		}
	}

	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}

	@Override
	protected void refreshDisplaySync() {
		
		// let's register to algo parameters
		algoInstance.addParametersListener(this);	

		// update the parameters of the algo depending to its content
		updateParametersFromContent();
		
		// and load the values from these parameters (possibly default values)
		loadParametersFromAlgoInstance();
		
		// udpate the internal graph
		updatePrefuseGraphFromGenlab();
		
		// we should run !
		shouldBeRunning = true;
		
		// if no layout yet, create it
		initLayout();
		
		// and for sure, start it !
		// TODO or only when it is shown ? 
		startNetVisu();
		
	}

	@Override
	public boolean isDisposed() {
		return hostSwtComposite != null && hostSwtComposite.isDisposed();
	}

	@Override
	public void setFocus() {

		if (hostSwtComposite == null || hostSwtComposite.isDisposed())
			return;
		
		hostSwtComposite.forceFocus();
	}

	
	
	protected void initActionColor() {

		if (vis == null || lastVersionDataToDisplay == null)
			return;

		if (color != null) {
			color.cancel();
		}

		// action list for colors
		color = new ActionList(Activity.INFINITY);
			
		// use black for node text
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));		
		color.add(text);


		// Color for fill
		linktype2stroke.clear();
		
		// the action for drawing edges: basic...
		saEdges = new StrokeAction("graph.edges",
				new BasicStroke()
				);
		
		ColorAction caEdges1 = new ColorAction("graph.edges", VisualItem.STROKECOLOR);
		ColorAction caEdges2 = new ColorAction("graph.edges", VisualItem.FILLCOLOR);
		
		// Colors for edges (stroke for the line, fill for the arraws)
		// fill values for each declared linktype.7
		
		for (String linktypeId: lastVersionDataToDisplay.getEdgeTypes()) {
			
			LinktypeParameters params = linktype2parameters.get(getOrCreatePrefuseIdxForLinkType(linktypeId));
			try {
		
			int color = ColorLib.rgb(
					params.color.red, 
					params.color.green, 
					params.color.blue
					);

			Predicate p = ExpressionParser.predicate("["+MultiplexForceDirectedLayout.FIELD+"]="+getOrCreatePrefuseIdxForLinkType(linktypeId));
			
			messages.debugTech("Added a predicate for links strokes: "+p, getClass());
			
			caEdges1.add(p, color);
			caEdges2.add(p, color);

			BasicStroke stroke = linktype2stroke.get(linktypeId);
			if (stroke == null) {
				
				stroke = StrokeLib.getStroke((float)params.width);

				linktype2stroke.put(linktypeId, stroke);
				
				messages.debugTech("Added a predicate for links strokes: "+p, getClass());
				
				saEdges.add(p, stroke);
			} 
			} catch (NullPointerException e) {
				messages.warnTech("catched a nullpointerexception here", getClass(), e);
			}
		}
		color.add(caEdges1);
		color.add(caEdges2);
		color.add(saEdges);
		
		if (paletteNodeColor == null)  {
			messages.warnTech("no palette exists for nodes colors; in order to prevent NullPointerExceptions, I create an empty one, but this will not lead me to heaven !", getClass());
			paletteNodeColor = new int[1];
		}
		DataColorAction dcaNodes = new DataColorAction(
				"graph.nodes", 
				fieldForNodeColoring,
				Constants.NUMERICAL,//Constants.NOMINAL, 
				VisualItem.FILLCOLOR, 
				paletteNodeColor
				);
		dcaNodes.setScale(Constants.LOG_SCALE);
		color.add(dcaNodes);
		
		//color.add(ca);

		StrokeAction sa = new StrokeAction("graph.nodes", new BasicStroke(2));
		sa.add(VisualItem.FIXED, new BasicStroke(3));
		sa.add(VisualItem.HIGHLIGHT, new BasicStroke(2));
		color.add(sa);

		ColorAction caStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR, ColorLib.rgba(255, 255, 255, 0));
		caStroke.add(VisualItem.FIXED, new ColorAction("graph.nodes", VisualItem.STROKECOLOR, ColorLib.rgb(0, 0, 0) /*
															 * ColorLib.rgb(255,100
															 * ,100)
															 */));
		caStroke.add(VisualItem.HIGHLIGHT, new ColorAction("graph.nodes", VisualItem.STROKECOLOR, ColorLib.rgb(90, 90, 90))); // 255,200,125
		color.add(caStroke);

		color.add(new RepaintAction());
		vis.removeAction("color");
		vis.putAction("color", color);
		//if (shouldBeRunning)
			vis.run("color");
		
	}

	protected void initLayout() {

		if (theLayout == null)
			theLayout = new MultiplexForceDirectedLayout("graph", linktype2parameters);
		
		if (layout == null) {
			layout = new ActionList(Activity.INFINITY);
			if (theLayout != null) {
				layout.add(theLayout);
				theLayout.setEnabled(true);
	
			}
			vis.removeAction("layout");
			vis.putAction("layout", layout);
		}
		
		initActionColor();

	}

	private static void initPrefuseLoggers() {
		// prefuse uses the standart Java Logger framework. It is verbose, however. This asks it politely to shut up.
		java.util.logging.Logger.getLogger("prefuse").setLevel(java.util.logging.Level.ALL); // TODO SEVERE
		java.util.logging.Logger.getLogger("prefuse.data.expression.parser.ExpressionParser").setLevel(java.util.logging.Level.ALL);

		/*
		ActivityManager.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			private Logger logger = Logger.getLogger("prefuse.exceptions");

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof IllegalArgumentException) {
					logger.warn("caught an exception from the activity manager", e);
				}
			}
		});
		*/
		
		// especially usefull to catch " prefuse.action.ActionList run / ATTENTION: null / DataColorAction.getColor(DataColorAction.java:345)"
		java.util.logging.Logger.getLogger(ActionList.class.getName()).setLevel(java.util.logging.Level.ALL); // WARNING
		//java.util.logging.Logger.getLogger("prefuse.action.ActionList run").setLevel(java.util.logging.Level.SEVERE);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
				
		// add parameters access
		getViewSite().getActionBars().getToolBarManager().add(new ShowParametersAction());  

		
		messages.traceTech("init an AWT frame...", getClass());
		
		// data
		linktype2stroke = new HashMap<String, BasicStroke>();


		hostSwtComposite = new Composite(parent, SWT.EMBEDDED | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND | SWT.TRANSPARENT );  
		
		// create the AWT composite
		final Frame frameAwt = SWT_AWT.new_Frame(hostSwtComposite);
		frameAwt.setIgnoreRepaint(true);
		try {
			frameAwt.setUndecorated(true);
		} catch (RuntimeException e) {
		}
		frameAwt.setBackground(Color.GREEN);
		
		// TODO configure mouse listening

		// visu
		if (vis != null) {
			// TODO ???!
		}
		vis = new Visualization();
		
		// renderers
		labelRendered = new MyLabelRenderer("label");
		//labelRendered.setManageBounds(false);
		MyEdgeRenderer edgeRenderer = new MyEdgeRenderer();
		DefaultRendererFactory df = new DefaultRendererFactory(
													labelRendered, 
													edgeRenderer
													);

		vis.setRendererFactory(df);

		// create display
		prefuseDisplay = new MyPrefuseDisplay(vis);

		//prefuseDisplay.setEnabled(false); // used as a flag: if prefuseDisplay is not enabled, no vis is running task
		
		// add the legend to post-paint events
		prefuseDisplay.addPaintListener(new PaintListener() {
			
			@Override
			public void postPaint(prefuse.Display d, Graphics2D g) {
				// TODO legend
				// legend.paintOn(display, graphics, 1.0);	
				
			}

			@Override
			public void prePaint(prefuse.Display d, Graphics2D g) {
				
				//Graphics2D gd2 = (Graphics2D)prefuseDisplay.getGraphics();
				//gd2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
//				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				
			}
		});
		

		// configure the AWT frame that contains the prefuse display 
		JInternalFrame frame = new JInternalFrame("", false, false, false, false);
		frame.setBorder(null);
		((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).setNorthPane(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.add(prefuseDisplay);
		frameAwt.add(frame);
		try {
			frame.setMaximum(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.awt.event.MouseListener mlAwt = new java.awt.event.MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				//System.err.println("force focus from AWT entered \t\t"+e);
				if (!hostSwtComposite.isDisposed())
					hostSwtComposite.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!hostSwtComposite.isDisposed())
							hostSwtComposite.forceFocus();
					}
				});
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				//System.err.println("force focus from AWT entered \t\t"+e);
				if (!hostSwtComposite.isDisposed())
					hostSwtComposite.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!hostSwtComposite.isDisposed())
							hostSwtComposite.forceFocus();
					}
				});
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		}; 
		MouseMotionListener mlAwt2 = new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				//System.err.println("force focus from AWT entered \t\t"+e);
				if (!hostSwtComposite.isDisposed())
					hostSwtComposite.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!hostSwtComposite.isDisposed())
							hostSwtComposite.forceFocus();
					}
				});
			}
			@Override
			public void mouseDragged(MouseEvent e) {
			}
		};
		prefuseDisplay.addMouseListener(mlAwt);
		// TODO prefuseDisplay.addMouseMotionListener(mlAwt2);

		// configure the prefuse display
		prefuseDisplay.setSize(800, 800); // set display size
		prefuseDisplay.setDoubleBuffered(true);
		// TODO ??? 
		prefuseDisplay.setIgnoreRepaint(true);

		// TODO prefuseDisplay.addControlListener(legendMouseClickDetector);
		
		prefuseDisplay.addControlListener(new DragControl()); // drag items
																// around
		prefuseDisplay.addControlListener(new PanControl()); // pan with
																// background
																// left-drag
		prefuseDisplay.addControlListener(new WheelZoomControl()); // zoom with
																	// wheel
		zoomToFitControl = new MyZoomToFitControl(prefuseDisplay);
		prefuseDisplay.addControlListener(zoomToFitControl);
		
		prefuseDisplay.addControlListener(new NeighborHighlightControl());

		
		// add the tooltip manager 
		tooltipcontrol = new MyToolTipControl(getSite().getShell());
		prefuseDisplay.addControlListener(tooltipcontrol);
	
		// init tooltip duration
		ToolTipManager.sharedInstance().setDismissDelay(1000*60);
		ToolTipManager.sharedInstance().setInitialDelay(200);

		// inits the graph with a toy example (is it such a good idea ?)
		prefuseGraph = GraphLib.getGrid(18, 18);// new Graph(false); 
		//prefuseGraph.getNodeTable().addColumn("label", String.class, "");
		prefuseGraph.getEdgeTable().addColumn("type", Integer.class, 0);
		prefuseGraph.getEdgeTable().addColumn(MyEdgeRenderer.FIELD_DIRECTED, Boolean.class, Boolean.FALSE);
		prefuseGraph.getNodeTable().addColumn("colorid", Integer.class);
		vis.add("graph", prefuseGraph);


		// inits colors for the toy example (will be changed later)à
		//paletteLinkColor = new int[2];
		//paletteLinkColor[0] = ColorLib.rgb(100, 100, 100); 
		//paletteLinkColor[1] = ColorLib.rgb(100, 100, 100);
		paletteNodeColor = new int[2];
		paletteNodeColor[0] = ColorLib.rgb(150, 150, 150); 
		paletteNodeColor[1] = ColorLib.rgb(150, 150, 150);
		initLayout();

		// configure composite and frame 
		// TODO ??? compositeNet.setRedraw(false);
		hostSwtComposite.setVisible(true);
		frame.setVisible(true);
		frame.setIgnoreRepaint(false);
		//prefuseDisplay.setEnabled(true); 

		// center the display
		centerDisplayImmediat();
		

	}



	public void zoomOut() {
		
		Rectangle2D r = prefuseDisplay.getItemBounds();
		
		prefuseDisplay.animateZoomAbs(
				new Point((int)r.getCenterX(), (int)r.getCenterY()), 
				0.8, 
				PREFUSE_ZOOM_DURATION
				);
		
	}

	public void zoomIn() {
		
		
		Rectangle2D r = prefuseDisplay.getItemBounds();
		
		prefuseDisplay.animateZoomAbs(
				new Point((int)r.getCenterX(), (int)r.getCenterY()), 
				1.2, 
				PREFUSE_ZOOM_DURATION
				);
		
	}
	
	/**
	 * especially relevant at init
	 */
	public void centerDisplayImmediat() {
	
		Rectangle2D r = prefuseDisplay.getItemBounds();
		
		Point p = new Point(
				(int)r.getCenterX(), 
				(int)r.getCenterY()
				); 
		
		prefuseDisplay.panToAbs(p);
		prefuseDisplay.zoomAbs(p, 0.5);
	}
	
	public void fitToScreen() {
		zoomToFitControl.zommToFitNow();
	}
	
	
	/**
	 * Retrieved from the prefuse sourcecode with some changes.
	 * @param output
	 * @param format
	 * @param scale
	 * @return
	 */
    protected boolean saveImage(OutputStream output, String format, double scale)
    {
        try {
            // get an image to draw into
            
            
            BufferedImage img = prefuseDisplay.getBufferedImage(scale);
           
            // TODO legend legend.paintOn(prefuseDisplay, (Graphics2D)img.getGraphics(), scale);
            
            // save the image and return
            return ImageIO.write(img, format, output);
            
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    

	/**
	 * Clears the graph (removes the nodes, creates another Graph object, and clears other internal 
	 * maps).
	 */
	protected void emptyGraph() {

		// stopNetVisu();

		
		// clears the previous graph
		if (prefuseGraph != null) {
			prefuseGraph.clear();
			prefuseGraph.removeAllSets();
			// graph.dispose();
		}
		
		// removes the previous graph from the visu
		if (vis != null) {
			vis.removeGroup("graph");
			vis.reset();
		}
		
		// create a new graph
		prefuseGraph = new Graph();
		prefuseGraph.getNodeTable().addColumn("label", String.class, "");
		prefuseGraph.getNodeTable().addColumn(MyToolTipControl.FIELD, String.class, "");
		prefuseGraph.getNodeTable().addColumn("colorid", Integer.class, 0);
		prefuseGraph.getNodeTable().index("colorid");
		prefuseGraph.getEdgeTable().addColumn(MultiplexForceDirectedLayout.FIELD, Integer.class, 0);
		prefuseGraph.getEdgeTable().index(MultiplexForceDirectedLayout.FIELD);
		prefuseGraph.getEdgeTable().addColumn(MyEdgeRenderer.FIELD_DIRECTED, Boolean.class);
				
		
		
		// ... and add it to visu
		vis.add("graph", prefuseGraph);

		// startNetVisu();

	}
	
	private void updatePrefuseGraphFromGenlab() {
		
		// preprocess: identify the attributes to declare
		List<String> vertexAttributes = new LinkedList<String>();
		for (Entry<String,Class> attribute: lastVersionDataToDisplay.getDeclaredVertexAttributesAndTypes().entrySet()) {
			
			if (attribute.getValue().equals(Double.class) || attribute.getValue().equals(Integer.class) || attribute.getValue().equals(String.class)) {
				vertexAttributes.add(attribute.getKey());
			}
			
		}
		
		// empty graph first
		if (prefuseGraph == null) {
			prefuseGraph = new Graph();
			prefuseGraph.getNodeTable().addColumn("label", String.class, "");
			prefuseGraph.getNodeTable().addColumn(MyToolTipControl.FIELD, String.class, "");
			prefuseGraph.getNodeTable().addColumn("colorid", Integer.class, 0);
			prefuseGraph.getNodeTable().index("colorid");
			prefuseGraph.getEdgeTable().addColumn(MultiplexForceDirectedLayout.FIELD, Integer.class, 0);
			prefuseGraph.getEdgeTable().index(MultiplexForceDirectedLayout.FIELD);
			prefuseGraph.getEdgeTable().addColumn(MyEdgeRenderer.FIELD_DIRECTED, Boolean.class);	
			
						
		} else {
			prefuseGraph.clear();
			
		}

		// declare vertex attributes
		if (prefuseGraph.getNodeTable().getColumn(MyToolTipControl.FIELD) == null) 
			prefuseGraph.getNodeTable().addColumn(MyToolTipControl.FIELD, String.class, "");

		for (String attributeId: vertexAttributes) {
			if (prefuseGraph.getNodeTable().getColumn(attributeId) != null)
				continue;
			Class type = lastVersionDataToDisplay.getDeclaredVertexAttributesAndTypes().get(attributeId);
			if (type == Double.class)
				prefuseGraph.getNodeTable().addColumn(attributeId, double.class);
			else
				prefuseGraph.getNodeTable().addColumn(attributeId, type);
			
		}
		
		// copy nodes
		Map<String,Node> glVertexId2prefuseVertex = new HashMap<String,Node>((int) lastVersionDataToDisplay.getVerticesCount());
		for (String vertexId: lastVersionDataToDisplay.getVertices()) {
			
			// create prefuse node
			Node n = prefuseGraph.addNode();
			glVertexId2prefuseVertex.put(
					vertexId, 
					n
					);
			
			// and define its parameters
			n.setString("label", vertexId);
			
			// copy attributes
			for (String attributeId: vertexAttributes) {
				Object value = lastVersionDataToDisplay.getVertexAttributeValue(vertexId, attributeId);
				if (value == null)
					continue;
				n.set(attributeId, (double)lastVersionDataToDisplay.getVertexAttributeValue(vertexId, attributeId));
			}
			
			// tooltip
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Vertex ").append(vertexId).append(":\n");
				for (Entry<String,Object> att: lastVersionDataToDisplay.getVertexAttributes(vertexId).entrySet()) {
					sb.append("- ").append(att.getKey()).append(": ");
					sb.append(att.getValue()).append("\n");
				}
				n.setString(MyToolTipControl.FIELD, sb.toString());
			}
			
		}
		
		// create IDs for edge types
		/*edgetypeId2idx.clear();
		for (String edgetypeId : lastVersionDataToDisplay.getEdgeTypes()) {
			if (!edgetypeId2idx.containsKey(edgetypeId)) {
				edgetypeId2idx.put(edgetypeId, edgetypeId2idx.size());
			}
		}*/
		
		// copy edges
		for (String edgeId: lastVersionDataToDisplay.getEdges()) {
			
			// create edge
			Edge e = prefuseGraph.addEdge(
					glVertexId2prefuseVertex.get(lastVersionDataToDisplay.getEdgeVertexFrom(edgeId)),
					glVertexId2prefuseVertex.get(lastVersionDataToDisplay.getEdgeVertexTo(edgeId))
					);
			
			// and tune its parameters
			e.set(MultiplexForceDirectedLayout.FIELD, getOrCreatePrefuseIdxForLinkType(lastVersionDataToDisplay.getEdgeType(edgeId)));
			e.set(MyEdgeRenderer.FIELD_DIRECTED, lastVersionDataToDisplay.isEdgeDirected(edgeId));
			
		}
		
	}

	/**
	 * Stops both layout and color
	 */
	public void stopNetVisu() {

		if (vis == null)
			return;
			
		messages.debugTech("stopping layout...", getClass());
		
		vis.cancel("color");
		//vis.removeAction("color");
		vis.cancel("layout");
		//vis.removeAction("layout");
		prefuseDisplay.setEnabled(false);

		shouldBeRunning = false;
		

	}


	/**
	 * Starts all the prefuse actions (start to layout and paint... and consume your CPU power !)
	 */
	public void startNetVisu() {

		if ((layout == null) || (color == null) || (vis == null))
			return;

		messages.debugTech("restarting layout !", getClass());
		
		shouldBeRunning = true;
		
		prefuseDisplay.setEnabled(true);
		if (layout != null) {
			vis.putAction("layout", layout);
			vis.run("layout");

		}
		if (color != null) {
			vis.putAction("color", color);
			vis.run("color");
		}
		
		prefuseDisplay.setVisible(true);
		
	}

	
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		
		stopNetVisu();
		
		super.partClosed(partRef);
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// TODO stop layout !
		super.partDeactivated(partRef);
	}


	@Override
	public void partHidden(IWorkbenchPartReference partRef) {

		// TODO stop layout !
		super.partHidden(partRef);
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {

		// TODO restart layout !
		
		super.partVisible(partRef);
		
		// TODO not ok
		shouldBeRunning = (partRef.getPartName().equals(this.getPartName()));
	}

	@Override
	public void dispose() {
		
		algoInstance.removeParametersListener(this);
		
		stopNetVisu();
		
		if (hostSwtComposite != null && !hostSwtComposite.isDisposed())
			hostSwtComposite.dispose();
		
		super.dispose();
	}

	@Override
	public void parameterValueChanged(IAlgoInstance ai, String parameterId, Object novelValue) {
		
		if (ai != algoInstance)
			return;
		
		// a parameter value changed; let's update our parameters !
		loadParametersFromAlgoInstance();
		
		// stop and restart ? 
		initActionColor();
		
		
		
	}

	
	
	
}
