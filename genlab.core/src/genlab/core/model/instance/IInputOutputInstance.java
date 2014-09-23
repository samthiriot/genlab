package genlab.core.model.instance;

import genlab.core.model.IGenlabResource;
import genlab.core.model.meta.IInputOutput;
import genlab.core.parameters.IParameterConstraint;

import java.util.Collection;

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
	
	public boolean acceptsConnectionFrom(IInputOutputInstance from);
	public boolean acceptsConnectionTo(IInputOutputInstance to);

}
