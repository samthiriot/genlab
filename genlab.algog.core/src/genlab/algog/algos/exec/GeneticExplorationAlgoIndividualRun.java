package genlab.algog.algos.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.Map;

/**
 * A run of only one individual
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationAlgoIndividualRun extends AbstractContainerExecution {

	private Collection<IAlgoInstance> algoInstancesToRun;
	
	public GeneticExplorationAlgoIndividualRun(
			
			// standard genlab stuff
			IExecution exec, 
			IAlgoContainerInstance algoInst,
			
			Collection<IAlgoInstance> algoInstancesToRun
			) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		
		
		
	}

	
	
	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		// TODO Auto-generated method stub
		super.notifyInputAvailable(to);
	}



	@Override
	public void initInputs(Map<IAlgoInstance, IAlgoExecution> instance2exec) {

		// don't. 
		// super.initInputs(instance2exec);
		
		// create fake executable connections for the runners
		

		// prepare the connections to outside
		// ... all the connections coming from outside actually come from me
		for (IConnection c: algoInst.getConnectionsComingFromOutside()) {
			instance2execForSubtasks.put(
					c.getFrom().getAlgoInstance(), 
					this
					);
		}
		
		
		// create execs
		messages.debugTech("creating the exec for children...", getClass());
		for (IAlgoInstance aiChild : algoInstancesToRun) {
			IAlgoExecution aiExecChild = aiChild.execute(this.getExecution());
			
			aiExecChild.setParent(this);
			addTask(aiExecChild);
			instance2execForSubtasks.put(aiChild, aiExecChild);
		}
		
		for (IAlgoInstance aiChild : algoInstancesToRun) {
			IAlgoExecution aiExecChild = instance2execForSubtasks.get(aiChild);
			aiExecChild.initInputs(instance2exec);
		}
		
		
	}



	@Override
	public void run() {

				
		// set the initial values of each child
		
	}

}
