package genlab.gui.graphiti.features;

import genlab.algog.gui.examples.CreateWorkflowNSGA2FromWorkflow;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.persistence.GenlabPersistence;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Encapsulates some selected elements inside a NSGA2 algorithm instance.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class EncaspulateNSGA2DiagramFeature extends AbstractCustomFeature {

	public EncaspulateNSGA2DiagramFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getDescription() {
		return "Creates another workflow for exploring some algorithms with NSGA2"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return "&Encapsule inside a genetic algorithm"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		
		// retrieve the workflow
		IGenlabWorkflowInstance originalWorkflow = null;

		// list selected elements
		Collection<IAlgoInstance> selected = new LinkedList<IAlgoInstance>();
		for (PictogramElement e: context.getPictogramElements()) {
			Object bo = getBusinessObjectForPictogramElement(e);
			if (bo instanceof IGenlabWorkflowInstance) {
				originalWorkflow = (IGenlabWorkflowInstance) bo;
			} else if (bo instanceof IAlgoInstance) {
				selected.add((IAlgoInstance) bo);
				originalWorkflow = ((IAlgoInstance) bo).getWorkflow();
			}
		}
		
		if (originalWorkflow == null) {
			return;
		}
		
		IGenlabWorkflowInstance createdWorkflow = CreateWorkflowNSGA2FromWorkflow.createNSGA2InstanceForWorkflow(
				originalWorkflow, 
				selected
				);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(createdWorkflow);
		
		GenlabPersistence.getPersistence().saveWorkflow(createdWorkflow);
		
		
	}

}
