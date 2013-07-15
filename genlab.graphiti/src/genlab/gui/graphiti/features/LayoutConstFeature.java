package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * Manages the resizing of constant boxes
 * 
 * @author Samuel Thiriot
 *
 */
public class LayoutConstFeature extends AbstractLayoutFeature {


	    
	public LayoutConstFeature(IFeatureProvider fp) {
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
	    
	    return (ai.getAlgo() instanceof IConstantAlgo);   
	    
	}

	
	@Override
	public boolean layout(ILayoutContext context) {

		GLLogger.traceTech("layout of picto element: "+context, getClass());
		
        boolean anythingChanged = false;

        // retrieve our objects
        
        ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
        GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

        Text text = (Text) containerShape.getChildren().get(0).getGraphicsAlgorithm();
        
        // and the theoretical size
        IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(text.getValue(), text.getFont());
        final int textWidth = dim.getWidth();
        final int textHeight = dim.getHeight();
        final int width = textWidth + AddIAlgoConstFeature.MARGIN_WIDTH*2;
        final int height = textHeight +  + AddIAlgoConstFeature.MARGIN_HEIGHT*2;
       
        // ensure min height and width are ok for invisible rectangle
        
        // height
        if (containerGa.getHeight() != height) {
            containerGa.setHeight(height);
            anythingChanged = true;
        }
 
        // width
        if (containerGa.getWidth() != width) {
            containerGa.setWidth(width);
            anythingChanged = true;
        }        
        
        // resize text
        {
            if (text.getHeight() != textHeight) {
            	text.setHeight(textHeight);
            	anythingChanged = true;
            }
            if (text.getWidth() != textWidth) {
            	text.setWidth(textWidth);
            	anythingChanged = true;
            }        

        }
        
        
        return anythingChanged;
        
	}

}
