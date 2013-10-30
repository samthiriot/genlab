package genlab.core.model.exec;

import java.util.Set;


public interface IDumpAsExecutionNetwork {

	/**
	 * Collects the entities 
	 * @param execs
	 * @param connections
	 */
	public void collectEntities(Set<IAlgoExecution> execs, Set<ConnectionExec> connections);
	
}
