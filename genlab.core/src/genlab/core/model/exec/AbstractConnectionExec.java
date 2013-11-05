package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IConnection;

/**
 * A connection instance in the execution world: links two algo executions.
 * In practice, the connection is in charge of storing and making the result accessible for the 
 * next algo.
 * 
 * This standard connection works to link two algos at the same level of aggregation: 
 * once algo A linekd to B finished, its value is readen by the connection, then the algo B is notified 
 * of the availability of the data.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractConnectionExec<TypeFrom extends IAlgoExecution, TypeTo extends IAlgoExecution> implements IConnectionExecution {

	public final IConnection c;
	public final TypeFrom from;
	public final TypeTo to;
	
	protected final IExecution exec;
	
	protected Object value = null;
	
	public AbstractConnectionExec(IConnection c, TypeFrom from, TypeTo to) {
		
		if (from == to)
			throw new ProgramException("inconsistant executable connection (short loop)");
		
		if (from == null || to == null)
			throw new ProgramException("inconsistant executable connection (null)");
		
		// store them
		this.c = c;
		this.from = from;
		this.to = to;
		
		exec = from.getExecution();
		
		// listen for the "from" exec
		from.getProgress().addListener(this);
		
	}
	
	
	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#getValue()
	 */
	@Override
	public final Object getValue() {
		return value;
	}

	
	public void reset() {
		this.value = null;
	}


	@Override
	public final IConnection getConnection() {
		return c;
	}

	@Override
	public final IAlgoExecution getFrom() {
		return from;
	}

	@Override
	public final IAlgoExecution getTo() {
		return to;
	}


}
