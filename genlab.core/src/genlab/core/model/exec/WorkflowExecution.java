package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.exec.Runner;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkflowExecution extends AbstractAlgoExecution {

	private final IGenlabWorkflowInstance workflowInstance;
	protected Map<IAlgoInstance, IAlgoExecution> instance2execution = null;


	// the roots that can be ran independantly
	protected Set<IAlgoInstance> roots = null;

	
	
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
		roots = new HashSet<IAlgoInstance>();
		
		// first create execution for each sub algo
		for (IAlgoInstance sub : workflowInstance.getAlgoInstances()) {
		
			GLLogger.traceTech("creating an execution task for subalgo "+sub, getClass());
			instance2execution.put(
					sub, 
					sub.execute(exec)
					);
			
		}
		
		// now init links
		Map<IAlgoInstance, IAlgoExecution> unmodifiableMap = Collections.unmodifiableMap(instance2execution);
		
		for (IAlgoExecution exec : instance2execution.values()) {
			
			GLLogger.traceTech("init links for "+exec, getClass());
			exec.initInputs(unmodifiableMap);
		}
		
		
		
	}

	@Override
	public void run() {
		
		GLLogger.traceTech("starting the execution of worklow "+workflowInstance, getClass());

		Runner r = new Runner(exec, progress, instance2execution.values());
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






}
