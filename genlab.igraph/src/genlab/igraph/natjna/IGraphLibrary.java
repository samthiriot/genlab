package genlab.igraph.natjna;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import java.util.Iterator;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO a pool for igraph libs
public class IGraphLibrary {

	private final IGraphRawLibrary rawLib;
	
	public String versionString = null; 
	
	public final static String GRAPH_KEY_DIAMETER = "diameter";
	public final static String GRAPH_KEY_AVERAGE_PATH_LENGTH = "average path length";
	
	public final static String GRAPH_KEY_CONNECTED = "connected";
	public final static String GRAPH_KEY_COMPONENTS_COUNT = "components.count";
	public final static String GRAPH_KEY_COMPONENTS_GIANT_SIZE = "components.giant.size";
	public final static String GRAPH_KEY_CLUSTERING_GLOBAL = "stats.clustering.global";
	public final static String GRAPH_KEY_CLUSTERING_GLOBAL_AVG = "stats.clustering.global.avg";

	/**
	 * Stores a double array containing for each vertex the id of its cluster.
	 */
	public final static String GRAPH_KEY_COMPONENTS_MEMBERSHIP = "components.giant.membership";

	/**
	 * Stores a double array containing for each component id its size.
	 */
	public final static String GRAPH_KEY_COMPONENTS_CLUSTER_SIZES = "components.giant.sizes";

	
	public final boolean paramUseCache = true;
	
	protected ListOfMessages listOfMessages = ListsOfMessages.getGenlabMessages();
	
	// TODO errors !
	//private final static int IGRAPH_ERROR_IGRAPH_ENOMEM = ;
	
	public IGraphLibrary() {

		//GLLogger.debugTech("init igraph native library...", getClass());
		
		if (!IGraphRawLibrary.isAvailable) 
			throw new ProgramException("unable to use the igraph native library");
		
		rawLib = new IGraphRawLibrary();
		
		retrieveVersion();
		
		//GLLogger.debugTech("detected version: "+versionString, getClass());
		
	}


	/**
	 * Retrieves the version and stores it into attributes. 
	 * Is supposed to be called only once.
	 */
	private void retrieveVersion() {

		PointerByReference str = new PointerByReference();
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference subminor = new IntByReference();
		
		rawLib.igraph_version(str, major, minor, subminor);
				
		Pointer p = str.getValue();
		versionString = p.getString(0);

	}

	/**
	 * Give this library a place to export information to the user.
	 * If null, this will fall back to the main one.
	 * @param messages
	 */
	public void setListOfMessages(ListOfMessages messages) {
		if (messages == null)
			listOfMessages = ListsOfMessages.getGenlabMessages();
		else
			listOfMessages = messages;
	}
	
	/**
	 * Returns the igraph version string (efficient, 
	 * because it is cached locally)
	 * @return
	 */
	public String getVersionString() {
		return versionString;
	}
	
	protected IGraphRawLibrary.InternalGraphStruct createEmptyGraph() {
		return new IGraphRawLibrary.InternalGraphStruct(rawLib);
	}
	
	public void clearGraphMemory(IGraphGraph g) {
		
		if (rawLib == null)
			return;
		
		if (g == null)
			return;
		
		rawLib.igraph_destroy(g.getPointer());
	}
	
	/**
	 * Check the integer code returned by igraph.
	 * @param code
	 */
	protected void checkIGraphResult(int code) {
		
		if (code != 0)
			// TODO !
			throw new ProgramException("error during the computation");
		
	}
	
	public IGraphGraph generateErdosRenyiGNP(int size, double proba, boolean directed, boolean allowLoops) {

		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_erdos_renyi_game_gnp(
				g,
				size,
				proba,
				directed,
				allowLoops
		);
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		listOfMessages.debugTech("processing took "+duration+" ms", getClass());
		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		// basic checks
		// TODO
		
		return result;
		
	}
	

	public IGraphGraph generateErdosRenyiGNM(int size, double m, boolean directed, boolean allowLoops) {

		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_erdos_renyi_game_gnm(
				g,
				size,
				m,
				directed,
				allowLoops
		);
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		listOfMessages.debugTech("processing took "+duration+" ms", getClass());
		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		// basic checks
		// TODO
		
		return result;
		
	}
	
	public IGraphGraph generateForestFire(int size, double fw_prob, double bw_factor,
		    int pambs, boolean directed) {

		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_forest_fire_game(
				g, 
				size, 
				fw_prob, 
				bw_factor, 
				pambs, 
				directed
				);
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		listOfMessages.debugTech("processing took "+duration+" ms", getClass());
		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		// basic checks
		// TODO
		
		return result;
		
	}
	
	public IGraphGraph generateInterconnectedIslands(
			int islands_n, 
			int islands_size,
			double islands_pin, 
			int n_inter) {

		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_simple_interconnected_islands_game(g, islands_n, islands_size, islands_pin, n_inter);
		
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		listOfMessages.debugTech("processing took "+duration+" ms", getClass());
		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, false);
		
		// basic checks
		// TODO
		
		return result;
		
	}
	
	public IGraphGraph generateWattsStrogatz(int size, int dimension, double proba, int nei, boolean directed, boolean allowLoops) {
		
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
		
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		int res = rawLib.igraph_watts_strogatz_game(
				g,
				dimension, 
				size, 
				nei, 
				proba, 
				directed, 
				allowLoops
				);
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		return result;
	}
	
	public IGraphGraph generateGRG(int nodes, double radius, boolean torus) {
		
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
		
		IGraphRawLibrary.Igraph_vector_t x = new IGraphRawLibrary.Igraph_vector_t();
		rawLib.igraph_vector_init(x, 0);
		IGraphRawLibrary.Igraph_vector_t y = new IGraphRawLibrary.Igraph_vector_t();
		rawLib.igraph_vector_init(y, 0);
		
		try {
		
			//GLLogger.debugTech("calling igraph", getClass());
			final long startTime = System.currentTimeMillis();
			int res = rawLib.igraph_grg_game(
					g,
					nodes,
					radius,
					torus,
					x,
					y
					);
			final long duration = System.currentTimeMillis() - startTime;
			//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
	
			// detect errors
			checkIGraphResult(res);
			
			IGraphGraph result = new IGraphGraph(this, rawLib, g, false);
			result.xPositions = x.asDoubleArray(nodes);
			result.yPositions = y.asDoubleArray(nodes);
					
			return result;

		} finally {
			rawLib.igraph_vector_destroy(x);
			rawLib.igraph_vector_destroy(y);
		}
	}
	
	public IGraphGraph generateLCF(int nodes, int[] paramShifts, int repeats) {
		
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
		
		if (repeats < 1)
			throw new WrongParametersException("argument repeat should be positive");
		if (nodes < 1)
			throw new WrongParametersException("argument nodes should be positive");

		// init the shifts param
		IGraphRawLibrary.Igraph_vector_t shifts = new IGraphRawLibrary.Igraph_vector_t();
		{
			Pointer shitsPointer = new Memory(paramShifts.length * Native.getNativeSize(Double.TYPE));
			for (int dloop=0; dloop<paramShifts.length; dloop++) {
				// populate the array with junk data (just for the sake of the example)
				shitsPointer.setDouble(dloop * Native.getNativeSize(Double.TYPE), (double)paramShifts[dloop]);
			}
			rawLib.igraph_vector_init_copy(shifts, shitsPointer, paramShifts.length);

		}

		try {
		
			//GLLogger.debugTech("calling igraph", getClass());
			final long startTime = System.currentTimeMillis();
			int res = rawLib.igraph_lcf_vector(
					g, 
					nodes, 
					shifts, 
					repeats
					);
			final long duration = System.currentTimeMillis() - startTime;
			//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
	
			// detect errors
			checkIGraphResult(res);
			
			IGraphGraph result = new IGraphGraph(this, rawLib, g, false);

			return result;

		} finally {
			rawLib.igraph_vector_destroy(shifts);
		}
	}
	

	public IGraphGraph copyGraph(IGraphGraph original) {

		final IGraphRawLibrary.InternalGraphStruct theCopy = createEmptyGraph();

		rawLib.igraph_copy(original.getStruct(), theCopy);
		
		IGraphGraph theCopyRes = new IGraphGraph(original, this, rawLib, theCopy);
		
		return theCopyRes;
		
	}
	
	public IGraphGraph generateEmpty(int size, boolean directed) {
	
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		//GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_empty(
				g, 
				size, 
				directed
				);
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed, size);

		// basic checks
		// TODO
		
		return result;
	}
	
	
	public IGraphGraph generateErdosRenyiGNP(int size, double proba) {
		return generateErdosRenyiGNP(size, proba, false, false);
	}
	
	public void addEdge(IGraphGraph g, int from, int to) {
		
		g.graphChanged();
		
		if (from < 0 || to < 0)
			throw new WrongParametersException("ids of nodes should be positive");
		
		// TODO check parameters
		
		final int res = rawLib.igraph_add_edge(g.getPointer(), from, to);
		
		checkIGraphResult(res);
	}
	
	
	public void rewire(IGraphGraph g, int count) {
		
		g.graphChanged();
		
		if (count <= 0)
			throw new WrongParametersException("count of nodes should be positive");
		
		final int res = rawLib.igraph_rewire(g.getPointer(), count, 0);
		
		checkIGraphResult(res);
	}
	
	public int getVertexCount(IGraphGraph g) {
		
		return rawLib.igraph_vcount(g.getPointer());
	}
	
	public int getVertexCount(IGraphRawLibrary.InternalGraphStruct g) {
		
		return rawLib.igraph_vcount(g.getPointer());
	}
	

	public int getEdgeCount(IGraphGraph g) {
				
		return rawLib.igraph_ecount(g.getPointer());
	}
	
	
	public boolean isDirected(IGraphGraph g) {
	
		return rawLib.igraph_is_directed(g.getPointer());
		
	}
	
	public double computeAveragePathLength(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_AVERAGE_PATH_LENGTH)) {
			return (Double) g.getCachedProperty(GRAPH_KEY_AVERAGE_PATH_LENGTH);
		}
		
		DoubleByReference res = new DoubleByReference();
		
		//GLLogger.debugTech("calling igraph to compute average path length...", getClass());

		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_average_path_length(g.getPointer(), res, g.directed, true);

		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		
		checkIGraphResult(res2);
		
		final double length = res.getValue();
		
		g.setCachedProperty(GRAPH_KEY_AVERAGE_PATH_LENGTH, length);
		
		//System.err.println("igraph/ average path length: "+length);

		return length;
		
	}
	
	public boolean computeIsomorphicm(IGraphGraph g1, IGraphGraph g2) {
		
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute average path length...", getClass());

		final long startTime = System.currentTimeMillis();

		
		final int res2 = rawLib.igraph_isomorphic(g1.getPointer(), g2.getPointer(), res);

		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		final Boolean iso = (res.getValue() != 0);
		

		return iso;
		
	}
	
	public boolean computeIsomorphismVF2(IGraphGraph g1, IGraphGraph g2) {
		
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute average path length...", getClass());

		final long startTime = System.currentTimeMillis();

		
		final int res2 = rawLib.igraph_isomorphic_vf2(
				g1.getPointer(), g2.getPointer(), 
				null, null, null, null, 
				res, 
				null, null, 
				null, 
				null, null
				);

		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		final Boolean iso = (res.getValue() != 0);
		

		return iso;
		
	}
	
	
	public int computeIsomorphismVF2Count(IGraphGraph g1, IGraphGraph g2) {
		
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute average path length...", getClass());

		final long startTime = System.currentTimeMillis();

		
		final int res2 = rawLib.igraph_count_isomorphisms_vf2(
				g1.getPointer(), g2.getPointer(), 
				null, null, null, null, 
				res, 
				null, null, 
				null
				);

		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		return res.getValue();
	
	}
	
	public boolean computeVF2Isomorphicm(IGraphGraph g1, IGraphGraph g2) {
		
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute average path length...", getClass());

		final long startTime = System.currentTimeMillis();

		
		final int res2 = rawLib.igraph_isomorphic(g1.getPointer(), g2.getPointer(), res);

		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		final Boolean iso = res.getValue()==0;
		

		return iso;
		
	}
	

	public int computeDiameter(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_DIAMETER)) {
			return (Integer) g.getCachedProperty(GRAPH_KEY_DIAMETER);
		}
		
		// compute 
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute the diameter...", getClass());

		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_diameter(
				g.getPointer(), 
				res,
				null, 
				null, 
				null, 
				g.directed, 
				true
				);
		
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		
		// use result
		final int length = res.getValue();
		
		// store in cache
		g.setCachedProperty(GRAPH_KEY_DIAMETER, new Integer(length));
		
		//System.err.println("igraph/ diameter: "+length);

		return length;
		
	}
	

	public boolean isConnected(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_CONNECTED)) {
			return (Boolean) g.getCachedProperty(GRAPH_KEY_CONNECTED);
		}
		
		IntByReference res = new IntByReference();
		
		//GLLogger.debugTech("calling igraph to compute the diameter...", getClass());

		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_is_connected(
				g.getPointer(), 
				res,
				1
				);
		
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		final boolean connected = res.getValue()>0;
		//System.err.println("igraph/ connected: "+connected);

		g.setCachedProperty(GRAPH_KEY_CONNECTED, new Boolean(connected));

		return connected;
		
	}
	

	protected void computeComponentThings(IGraphGraph g) {

		IntByReference res = new IntByReference();
		

		final long startTime = System.currentTimeMillis();
		
		//GLLogger.debugTech("calling igraph to initialize vectors...", getClass());

		IGraphRawLibrary.Igraph_vector_t membership = new IGraphRawLibrary.Igraph_vector_t();
		rawLib.igraph_vector_init(membership, 0);
		IGraphRawLibrary.Igraph_vector_t csize = new IGraphRawLibrary.Igraph_vector_t();
		rawLib.igraph_vector_init(csize, 0);
		IntByReference count = new IntByReference();
		
		try {
			//GLLogger.debugTech("calling igraph to compute the clusters...", getClass());
	
			final int res2 = rawLib.igraph_clusters(
					g.getPointer(), 
					membership, 
					csize, 
					count, 
					1
					);
			
			final long duration = System.currentTimeMillis() - startTime;
			//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
			
			checkIGraphResult(res2);
			
			// process and store results
			
			// count
			{
				final int countInt = count.getValue();
				//System.err.println("igraph/ components: "+countInt);
				g.setCachedProperty(GRAPH_KEY_COMPONENTS_COUNT, new Integer(countInt));
			
			}
			
			// memberships
			{
				final int membershipSize = rawLib.igraph_vector_size(membership);
				final double[] memberships = membership.asDoubleArray(membershipSize);
				g.setCachedProperty(GRAPH_KEY_COMPONENTS_MEMBERSHIP, membership);
			}
			
			// size
			{
				final int csizeSize = rawLib.igraph_vector_size(csize);
				final double[] csizes = csize.asDoubleArray(csizeSize);
				g.setCachedProperty(GRAPH_KEY_COMPONENTS_CLUSTER_SIZES, csizes);
				double max = 0;
				for (int i=0; i<csizes.length; i++) {
					max = Math.max(csizes[i], max);
				}
				//System.err.println("igraph/ giant cluster: "+max);
				g.setCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE, new Integer((int)max));
			}
			
		} finally {
			rawLib.igraph_vector_destroy(csize);
			rawLib.igraph_vector_destroy(membership);
		}
		
	}
	
	public int computeComponentsCount(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_COMPONENTS_COUNT)) {
			return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_COUNT);
		}
		
		computeComponentThings(g);
		
		return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_COUNT);
		
	}
	
	public int computeGiantCluster(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE)) {
			return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE);
		}
		
		computeComponentThings(g);
		
		return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE);
		
	}
	
	public Double computeGlobalClustering(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL)) {
			return (Double) g.getCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL);
		}
		
		// compute 
		DoubleByReference res = new DoubleByReference();
		
		//GLLogger.debugTech("calling igraph to compute the global clustering...", getClass());

		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_transitivity_undirected(
				g.getPointer(), 
				res,
				0
				);
		
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		// use result
		final double clustering = res.getValue();
		
		// store in cache
		g.setCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL, new Double(clustering));
		
		//System.err.println("igraph/ clustering global: "+clustering);

		return clustering;
		
	}
	
	public Double computeGlobalClusteringLocal(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL_AVG)) {
			return (Double) g.getCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL_AVG);
		}
		
		// compute 
		DoubleByReference res = new DoubleByReference();
		
		//GLLogger.debugTech("calling igraph to compute the average clustering...", getClass());

		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_transitivity_avglocal_undirected(
				g.getPointer(), 
				res,
				0
				);
		
		final long duration = System.currentTimeMillis() - startTime;
		//GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());
		
		checkIGraphResult(res2);
		
		// use result
		final double clustering = res.getValue();
		
		// store in cache
		g.setCachedProperty(GRAPH_KEY_CLUSTERING_GLOBAL_AVG, new Float(clustering));
		
		//System.err.println("igraph/ average clustering: "+clustering);

		return clustering;
		
	}
	
	
	protected class EdgesIterator implements Iterator<IGraphEdge> {

		private final IGraphGraph g;
		
		int lastEdgeId = 0;
		final int maxEdge;
		
		public EdgesIterator(IGraphGraph g) {
			this.g = g;
			maxEdge = getEdgeCount(g);
		}
		
		@Override
		public boolean hasNext() {
			
			return lastEdgeId < maxEdge;
		}

		@Override
		public IGraphEdge next() {
			IntByReference from = new IntByReference();
			IntByReference to = new IntByReference();
			rawLib.igraph_edge(g.getPointer(), lastEdgeId, from, to);
			
			return new IGraphEdge(lastEdgeId++, from.getValue(), to.getValue());
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}
		
	}

	public Iterator<IGraphEdge> getEdgeIterator(IGraphGraph g) {
		return new EdgesIterator(g);
	}
	
	/*
	public IGenlabGraph computeClusterInfos(IGraphGraph g) {
		
		if (paramUseCache && g.hasCachedProperty(GRAPH_KEY_COMPONENTS_)) {
			return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE);
		}
		
		computeComponentThings(g);
		
		return (Integer) g.getCachedProperty(GRAPH_KEY_COMPONENTS_GIANT_SIZE);
		
	}
	*/
	
}
