package genlab.igraph.natjna;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Opaque type that actually reflect internal data from igraph.
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphGraph {

	public final PointerByReference igraphPointer;
	
	public boolean directed;
	
	public IGraphGraph(PointerByReference igraphPointer, boolean directed) {
		this.igraphPointer = igraphPointer;
		this.directed = directed;
	}
	
	final public PointerByReference getReference() {
		return igraphPointer;
	}
	
	final public Pointer getPointer() {
		return igraphPointer.getPointer();
	}

}
