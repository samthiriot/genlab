package genlab.core.model.instance;

import java.util.Collection;

import genlab.core.model.IGenlabResource;
import genlab.core.model.meta.IInputOutput;
import genlab.core.parameters.IParameterConstraint;

public interface IInputOutputInstance extends IGenlabResource {

	public String getId();
	
	public IInputOutput<?> getMeta();
	
	public IAlgoInstance getAlgoInstance();
	
	public void addConnection(IConnection c);
	
	public void removeConnection(IConnection c);
	
	public Collection<IConnection> getConnections();
	
	/**
	 * Returns, if any, the constraints on these parameters
	 * @return
	 */
	public IParameterConstraint<?> getParametersConstraints();
	
	
}
