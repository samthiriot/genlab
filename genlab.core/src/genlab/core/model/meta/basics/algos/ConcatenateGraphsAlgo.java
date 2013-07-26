package genlab.core.model.meta.basics.algos;

import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class ConcatenateGraphsAlgo extends BasicAlgo {

	public static final GraphInOut INPUT_GRAPH = new GraphInOut(
			"in_graphs", 
			"graphs", 
			"graph to concatenate",
			true
			);
	
	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"concatenated graph"
			);
	
	public ConcatenateGraphsAlgo() {
		super(
				"concatenate graphs", 
				"concatenate graphs", 
				null, 
				ExistingAlgoCategories.CASTING.getTotalId(), // TODO another one !
				null);

		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_GRAPH);
	}

	

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractAlgoExecution(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void run() {
				
				progress.setComputationState(ComputationState.STARTED);
				
				ComputationResult result = new ComputationResult(algoInst, progress, messages);
				setResult(result);
				
				
				Map<IConnection,Object> inputs = getInputValuesForInput(INPUT_GRAPH);
				int totalToDo = 0;
				GraphDirectionality dir = null;
				for (Object graph : inputs.values()) {
					
					IGenlabGraph inGraph = (IGenlabGraph)graph;
					
					// evaluate duration
					totalToDo += inGraph.getVerticesCount() + inGraph.getEdgesCount();
					
					// define directionality of the result
					if (dir == null)
						dir = inGraph.getDirectionality();
					else {
						if (dir != inGraph.getDirectionality() && dir != GraphDirectionality.MIXED) {
							dir = GraphDirectionality.MIXED;
						}
						
					}
				}
				progress.setProgressTotal(totalToDo);

				messages.infoUser("for the fusion of these graphs,  the resulting directionality will be : "+dir, getClass());
				
				IGenlabGraph outGraph = GraphFactory.createGraph("conc", dir, false);
				for (Object graph : inputs.values()) {
					
					outGraph.addAll((IGenlabGraph)graph, true, true, true);
					
				}
				
				result.setResult(OUTPUT_GRAPH, outGraph);
				
				progress.setComputationState(ComputationState.FINISHED_OK);
				
			}
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

}
