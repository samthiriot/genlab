package genlab.core.algos;

import genlab.core.IGenlabResource;

import java.util.Map;
import java.util.Set;

/**
 * An algo is a basic processing entity that works this way:
 * can be plugged to inputs, can process these inputs to provide outputs.
 * This class refers to an algo available, not an algo instanciated into 
 * a workflow.
 * 
 * TODO add group to sort things.
 * 
 * @author Samuel Thiriot
 */
public interface IAlgo extends IGenlabResource {

			
	public String getName();
	
	public String getDescription();
	
	
	public Set<IInputOutput> getInputs();
	
	public Set<IInputOutput> getOuputs();
	
	public IAlgoInstance createInstance(IGenlabWorkflow workflow);


	/**
	 * computes the algo on these input types. 
	 * Note that not all the algo can run in this way ("static")
	 * without instance. So some algos could raise an NotImplementedException there.
	 * @param inputs
	 * @return
	 */
	public IAlgoExecution createExec(AlgoInstance algoInstance, Map<IInputOutput, Object> inputs);

	/**
	 * Returns the category of the algo, in the form "category1/subcategory1/subsub" (various number of levels, 2 recommanded).
	 * Would be better to use the constants defined here.
	 * Constants have to be localized.
	 * @return
	 */
	public String getCategoryId();
}
