package genlab.core.model.meta.basics.algos;

import genlab.core.Activator;
import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;
import genlab.core.parameters.FileParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.osgi.framework.Bundle;

public class WriteTableCSV extends BasicAlgo {

	public static final InputOutput<IGenlabTable> INPUT_TABLE = new InputOutput<IGenlabTable>(
			TableFlowType.SINGLETON, 
			"in_table", 
			"table", 
			"the table to write in a file"
			);
	
	public static final InputOutput<File> OUTPUT_FILE = new InputOutput<File>(
			FileFlowType.SINGLETON, 
			"out_file", 
			"file", 
			"the file in which the table is stored"
	);
	
	
	public static final FileParameter PARAMETER_FILE = new FileParameter(
			"param_file", 
			"file", 
			"the file into which write the result", 
			FileUtils.getHomeDirectoryFile()
			);
	
	public WriteTableCSV() {
		super(
				"write table as CSV", 
				"writes a table into a file as a CSV file", 
				ExistingAlgoCategories.WRITER, 
				"/icons/writeToFile"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				null
				);
		
		inputs.add(INPUT_TABLE);
		outputs.add(OUTPUT_FILE);
		registerParameter(PARAMETER_FILE);
	}
	

	@Override
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new AlgoInstance(this, workflow, id) {

			@Override
			public void checkForRun(WorkflowCheckResult res) {
				// checks by parent: connected, etc.
				super.checkForRun(res);
				
				// local checks: conformity of parameters
				File file = (File)getValueForParameter(PARAMETER_FILE);
				if (file.isDirectory()) {
					res.messages.errorUser("invalid value for the parameter "+PARAMETER_FILE.getName()+": the path "+file.getPath()+" is a directory while a file is expected", getClass());
				} else if (file.exists()) {
					if (!file.canWrite()) {
						res.messages.errorUser("invalid value for the parameter "+PARAMETER_FILE.getName()+": the file "+file.getPath()+" is not writable", getClass());
					} else {
						res.messages.warnUser("the parameter "+PARAMETER_FILE.getName()+" will lead to the replacement of the file "+file.getPath()+"; its previous content will be lost", getClass());
					}
				}
			}
			
		};
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		final File file = (File)algoInstance.getValueForParameter(PARAMETER_FILE);
		
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void run() {
				
				progress.setComputationState(ComputationState.STARTED);
				
				IGenlabTable table = (IGenlabTable)getInputValueForInput(INPUT_TABLE);
				
				if (table == null)
					throw new ProgramException("no data available for computation");
				
				progress.setProgressTotal(table.getRowsCount());
				
				try {
					PrintStream ps = new PrintStream(file);
					
					// add titles
					ps.print("#");
					for (String id : table.getColumnsId()) {
						ps.print(id);
						ps.print(";");
					}
					ps.println();
					
					Object[] row = null;
					for (int i=0; i<table.getRowsCount(); i++) {

						row = table.getRow(i);
						
						for (int j=0; j<row.length; j++) {
							if (j>0)
								ps.print(";");
							ps.print(row[j]);	
						}
						ps.println();
						
						progress.incProgressMade();
					}
					
					ps.close();
					
					// export the file as a result
					ComputationResult res = new ComputationResult(algoInst, progress, messages);
					res.setResult(OUTPUT_FILE, file);
					setResult(res);
					
					progress.setComputationState(ComputationState.FINISHED_OK);

					
				} catch (RuntimeException e) {
					
					e.printStackTrace();
					messages.errorUser("an error occured during the writing of the table to a file: "+e.getMessage(), getClass(), e);
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
					progress.setException(e);
					
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
					messages.errorUser("unable to create a file named \""+file+"\": "+e.getMessage(), getClass(), e);
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
					progress.setException(e);
					
				}
				
					
				
			}
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 5000;
			}
		};
	}


	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
