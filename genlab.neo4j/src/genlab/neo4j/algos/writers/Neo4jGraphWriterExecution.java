package genlab.neo4j.algos.writers;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jGraphWriterExecution extends AbstractAlgoExecution {

	private static final long UPDATE_PROGRESS_EVERY = 20;
	
	public Neo4jGraphWriterExecution(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}
	
	public enum UniplexTypes implements RelationshipType {
		LINK
	}

	@Override
	public void run() {
		
		final String paramFile = "/tmp/truc.neo4j";
		
		// notify start
		progress.setProgressMade(0);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		

		// retrieve inputs 
		// decode parameters
		final IGenlabGraph genLabGraph = (IGenlabGraph) getInputValueForInput(Neo4jGraphWriter.PARAM_GRAPH);
		
		// check parameters
		if (genLabGraph.getVerticesCount() > Integer.MAX_VALUE)
	    	throw new WrongParametersException("unable to process graphs having more than "+Integer.MAX_VALUE+" nodes");
	    
		// start !
		progress.setProgressTotal(genLabGraph.getEdgesCount()+genLabGraph.getVerticesCount());
		long done = 0;
		
		
		final long startTime = System.currentTimeMillis();
		
		// create database
    	GLLogger.debugTech("creating a neo4j database in "+paramFile, getClass());
    	
	    GraphDatabaseService graphDb = null;
	    try {
	    	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( paramFile );
	    } catch (RuntimeException e)  {
	    	progress.setComputationState(ComputationState.FINISHED_FAILURE);
	    	result.getMessages().errorUser("error during the initialization of the database", getClass(), e);
	    	return;
	    }
	    
	    Map<String,Node> genlab2neo4jVertexId = new HashMap<String, Node>((int)genLabGraph.getVerticesCount());
	    Transaction tx = graphDb.beginTx();
        try
        {

        	// copy nodes
        	for (String nodeId: genLabGraph.getVertices()) {
        		
        		Node neoNode = graphDb.createNode();
        		
        		genlab2neo4jVertexId.put(nodeId, neoNode);
        		
        		// ... and attributes
        		neoNode.setProperty("label", nodeId);
        		for (Map.Entry<String,Object> genlabAttribute : genLabGraph.getVertexAttributes(nodeId).entrySet()) {
        			neoNode.setProperty(
        					genlabAttribute.getKey(),
        					genlabAttribute.getValue()
        					);
        		}
        		
        		done ++;
        		if (done % UPDATE_PROGRESS_EVERY == 0) // don't update too often (coslty !)
        			progress.setProgressMade(done);
        		
        	}
        	
			progress.setProgressMade(done);

        	// copy edges
        	for (String edgeId: genLabGraph.getEdges()) {
        		
        		final String genlabFromId = genLabGraph.getEdgeVertexFrom(edgeId);
        		final String genlabToId = genLabGraph.getEdgeVertexTo(edgeId);
        		
        		final Node neoNodeFrom = genlab2neo4jVertexId.get(genlabFromId);
        		final Node neoNodeTo = genlab2neo4jVertexId.get(genlabFromId);
        		
        		Relationship neoRel = neoNodeFrom.createRelationshipTo(neoNodeTo, UniplexTypes.LINK);
        		
        		System.err.println("created "+neoRel);
        		// copy attributes
        		neoRel.setProperty("label", edgeId);
        		for (Map.Entry<String,Object> genlabAttribute : genLabGraph.getEdgeAttributes(edgeId).entrySet()) {
        			neoRel.setProperty(
        					genlabAttribute.getKey(),
        					genlabAttribute.getValue()
        					);
        		}
        		
        		done ++;
        		if (done % UPDATE_PROGRESS_EVERY == 0) // don't update too often (coslty !)
        			progress.setProgressMade(done);
        		
        	}
        	
        	progress.setProgressMade(done);
        	
            // end of transaction
            tx.success();

            // basic checks
            // TODO how to read ?
            //ExecutionEngine engine = new ExecutionEngine( graphDb );
            //ExecutionResult result = engine.execute( "start n=node(*) where n.name! = 'my node' return n, n.name" );
            
    		progress.setComputationState(ComputationState.FINISHED_OK);

            
        } catch (Throwable e) {
        	result.getMessages().errorTech(
        			"an exception was catched during the graph writing: "+e.getMessage(), 
        			getClass(), 
        			e
        			);
        	progress.setComputationState(ComputationState.FINISHED_FAILURE);
        } finally {
        
        	GLLogger.debugTech("closing neo4j database...", getClass());
            tx.finish();
            graphDb.shutdown();
        }
        
        GLLogger.debugTech("graph wrote into a neo4j database in "+(System.currentTimeMillis()-startTime)+" ms", getClass());
	    
		progress.setProgressMade(1);


	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		return 1000*30;
	}

	

}
