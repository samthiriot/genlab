package genlab.core.model.meta;

import java.util.Map;

/**
 * Tags the algorithms that may container other algorithms (like loops for instance)
 * 
 * @author Samuel Thiriot
 *
 */
public interface IAlgoContainer extends IAlgo {

	/**
	 * returns true if this type of algo container can contain this kind of algo.
	 * The behaviour may be refined at the instanciation level.
	 * @param algo
	 * @return
	 */
	public boolean canContain(IAlgo algo);
	
	/**
	 * Lists algorithms that can be created in this container. USed for intuitive
	 * creation of algos. Associates algos with levels of relevance; the higher the better (Infinite would mean:  use it).
	 * By convention, system-wide constants have a priority between 0 to 99.
	 * If the algo is proposing something more relevant, its priority should be higher/eq than 100.
	 * @param inputInstance
	 * @return
	 */
	public Map<IAlgo,Integer> recommandAlgosContained();
	
	
	
}
