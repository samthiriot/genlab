package genlab.algog.types;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.flowtypes.AbstractFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

/**
 * A flowtype of datatypes which can be encoded as genes.
 * CUrrently, one can only encore floats and integers
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneFlowType extends AbstractFlowType<Object> {

	public static final GeneFlowType SINGLETON = new GeneFlowType();
	
	private GeneFlowType() {
		super(
				"genlab.genes.types.gene", 
				"gene", 
				"any acceptable gene type (integer, float)"
				);

	}

	@Override
	public Object decodeFrom(Object value) {
		
		throw new NotImplementedException();
		
		// TODO Auto-generated method stub
		//return null;
	}
	
	@Override
	public boolean compliantWith(IFlowType<?> other) {
		
		return (
				other.getId().equals(IntegerFlowType.SINGLETON.getId())
				||
				other.getId().equals(DoubleFlowType.SINGLETON.getId())
				);
		
	}

}
