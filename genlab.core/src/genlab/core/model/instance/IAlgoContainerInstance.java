package genlab.core.model.instance;

import java.util.Collection;
import java.util.List;

/**
 * Represents an algo which contains other algos
 * 
 * @author Samuel Thiriot
 *
 */
public interface IAlgoContainerInstance extends IAlgoInstance {

	/**
	 * The children in this algo container
	 * @return
	 */
	public Collection<IAlgoInstance> getChildren();
	
	public void addChildren(IAlgoInstance child);
	
	public void removeChildren(IAlgoInstance child);

	public Collection<IAlgoInstance> getAlgoInstancesDependingToOurChildren();

	public Collection<IConnection> getConnectionsComingFromOutside();
	
	public Collection<IConnection> getConnectionsGoingToOutside();

	/**
	 * accumulate in the list passed as parameter the algorithm in hierarchy order: 
	 * parents first, children of parents last (recusrive)
	 * @param accumulator
	 */
	public void collectChildrenInOrder(List<IAlgoInstance> accumulator);

	/**
	 * 
	 * @param bo
	 * @return
	 */
	public boolean canContain(IAlgoInstance bo);
	
	
}
