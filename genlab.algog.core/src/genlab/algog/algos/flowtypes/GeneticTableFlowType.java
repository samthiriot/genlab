package genlab.algog.algos.flowtypes;

import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.flowtypes.TableFlowType;

/**
 * A table exactly similar to a standart table, 
 * but with metadata related to a genetic algorithm
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticTableFlowType extends TableFlowType {

	public GeneticTableFlowType() {
		super(
				"algog.types.table",
				"a table of values from algog",
				"stores values in a table with metadata"
				);
	}

	

	@Override
	public boolean compliantWith(IFlowType<?> other) {
		return other.getId().equals(id) 
				|| other.getId().equals(TableFlowType.SINGLETON.id);
	}
	
	

}
