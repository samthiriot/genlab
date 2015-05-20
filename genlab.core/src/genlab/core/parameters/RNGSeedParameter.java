package genlab.core.parameters;

public class RNGSeedParameter extends NumberParameter<Long> {

	public RNGSeedParameter(String id, String name, String desc) {
		super(
				id, 
				name, 
				desc, 
				null,
				new Long(0),
				Long.MAX_VALUE,
				1l
				);

	}
	
	@Override
	public Long parseFromString(String value) {
		if (value=="null")
			return null;
		return Long.parseLong(value);
	}

}
