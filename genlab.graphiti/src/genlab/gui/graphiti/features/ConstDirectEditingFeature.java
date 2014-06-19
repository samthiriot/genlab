package genlab.gui.graphiti.features;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Map;

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
public class ConstDirectEditingFeature extends AbstractDirectEditingFeature {

	public ConstDirectEditingFeature(IFeatureProvider fp) {
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

		if (bo == null)
			return false;
		
		if (!(bo instanceof IAlgoInstance))
			return false;
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		return (ai.getAlgo() instanceof IConstantAlgo);
    }
    
	@Override
	public String getInitialValue(IDirectEditingContext context) {

		PictogramElement pe = context.getPictogramElement();
        
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo == null || ! (bo instanceof IAlgoInstance)) {
			GLLogger.warnTech("wrong context for direct edition", getClass());
			return null;
		}
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		IConstantAlgo algo = (IConstantAlgo) ai.getAlgo();
		
		return ai.getValueForParameter(algo.getConstantParameter().getId()).toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
		
		
		PictogramElement pe = context.getPictogramElement();
        
		Object bo = getBusinessObjectForPictogramElement(pe);

		if (bo == null || ! (bo instanceof IAlgoInstance)) {
			GLLogger.warnTech("wrong context for direct edition", getClass());
			return null;
		}
		
		value = value.trim();
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		IConstantAlgo algo = (IConstantAlgo) ai.getAlgo();
		
		Parameter param = algo.getConstantParameter();
		
		Object valueCasted = null;
		try {
			valueCasted = param.parseFromString(value);
		} catch (WrongParametersException e) {
			return e.getMessage();
		}
		
		Map<String,Object> params = param.check(valueCasted);
		
		if (!params.isEmpty()) {
			return params.keySet().iterator().next();
		}
		
        // null means, that the value is valid
		return null;
	}
	 
	@SuppressWarnings("rawtypes")
	public void setValue(String value, IDirectEditingContext context) {
		
		// set the new name for the MOF class
		PictogramElement pe = context.getPictogramElement();
		
		IAlgoInstance ai = (IAlgoInstance)getBusinessObjectForPictogramElement(pe);

		IConstantAlgo algo = (IConstantAlgo) ai.getAlgo();

		value = value.trim();
		
		
		Parameter param = algo.getConstantParameter();
		
		ai.setValueForParameter(param, param.parseFromString(value));
		
		
        // Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
	 
		// nota bene: the graphic element is updated because of an event for workflow change.
		// nothing to do there.
	    // updatePictogramElement(((Shape) pe).getContainer());
        
	}
	

}
