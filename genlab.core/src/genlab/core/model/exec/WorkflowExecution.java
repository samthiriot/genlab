package genlab.core.model.exec;

import genlab.core.commons.GenLabException;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.exec.client.ComputationNodes;
import genlab.core.exec.client.Runner;
import genlab.core.model.DebugGraphviz;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
			
			instance2execution = new HashMap<IAlgoInstance, IAlgoExecution>(workflowInstance.getAlgoInstances().size());
	
			// first create execution for each direct sub algo
			
			for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {

				if (sub.getContainer() != null && sub.getContainer() != workflowInstance) {
					// ignored ! this is contained in something, and this something will be in charge of creating execs
					
				} else {
					
					if (sub.isDisabled()) {
						messages.warnUser("the algorithm "+sub.getName()+" is disabled, so it will not be run", getClass()); 
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
			}
			
			// special case of container algos: for each container exec, assume it represents the 
			// exec instance for each children.
			// so all the algos interested in the results of containers' child
			// will actually listen for the container
			for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
	
				if (sub.getContainer() != null && sub.getContainer() != workflowInstance) {
					// ignored ! this is contained in something, and this something 
					instance2execution.put(
							sub, 
							instance2execution.get(sub.getContainer())
							);
				} 
			}
			
			// display TODO debug
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
				
				// maybe we did not created this one ? (notably is disabled)
				if (subExec == null)
					continue;
				
				if (sub.getContainer() != null && sub.getContainer() != workflowInstance) {
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
				if (exec.getAlgoInstance().getContainer() != null  && (exec.getAlgoInstance().getContainer() != workflowInstance))
					continue;
		
				messages.traceTech("init links for "+exec, getClass());
				exec.initInputs(unmodifiableMap);
			}
			
			// now (and only now), call addSubtask on each task
			// this will also register the task to the runner !
			for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
				
				IAlgoExecution subExec = instance2execution.get(sub);
				
				// maybe we did not created this one ? (notably if disabled)
				if (subExec == null)
					continue;
				
				if (sub.getContainer() != null && sub.getContainer() != workflowInstance) {
					// container will manage that itself.
					
					/* TODO for container exec !
					// tasks with a container will have a container task
					IContainerTask subExecContainer = (IContainerTask)instance2execution.get(sub.getContainer());
					subExec.setParent(subExecContainer);
					subExecContainer.addTask(subExec);
					*/
				} else {
					this.addTask(subExec);
				}
				
			}
			
			// now propagate ranks, and so detect loops
			for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
				
				if (sub.getAllIncomingConnections().isEmpty()) {
					IAlgoExecution subExec = instance2execution.get(sub);
					// maybe we did not created this one ? (notably if disabled)
					if (subExec == null)
						continue;
					subExec.propagateRank(1, new HashSet<ITask>());
				}

			}

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
/*

	@Override
	public void run() {
		
		// start
		messages.traceTech("starting the execution of worklow "+workflowInstance, getClass());
		progress.setComputationState(ComputationState.STARTED);

		// create subtasks
		
		ExecutionHooks.singleton.notifyParentTaskAdded(exec);
	
		// TODO ??? TasksManager.singleton.notifyListenersOfTaskAdded(task);
		
		Runner r = LocalComputationNode.getSingleton().getRunner();
		
		//r = new Runner(exec, progress, , this);
		r.addTasks(instance2execution.values());
		
		DebugGraphviz.exportExecutionNetwork("/tmp/testBefore.dot", this);

		r.run();
		
		// plan the execution time
		int totalTimeRequired = 0;
		// TODO how to compute this ? 
		// probably: already create the exec for each child, so we can use it.
		// and even, use a progress container instead of our own.
		
		
		// TODO
		
		// build the execution graph
		DebugGraphviz.exportExecutionNetwork("/tmp/test.dot", this);

		exec.displayTechnicalInformationsOnMessages();
		
		
	}
*/



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
