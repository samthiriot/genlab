package genlab.igraph.natjna;

import genlab.core.commons.ProgramException;

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


#define IGRAPH_VS_ALL       0
#define IGRAPH_VS_ADJ       1
#define IGRAPH_VS_NONE      2
#define IGRAPH_VS_1         3
#define IGRAPH_VS_VECTORPTR 4
#define IGRAPH_VS_VECTOR    5
#define IGRAPH_VS_SEQ       6
#define IGRAPH_VS_NONADJ    7
 * 
 * 
 */
public class InternalVertexSelector extends Structure {


	public final static int IGRAPH_VS_ALL = 0;
	public final static int IGRAPH_VS_ADJ = 1;
	public final static int IGRAPH_VS_NONE = 2;
	public final static int IGRAPH_VS_1 = 3;
	public final static int IGRAPH_VS_VECTORPTR = 4;
	public final static int IGRAPH_VS_VECTOR = 5;
	public final static int IGRAPH_VS_SEQ = 6;
	public final static int IGRAPH_VS_NONADJ = 7;
	
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
    	
    		ensureAllocated();

    	}
    	
    	public DataUnion(Pointer p) {
        	
    		super(p);
    		
    	}
    	
    	@Override
    	public void read() {
    		
    		super.read();        


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
	public void read() {
		
		super.read();        
	    
	    switch (type) {
    		
		case IGRAPH_VS_ALL:
		case IGRAPH_VS_NONE:
    		// read nothing
			data.setType((String)null);
			break;
		
		case IGRAPH_VS_NONADJ:
    	case IGRAPH_VS_ADJ:
    		data.setType("adj");
			break;
		
		case IGRAPH_VS_1:
			// read vid
			data.setType("vid");
			break;
			
		case IGRAPH_VS_VECTORPTR:
		case IGRAPH_VS_VECTOR:
			data.setType("vecptr");
			break;
			
		case IGRAPH_VS_SEQ:
			data.setType("seq");
			break;
			
		default:
			throw new ProgramException("unknown union type: "+type);
			
		}
	    
		data.read();
	}
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "type", "data"});	
	}
	

}
