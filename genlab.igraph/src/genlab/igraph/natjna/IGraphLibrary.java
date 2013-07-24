package genlab.igraph.natjna;

import java.util.Arrays;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
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
