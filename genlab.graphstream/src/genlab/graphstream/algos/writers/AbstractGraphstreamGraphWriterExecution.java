package genlab.graphstream.algos.writers;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSink;

public class AbstractGraphstreamGraphWriterExecution extends
	AbstractAlgoExecutionOneshot {
	
	protected IGenlabGraph inputGraph = null;
	protected final FileSink fileSink;

	public AbstractGraphstreamGraphWriterExecution(
			IExecution exec, 
			IAlgoInstance algoInst,
			FileSink fileSink
			) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		this.fileSink = fileSink;
	}

	@Override
	public void run() {

		try {
		
			// retrieve the graph
			inputGraph = AbstractGraphStreamGraphWriter.INPUT_GRAPH.decodeFromParameters(
					getInputValueForInput(
							AbstractGraphStreamGraphWriter.INPUT_GRAPH
							)
					);
			if (inputGraph == null)
				 throw new WrongParametersException("input graph expected");
	
					
			// notify start
			progress.setProgressMade(0);
			progress.setProgressTotal(1);
			progress.setComputationState(ComputationState.STARTED);
			
			ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
	
			File tmpFile = null;
			
			try {
	
				tmpFile = File.createTempFile("genlab_tmp_", ".net");
				
				fileSink.writeAll(
						GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(inputGraph, result.getMessages()), 
						tmpFile.getAbsolutePath()
						);
				
				System.err.println("written in: "+tmpFile);
				
	
			} catch (IOException e) {
				e.printStackTrace();
				// TODO !!!
			}
			
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

	
	

}
