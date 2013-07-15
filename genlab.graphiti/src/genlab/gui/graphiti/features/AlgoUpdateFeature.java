package genlab.gui.graphiti.features;

import genlab.core.model.instance.IAlgoInstance;

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
public class AlgoUpdateFeature extends AbstractUpdateFeature {

	public AlgoUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {

		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		return (bo != null) && (bo instanceof IAlgoInstance);
		
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		
		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		// check name
		{
			String displayedName = null;
			
			if (pe instanceof ContainerShape) {
	            ContainerShape cs = (ContainerShape) pe;
	            for (Shape shape : cs.getChildren()) {
	                if (shape.getGraphicsAlgorithm() instanceof Text) {
	                    Text text = (Text) shape.getGraphicsAlgorithm();
	                    displayedName = text.getValue();
	                    break;
	                }
	            }
	        }
			
			if (!displayedName.equals(ai.getName()))
		            return Reason.createTrueReason("Name is out of date");
		}
		
		return Reason.createFalseReason();
		
	}

	@Override
	public boolean update(IUpdateContext context) {

		PictogramElement pe = context.getPictogramElement();
		
		Object bo = getBusinessObjectForPictogramElement(pe);
		
		IAlgoInstance ai = (IAlgoInstance)bo;
		
		// replace name (first occurence of text)
		boolean replacedName = false;
		{
			String displayedName = null;
			
			if (pe instanceof ContainerShape) {
	            ContainerShape cs = (ContainerShape) pe;
	            for (Shape shape : cs.getChildren()) {
	                if (shape.getGraphicsAlgorithm() instanceof Text) {
	                    Text text = (Text) shape.getGraphicsAlgorithm();
	                    text.setValue(ai.getName());
	                    replacedName = true;
	                    break;
	                }
	            }
	        }
			
		}
		
		return replacedName;
	}

}
