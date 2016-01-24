package genlab.graphstream.algos.writers;

import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.AbstractAlgoExecutionOneshotOrReduce;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ConnectionExecFromIterationToReduce;
import genlab.core.model.exec.ConnectionExecFromSupervisorToChild;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSink;

public class AbstractGraphstreamGraphWriterExecution 
							extends AbstractAlgoExecutionOneshotOrReduce {
	
	protected IGenlabGraph inputGraph = null;
	protected final FileSink fileSink;
	private final String fileExtension;

	public AbstractGraphstreamGraphWriterExecution(
			IExecution exec, 
			IAlgoInstance algoInst,
			FileSink fileSink,
			String fileExtension
			) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		this.fileSink = fileSink;
		this.fileExtension = fileExtension;
	}

	protected File writeGraph(IGenlabGraph inputGraph, ListOfMessages msg) {

		File tmpFile = null;
		
		synchronized (fileSink) {
			try {

				tmpFile = FileUtils.createFileWithIncrementingNumber(
						getExecution().getResultsDirectory(), 
						"graph_", 
						fileExtension
						);
				
				//tmpFile = File.createTempFile("genlab_tmp_", ".net");
				
				fileSink.writeAll(
						GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(inputGraph, msg), 
						tmpFile.getAbsolutePath()
						);
							

			} catch (IOException e) {
				throw new ProgramException("unable to write the graph into "+tmpFile.getAbsolutePath()+": "+e.getMessage(), e);
			}
			
	
		}
		return tmpFile;
	}
	
	@Override
	public void run() {
		
		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);

		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());

		
		// exclude the case where we were called before using a reduce connection
		{
			boolean normalConnectionFound = false; 
			for (IConnectionExecution cEx: getConnectionsForInput(algoInst.getInputInstanceForInput(AbstractGraphStreamGraphWriter.INPUT_GRAPH))) {
				if (cEx instanceof ConnectionExecFromIterationToReduce || cEx instanceof ConnectionExecFromSupervisorToChild) {
					continue;
				}
				normalConnectionFound = true;
			}
			if (!normalConnectionFound) {
				result.setResult(AbstractGraphStreamGraphWriter.OUTPUT_FILE, null);
				setResult(result);
				progress.setComputationState(ComputationState.FINISHED_OK);
		
				return;
			}
		}

		try {
		
			// retrieve the graph
			inputGraph = AbstractGraphStreamGraphWriter.INPUT_GRAPH.decodeFromParameters(
					getInputValueForInput(
							AbstractGraphStreamGraphWriter.INPUT_GRAPH
							)
					);
			if (inputGraph == null)
				 throw new WrongParametersException("input graph expected");
	
					
			File tmpFile = writeGraph(inputGraph, result.getMessages());
			
			// ended !
			result.setResult(AbstractGraphStreamGraphWriter.OUTPUT_FILE, tmpFile);
	
			setResult(result);
	
			progress.setProgressMade(1);
			progress.setComputationState(ComputationState.FINISHED_OK);
	
			
		} catch (Exception e) {
			e.printStackTrace();
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			// TODO store info exception
		}
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		return 1000*60;
	}

	@Override
	public void receiveInput(
			IAlgoExecution executionRun, 
			IConnectionExecution connectionExec, 
			Object value) {
		
		// we receive a data right now from a continuous algo.
		// let's just write it.

		// retrieve the graph
		inputGraph = AbstractGraphStreamGraphWriter.INPUT_GRAPH.decodeFromParameters(
				value
				);
		if (inputGraph == null)
			 throw new WrongParametersException("input graph expected");
	
		File tmpFile = writeGraph(inputGraph, getExecution().getListOfMessages());
		
		// TODO send something or ?
		inputGraph = null;
	}

	@Override
	public void signalIncomingSupervisor(
			AbstractContainerExecutionSupervisor supervisor) {
		// do nothing: we don't care about who is sending what
	}

	@Override
	public void signalEndOfTasksForSupervisor(
			AbstractContainerExecutionSupervisor supervisor,
			ComputationState state) {
		// we don't care. 
	}

	
	

}
