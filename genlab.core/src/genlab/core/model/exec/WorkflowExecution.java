package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.IExecutionTask;
import genlab.core.exec.ITask;
import genlab.core.exec.Runner;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class WorkflowExecution extends AbstractAlgoExecution implements IContainerTask, IComputationProgressSimpleListener {

	private final IGenlabWorkflowInstance workflowInstance;
	protected Map<IAlgoInstance, IAlgoExecution> instance2execution = null;

	protected Runner r = null;
	
	private Collection<ITask> subTasks = new LinkedList<ITask>();
	
	
	public WorkflowExecution(IExecution exec, IGenlabWorkflowInstance workflowInstance) {
		super(
				exec, 
				workflowInstance, 
				new ComputationProgressWithSteps()
				);
		
		this.workflowInstance = workflowInstance;

		initTasks();
	}
	

	
	protected void initTasks() {
		
		GLLogger.traceTech("preparing the execution of worklow "+workflowInstance, getClass());
		
		instance2execution = new HashMap<IAlgoInstance, IAlgoExecution>(workflowInstance.getAlgoInstances().size());

		// first create execution for each sub algo
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
						
			GLLogger.traceTech("creating the execution task for algo "+sub, getClass());
			IAlgoExecution subExec = sub.execute(exec);
			
			if (subExec == null)
				throw new ProgramException("an algorithm was unable to prepare an execution "+sub);
			
			instance2execution.put(
					sub, 
					subExec
					);
			
		}
		
		// now add the parent relationship to each task 
		// (something the parent is a container algo; 
		//  else it is the workflow that, that is "this")
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
			
			IAlgoExecution subExec = instance2execution.get(sub);
			
			if (sub.getContainer() != null) {
				// tasks with a container will have a container task
				IContainerTask subExecContainer = (IContainerTask)instance2execution.get(sub.getContainer());
				subExec.setParent(subExecContainer);
				subExecContainer.addTask(subExec);
			} else {
				// standard: 
				subExec.setParent(this);
				this.addTask(subExec);
			}
			
		}
		
		// then create the inter dependancies between the tasks
		/* TODO USELESS ? probably done in executable connections !
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
			
			IAlgoExecution exec = instance2execution.get(sub);
			
			for (IInputOutputInstance in : sub.getInputInstances()) {
				for (IConnection c : in.getConnections()) {
					IAlgoInstance aiFrom = c.getFrom().getAlgoInstance();
					IAlgoExecution execFrom = instance2execution.get(aiFrom);
					exec.addPrerequire(execFrom);
					
				}
			}
			
		}*/
		
		
		// now init links
		
		// call each algo and ask him to create its executable connections
		Map<IAlgoInstance, IAlgoExecution> unmodifiableMap = Collections.unmodifiableMap(instance2execution);
		for (IAlgoExecution exec : instance2execution.values()) {
			
			GLLogger.traceTech("init links for "+exec, getClass());
			exec.initInputs(unmodifiableMap);
		}
		
		
	}

	@Override
	public void run() {
		
		GLLogger.traceTech("starting the execution of worklow "+workflowInstance, getClass());

		
		r = new Runner(exec, progress, instance2execution.values(), this);
		r.run();
		
		// plan the execution time
		int totalTimeRequired = 0;
		// TODO how to compute this ? 
		// probably: already create the exec for each child, so we can use it.
		// and even, use a progress container instead of our own.
		
		/*
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
			totalTimeRequired += workflowInstance.get
		}
		*/
		
		
		// TODO
		
		// build the execution graph
		
		
		
	}



	@Override
	public void cancel() {
		if (r == null)
				return;
		
		GLLogger.debugTech("attempts to cancel this execution", getClass());
		
		r.cancel();
	}



	@Override
	public void kill() {
		if (r == null)
			return;
		
		GLLogger.debugTech("attempts to kill this execution", getClass());
		
		r.kill();
	}



	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("workflow execution: ");
		sb.append(workflowInstance.getName());
		
		return sb.toString();
	}



	@Override
	public long getTimeout() {
		return -1;
	}


	@Override
	public void addTask(ITask t) {
		if (!subTasks.contains(t)) {
			subTasks.add(t);
			t.getProgress().addListener(this);
		}
	}


	@Override
	public Collection<ITask> getTasks() {
		return subTasks;
	}

	protected boolean allSubtasksSucceed() {
		for (ITask t: subTasks) {
			if (t.getProgress().getComputationState() != ComputationState.FINISHED_OK)
				return false;
		}
		return true;
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		switch (progress.getComputationState()) {
		case FINISHED_CANCEL:
		case FINISHED_FAILURE:
			// if one of our child fails or is canceled, the whole is.
			this.progress.setComputationState(progress.getComputationState());
			// and we stop listening it.
			progress.removeListener(this);
			break;
		case FINISHED_OK:
			// stop listen for this one
			progress.removeListener(this);
			// if one of our childs succeeds... we still have to wait for all of them
			if (allSubtasksSucceed()) {
				this.progress.setComputationState(ComputationState.FINISHED_OK);
			}
			break;
		default:
			// do nothing :-)
		}
	}



	@Override
	public int getThreadsUsed() {
		return 0;
	}


}
