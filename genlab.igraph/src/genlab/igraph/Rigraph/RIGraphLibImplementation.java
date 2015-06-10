package genlab.igraph.Rigraph;

import static org.junit.Assert.fail;
import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraphLibImplementation;
import genlab.r.rsession.Genlab2RSession;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RSession;

public class RIGraphLibImplementation implements IGraphLibImplementation {

	
	protected static Boolean isAvailable = null;
	
	/**
	 * Ensures it is possible to load the igraph package
	 * @return
	 */
	protected static boolean ensureIgraphCanBeLoaded() {
		Rsession r = null;
		try {
			r = Genlab2RSession.pickOneSessionFromPool();
			
			boolean res = r.isPackageInstalled("igraph", null);
			
			if (!res) {
				GLLogger.tipUser("the connection with R is working, but R/igraph library is not installed. Please install it with install.packages(igraph)", RIGraphLibImplementation.class);
				return false;
			} else {
				
				String versionStr = null;
				
				// try to get version n1
				try {
					r.loadPackage("igraph");
					REXP version = r.eval("igraph.version()");
					if (version != null)
						versionStr = version.asString();
				} catch (REXPMismatchException e) {
					// not working ? 
				}
				
				if (versionStr == null) {
					try {
						REXP version = r.eval("packageDescription(\"igraph\")$Version");
						versionStr = version.asString();
					} catch (REXPMismatchException e) {
						// not working ? 
					}
				}
				
				GLLogger.infoUser("Can use the R/igraph library "+versionStr, RIGraphLibImplementation.class);
				return true;
			}
			// TODO test version ?
		} catch (Exception e) {
			GLLogger.errorTech("Unable to connect to R/igraph: "+e.getLocalizedMessage(), RIGraphLibImplementation.class, e);
			return false;
		} finally {
			if (r != null)
				Genlab2RSession.returnSessionFromPool(r);
		}
	}
	
	public static boolean isAvailable() {
		if (isAvailable == null) {
			
			if (!Genlab2RSession.isRAvailable()) {
				isAvailable = false;
				GLLogger.tipUser("the R/igraph library is not available, because the connection with R is not available. Configure it to use the numerous features of igraph", RIGraphLibImplementation.class); 
			} else {
				isAvailable = ensureIgraphCanBeLoaded();
			}
		}
		return isAvailable;
	}
	
	public RIGraphLibImplementation() {
		// TODO Auto-generated constructor stub
	}

	protected void initializeSession(Rsession rsession, Long seed, ListOfMessages messages) {
		
		// load lib
		if (!rsession.isPackageLoaded("igraph"))
			rsession.loadPackage("igraph");
		
		if (!rsession.isPackageLoaded("igraph"))
			throw new ProgramException("was unable to load the igraph package in the R session");
		
		// set seed
		if (seed != null) {
			// we should define a seed
			
			// is this seed too long ? 
			if (seed.longValue() >= Integer.MAX_VALUE) 
				messages.warnUser("the seed proposed is too long for the R/igraph library; it will be truncated. Please use a seed lower than "+Integer.MAX_VALUE, getClass());
			
			StringBuffer cmd = new StringBuffer();
			cmd.append("set.seed(").append(seed.intValue()).append(")");
			rsession.eval(cmd.toString());
		}
		
	}
	@Override
	public IGenlabGraph generateErdosRenyiGNM(int size, double m,
			boolean directed, boolean allowLoops, IExecution execution, Long seed) {

		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			initializeSession(rsession, seed, execution.getListOfMessages());
			
			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- erdos.renyi.game(n=");
			command.append(size).append(", p.or.m=");
			command.append(m).append(", directed=");
			command.append(directed?"TRUE":"FALSE").append(", loops=");
			command.append(allowLoops?"TRUE":"FALSE");
			command.append(", type=\"gnm\")");
			rsession.eval(command.toString());
	
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateForestFire(int size, double fw_prob,
			double bw_factor, int pambs, boolean directed,
			boolean simplifyMultiple, boolean simplifyLoops,
			IExecution execution, Long seed) {

		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
			
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- forest.fire.game(");
			command.append(size).append(", ");
			command.append(fw_prob).append(", ");
			command.append(bw_factor).append(", ");
			command.append(pambs).append(", ");
			command.append(directed?"TRUE":"FALSE");
			command.append(")");
			rsession.eval(command.toString());
	
			// simplify it
			simplifyTheGraph(rsession, "g", true, true);
						
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateBarabasiAlbert(int size, int m, double power,
			double zeroAppeal, boolean directed, boolean outputPref, double A,
			IExecution execution, Long seed) {

		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();

			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- barabasi.game(");
			command.append(size).append(", power=");
			command.append(power).append(", m=");
			command.append(m).append(", out.pref=");
			command.append(outputPref?"TRUE":"FALSE").append(", zero.appeal=");
			command.append(zeroAppeal);
			command.append(", algorithm=\"psumtree\")");
			rsession.eval(command.toString());
	
			// simplify it
			// useless here simplifyTheGraph(rsession, "g", true, true);
						
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateInterconnectedIslands(int islands_n,
			int islands_size, double islands_pin, int n_inter,
			boolean simplifyLoops, boolean simplifyMultiplex,
			IExecution execution, Long seed) {
		
		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- interconnected.islands.game(");
			command.append(islands_n).append(", ");
			command.append(islands_size).append(", ");
			command.append(islands_pin).append(", ");
			command.append(n_inter);
			command.append(")");
			rsession.eval(command.toString());
	
			// simplify it
			// useless here simplifyTheGraph(rsession, "g", true, true);
						
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateWattsStrogatz(int size, int dimension,
			double proba, int nei, boolean directed, boolean allowLoops,
			IExecution execution, Long seed) {
		
		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- watts.strogatz.game(");
			command.append(dimension).append(", ");
			command.append(size).append(", ");
			command.append(nei).append(", ");
			command.append(proba).append(", ");
			command.append("loops=").append(allowLoops?"TRUE":"FALSE").append(", ");
			command.append("multiple=FALSE");
			command.append(")");
			rsession.eval(command.toString());
	
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(
					rsession, "g", false, execution.getListOfMessages(), GraphDirectionality.UNDIRECTED);
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateGRG(int nodes, double radius, boolean torus,
			IExecution execution, Long seed) {
		
		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- grg.game(");
			command.append(nodes).append(", ");
			command.append(radius).append(", ");
			command.append(torus?"TRUE":"FALSE").append(", ");
			command.append(")");
			rsession.eval(command.toString());
	
			// simplify it
			simplifyTheGraph(rsession, "g", true, true);
						
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateLCF(int nodes, int[] paramShifts, int repeats,
			IExecution execution) {
		return null;
	}
	
	protected void simplifyTheGraph(Rsession r, String variableName, boolean removeMultiple, boolean removeLoops) {
		

		// simplify it
		StringBuffer command = new StringBuffer();
		command.append(variableName).append(" <- simplify(").append(variableName).append(", remove.multiple=");
		command.append(removeMultiple?"TRUE":"FALSE");
		command.append(", remove.loops=");
		command.append(removeLoops?"TRUE":"FALSE");
		command.append(")");
		
		r.eval(command.toString());
		
	}

	@Override
	public IGenlabGraph simplifyGraph(IGenlabGraph g, boolean removeMultiple,
			boolean removeLoops, IExecution execution) {

		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());

			// simplify it
			simplifyTheGraph(rsession, "g", removeMultiple, removeLoops);
			
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph generateErdosRenyiGNP(int size, double proba,
			boolean directed, boolean loops, IExecution execution, Long seed) {
		
		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			StringBuffer command = new StringBuffer();
			command.append("g <- erdos.renyi.game(n=");
			command.append(size).append(", p.or.m=");
			command.append(proba).append(", directed=");
			command.append(directed?"TRUE":"FALSE").append(", loops=");
			command.append(loops?"TRUE":"FALSE");
			command.append(", type=\"gnp\")");
			rsession.eval(command.toString());
	
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public IGenlabGraph rewire(IGenlabGraph g, int count, IExecution execution, Long seed) {
		Rsession rsession = null;
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
			initializeSession(rsession, seed, execution.getListOfMessages());

			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());

			// simplify it
			rsession.eval("g <- rewire(g, more=\"simple\"), niter="+count+")");
			
			// load the graph from R
			IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, execution.getListOfMessages());
			
			return graph;
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public double computeAveragePathLength(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("average.path.length(g)");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDouble();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public boolean computeIsomorphicm(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g1, rsession, "g1", execution.getListOfMessages());
			RIGraph2Genlab.loadGraphToRIgraph(g2, rsession, "g2", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("graph.isomorphic(g1,g2)");
			Genlab2RSession.checkStatus(rsession);

			return ((REXPLogical)rexp).isTRUE()[0];
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g1");
				rsession.unset("g2");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}

	}

	@Override
	public boolean computeIsomorphismVF2(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public int computeIsomorphismVF2Count(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public boolean computeVF2Isomorphicm(IGenlabGraph g1, IGenlabGraph g2,
			IExecution execution) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public int computeDiameter(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("diameter(g)");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asInteger();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public boolean isConnected(IGenlabGraph g, IExecution execution) {
		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("is.connected(g)");
			Genlab2RSession.checkStatus(rsession);

			return ((REXPLogical)rexp).isTRUE()[0];
			
		} catch (Exception e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public double[] computeNodeBetweeness(IGenlabGraph g, boolean directed,
			IExecution execution) {
	
		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("betweenness(g, directed=");
			cmd.append(directed?"TRUE":"FALSE");
			cmd.append(")");
			REXP rexp = rsession.eval(cmd.toString());
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public double[] computeNodeBetweenessEstimate(IGenlabGraph g,
			boolean directed, double cutoff, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("betweenness.estimate(g, directed=");
			cmd.append(directed?"TRUE":"FALSE");
			cmd.append(", cutoff=").append(cutoff);
			cmd.append(")");
			REXP rexp = rsession.eval(cmd.toString());
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}



	@Override
	public double[] computeNodeCloseness(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("closeness(g)");
			
			REXP rexp = rsession.eval(cmd.toString());
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}
	

	@Override
	public double[] computeNodeAlphaCentrality(IGenlabGraph g,
			IExecution execution) {
		
		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			if (!rsession.isPackageLoaded("Matrix"))
				rsession.loadPackage("Matrix");
			if (!rsession.isPackageLoaded("Matrix"))
				throw new ProgramException("was unable to load the Matrix package in the R session");
			
			if (!rsession.isPackageLoaded("lattice"))
				rsession.loadPackage("lattice");
			if (!rsession.isPackageLoaded("lattice"))
				throw new ProgramException("was unable to load the lattice package in the R session");
			

			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("alpha.centrality(g)");
			
			REXP rexp = rsession.eval(cmd.toString());
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	
	@Override
	public double[] computeEdgeBetweeness(IGenlabGraph g, boolean directed,
			IExecution execution) {


		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("edge.betweenness(g, directed=");
			cmd.append(directed?"TRUE":"FALSE");
			cmd.append(")");
			REXP rexp = rsession.eval(cmd.toString());
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public double[] computeEdgeBetweenessEstimate(IGenlabGraph g,
			boolean directed, double cutoff, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			StringBuffer cmd = new StringBuffer();
			cmd.append("edge.betweenness.estimate(g, directed=");
			cmd.append(directed?"TRUE":"FALSE");
			cmd.append(", cutoff=").append(cutoff);
			cmd.append(")");
			REXP rexp = rsession.eval(cmd.toString());
	
			return rexp.asDoubles();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while decoding the result from R"+e.getLocalizedMessage(), e);
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public int computeComponentsCount(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("no.clusters(g)");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asInteger();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public int computeGiantCluster(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("max(clusters(g)$csize)");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asInteger();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public Double computeGlobalClustering(IGenlabGraph g, IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("transitivity(g, type=\"global\")");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDouble();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

	@Override
	public Double computeGlobalClusteringLocal(IGenlabGraph g,
			IExecution execution) {

		Rsession rsession = null;
		
		try {
			rsession = Genlab2RSession.pickOneSessionFromPool();
	
			// load lib
			if (!rsession.isPackageLoaded("igraph"))
				rsession.loadPackage("igraph");
			
			if (!rsession.isPackageLoaded("igraph"))
				throw new ProgramException("was unable to load the igraph package in the R session");
			
			// create graph
			RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g", execution.getListOfMessages());
			
			// measure it
			REXP rexp = rsession.eval("transitivity(g, type=\"localaverage\")");
			Genlab2RSession.checkStatus(rsession);

			return rexp.asDouble();
			
		} catch (REXPMismatchException e) {
			throw new RuntimeException("error while computing transitivity: "+e.getMessage(), e);
			
		} finally {

			if (rsession != null) {
				// reclaim R memory
				rsession.unset("g");
				// return the session to the pool
				Genlab2RSession.returnSessionFromPool(rsession);
			}
			
		}
	}

}
