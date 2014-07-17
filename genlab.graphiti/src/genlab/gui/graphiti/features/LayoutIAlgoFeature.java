package genlab.gui.graphiti.features;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.commands.operations.ICompositeOperation;
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
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * Manages the resizing of standard algo boxes
 * @see for resizing with invisible rect http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fselection-behavior.htm
 * 
 * @author Samuel Thiriot
 *
 */
public class LayoutIAlgoFeature extends AbstractLayoutFeature {

	public static final int INVISIBLE_RECT_MARGIN_TOP = 5;

	
	public static final int ANCHOR_WIDTH = 12;
	
	/**
	 * Margin required on the right and left of the visible rectangle, in order
	 * to display half of the anchors.
	 */
	public static final int INVISIBLE_RECT_MARGIN_HORIZ = ANCHOR_WIDTH/2;

	public static final int TITLE_TEXT_LEFT = INVISIBLE_RECT_MARGIN_HORIZ+ANCHOR_WIDTH+16;
	
	
	public static final int MIN_HEIGHT = 100;
	 
	public static final int MIN_WIDTH = 100;
	    
	public LayoutIAlgoFeature(IFeatureProvider fp) {
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
	   
	    if (!(genlabObj instanceof IAlgoInstance))
	    	return false; 
	    
	    IAlgoInstance ai = (IAlgoInstance)genlabObj;
	    
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
            					// x
            					containerWidth/2,
            					// y
            					text.getY(),
            					// width
            					containerWidth/2-ANCHOR_WIDTH/2,
            					// height
            					text.getHeight()
            					);
            		} else {
            			// this is a left element: just resize it
            			gaService.setWidth(text, containerWidth/2-ANCHOR_WIDTH);
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
        
        //final int theoreticalX = 0;
        
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
        
        // ensure min height and width are ok for invisible rectangle
        
        // determine minimal height
        int minHeight = MIN_HEIGHT;
        for (Anchor anchor: containerShape.getAnchors()) {
        	
        	try {
	        	FixPointAnchor fixedAnchor = (FixPointAnchor)anchor;
	        	
	        	int heightOfAnchor = fixedAnchor.getLocation().getY()+ANCHOR_WIDTH*2;
	        	if (heightOfAnchor > minHeight)
	        		minHeight = heightOfAnchor;
	            	
        	} catch (ClassCastException e) {
        		// do nothing 
        	}
        }
        
        int minWidth = MIN_WIDTH;
                
        // height
        if (containerGa.getHeight() < minHeight) {
            containerGa.setHeight(minHeight);
            anythingChanged = true;
        }
 
        // width
        if (containerGa.getWidth() < minWidth) {
            containerGa.setWidth(minWidth);
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
        
        // rezize text, lines and this kind of stuff
        for (Shape shape : containerShape.getChildren()){
        	
        	//TITLE_TEXT_LEFT
        	anythingChanged = manageResizing(containerWidth, shape) || anythingChanged;
        	
        }
        
        
        // move anchors
        
        for (Anchor anchor: containerShape.getAnchors()) {
        	
        	anythingChanged = manageResizing(
        			containerShape.getGraphicsAlgorithm().getWidth(), 
        			(FixPointAnchor)anchor
        			) 
        			|| anythingChanged;
            	
        }
        
        
        return anythingChanged;
        
	}

}
