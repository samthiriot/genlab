package genlab.core.model.meta;

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
	
	
}
