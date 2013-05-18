package genlab.core.algos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BasicAlgo implements IAlgo {

	protected final String name;
	protected final String description;
	
	protected final Set<IInputOutput> outputs = new HashSet<IInputOutput>();
	protected final Set<IInputOutput> inputs = new HashSet<IInputOutput>();
			
	public BasicAlgo(
			String name,
			String description
			) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<IInputOutput> getInputs() {
		return Collections.unmodifiableSet(inputs);
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		return Collections.unmodifiableSet(outputs);
	}

}
