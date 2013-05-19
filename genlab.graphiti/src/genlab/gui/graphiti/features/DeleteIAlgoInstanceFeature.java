package genlab.gui.graphiti.features;

import genlab.core.algos.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.genlab2graphiti.MappingObjects;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteIAlgoInstanceFeature extends DefaultDeleteFeature {

	public DeleteIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);

	}

	@Override
	public void delete(IDeleteContext context) {
		
		// delete graphiti objects
		super.delete(context);
		
		GLLogger.debugTech("IAlgoInstance removed from the diagram, removing it from genlab as well", getClass());
		
		// delete the corresponding IAlgoInstance
		IAlgoInstance algoInstance = (IAlgoInstance) MappingObjects.getGenlabResourceFor(context.getPictogramElement());
		algoInstance.delete();
		
		MappingObjects.removeGenlabResourceFor(context.getPictogramElement());
		
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return true;
	}

	
	
	
}
