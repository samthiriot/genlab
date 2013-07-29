package genlab.igraph.algos.generation.lcffamous;


public class FamousLCFGraphs {

	public static void declareFamousLCF() {
		
		// http://mathworld.wolfram.com/LCFNotation.html
		
		new AbstractLCFFamousGraph(
				"tetrahedral graph", 
				"The tetrahedral graph is the Platonic graph that is the unique polyhedral graph on four nodes which is also the complete graph K4 and therefore also the wheel graph W4.", 
				"[2,-2]2", 
				"http://en.wikipedia.org/wiki/Tetrahedron",
				4
				);
		new AbstractLCFFamousGraph(
				"utility graph", 
				"The utility problem posits three houses and three utility companies--say, gas, electric, and water--and asks if each utility can be connected to each house without having any of the gas/water/electric lines/pipes pass over any other. This is equivalent to the equation \"Can a planar graph be constructed from each of three nodes ('houses') to each of three other nodes ('utilities')?\" This problem was first posed in this form by H. E. Dudeney in 1917 (Gardner 1984, p. 92).", 
				"[3,-3]3", 
				"http://mathworld.wolfram.com/UtilityGraph.html",
				6
				);
		new AbstractLCFFamousGraph(
				"cubical graph", 
				"In graph theory, the hypercube graph Qn is a regular graph with 2n vertices, 2n−1n edges, and n edges touching each vertex. It can be obtained as the one-dimensional skeleton of the geometric hypercube; for instance, Q3 is the graph formed by the 8 vertices and 12 edges of a three-dimensional cube. Alternatively, it can be obtained from the family of subsets of a set with n elements, by making a vertex for each possible subset and joining two vertices by an edge whenever the corresponding subsets differ in a single element.", 
				"[3,-3]4", 
				"http://en.wikipedia.org/wiki/Hypercube_graph",
				8
				);
		new AbstractLCFFamousGraph(
				"wagner graph", 
				"In the mathematical field of graph theory, the Wagner graph is a 3-regular graph with 8 vertices and 12 edges. It is the 8-vertex Möbius ladder graph.", 
				"[4,-3,3,4]2", 
				"http://en.wikipedia.org/wiki/Wagner_graph",
				8
				);
		new AbstractLCFFamousGraph(
				"Bidiakis cube", 
				"In the mathematical field of graph theory, the Bidiakis cube is a 3-regular graph with 12 vertices and 18 edges.", 
				"[6,4,-4]4", 
				"http://en.wikipedia.org/wiki/Bidiakis_cube",
				12
				);
		new AbstractLCFFamousGraph(
				"Franklin graph", 
				"In the mathematical field of graph theory, the Franklin graph a 3-regular graph with 12 vertices and 18 edges.", 
				"[-5,-3,3,5]3", 
				"http://en.wikipedia.org/wiki/Franklin_graph",
				12
				);
		new AbstractLCFFamousGraph(
				"Frucht graph", 
				"In the mathematical field of graph theory, the Frucht graph is a 3-regular graph with 12 vertices, 18 edges, and no nontrivial symmetries. It was first described by Robert Frucht in 1939.", 
				"[-5,-2,-4,2,5,-2,2,5,-2,-5,4,2]", 
				"http://en.wikipedia.org/wiki/Frucht_graph",
				12
				);
		new AbstractLCFFamousGraph(
				"Truncated tetrahedral graph", 
				"Truncated tetrahedral graph", 
				"[2,6,-2]4", 
				"http://en.wikipedia.org/wiki/Tetrahedron",
				12
				);
		new AbstractLCFFamousGraph(
				"Heawood graph", 
				"In the mathematical field of graph theory, the Heawood graph is an undirected graph with 14 vertices and 21 edges, named after Percy John Heawood.", 
				"[5,-5]7", 
				"http://en.wikipedia.org/wiki/Heawood_graph",
				14
				);

		new AbstractLCFFamousGraph(
				"Möbius–Kantor graph", 
				"In the mathematical field of graph theory, the Möbius–Kantor graph is a symmetric bipartite cubic graph with 16 vertices and 24 edges named after August Ferdinand Möbius and Seligmann Kantor. It can be defined as the generalized Petersen graph G(8,3): that is, it is formed by the vertices of an octagon, connected to the vertices of an eight-point star in which each point of the star is connected to the points three steps away from it.", 
				"[5,-5]8", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius%E2%80%93Kantor_graph",
				16
				);

		new AbstractLCFFamousGraph(
				"Pappus graph", 
				"In the mathematical field of graph theory, the Pappus graph is a bipartite 3-regular undirected graph with 18 vertices and 27 edges, formed as the Levi graph of the Pappus configuration.", 
				"[5,7,-7,7,-7,-5]3", 
				"http://en.wikipedia.org/wiki/Pappus_graph",
				18
				);

		new AbstractLCFFamousGraph(
				"Desargues graph", 
				"In the mathematical field of graph theory, the Desargues graph is a distance-transitive cubic graph with 20 vertices and 30 edges.", 
				"[5,-5,9,-9]5", 
				"http://en.wikipedia.org/wiki/Desargues_graph",
				20
				);


		new AbstractLCFFamousGraph(
				"Dodecahedral graph", 
				"The high degree of symmetry of the polygon is replicated in the properties of this graph, which is distance-transitive, distance-regular, and symmetric. The automorphism group has order 120. The vertices can be colored with 3 colors, as can the edges, and the diameter is 5", 
				"[10,7,4,-4,-7,10,-4,7,-7,4]2", 
				"http://en.wikipedia.org/wiki/Dodecahedron#As_a_graph",
				20
				);


		new AbstractLCFFamousGraph(
				"McGee graph", 
				"In the mathematical field of graph theory, the McGee Graph or the (3-7)-cage is a 3-regular graph with 24 vertices and 36 edges.", 
				"[12,7,-7]8", 
				"http://en.wikipedia.org/wiki/McGee_graph",
				24
				);

/*
		new AbstractLCFFamousGraph(
				"", 
				"", 
				"", 
				"",
				
				);
*/
		// TODO from http://en.wikipedia.org/wiki/LCF_notation
		
		new AbstractLCFFamousGraph(
				"Tutte 12-cage", 
				"In the mathematical field of graph theory, the Tutte 12-cage or Benson graph is a 3-regular graph with 126 vertices and 189 edges named after W. T. Tutte.", 
				"[17, 27, -13, -59, -35, 35, -11, 13, -53, 53, -27, 21, 57, 11, -21, -57, 59, -17]7", 
				"http://en.wikipedia.org/wiki/Tutte_12-cage",
				125
				);
		
		new AbstractLCFFamousGraph(
				"4-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-4]8", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				8
				);
		new AbstractLCFFamousGraph(
				"5-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-5]10", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				10
				);
		new AbstractLCFFamousGraph(
				"6-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-6]12", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				12
				);
		new AbstractLCFFamousGraph(
				"7-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-7]14", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				14
				);
		new AbstractLCFFamousGraph(
				"8-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-8]16", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				16
				);
		new AbstractLCFFamousGraph(
				"9-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-9]18", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				18
				);
		new AbstractLCFFamousGraph(
				"10-Möbius ladder", 
				"The Möbius ladder Mn is a cubic circulant graph with an even number n of vertices, formed from an n-cycle by adding edges (called \"rungs\") connecting opposite pairs of vertices in the cycle.", 
				"[-10]20", 
				"http://en.wikipedia.org/wiki/M%C3%B6bius_ladder",
				20
				);
		
		// TODO http://mathworld.wolfram.com/LCFNotation.html
		
		
	}
	
	private FamousLCFGraphs() {
		
	}

}
