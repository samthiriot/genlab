package genlab.core.model.meta.basics.algos;

import genlab.core.model.instance.AlgoInstanceWithParametersDependantToConnections;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.parameters.ListParameter;
import genlab.core.usermachineinteraction.GLLogger;

@SuppressWarnings("serial")
public class StatisticsOfColumnInstance extends AlgoInstanceWithParametersDependantToConnections  {

	public ListParameter PARAM_COLUMN;
	
	public StatisticsOfColumnInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
	}

	public StatisticsOfColumnInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
	}

	@Override
	protected void declareLocalParameters() {
		
		if (PARAM_COLUMN == null)
			PARAM_COLUMN = new ListParameter(
					"param_column", 
					"column", 
					"column for which to compute the average"
					);
		declareParameter(PARAM_COLUMN);

	}

	@Override
	protected void adaptParametersToInputs() {
		
		try {
			IGenlabTable table = (IGenlabTable)getPrecomputedValueForInput(StatisticsOfColumnAlgo.INPUT_TABLE);
			
			PARAM_COLUMN.setItems(table.getColumnsId());
		
			System.err.println("detected columns "+table.getColumnsId());
			
		} catch (Exception e) {
			GLLogger.warnTech("unable to prepare the values for parameter "+PARAM_COLUMN, getClass());
		}
	}

	
	
}
