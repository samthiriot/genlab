package genlab.gui;

public abstract class SyncExecWithResult<ResultClass extends Object> implements Runnable {

	private ResultClass result = null;
	private boolean finished = false;
	
	public SyncExecWithResult() {

	}

	protected abstract ResultClass retrieveResult();
	
	@Override
	public void run() {

		result = retrieveResult();
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}
	
	public ResultClass getResult() {
		return result;
	}
	
}
