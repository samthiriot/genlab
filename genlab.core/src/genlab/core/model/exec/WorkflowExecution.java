package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.exec.Runner;
import genlab.core.model.DebugGraphviz;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class WorkflowExecution 
						extends AbstractAlgoExecution 
						implements 	IContainerTask, 
									IComputationProgressSimpleListener{

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
		
		messages.traceTech("preparing the execution of worklow "+workflowInstance, getClass());
		
		instance2execution = new HashMap<IAlgoInstance, IAlgoExecution>(workflowInstance.getAlgoInstances().size());

		// first create execution for each direct sub algo
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {

			if (sub.getContainer() != null && sub.getContainer() != this) {
				// ignored ! this is contained in something, and this something will be in charge of creating execs
				
			} else {
				messages.traceTech("creating the execution task for algo "+sub, getClass());
				IAlgoExecution subExec = sub.execute(exec);
				
				if (subExec == null)
					throw new ProgramException("an algorithm was unable to prepare an execution "+sub);
				
				instance2execution.put(
						sub, 
						subExec
						);
			}
		}
		
		// special case of container algos: for each container exec, assume it represents the 
		// exec instance for each children.
		// so all the algos interested in the results of containers' child
		// will actually listen for the container
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {

			if (sub.getContainer() != null && sub.getContainer() != this) {
				// ignored ! this is contained in something, and this something 
				instance2execution.put(
						sub, 
						instance2execution.get(sub.getContainer())
						);
			} 
		}
		
		// display
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
			System.err.print(sub.getName());
			
			IAlgoExecution exec = instance2execution.get(sub);
			if (exec != null) {
				System.err.print("\t->\t");
				System.err.print(exec.getName());
			} else {
				System.err.print("\t->\t???");
			}
			
			System.err.println();
		}
		
		// now add the parent relationship to each task 
		// (sometimes the parent is a container algo; 
		//  else it is the workflow that, that is "this")
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
			
			IAlgoExecution subExec = instance2execution.get(sub);
			
			if (sub.getContainer() != null) {
				// container will manage that itself.
				
				/* TODO for container exec !
				// tasks with a container will have a container task
				IContainerTask subExecContainer = (IContainerTask)instance2execution.get(sub.getContainer());
				subExec.setParent(subExecContainer);
				subExecContainer.addTask(subExec);
				*/
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
		for (IAlgoExecution exec : new HashSet<IAlgoExecution>(instance2execution.values())) {
			
			// do not create links for the algos contained elsewhere (this is the responsability of the container)
			if (exec.getAlgoInstance().getContainer() != null)
				continue;
	
			
			messages.traceTech("init links for "+exec, getClass());
			exec.initInputs(unmodifiableMap);
		}
		
		
	}

	@Override
	public void run() {
		
		messages.traceTech("starting the execution of worklow "+workflowInstance, getClass());

		ExecutionHooks.singleton.notifyParentTaskAdded(exec);
	
		r = new Runner(exec, progress, instance2execution.values(), this);
		DebugGraphviz.exportExecutionNetwork("/tmp/test.dot", this);

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
		
		messages.debugTech("attempts to cancel this execution", getClass());
		
		r.cancel();
	}



	@Override
	public void kill() {
		if (r == null)
			return;
		
		messages.debugTech("attempts to kill this execution", getClass());
		
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



	@Override
	public void collectEntities(
			Set<IAlgoExecution> execs,
			Set<ConnectionExec> connections
			) {
		
		super.collectEntities(execs, connections);
		
		// also collect my subentities
		for (IAlgoExecution e: instance2execution.values()) {
			e.collectEntities(execs, connections);
		}
		
		
	}


}
