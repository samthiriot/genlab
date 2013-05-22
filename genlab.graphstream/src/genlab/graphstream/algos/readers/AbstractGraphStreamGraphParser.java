package genlab.graphstream.algos.readers;

import genlab.basics.flow.FileFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.BasicAlgo;
import genlab.core.algos.ExistingAlgoCategories;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.io.File;
import java.util.Map;

import org.graphstream.stream.file.FileSource;

public abstract class AbstractGraphStreamGraphParser extends BasicAlgo {

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
	
	public AbstractGraphStreamGraphParser(String name, String desc) {
		super(name, desc, ExistingAlgoCategories.PARSER_GRAPH.getTotalId());
		inputs.add(PARAM_FILE);
		outputs.add(OUTPUT_GRAPH);
	}
	
	@Override
	public IAlgoInstance createInstance(IGenlabWorkflow workflow) {
		return new AlgoInstance(this, workflow);
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
