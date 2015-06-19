package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoReduceExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IAlgoExecutionOneshot;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * At creation, initialize a table with all the input connections as columns.
 * During execution, it will store the result of each parrallel exec as a row. 
 * When this one runs, it will just export the table as its result.
 * 
 * TODO case of imbricated loops ? 
 * 
 * @author Samuel Thiriot
 *
 */
public class AppendToTableExecutable extends AbstractAlgoReduceExecution implements IAlgoExecutionOneshot {

	protected IGenlabTable outputTable = null;

	protected Map<IConnection, Integer> connection2colIdx;
	protected Map<IAlgoExecution, Integer> execId2rowId;
	
	public AppendToTableExecutable(IExecution exec,
			IAlgoInstance algoInst) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		messages.debugTech("init table", getClass());
		outputTable = new GenlabTable();
		
		// mapping between inputs and column ids: filled right now
		{
			
			IInputOutputInstance inputInstance = algoInst.getInputInstanceForInput(AppendToTableAlgo.INPUT_ANYTHING);
			connection2colIdx = new HashMap<IConnection, Integer>(inputInstance.getConnections().size());
			for (IConnection c: inputInstance.getConnections()) {
				
				int columnIdx = outputTable.declareColumn(c.getFrom().getName());
				
				connection2colIdx.put(c, columnIdx);
				
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
		
		// where do we come from ?
		try {
			throw new ProgramException("where are we ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("we are in thread: "+Thread.currentThread().getName());
		
		// maybe we did not receive all the values yet ?
		completeValues();
		
		// define result
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		result.setResult(AppendToTableAlgo.OUTPUT_TABLE, outputTable);
		setResult(result);
		
		// TODO we could add a hook there for post processing (why not ?)
		
		// notify end
		messages.traceTech("finished !", getClass());
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
	
	/**
	 * As long as connections come from Container algos, then we will have received the values
	 * with an execution context.
	 * But, we may also receive some values from non "to be reduced" algos. In this case, 
	 * we just retrieve their values in the standard way.
	 * 
	 */
	protected void completeValues() {
		
		//outputTable.setValue(rowId, columnIdx, value);
		
		messages.debugTech("attempting to fill the columns which were not pushed by containers...", getClass());
		
		// defined which columns are always empty
		Collection<Integer> emptyColumns = outputTable.getEmptyColumnsIndexes();
		
		if (emptyColumns.isEmpty()) {
			messages.debugTech("all columns are already availabe.", getClass());
			return;
		} else {
		}
		messages.debugTech("attempting to fill columns: "+emptyColumns.toString(), getClass());
		
		// retrieve data for all these columns
		for (Map.Entry<IConnection,Integer> connex2idx : connection2colIdx.entrySet()) {
			
			if (!emptyColumns.contains(connex2idx.getValue()))
				// only focus of empty columns
				continue;
			
			// this column is empty; let's attempt to retrieve the value for it !
			IConnection inputConnection = connex2idx.getKey();
			IConnectionExecution cEx = getExecutableConnectionForConnection(inputConnection);
			if (cEx == null)
				throw new ProgramException("no connection exec for this input connection: "+inputConnection);
			Object value = cEx.getValue();
			if (value == null)
				throw new ProgramException("no value provided for the input connection: "+inputConnection);
			
			outputTable.fillColumn(connex2idx.getValue(), value);
			
		}
		
		// end of all
		
		
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
		
		final Integer columnId = connection2colIdx.get(connectionExec.getConnection());
		if (columnId == null)
			 throw new ProgramException("unknown column "+connectionExec.getConnection().getFrom().getName()+": it should have been defined previously");
		
		// check wether the row id is filled or not
		outputTable.setValue(rowId, columnId, value);
		
		
	}



	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		
		// we have to suppose this comes from a constant; else we would 
		// not be able to attach it to a given context

		
		// do nothing !
		// this value will be used at the very end of the process.
		
	}



	

}
