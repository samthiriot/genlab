package genlab.core.model.instance;

import genlab.core.model.IGenlabResource;

public interface IConnection extends IGenlabResource {

	public IInputOutputInstance getFrom();
	
	public IInputOutputInstance getTo();
	
	
}
