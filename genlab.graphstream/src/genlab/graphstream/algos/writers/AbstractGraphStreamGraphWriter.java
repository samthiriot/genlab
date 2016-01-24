package genlab.graphstream.algos.writers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IReduceAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.graphstream.algos.GraphStreamAlgo;

import java.io.File;

import org.graphstream.stream.file.FileSink;

/**
 * TODO add the file as a parameter
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractGraphStreamGraphWriter extends GraphStreamAlgo implements IReduceAlgo {

	
	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"param_graph", 
			"graph", 
			"the graph to save"
	);
	
	public static final InputOutput<File> OUTPUT_FILE = new InputOutput<File>(
			FileFlowType.SINGLETON, 
			"out_file", 
			"file", 
			"the file to save the thing to"
	);
	
	public AbstractGraphStreamGraphWriter(String name, String description) {
		super(name, description, ExistingAlgoCategories.WRITER_GRAPH);

		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_FILE);
		
	}
	
	protected abstract FileSink getGraphStreamFileSink();

	protected abstract String getFilenameExtension();
	
	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		
		return new AbstractGraphstreamGraphWriterExecution(
				execution,
				algoInstance, 
				getGraphStreamFileSink(),
				getFilenameExtension()
				);
	}
	

}
