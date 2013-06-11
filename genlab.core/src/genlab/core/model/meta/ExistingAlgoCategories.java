package genlab.core.model.meta;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Lists existing categories, and exposes default (and recommanded) ones.
 * 
 * TODO add an extension point to add new ones ? 
 * 
 * @author Samuel Thiriot
 *
 */
public class ExistingAlgoCategories {

	private Map<String, AlgoCategory> id2algo = new HashMap<String, AlgoCategory>();
	
	private Collection<String> parentCategories = new LinkedList<String>();
	
	public static final AlgoCategory PARSER = new AlgoCategory(
			null, 
			"parser", 
			"read something from a filesystem", 
			"parsers"
			);
	
	public static final AlgoCategory PARSER_GRAPH = new AlgoCategory(
			PARSER, 
			"graphs", 
			"parse graphs from files", 
			"graphs"
			);
	
	public static final AlgoCategory WRITER = new AlgoCategory(
			null, 
			"writers", 
			"write something to the filesystem (one or more files)", 
			"writers"
			);
	
	public static final AlgoCategory WRITER_GRAPH = new AlgoCategory(
			WRITER, 
			"graphs", 
			"write graphs to files", 
			"graphs"
			);
	
	public static final AlgoCategory GENERATORS = new AlgoCategory(
			null, 
			"generators", 
			"generate things", 
			"generators" 
			);
	
	public static final AlgoCategory GENERATORS_GRAPHS = new AlgoCategory(
			GENERATORS, 
			"graphs", 
			"generate graphs", 
			"graphs" 
			);
	
	public static final AlgoCategory ANALYSIS = new AlgoCategory(
			null, 
			"analysis", 
			"analyse data", 
			"analysis"
			);
	
	public static final AlgoCategory ANALYSIS_GRAPH = new AlgoCategory(
			ANALYSIS, 
			"graphs", 
			"analyse graphs", 
			"graphs"
			);
	

	private ExistingAlgoCategories() {
		declareCategory(PARSER);
		declareCategory(PARSER_GRAPH);
		declareCategory(GENERATORS);
		declareCategory(GENERATORS_GRAPHS);
		declareCategory(WRITER);
		declareCategory(WRITER_GRAPH);
		declareCategory(ANALYSIS);
		declareCategory(ANALYSIS_GRAPH);
	}

	public AlgoCategory getCategoryForId(String id) {
		return id2algo.get(id);
	}
	
	public void declareCategory(AlgoCategory ac) {
		GLLogger.debugTech("added algo category: "+ac, ExistingAlgoCategories.class);
		id2algo.put(ac.getTotalId(), ac);
		if (ac.getParentCategory() == null && !parentCategories.contains(ac.getTotalId())) {
			GLLogger.debugTech("added parent algo category: "+ac, ExistingAlgoCategories.class);
			parentCategories.add(ac.getTotalId());
		}
	}
	
	public Collection<String> getParentCategories() {
		return Collections.unmodifiableCollection(parentCategories);
	}
	
	private final static ExistingAlgoCategories singleton = new ExistingAlgoCategories();
	
	public final static ExistingAlgoCategories getExistingAlgoCategories() {
		return singleton;
	}
	

}
