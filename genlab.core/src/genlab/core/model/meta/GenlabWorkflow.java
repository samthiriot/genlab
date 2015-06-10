package genlab.core.model.meta;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.WorkflowExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;

/**
 * TODO later, we should declare it using the extension
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabWorkflow implements IGenlabWorkflow {

	public static final String ALGO_CATEGORY = "containers";
	
	public static GenlabWorkflow singleton = new GenlabWorkflow();
	
	public GenlabWorkflow() {
	}

	@Override
	public String getName() {
		return "genlab workflow";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInputOutput> getInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		throw new NotImplementedException();
	}

	@Override
	public IAlgoExecution createExec(IExecution exec, AlgoInstance algoInstance) {
		return new WorkflowExecution(exec, (IGenlabWorkflowInstance) algoInstance);
	}

	@Override
	public String getCategoryId() {
		return ALGO_CATEGORY;
	}

	@Override
	public String getId() {
		return "meta.workflow";
	}

	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Parameter<?>> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parameter<?> getParameter(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasParameter(String id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getImagePath16X16() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		return false;
	}

	@Override
	public boolean canContain(IAlgo algo) {
		return true;
	}

	@Override
	public String getImagePath32X32() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImagePath64X64() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImagePathBig() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<IAlgo, Integer> recommandAlgosContained() {

		// we recommand nothing special. It will come back to the fallback (constants)
		return Collections.EMPTY_MAP;
	}

	@Override
	public boolean canBeContainedInto(IAlgoContainer algoContainer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInputOutput<?> getInputInstanceForId(String inputId) {
		return null;
	}

	@Override
	public IInputOutput<?> getOutputInstanceForId(String outputId) {
		return null;
	}


	public static Collection<IAlgoInstance> completeSelectionWithChildren(Collection<IAlgoInstance> selected) {
		
		// we at least consider all the ones included there
		Set<IAlgoInstance> res = new HashSet<IAlgoInstance>();
		
		Set<IAlgoInstance> toProcess = new HashSet<IAlgoInstance>();
		toProcess.addAll(selected);
		
		while (!toProcess.isEmpty()) {
			
			// pick up one
			Iterator<IAlgoInstance> it = toProcess.iterator();
			IAlgoInstance ai = it.next();;
			it.remove();
			
			// add it
			res.add(ai);
			
			// process it
			if (ai instanceof IAlgoContainerInstance) {
				IAlgoContainerInstance aic = (IAlgoContainerInstance)ai;
				toProcess.addAll(aic.getChildren());
			}
		}
		
		return res;
	}
	
	/**
	 * From a workflow, creates a copy of selected algos inside a target algo, 
	 * and returns the map between previous and present algos
	 * @param originalWorkflow
	 * @param selectedAlgos
	 * @param targetWorkflow
	 * @param container
	 * @return
	 */
	public static Map<IAlgoInstance,IAlgoInstance> copyAlgosFromWorkflowToWorkflow(
			IGenlabWorkflowInstance originalWorkflow,
			Collection<IAlgoInstance> selectedAlgos,
			IGenlabWorkflowInstance targetWorkflow,
			IAlgoContainerInstance container,
			boolean keepConstantsInWorkflow
			) {
		
		// start by duplicating the algo instances
		Map<IAlgoInstance,IAlgoInstance> original2copy = new HashMap<IAlgoInstance, IAlgoInstance>(selectedAlgos.size());
		
		// there should be an order: a child should be processed only when its parent was processed.
		// we do it dumbly: if we cannot process one algo now, we will retry later. 
		Set<IAlgoInstance> toProcess = new HashSet<IAlgoInstance>(selectedAlgos);
		Set<IAlgoInstance> toProcessAgain = new HashSet<IAlgoInstance>();
		
		do {
			System.err.println("should process "+toProcess.size()+" algos");
			while (!toProcess.isEmpty()) {
				
				// pick up one
				Iterator<IAlgoInstance> it = toProcess.iterator();
				IAlgoInstance ai = it.next();;
				it.remove();
				
				if (ai.getContainer() != null && !original2copy.containsKey(ai.getContainer())) {
					// we should retry later
					toProcessAgain.add(ai);
				} else {
					
					IAlgoInstance resultInstance = ai.cloneInContext(targetWorkflow);

					// process it !
					if ( 
							// there is a container in which copy everything
							(container != null) &&
							// and this is not a constant or the user asked us to also project them inside the container
							(!keepConstantsInWorkflow || !(resultInstance.getAlgo() instanceof IConstantAlgo))
							) {
							
							// is it a container which was mapped ?
							if (ai.getContainer() != null) {
								// there was an original container.
								// let's retrieve the copy of this container and set it as a container
								IAlgoContainerInstance aiContainer = (IAlgoContainerInstance) original2copy.get(ai.getContainer());
								resultInstance.setContainer(aiContainer);
								aiContainer.addChildren(resultInstance);
							} else {
								// else reuse the parent container passed as parameter
								resultInstance.setContainer(container);
								container.addChildren(resultInstance);
							}
							
					} /*else {
						resultInstance.setContainer(targetWorkflow);
					}*/
					targetWorkflow.addAlgoInstance(resultInstance);
						
						
					original2copy.put(ai, resultInstance);
				}
			}
			toProcess.addAll(toProcessAgain);
			toProcessAgain.clear();
		} while (!toProcess.isEmpty());
				
		
		// and add the connections
		for (IAlgoInstance original: original2copy.keySet()) {
			
			// add the input connections
			for (IConnection cInOrig : original.getAllIncomingConnections()) {
				
				IAlgoInstance fromOrigin = cInOrig.getFrom().getAlgoInstance();
				IAlgoInstance fromCopy = original2copy.get(fromOrigin);
				if (fromCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IAlgoInstance toOrigin = cInOrig.getTo().getAlgoInstance();
				IAlgoInstance toCopy = original2copy.get(toOrigin);
				if (toCopy == null) {
					// no copy; this one was not copied, so don't manage this connection
					continue;
				}
				
				IInputOutputInstance outputInstanceCopy = fromCopy.getOutputInstanceForOutput(cInOrig.getFrom().getMeta());
				if (outputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for output "+cInOrig.getFrom().getMeta());
				
				IInputOutputInstance inputInstanceCopy = toCopy.getInputInstanceForInput(cInOrig.getTo().getMeta());
				if (inputInstanceCopy == null)
					throw new ProgramException("unable to find the copy for input "+cInOrig.getTo().getMeta());
				
				IConnection cCopy = targetWorkflow.connect(outputInstanceCopy, inputInstanceCopy);
			
			}

		}
        
		return original2copy;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
	

}
