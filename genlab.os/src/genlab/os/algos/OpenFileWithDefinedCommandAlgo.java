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
import genlab.core.parameters.StringParameter;
import genlab.os.utils.Utils;

import java.io.File;
import java.util.Map;

/**
 * This algo just opens a file
 * 
 * @author Samuel Thiriot
 *
 */
public class OpenFileWithDefinedCommandAlgo extends BasicAlgo {

	public static final InputOutput<File> INPUT_FILE = new InputOutput<File>(
			FileFlowType.SINGLETON, 
			"in_file", 
			"file", 
			"the file to open",
			true
			);
	
	public static final StringParameter PARAM_COMMAND = new StringParameter(
			"param_command", 
			"command", 
			"the command to run to open the file", 
			"open"
			);
	
	public OpenFileWithDefinedCommandAlgo() {
		super(
				"open the file with a specific command (os)", 
				"opens this file using the OS default command line", 
				null, 
				ExistingAlgoCategories.DISPLAY.getTotalId(),  
				null
				);
		
		
		inputs.add(INPUT_FILE);

		registerParameter(PARAM_COMMAND);
		
	}


	@Override
	public IAlgoExecution createExec(final IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void run() {
				
				setResult(new ComputationResult(algoInst, progress, execution.getListOfMessages()));
				
				progress.setComputationState(ComputationState.STARTED);
				
				Map<IConnection,Object> c2v = this.getInputValuesForInput(INPUT_FILE);
				
				progress.setProgressTotal(c2v.size());
				
				boolean hadAProblem = false;
				
				final String command = (String)algoInst.getValueForParameter(PARAM_COMMAND.getId());
				
				for (Object o: c2v.values()) {
				
					File f = (File)o;
					try {
						if (!f.exists())
							throw new ProgramException("this file does not exists");
						
						Utils.openFileWithEditor(command, f);
					} catch (RuntimeException e) {
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
