package genlab.graphstream.algos.readers;

import genlab.basics.flow.FileFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.stream.file.FileSource;

public abstract class AbstractGraphStreamGraphParser implements IAlgo {

	public static final InputOutput<File> PARAM_FILE = new InputOutput<File>(
			new FileFlowType(), 
			"TODO.file", 
			"file", 
			"the file to read the graph from"
	);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			"TODO.graph", 
			"graph", 
			"the graph loaded from the file"
	);
	
	private final Set<IInputOutput> inputs = new HashSet<IInputOutput>() {{
		add(PARAM_FILE);
	}};
	
	private final Set<IInputOutput> outputs = new HashSet<IInputOutput>() {{
		add(OUTPUT_GRAPH);
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
	public IAlgoInstance createInstance() {
		return new AlgoInstance(this);
	}
	
	protected abstract FileSource getGraphStreamFileSource();

	@Override
	public IAlgoExecution createExec(AlgoInstance algoInstance, Map<IInputOutput, Object> inputs) {
		
		return new AbstractGraphstreamGraphParserExecution(
				algoInstance, 
				PARAM_FILE.decodeFromParameters(inputs), 
				getGraphStreamFileSource(), 
				"tmpGraphTODO"
				);
	}
	

}
