package genlab.core.model.meta.basics.algos;

import java.util.HashSet;
import java.util.Set;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.TextMessageFromAlgoInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.usermachineinteraction.MessageLevel;

@SuppressWarnings("serial")
public class AppendToTableInstance extends AlgoInstance {

	public AppendToTableInstance(IAlgo algo, IGenlabWorkflowInstance workflow,
			String id) {
		super(algo, workflow, id);
	}

	public AppendToTableInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}
	

	@Override
	public Object getPrecomputedValueForOutput(IInputOutput<?> output) {

		// the precomputed values of a table depend on each input received
		IInputOutputInstance inputInstance = getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING);
		
		// we create a table with just the columns titles declared
		IGenlabTable tablePrecomputed = new GenlabTable();
		
		// append each column
		for (IConnection c: inputInstance.getConnections()) {
			
			tablePrecomputed.declareColumn(c.getFrom().getName());
			
		}
		
		return tablePrecomputed;
		
	}

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		super.checkForRun(res);
		
		// ensure there are not similar names in the column ? 
		try {
			IInputOutputInstance inputInstance = getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING);
			Set<String> columnNames = new HashSet<String>(); 
			for (IConnection c: inputInstance.getConnections()) {
				if (!columnNames.add(c.getFrom().getName())) {
					res.messages.add(new TextMessageFromAlgoInstance(
							this, 
							MessageLevel.ERROR, 
							"several inputs of this table have for name "+c.getFrom().getName()+"; please rename one of the algorithms so there will be no conflict anymore"
							)
					);
				}	
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	
}
