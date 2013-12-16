package genlab.gui.graphiti.features;

import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * Manages the resizing of standard algo boxes
 * @see for resizing with invisible rect http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fselection-behavior.htm
 * 
 * @author Samuel Thiriot
 *
 */
public class LayoutIAlgoContainerFeature extends AbstractLayoutFeature {

	public static final int INVISIBLE_RECT_MARGIN_TOP = 5;

	public static final int ANCHOR_WIDTH = 12;
	public static final int INVISIBLE_RECT_MARGIN_HORIZ = ANCHOR_WIDTH/2;

	public static final int TITLE_TEXT_LEFT = INVISIBLE_RECT_MARGIN_HORIZ+ANCHOR_WIDTH+16;

	/**
	 * the y coordinate of the line between title and details
	 */
	public static final int Y_LINE = ANCHOR_WIDTH/2 + INVISIBLE_RECT_MARGIN_TOP + 20;
	
	/**
	 * The width of the "right column", for "anchors" (graphiti world) or outputs (genlab world).
	 */
	public static final int IO_LABELS_WIDTH = 80 + ANCHOR_WIDTH;
	

	public static final int RECTANGLE_INSIDE_TOP = Y_LINE + 2;
	public static final int RECTANGLE_INSIDE_BOTTOM_MARGIN = 10;
	public static final int RECTANGLE_INSIDE_LEFT = 20;
	
	 
	/**
	 * The minimal width and height for ergonomic reasons
	 */
	public static final int MIN_WIDTH = 100;
	public static final int MIN_HEIGHT = 100 + INVISIBLE_RECT_MARGIN_TOP + Y_LINE + RECTANGLE_INSIDE_TOP;
	
	
	/**
	 * computes the minimal width for this precise algo container
	 * @param addedAlgo
	 */
	public static int computeMinimumWidth(AlgoContainerInstance addedAlgo) {
		
		int width = 0; 
		
		// adds the space for these anchors
		width += ANCHOR_WIDTH*2;
		
		// include the space for label on the left (if required)
		width += addedAlgo.getInputInstances().isEmpty()?0: LayoutIAlgoContainerFeature.IO_LABELS_WIDTH;
		
		// include the space for labels on the right (if required)
		width += addedAlgo.getOutputInstances().isEmpty()?0: LayoutIAlgoContainerFeature.IO_LABELS_WIDTH;
		
		// and, add some more space
		width += 20;
		
		return Math.max(MIN_WIDTH, width);
		
	}
	
	public static int computeMinimumHeight(AlgoContainerInstance addedAlgo) {
		
		int height = 0; 
		
		height += Y_LINE;
		
		// add the space for anchors
		// TODO compute the line height based on the font size ? 
		height += Math.max(
					addedAlgo.getInputInstances().size(),
					addedAlgo.getOutputInstances().size()
					) * LayoutIAlgoFeature.ANCHOR_WIDTH * 2;
		
		return Math.max(MIN_HEIGHT, height);
		
	}
	
	public LayoutIAlgoContainerFeature(IFeatureProvider fp) {
		super(fp);
		
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
	    if (!(pe instanceof ContainerShape))
	    	return false;
	       
	    Object genlabObj = getBusinessObjectForPictogramElement(pe);
	    if (genlabObj == null) 
	    	return false;
	   
	    if (!(genlabObj instanceof IAlgoContainerInstance))
	    	return false; 
	    
	    IAlgoContainerInstance ai = (IAlgoContainerInstance)genlabObj;
	    
	    return !(ai.getAlgo() instanceof IConstantAlgo);
	}
	

	/**
	 * returns true if something changed
	 * @param shape
	 * @return
	 */
	protected boolean manageResizing(int containerWidth, Shape shape) {

		boolean anythingChanged = false;
		
        GraphicsAlgorithm graphicsAlgorithm = shape.getGraphicsAlgorithm();
        IGaService gaService = Graphiti.getGaService();
        IDimension size =  gaService.calculateSize(graphicsAlgorithm);
        if (containerWidth != size.getWidth()) {
            if (graphicsAlgorithm instanceof Polyline) {
                Polyline polyline = (Polyline) graphicsAlgorithm;
                Point secondPoint = polyline.getPoints().get(1);
                Point newSecondPoint = gaService.createPoint(containerWidth+INVISIBLE_RECT_MARGIN_HORIZ, secondPoint.getY()); 
                polyline.getPoints().set(1, newSecondPoint);
                anythingChanged = true;
            } else if (graphicsAlgorithm instanceof Text) {
            	Object bo = getBusinessObjectForPictogramElement(shape);
            	if (bo != null && bo instanceof AlgoInstance) {
            		// if this is the top (title) text
                	// ... resize it !
            		gaService.setWidth(graphicsAlgorithm, containerWidth-TITLE_TEXT_LEFT);
            		anythingChanged = true;
            	} else {
            		// this is probably a text for anchor
            		Text text = (Text)graphicsAlgorithm;
            		if (text.getX() > ANCHOR_WIDTH*2) {
            			// this is a right element
            			gaService.setLocationAndSize(
            					text, 
            					containerWidth - IO_LABELS_WIDTH - ANCHOR_WIDTH/2 - INVISIBLE_RECT_MARGIN_HORIZ,
            					text.getY(),
            					IO_LABELS_WIDTH,
            					text.getHeight()
            					);
            		} else {
            			// this is a left element: just resize it
            			gaService.setWidth(text, IO_LABELS_WIDTH - ANCHOR_WIDTH/2 - INVISIBLE_RECT_MARGIN_HORIZ);
            		}
            	}
                
            }
        }
        
        return anythingChanged;

	}
	
	
	protected boolean manageResizing(int containerWidth, FixPointAnchor anchor) {

		boolean anythingChanged = false;
		
        GraphicsAlgorithm graphicsAlgorithm = anchor.getGraphicsAlgorithm();
        
        // do not process the anchors on the left
        if (anchor.getLocation().getX() == 0)
        	return false;
        
        
        final int theoreticalX =  (containerWidth - INVISIBLE_RECT_MARGIN_HORIZ*2);
        
        if (graphicsAlgorithm.getX() != theoreticalX) {
        	GLLogger.traceTech("moving anchor horizontally from "+graphicsAlgorithm.getX()+" to "+theoreticalX+" over "+containerWidth, getClass());
        	//graphicsAlgorithm.setX(theoreticalX);
    		IGaService gaService = Graphiti.getGaService();
    		
    		anchor.setLocation(gaService.createPoint(
    				theoreticalX,
    				anchor.getLocation().getY()
    				));
    		
    		/*
        	gaService.setLocationAndSize(
        			graphicsAlgorithm, 
        			theoreticalX,
        			graphicsAlgorithm.getY(),
        			ANCHOR_WIDTH,
        			ANCHOR_WIDTH,
        			false
        			);
        			*/
        	anythingChanged = true;
//        	layoutPictogramElement(anchor);
        }
        
       
        
        return anythingChanged;

	}
	
	
	@Override
	public boolean layout(ILayoutContext context) {

		GLLogger.traceTech("layout of picto element: "+context, getClass());
		
        boolean anythingChanged = false;

        
        ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
        GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();


        // retrieve the corresponding business object
	    IAlgoContainerInstance ai = (IAlgoContainerInstance)getBusinessObjectForPictogramElement(containerShape);

        // ensure min height and width are ok for invisible rectangle
        
        // height
        if (containerGa.getHeight() < MIN_HEIGHT) {
            containerGa.setHeight(MIN_HEIGHT);
            anythingChanged = true;
        }
 
        // width
        if (containerGa.getWidth() < MIN_WIDTH) {
            containerGa.setWidth(MIN_WIDTH);
            anythingChanged = true;
        }        
  
        int containerWidth = containerGa.getWidth() - INVISIBLE_RECT_MARGIN_HORIZ*2;

        // resize visible rectangle
        {
            GraphicsAlgorithm rectangle = containerGa.getGraphicsAlgorithmChildren().get(0);
            if (rectangle.getHeight() != containerGa.getHeight()-INVISIBLE_RECT_MARGIN_TOP) {
            	rectangle.setHeight(containerGa.getHeight()-INVISIBLE_RECT_MARGIN_TOP);
            	anythingChanged = true;
            }
            if (rectangle.getWidth() != containerWidth) {
            	rectangle.setWidth(containerWidth);
            	anythingChanged = true;
            }        
            containerWidth = rectangle.getWidth();

        }
        
        // resize visible rectangle inside
        {
        	final int marginLeft = RECTANGLE_INSIDE_LEFT + (ai.getInputInstances().isEmpty()?0:LayoutIAlgoContainerFeature.IO_LABELS_WIDTH);
        	final int marginTop = RECTANGLE_INSIDE_TOP;
        	final int marginRight = ANCHOR_WIDTH*2 +
        			(ai.getOutputInstances().isEmpty()?0:LayoutIAlgoContainerFeature.IO_LABELS_WIDTH)
        			;
        	final int marginBottom = RECTANGLE_INSIDE_BOTTOM_MARGIN;
        	
            GraphicsAlgorithm rectangle = containerGa.getGraphicsAlgorithmChildren().get(1);
            final int expectedHeight = containerGa.getHeight() - INVISIBLE_RECT_MARGIN_TOP - marginTop - marginBottom;
        	final int expectedWidth = containerGa.getWidth() - INVISIBLE_RECT_MARGIN_HORIZ*2 - marginLeft - marginRight;
			
            if (rectangle.getHeight() != expectedHeight) {
            	rectangle.setHeight(expectedHeight);
            	anythingChanged = true;
            }
            if (rectangle.getWidth() != expectedWidth) {
            	rectangle.setWidth(expectedWidth);
            	anythingChanged = true;
            }        

        }
        
        // rezize text, lines and this kind of stuff
        for (Shape shape : containerShape.getChildren()){
        	
        	//TITLE_TEXT_LEFT
        	anythingChanged = manageResizing(containerWidth, shape) || anythingChanged;
        	
        }
        
        
        // move anchors
        
        for (Anchor anchor: containerShape.getAnchors()) {
        	
        	anythingChanged = manageResizing(containerShape.getGraphicsAlgorithm().getWidth(), (FixPointAnchor)anchor) || anythingChanged;
            	
        }
        
        
        return anythingChanged;
        
	}

}
