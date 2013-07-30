package genlab.igraph.natjna;

import genlab.core.commons.NotImplementedException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/*
00086 typedef struct igraph_s {
  igraph_integer_t n;
  igraph_bool_t directed;
  igraph_vector_t from;
  igraph_vector_t to;
  igraph_vector_t oi;
  igraph_vector_t ii;
  igraph_vector_t os;
  igraph_vector_t is;
  void *attr;
} igraph_t;

*/

/**
 * Reflects the internal igraph structure for graphs storage
 * @see http://igraph.sourcearchive.com/documentation/0.5.4/structigraph__t.html
 * 
 * @author Samuel Thiriot
 *
 */
public class InternalGraphStruct extends Structure implements Iterable<IGraphEdge> {
	
	public static class ByReference extends InternalGraphStruct implements Structure.ByReference {

		public ByReference(IGraphRawLibrary rawlib) {
			super(rawlib);
		}
	}
	
	public int n;
	public boolean directed;
	public InternalVectorStruct from;
	public InternalVectorStruct to;
	public InternalVectorStruct oi;
	public InternalVectorStruct ii;
	public InternalVectorStruct os;
	public InternalVectorStruct is;
	public Pointer attr;
	
	public InternalGraphStruct(Pointer p) {
		super(p);
		read();
	}
	
	public InternalGraphStruct(IGraphRawLibrary rawLib, boolean directed, int nodesPlanned, int edgesPlanned) {

		n = 0;
		this.directed = directed;
		from = new InternalVectorStruct();
		to = new InternalVectorStruct();
		oi = new InternalVectorStruct();
		ii = new InternalVectorStruct();
		os = new InternalVectorStruct();
		is = new InternalVectorStruct();
		
		n = 0;
		
		ensureAllocated();

		rawLib.igraph_vector_init(from, edgesPlanned);
		rawLib.igraph_vector_init(to, edgesPlanned);
		rawLib.igraph_vector_init(oi, edgesPlanned);
		rawLib.igraph_vector_init(ii, edgesPlanned);
		
		
	}
	
	public InternalGraphStruct(IGraphRawLibrary rawlib) {
		
		n = 0;
		directed = false;
		from = new InternalVectorStruct();
		to = new InternalVectorStruct();
		oi = new InternalVectorStruct();
		ii = new InternalVectorStruct();
		os = new InternalVectorStruct();
		is = new InternalVectorStruct();
		
		ensureAllocated();

		n = 0;
		directed = false;
		
	}
	

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "n", "directed", "from", "to", "oi", "ii", "os", "is", "attr" });
	}
	 

	/**
	 * A very efficient iterator with direct memory access to the igraph structure
	 * in the share memory
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	private final class EdgesIterator implements Iterator<IGraphEdge> {

		private int i = 0;
		
		private int edgesCount;
		
		final int DOUBLE_SIZE = Native.getNativeSize(Double.class);


		public EdgesIterator() {
			
			long diffSize = Pointer.nativeValue(oi.end)-Pointer.nativeValue(oi.stor_begin);
			// maxi should be the number of edges in the graph.
			edgesCount = (int)(diffSize/DOUBLE_SIZE);
			
		}
		
		@Override
		public boolean hasNext() {
			return i<edgesCount;
		}

		@Override
		public IGraphEdge next() {
			
			final int edgeId = (int)oi.stor_begin.getDouble(DOUBLE_SIZE*i);
			i++;
			return new IGraphEdge(
					edgeId,
					(int)from.stor_begin.getDouble(edgeId*DOUBLE_SIZE), 
					(int)to.stor_begin.getDouble(edgeId*DOUBLE_SIZE)
					);
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}
		
	}
	
	/**
	 * Returns a very efficient iterator based on a direct memory access to the graph.
	 * @return
	 */
	public Iterator<IGraphEdge> iterator() {
		return new EdgesIterator();
	}

}
