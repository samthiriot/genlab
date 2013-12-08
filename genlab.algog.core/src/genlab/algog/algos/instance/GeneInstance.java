package genlab.algog.algos.instance;

import java.util.Map;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.Parameter;

public class GeneInstance extends AlgoInstance {

	public GeneInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public GeneInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		super.checkForRun(res);
		
		boolean paramAtDefault = false;
		for (Parameter<?> param : getAlgo().getParameters()) {
			
			if (isParameterAtDefaultValue(param.getId())) {
				res.messages.warnUser("for gene "+name+", the parameter "+param.getName()+" was not tuned.", getClass());
				paramAtDefault = true;
			}
		}
		if (paramAtDefault)
			res.messages.warnUser("the parameters of genes have a strong impact on the efficiency of genetic algorithms. You should use these parameters to restrict the search space as much as possible.", getClass());
		
	}

	
}
