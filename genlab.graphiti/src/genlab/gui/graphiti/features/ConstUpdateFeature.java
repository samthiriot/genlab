package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IConstantAlgo;
import genlab.gui.graphiti.UIUtils;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Updates algo instances; notably updates the name
 * 
 * @author Samuel Thiriot
 *
 */
public class ConstUpdateFeature extends AbstractUpdateFeature {

	public ConstUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {

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
	public IReason updateNeeded(IUpdateContext context) {
		
		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		IConstantAlgo algo = (IConstantAlgo) ai.getAlgo();
		
		// check value
		{
			String displayedValue = null;
			
			if (pe instanceof ContainerShape) {
	            ContainerShape cs = (ContainerShape) pe;
	            for (Shape shape : cs.getChildren()) {
	                if (shape.getGraphicsAlgorithm() instanceof Text) {
	                    Text text = (Text) shape.getGraphicsAlgorithm();
	                    displayedValue = text.getValue();
	                    break;
	                }
	            }
	        }
			
			String theoreticalValue = ai.getValueForParameter(algo.getConstantParameter().getId()).toString();
			
			if (!displayedValue.equals(theoreticalValue))
		            return Reason.createTrueReason("Value is out of date");
		}
		
		return Reason.createFalseReason();
		
	}

	@Override
	public boolean update(IUpdateContext context) {

		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		
		IConstantAlgo algo = (IConstantAlgo) ai.getAlgo();
		
		// replace value
		String theoreticalValue = ai.getValueForParameter(algo.getConstantParameter().getId()).toString();

		
		if (pe instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pe;
            for (Shape shape : cs.getChildren()) {
                if (shape.getGraphicsAlgorithm() instanceof Text) {
                    Text text = (Text) shape.getGraphicsAlgorithm();
                    if (!text.getValue().equals(theoreticalValue)) {
                    	UIUtils.setValueInTransaction(text, theoreticalValue);
                    	layoutPictogramElement(pe);
                        return true;
                    } else {
                    	return false;
                    }
                   
                }
            }
        }
		
		return false;
	}

}
