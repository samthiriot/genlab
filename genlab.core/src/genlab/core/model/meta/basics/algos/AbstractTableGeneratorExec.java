package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractTableGeneratorExec extends AbstractAlgoExecutionOneshot {

	public AbstractTableGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}
	

	protected abstract Object[] generateValues(int count);

	protected void initRun() {
		
	}
	
	
	@Override
	public long getTimeout() {
		return 1000;
	}

	@Override
	public final void run() {

		// TODO res
		progress.setComputationState(ComputationState.STARTED);
		
	
		final int nbCols = (Integer)getInputValueForInput(AbstractTableGenerator.INPUT_COLS);
		final int nbRows = (Integer)getInputValueForInput(AbstractTableGenerator.INPUT_ROWS);
	
		progress.setProgressTotal(nbRows+2);
		
		initRun();
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		
		// columns titles
		Collection<String> titles = new LinkedList<String>();
		for (int i=0; i<nbCols; i++) {
			titles.add("col "+i);
		}
		progress.incProgressMade();
		
		// create the table
		IGenlabTable resultTable = new GenlabTable();
		resultTable.declareColumns(titles);

		progress.incProgressMade();
		
		// fill the table 
		for (int i=0; i<nbRows; i++) {
			
			resultTable.addRow(generateValues(nbCols));
			progress.incProgressMade();
		}
		
		result.setResult(AbstractTableGenerator.OUTPUT_TABLE, resultTable);
		
		setResult(result);
		progress.setComputationState(ComputationState.FINISHED_OK);
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
