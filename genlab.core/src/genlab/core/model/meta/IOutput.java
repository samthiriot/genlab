package genlab.core.model.meta;

public interface IOutput<JavaType> extends IInputOutput<JavaType> {

	/**
	 * Returns true if this output can export results in continuous mode.
	 */
	public void isContinuous();
	
}
