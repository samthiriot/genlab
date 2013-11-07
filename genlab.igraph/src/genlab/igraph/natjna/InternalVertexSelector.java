package genlab.igraph.natjna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

/*
typedef struct igraph_vs_t {
  int type;
  union {
    igraph_integer_t vid;    	        /* single vertex  *
    const igraph_vector_t *vecptr;      /* vector of vertices  *
    struct {
      igraph_integer_t vid;
      igraph_neimode_t mode;
    } adj;			        /* adjacent vertices  *
    struct {                           
      igraph_integer_t from;
      igraph_integer_t to;
    } seq;                              /* sequence of vertices from:to *
  } data;
} igraph_vs_t;
 * 
 * 
 */
public class InternalVertexSelector extends Structure {

    public static class ByReference extends InternalVertexSelector implements Structure.ByReference { }

    public static class DataUnion extends Union {
    	
        public static class ByReference extends DataUnion implements Structure.ByReference {}
        
        public static class AdjStructure extends Structure {
        	
            public static class ByReference extends AdjStructure implements Structure.ByReference { }

            public int vid;
            
            /*
             * 
typedef enum { IGRAPH_OUT=1, IGRAPH_IN=2, IGRAPH_ALL=3,
	       IGRAPH_TOTAL=3 } igraph_neimode_t;
             */
            public int mode;
            
			@Override
			protected List getFieldOrder() {
        		return Arrays.asList(new String[] { "vid", "mode"});
			}

        }
        
        public static class SeqStructure extends Structure {
        	
            public static class ByReference extends AdjStructure implements Structure.ByReference { }

            public int from;
            public int to;
            
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "from", "to"});	
			}

        }

        
        public int vid;
        public InternalVectorStruct vecptr;
        
        public AdjStructure adj;
        public SeqStructure seq;
        
    	public DataUnion() {
    	
    		vecptr = new InternalVectorStruct();
    		adj = new AdjStructure();
    		seq = new SeqStructure();
    		
    		ensureAllocated();

    	}
        
    }

    
	public int type;
	public DataUnion data;
	
	
	public InternalVertexSelector() {
		
		data = new DataUnion();
		
		ensureAllocated();
//	    allocateMemory();

	}
	
	public InternalVertexSelector(Pointer p) {
		super(p);
		read();
	}

	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "type", "data"});	
	}
	

}
