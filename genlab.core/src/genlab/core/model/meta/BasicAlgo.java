package genlab.core.model.meta;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BasicAlgo implements IAlgo {

	protected final String name;
	protected final String description;
	protected final String id;
	protected final String categoryId;
	protected final Set<IInputOutput> outputs = new HashSet<IInputOutput>();
	protected final Set<IInputOutput> inputs = new HashSet<IInputOutput>();
			
	public BasicAlgo(
			String name,
			String description,
			String categoryId
			) {
		
		this.id = getClass().getCanonicalName();
		this.name = name;
		this.description = description;
		this.categoryId = categoryId;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	@Override
	public final Set<IInputOutput> getInputs() {
		return Collections.unmodifiableSet(inputs);
	}

	@Override
	public final Set<IInputOutput> getOuputs() {
		return Collections.unmodifiableSet(outputs);
	}

	@Override
	public final String getId() {
		return id;
	}
	
	public final String getCategoryId() {
		return categoryId;
	}

	@Override
	public final IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new AlgoInstance(this, workflow);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
