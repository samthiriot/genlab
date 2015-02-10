package genlab.gui.examples.contributors;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IFlowType;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;

/**
 * Detects and publishes the list of available examples. Loads them from extensions.
 * 
 * @author Samuel Thiriot
 *
 */
public final class ExistingExamples {

	private static final String EXTENSION_POINT_EXAMPLES_ID = "genlab.examples.extensions.example";

	public static final ExistingExamples SINGLETON = new ExistingExamples();
	
	private Map<String,IGenlabExample> id2example = new HashMap<String, IGenlabExample>();
	
	private ExistingExamples() {
		detectExamples();
	}
	
	protected void declareExample(String id, IGenlabExample example) {
		
		GLLogger.debugTech("declaring example "+id, getClass());
		IGenlabExample previous = id2example.put(id, example);
		
		if (previous != null) {
			GLLogger.warnTech("an example was declared twice with id "+id, getClass());
		}
	}
	
	/**
	 * Loads 
	 */
	protected void detectExamples() {

		GLLogger.debugTech("detecting available examples proposed by plugins...", getClass());
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    if (reg == null) {
	    	GLLogger.warnTech("no extension registry detected; no example can be detected", this.getClass());
	    	return;
	    }
	    
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_EXAMPLES_ID);
	    for (IConfigurationElement e : elements) {
	    	Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o == null) {
					GLLogger.errorTech("unable to load an example", getClass());
				} else if (o instanceof IGenlabExample) {
					declareExample(e.getAttribute("id"), (IGenlabExample)o);
				} else {
					GLLogger.warnTech("detected something which is not an example: "+o, getClass());
				}
			} catch (Throwable e1) {
				GLLogger.errorTech("error while detecting available examples: error with extension point "+e.getName(), getClass(), e1);
				e1.printStackTrace();
			}
			
		}
	    
		GLLogger.infoTech("detected "+id2example.size()+" examples proposed by plugins", getClass());

	}
	
	public Collection<IGenlabExample> getAvailableExamples() {
		return id2example.values();
	}
	

	public Collection<IGenlabExample> getAvailableExamplesOfDifficulty(GenlabExampleDifficulty difficulty) {
		Collection<IGenlabExample> res = new LinkedList<IGenlabExample>();
		for (IGenlabExample e: id2example.values()) {
			if (e.getDifficulty().equals(difficulty))
				res.add(e);
		}
		return res;
	}
	
	public Map<IFlowType<?>,Collection<IGenlabExample>> builtMappingOfFlowtypesToExamples() {
		
		Map<IFlowType<?>,Collection<IGenlabExample>> res = new HashMap<IFlowType<?>, Collection<IGenlabExample>>();
		
		for (IGenlabExample e: id2example.values()) {
			for (IFlowType<?> ft : e.getIllustratedFlowTypes()) {
				Collection<IGenlabExample> l = res.get(ft);
				if (l == null) {
					l = new LinkedList<IGenlabExample>();
					res.put(ft, l);
				}
				l.add(e);
			}
		}
		
		return res;
		
	}

	public Map<AlgoCategory,Collection<IGenlabExample>> builtMappingOfCategoriesToExamples() {
		
		Map<AlgoCategory,Collection<IGenlabExample>> res = new HashMap<AlgoCategory, Collection<IGenlabExample>>();
		
		for (IGenlabExample e: id2example.values()) {
			for (AlgoCategory ac : e.getIllustratedAlgoCategories()) {
				Collection<IGenlabExample> l = res.get(ac);
				if (l == null) {
					l = new LinkedList<IGenlabExample>();
					res.put(ac, l);
				}
				l.add(e);
			}
		}
		
		return res;
		
	}

}
