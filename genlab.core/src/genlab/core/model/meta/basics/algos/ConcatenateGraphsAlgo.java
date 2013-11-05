package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
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
import genlab.core.parameters.BooleanParameter;

import java.util.Map;

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
	
	
	public static final BooleanParameter PARAM_KEEP_EDGES_ATTRIBUTES = new BooleanParameter(
			"param_keep_edges_attr", 
			"keep edges attributes", 
			"keep the edges attributes", 
			true
			);
	
	public static final BooleanParameter PARAM_KEEP_GRAPH_ATTRIBUTES = new BooleanParameter(
			"param_keep_graph_attr", 
			"keep graph attributes", 
			"keep the attributes of the graphs", 
			true
			);
	
	public static final BooleanParameter PARAM_KEEP_VERTEX_ATTRIBUTES = new BooleanParameter(
			"param_keep_vertex_attr", 
			"keep vertex attributes", 
			"keep the vertex attributes", 
			true
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

		registerParameter(PARAM_KEEP_VERTEX_ATTRIBUTES);
		registerParameter(PARAM_KEEP_EDGES_ATTRIBUTES);
		registerParameter(PARAM_KEEP_GRAPH_ATTRIBUTES);
	}

	

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
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
					
					outGraph.addAll(
							(IGenlabGraph)graph, 
							(Boolean)algoInst.getValueForParameter(PARAM_KEEP_GRAPH_ATTRIBUTES.getId()),
							(Boolean)algoInst.getValueForParameter(PARAM_KEEP_VERTEX_ATTRIBUTES.getId()),
							(Boolean)algoInst.getValueForParameter(PARAM_KEEP_EDGES_ATTRIBUTES.getId())
							);
					
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
