package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;

public class HeterogeneousMapFlowType extends AbstractFlowType<IHeterogeneousMap> {

	public static HeterogeneousMapFlowType SINGLETON = new HeterogeneousMapFlowType();

	protected HeterogeneousMapFlowType() {
		super(
				"core.types.vectorHeterogeneous",
				"heterogeneous vector", 
				"an heterogeneous vector"
				);
	}
	
	@Override
	public IHeterogeneousMap decodeFrom(Object value) {
		if (value instanceof IHeterogeneousMap)
			try {
				return (IHeterogeneousMap)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to decode an IHeterogeneousMap from "+value);
			}
		else
			throw new WrongParametersException("unable to decode an heterogeneous map from "+value);
		
	}



}
