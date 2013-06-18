package genlab.igraph.natjna;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Pointer;

/**
 * Opaque type that actually reflect internal data from igraph.
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphGraph {

	public final IGraphLibrary lib;

	public final IGraphRawLibrary baseLib;
	
	public final IGraphRawLibrary.InternalGraphStruct graphStruct;
	
	public boolean directed;
	
	/**
	 * maps a genlab node to a graph integer; 
	 * used for translation between genlab and igraph
	 * TODO change to a list with a binary search ?
	 */
	private Map<String,Integer> genlabNodeId2igraphId = null;
	
	/**
	 * Stores caches properties to avoid useless computations
	 */
	private Map<String,Object> cachedProperties = new HashMap<String, Object>();
	
	public IGraphGraph(IGraphLibrary lib, IGraphRawLibrary baseLib, IGraphRawLibrary.InternalGraphStruct graphStruct, boolean directed) {
		this.lib = lib;
		this.baseLib = baseLib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(500);
	}
	
	/**
	 * To call for copy
	 * @param original
	 * @param lib
	 * @param baseLib
	 * @param graphStruct
	 */
	public IGraphGraph(IGraphLibrary lib, IGraphRawLibrary baseLib, IGraphRawLibrary.InternalGraphStruct graphStruct, boolean directed, int initSize) {
		this.lib = lib;
		this.baseLib = baseLib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(initSize);
		
	}
	
	public IGraphGraph(IGraphGraph original, IGraphLibrary lib, IGraphRawLibrary baseLib, IGraphRawLibrary.InternalGraphStruct graphStruct) {
		this.lib = lib;
		this.baseLib = baseLib;
		this.graphStruct = graphStruct;
		
		// copy properties
		this.directed = original.directed;
		genlabNodeId2igraphId.putAll(original.genlabNodeId2igraphId);
		cachedProperties = new HashMap<String, Object>(original.genlabNodeId2igraphId);
	}
	
	public final Integer getIGraphNodeIdForGenlabId(String genlabId) {
		 Integer res = genlabNodeId2igraphId.get(genlabId);
		 if (res == null) {
			 res = new Integer(genlabNodeId2igraphId.size());
			 genlabNodeId2igraphId.put(genlabId, res);
		 }
		 return res;
	}
	
	final public IGraphRawLibrary.InternalGraphStruct getStruct() {
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
	
	

}
