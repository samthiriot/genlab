package genlab.core.model;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import genlab.core.model.exec.ConnectionExec;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IDumpAsExecutionNetwork;
import genlab.core.usermachineinteraction.GLLogger;

public class DebugGraphviz {

	private DebugGraphviz() {
	}

	
	public static void exportExecutionNetwork(String filename, IDumpAsExecutionNetwork dumpable) {
		
		try {
			PrintStream ps = new PrintStream(filename);
			exportExecutionNetwork(ps, dumpable);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("unable to export the execution network as a file "+filename+" because of error: "+e.getMessage(), DebugGraphviz.class, e);
			return;
		}
		
	}

	
	protected static void addSubgraph(
			PrintStream ps, 
			Map<IAlgoExecution, Collection<IAlgoExecution>> exec2children, 
			Set<ConnectionExec> connections,
			IAlgoExecution e,
			int countTabs
			) {
		
		String tabulations = null;
		{
			StringBuffer sbTabulations = new StringBuffer(countTabs);
			for (int i=0; i<countTabs; i++)
				sbTabulations.append("\t");
			tabulations = sbTabulations.toString();
		}
		
		if (!exec2children.containsKey(e)) {
			
			// this is a simple node, with no childrne
			ps.print(tabulations);
			ps.print(e.hashCode());
			ps.print(" [label=\"");
			ps.print(e.getName());
			ps.println("\"];");
			
		} else {
		
			// this is a subgraph (with children)
			
			Collection<IAlgoExecution> children = exec2children.get(e);
			
			// open the subgraph
			ps.print(tabulations);
			ps.print("subgraph cluster");
			ps.print(e.hashCode());
			ps.println(" {");
			
			ps.print(tabulations);
			ps.print("\tlabel=\"");
			ps.print(e.getName());
			ps.println("\";");
			
			// add children
			for (IAlgoExecution e2: children) {
				addSubgraph(ps, exec2children, connections, e2, countTabs+1);
			}
			
			// add possible links

			for (ConnectionExec cex: connections) {
				
				// don't display the edge if it connects to something 
				// which is not this parent or one direct children
				if (	
						cex.from != e 
						&& !children.contains(cex.from) 
					)
					continue; 
				if ( 	cex.to != e
						&& !children.contains(cex.to)
						)
					continue;
				
				ps.print(tabulations);
				ps.print("\t");
				if (exec2children.containsKey(cex.from))
					ps.print("cluster");
				ps.print(cex.from.hashCode());
				ps.print(" -> ");
				if (exec2children.containsKey(cex.to))
					ps.print("cluster");
				ps.print(cex.to.hashCode());
				ps.println(";");
			}
			
			// end of this subgraph
			ps.print(tabulations);
			ps.println("}\r\r");
			
		}
	}
		
	public static void exportExecutionNetwork(PrintStream ps, IDumpAsExecutionNetwork dumpable) {
	
		Set<IAlgoExecution> execs = new HashSet<IAlgoExecution>();
		Set<ConnectionExec> connections = new HashSet<ConnectionExec>();
		
		// collect
		try {
			dumpable.collectEntities(execs, connections);
		} catch (RuntimeException e) {
			// TODO logger
			GLLogger.errorTech("error while attempting to dump the execution network to a file: "+e.getMessage(), DebugGraphviz.class, e);
			return;
		}
		
		// post treatment
		// ... compute the relation parent to children 
		Map<IAlgoExecution, Collection<IAlgoExecution>> exec2children = new HashMap<IAlgoExecution, Collection<IAlgoExecution>>(execs.size());
		{
			for (IAlgoExecution e: execs) {
				
				if (e.getParent() == null)
					continue;
				
				Collection<IAlgoExecution> children = exec2children.get(e.getParent());
				if (children == null) {
					children = new LinkedList<IAlgoExecution>();
					exec2children.put((IAlgoExecution) e.getParent(), children);
				}
				if (!children.contains(e)) {
					children.add(e);
				}
				
			}
		}
		
		
		// header of the file
		ps.println("# to generate a PDF, with graphviz installed, you may use:");
		ps.println("# fdp -Tpdf -otest.pdf test.dot && xdg-open test.pdf"); // TODO replace by the name of the file
		
		ps.println("digraph globalgraph {");
		ps.println();
		
		// header for nodes
		ps.println("\tnode [shape=\"ellipse\"];");
		ps.println();
		
		// first list all nodes
		// ... first display only the root nodes
		for (IAlgoExecution e: execs) {
			
			// filter only the nodes with no parent (i.e. the root nodes)
			if (e.getParent() != null )
				continue;
			
			// also don't display now the nodes having children
			if (exec2children.containsKey(e))
				continue;
			
			ps.print("\t");
			ps.print(e.hashCode());
			ps.print(" [label=\"");
			ps.print(e.getName());
			ps.println("\"];");
		}
		ps.println("\r");
		
		// ... display the subgraphs
		for (IAlgoExecution e: exec2children.keySet()) {
			
			// filter only the nodes with no parent (i.e. the root nodes)
			if (e.getParent() != null )
				continue;
			
			addSubgraph(ps, exec2children, connections, e, 1);

		}
		
		// then connections 
		//ps.println("\tedge [shape=\"ellipse\"];");
		
		for (ConnectionExec cex: connections) {
			if (cex.from.getParent() != null || cex.to.getParent() != null)
				continue; 
			
			ps.print("\t");
			ps.print(cex.from.hashCode());
			ps.print(" -> ");
			ps.print(cex.to.hashCode());
			ps.println(";");
		}
	
		// file footer
		ps.println("}");
		
	}
	
}
