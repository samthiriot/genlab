package genlab.jung.generators;

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.algorithms.generators.EvolvingGraphGenerator;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
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

public class BarabasiAlbertGeneratorAlgo extends AbstractJungGeneratorAlgo {

	public final static IntegerInOut INPUT_N = new IntegerInOut(
			"in_iterations", 
			"iterations", 
			"number of iterations"
			);
	
	public final static IntegerInOut INPUT_INITVERTICES = new IntegerInOut(
			"in_numvertices", 
			"initial vertices", 
			"number of initial vertices"
			);
	
	public final static IntegerInOut INPUT_NUMEDGES = new IntegerInOut(
			"in_edgescount", 
			"m", 
			"edges to add at each iteration"
			);
	
	
	public BarabasiAlbertGeneratorAlgo() {
		super(
				"Barabasi-Albert (JUNG)", 
				"Simple evolving scale-free random graph generator. At each time step, a new vertex is created and is connected to existing vertices according to the principle of \"preferential attachment\", whereby vertices with higher degree have a higher probability of being selected for attachment."
				);
		inputs.add(INPUT_INITVERTICES);
		inputs.add(INPUT_NUMEDGES);
		inputs.add(INPUT_N);
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
				
				// TODO check params !
				
				final IGenlabGraph glGraph = GraphFactory.createGraph("generated", GraphDirectionality.UNDIRECTED, false);
				final Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphWritable(glGraph);
				
				Set<String> seed = new HashSet<String>();
				BarabasiAlbertGenerator<String,String> generator = new BarabasiAlbertGenerator<String, String>(
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
						
						(Integer)getInputValueForInput(INPUT_INITVERTICES),
						(Integer)getInputValueForInput(INPUT_NUMEDGES),
						seed
						);
				
				Graph<String,String> jungGraph2 = generator.create();
				
				if (jungGraph2 != jungGraph)
					throw new ProgramException("the generated graph is not the one provided by our factory");
				
				generator.evolveGraph((Integer)getInputValueForInput(INPUT_N));
				
				return glGraph;
			}
		};
	}

}
