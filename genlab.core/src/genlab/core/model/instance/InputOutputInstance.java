package genlab.core.model.instance;

import genlab.core.model.meta.IInputOutput;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class InputOutputInstance implements IInputOutputInstance {

	protected final IInputOutput<?> meta;
	protected final IAlgoInstance algoInstance;
	protected final String id;
	
	protected final transient Set<IConnection> connections = new HashSet<IConnection>();
	
	public InputOutputInstance(IInputOutput<?> meta, IAlgoInstance algoInstance) {
		
		this.id = algoInstance.getId()+"."+meta.getId(); // TODO id ! 
		this.meta = meta;
		this.algoInstance = algoInstance;
		
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return id;
	}
	
	@Override
	public IInputOutput<?> getMeta() {
		return meta;
	}

	@Override
	public IAlgoInstance getAlgoInstance() {
		return algoInstance;
	}

	@Override
	public void addConnection(IConnection c) {
		connections.add(c);
	}

	@Override
	public void removeConnection(IConnection c) {
		connections.remove(c);		
	}

	@Override
	public Collection<IConnection> getConnections() {
		return Collections.unmodifiableCollection(connections);
	}

}
