package genlab.gui.views;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IRunner;
import genlab.core.exec.ITask;
import genlab.core.exec.ITaskLifecycleListener;
import genlab.core.exec.ITaskManagerListener;
import genlab.core.exec.TasksManager;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.UserMachineInteractionUtils;
import genlab.gui.actions.ClearProgressAction;
import genlab.quality.TestResponsivity;
import genlab.quality.Timers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Optimizing:
 * <ul> 
 * <li>no actual execution when no task is running</li> 
 * <li>no update on tasks which are not running; </li>
 * <li>do not display progress bar when the item is not visible;</li>
 * </ul> 
 * 
 * TODO optimisation: réduire la fréquence d'appel si on n'a plus rien en cours de progres
 * TODO add a context menu
 * TODO add a menu in the view (TCP stuff)
 * 
 * @author Samuel Thiriot
 *
 */
public final class TasksProgressView 
					extends ViewPart 
					implements ITaskManagerListener, ITaskLifecycleListener
					{

	private static final String SWT_THREAD_USER_ID = TasksProgressView.class.getCanonicalName()+":refresh";

	public static final int MAX_DEPTH_FOR_CREATING_WIDGET = 5;
	public static final int MAX_DEPTH_FOR_ANALYZING_CHILDREN = 3;
	public static final int MAX_DEPTH_FOR_DISPLAYING_PROGRESS = 3;
	
	public final static long REFRESH_PERIOD = 200;

	private Display display = null;
	private Tree treeWidget = null;
	
	public final static String VIEW_ID = "genlab.gui.views.ProgressView";
	
	public static final boolean DEBUG_DURATIONS = false;
	public static final String DEBUG_KEY_UPDATE_DATA = "ViewAlgogRadarTable:update data";

	/**
	 * External events submit tasks which require attention.
	 * They will be taken into account on refresh.
	 */
	private Set<ITask> _program_tasksToUpdate = new HashSet<ITask>(1000);
	

	/**
	 * Associates each task with its counterpart in the table/tree
	 */
	private Map<ITask,TreeItem> _ui_task2item = new HashMap<ITask,TreeItem>(1000);
	
	/**
	 * The set of tasks which are not only displayed, but also require update.
	 */
	private Set<ITask> _ui_tasksDisplayedNotStopped = new HashSet<ITask>(1000);

	/**
	 * Contains the tasks which require an update during the next update cycle.
	 * Typically, it contains these tasks for tasks detected through event (like: a novel task was added).
	 */
	private Set<ITask> _ui_tasksToUpdate = new HashSet<ITask>(1000);
	
	/**
	 * Contains the task to remove
	 */
	private Set<ITask> _ui_tasksToRemove = new HashSet<ITask>(1000);

	
	private Map<ITask, ProgressBar> _ui_task2progress = new HashMap<ITask, ProgressBar>(1000);
	private Map<ITask, TreeEditor> _ui_task2editor = new HashMap<ITask, TreeEditor>(1000);

	/**
	 * A temporary variable used only inside {@link #_ui_updateWidgets()}, but put here to avoid
	 * the frequent creation of a set.
	 */
	private Set<ITask> tasksUpdating = new HashSet<ITask>(1000);

	protected Rectangle treeBounds = new Rectangle(0, 0, 0, 0);

	private Map<IAlgo,Image> _ui_algo2image = new HashMap<IAlgo, Image>(100);

	
	/**
	 * Refresh the progress view
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	private class ProgressThread extends Thread {

		private final TasksProgressView view;
		private boolean canceled = false;
		
		/**
		 * True means an update was already submitted to the SWT thread. 
		 * In this case, it is obviously useless to submit it again :-)
		 */
		private boolean updatePending = false;
		
		
		public ProgressThread(TasksProgressView view) {
			this.view = view;
			
			// configure thread
			setName("refresh tasks view");
			setDaemon(true);
			//setPriority(NORM_PRIORITY);
			setPriority(Thread.MIN_PRIORITY);
			
			GLLogger.traceTech("created.", getClass());
		}
		
		@Override
		public void run() {
			
			/**
			 * The runnable used to actually refresh the widgets
			 */
			final Runnable updateRunnable = new Runnable() {
				
				@Override
				public void run() {
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserStartsRunnable(SWT_THREAD_USER_ID);
	
					view._ui_updateWidgets();
					updatePending = false;
					
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserEndsRunnable(SWT_THREAD_USER_ID);
	
				}
			};
			
			while (!canceled && (display == null || !display.isDisposed())) {
				
				if (!updatePending && view.hasSomethingToUpdate()) {
					//GLLogger.traceTech("refresh !.", getClass());
					updatePending = true;
					if (TestResponsivity.AUDIT_SWT_THREAD_USE) 
						TestResponsivity.singleton.notifySWTThreadUserSubmitsRunnable(SWT_THREAD_USER_ID);
					view.getDisplay().asyncExec(updateRunnable);
				}

				try {
					//GLLogger.traceTech("sleeping", getClass());
					Thread.sleep(REFRESH_PERIOD);
				} catch (InterruptedException e) {
				}
			}
		}

		public void cancel() {
			canceled = true;
		}
		
	}
	
	private ProgressThread thread = null;
	
	public TasksProgressView() {

		// listen !
		TasksManager.singleton.addListener(this);
		

	}
	
	@Override
	public void dispose() {
		
		
		thread.cancel();
		
		TasksManager.singleton.removeListener(this);
		
		
		// dispose the images
		for (Image img : _ui_algo2image.values()) {
			try {
				img.dispose();
			} catch (RuntimeException e) {
			}		
		}
		_ui_algo2image.clear();
		
		// dispose task widgets
		for (TreeEditor editor: _ui_task2editor.values()) {
			try {
				editor.dispose();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		for (ProgressBar bar: _ui_task2progress.values()) {
			try {
				bar.dispose();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		for (TreeItem item: _ui_task2item.values()) {
			try {
				item.dispose();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		// dispose the widgets
		treeWidget.dispose();
		
		
		super.dispose();
	}

	public Display getDisplay() {
		return display;
	}
	
	/**
	 * The widget displays tasks based on events: it listens the manager and 
	 * discovers new tasks thanks to events. But at the original creation,
	 * it has to discover the tasks which may already be runnning.
	 */
	protected void loadTasksAlreadyRunning() {
		for (IRunner runner: TasksManager.singleton.getRunners()) {
			for (IAlgoExecution exec: runner.getAllTasks()) {
				addTaskToManage(exec);		
			}
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
				
		display = parent.getDisplay();
		
		treeWidget = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		treeWidget.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(treeWidget, SWT.LEFT);
		columnName.setText("name");
		columnName.setWidth(200);
		TreeColumn columnState = new TreeColumn(treeWidget, SWT.LEFT);
		columnState.setText("state");
		columnState.setWidth(200);
		
		thread = new ProgressThread(this);
		thread.start();
		
		getViewSite().getActionBars().getToolBarManager().add(new ClearProgressAction());  

		// create a contextual menu
	    final Menu menu = new Menu(treeWidget);
	    treeWidget.setMenu(menu);
	    final MenuItem cancelItem = new MenuItem(menu, SWT.NONE);
	    cancelItem.setText("cancel");
	    cancelItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (treeWidget.getSelection().length == 0)
					return;
				
				for (TreeItem item : treeWidget.getSelection()) {

		        	if (item == null)
		        		return;
		        	
		        	ITask currentTask  = (ITask)item.getData();

		        	if (currentTask == null)
		        		return;
		        	
		        	currentTask.cancel();
	
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		menu.addMenuListener(new MenuListener() {
			
			protected boolean isCancelEnabled() {

				if (treeWidget.getSelection().length == 0)
					return false;
				
				// "cancel" is enabled when the computation state is not finished
				// (for at least one item in the selection)
				for (TreeItem item : treeWidget.getSelection()) {
		        	ITask currentTask  = (ITask)item.getData();
		        	if (!currentTask.getProgress().getComputationState().isFinished()) {
		        		return true;
		        	}
				}
				
				return false;
			}
			
			@Override
			public void menuShown(MenuEvent e) {
				
				cancelItem.setEnabled(isCancelEnabled());
				
			}
			
			@Override
			public void menuHidden(MenuEvent e) {
				
			}
		});

		// discover tasks which are already running
		loadTasksAlreadyRunning();
	}

	@Override
	public void setFocus() {
		treeWidget.setFocus();
	}
	

	// TODO find position based on ranking ?
	private int findPositionForTask(ITask t) {
		
		if (_ui_task2item.isEmpty())
			return 0;
		
		GLLogger.debugTech("displaying task "+t+" "+t.getProgress().getTimestampCreation(), getClass());

		if (t.getPrerequires().isEmpty())
			return 0;	// quick solution
		
		LinkedList<ITask> dependenciesNotSatisfied = new LinkedList<ITask>(t.getPrerequires());
	
		TreeItem[] items = null;
		if (t.getParent() == null)
			items = treeWidget.getItems();
		else 
			items = _ui_task2item.get(t.getParent()).getItems();
		
		if (items.length == 0)
			return 0; // quick solution
		
		int i = 0;
		for (i=0; i<items.length; i++) {
			ITask currentTask  = (ITask)items[i].getData();
			dependenciesNotSatisfied.remove(currentTask);
			if (dependenciesNotSatisfied.isEmpty()) {
				//GLLogger.debugTech("displaying task "+t+" at index "+i+" among "+Arrays.toString(items), getClass());
				return i+1;
			}
		}
		
		// TODO not working now
		//GLLogger.debugTech("displaying task "+t+" at index "+(i-1)+" among "+Arrays.toString(items), getClass());
		
		return Math.max(0, i-1);
			
	}
	
	
	/**
	 * returns the TreeItem corresponding to this task, or null in case of problem.
	 * @param t
	 * @return
	 */
	private TreeItem getOrCreateItemForTask(ITask t) {
		
		if (t == null) {
			GLLogger.warnTech("asked to display a null item...", getClass());
			return null;
		}
		
		TreeItem item = _ui_task2item.get(t);
	
		if (item == null) {
			
			// TODO task
			// TODO System.err.println("display task "+t+", rank "+t.getRank());
			
			// create the item !
			final IContainerTask parent = t.getParent(); 
			
			// create item
			try {
					
				if (parent == null) {
					item = new TreeItem(
							treeWidget, 
							SWT.NONE, 
							findPositionForTask(t)
							);
				} else {
					final TreeItem parentItem = getOrCreateItemForTask(parent);
					if (parentItem == null) {
						GLLogger.warnTech("unable to construct the parent item for task "+t+"; let's ignore it now", getClass());
						return null;
					}
					item = new TreeItem(
							parentItem,
							SWT.NONE, 
							findPositionForTask(t)
							);
				}
			} catch (IllegalArgumentException e) {
				GLLogger.warnTech("illegal argument when creating a tree item for task "+t, getClass(), e);
				return null;
			}
			item.setText(0, t.getName());
			item.setData(t);
			
			_ui_task2item.put(t, item);	
			_ui_tasksDisplayedNotStopped.add(t);
			
			// configure the item
			
			// ... its image...
			if (t instanceof IAlgoExecution) {
				IAlgoExecution ae = (IAlgoExecution)t;
				
				try {
					IAlgo algo = ae.getAlgoInstance().getAlgo();
					String imagePath = algo.getImagePath16X16();
					Image img = _ui_algo2image.get(algo);
					if (imagePath != null && img == null) {
					
						try {
							// TODO avoid to reload this image each time
							img = new Image(display, algo.getClass().getClassLoader().getResourceAsStream(imagePath));
							_ui_algo2image.put(algo, img);
						} catch (RuntimeException e) {
							GLLogger.warnTech("unable to find image "+imagePath, getClass());
						}
					}
					if (img != null) {
						item.setImage(img);
					}
				} catch (NullPointerException e) {
					GLLogger.warnTech("unable to find an image to display a task", getClass());
				}
				
			}
			
			t.addLifecycleListener(this);
			
			// ... its expanded state
			item.setExpanded(t instanceof IContainerTask);

			
		}
		
		
		
		return item;
		
	}
	
	/**
	 * to be called from the swt thread.
	 * @param t
	 */
	private void _ui_updateWidget(ITask t) {
		
		if (DEBUG_DURATIONS)
			Timers.SINGLETON.startTask("update progress for task "+t.getName());
		
		//GLLogger.debugTech("updating for task "+t, getClass());
		
		final int taskDepth = t.getDepth(); 
		if (taskDepth > MAX_DEPTH_FOR_CREATING_WIDGET)
			return;
		
		// create (or get) it ?
		TreeItem item = getOrCreateItemForTask(t);
		
		if (item == null)
			return;
		
		if (item.isDisposed()) {
			// remove the task ! Why is it still there in the first place ?
			_ui_tasksToRemove.add(t);
			_ui_tasksToUpdate.remove(t);
			return;
		}
		
		if (t.getProgress() == null || t.getProgress().getComputationState() == null) {
			// no progress... ?
			// probably means the task was cleaned. 
			System.err.println("NO PROGRESS for task "+t);
			_ui_tasksToUpdate.remove(t);
			_ui_tasksToRemove.add(t);
			return;
		}
		
		
		final boolean itemIsVisible = isItemVisible(item);
				

		// also create its children ? 
		if (
				(taskDepth <= MAX_DEPTH_FOR_ANALYZING_CHILDREN)
				&&
				(itemIsVisible) && (t instanceof IContainerTask)) {
			IContainerTask cont = (IContainerTask)t;
			
			for (ITask sub : cont.getTasks()) {
				getOrCreateItemForTask(sub);
			}
		}
		// update its state ? 
		
		final ComputationState state = t.getProgress().getComputationState();
		
		String txt = null;
		switch (state) {
		case FINISHED_OK: {
			StringBuffer sb = new StringBuffer();
			sb.append("finished ");
			if (t.getProgress() != null)
				sb.append("(").append(UserMachineInteractionUtils.getHumanReadableTimeRepresentation(t.getProgress().getDurationMs())).append(")");
			txt = sb.toString();
			_ui_tasksDisplayedNotStopped.remove(t);
		} break;
		case FINISHED_CANCEL:
			txt = "cancelled";
			_ui_tasksDisplayedNotStopped.remove(t);
			break;
		case FINISHED_FAILURE:
			txt = "failed";
			_ui_tasksDisplayedNotStopped.remove(t);
			break;
		case SENDING_CONTINOUS:
		case STARTED: { 
			StringBuffer sb = new StringBuffer();
			sb.append("running");
			try {
				sb.append(" (").append(t.getProgress().getProgressDone()).append("/").append(t.getProgress().getProgressTotalToDo()).append(")");
			} catch (RuntimeException e) {
				// not a pb;
			}
			txt = sb.toString();
		} break;
		case READY: {
			txt = "ready";
		} break;
		case WAITING_DEPENDENCY: {
			txt = "pending";
		} break;
		default:
			txt  = state.toString();
			break;
		}
		if (txt == null)
			return; // was cleaned during this process
		
		item.setText(1, txt);
		
		// progress bar :-)
		if (taskDepth <= MAX_DEPTH_FOR_DISPLAYING_PROGRESS) {
			ProgressBar pb = _ui_task2progress.get(t);
			if (state == ComputationState.STARTED) {
				if (itemIsVisible) {
					if (pb == null) {
						// create the progress bar !
						TreeEditor editor = new TreeEditor(treeWidget);
						pb = new ProgressBar(treeWidget, SWT.SMOOTH);
						editor.grabHorizontal = true;
						editor.setEditor(pb, item, 1);
						_ui_task2progress.put(t, pb);
						_ui_task2editor.put(t, editor);
						pb.setState(SWT.NORMAL);
					}
					pb.setVisible(true);
					try {
						final Double progressPercent = t.getProgress().getProgressPercent();
						if (progressPercent != null)
							pb.setSelection((int)Math.floor(progressPercent));
					} catch (NullPointerException e) {
						// ignore it
						// TODO remove ? clear(t);
						GLLogger.warnTech("null on the progress of task "+t, getClass(), e);
					}
				} else if (pb != null) {
					pb.setVisible(false);
				}
			} else if (pb != null) {
				// should remove this progress bar !
				pb.dispose();
				_ui_task2editor.remove(t).dispose();
				_ui_task2progress.remove(t);
			}
			
		}

		if (DEBUG_DURATIONS)
			Timers.SINGLETON.endTask("update progress for task "+t.getName(), 10);
		
	}
	
	protected boolean hasSomethingToUpdate() {
		
		if (!_ui_tasksDisplayedNotStopped.isEmpty())
			return true;
		
		if (!_ui_tasksToUpdate.isEmpty())
			return true;
		
		if (!_ui_tasksToRemove.isEmpty())
			return true;
		
		synchronized (_program_tasksToUpdate) {
			if (!_program_tasksToUpdate.isEmpty())
				return true;
			
		}
		
		return false;
	}
	
	
	/**
	 * Inits the internal state to compute widgets visibility.
	 * Mainly stores the bounds of the tree, assuming it will not change during
	 * our iteration.
	 */
	protected void initItemsVisibility() {

		// update tree bounds (maybe the widget was received ?)
		final Point treeSize = treeWidget.getSize();
		treeBounds.width = treeSize.x;
		treeBounds.height = treeSize.y;
		
		
	}
	
	protected boolean isItemVisible(TreeItem item) {
		
		// first of all, an item is only visible if its parent is expanded.
		TreeItem parent = item.getParentItem();
		while (parent != null) {
			if (!parent.getExpanded()) 
				return false;
			parent = parent.getParentItem();
		}
		
		// also, it depends on the widget's bounds
		return treeBounds.intersects(item.getBounds());
		
	}
	
	/**
	 * SHould be called from the SWT thread.
	 * Entry point to display all the tasks !
	 */
	protected void _ui_updateWidgets() {
				
		//long timeStart = System.currentTimeMillis();
		
		if (treeWidget == null || treeWidget.isDisposed()) {
			thread.cancel();
			return;
		}
		
		// transfer elements from program events to ui events
		synchronized (_program_tasksToUpdate) {
			_ui_tasksToUpdate.addAll(_program_tasksToUpdate);
			_program_tasksToUpdate.clear();
		}		
		
		treeWidget.setRedraw(false);
		
		// first remove the tasks to remove
		_ui_processRemoveTasks();
		
		// first copy the collection of tasks updates to avoid concurrent modifications
		tasksUpdating.addAll(_ui_tasksDisplayedNotStopped);		// don't update the tasks which are displayed, but no more running
		tasksUpdating.addAll(_ui_tasksToUpdate);
		_ui_tasksToUpdate.clear();
						
		// now update each task / widget
		initItemsVisibility();
		
		
		for (ITask t : tasksUpdating) {
			try {
				_ui_updateWidget(t);
			} catch (RuntimeException e) {
				// log ? 
				GLLogger.warnTech("catched an error while updating a progress: "+e.getMessage()+" for task "+t, getClass(), e);
				e.printStackTrace();
				/*synchronized (tasksToUpdate) {
					tasksToUpdate.remove(t);
				}*/
			}
			//if (System.currentTimeMillis() - timeStart > 500) {
				
			//}
		}

		// clear the list
		tasksUpdating.clear();
		
		treeWidget.setRedraw(true);
		treeWidget.redraw();
		//System.err.println("update tasks took: "+(System.currentTimeMillis() - timeStart)+" ms");
		
		
	}

	private void addTaskToManage(ITask task) {
		
		//GLLogger.debugTech("a task was added: "+task, getClass());
		synchronized (_program_tasksToUpdate) {
			_program_tasksToUpdate.add(task);	
			
			/* TODO je pense que c'est inutile
			 if (task instanceof IContainerTask) {
			 	IContainerTask ct = (IContainerTask)task;
				for (ITask subTask : ct.getTasks()) {
					_program_tasksToUpdate.add(subTask);
				}
			}
			*/
		}
		//thread.interrupt();
				
	}

	@Override
	public void notifyTaskAdded(ITask task) {
		
		addTaskToManage(task);
		//task.getProgress().addListener(this);
		
	}

	@Override
	public void notifyTaskRemoved(ITask task) {
		
		//GLLogger.debugTech("a task was removed: "+task, getClass());
		
		//task.getProgress().removeListener(this);
		
		// TODO !!! task2item.remove(task);
	}


/*
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		notifyTaskAdded(progress.getAlgoExecution());
	}
	*/
	
	/**
	 * Actual clearing of the task (removes the widgets, etc.)
	 * @param task
	 */
	protected void _ui_clear(ITask task) {
		
		// TODO ??? System.err.println("WARN clearing item for task "+task);
		TreeEditor editor = _ui_task2editor.remove(task);
		if (editor != null)
			editor.dispose();
		
		ProgressBar bar = _ui_task2progress.remove(task);
		if (bar != null)
			bar.dispose();
	
		TreeItem item = _ui_task2item.remove(task);
		if (item != null)
			item.dispose();
		
		_ui_tasksDisplayedNotStopped.remove(task);
		
		_ui_tasksToUpdate.remove(task);
	
		
	}
	
	/**
	 * Called from the program. 
	 * Will clear all the finished tasks.
	 * In practice, takes each finished task, and adds it to the list 
	 * of the tasks which should be removed
	 */
	public void clearFinished() {
		
		Set<ITask> toRemove = new HashSet<ITask>(_ui_task2item.size());
		
		Set<ITask> existingTasks = new HashSet<ITask>(_ui_task2item.keySet());
		
		// detect parent tasks to remove
		for (ITask task: existingTasks) {
			
			// do not clean
			if (
					// subtasks
					(task.getParent() != null 
					|| 
					// or running or queued tasks
					(task.getProgress() != null && task.getProgress().getComputationState() != null && !task.getProgress().getComputationState().isFinished())))
				continue;
			
			toRemove.add(task);
		}
		
		// detect children to remove
		for (ITask task: existingTasks) {
			
			// do not clean
			if (
					// subtasks
					(task.getParent() == null 
					|| 
					// or running or queued tasks
					!task.getProgress().getComputationState().isFinished()))
				continue;
			
			if (toRemove.contains(task.getTopParent()))
				toRemove.add(task);
			
		}
		
	
		// now remove all those listen
		synchronized (_program_tasksToUpdate) {
			_program_tasksToUpdate.addAll(toRemove);
		}
		
	}
	
	/**
	 * Called from the SWT thread to clear the items of removed tasks
	 */
	protected void _ui_processRemoveTasks() {
				
		for (ITask task: _ui_tasksToRemove) {
	
			_ui_clear(task);
			
		}
		
		_ui_tasksToRemove.clear();

	}
	
	@Override
	public void taskCleaning(ITask task) {

		// TODO do we care ??? 
		// _program_tasksToRemove.add(task);
			
		
	}

}
