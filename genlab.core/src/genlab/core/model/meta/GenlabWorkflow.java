package genlab.core.model.meta;

import genlab.core.commons.NotImplementedException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.WorkflowExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.Set;

public class GenlabWorkflow implements IGenlabWorkflow {

	public static final String ALGO_CATEGORY = "containers";
	
	public GenlabWorkflow() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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

}
