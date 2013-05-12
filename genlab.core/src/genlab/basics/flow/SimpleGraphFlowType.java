package genlab.basics.flow;

import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.WrongParametersException;
import genlab.core.flow.IFlowType;

public class SimpleGraphFlowType implements IFlowType<IGenlabGraph> {

	public SimpleGraphFlowType() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getShortName() {
		return "Graph(simple)";
	}

	@Override
	public String getDescription() {
		return "simple graph";
	}

	@Override
	public String getHtmlDescription() {
		return getDescription();
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
