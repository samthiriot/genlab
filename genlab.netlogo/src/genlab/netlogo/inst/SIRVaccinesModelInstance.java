package genlab.netlogo.inst;

import genlab.core.model.instance.AlgoInstanceWithParametersDependantToConnections;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.ListParameter;
import genlab.netlogo.algos.SIRVaccinesModelAlgo;

public class SIRVaccinesModelInstance extends
		AlgoInstanceWithParametersDependantToConnections {

	public ListParameter PARAM_COLUMN_BETWEENESS;
	
	public SIRVaccinesModelInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public SIRVaccinesModelInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	@Override
	protected void declareLocalParameters() {
		if (PARAM_COLUMN_BETWEENESS == null)
			PARAM_COLUMN_BETWEENESS = new ListParameter(
					"param_column_betweeness", 
					"attribute for betweeness", 
					"attribute to read for the betweeness"
					);
		declareParameter(PARAM_COLUMN_BETWEENESS);
	}

	@Override
	protected void adaptParametersToInputs() {

		// TODO ! TODO ! IGenlabGraph g = (IGenlabGraph)getPrecomputedValueForInput(SIRVaccinesModelAlgo.INPUT_GRAPH);
		
	}

}
