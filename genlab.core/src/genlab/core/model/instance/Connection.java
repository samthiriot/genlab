package genlab.core.model.instance;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.IFlowType;


public class Connection implements IConnection {
	
	protected final IInputOutputInstance from;
	protected final IInputOutputInstance to;
	
	protected final String id;
	
	public Connection(String id, IInputOutputInstance from, IInputOutputInstance to) {
		super();
		
		
		this.id = id; // TODO unique ? 
		
		this.from = from;
		this.to = to;
		
		
		// no loops
		if (from == to || from.getMeta() == to.getMeta())
			throw new WrongParametersException("can not create loops");
		
		// always compliant types
		if (!from.getMeta().getType().compliantWith(to.getMeta().getType()))
			throw new WrongParametersException("types not compliant: "+from.getMeta().getType()+" and "+to.getMeta().getType());
		
	}
	public Connection(IInputOutputInstance from, IInputOutputInstance to) {
		this(
				from.getId()+"_"+to.getId(),
				from,
				to
				);
	}

	@Override
	public IInputOutputInstance getFrom() {
		return from;
	}

	@Override
	public IInputOutputInstance getTo() {
		return to;
	}
	
	public IFlowType<?> getType() {
		// from and to have the same type 
		return from.getMeta().getType(); 
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
	public String toString() {
		return id;
	}

}
