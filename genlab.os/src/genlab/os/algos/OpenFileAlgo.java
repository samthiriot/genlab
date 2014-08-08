package genlab.os.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IAlgoExecutionOneshot;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.os.utils.Utils;

import java.io.File;
import java.util.Map;

/**
 * This algo just opens a file
 * 
 * @author Samuel Thiriot
 *
 */
public class OpenFileAlgo extends BasicAlgo {

	public static final InputOutput<File> INPUT_FILE = new InputOutput<File>(
			FileFlowType.SINGLETON, 
			"in_file", 
			"file", 
			"the file to open",
			true
			);
	
	public OpenFileAlgo() {
		super(
				"open the file with the default system editor (os)", 
				"opens this file using the OS default command line", 
				ExistingAlgoCategories.DISPLAY,  
				null,
				null
				);
		
		
		inputs.add(INPUT_FILE);

	}


	@Override
	public IAlgoExecution createExec(final IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void cancel() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void run() {
				
				setResult(new ComputationResult(algoInst, progress, execution.getListOfMessages()));
				
				progress.setComputationState(ComputationState.STARTED);
				
				Map<IConnection,Object> c2v = this.getInputValuesForInput(INPUT_FILE);
				
				progress.setProgressTotal(c2v.size());
				
				boolean hadAProblem = false;
				
				for (Object o: c2v.values()) {
				
					File f = (File)o;
					try {
						if (!f.exists())
							throw new ProgramException("this file does not exists");
						
						Utils.openFileWithDefaultEditor(f);
					} catch (RuntimeException e) {
						e.printStackTrace();
						hadAProblem = true;
						messages.errorUser("error while attempting to open the file "+f, getClass(), e);
					}
					
					progress.incProgressMade();

				}
				
				if (hadAProblem)
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
				else 
					progress.setComputationState(ComputationState.FINISHED_OK);
				
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 1000;
			}
		};
	}

}
