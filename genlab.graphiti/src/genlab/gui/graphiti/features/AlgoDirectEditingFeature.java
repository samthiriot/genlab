package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Provides the direct edition feature for algos: 
 * enables to edit the name of the algo.
 * 
 * @author Samuel Thiriot
 *
 */
public class AlgoDirectEditingFeature extends AbstractDirectEditingFeature {

	public AlgoDirectEditingFeature(IFeatureProvider fp) {
		super(fp);

	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}


    @Override
    public boolean canDirectEdit(IDirectEditingContext context) {
    	
		PictogramElement pe = context.getPictogramElement();
        
		Object bo = getBusinessObjectForPictogramElement(pe);

		return (bo != null && bo instanceof IAlgoInstance);
		
    }
    
	@Override
	public String getInitialValue(IDirectEditingContext context) {

		PictogramElement pe = context.getPictogramElement();
        
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo == null || ! (bo instanceof IAlgoInstance)) {
			GLLogger.warnTech("wrong context for direct edition", getClass());
			return null;
		}
		
		IAlgoInstance algo = (IAlgoInstance)bo;
		
		return algo.getName();
	}
	
	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
		
		PictogramElement pe = context.getPictogramElement();
        
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo == null || ! (bo instanceof IAlgoInstance)) {
			GLLogger.warnTech("wrong context for direct edition", getClass());
			return null;
		}
		
		value = value.trim();
		
		IAlgoInstance algo = (IAlgoInstance)bo;
		IGenlabWorkflowInstance workflow = algo.getWorkflow();
		
		if (value.length() < 1)
            return "The name can not be empty.";
		if (value.contains("\n"))
			return "Line breakes are not allowed in class names.";
 		if (!workflow.getAlgoInstanceForName(value).equals(algo))
			return "This name is already used";
		
        // null means, that the value is valid
		return null;
	}
	 

	public void setValue(String value, IDirectEditingContext context) {
		
		// set the new name for the MOF class
		PictogramElement pe = context.getPictogramElement();
		
		IAlgoInstance ai = (IAlgoInstance)getBusinessObjectForPictogramElement(pe);
		ai.setName(value.trim());
	
        // Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
	 
		// nota bene: the graphic element is updated because of an event for workflow change.
		// nothing to do there.
	    // updatePictogramElement(((Shape) pe).getContainer());
        
	}
	

}
