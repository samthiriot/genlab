package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class SimpleGraphFlowType extends AbstractFlowType<IGenlabGraph> {

	public static SimpleGraphFlowType SINGLETON = new SimpleGraphFlowType();

	protected SimpleGraphFlowType() {
		super(
				"core.types.simplegraph",
				"Graph(simple)",
				"simple graph" // TODO
				);
	}

	@Override
	public IGenlabGraph decodeFrom(Object value) {
		try {
			return (IGenlabGraph)value;
		} catch (ClassCastException e) {
			throw new WrongParametersException("unable to cast IGenlabGraph from "+value);
		}
	
	}
	

}
