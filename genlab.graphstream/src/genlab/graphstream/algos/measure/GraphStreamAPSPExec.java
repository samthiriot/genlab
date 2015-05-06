package genlab.graphstream.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

public class GraphStreamAPSPExec extends AbstractGraphstreamMeasureExecution implements IAlgoExecutionRemotable {
	
	public GraphStreamAPSPExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}
	
	public GraphStreamAPSPExec() {}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			final IComputationProgress progress, 
			final Graph gsGraph,
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {
		
		final long PROGRESS_DURATION_INITS = 1;
		final long PROGRESS_DETECT_COMPONENTS = gsGraph.getNodeCount();
		final long PROGRESS_REMOVE_NODES = 5;
		long PROGRESS_COMPUTE_SHORTEST = gsGraph.getNodeCount()*gsGraph.getNodeCount()*gsGraph.getNodeCount();
		long PROGRESS_USE_OUTPUTS = gsGraph.getNodeCount()*gsGraph.getNodeCount();
		final long PROGRESS_DEFINE_OUTPUTS = 1;
		final long PROGRESS_firstTotal = PROGRESS_DURATION_INITS +
				PROGRESS_DETECT_COMPONENTS +
				PROGRESS_REMOVE_NODES + 
				PROGRESS_COMPUTE_SHORTEST + 
				PROGRESS_USE_OUTPUTS +
				PROGRESS_DEFINE_OUTPUTS;
		long PROGRESS_secondTotal = PROGRESS_firstTotal;
		progress.setProgressTotal(
				progress.getProgressTotalToDo() + 
				PROGRESS_firstTotal
				);

		progress.setCurrentTaskName("init");
		
		// final results
		final Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
		
		progress.incProgressMade(PROGRESS_DURATION_INITS);
		progress.setCurrentTaskName("detect connected components");
		
		// first detect connected components
		ConnectedComponents cc = new ConnectedComponents();				
		cc.init(gsGraph);
		cc.compute();
		List<Node> nodesInGiantCompoennt = cc.getGiantComponent();
		if (cc.getConnectedComponentsCount() > 1) {
			getResult().getMessages().infoUser(
					"this graph contains "+cc.getConnectedComponentsCount()+" components; " +
					"these statistics will be computed only for the giant one, " +
					"which covers "+nodesInGiantCompoennt.size()+" vertices over "+gsGraph.getNodeCount(), 
					getClass()
					
					);
			
		}
		
		progress.incProgressMade(PROGRESS_DETECT_COMPONENTS);
		
		
		progress.setCurrentTaskName("focus on the giant component");
		
		
		// then remove all the nodes out of the giant component (so the algo will be quicker, nice !)
		/*
		if (cc.getConnectedComponentsCount() > 1) {
			Set<Node> nodesToRemove = new HashSet<Node>(gsGraph.getNodeSet());
			nodesToRemove.removeAll(nodesInGiantCompoennt);
			for (Node n: nodesToRemove) {
				gsGraph.removeNode(n);
			}
			
			// update expected complexity
			PROGRESS_COMPUTE_SHORTEST = gsGraph.getNodeCount()*gsGraph.getNodeCount()*gsGraph.getNodeCount();
			PROGRESS_USE_OUTPUTS = gsGraph.getNodeCount()*gsGraph.getNodeCount();
			PROGRESS_secondTotal =  PROGRESS_DURATION_INITS +
					PROGRESS_DETECT_COMPONENTS +
					PROGRESS_REMOVE_NODES + 
					PROGRESS_COMPUTE_SHORTEST + 
					PROGRESS_USE_OUTPUTS +
					PROGRESS_DEFINE_OUTPUTS;
			progress.setProgressTotal(
					progress.getProgressTotalToDo()
					- PROGRESS_firstTotal 
					+ PROGRESS_secondTotal
					);
		}
		*/
		// clear data of the componenets
		cc.terminate();
		
		progress.incProgressMade(PROGRESS_REMOVE_NODES);
		progress.setCurrentTaskName("computing the shortest pathes");
		
		// create and run the graphstream algorithm
		APSP apsp = new APSP(gsGraph);
		
		if (genlabGraph.getDirectionality() == GraphDirectionality.MIXED)
			messages.warnUser("graphstream APSP algorithm does not supports mixed network; the computations will assume the network is undirected.", getClass());

		apsp.setDirected(genlabGraph.getDirectionality() == GraphDirectionality.DIRECTED);
		
		getResult().getMessages().infoUser(
				"The network is computed as a"+(apsp.isDirected()?" directed":"n undirected")+" graph", 
				getClass()
				);
		
		
		final long totalExpected = PROGRESS_COMPUTE_SHORTEST;
				
		apsp.registerProgressIndicator(new APSP.Progress() {

			double previousP = 0;
					
			@Override
			public void progress(double progressV) {
				
				
				if (progressV-previousP > 0.01) { // filter, we don't want to propage 10E-5 evolutions of the progress ! 
					System.err.println(progressV);
					long progressed = (long)Math.floor(totalExpected/progressV);
					progress.incProgressMade(progressed);
					previousP = progressV;
				}
				
			}
			
		});
	    apsp.setWeightAttributeName(null); // ensure that the attribute name
		apsp.compute();
		
		progress.setCurrentTaskName("computing the average of pathes");
		
		// process its results
		BigDecimal longest = new BigDecimal(Integer.MIN_VALUE);
		BigDecimal shortest = new BigDecimal(Integer.MAX_VALUE);
		BigDecimal sum = new BigDecimal(0);
		BigDecimal count = new BigDecimal(0);
		
		// ... for each node,
		for (Node n1: nodesInGiantCompoennt) {
			
			if (cancelled) {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
				return null;
			}
			
			// ... and each other node
			for (Node n2: nodesInGiantCompoennt) {
				
				// increment the sum
				if (n1 == n2) {
					// distance == 0; don't add it
				} else {
					 APSPInfo info = n1.getAttribute(APSPInfo.ATTRIBUTE_NAME);
					 //double length = info.getLengthTo(n2.getId());
					 Path path = info.getShortestPathTo(n2.getId());
					 if (path == null)
						 continue; // ignore this one, there is not path
					 
					 int length = path.size();
					 BigDecimal lengthBD = new BigDecimal(length);
					 sum = sum.add(lengthBD);
					 longest = longest.max(lengthBD);
					 shortest = shortest.min(lengthBD);
				
				}
				// TODO case of disconnected ? 
				
				// increase count	
				count = count.add(BigDecimal.ONE);

			}
			
			progress.incProgressMade(nodesInGiantCompoennt.size());
		}
		
		progress.setCurrentTaskName("output");
		
		// post-processing
		MathContext mathContext = new MathContext(5, RoundingMode.HALF_UP);
		BigDecimal averagePathLength = sum.divide(count, mathContext); 
		double averagePathLengthDouble = averagePathLength.doubleValue();
		
		// use results
		results.put(GraphStreamAPSP.OUTPUT_AVERAGE_PATH_LENGTH, averagePathLengthDouble);
		results.put(GraphStreamAPSP.OUTPUT_DIAMETER, longest.doubleValue());
		System.err.println("graphstream/ average path length: "+averagePathLengthDouble);
		System.err.println("graphstream/ diameter: "+longest.doubleValue());
		
		// TODO distribution of path length ???
		
		// clean graphstream algo internal data
		
		
		return results;
	}

	@Override
	public long getTimeout() {
		return 1000*60*5;
	}
}