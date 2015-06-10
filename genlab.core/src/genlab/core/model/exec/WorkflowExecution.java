package genlab.core.model.exec;

import genlab.core.commons.GenLabException;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;

import java.util.Collection;
import java.util.Map;

public class WorkflowExecution 
						extends AbstractContainerExecution 
						implements 	IContainerTask, 
									IComputationProgressSimpleListener{

	private final IGenlabWorkflowInstance workflowInstance;
	protected Map<IAlgoInstance, IAlgoExecution> instance2execution = null;

	
	public WorkflowExecution(IExecution exec, IGenlabWorkflowInstance workflowInstance) {
		super(
				exec, 
				workflowInstance, 
				new ComputationProgressWithSteps()
				);
		
		this.workflowInstance = workflowInstance;

		initTasks();
		
		// till now, a workflow is always ready for exec
		progress.setComputationState(ComputationState.STARTED);
		
	}
	
	
	
	protected void initTasks() {
		
		try {
			messages.traceTech("preparing the execution of worklow "+workflowInstance, getClass());
			
			final Collection<IAlgoInstance> allAlgoInstances = workflowInstance.getAlgoInstances();
			
			// create execution for children
			instance2execution = initCreateExecutionsForSubAlgos(allAlgoInstances);
			
			// consider children of children as children
			initContainerAlgosChildren(allAlgoInstances, instance2execution);
					
			//initDebugExec(allAlgoInstances, instance2execution);
			
			initParentForSubtasks(allAlgoInstances, instance2execution);
			
			// now init links
			initLinksWithSubExec(instance2execution);
		
			initAddTasksAsSubtasks(allAlgoInstances, instance2execution);
			
			initPropagateRanks(allAlgoInstances, instance2execution);
			
		} catch (GenLabException e) {
			messages.errorTech("an error occured while initializing the subtasks: "+e.getLocalizedMessage(), this.getClass());
			e.printStackTrace();
			throw e;
		}
	}
	

	@Override
	public void run() {
		
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
		
		messages.debugTech("starting this iteration", getClass());
		
		//progress.setComputationState(ComputationState.STARTED);
	

	
		// nothing to do there; 
		// the subtasks are ready as well
		
		// so they should run, and our state will switch to finished ok/cancel/failure depending to their state
		
		// TODO later, we should manage variables
		
		// done.
		messages.debugTech("initialized all the children inputs; now waiting for them to finish", getClass());

	
		// we DO NOT set the progress to finished, because this container task will only be assumed to be finished once its childrne will be finiched as well
		
		
	}



	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("workflow execution: ");
		sb.append(workflowInstance.getName());
		
		return sb.toString();
	}


	@Override
	public void clean() {
	
		// clean lodal data
		instance2execution.clear();
		instance2execution = null;
		
		// super clean
		super.clean();
	}



	@Override
	protected void hookContainerExecutionFinished(ComputationState state) {

		switch (state) {
		case FINISHED_OK:
			messages.infoUser("successfull end of execution for workflow "+this.getAlgoInstance().getName(), getClass());
			break;
		case FINISHED_CANCEL:
			messages.infoUser("end of execution for workflow "+this.getAlgoInstance().getName()+" (cancel)", getClass());
			break;
		case FINISHED_FAILURE:
			messages.errorUser("end of execution for workflow "+this.getAlgoInstance().getName()+" (failure)", getClass());
			// TODO Display exception
			break;
		default: 
			throw new ProgramException("can not finish in such a state: "+state);
		}
			
			
	}



	@Override
	public boolean containedInto(IAlgoExecution other) {
		return false;
	}

}
