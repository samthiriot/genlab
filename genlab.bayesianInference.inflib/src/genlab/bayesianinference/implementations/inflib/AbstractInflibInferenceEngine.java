package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.EvidenceController;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.StateNotFoundException;
import edu.ucla.belief.io.hugin.HuginNetImpl;
import edu.ucla.belief.io.hugin.HuginNodeImpl;
import genlab.bayesianinference.AbstractInferenceEngine;
import genlab.bayesianinference.BNCache;
import genlab.bayesianinference.BayesianNetworkException;
import genlab.core.performance.TimeMonitor;
import genlab.core.performance.TimeMonitors;
import genlab.core.random.IRandomNumberGenerator;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.random.colt.ColtRandomGenerator;

import java.io.PrintStream;
import java.util.List;

public abstract class AbstractInflibInferenceEngine extends AbstractInferenceEngine<InflibNode> {

	/**
	 * Enables or disables cache.
	 */
	public static boolean WITH_CACHE = true;

	/**
	 * If true, will export on std output the cost in a synthetic way ($=costly, c=cache)
	 */
	public static boolean DEBUG = false;

	/**
	 * if true, will monitor the time spent in tasks 
	 */
	public static boolean MONITOR = true;

	/**
	 * The time monitoring, defined only if MONITOR is true
	 */
	private final TimeMonitor timeMonitor;
	
	private final static String keyTimeForCompilation = "compilation";
	private final static String keyTimeForEvidenceRetract = "evidence retractation";
	private final static String keyTimeForPosteriors = "posteriors computation";
	private final static String keyTimeForEvidenceAssertion = "evidence assertion";
	
	
	/**
	 * The actual Bayesian network Inflib implementation
	 */
	protected HuginNetImpl bayesianNetwork = null;

	/**
	 * The inflib evidence controller
	 */
	protected EvidenceController evidenceController;

	/**
	 * The actual Bayesian inference engine (inflib implementation)
	 */
	private InferenceEngine engine;

	/**
	 * Logging facility
	 */
	protected ListOfMessages messages;

	/**
	 * Caching to avoid the computation of similar evidence propagation 
	 */
	private BNCache cache = null;
	
	private IRandomNumberGenerator randomNumberGenerator;

	public AbstractInflibInferenceEngine(String name) {
		
		if (MONITOR)
			timeMonitor = TimeMonitors.SINGLETON.getMonitor("inference "+name);
		else
			timeMonitor = null;
		
		messages = ListsOfMessages.getGenlabMessages();
		
		if (WITH_CACHE)
			cache = new BNCache();
		
		randomNumberGenerator = new ColtRandomGenerator();
	}
	
	public AbstractInflibInferenceEngine(String name, String filename) {
		this(name);
		
		initFromFile(filename);
	}

	public void clear() {
		cache.clear();
	
	}
	
	public void initFromFile(String filenameBayesianNetwork) {
		
		messages.traceTech("init started...", getClass());
		messages.traceTech("loading network from "+filenameBayesianNetwork, getClass());
		if (bayesianNetwork== null) 
			loadBN(filenameBayesianNetwork, false);
		messages.traceTech("launch compilation of "+filenameBayesianNetwork, getClass());
		
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForCompilation, this);
		engine = compileBN();
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForCompilation, this);
		
		messages.traceTech("init evidence...", getClass());
		initEvidence();		
		messages.traceTech("init completed.", getClass());
		
	}	
	
	protected void buildInternalMappingForNetwork() {
		
		InflibBaysianNetwork inflibNet = new InflibBaysianNetwork(bayesianNetwork);
				
		for (Object v: bayesianNetwork.vertices()) {
		
			final HuginNodeImpl vHugin = (HuginNodeImpl)v;
			
			setBNAttributeForAttributeName(vHugin.id, new InflibNode(inflibNet, vHugin));
		}
		
	}
	
	public void loadBN(String filenameBayesianNetwork, boolean createIfNotFound) {
		
		bayesianNetwork = InflibBaysianNetwork.loadFromFile(filenameBayesianNetwork, createIfNotFound);
		
		buildInternalMappingForNetwork();
		
	}
	
	/**
	 * Should set the internal value of "engine"
	 * @return 
	 */
	public abstract InferenceEngine compileBN();

	protected void initEvidence() {

		evidenceController = bayesianNetwork.getEvidenceController();
		evidenceController.setNotifyEnabled(true);
		
	}
	
	@Override
	public void resetEvidence() {

		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceRetract, this);
		
		if (WITH_CACHE)
			cache.resetEvidence();
		
		synchronized (evidenceController) {
			evidenceController.resetEvidence();
		}
		
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceRetract, this);
		
	}

	@Override
	public void addEvidenceForAttribute(String attributeName, int index) {
	
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceAssertion, this);
		
		InflibNode node = getBNAttributeForAttributeName(attributeName);
		
		try {
			
			synchronized (evidenceController) {
				evidenceController.observe(
						node.huginNode,
						node.getDomain().get(index)
						);
			}
			
		} catch (StateNotFoundException e) {
			throw new BayesianNetworkException("while trying to add evidence for variable "+node.getID()+":"+index+" ("+node+")", e);
		}
		
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceAssertion, this);
	
	}

	@Override
	public void retractEvidence(String attributeName) {
		
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForEvidenceRetract, this);
		
		if (WITH_CACHE)
			cache.resetEvidence();
		
		synchronized (evidenceController) {
			evidenceController.unobserve(getBNAttributeForAttributeName(attributeName).huginNode);
		}
		
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForEvidenceRetract, this);
		
	}

	@Override
	public int getAttributeRandomnlyGiven(String attributeName) {
		
		double total2 = 0.0;
		final double random = randomNumberGenerator.nextDoubleUniform();
	
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

	protected final double[] getPosteriors(InflibNode node) {
		
		if (MONITOR)
			timeMonitor.notifyTaskStart(keyTimeForPosteriors, this);
				
		if ( WITH_CACHE && cache.hasCacheForThisEntry(node) ) {
				return cache.getCurrentPosteriors(node);	
		} 
		
		double[] res;
		
		synchronized (evidenceController) {
			res = engine.conditional(node.huginNode).dataclone();
		}
		
		if (DEBUG) System.err.print("$");
		
		if (WITH_CACHE)
			cache.addResultForCurrentEvidence(node, res);
		
		if (MONITOR)
			timeMonitor.notifyTaskEnd(keyTimeForPosteriors, this);
		
		return res;		
	}

	@Override
	public final double[] getPosteriors(String attributeName) {
		
		final InflibNode node = getBNAttributeForAttributeName(attributeName);
		if (node == null)
			throw new BayesianNetworkException("no attribute "+attributeName+" defined");
		
		return getPosteriors(node);		
		
		
	}

	@Override
	public Object getValueOf(String attributeName, int index) {
		return getBNAttributeForAttributeName(attributeName).getDomain().get(index);
	}

	@Override
	public List<String> valuesOf(String attributeName) {
		return getBNAttributeForAttributeName(attributeName).getDomain();
	}

	@Override
	public void exportPropagationStatistics(PrintStream ps) {
		if (MONITOR)
			timeMonitor.printToStream(ps);
	}
}
