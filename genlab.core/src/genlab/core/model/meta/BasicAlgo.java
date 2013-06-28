package genlab.core.model.meta;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public abstract class BasicAlgo implements IAlgo {

	protected final String name;
	protected final String description;
	protected final String id;
	protected final String categoryId;
	protected final Set<IInputOutput> outputs = new LinkedHashSet<IInputOutput>();
	protected final Set<IInputOutput> inputs = new LinkedHashSet<IInputOutput>();
	protected final Map<String,Parameter<?>> parameters = new LinkedHashMap<String,Parameter<?>>();
	
	public BasicAlgo(
			String name,
			String description,
			String categoryId
			) {
		
		this.id = name.replaceAll("[-+.^:, ]","_");;
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
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new AlgoInstance(this, workflow, id);
	}

	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Collection<Parameter<?>> getParameters() {
		return parameters.values();
	}
	

	@Override
	public Parameter<?> getParameter(String id) {
		return parameters.get(id);
	}

	@Override
	public boolean hasParameter(String id) {
		return parameters.containsKey(id);
	}
	
	protected void registerParameter(Parameter<?> p) {
		this.parameters.put(p.getId(), p);
	}


}
