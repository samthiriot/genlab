package genlab.igraph.natjna;

import java.util.HashMap;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.GLLogger;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class IGraphLibrary {

	private final IGraphRawLibrary rawLib;
	
	public String versionString = null; 
	
	public IGraphLibrary() {

		GLLogger.debugTech("init igraph native library...", getClass());
		
		rawLib = new IGraphRawLibrary();
		
		retrieveVersion();
		
		GLLogger.debugTech("detected version: "+versionString, getClass());
		
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
	
	protected void checkIGraphResult(int code) {
		
		if (code != 0)
			// TODO !
			throw new ProgramException("error during the computation");
		
	}
	
	public IGraphGraph generateErdosRenyiGNP(int size, double proba, boolean directed, boolean allowLoops) {

		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_erdos_renyi_game_gnp(
				g,
				size,
				proba,
				directed,
				allowLoops
		);
		final long duration = System.currentTimeMillis() - startTime;
		GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		// basic checks
		// TODO
		
		return result;
		
	}
	

	
	public IGraphGraph generateWattsStrogatz(int size, int dimension, double proba, int nei, boolean directed, boolean allowLoops) {
		
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
		
		GLLogger.debugTech("calling igraph", getClass());
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
		GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		// detect errors
		checkIGraphResult(res);
		
		IGraphGraph result = new IGraphGraph(this, rawLib, g, directed);
		
		return result;
	}
	

	public IGraphGraph copyGraph(IGraphGraph original) {

		final IGraphRawLibrary.InternalGraphStruct theCopy = createEmptyGraph();

		rawLib.igraph_copy(original.getStruct(), theCopy);
		
		IGraphGraph theCopyRes = new IGraphGraph(this, rawLib, theCopy, original.directed);
		theCopyRes._setMapping(new HashMap<String, Integer>(original._getMapping()));
		
		return theCopyRes;
		
	}
	
	public IGraphGraph generateEmpty(int size, boolean directed) {
	
		final IGraphRawLibrary.InternalGraphStruct g = createEmptyGraph();
				
		GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		final int res = rawLib.igraph_empty(
				g, 
				size, 
				directed
				);
		final long duration = System.currentTimeMillis() - startTime;
		GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

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
		
		DoubleByReference res = new DoubleByReference();
		
		GLLogger.debugTech("calling igraph to compute average path length...", getClass());
		GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_average_path_length(g.getPointer(), res, g.directed, false);

		final long duration = System.currentTimeMillis() - startTime;
		GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		checkIGraphResult(res2);
		
		final double length = res.getValue();
		
		return length;
		
	}
	
	public double computeAveragePathLength(IGraphRawLibrary.InternalGraphStruct g) {
		
		DoubleByReference res = new DoubleByReference();
		
		GLLogger.debugTech("calling igraph to compute average path length...", getClass());
		GLLogger.debugTech("calling igraph", getClass());
		final long startTime = System.currentTimeMillis();
		
		final int res2 = rawLib.igraph_average_path_length(g.getPointer(), res, g.directed, false);

		final long duration = System.currentTimeMillis() - startTime;
		GLLogger.debugTech("back from igraph after "+duration+" ms", getClass());

		checkIGraphResult(res2);
		
		final double length = res.getValue();
		
		return length;
		
	}
	
	

}
