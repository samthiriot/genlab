package genlab.bayesianinference;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A Bayesian Network cache
 * TODO no max.
 * @author Samuel Thiriot
 */
public class BNCache {
	
	//private Logger logger = Logger.getLogger("bnCache");
	
	private Map<String, String> currentEvidenceVar2observed = new HashMap<String, String>(100);
	
	
	protected String currentEvidence = null;
	protected Map<String,double[]> currentPosteriors = null;
	
	private final int START_SIZE = 200;
	
	/**
	 * Maps some evidence (built from some values) to the corresponding posteriors
	 */
	private Map<String,Map<String,double[]>> evidence2posteriors = new HashMap<String, Map<String,double[]>>(START_SIZE);
	private Map<String,Integer> evidence2count = new HashMap<String, Integer>(START_SIZE);
	
	
	public final static boolean DEBUG1 = false;
	
	
	public void clear() {
		clearInternal();
		currentEvidenceVar2observed.clear();
		evidence2count.clear();
		evidence2posteriors.clear();
		Runtime.getRuntime().gc();
	}
	
	public void addCurrentEvidence(String var, Object observed) {
		currentEvidenceVar2observed.put(var, observed.toString());
		clearInternal();
		//logger.debug("added "+var.getID()+" = "+observed.toString());
		
	}
	
	public void removeCurrentEvidence(String var) {
		currentEvidenceVar2observed.remove(var);
		clearInternal();
		//logger.debug("removed "+var.getID());
		if (DEBUG1) System.err.print("r");
	}
		
	
	public void resetEvidence() {
		currentEvidenceVar2observed.clear();
		clearInternal();
		if (DEBUG1) System.err.print("R");
		//logger.debug("cleared");
	}
	
	private String createEvidenceForCurrent() {
		// TODO 
		// currentEvidence = 
		StringBuffer sb = new StringBuffer();
	
		LinkedList<String> sortedVariables = new LinkedList<String>(currentEvidenceVar2observed.keySet());
		Collections.sort(sortedVariables);
		
		for (String s : sortedVariables) {
			sb.append(s);
			sb.append("=");
			sb.append(currentEvidenceVar2observed.get(s));
			sb.append("|");
		}
		
		
		return sb.toString();
	}
	
	private String getEvidenceForCurrent() {
		if (currentEvidence == null) {
			currentEvidence = createEvidenceForCurrent();

			//logger.debug("current evidence is : "+currentEvidence);
		}
		return currentEvidence;
	}
	
	private void clearInternal() {
		currentEvidence = null;
		currentPosteriors = null;
	}
	
	private void loadPosteriorsForCurrentEvidence() {
		currentEvidence = createEvidenceForCurrent();
		currentPosteriors = evidence2posteriors.get(currentEvidence);
	}
	
	public boolean hasCacheForThisEntry() {
		if (currentEvidence == null)
			loadPosteriorsForCurrentEvidence();
	
		
		/*
		if (currentPosteriors != null)

			logger.debug("evidence in cache :-)");
		else 
			logger.debug("no evidence in cache :-(");
		
		*/
		return (currentPosteriors != null);
	}
	
	public boolean hasCacheForThisEntry(IBayesianNode var) {
	
		if (!hasCacheForThisEntry())
			return false; // no cache at all
		
		return currentPosteriors.containsKey(var);
		
	}
	
	public double[] getCurrentPosteriors(IBayesianNode var) {
		if (currentPosteriors == null) {
			loadPosteriorsForCurrentEvidence();
			if (DEBUG1) System.err.print("$");
			//logger.debug("loaded from cache");
		}
		if (DEBUG1) 
			System.err.print("#");
		evidence2count.put(getEvidenceForCurrent(), evidence2count.get(getEvidenceForCurrent())+1);
		
		return currentPosteriors.get(var);
	}
	
	public void addResultForCurrentEvidence(IBayesianNode variable, double[] values) {
		
		if (currentPosteriors == null) {
			currentPosteriors = new HashMap<String, double[]>();
			evidence2posteriors.put(getEvidenceForCurrent(), currentPosteriors);
		//	logger.debug("added :"+getEvidenceForCurrent()+", "+Arrays.toString(values));
		//	logger.debug("now containing "+evidence2posteriors.size());
			evidence2count.put(getEvidenceForCurrent(), 0);
		} 
		currentPosteriors.put(variable.getID(), values);
		if (DEBUG1) System.err.print("c");
		
		//logger.debug("added :"+getEvidenceForCurrent()+", "+Arrays.toString(values));
		//logger.debug("now containing "+evidence2posteriors.size());
	}
	
	public void printall(PrintStream ps) {
		
		int to = 0;
		for (String s: evidence2count.keySet()) {
			ps.println(evidence2count.get(s)+"\t("+evidence2posteriors.get(s).size()+")\t"+s);
			to+= evidence2posteriors.get(s).size();
		}
		ps.println("total "+evidence2count.size()+" cached, total access "+to);
	}
}
