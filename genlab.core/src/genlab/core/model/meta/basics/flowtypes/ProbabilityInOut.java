package genlab.core.model.meta.basics.flowtypes;

public class ProbabilityInOut extends DoubleInOut {

	public ProbabilityInOut(String id, String name, String desc) {
		super(id, name, desc);
		this.min = 0.0;
		this.max = 1.0;
	}

	public ProbabilityInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(id, name, desc, acceptMultipleInputs);
		this.min = 0.0;
		this.max = 1.0;
	}

	public ProbabilityInOut(String id, String name, String desc,
			Double defaultValue) {
		super(id, name, desc, defaultValue);
		this.min = 0.0;
		this.max = 1.0;
	}

	public ProbabilityInOut(String id, String name, String desc,
			Double defaultValue, boolean acceptMultipleInputs) {
		super(id, name, desc, defaultValue, acceptMultipleInputs);
		this.min = 0.0;
		this.max = 1.0;
	}

}
