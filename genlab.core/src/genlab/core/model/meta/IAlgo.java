package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.IGenlabResource;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.Set;

import org.osgi.framework.Bundle;

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
	
	/**
	 * Creates an instance into the workflow instance passed as parameter. 
	 * Note that caller still have to add the instance to this workflow.
	 * @param workflow
	 * @return
	 */
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow);
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow);


	/**
	 * computes the algo on these input types. 
	 * Note that not all the algos can run in this way ("static")
	 * without instance. So some algos could raise an NotImplementedException there.
	 * @param inputs
	 * @return
	 */
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance);

	/**
	 * Returns the category of the algo, in the form "category1/subcategory1/subsub" (various number of levels, 2 recommanded).
	 * Would be better to use the constants defined here.
	 * Constants have to be localized.
	 * @return
	 */
	public String getCategoryId();
	
	/**
	 * Returns the list of parameters (not inputs: parameters)
	 * @return
	 */
	public Collection<Parameter<?>> getParameters();
	
	public Parameter<?> getParameter(String id);

	public boolean hasParameter(String id);

	/**
	 * Returns a path to retrieve an icon / image for this algo
	 * or null if none. The path should be absolute, 
	 * so any plugin can find the file.
	 * @return
	 */
	public String getImagePath16X16();
	public String getImagePath32X32();
	public String getImagePath64X64();
	public String getImagePathBig();
	
	/**
	 * Returns the OSGI bundle which provides this algo. Usefull
	 * to load related infos like images. 
	 * @return
	 */
	public Bundle getBundle();
	
	public boolean canBeContainedInto(IAlgoInstance algoInstance);
	
}
