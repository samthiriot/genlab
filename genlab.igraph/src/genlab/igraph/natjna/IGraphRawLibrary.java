package genlab.igraph.natjna;

import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/*
 * igraph_types.h
 * 
typedef int    igraph_integer_t;
typedef double igraph_real_t;
typedef int    igraph_bool_t;
 */
public class IGraphRawLibrary {


	//IGraphLibrary INSTANCE = (IGraphLibrary) Native.loadLibrary("igraph", IGraphLibrary.class);

	private static final String[] librariesPathes = new String[] {
		"./ext/native/linux/x86_64/",
		"./genlab.igraph/ext/native/linux/x86_64/",
		"../genlab.igraph/ext/native/linux/x86_64/"
	};
	
	/**
	 * Defines libraries in order to enable JNA to find OUR libraries, 
	 * with an higher priority than system ones.
	 */
	private static final void defineLibraryPath() {
		try{
    		final String previousValue = System.getProperty("jna.library.path");
    		StringBuffer sb = new StringBuffer();
    		// add static libs
    		for (String lib: librariesPathes) {
    			sb.append(lib).append(File.pathSeparator);
    		}
    		// add previous value
    		if (previousValue != null)
    			sb.append(previousValue);

    		// actually set the value
    		System.setProperty("jna.library.path", sb.toString());
    		
    		GLLogger.debugTech(
    				"initialized system property jna.library.path="+sb.toString(), 
    				IGraphRawLibrary.class
    				);
    		
    	} catch(IllegalStateException ise){
    		GLLogger.errorTech(
    				"error during the initialization of the system property jna.library.path", 
    				IGraphRawLibrary.class
    				);
    	}
	}
	
	static {

    	defineLibraryPath();
	}
	
	public static boolean isAvailable = false;
	public static Throwable problem = null;


	/**
	 * Reflects the igraph internal igraph_vector_t structure.
	 * Warning, it should first by initialized using the igraph init vector method.
	 * @author B12772
	 *
	 */
	public static class Igraph_vector_t extends Structure {
		
		public Pointer stor_begin;
		public Pointer stor_end;
		public Pointer end;

		public static class ByReference extends Igraph_vector_t implements Structure.ByReference {}

		public Igraph_vector_t() {
			stor_begin = new Pointer(Pointer.SIZE);
			stor_end = new Pointer(Pointer.SIZE);
			end = new Pointer(Pointer.SIZE);
			ensureAllocated();
//		    allocateMemory();

		}
		
		public Igraph_vector_t(Pointer p) {
			super(p);
			read();
		}
		
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "stor_begin", "stor_end", "end" });
		}
		
		/*
		BASE FUNCTION(igraph_vector,e)         (const TYPE(igraph_vector)* v, long int pos) {
			assert(v != NULL);
			assert(v->stor_begin != NULL);
			return * (v->stor_begin + pos);
		}
		*/
		/**
		 * Returns the element with this index, supposing it is a integer.
		 * Warning: if the vector was changed (memory copy)
		 * and not update, something will become really wrong here.
		 * @param idx
		 * @return
		 */
		public double getInt(int idx) {
			//Pointer p = stor_begin.getPointer(idx);
			//return p.getInt(0);
			return stor_begin.getInt(idx);
		}
		
		public int[] asIntArray(int size) {
			return stor_begin.getIntArray(0, size);
		}
		public double[] asDoubleArray(int size) {
			return stor_begin.getDoubleArray(0, size);
		}
		
	}


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
	public static class InternalGraphStruct extends Structure {
		
		public static class ByReference extends InternalGraphStruct implements Structure.ByReference {

			public ByReference(IGraphRawLibrary rawlib) {
				super(rawlib);
			}}
		
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
		 
	
	}
	
	/*
	 * int igraph_vector_init      (igraph_vector_t* v, int long size);
     */
	public native int igraph_vector_init (Igraph_vector_t v, int size);

	/*
	 * 
     */
	public native int igraph_vector_size (Igraph_vector_t v);

	/*
	 * igraph_version.h
	 */
	/*
	 * 
int igraph_version(const char **version_string,
		   int *major,
		   int *minor,
		   int *subminor);
	 */
	public native int igraph_version(				
				PointerByReference version_string,
				IntByReference major,
				IntByReference minor,
				IntByReference subminor
			  	);

	/*
	int igraph_watts_strogatz_game(igraph_t *graph, igraph_integer_t dim,
		       igraph_integer_t size, igraph_integer_t nei,
		       igraph_real_t p, igraph_bool_t loops, 
		       igraph_bool_t multiple);
	*/
	/**
	 * @see http://igraph.sourceforge.net/doc/html/ch09s02.html#igraph_watts_strogatz_game
	 */
	public native int igraph_watts_strogatz_game (
			InternalGraphStruct graph,
			int dim,
			int size,
			int nei,
			double p,
			boolean loops,
			boolean multiple
			);
	
		/**
	 * @see http://igraph.sourceforge.net/doc/html/ch09s02.html#igraph_grg_game
	 */
	/*
	int igraph_grg_game(
			igraph_t *graph, 
			igraph_integer_t nodes,
		    igraph_real_t radius, 
		    igraph_bool_t torus,
		    igraph_vector_t *x, 
		    igraph_vector_t *y
		    );
	public void igraph_grg_game(
			MyGraph graph, // TODO
			int nodes,
			float radius,
			boolean torus,
			
			);
*/
	/*
	 * igraph_game.h
	 */
/*
int igraph_barabasi_game(igraph_t *graph, igraph_integer_t n,
			 igraph_real_t power, 
			 igraph_integer_t m,
			 const igraph_vector_t *outseq,
			 igraph_bool_t outpref,
			 igraph_real_t A,
			 igraph_bool_t directed,
			 igraph_barabasi_algorithm_t algo,
			 const igraph_t *start_from);
int igraph_nonlinear_barabasi_game(igraph_t *graph, igraph_integer_t n,
				   igraph_real_t power,
				   igraph_integer_t m,  
				   const igraph_vector_t *outseq,
				   igraph_bool_t outpref,
				   igraph_real_t zeroappeal,
				   igraph_bool_t directed);
int igraph_erdos_renyi_game(igraph_t *graph, igraph_erdos_renyi_t type,
			    igraph_integer_t n, igraph_real_t p,
			    igraph_bool_t directed, igraph_bool_t loops);
int igraph_erdos_renyi_game_gnp(igraph_t *graph, igraph_integer_t n, igraph_real_t p,
				igraph_bool_t directed, igraph_bool_t loops);
int igraph_erdos_renyi_game_gnm(igraph_t *graph, igraph_integer_t n, igraph_real_t m,
				igraph_bool_t directed, igraph_bool_t loops);
int igraph_degree_sequence_game(igraph_t *graph, const igraph_vector_t *out_deg,
				const igraph_vector_t *in_deg, 
				igraph_degseq_t method);
int igraph_growing_random_game(igraph_t *graph, igraph_integer_t n, 
			       igraph_integer_t m, igraph_bool_t directed, igraph_bool_t citation);
int igraph_barabasi_aging_game(igraph_t *graph, 
			       igraph_integer_t nodes,
			       igraph_integer_t m,
			       const igraph_vector_t *outseq,
			       igraph_bool_t outpref,
			       igraph_real_t pa_exp,
			       igraph_real_t aging_exp,
			       igraph_integer_t aging_bin,
			       igraph_real_t zero_deg_appeal,
			       igraph_real_t zero_age_appeal,
			       igraph_real_t deg_coef,
			       igraph_real_t age_coef,
			       igraph_bool_t directed);
int igraph_recent_degree_game(igraph_t *graph, igraph_integer_t n,
			      igraph_real_t power,
			      igraph_integer_t window,
			      igraph_integer_t m,  
			      const igraph_vector_t *outseq,
			      igraph_bool_t outpref,
			      igraph_real_t zero_appeal,
			      igraph_bool_t directed);
int igraph_recent_degree_aging_game(igraph_t *graph,
				    igraph_integer_t nodes,
				    igraph_integer_t m, 
				    const igraph_vector_t *outseq,
				    igraph_bool_t outpref,
				    igraph_real_t pa_exp,
				    igraph_real_t aging_exp,
				    igraph_integer_t aging_bin,
				    igraph_integer_t window,
				    igraph_real_t zero_appeal,
				    igraph_bool_t directed);
int igraph_callaway_traits_game (igraph_t *graph, igraph_integer_t nodes, 
				 igraph_integer_t types, igraph_integer_t edges_per_step, 
				 igraph_vector_t *type_dist,
				 igraph_matrix_t *pref_matrix,
				 igraph_bool_t directed);
int igraph_establishment_game(igraph_t *graph, igraph_integer_t nodes,
			      igraph_integer_t types, igraph_integer_t k,
			      igraph_vector_t *type_dist,
			      igraph_matrix_t *pref_matrix,
			      igraph_bool_t directed);
int igraph_grg_game(igraph_t *graph, igraph_integer_t nodes,
		    igraph_real_t radius, igraph_bool_t torus,
		    igraph_vector_t *x, igraph_vector_t *y);
int igraph_preference_game(igraph_t *graph, igraph_integer_t nodes,
			   igraph_integer_t types, 
			   const igraph_vector_t *type_dist,
			   igraph_bool_t fixed_sizes,
			   const igraph_matrix_t *pref_matrix,
			   igraph_vector_t *node_type_vec,
			   igraph_bool_t directed, igraph_bool_t loops);
int igraph_asymmetric_preference_game(igraph_t *graph, igraph_integer_t nodes,
				      igraph_integer_t types,
				      igraph_matrix_t *type_dist_matrix,
				      igraph_matrix_t *pref_matrix,
				      igraph_vector_t *node_type_in_vec,
				      igraph_vector_t *node_type_out_vec,
				      igraph_bool_t loops);

int igraph_rewire_edges(igraph_t *graph, igraph_real_t prob, 
			igraph_bool_t loops, igraph_bool_t multiple);
int igraph_watts_strogatz_game(igraph_t *graph, igraph_integer_t dim,
			       igraph_integer_t size, igraph_integer_t nei,
			       igraph_real_t p, igraph_bool_t loops, 
			       igraph_bool_t multiple);

int igraph_lastcit_game(igraph_t *graph, 
			igraph_integer_t nodes, igraph_integer_t edges_per_node, 
			igraph_integer_t agebins,
			const igraph_vector_t *preference, igraph_bool_t directed);

int igraph_cited_type_game(igraph_t *graph, igraph_integer_t nodes,
			   const igraph_vector_t *types,
			   const igraph_vector_t *pref,
			   igraph_integer_t edges_per_step,
			   igraph_bool_t directed);

int igraph_citing_cited_type_game(igraph_t *graph, igraph_integer_t nodes,
				  const igraph_vector_t *types,
				  const igraph_matrix_t *pref,
				  igraph_integer_t edges_per_step,
				  igraph_bool_t directed);

int igraph_forest_fire_game(igraph_t *graph, igraph_integer_t nodes,
			    igraph_real_t fw_prob, igraph_real_t bw_factor,
			    igraph_integer_t ambs, igraph_bool_t directed);


int igraph_simple_interconnected_islands_game(
				igraph_t *graph, 
				igraph_integer_t islands_n, 
				igraph_integer_t islands_size,
				igraph_real_t islands_pin, 
				igraph_integer_t n_inter);

int igraph_static_fitness_game(igraph_t *graph, igraph_integer_t no_of_edges,
                igraph_vector_t* fitness_out, igraph_vector_t* fitness_in,
                igraph_bool_t loops, igraph_bool_t multiple);

int igraph_static_power_law_game(igraph_t *graph,
    igraph_integer_t no_of_nodes, igraph_integer_t no_of_edges,
    igraph_real_t exponent_out, igraph_real_t exponent_in,
    igraph_bool_t loops, igraph_bool_t multiple,
    igraph_bool_t finite_size_correction);

int igraph_k_regular_game(igraph_t *graph,
    igraph_integer_t no_of_nodes, igraph_integer_t k,
    igraph_bool_t directed, igraph_bool_t multiple);
*/
	
	/*
	 * igraph_interface.c
	 */
	
	/*
	 * int igraph_empty(igraph_t *graph, igraph_integer_t n, igraph_bool_t directed);
	 */
	/**
	 * Creates an empty graph
	 * @param graph
	 * @param n
	 * @param directed
	 * @return
	 */
	public native int igraph_empty(InternalGraphStruct graph, int n, boolean directed);

	/*
	int igraph_erdos_renyi_game_gnp(igraph_t *graph, igraph_integer_t n, igraph_real_t p,
			igraph_bool_t directed, igraph_bool_t loops);
	*/
	public native int igraph_erdos_renyi_game_gnp(
			InternalGraphStruct graph, 
			int n, 
			double p,
			boolean directed, 
			boolean loops
			);
	
	/*
	 * igraph_integer_t igraph_vcount(const igraph_t *graph);
	 */
	/**
	 * Returns the vertex count
	 * @param graph
	 * @return
	 */
	public native int igraph_vcount(Pointer graph);
	
	 
	/*
	 * igraph_integer_t igraph_ecount(const igraph_t *graph);
	 */
	/**
	 * Returns the edge count
	 * @param graph
	 * @return
	 */
	public native int igraph_ecount(Pointer graph);
		
	/*
	 * igraph_bool_t igraph_is_directed(const igraph_t *graph);
	 */
	public native boolean igraph_is_directed(Pointer graph);

	/*
	 * int igraph_copy(igraph_t *to, const igraph_t *from);
	 */
	public native int igraph_copy(InternalGraphStruct to, InternalGraphStruct from);


	// TODO free memory !
	/*
	 * igraph_destroy
	 */
	
	/*
	 * int igraph_add_edge(igraph_t *graph, igraph_integer_t from, igraph_integer_t to);
	 */
	public native int igraph_add_edge(Pointer graph, int from, int to);
	
	/*
	 * int igraph_average_path_length(const igraph_t *graph, igraph_real_t *res,
			       igraph_bool_t directed, igraph_bool_t unconn);
	 */
	public native int igraph_average_path_length(Pointer graph, DoubleByReference res,
		       boolean directed, boolean unconn);

	/*
	 * int igraph_diameter(const igraph_t *graph, igraph_integer_t *pres, 
		    igraph_integer_t *pfrom, igraph_integer_t *pto, 
		    igraph_vector_t *path,
		    igraph_bool_t directed, igraph_bool_t unconn);
	 */
	public native int igraph_diameter(Pointer graph, IntByReference pres, 
			IntByReference pfrom,  IntByReference pto, 
		    PointerByReference path,
		    boolean directed, boolean unconn);
	
	/*
	 * int igraph_is_connected(const igraph_t *graph, igraph_bool_t *res, 
			igraph_connectedness_t mode);
	 * mode: IGRAPH_WEAK=1, IGRAPH_STRONG=2 (ignored for undirected)
	 */
	public native int igraph_is_connected(Pointer graph, IntByReference res, 
			int mode);
	
	/*
	 * int igraph_clusters(const igraph_t *graph, igraph_vector_t *membership, 
		    igraph_vector_t *csize, igraph_integer_t *no,
		    igraph_connectedness_t mode);
		    mode: IGRAPH_WEAK=1, IGRAPH_STRONG=2 (ignored for undirected)
	 */
	public native int igraph_clusters(Pointer graph, Igraph_vector_t membership, 
			Igraph_vector_t csize, IntByReference no,
		    int mode);
	
    static {
    	
    	// debug: to check we can really load this library
    	// System.load("TODO absolute prefix/genlab/genlab.igraph/ext/native/linux/x86_64/libigraph.so");

		try {
			GLLogger.debugTech(
					"attempting to register the native igraph library with parameters: " +
					"jna.library.path="+System.getProperty("jna.library.path"), 
					IGraphRawLibrary.class
					);
			Native.register("igraph");
			isAvailable = true;
			GLLogger.debugTech("registered native igraph", IGraphRawLibrary.class);
		} catch (UnsatisfiedLinkError e) {
			isAvailable = false;
			problem = e;
			GLLogger.errorTech("unable to register the native igraph library", IGraphRawLibrary.class, e);
		}
    }

}
