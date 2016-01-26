package genlab.igraph.algos.writers;

import java.io.File;

import genlab.core.commons.FileUtils;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphExec;
import genlab.igraph.algos.measure.AbstractIGraphMeasure;

public abstract class AbstractIGraphWriterExec extends AbstractIGraphExec {

	
	public AbstractIGraphWriterExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst
				);
		
	}
	
	protected abstract void writeGraph(IGenlabGraph graph, String filename);

	@Override
	public void run() {

		// notify start
		progress.setProgressMade(1);
		progress.setProgressTotal(40);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
	
		// decode parameters
		final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(AbstractIGraphMeasure.INPUT_GRAPH);
		
		// generate a file name
		File targetFile = FileUtils.createFileWithIncrementingNumber(
				getExecution().getResultsDirectory(), 
				"graph_", 
				((AbstractIGraphWriterAlgo)getAlgoInstance().getAlgo()).extension
				);
		messages.infoUser("the graph will be written to "+targetFile.getAbsolutePath(), getClass());
		progress.setProgressTotal(5);
		
		try {
			
			// write
			writeGraph(glGraph, targetFile.getAbsolutePath());
							
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
	public AbstractIGraphWriterExec() {}

}
