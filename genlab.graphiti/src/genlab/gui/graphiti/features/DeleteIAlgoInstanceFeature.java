package genlab.gui.graphiti.features;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

/**
 * Feature to delete an algo instance
 * 
 * @author Samuel Thiriot
 *
 */
public class DeleteIAlgoInstanceFeature extends DefaultDeleteFeature {

	public DeleteIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);

	}

	@Override
	public void delete(IDeleteContext context) {
		
		GLLogger.debugTech("IAlgoInstance removed from the diagram, removing it from genlab as well", getClass());

		IAlgoInstance algoInstance = (IAlgoInstance) getBusinessObjectForPictogramElement(context.getPictogramElement());

		// delete graphiti objects
		//super.delete(context);
		
		
		// delete the corresponding IAlgoInstance
		algoInstance.delete();
		
		// the corresponding graphical elements are supposed to be removed by a callback
		
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		
		if (context.getPictogramElement() == null)
			return false;
		
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		
		return (bo != null && (bo instanceof IAlgoInstance));
		
		
	}

	
	
	
}
