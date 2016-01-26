package genlab.igraph.natjna;

import genlab.core.commons.NotImplementedException;
import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.commons.IGraphLibImplementation;

public class IGraphLibImplementationNative implements IGraphLibImplementation {

	public IGraphLibImplementationNative() {
		
	}

	/**
	 * Sets the list of messages, the seed (if any provided)
	 * @param lib
	 * @param seed
	 * @param messages
	 */
	protected void initializeLib(IGraphNativeLibrary lib, Long seed, ListOfMessages messages) {
		
		lib.setListOfMessages(messages);
		
		if (seed == null)
			return;
		
		// TODO !!!
		lib.setSeed(seed);
		
		messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
				+ "thus randomness of this network is somewhat dependant of these other processes. "
				+ "It is not possible to replay exactly this stochastic process.", 
				IGraphLibImplementationNative.class
				); 
		
	}
	
	public IGenlabGraph generateBarabasiAlbert(
			int size, int m, double power, double zeroAppeal, 
			boolean directed, boolean outputPref, IExecution execution, Long seed) {
		
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateBarabasiAlbert(
					size, 
					m, 
					power,
					zeroAppeal,
					directed,
					outputPref
					);
			
			// simplify it, as sometimes we do have loops
			lib.simplifyGraph(igraphGraph, true, true);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
		
	}

	@Override
	public IGenlabGraph generateErdosRenyiGNM(int size, double m,
			boolean directed, boolean allowLoops, IExecution execution, Long seed) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateErdosRenyiGNM(size, m, directed, allowLoops);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph generateForestFire(int size, double fw_prob,
			double bw_factor, int pambs, boolean directed,
			boolean simplifyMultiple, boolean simplifyLoops,
			IExecution execution, Long seed) {
		

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateForestFire(size, fw_prob, bw_factor, pambs, directed);
			
			if (simplifyMultiple || simplifyLoops) {
				lib.simplifyGraph(igraphGraph, simplifyMultiple, simplifyLoops);
			}
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}

	}

	@Override
	public IGenlabGraph generateInterconnectedIslands(int islands_n,
			int islands_size, double islands_pin, int n_inter,
			boolean simplifyLoops, boolean simplifyMultiplex,
			IExecution execution, Long seed) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateInterconnectedIslands(islands_n, islands_size, islands_pin, n_inter, simplifyLoops, simplifyMultiplex);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph generateWattsStrogatz(int size, int dimension,
			double proba, int nei, boolean allowLoops, boolean allowMultiple,
			IExecution execution, Long seed) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateWattsStrogatz(size, dimension, proba, nei, allowLoops, allowMultiple);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph generateGRG(int nodes, double radius, boolean torus,
			IExecution execution, Long seed) {
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateGRG(nodes, radius, torus);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph generateLCF(int nodes, int[] paramShifts, int repeats,
			IExecution execution) {
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			// generate
			igraphGraph = lib.generateLCF(nodes, paramShifts, repeats);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph simplifyGraph(IGenlabGraph g, boolean removeMultiple,
			boolean removeLoops, IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			lib.simplifyGraph(igraphGraph, removeMultiple, removeLoops);
			
			
			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}


	@Override
	public IGenlabGraph rewire(IGenlabGraph g, int count, IExecution execution, Long seed) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			lib.rewire(igraphGraph, count);
			
			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double computeAveragePathLength(IGenlabGraph g, IExecution execution) {
		
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeAveragePathLength(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public boolean computeIsomorphicm(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph1 = null;
		IGraphGraph igraphGraph2 = null;

		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph1 = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g1, execution);
			igraphGraph2 = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g2, execution);

			// generate
			return lib.computeIsomorphicm(igraphGraph1, igraphGraph2);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			if (igraphGraph1 != null)
				lib.clearGraphMemory(igraphGraph1);
			if (igraphGraph2 != null)
				lib.clearGraphMemory(igraphGraph2);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public boolean computeIsomorphismVF2(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int computeIsomorphismVF2Count(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public boolean computeVF2Isomorphicm(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int computeDiameter(IGenlabGraph g, IExecution execution) {
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeDiameter(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public boolean isConnected(IGenlabGraph g, IExecution execution) {
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.isConnected(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double[] computeNodeBetweeness(IGenlabGraph g, boolean directed, IExecution execution) {
		
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			//igraphGraph  = lib.generateEmpty(10, true);
			
			// generate
			return lib.computeNodeBetweeness(igraphGraph, directed);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double[] computeNodeBetweenessEstimate(IGenlabGraph g,
			boolean directed, double cutoff, IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeNodeBetweenessEstimate(igraphGraph, directed, cutoff);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double[] computeEdgeBetweeness(IGenlabGraph g, boolean directed,
			IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeEdgeBetweeness(igraphGraph, directed);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double[] computeEdgeBetweenessEstimate(IGenlabGraph g,
			boolean directed, double cutoff, IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeEdgeBetweenessEstimate(igraphGraph, directed, cutoff);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public int computeComponentsCount(IGenlabGraph g, IExecution execution) {
	
		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeComponentsCount(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	
	}

	@Override
	public int computeGiantCluster(IGenlabGraph g, IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeGiantCluster(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public Double computeGlobalClustering(IGenlabGraph g, IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeGlobalClustering(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public Double computeGlobalClusteringLocal(IGenlabGraph g,
			IExecution execution) {

		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			// ask the lib to transmit its information as the result of OUR computations
			lib.setListOfMessages(messages);
			
			igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(g, execution);

			// generate
			return lib.computeGlobalClusteringLocal(igraphGraph);
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public IGenlabGraph generateErdosRenyiGNP(int size, double proba,
			boolean directed, boolean loops, IExecution execution, Long seed) {


		final ListOfMessages messages = execution.getListOfMessages();

		IGraphNativeLibrary lib = new IGraphNativeLibrary();
				
		IGraphGraph igraphGraph = null;
				
		try {
			
			messages.debugUser("using the igraph native library version "+lib.getVersionString(), IGraphLibImplementationNative.class);
			
			initializeLib(lib, seed, messages);
			
			// generate
			igraphGraph = lib.generateErdosRenyiGNP(size, proba, directed, loops);
			
			IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, execution);

			messages.warnUser("please note that the random number generator of igraph is shared between all igraph process; "
					+ "thus randomness of this network is somewhat dependant of these other processes. "
					+ "It is not possible to replay exactly this stochastic process.", 
					IGraphLibImplementationNative.class
					); 
			
			return genlabGraph;
			
		} catch (RuntimeException e) {
			messages.errorTech("error during the igraph call: "+e.getMessage(), IGraphLibImplementationNative.class, e);
			throw new RuntimeException("error during the igraph call: "+e.getMessage(), e);
		} finally {
			// clear memory
			lib.clearGraphMemory(igraphGraph);
			lib.setListOfMessages(null);
		}
	}

	@Override
	public double[] computeNodeCloseness(IGenlabGraph genlabGraph, IExecution exec) {

		// TODO 
		throw new NotImplementedException();
		
		// return null;
	}

	@Override
	public double[] computeNodeAlphaCentrality(IGenlabGraph genlabGraph,
			IExecution exec) {
		throw new NotImplementedException();
	}

	@Override
	public void writeGraphEdgelist(IGenlabGraph g, String filename,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphPajek(IGenlabGraph g, String filename,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphGraphML(IGenlabGraph g, String filename,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphDot(IGenlabGraph g, String filename,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphLGL(IGenlabGraph g, String filename,
			IExecution execution, String attributeNameForEdgeWeights) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphNcol(IGenlabGraph g, String filename,
			IExecution execution, String attributeNameForEdgeWeights) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphGML(IGenlabGraph g, String filename,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void writeGraphLeda(IGenlabGraph g, String filename,
			IExecution execution, String attributeNameForVertexAttribute,
			String attributeNameForEdgeAttribute) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

}
