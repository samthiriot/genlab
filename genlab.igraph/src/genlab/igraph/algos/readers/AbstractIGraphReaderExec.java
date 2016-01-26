package genlab.igraph.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphExec;

import java.io.File;

public abstract class AbstractIGraphReaderExec extends AbstractIGraphExec {

	public AbstractIGraphReaderExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst
				);
		
	}
	
	protected abstract IGenlabGraph readGraph(String filename);

	@Override
	public void run() {

		// notify start
		progress.setProgressMade(1);
		progress.setProgressTotal(40);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		
		// decode parameters
		final File inputFile = (File)getInputValueForInput(AbstractIGraphReaderAlgo.INPUT_FILE);

		progress.setProgressTotal(5);
		
		try {
			
			// read graph
			IGenlabGraph glGraph = readGraph(inputFile.getAbsolutePath());		
			result.setResult(AbstractIGraphReaderAlgo.OUTPUT_GRAPH, glGraph);
			setResult(result);
			
			progress.setProgressMade(40);
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (Exception e) {
			messages.errorTech("the measure of graph properties failed: "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			progress.setException(e);
		} 			
		

	}
	
	@Override
	public void kill() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void cancel() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}
	

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 10000;
	}
	/**
	 * for serialization only
	 */
	public AbstractIGraphReaderExec() {}

}
