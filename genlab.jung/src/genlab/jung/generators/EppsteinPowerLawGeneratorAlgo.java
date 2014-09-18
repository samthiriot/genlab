package genlab.jung.generators;

import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.graph.Graph;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.jung.utils.Converters;

import org.apache.commons.collections15.Factory;

public class EppsteinPowerLawGeneratorAlgo extends AbstractJungGeneratorAlgo {

	public final static IntegerInOut INPUT_NUMVERTICES = new IntegerInOut(
			"in_numvertices", 
			"num vertices", 
			"number of vertices",
			200
			);
	
	public final static IntegerInOut INPUT_NUMEDGES = new IntegerInOut(
			"in_edgescount", 
			"edges count", 
			"number of edges",
			400
			);
	
	public final static IntegerInOut INPUT_R = new IntegerInOut(
			"in_R", 
			"r", 
			"the number of iterations to use; the larger the value the better the graph's degree distribution will approximate a power-law",
			50
			);
	
	public EppsteinPowerLawGeneratorAlgo() {
		super(
				"Eppstein power-law (JUNG)", 
				"Graph generator that generates undirected graphs with power-law degree distributions."
				);
		inputs.add(INPUT_NUMVERTICES);
		inputs.add(INPUT_NUMEDGES);
		inputs.add(INPUT_R);
		
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
				
				final IGenlabGraph glGraph = GraphFactory.createGraph("generated", GraphDirectionality.UNDIRECTED, false);
				final Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphWritable(glGraph);
				
				EppsteinPowerLawGenerator<String,String> generator = new EppsteinPowerLawGenerator<String, String>(
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
						(Integer)getInputValueForInput(INPUT_NUMVERTICES),
						(Integer)getInputValueForInput(INPUT_NUMEDGES),
						(Integer)getInputValueForInput(INPUT_R)
						);
				
				Graph<String,String> jungGraph2 = generator.create();
				
				if (jungGraph2 != jungGraph)
					throw new ProgramException("the generated graph is not the one provided by our factory");
				
				return glGraph;
			}
		};
	}

}
