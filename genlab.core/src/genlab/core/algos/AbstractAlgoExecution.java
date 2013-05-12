package genlab.core.algos;

/**
 * Minimal algo execution: able to retrieve the algo, computation progress.
 * 
 * @author Samuel THiriot
 *
 * @param 
 */
public abstract class AbstractAlgoExecution implements IAlgoExecution {

	protected final IAlgoInstance algoInst;
	protected final IComputationProgress progress;
	private IComputationResult result = null;
	
	public AbstractAlgoExecution(IAlgoInstance algoInst, IComputationProgress progress) {
		this.algoInst = algoInst;
		this.progress = progress;
	}	

	@Override
	public final IComputationProgress getProgress() {
		return progress;
	}

	@Override
	public final IComputationResult getResult() {
		return result;
	}
	
	protected void setResult(IComputationResult res) {
		this.result = res;
	}
	

	@Override
	public IAlgoInstance getAlgoInstance() {
		return algoInst;
	}

}
