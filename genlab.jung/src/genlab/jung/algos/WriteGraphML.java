package genlab.jung.algos;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.jung.utils.Converters;
import genlab.jung.utils.JungWriters;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;

public class WriteGraphML extends BasicAlgo {

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
	
	public WriteGraphML() {
		super(
				"write as GraphML", 
				"writes a graph in GraphML", 
				ExistingAlgoCategories.WRITER_GRAPH, 
				null, 
				null
				);
		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_FILE);
		
	}


	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void cancel() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void run() {
				
				progress.setProgressTotal(2);
				progress.setComputationState(ComputationState.STARTED);

				ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		
				try {
				
					// retrieve parameters
					final IGenlabGraph graph = (IGenlabGraph) getInputValueForInput(INPUT_GRAPH);

					
					
					// create output file
					File tmpFile = File.createTempFile("genlab_tmp_", ".net");

					progress.incProgressMade();
					
					// transform graph
					JungWriters.writeGraphAsGraphML(graph, tmpFile);
					progress.incProgressMade();
					
					result.setResult(OUTPUT_FILE, tmpFile);
					setResult(result);
					progress.setComputationState(ComputationState.FINISHED_OK);
				
				} catch (Exception e) {
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
				}
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

}
