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
public class GenomeFlowType extends AbstractFlowType<Genome> {

	public static final GenomeFlowType SINGLETON = new GenomeFlowType();
	
	private GenomeFlowType() {
		super(
				"genlab.genes.types.genome", 
				"genome", 
				"a genome, made to contain genes"
				);

	}

	@Override
	public Genome decodeFrom(Object value) {
		
		throw new NotImplementedException();
		
		// TODO Auto-generated method stub
		//return null;
	}
	

}
