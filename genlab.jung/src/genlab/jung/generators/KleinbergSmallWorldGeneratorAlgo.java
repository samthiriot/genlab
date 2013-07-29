package genlab.jung.generators;

import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.BooleanParameter;
import genlab.jung.utils.Converters;

import org.apache.commons.collections15.Factory;

public class KleinbergSmallWorldGeneratorAlgo extends AbstractJungGeneratorAlgo {

	public final static IntegerInOut INPUT_ROWCOUNT = new IntegerInOut(
			"in_rowcount", 
			"rows count", 
			"number of rows in the initial lattice"
			);
	
	public final static IntegerInOut INPUT_COLCOUNT = new IntegerInOut(
			"in_colcount", 
			"columns count", 
			"number of columns in the initial lattice"
			);
	
	public final static DoubleInOut INPUT_CLUSTERING = new DoubleInOut(
			"in_clustering", 
			"clustering", 
			"clustering exponent"
			);
	
	public final static BooleanParameter PARAM_TORUS = new BooleanParameter(
			"param_torus", 
			"torus", 
			"create a toroidal lattice", 
			false
			);
	
	public KleinbergSmallWorldGeneratorAlgo() {
		super(
				"Kleinberg small-world (JUNG)", 
				"Graph generator that produces a random graph with small world properties. The underlying model is an mxn (optionally toroidal) lattice. Each node u has four local connections, one to each of its neighbors, and in addition one long range connection to some node v where v is chosen randomly according to probability proportional to d^-alpha where d is the lattice distance between u and v and alpha is the clustering exponent."
				);
		inputs.add(INPUT_ROWCOUNT);
		inputs.add(INPUT_COLCOUNT);
		inputs.add(INPUT_CLUSTERING);
		
		registerParameter(PARAM_TORUS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractJungGeneratorExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 100;
			}
			
			@Override
			protected IGenlabGraph generate() {
				
				int rowCount = (Integer) getInputValueForInput(INPUT_ROWCOUNT);
				int columnsCount = (Integer) getInputValueForInput(INPUT_COLCOUNT);
				double clusteringComponents = (Double)getInputValueForInput(INPUT_CLUSTERING);
				boolean torus = (Boolean)algoInst.getValueForParameter(PARAM_TORUS.getId());
				
				final IGenlabGraph glGraph = GraphFactory.createGraph("generated", GraphDirectionality.UNDIRECTED, false);
				final Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphWritable(glGraph);
				
				KleinbergSmallWorldGenerator<String,String> generator = new KleinbergSmallWorldGenerator<String, String>(
						new Factory<Graph<String,String>>() {

							@Override
							public Graph<String,String> create() {
								return jungGraph;
							}
						},
						new Factory<String>() {

							@Override
							public String create() {
								// vertex
								return Integer.toString((int)glGraph.getVerticesCount());
							}
						},
						new Factory<String>() {

							@Override
							public String create() {
								// edge
								return Integer.toString((int)glGraph.getEdgesCount());
							}
							
						},
						rowCount,
						columnsCount,
						clusteringComponents,
						torus
						);
				
				Graph<String,String> jungGraph2 = generator.create();
				
				if (jungGraph2 != jungGraph)
					throw new ProgramException("the generated graph is not the one provided by our factory");
				
				return glGraph;
			}
		};
	}

}
