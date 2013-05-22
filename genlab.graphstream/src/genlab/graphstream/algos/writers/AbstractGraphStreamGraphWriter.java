package genlab.graphstream.algos.writers;

import genlab.basics.flow.FileFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.BasicAlgo;
import genlab.core.algos.ExistingAlgoCategories;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.io.File;
import java.util.Map;

import org.graphstream.stream.file.FileSink;

public abstract class AbstractGraphStreamGraphWriter extends BasicAlgo implements IAlgo {

	
	public static final InputOutput<IGenlabGraph> PARAM_GRAPH = new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			"TODO.graph", 
			"graph", 
			"the graph to save"
	);
	
	public static final InputOutput<File> OUTPUT_FILE = new InputOutput<File>(
			new FileFlowType(), 
			"TODO.file", 
			"file", 
			"the file to save the thing to"
	);
	
	public AbstractGraphStreamGraphWriter(String name, String description) {
		super(name, description, ExistingAlgoCategories.WRITER_GRAPH.getTotalId());

		inputs.add(PARAM_GRAPH);
		outputs.add(OUTPUT_FILE);
	}
	
	@Override
	public IAlgoInstance createInstance(IGenlabWorkflow workflow) {
		return new AlgoInstance(this, workflow);
	}
	
	protected abstract FileSink getGraphStreamFileSink();

	@Override
	public IAlgoExecution createExec(AlgoInstance algoInstance, Map<IInputOutput, Object> inputs) {
		
		return new AbstractGraphstreamGraphWriterExecution(
				algoInstance, 
				PARAM_GRAPH.decodeFromParameters(inputs),
				getGraphStreamFileSink()
				);
	}
	

}
