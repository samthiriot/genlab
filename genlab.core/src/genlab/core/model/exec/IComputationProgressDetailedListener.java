package genlab.core.model.exec;

/**
 * Listens for detailed computation progress changes (like, the numerical progress).
 * So listeners will be called often.
 */
public interface IComputationProgressDetailedListener extends IComputationProgressSimpleListener {

	public void computationProgressChanged(IComputationProgress progress);
	
}
