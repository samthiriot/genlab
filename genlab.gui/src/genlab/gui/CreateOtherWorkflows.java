package genlab.gui;

import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.GenlabWorkflow;
import genlab.core.model.meta.LoopForAlgo;
import genlab.core.model.meta.basics.algos.AppendToTableAlgo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class CreateOtherWorkflows {
	
	/**
	 * In a workflow, detect all the free outputs that might enter a table reduction
	 * @param workflow
	 * @return
	 */
	protected static Collection<IInputOutputInstance> detectOutputsForAggregationOfResults(
			IGenlabWorkflowInstance workflow
			) {

		Collection<IInputOutputInstance> res = new LinkedList<IInputOutputInstance>();
		
		for (IAlgoInstance ai: workflow.getAlgoInstances()) {
			
			for (IInputOutputInstance out: ai.getOutputInstances()) {
				// only focus on the outputs which are not used
				if (!out.getConnections().isEmpty())
					continue;
				
				if (AppendToTableAlgo.INPUT_ANYTHING.getType().compliantWith(out.getMeta().getType()))
					res.add(out);
			}
		}
		
		return res;
		
	}
	
	public static IGenlabWorkflowInstance createLoopForAlgos(
			IGenlabWorkflowInstance originalWorkflow,
			Collection<IAlgoInstance> selectedAlgos
			) {
		
		// process parameters
		// ... if no algo is selected, they are all selected
		if (selectedAlgos == null) 
			selectedAlgos = new LinkedList<IAlgoInstance>();
		if (selectedAlgos.isEmpty()) {
			selectedAlgos.addAll(originalWorkflow.getAlgoInstances());
		}
		
		// create the target workflow
		final String postfix = " loop "+System.currentTimeMillis();
		IGenlabWorkflowInstance workflowRes = GenlabFactory.createWorkflow(
				originalWorkflow.getProject(),
				originalWorkflow.getName()+postfix, 
				"automatically created for loop", 
				originalWorkflow.getRelativePath()+"/"+originalWorkflow.getFilename()+postfix+".glw"
				);
		
		// inside this workflow, let's create a NSGA2 instance ! 
		IAlgoContainerInstance loopInstance = null;
		{
			LoopForAlgo algo = new LoopForAlgo();
			loopInstance = (IAlgoContainerInstance)algo.createInstance(workflowRes);
			loopInstance.setContainer(workflowRes);
			workflowRes.addAlgoInstance(loopInstance);
		}
		
		// copy all the selected elements from the original workflow to the target one
		Map<IAlgoInstance,IAlgoInstance> original2copy = GenlabWorkflow.copyAlgosFromWorkflowToWorkflow(
				originalWorkflow, 
				GenlabWorkflow.completeSelectionWithChildren(selectedAlgos), 
				workflowRes, 
				loopInstance,
				true
				);

		// add a table aggregation
		final Collection<IInputOutputInstance> freeOutputsToAggregate = detectOutputsForAggregationOfResults(workflowRes);
		if (!freeOutputsToAggregate.isEmpty()) {
			// create the table aggregation algo
			AppendToTableAlgo algo = new AppendToTableAlgo();
			IAlgoInstance appendInstance = algo.createInstance(workflowRes);
			workflowRes.addAlgoInstance(appendInstance);
			
			// create all the links
			final IInputOutputInstance in = appendInstance.getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING);
			for (IInputOutputInstance out: freeOutputsToAggregate) {
				workflowRes.connect(out, in);
			}
			
		}
		
		return workflowRes;
	}

	
	private CreateOtherWorkflows() {
		
	}

}
