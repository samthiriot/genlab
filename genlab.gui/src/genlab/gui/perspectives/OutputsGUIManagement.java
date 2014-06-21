package genlab.gui.perspectives;

import genlab.core.exec.IExecution;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.algos.GraphicalConsoleExec;
import genlab.gui.views.IExecutionView;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * TODO also register the things not initialized (without exec !)
 * 
 * @author Samuel Thiriot
 */
public class OutputsGUIManagement implements IPartListener {

	/**
	 * Use this property to exchange between a view and an algo: the algo asks for GUI opening, 
	 * then transmits to the view a property with this ID as a key and a String as an id of algo;
	 * then the view will get this view using the methods of this class.
	 */
	public static final String PROPERTY_ALGOVIEW_EXEC = "algoview_id";

	public static final OutputsGUIManagement singleton = new OutputsGUIManagement();
	
	/**
	 * associates the execution with all its displays
	 */
	private HashMap<IExecution, Collection<IViewPart>> exec2guis = new HashMap<IExecution, Collection<IViewPart>>(50);
	private Map<IViewPart,IExecution> view2exec = new HashMap<IViewPart,IExecution>(100);

	/**
	 * Associates ids of these views with the corresponding instance.
	 * This enables views opened to search for their counterpart.
	 * 
	 */
	private final Object algoViewIdsLock = new Object();
	private Map<String,AbstractOpenViewAlgoExec> id2algoView = new HashMap<String, AbstractOpenViewAlgoExec>(100);
	private Map<AbstractOpenViewAlgoExec,String> algoView2id = new HashMap<AbstractOpenViewAlgoExec,String>(100);
	
	
	private LinkedList<IOutputGUIManagementListener> listeners = new LinkedList<IOutputGUIManagementListener>();
	
	public AbstractOpenViewAlgoExec getViewExecForId(String id) {
		
		GLLogger.debugTech("attempting to provide an execview for id: "+id, GraphicalConsoleExec.class);
		
		synchronized (algoViewIdsLock) {

			AbstractOpenViewAlgoExec res = id2algoView.get(id);
			return res;
		}		
	}
	
	public void addListener(IOutputGUIManagementListener l ) {
		synchronized (listeners) {
			if (!listeners.contains(l))
				listeners.add(l);
		}
	}
	
	private void dispatchChange() {
		synchronized (listeners) {
			for (IOutputGUIManagementListener l: listeners) {
				l.notifyOutputGUIchanged();
			}
		}
	}
	
	public void setViewExecForId(String id, AbstractOpenViewAlgoExec viewExec) {
		synchronized (algoViewIdsLock) {
	
			id2algoView.put(id, viewExec);
			algoView2id.put(viewExec, id);
		}
	}
	
	protected Collection<IViewPart> getOrCreateGuisForExec(IExecution ex) {
		
		Collection<IViewPart> res = exec2guis.get(ex);
		
		if (res == null) {
			res = new LinkedList<IViewPart>();
			exec2guis.put(ex, res);
		}
		
		return res;
	}
	
	
	public void registerOutputGUI(IExecutionView view) {
	
		registerOutputGUI(view, view.getExecution());
				
	}
	
	public void registerOutputGUI(IViewPart view, IExecution execution) {
		
		GLLogger.traceTech("registered view: "+view+" with exec "+execution, getClass());
		
		if (execution == null) {
			GLLogger.warnTech("unable to mange this new view because its exec is null "+view, getClass());
			return;
		}
	
		synchronized (exec2guis) {

			Collection<IViewPart> l = getOrCreateGuisForExec(execution);
			if (!l.contains(view))
				l.add(view);
			view2exec.put(view, execution);
				
		}
		
		
		view.getSite().getPage().addPartListener(this);
		
		dispatchChange();
		
	}
	
	public void unregisterOutputGUI(IViewPart view, IExecution exec) {
		GLLogger.traceTech("unregistering view: "+view, getClass());

		synchronized (exec2guis) {

			Collection<IViewPart> l = getOrCreateGuisForExec(exec);
			l.remove(view);
			if (l.isEmpty())
				exec2guis.remove(exec);
			view2exec.remove(view);
		}
		
		synchronized (algoViewIdsLock) {
			String viewId = algoView2id.get(view);
			id2algoView.remove(viewId);
			algoView2id.remove(view);
			
		}
		
		dispatchChange();

	}

	public void unregisterOutputGUI(IExecutionView view) {
		
		unregisterOutputGUI(view, view.getExecution());
		
	}
	
	public void closeAllOutputs() {
	
		GLLogger.traceTech("attempting to close all the views", getClass());
		
		Map<IExecution, Collection<IViewPart>> copy = null;
		synchronized (exec2guis) {

			copy = (Map<IExecution, Collection<IViewPart>>)exec2guis.clone();
		}
		
		for (IExecution execution: copy.keySet()) {
			for (IViewPart view : new LinkedList<IViewPart>(copy.get(execution))) {
				GLLogger.traceTech("attempting to close the view "+view, getClass());
				view.getViewSite().getPage().hideView(view);
			}
		}
		
	}
	
	public void closeAllOutputsForExecution(IExecution exec) {
		
		GLLogger.traceTech("attempting to close the views related to execution "+exec, getClass());
		Collection<IViewPart> toClose = null;
		synchronized (exec2guis) {
			toClose = new LinkedList<IViewPart>(exec2guis.get(exec));
		}
		
		if (!exec2guis.containsKey(exec))
			return;
		
		for (IViewPart view : toClose) {
			GLLogger.traceTech("attempting to close the view "+view, getClass());
			view.getViewSite().getPage().hideView(view);
		}
	}
	
	public void closeOutput(IViewPart view) {
		
		GLLogger.traceTech("attempting to close the view "+view, getClass());
		view.getViewSite().getPage().hideView(view);

	}
	
	public void showOutput(IViewPart view) {
		view.getViewSite().getPage().bringToTop(view);
	}

	public Map<IExecution, Collection<IViewPart>> getAllWindows() {
		
		synchronized (exec2guis) {

			return (Map<IExecution, Collection<IViewPart>>)exec2guis.clone();
		}

	}
	private OutputsGUIManagement() {
		
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		
		IExecution exec = view2exec.get(part);
		if (exec == null)
			return;
		unregisterOutputGUI((IViewPart) part, exec);
	}
	

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
	}

	

}
