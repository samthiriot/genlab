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
	
	private Map<String,Integer> genlabNodeId2igraphId = null;
	
	public IGraphGraph(IGraphLibrary lib, IGraphRawLibrary baseLib, IGraphRawLibrary.InternalGraphStruct graphStruct, boolean directed) {
		this.lib = lib;
		this.baseLib = baseLib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(500);
	}
	
	public IGraphGraph(IGraphLibrary lib, IGraphRawLibrary baseLib, IGraphRawLibrary.InternalGraphStruct graphStruct, boolean directed, int initialCount) {
		this.lib = lib;
		this.baseLib = baseLib;
		this.graphStruct = graphStruct;
		this.directed = directed;
		
		genlabNodeId2igraphId = new HashMap<String, Integer>(initialCount);
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
	}
	
	public Map<String,Integer> _getMapping() {
		return this.genlabNodeId2igraphId;
	}

}
