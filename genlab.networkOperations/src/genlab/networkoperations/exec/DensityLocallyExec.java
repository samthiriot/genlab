package genlab.networkoperations.exec;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.networkoperations.algos.DensifyLocallyAlgo;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

public class DensityLocallyExec extends AbstractAlgoExecutionOneshot {


	public DensityLocallyExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		
	} 
	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		
		// load parameters
		final IGenlabGraph inGraph = (IGenlabGraph) getInputValueForInput(DensifyLocallyAlgo.INPUT_GRAPH);
		final Integer places = (Integer) getInputValueForInput(DensifyLocallyAlgo.INPUT_PLACES);
		final Integer horizon = (Integer) getInputValueForInput(DensifyLocallyAlgo.INPUT_HORIZON);
		final Double proba = (Double) getInputValueForInput(DensifyLocallyAlgo.INPUT_PCLOSURE);
		final Integer maxlinks = (Integer) getInputValueForInput(DensifyLocallyAlgo.INPUT_MAX_LINKS);

		final Long seed = (Long) algoInst.getValueForParameter(DensifyLocallyAlgo.PARAM_SEED);

		progress.setProgressTotal(places+1);
		progress.setComputationState(ComputationState.STARTED);
			
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		progress.incProgressMade(1);

		// specific case: empty graph
		if (inGraph.getVerticesCount() == 0 || places==0 || horizon<2 || maxlinks==0 || proba==0d) {
			result.setResult(DensifyLocallyAlgo.OUTPUT_GRAPH, inGraph);
			progress.setComputationState(ComputationState.FINISHED_OK);
			return;
		} 

		final IGenlabGraph outGraph = inGraph.clone("outOfDensify"); 
		final boolean shouldBeDirected = outGraph.getDirectionality() == GraphDirectionality.DIRECTED;
		
		// initialize random number generator
		RandomEngine coltRandom;
		if (seed != null) {
			if (seed > Integer.MAX_VALUE) {
				messages.warnUser("the seed provided is too long for the colt random generator; it will be truncated ", getClass());
			}
			coltRandom = new MersenneTwister(seed.intValue());			
		} else {
			coltRandom = new MersenneTwister();	
		}
		Uniform uniform = new Uniform(coltRandom);
		
		// for each place...
		for (int placeIdx=0; (placeIdx < places) && (progress.getComputationState() != ComputationState.FINISHED_CANCEL); placeIdx++) {
			
			String originalVertexId = inGraph.getVertex(
					uniform.nextIntFromTo(
							0, (int)inGraph.getVerticesCount()-1							)
					);
			
			//System.err.println("processing place "+placeIdx+"/"+places+": "+originalVertexId);

			int linksToCreate = maxlinks;

			Set<String> verticesBaseProcessed = new LinkedHashSet<String>();
			Set<String> verticesBaseToProcess = new LinkedHashSet<String>(); 
			Map<String,Integer> vertex2distance = new HashMap<String,Integer>();
			verticesBaseToProcess.add(originalVertexId);
			vertex2distance.put(originalVertexId, 0);
			
			loopVertices: while (
					!verticesBaseToProcess.isEmpty() 
					&& (progress.getComputationState() != ComputationState.FINISHED_CANCEL) 
					) {
			
				// get a vertex to process
				Iterator<String> it = verticesBaseToProcess.iterator();
				String vertexBaseId = it.next();
				it.remove();
				verticesBaseProcessed.add(vertexBaseId);
				Integer currentDistance = vertex2distance.get(vertexBaseId);
			
				//System.err.println("processing node R "+vertexBaseId+" ("+currentDistance+")");
			
				// add all of its neighboors which are not too far
				Integer distanceNeighboor = currentDistance+1;
				Integer distanceNeighboor2 = currentDistance+2;
				loopN1: for (String neighboorId : outGraph.getNeighboors(vertexBaseId)) {
					
					//System.err.println("processing node R+1 "+neighboorId);
					// update its distance: me+1, or the lower previous value
					Integer currentDistanceNeighboor = vertex2distance.get(neighboorId);
					if (currentDistanceNeighboor == null || currentDistanceNeighboor > distanceNeighboor) {
						vertex2distance.put(neighboorId, distanceNeighboor);
						currentDistanceNeighboor = distanceNeighboor;
					}
					// add this vertex to the vertices to process
					if (	// if it was not already used as base,
							!verticesBaseProcessed.contains(neighboorId)
							&&
							// it is not too far, so it can be used as a base
							currentDistanceNeighboor <= horizon-2
							)
						verticesBaseToProcess.add(neighboorId);
										
					// list its neighboors 
					for (String neighboorId2th : outGraph.getNeighboors(neighboorId)) {
						
						//System.err.println("processing node R+2 "+neighboorId2th);

						// update its distance: me+2, or the lower previous value
						Integer currentDistanceNeighboor2 = vertex2distance.get(neighboorId2th);
						if (currentDistanceNeighboor2 == null || currentDistanceNeighboor2 > distanceNeighboor2) {
							vertex2distance.put(neighboorId2th, distanceNeighboor2);
							currentDistanceNeighboor2 = distanceNeighboor2;
						}
						
						// TODO remove, it should never come here
						// don't follow too far
						if (currentDistanceNeighboor2 > horizon)
							continue loopN1;
						
						// maybe create a edge ?
						if (
								(vertexBaseId != neighboorId2th)
								&&
								(!outGraph.containsEdge(vertexBaseId, neighboorId2th))
								&&
								(uniform.nextDoubleFromTo(0d, 1d) <= proba)
							) {
							//System.err.println("create link "+vertexBaseId+" -> "+neighboorId2th);
							outGraph.addEdge(vertexBaseId, neighboorId2th, shouldBeDirected);
							linksToCreate --;
							if (linksToCreate == 0)
								break loopVertices;
						}
					}
					
				}
					
			
				
			}
						
			long totalCreated = outGraph.getEdgesCount() - inGraph.getEdgesCount();
			messages.debugUser("created "+totalCreated+" edges", getClass());
			progress.incProgressMade(1);
		}
	
		result.setResult(DensifyLocallyAlgo.OUTPUT_GRAPH, outGraph);
		progress.setComputationState(ComputationState.FINISHED_OK);
	}

	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
		
	}

}
