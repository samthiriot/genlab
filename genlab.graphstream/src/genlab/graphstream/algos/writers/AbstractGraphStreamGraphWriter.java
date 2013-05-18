package genlab.graphstream.algos.writers;

import genlab.basics.flow.FileFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.stream.file.FileSink;

// TODO use basicalgo instead
public abstract class AbstractGraphStreamGraphWriter implements IAlgo {

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
	
	private final Set<IInputOutput> inputs = new HashSet<IInputOutput>() {{
		add(PARAM_GRAPH);
	}};
	
	private final Set<IInputOutput> outputs = new HashSet<IInputOutput>() {{
		add(OUTPUT_FILE);
	}};
	
	@Override
	public Set<IInputOutput> getInputs() {
		return inputs;
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		return outputs;
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
