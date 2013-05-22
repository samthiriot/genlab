package genlab.graphstream.algos.writers;

import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AbstractAlgoExecution;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.ComputationResult;
import genlab.core.algos.ComputationState;
import genlab.core.algos.IAlgoInstance;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSink;

public class AbstractGraphstreamGraphWriterExecution extends
		AbstractAlgoExecution {
	
	protected final IGenlabGraph inputGraph;
	protected final FileSink fileSink;

	public AbstractGraphstreamGraphWriterExecution(
			IAlgoInstance algoInst,
			IGenlabGraph inputGraph,
			FileSink fileSink
			) {
		super(algoInst, new ComputationProgressWithSteps(algoInst.getAlgo()));
		this.inputGraph = inputGraph;
		this.fileSink = fileSink;
	}

	@Override
	public void run() {

		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst.getAlgo(), progress);

		File tmpFile = null;
		
		try {

			tmpFile = File.createTempFile("genlab_tmp_", ".net");
			
			fileSink.writeAll(
					GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(inputGraph, result.getMessages()), 
					tmpFile.getAbsolutePath()
					);

		} catch (IOException e) {
			e.printStackTrace();
			// TODO !!!
		}
		
		// ended !
		result.setResult(AbstractGraphStreamGraphWriter.OUTPUT_FILE, tmpFile);

		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);

		setResult(result);
	}

}
