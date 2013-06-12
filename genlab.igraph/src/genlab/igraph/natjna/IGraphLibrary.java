package genlab.igraph.natjna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
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
public interface IGraphLibrary extends Library {


	IGraphLibrary INSTANCE = (IGraphLibrary) Native.loadLibrary("igraph", IGraphLibrary.class);
	
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

	public class Igraph_vector_t extends Structure {
		
		public IntByReference stor_begin;
		public IntByReference stor_end;
		public IntByReference end;

		public static class ByReference extends Igraph_vector_t implements Structure.ByReference {}

		public Igraph_vector_t() {
			stor_begin = new IntByReference();
			stor_end = new IntByReference();
			end = new IntByReference();
			ensureAllocated();
//		    allocateMemory();

		}
		
		public Igraph_vector_t(Pointer p) {
			super(p);
			read();
			/*
			stor_begin = new IntByReference();
			stor_end = new IntByReference();
			end = new IntByReference();
			*/
		}
		
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "stor_begin", "stor_end", "end" });
		}
	}

	public static class IGraphGraph extends Structure {
		
		public static class ByReference extends IGraphGraph implements Structure.ByReference {}
		
		public int n;
		public boolean directed;
		public Pointer from;
		public Pointer to;
		public Pointer oi;
		public Pointer ii;
		public Pointer os;
		public Pointer is;
		public PointerByReference attr;
		
		public IGraphGraph(Pointer p) {
			super(p);
			read();
		}
		
		public IGraphGraph() {
			n = 0;
			directed = false;
			from = Memory.createConstant(Pointer.SIZE);
			to = Memory.createConstant(Pointer.SIZE);
			ensureAllocated();
			/*
			n = 0;
			directed = false;
			from = new Igraph_vector_t();
			to = new Igraph_vector_t();
			oi = new Igraph_vector_t();
			ii = new Igraph_vector_t();
			os = new Igraph_vector_t();
			is = new Igraph_vector_t();
			attr = new PointerByReference();
			*/
		}
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "n", "directed", "from", "to", "oi", "ii", "os", "is", "attr" });
		}
		 
	
	}
	
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
	// TODO ?
	
	/*
	int igraph_watts_strogatz_game(igraph_t *graph, igraph_integer_t dim,
		       igraph_integer_t size, igraph_integer_t nei,
		       igraph_real_t p, igraph_bool_t loops, 
		       igraph_bool_t multiple);
	*/
	/**
	 * @see http://igraph.sourceforge.net/doc/html/ch09s02.html#igraph_watts_strogatz_game
	 */
	public int igraph_watts_strogatz_game (
			IGraphLibrary.IGraphGraph graph,
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
	public int igraph_empty(ByReference graph, int n, boolean directed);

	/*
	 * igraph_integer_t igraph_vcount(const igraph_t *graph);
	 */
	/**
	 * Returns the vertex count
	 * @param graph
	 * @return
	 */
	public int igraph_vcount(IGraphLibrary.IGraphGraph.ByReference graph);
	
	 
	/*
	 * igraph_integer_t igraph_ecount(const igraph_t *graph);
	 */
	/**
	 * Returns the edge count
	 * @param graph
	 * @return
	 */
	public int igraph_ecount(IGraphLibrary.IGraphGraph.ByReference graph);
		
	// TODO free memory !
	/*
	 * igraph_destroy
	 */
}
