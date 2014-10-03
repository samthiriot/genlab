package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class AbstractFlowType<JavaType> implements IFlowType<JavaType> {

	public final String id;

	protected final String name;
	protected final String desc;
	
	public AbstractFlowType(String id, String name, String desc) {
		
		this.id = id;
		this.name = name;
		this.desc = desc;
		
		ExistingFlowTypes.registerType(this);
	}

	@Override
	public final String getShortName() {
		return name;
	}

	@Override
	public final String getDescription() {
		return desc;
	}

	@Override
	public final String getHtmlDescription() {
		return desc;
	}

	@Override
	public boolean equals(Object arg0) {

		// quick solution, the same object
		if (this == arg0)
			return true;
		
		// more complex solution (sorry)
		try {
			AbstractFlowType<?> other = (AbstractFlowType<?>)arg0;
			return other.getShortName().equals(this.getShortName());
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean compliantWith(IFlowType<?> other) {
		return id.equals(other.getId());
	}
	
}
