package genlab.igraph.natjna;

import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
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
/**
 * 
 * For debug: VM option jna.debug_load=true .
 * Allows multi thread access
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraphRawLibrary {

	public static Boolean isAvailable = null;
	
	static {
		
		try {
			// in this block, register which native library is supposed to be directly mapped to this method
			Native.setProtected(false);
	
	
			
			String filenameOriginal = "/ext/native/linux/x86_64/libigraph.so";
			String libNameOriginal = "igraph";
					
			URL url = genlab.igraph.Activator.getDefault().getBundle().getEntry(filenameOriginal);
	        URL resolvedURL = null;
			try {
				resolvedURL = FileLocator.toFileURL(url);
			} catch (IOException e) {
				throw new ProgramException("unable to locale the root of the plugin; icons will not be added for algorithms", e);
			}
			        
			//File originalDll = Activator.getDefault().getBundle().
			File originalDll = new File(resolvedURL.getFile());
			
			if (!originalDll.exists() || !originalDll.isFile() || !originalDll.canRead())
				throw new ProgramException("unable to read the igraph shared library from the file "+filenameOriginal);
	
			// found; let's copy it somewhere
			File copy;
			final String prefix = FileUtils.extractFilenameWithoutExtension(filenameOriginal);
			final String suffix = "."+FileUtils.getExtension(filenameOriginal);
			try {
				copy = File.createTempFile(
						prefix, 
						suffix
						);
			} catch (IOException e) {
				throw new ProgramException("unable to copy the native library to a temporary space", e);
			}
	
			FileUtils.copyFiles(originalDll, copy);
	
			String tmpLibraryUndecoratedName = copy.getName();
			tmpLibraryUndecoratedName = libNameOriginal+tmpLibraryUndecoratedName.substring(prefix.length(), tmpLibraryUndecoratedName.length()-suffix.length());
			
			
			GLLogger.debugTech("adding as a search path :"+FileUtils.extractPath(copy.getAbsolutePath())+" for library named "+tmpLibraryUndecoratedName, IGraphRawLibrary.class);
			NativeLibrary.addSearchPath(tmpLibraryUndecoratedName, FileUtils.extractPath(copy.getAbsolutePath()));
			try{
	    		final String previousValue = System.getProperty("jna.library.path");
	    		StringBuffer sb = new StringBuffer();
	    		sb.append(tmpLibraryUndecoratedName).append(File.pathSeparator);
	    		// add previous value
	    		if (previousValue != null)
	    			sb.append(previousValue);
	    		// actually set the value
	    		System.setProperty("jna.library.path", sb.toString());
			} catch(IllegalStateException ise){
	    		GLLogger.errorTech(
	    				"error during the initialization of the system property jna.library.path", 
	    				IGraphRawLibrary.class
	    				);
	    	}
			
			Native.register(tmpLibraryUndecoratedName);
			
			isAvailable = true;
		} catch (RuntimeException e) {
			isAvailable = false;
		}
	}
	/*
	 * int igraph_vector_init      (igraph_vector_t* v, int long size);
     */
	public static native int igraph_vector_init (InternalVectorStruct v, int size);

	/*
	 * int igraph_vector_init_copy(igraph_vector_t *v, 
				      igraph_real_t *data, long int length);
	 */
	public static native int igraph_vector_init_copy(
			InternalVectorStruct v, 
		    Pointer data, 
		    int length
		    );
	
	/*
	 * void igraph_vector_destroy   (igraph_vector_t* v);
	 */
	public static native void igraph_vector_destroy(InternalVectorStruct v);

	
	/*
	 * 
     */
	public static native int igraph_vector_size (InternalVectorStruct v);

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
	public static native int igraph_version(				
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
	public static native int igraph_watts_strogatz_game (
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
	
*/
	public static native int igraph_grg_game(
			InternalGraphStruct graph, 
			int nodes,
		    double radius, 
		    boolean torus,
		    InternalVectorStruct x, 
		    InternalVectorStruct y
		    );

	/*
	int igraph_rewire(igraph_t *graph, igraph_integer_t n, igraph_rewiring_t mode);
	IGRAPH_REWIRING_SIMPLE=0 
	 */
	public static native int igraph_rewire(Pointer graph, int n, int mode);

	
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

*/
	
	/*
	int igraph_forest_fire_game(igraph_t *graph, igraph_integer_t nodes,
				    igraph_real_t fw_prob, igraph_real_t bw_factor,
				    igraph_integer_t ambs, igraph_bool_t directed);
	*/
	public static native int igraph_forest_fire_game(InternalGraphStruct graph, int nodes,
		    double fw_prob, double bw_factor,
		    int pambs, boolean directed);

	/*
	int igraph_simple_interconnected_islands_game(
				igraph_t *graph, 
				igraph_integer_t islands_n, 
				igraph_integer_t islands_size,
				igraph_real_t islands_pin, 
				igraph_integer_t n_inter);
	 */
	public static native int igraph_simple_interconnected_islands_game(
			InternalGraphStruct graph, 
			int islands_n, 
			int islands_size,
			double islands_pin, 
			int n_inter
			);

	/*
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
	public static native int igraph_empty(InternalGraphStruct graph, int n, boolean directed);

	/*
	int igraph_erdos_renyi_game_gnp(igraph_t *graph, igraph_integer_t n, igraph_real_t p,
			igraph_bool_t directed, igraph_bool_t loops);
	*/
	public static native int igraph_erdos_renyi_game_gnp(
			InternalGraphStruct graph, 
			int n, 
			double p,
			boolean directed, 
			boolean loops
			);
	public static native int igraph_erdos_renyi_game_gnm(
			InternalGraphStruct graph, 
			int n, 
			double m,
			boolean directed, 
			boolean loops
			);
	

	/*
	 * int igraph_lcf_vector(igraph_t *graph, igraph_integer_t n,
		      const igraph_vector_t *shifts, 
		      igraph_integer_t repeats);
	 */
	public static native int igraph_lcf_vector(
			InternalGraphStruct graph, 
			int n,
		    InternalVectorStruct shifts, 
		    int repeats
		    );
	
	/*
	 * igraph_integer_t igraph_vcount(const igraph_t *graph);
	 */
	/**
	 * Returns the vertex count
	 * @param graph
	 * @return
	 */
	public static native int igraph_vcount(Pointer graph);
	
	 
	/*
	 * igraph_integer_t igraph_ecount(const igraph_t *graph);
	 */
	/**
	 * Returns the edge count
	 * @param graph
	 * @return
	 */
	public static native int igraph_ecount(Pointer graph);
		
	/*
	 * igraph_bool_t igraph_is_directed(const igraph_t *graph);
	 */
	public static native boolean igraph_is_directed(Pointer graph);

	/*
	 * int igraph_copy(igraph_t *to, const igraph_t *from);
	 */
	public static native int igraph_copy(InternalGraphStruct to, InternalGraphStruct from);


	// TODO free memory !
	/*
	 * int igraph_destroy(igraph_t *graph);
	 */
	public static native int igraph_destroy(Pointer graph);

	public static native int igraph_add_vertices(Pointer graph, int nv, Pointer attr);

	/*
	 * int igraph_add_edge(igraph_t *graph, igraph_integer_t from, igraph_integer_t to);
	 */
	public static native int igraph_add_edge(Pointer graph, int from, int to);
	
	public static native int igraph_add_edges(Pointer graph, InternalVectorStruct edges, Pointer attr);

	/*
	 * int igraph_edge(const igraph_t *graph, igraph_integer_t eid, 
		igraph_integer_t *from, igraph_integer_t *to);
	 */
	public static native int igraph_edge(Pointer graph, int eid, 
			IntByReference from, IntByReference to);
	
	/*
	 * int igraph_average_path_length(const igraph_t *graph, igraph_real_t *res,
			       igraph_bool_t directed, igraph_bool_t unconn);
	 */
	public static native int igraph_average_path_length(Pointer graph, DoubleByReference res,
		       boolean directed, boolean unconn);

	/*
	 * int igraph_diameter(const igraph_t *graph, igraph_integer_t *pres, 
		    igraph_integer_t *pfrom, igraph_integer_t *pto, 
		    igraph_vector_t *path,
		    igraph_bool_t directed, igraph_bool_t unconn);
	 */
	public static native int igraph_diameter(Pointer graph, IntByReference pres, 
			IntByReference pfrom,  IntByReference pto, 
		    PointerByReference path,
		    boolean directed, boolean unconn);
	
	/*
	 * int igraph_is_connected(const igraph_t *graph, igraph_bool_t *res, 
			igraph_connectedness_t mode);
	 * mode: IGRAPH_WEAK=1, IGRAPH_STRONG=2 (ignored for undirected)
	 */
	public static native int igraph_is_connected(Pointer graph, IntByReference res, 
			int mode);
	
	/*
	 * int igraph_clusters(const igraph_t *graph, igraph_vector_t *membership, 
		    igraph_vector_t *csize, igraph_integer_t *no,
		    igraph_connectedness_t mode);
		    mode: IGRAPH_WEAK=1, IGRAPH_STRONG=2 (ignored for undirected)
	 */
	public static native int igraph_clusters(Pointer graph, InternalVectorStruct membership, 
			InternalVectorStruct csize, IntByReference no,
		    int mode);
	
	/*
	 * int igraph_transitivity_undirected(const igraph_t *graph,
				   igraph_real_t *res,
				   igraph_transitivity_mode_t mode);
				   
				   mode: Defines how to treat graphs with no connected triples. IGRAPH_TRANSITIVITY_NAN=0 returns NaN in this case, IGRAPH_TRANSITIVITY_ZERO=1 returns zero.
	 */
	public static native int igraph_transitivity_undirected(Pointer graph, DoubleByReference res, int mode);
	
	/*
	 * int igraph_isomorphic(const igraph_t *graph1, const igraph_t *graph2,
		      igraph_bool_t *iso);

	 */
	public static native int igraph_isomorphic(Pointer graph1, Pointer graph2,
		      IntByReference iso);

	/*
	 * int igraph_count_isomorphisms_vf2(const igraph_t *graph1, const igraph_t *graph2, 
				  const igraph_vector_int_t *vertex_color1,
				  const igraph_vector_int_t *vertex_color2,
				  const igraph_vector_int_t *edge_color1,
				  const igraph_vector_int_t *edge_color2,
				  igraph_integer_t *count,
				  igraph_isocompat_t *node_compat_fn,
				  igraph_isocompat_t *edge_compat_fn,
				  void *arg);

	 */
	public static native int igraph_count_isomorphisms_vf2(Pointer graph1, Pointer graph2, 
			  InternalVectorStruct vertex_color1,
			  InternalVectorStruct vertex_color2,
			  InternalVectorStruct edge_color1,
			  InternalVectorStruct edge_color2,
			  IntByReference count,
			  Pointer node_compat_fn,
			  Pointer edge_compat_fn,
			  Pointer arg
			  );
	/*
	 * int igraph_isomorphic_vf2(const igraph_t *graph1, const igraph_t *graph2, 
			  const igraph_vector_int_t *vertex_color1,
			  const igraph_vector_int_t *vertex_color2,
			  const igraph_vector_int_t *edge_color1,
			  const igraph_vector_int_t *edge_color2,
			  igraph_bool_t *iso, igraph_vector_t *map12, 
			  igraph_vector_t *map21,
			  igraph_isocompat_t *node_compat_fn,
			  igraph_isocompat_t *edge_compat_fn,
			  void *arg);
	 */
	public static native int igraph_isomorphic_vf2(Pointer graph1, Pointer graph2, 
			  InternalVectorStruct vertex_color1,
			  InternalVectorStruct vertex_color2,
			  InternalVectorStruct edge_color1,
			  InternalVectorStruct edge_color2,
			  IntByReference iso, 
			  InternalVectorStruct map12, 
			  InternalVectorStruct map21,
			  Pointer node_compat_fn,
			  Pointer edge_compat_fn,
			  Pointer arg
			  );
	
	/*
	public static native int int igraph_transitivity_local_undirected(const igraph_t *graph,
			 igraph_vector_t *res,
			 const igraph_vs_t vids,
			 igraph_transitivity_mode_t mode);
			 TODO
	*/
	
	/*
	int igraph_transitivity_avglocal_undirected(const igraph_t *graph,
		    igraph_real_t *res,
		    igraph_transitivity_mode_t mode);
	*/
	public static native int igraph_transitivity_avglocal_undirected(Pointer graph,
			DoubleByReference res,
		    int mode);
	

}
