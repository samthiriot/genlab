package genlab.populations.flowtypes;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.flowtypes.AbstractFlowType;
import genlab.populations.bo.PopulationDescription;

public class PopulationDescriptionFlowType extends AbstractFlowType<PopulationDescription> {

	public static PopulationDescriptionFlowType SINGLETON = new PopulationDescriptionFlowType();

	private PopulationDescriptionFlowType() {
		super(
				"genlab.population.yang.flowtypes.popdesc", 
				"population_description", 
				"defines what can exist as individual types, attributes and relationships within a population"
				);
	}

	@Override
	public PopulationDescription decodeFrom(Object value) {
	
		try {
			return (PopulationDescription)value;
		} catch (ClassCastException e) {
			throw new WrongParametersException("unable to cast PopulationDescription from "+value);
		}
	}

}
