package genlab.igraph.natjna;

import genlab.core.commons.NotImplementedException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
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
http://igraph.sourcearchive.com/documentation/0.5.4/structigraph__t.html
*/

public class InternalGraphStruct extends Structure implements Iterable<IGraphEdge> {
	
	public static class ByReference extends InternalGraphStruct implements Structure.ByReference {

		public ByReference(IGraphRawLibrary rawlib) {
			super(rawlib);
		}
	}
	
	public int n;
	public boolean directed;
	public Igraph_vector_t from;
	public Igraph_vector_t to;
	public Igraph_vector_t oi;
	public Igraph_vector_t ii;
	public Igraph_vector_t os;
	public Igraph_vector_t is;
	public Pointer attr;
	
	public InternalGraphStruct(Pointer p) {
		super(p);
		read();
	}
	
	public InternalGraphStruct(IGraphRawLibrary rawlib) {
		
		n = 0;
		directed = false;
		from = new Igraph_vector_t();
		to = new Igraph_vector_t();
		oi = new Igraph_vector_t();
		ii = new Igraph_vector_t();
		os = new Igraph_vector_t();
		is = new Igraph_vector_t();
		
		/*
		rawlib.igraph_vector_init(from, 0);
		rawlib.igraph_vector_init(to, 0);
		rawlib.igraph_vector_init(oi, 0);
		rawlib.igraph_vector_init(ii, 0);
		rawlib.igraph_vector_init(os, 0);
		rawlib.igraph_vector_init(is, 0);
		
		attr = new Pointer(Pointer.SIZE);
		*/
		ensureAllocated();

		n = 0;
		directed = false;
		
	}
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "n", "directed", "from", "to", "oi", "ii", "os", "is", "attr" });
	}
	 
	
	private final class EdgesIterator implements Iterator<IGraphEdge> {

		private int i = 0;
		
		private int edgesCount;
		
		final int INT_SIZE = Native.getNativeSize(Integer.class);
		final int LONG_SIZE = Native.getNativeSize(NativeLong.class);


		public EdgesIterator() {
			
			long diffSize = Pointer.nativeValue(oi.end)-Pointer.nativeValue(oi.stor_begin);
			// maxi should be the number of edges in the graph.
			edgesCount = (int)(diffSize/LONG_SIZE);
			
		}
		
		@Override
		public boolean hasNext() {
			return i<edgesCount;
		}

		@Override
		public IGraphEdge next() {
			
			final long edgeId = oi.stor_begin.getLong(LONG_SIZE*i);
			
			return new IGraphEdge(
					i++,
					(int)from.stor_begin.getLong(edgeId), 
					(int)to.stor_begin.getLong(edgeId)
					);
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}
		
	}
	
	public Iterator<IGraphEdge> iterator() {
		return new EdgesIterator();
	}

}
