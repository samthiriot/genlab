package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.exec.AbstractAlgoReduceExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.IReduceAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

import java.util.HashMap;
import java.util.Map;

/**
 * At creation, initialize a table with all the input connections as columns.
 * During execution, it will store the result of each parrallel exec as a row. 
 * When this one runs, it will just export the table as its result.
 * 
 * @author Samuel Thiriot
 *
 */
public class AppendToTableExecutable extends AbstractAlgoReduceExecution {

	protected IGenlabTable outputTable = null;

	protected Map<IConnection, Integer> connection2colId;
	protected Map<IAlgoExecution, Integer> execId2rowId;
	
	public AppendToTableExecutable(IExecution exec,
			IAlgoInstance algoInst) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		messages.debugTech("init table", getClass());
		outputTable = new GenlabTable();
		
		// mapping between inputs and column ids: filled right now
		{
			
			IInputOutputInstance inputInstance = algoInst.getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING);
			connection2colId = new HashMap<IConnection, Integer>(inputInstance.getConnections().size());
			for (IConnection c: inputInstance.getConnections()) {
				
				int columnIdx = outputTable.declareColumn(c.getFrom().getName());
				
				connection2colId.put(c, columnIdx);
				
			}
		}
		
		if (outputTable == null) { 
			outputTable = new GenlabTable();
		}
		
		
		// mapping between exec if and row id: start empty
		execId2rowId = new HashMap<IAlgoExecution, Integer>();
		
		
	}

	

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {

		messages.traceTech("starting", getClass());
		progress.setComputationState(ComputationState.STARTED);
		
		// define result
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		result.setResult(AppendToTableAlgo.OUTPUT_TABLE, outputTable);
		setResult(result);
		
		// TODO we could add a hook there for post processing (why not ?)
		
		// notify end
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}


	@Override
	public void reset() {
		super.reset();
		
		// outputTable.clear // TODO clear table when reset !
		execId2rowId.clear();
		exec2finishStatus.clear();
	}



	@Override
	protected void processEndOfExecution(IAlgoExecution executionRun) {

		// means one "row" is finished
		
		final Integer rowId = execId2rowId.get(executionRun);
		if (rowId == null)
			 throw new ProgramException("unknown executable: it should have been defined previously");
		
		// check wether the row id is filled or not
		/* don't, as the connections are ran in a quiet asynnchronous way
		Object[] values = outputTable.getRow(rowId);
		for (Object value : values) {
			if (value == null)
				messages.warnUser("one exec finished, but somes values are still undefined", getClass());
		}
		*/
		
	}

	@Override
	protected void prepareToProcessNovelInputsFor(IAlgoExecution executionRun) {
		
		if (execId2rowId.containsKey(executionRun))
			 throw new ProgramException("executable is already defined: "+executionRun);
	
		messages.traceTech("creating a row for execution: "+executionRun, getClass());
		
		Integer rowId = outputTable.addRow();
		execId2rowId.put(executionRun, rowId);
			
	}



	@Override
	protected void processNovelInputs(IAlgoExecution executionRun, IConnectionExecution connectionExec, Object value) {
		
		final Integer rowId = execId2rowId.get(executionRun);
		if (rowId == null)
			 throw new ProgramException("unknown executable: it should have been defined previously");
		
		final Integer columnId = connection2colId.get(connectionExec.getConnection());
		if (columnId == null)
			 throw new ProgramException("unknown column "+connectionExec.getConnection().getFrom().getName()+": it should have been defined previously");
		
		// check wether the row id is filled or not
		outputTable.setValue(rowId, columnId, value);
		
		
	}



	

}