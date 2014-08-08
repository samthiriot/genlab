package genlab.bayesianinference.smile;

import java.io.PrintStream;
import java.util.List;

import javax.swing.text.html.MinimalHTMLWriter;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IInferenceEngine;
import genlab.core.performance.TimeMonitor;
import genlab.core.performance.TimeMonitors;
import genlab.core.random.IRandomNumberGenerator;
import genlab.random.colt.ColtRandomGenerator;

public class SMILEInferenceEngine implements IInferenceEngine {

	final SMILEBayesianNetwork bn;
	final IRandomNumberGenerator rand;
	

	/**
	 * if true, will monitor the time spent in tasks 
	 */
	public static boolean MONITOR = true;

	/**
	 * The time monitoring, defined only if MONITOR is true
	 */
	private final TimeMonitor timeMonitor;
	
	private final static String keyTimeForEvidenceRetract = "evidence retractation";
	private final static String keyTimeForPosteriors = "posteriors computation";
	private final static String keyTimeForEvidenceAssertion = "evidence assertion";
	
	
	public SMILEInferenceEngine(IBayesianNetwork bn, IRandomNumberGenerator rand) {

		this.bn = getAsLocalNetwork(bn);
		
		this.bn.smileNet.updateBeliefs();
		
		this.rand = rand;
		
		if (MONITOR)
			timeMonitor = TimeMonitors.SINGLETON.getMonitor("inference SMILE");
		else
			timeMonitor = null;
		
	}
	
	public SMILEInferenceEngine(IBayesianNetwork bn) {
		this(bn, new ColtRandomGenerator());
	}
	
	protected SMILEBayesianNetwork getAsLocalNetwork(IBayesianNetwork bn) {
		
		if (bn instanceof SMILEBayesianNetwork) {
			return (SMILEBayesianNetwork)bn;
		} else {
			return new SMILEBayesianNetwork(bn);
		}
	}

	@Override
	public void initFromFile(String filenameBayesianNetwork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBayesianNetwork getBayesianNetwork() {
		return bn;
	}

	@Override
	public void resetEvidence() {
		
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceRetract, this);
		
		bn.smileNet.clearAllEvidence();
		bn.smileNet.updateBeliefs();
		
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceRetract, this);
		

	}

	@Override
	public void addEvidenceForAttribute(String attributeName, int index) {
		

		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceAssertion, this);
		
		bn.smileNet.setEvidence(attributeName, index);
		bn.smileNet.updateBeliefs();


		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceAssertion, this);
		
	}

	@Override
	public void retractEvidence(String attributeName) {
		
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceRetract, this);
		
		bn.smileNet.clearEvidence(attributeName);
		bn.smileNet.updateBeliefs();

		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceRetract, this);
		
	}

	@Override
	public int getAttributeRandomnlyGiven(String attributeName) {
		
		double total2 = 0.0;
		final double random = rand.nextDoubleUniform();
	
		try {
			double[] table = getPosteriors(attributeName);
			
			int i;
			for (i=0; i<table.length; i++) {
				total2 += table[i];
				
				if (random <= total2) {
					return i;
				} 
			}
			//logger.warn("used fallback probability for var "+var.name()+", marginals "+Arrays.toString(marginals));
			System.err.println("FALLBACK !");
			return i-1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}

	@Override
	public double[] getPosteriors(String attributeName) {
		return bn.smileNet.getNodeValue(attributeName);
	}

	@Override
	public Object getValueOf(String attributeName, int index) {
		return bn.smileNet.getOutcomeId(attributeName, index);
	}

	@Override
	public List<String> valuesOf(String attributeName) {
		return bn.getForID(attributeName).getDomain();
	}

	@Override
	public void exportPropagationStatistics(PrintStream ps) {
		if (MONITOR)
			timeMonitor.printToStream(ps);
	}

}
