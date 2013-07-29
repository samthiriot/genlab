package genlab.graphstream.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.BooleanParameter;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.graphstream.algos.GraphStreamAlgo;
import genlab.graphstream.utils.GraphstreamConvertors;

import org.graphstream.algorithm.generator.BaseGenerator;


public abstract class GraphStreamGeneratorAlgo extends GraphStreamAlgo {

	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH =  new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"graph", 
			"g", 
			"resulting graph"
		);
	
	
	public static final BooleanParameter PARAM_DIRECTED = new BooleanParameter(
			"param_directed",
			"directed", 
			"generate a directed graph", 
			Boolean.FALSE
			);
	
	
	public GraphStreamGeneratorAlgo(
			String name, 
			String description
			) {
		this(
				name, 
				description, 
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
				);
		
		
	}
	
	public GraphStreamGeneratorAlgo(
			String name, 
			String description,
			String categoryId
			) {
		super(
				name, 
				description, 
				categoryId
				);
		
		registerParameter(PARAM_DIRECTED);

		outputs.add(OUTPUT_GRAPH);
	}
	
	
	/**
	 * Returns a generator for this execution context (notably for parameters !)
	 * @param exec
	 * @param algoInstance 
	 * @return
	 */
	protected abstract BaseGenerator getBaseGeneratorForExec(AbstractGraphStreamGeneratorExec exec, AlgoInstance algoInstance);
		
	
	/**
	 * Returns the number of iterations to drive for this context (-1 to let the generator 
	 * finish its process)
	 * @param exec
	 * @return
	 */
	public abstract int getIterationsForExec(AbstractGraphStreamGeneratorExec exec);
	

	/**
	 * If it retuiurns true, the generator will be called the given count of iterations.
	 * Else it will focus on the number of vertices into the graph.
	 * @return
	 */
	public boolean shouldCountIterations() {
		return false;
	}
	
	@Override
	public final IAlgoExecution createExec(IExecution exec, final AlgoInstance algoInstance) {
		
		return new AbstractGraphStreamGeneratorExec(exec, algoInstance) {
			
			@Override
			public void run() {
				
				// notify start
				progress.setProgressMade(0);
				progress.setProgressTotal(1);
				progress.setComputationState(ComputationState.STARTED);
				
				ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
				
				final Integer size = getIterationsForExec(this);
					
				try {
									
					BaseGenerator generator = getBaseGeneratorForExec(this, algoInstance);
					
					Boolean directed = (Boolean)algoInstance.getValueForParameter(PARAM_DIRECTED.getId());
					
					messages.debugUser("configuring graph directionality to: "+directed, getClass());
					
					generator.setDirectedEdges(directed, false);
					
					IGenlabGraph graph = GraphstreamConvertors.loadGraphWithGraphstreamFromGeneratorSource(
							// TODO ???
							"generatedTODO", 
							generator, 
							size,
							result.getMessages(),
							shouldCountIterations(),
							directed?GraphDirectionality.DIRECTED:GraphDirectionality.UNDIRECTED,
							new IGenlabGraphInitializer() {
								
								@Override
								public void initGraph(IGenlabGraph glGraph) {
									glGraph.declareVertexAttribute("x", Double.class);
									glGraph.declareVertexAttribute("y", Double.class);
								}
							}
							);
					result.setResult(OUTPUT_GRAPH, graph);
					
					setResult(result);
					progress.setProgressMade(1);
					progress.setComputationState(ComputationState.FINISHED_OK);
								
					// TODO catch OutOfMemoryError with a specific message
				} catch (RuntimeException e) {
										
					result.getMessages().errorUser("something went wrong during the execution", getClass());
					result.getMessages().errorTech("exception catch when running "+e.getMessage(), getClass(), e);
					
					progress.setProgressMade(1);
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
					
				} 
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
			
			
			
			@Override
			public void kill() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public long getTimeout() {
				return 1000*60*5; // 5 minutes ?
			}
		};
	}
}
