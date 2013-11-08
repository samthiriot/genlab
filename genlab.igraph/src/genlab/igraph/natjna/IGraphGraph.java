package genlab.igraph.natjna;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.jna.Pointer;

/**
 * Opaque type that actually reflect internal data from igraph.
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphGraph implements Iterable<IGraphEdge> {

	public final IGraphLibrary lib;
	
	public final InternalGraphStruct graphStruct;
	
	public boolean directed;
	
	/**
	 * the "multiplex" value means: null==unknown, true=yes, false=no
	 */
	public Boolean multiplex = null;
	
	/**
	 * maps a genlab node to a graph integer; 
	 * used for translation between genlab and igraph
	 * TODO change to a list with a binary search ?
	 */
	private Map<String,Integer> genlabNodeId2igraphId = null;
	private Map<Integer,String> igraphNodeId2genlabNode = null;

	
	/**
	 * Stores caches properties to avoid useless computations
	 */
	private Map<String,Object> cachedProperties = new HashMap<String, Object>();
	
	public double[] xPositions = null;
	public double[] yPositions = null;
	
	
	public IGraphGraph(IGraphLibrary lib, InternalGraphStruct graphStruct, boolean directed) {
		this.lib = lib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(500);
		igraphNodeId2genlabNode = new HashMap<Integer, String>(500);
	}
	
	/**
	 * To call for copy
	 * @param original
	 * @param lib
	 * @param baseLib
	 * @param graphStruct
	 */
	public IGraphGraph(IGraphLibrary lib, InternalGraphStruct graphStruct, boolean directed, int initSize) {
		this.lib = lib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(initSize);
		igraphNodeId2genlabNode = new HashMap<Integer, String>(initSize);

		
	}
	
	public IGraphGraph(IGraphGraph original, IGraphLibrary lib, InternalGraphStruct graphStruct) {
		this.lib = lib;
		this.graphStruct = graphStruct;
		
		// copy properties
		this.directed = original.directed;
		genlabNodeId2igraphId.putAll(original.genlabNodeId2igraphId);
		igraphNodeId2genlabNode.putAll(original.igraphNodeId2genlabNode);
		cachedProperties = new HashMap<String, Object>(original.genlabNodeId2igraphId);
	}
	
	public final Integer getOrCreateIGraphNodeIdForGenlabId(String genlabId) {
		 Integer res = genlabNodeId2igraphId.get(genlabId);
		 if (res == null) {
			 res = new Integer(genlabNodeId2igraphId.size());
			 genlabNodeId2igraphId.put(genlabId, res);
			 igraphNodeId2genlabNode.put(res, genlabId);
		 }
		 return res;
	}
	
	public final Integer getIGraphNodeIdForGenlabId(String genlabId) {
		 return genlabNodeId2igraphId.get(genlabId);
	}
	
	public final String getGenlabIdForIGraphNode(Integer igraphNodeId) {
		 return igraphNodeId2genlabNode.get(igraphNodeId);
	}
	
	final public InternalGraphStruct getStruct() {
		return graphStruct;
	}
	
	final public Pointer getPointer() {
		return graphStruct.getPointer();
	}
	
	public void _setMapping(Map<String,Integer> genlabNodeId2igraphId) {
		this.genlabNodeId2igraphId = genlabNodeId2igraphId;
		clearCachedProperties();
	}
	
	public Map<String,Integer> _getMapping() {
		return this.genlabNodeId2igraphId;
	}
	
	protected void clearCachedProperties() {
		cachedProperties.clear();
	}
	
	public void setCachedProperty(String key, Object value) {
		cachedProperties.put(key, value);
	}
	
	public Object getCachedProperty(String key) {
		return cachedProperties.get(key);
	}
	
	public boolean hasCachedProperty(String key) {
		return cachedProperties.get(key) != null;
	}
	
	/**
	 * To be called by users to warn that the underlying igraph 
	 * graph was changed. It will clear cached informations for this graph.
	 */
	public void graphChanged() {
		clearCachedProperties();
	}

	public Iterator<IGraphEdge> iterator() {
		
		// uses a direct memory access
		return graphStruct.iterator();
		
		// could be replaced by explicit JNA calls to the igraph library
		// but they are slower; so this remains only a good test :-)
		//	return lib.getEdgeIterator(this);
	}
	
	public Boolean isMultiGraph() {
		return multiplex;
	}
	
	public void setMultiGraph(Boolean isMulti) {
		this.multiplex = isMulti;
	}

}
