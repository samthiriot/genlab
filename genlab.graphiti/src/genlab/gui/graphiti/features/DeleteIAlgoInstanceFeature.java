package genlab.gui.graphiti.features;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.genlab2graphiti.MappingObjects;

import org.eclipse.core.resources.IWorkspace;
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
		IAlgoInstance algoInstance = (IAlgoInstance) getBusinessObjectForPictogramElement(context.getPictogramElement());
		algoInstance.delete();
		
		// TODO !!!
		throw new ProgramException("not yet implemented, sorry");
		/*
		IGenlabWorkflow workflow = (IGenlabWorkflow) MappingObjects.removeGenlabResourceFor(context.getPictogramElement());
		MappingObjects.removeGenlabResourceFor(workflow.getAbsolutePath());
		MappingObjects.removeGenlabResourceFor(workflow.getAbsolutePath()+"."+GraphitiDiagramTypeProvider.GRAPH_EXTENSION);
		*/
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return true;
	}

	
	
	
}
