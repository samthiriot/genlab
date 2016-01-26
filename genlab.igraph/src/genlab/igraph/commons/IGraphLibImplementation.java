package genlab.igraph.commons;

import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public interface IGraphLibImplementation {


	public IGenlabGraph generateErdosRenyiGNM(
			int size, double m, boolean directed, boolean allowLoops, 
			IExecution execution, Long seed);
			
	public IGenlabGraph generateForestFire(
			int size, double fw_prob, double bw_factor,
		    int pambs, boolean directed, 
		    boolean simplifyMultiple,
		    boolean simplifyLoops,
			IExecution execution,
			Long seed);
	
	public IGenlabGraph generateBarabasiAlbert(
			int size, int m, double power, double zeroAppeal, 
			boolean directed, boolean outputPref,  
			IExecution execution,
			Long seed);
	

	public IGenlabGraph generateInterconnectedIslands(
			int islands_n, 
			int islands_size,
			double islands_pin, 
			int n_inter,
			boolean simplifyLoops,
			boolean simplifyMultiplex, 
			IExecution execution,
			Long seed
			);
	
	public IGenlabGraph generateWattsStrogatz(
			int size, int dimension, double proba, int nei, boolean allowLoops, boolean allowMultiple, 
			IExecution execution,
			Long seed);
	
	public IGenlabGraph generateGRG(
			int nodes, double radius, boolean torus, 
			IExecution execution,
			Long seed);
	
	public IGenlabGraph generateLCF(
			int nodes, int[] paramShifts, int repeats, 
			IExecution execution);
	

	public IGenlabGraph simplifyGraph(
			IGenlabGraph g,
			boolean removeMultiple, boolean removeLoops, 
			IExecution execution
			);


	public IGenlabGraph generateErdosRenyiGNP(
			int size, double proba,
			boolean directed, boolean loops,
			IExecution execution,
			Long seed);
	

	public IGenlabGraph rewire(
			IGenlabGraph g, int count, 
			IExecution execution,
			Long seed
			);
	
	public double computeAveragePathLength(IGenlabGraph g, 
			IExecution execution);
	
	public boolean computeIsomorphicm(IGenlabGraph g1, IGenlabGraph g2, 
			IExecution execution);
	
	public boolean computeIsomorphismVF2(IGenlabGraph g1, IGenlabGraph g2, 
			IExecution execution);
	
	public int computeIsomorphismVF2Count(IGenlabGraph g1, IGenlabGraph g2, 
			IExecution execution);
	
	public boolean computeVF2Isomorphicm(IGenlabGraph g1, IGenlabGraph g2, 
			IExecution execution);
	
	public int computeDiameter(IGenlabGraph g, 
			IExecution execution);
	
	public boolean isConnected(IGenlabGraph g, 
			IExecution execution);
		
	public double[] computeNodeBetweeness(
			IGenlabGraph g, boolean directed,  
			IExecution execution);
	
	public double[] computeNodeBetweenessEstimate(
			IGenlabGraph g, boolean directed, double cutoff, 
			IExecution execution);
	
	public double[] computeNodeCloseness(IGenlabGraph genlabGraph, IExecution exec);

	public double[] computeNodeAlphaCentrality(IGenlabGraph genlabGraph,
			IExecution exec);


	public double[] computeEdgeBetweeness(
			IGenlabGraph g, boolean directed, 
			IExecution execution);
	
	public double[] computeEdgeBetweenessEstimate(
			IGenlabGraph g, boolean directed, double cutoff, 
			IExecution execution);
	
	public int computeComponentsCount(
			IGenlabGraph g, 
			IExecution execution);
	
	public int computeGiantCluster(
			IGenlabGraph g, 
			IExecution execution);
	
	public Double computeGlobalClustering(
			IGenlabGraph g, 
			IExecution execution);
	
	public Double computeGlobalClusteringLocal(
			IGenlabGraph g, 
			IExecution execution);
	
	public void writeGraphEdgelist(
			IGenlabGraph g, 
			String filename, 
			IExecution execution
			);

	public void writeGraphPajek(
			IGenlabGraph g, 
			String filename, 
			IExecution execution
			);

	public void writeGraphGraphML(
			IGenlabGraph g, 
			String filename, 
			IExecution execution
			);

	public void writeGraphDot(
			IGenlabGraph g, 
			String filename, 
			IExecution execution
			);

	public void writeGraphLGL(
			IGenlabGraph g, 
			String filename, 
			IExecution execution, 
			String attributeNameForEdgeWeights
			);

	public void writeGraphNcol(
			IGenlabGraph g, 
			String filename, 
			IExecution execution, 
			String attributeNameForEdgeWeights
			);

	public void writeGraphGML(
			IGenlabGraph g, 
			String filename, 
			IExecution execution
			);

	public void writeGraphLeda(
			IGenlabGraph g, 
			String filename, 
			IExecution execution, 
			String attributeNameForVertexAttribute, 
			String attributeNameForEdgeAttribute
			);
	

	
}
