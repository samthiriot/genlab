package genlab.igraph.natjna;

import genlab.core.commons.ProgramException;
import genlab.igraph.natjna.InternalVertexSelector.DataUnion.AdjStructure;
import genlab.igraph.natjna.InternalVertexSelector.DataUnion.SeqStructure;

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
/**
 * Nota: not used now
 * @author Samuel Thiriot
 *
 */
public class InternalVertexSelector extends Structure {

    public static class ByValue extends InternalVertexSelector implements Structure.ByValue { }

	public static interface InternalVertexSelectorUnionType {
	
		public final static int IGRAPH_VS_ALL = 0;
		public final static int IGRAPH_VS_ADJ = 1;
		public final static int IGRAPH_VS_NONE = 2;
		public final static int IGRAPH_VS_1 = 3;
		public final static int IGRAPH_VS_VECTORPTR = 4;
		public final static int IGRAPH_VS_VECTOR = 5;
		public final static int IGRAPH_VS_SEQ = 6;
		public final static int IGRAPH_VS_NONADJ = 7;
		
	}
	

    public static class DataUnion extends Union {
    	
        public static class ByValue extends DataUnion implements Union.ByValue {}
        
        public static class AdjStructure extends Structure {
        	
        	public static final int IGRAPH_OUT = 1;
        	public static final int IGRAPH_IN = 2;
        	public static final int IGRAPH_ALL = 3;
        	public static final int IGRAPH_TOTAL = 3;
        	
            public static class ByValue extends AdjStructure implements Structure.ByValue { }

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
        	
            public static class ByValue extends SeqStructure implements Structure.ByValue { }

            public int from;
            public int to;
            
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "from", "to"});	
			}

        }

        
        public int vid;
        public InternalVectorStruct.ByReference vecptr;
        public AdjStructure adj;
        public SeqStructure seq;
        /*
    	public DataUnion() {
    	
    		//vid = 0;
    		//vecptr = new InternalVectorStruct();
    		
    				
    		ensureAllocated();

    	}
    	
    	public DataUnion(Pointer p) {
        	
    		super(p);
    		
    	}
    	*/
    	@Override
    	public void read() {
    		
    		System.err.println("DataUnion.read() 1");
    		super.read();        
    		System.err.println("DataUnion.read() 2");

    	}
        
    }

    
	public int type;
	public DataUnion data;
	
	public InternalVertexSelector() {

		super();
		
		System.err.println("InternalVectorSelector() 2");

		/*
		data = new DataUnion();
		
		ensureAllocated();
//	    allocateMemory();
		
		type = IGRAPH_VS_ALL;

	}
	
	public InternalVertexSelector(Pointer p) {
		
		super(p);
		
		System.err.println("InternalVectorSelector(p) 2");

		
		// TODO ? read();
	}
	/*
	@Override
	public void read() {
		
		System.err.println("InternalVectorSelector.read() 1");
		super.readField("type");
		//super.read();  
		System.err.println("InternalVectorSelector.read() 2");
	
	    switch (type) {
    		
		case InternalVertexSelectorUnionType.IGRAPH_VS_ALL:
		case InternalVertexSelectorUnionType.IGRAPH_VS_NONE:
    		// read nothing
			// data.read();
			break;
		
		case InternalVertexSelectorUnionType.IGRAPH_VS_NONADJ:
    	case InternalVertexSelectorUnionType.IGRAPH_VS_ADJ:
    		data.setType(AdjStructure.class);
    	    data.read();
			break;
		
		case InternalVertexSelectorUnionType.IGRAPH_VS_1:
			data.setType(int.class);
		    data.read();
			break;
			
		case InternalVertexSelectorUnionType.IGRAPH_VS_VECTORPTR:
		case InternalVertexSelectorUnionType.IGRAPH_VS_VECTOR:
			data.setType(InternalVectorStruct.class);
		    data.read();
			break;
			
		case InternalVertexSelectorUnionType.IGRAPH_VS_SEQ:
			data.setType(SeqStructure.class);
		    data.read();
			break;
			
		default:
			throw new ProgramException("unknown union type: "+type);
			
		}
	    
	    System.err.println("set type "+typeStr);
	    
	    //if (typeStr != null) {
	    	data.setType(typeStr);
			data.read();
	    //}
	}
	*/
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "type", "data"});	
	}
	

}
