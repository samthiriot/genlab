package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IAlgoExecutionContinuous;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class WriteTableCSVExec 
						extends AbstractAlgoExecutionOneshot 
						implements IAlgoExecutionContinuous {

	/**
	 * Writes the table passed as a parameter to the file. 
	 * Throws exceptions in case of problem. 
	 * @param table
	 */
	protected void writeTableToFile(File file, IGenlabTable table) {

		try {
			PrintStream ps = new PrintStream(file);
			
			// add titles
			ps.print("#");
			for (String id : table.getColumnsId()) {
				ps.print(id);
				ps.print(";");
			}
			ps.println();
			
			// write every line
			Object[] row = null;
			for (int i=0; i<table.getRowsCount(); i++) {

				row = table.getRow(i);
				
				for (int j=0; j<row.length; j++) {
					if (j>0)
						ps.print(";");
					ps.print(row[j]);	
				}
				ps.println();
				
			}
			
			ps.close();
			
			
		} catch (RuntimeException e) {
			
			throw new ProgramException("an error occured during the writing of the table to a file: "+e.getMessage(), e);
			
		} catch (FileNotFoundException e) {
			
			throw new ProgramException("unable to create a file named \""+file+"\": "+e.getMessage(), e);
			
		}
	}
	
	
	public WriteTableCSVExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	public WriteTableCSVExec() {

	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		
		progress.setComputationState(ComputationState.STARTED);
		
		final IGenlabTable table = (IGenlabTable)getInputValueForInput(WriteTableCSV.INPUT_TABLE);
		final File file = (File)algoInst.getValueForParameter(WriteTableCSV.PARAMETER_FILE);

		if (table == null)
			throw new ProgramException("no data available for computation");
		
		progress.setProgressTotal(10);
		
		try {
			writeTableToFile(file, table);
			
			// export the file as a result
			ComputationResult res = new ComputationResult(algoInst, progress, messages);
			res.setResult(WriteTableCSV.OUTPUT_FILE, file);
			setResult(res);
			progress.setProgressMade(10);
			progress.setComputationState(ComputationState.FINISHED_OK);

		} catch (ProgramException e) {

			e.printStackTrace();
			messages.errorUser(e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			progress.setException(e);
			
		}
		
		
	}

	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);

	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);

	}


	@Override
	public void receiveInputContinuous(IAlgoExecution continuousProducer, Object keyWave, IConnectionExecution connectionExec, Object value) {
		
		// received a refresh of the input; let's write it
		
		IGenlabTable table = null;
		
		try {
			table = (IGenlabTable)value;
			
			if (table == null) {
				messages.warnTech("should have received a table to display, but it is null; ignoring intermediate writing", getClass());
				return;
			}
		} catch (RuntimeException e) {
			messages.warnTech("should have received a table to display, but it is not correct; ignoring intermediate writing", getClass(), e);
			return;
		}
		
		final File file = (File)algoInst.getValueForParameter(WriteTableCSV.PARAMETER_FILE);

		
		try {
			writeTableToFile(file, table);					
		} catch (ProgramException e) {

			// ignore failures for intermediate steps
			e.printStackTrace();
			messages.warnTech(e.getMessage(), getClass(), e);
			
		}
	}

}
