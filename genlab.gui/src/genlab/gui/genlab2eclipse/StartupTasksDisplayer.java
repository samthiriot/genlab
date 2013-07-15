package genlab.gui.genlab2eclipse;

import genlab.core.exec.ITask;
import genlab.core.exec.ITaskManagerListener;

public class StartupTasksDisplayer implements ITaskManagerListener {

	public final static StartupTasksDisplayer singleton = new StartupTasksDisplayer();
	
	protected StartupTasksDisplayer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void notifyTaskAdded(ITask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyTaskRemoved(ITask task) {
		// TODO Auto-generated method stub
		
	}

}
