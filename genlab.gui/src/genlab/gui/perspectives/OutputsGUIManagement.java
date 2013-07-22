package genlab.gui.perspectives;

import genlab.core.exec.IExecution;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.views.IExecutionView;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO also register the things not initialized (without exec !)
 * 
 * @author Samuel Thiriot
 */
public class OutputsGUIManagement {

	public static final OutputsGUIManagement singleton = new OutputsGUIManagement();
	
	protected Map<IExecution, Collection<IExecutionView>> exec2guis = new HashMap<IExecution, Collection<IExecutionView>>(50);
	
	protected Collection<IExecutionView> getOrCreateGuisForExec(IExecution ex) {
		
		Collection<IExecutionView> res = exec2guis.get(ex);
		
		if (res == null) {
			res = new LinkedList<IExecutionView>();
			exec2guis.put(ex, res);
		}
		
		return res;
	}
	
	public void registerOutputGUI(IExecutionView view) {
	
		GLLogger.traceTech("registered view: "+view, getClass());
		
		if (view.getExecution() == null) {
			GLLogger.warnTech("unable to mange this new view because its exec is null "+view, getClass());
			return;
		}
			
		Collection<IExecutionView> l = getOrCreateGuisForExec(view.getExecution());
		l.add(view);
		
	}
	
	public void closeAllOutputs() {
		
		GLLogger.debugTech("attempting to close all the views", getClass());
		for (IExecution execution: exec2guis.keySet()) {
			for (IExecutionView view : exec2guis.get(execution)) {
				GLLogger.traceTech("attempting to close the view "+view, getClass());
				view.getViewSite().getPage().hideView(view);
			}
		}
	}
	
	private OutputsGUIManagement() {
		
	}

}
