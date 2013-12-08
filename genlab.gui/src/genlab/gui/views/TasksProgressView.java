package genlab.gui.views;

import genlab.core.exec.IContainerTask;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MenuAdapter;
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
import org.eclipse.ui.internal.ide.dialogs.CleanDialog;
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
public class TasksProgressView 
					extends ViewPart 
					implements ITaskManagerListener, ITaskLifecycleListener
					{

	public final static long REFRESH_PERIOD = 100;

	private Display display = null;
	private Tree treeWidget = null;
	
	public final static String VIEW_ID = "genlab.gui.views.ProgressView";
	
	/**
	 * Associates each task with its counterpart in the table/tree
	 */
	private Map<ITask,TreeItem> task2item = new HashMap<ITask,TreeItem>(200);
	
	private Set<ITask> tasksDisplayedNotStopped = new HashSet<ITask>(100);

	/**
	 * Contains the tasks which require an update during the next update cycle.
	 * Typically, it contains these tasks for tasks detected through event (like: a novel task was added).
	 */
	private Set<ITask> tasksToUpdate = new HashSet<ITask>(100);
	
	/**
	 * Contains the task to remove
	 */
	private Set<ITask> tasksToRemove = new HashSet<ITask>(100);

	
	private Map<ITask, ProgressBar> task2progress = new HashMap<ITask, ProgressBar>(100);
	private Map<ITask, TreeEditor> task2editor = new HashMap<ITask, TreeEditor>(100);

	/**
	 * A temporary variable used only inside {@link #updateWidgets()}, but put here to avoid
	 * the frequent creation of a set.
	 */
	private Set<ITask> tasksUpdating = new HashSet<ITask>(100);
	
	
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
			setPriority(Thread.NORM_PRIORITY);
			
			GLLogger.traceTech("created.", getClass());
		}
		
		@Override
		public void run() {
			
			final Runnable updateRunnable = new Runnable() {
				
				@Override
				public void run() {
					view.updateWidgets();
					updatePending = false;
				}
			};
			
			while (!canceled && (display == null || !display.isDisposed())) {
				
				if (!updatePending && view.hasSomethingToUpdate()) {
					//GLLogger.traceTech("refresh !.", getClass());
					updatePending = true;
					view.getDisplay().syncExec(updateRunnable);
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
		
		treeWidget.dispose();
		
		for (Image img : algo2image.values()) {
			img.dispose();
		}
		algo2image.clear();

		super.dispose();
	}

	public Display getDisplay() {
		return display;
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

	}

	@Override
	public void setFocus() {
		treeWidget.setFocus();
	}
	

	private int findPositionForTask(ITask t) {
		
		if (task2item.isEmpty())
			return 0;
		
		GLLogger.debugTech("displaying task "+t+" "+t.getProgress().getTimestampCreation(), getClass());

		if (t.getPrerequires().isEmpty())
			return 0;	// quick solution
		
		LinkedList<ITask> dependenciesNotSatisfied = new LinkedList<ITask>(t.getPrerequires());
	
		TreeItem[] items = null;
		if (t.getParent() == null)
			items = treeWidget.getItems();
		else 
			items = task2item.get(t.getParent()).getItems();
		
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
	
	private Map<IAlgo,Image> algo2image = new HashMap<IAlgo, Image>(50);
	
	private TreeItem getOrCreateItemForTask(ITask t) {
		
		TreeItem item = task2item.get(t);
		
		if (item == null) {
			// create the item !
			if (t.getParent() == null)
				item = new TreeItem(
						treeWidget, 
						SWT.NONE, 
						findPositionForTask(t)
						);
			else 
				item = new TreeItem(
						getOrCreateItemForTask(t.getParent()), 
						SWT.NONE, 
						findPositionForTask(t)
						);
			item.setText(0, t.getName());
			item.setData(t);
			
			task2item.put(t, item);	
			tasksDisplayedNotStopped.add(t);
			
			// configure the item
			
			// ... its image...
			if (t instanceof IAlgoExecution) {
				IAlgoExecution ae = (IAlgoExecution)t;
				
				try {
					IAlgo algo = ae.getAlgoInstance().getAlgo();
					String imagePath = algo.getImagePath16X16();
					Image img = algo2image.get(algo);
					if (imagePath != null && img == null) {
					
						try {
							// TODO avoid to reload this image each time
							img = new Image(display, algo.getClass().getClassLoader().getResourceAsStream(imagePath));
							algo2image.put(algo, img);
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
	private void updateWidget(ITask t) {
		
		//GLLogger.debugTech("updating for task "+t, getClass());
		
		// create (or get) it ?
		TreeItem item = getOrCreateItemForTask(t);
		
		// also create its children ? 
		if (t instanceof IContainerTask) {
			IContainerTask cont = (IContainerTask)t;
			
			for (ITask sub : cont.getTasks()) {
				getOrCreateItemForTask(sub);
			}
		}
		
		if (item.isDisposed()) {
			task2item.remove(t);
			tasksDisplayedNotStopped.remove(t);
			return;
		}
		
		// update its state ? 
		final ComputationState state = t.getProgress().getComputationState();
		if (state == null) {
			System.err.println("******************************** NULL state ???");
			tasksDisplayedNotStopped.remove(t);

			return; // this should never happen. In practice, sometimes a task was removed but this is not yet obvious.
		}
		
		String txt = null;
		switch (state) {
		case FINISHED_OK: {
			StringBuffer sb = new StringBuffer();
			sb.append("finished (").append(UserMachineInteractionUtils.getHumanReadableTimeRepresentation(t.getProgress().getDurationMs())).append(")");
			txt = sb.toString();
			tasksDisplayedNotStopped.remove(t);
		} break;
		case FINISHED_CANCEL:
		case FINISHED_FAILURE:
			tasksDisplayedNotStopped.remove(t);
			break;
		case STARTED: { 
			StringBuffer sb = new StringBuffer();
			sb.append("running (").append(t.getProgress().getProgressDone()).append("/").append(t.getProgress().getProgressTotalToDo()).append(")");
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
		ProgressBar pb = task2progress.get(t);
		if (state == ComputationState.STARTED) {
			if (isItemVisible(item)) {
				if (pb == null) {
					// create the progress bar !
					TreeEditor editor = new TreeEditor(treeWidget);
					pb = new ProgressBar(treeWidget, SWT.SMOOTH);
					editor.grabHorizontal = true;
					editor.setEditor(pb, item, 1);
					task2progress.put(t, pb);
					task2editor.put(t, editor);
					pb.setState(SWT.NORMAL);
				}
				pb.setVisible(true);
				try {
					pb.setSelection((int)Math.floor(t.getProgress().getProgressPercent()));
				} catch (NullPointerException e) {
					// ignore it
				}
			} else if (pb != null) {
				pb.setVisible(false);
			}
		} else if (pb != null) {
			// should remove this progress bar !
			pb.dispose();
			task2editor.get(t).dispose();
			task2progress.remove(t);
			task2editor.remove(t);
		}
		// TODO hide ?
		
		
		
	}
	
	protected boolean hasSomethingToUpdate() {
		
		if (!tasksDisplayedNotStopped.isEmpty())
			return true;
		
		synchronized (tasksToUpdate) {
			if (!tasksToUpdate.isEmpty())
				return true;
		}
		
		synchronized (tasksToRemove) {
			return !tasksToRemove.isEmpty();
		}
	}
	
	protected Rectangle treeBounds = new Rectangle(0, 0, 0, 0);
	
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
	 * SHould be called from the SWT thread
	 */
	protected void updateWidgets() {
				
		//long timeStart = System.currentTimeMillis();
		
		if (treeWidget == null || treeWidget.isDisposed()) {
			thread.cancel();
			return;
		}
		
		treeWidget.setRedraw(false);
		
		// first remove the tasks to remove
		processRemoveTasks();
		
		// first copy the collection of tasks updates to avoid concurrent modifications
		synchronized (task2item) {
			tasksUpdating.addAll(tasksDisplayedNotStopped);		// don't update the tasks which are displayed, but no more running
		}
		synchronized (tasksToUpdate) {
			tasksUpdating.addAll(tasksToUpdate);
			tasksToUpdate.clear();
		}
		
				
		// now update each task / widget
		initItemsVisibility();
		
		//System.err.println("update tasks: "+tasksUpdating.size());
		for (ITask t : tasksUpdating) {
			try {
				updateWidget(t);
			} catch (RuntimeException e) {
				// log ? 
				GLLogger.warnTech("catched an error while updating a progress: "+e.getMessage(), getClass(), e);
			}
		}

		// clear the list
		tasksUpdating.clear();
		
		treeWidget.setRedraw(true);
		treeWidget.redraw();
		//System.err.println("update tasks took: "+(System.currentTimeMillis() - timeStart)+" ms");
		
		
	}

	private void manageTaskChanged(ITask task) {

		//GLLogger.debugTech("a task was added: "+task, getClass());
		synchronized (tasksToUpdate) {
			tasksToUpdate.add(task);	
			
			if (task instanceof IContainerTask) {
				IContainerTask ct = (IContainerTask)task;
				for (ITask subTask : ct.getTasks()) {
					tasksToUpdate.add(subTask);
				}
			}
		}
		//thread.interrupt();
		
	}

	@Override
	public void notifyTaskAdded(ITask task) {
		
		manageTaskChanged(task);
		//task.getProgress().addListener(this);
		
	}

	@Override
	public void notifyTaskRemoved(ITask task) {
		
		GLLogger.debugTech("a task was removed: "+task, getClass());
		
		//task.getProgress().removeListener(this);
		
		// TODO !!! task2item.remove(task);
	}


/*
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		notifyTaskAdded(progress.getAlgoExecution());
	}
	*/
	
	protected void clear(ITask task) {
		
		synchronized (task2item) {
			
			TreeItem item = task2item.remove(task);
			if (item != null)
				item.dispose();
			
			tasksDisplayedNotStopped.remove(task);
			
			TreeEditor editor = task2editor.remove(task);
			if (editor != null)
				editor.dispose();
			
			ProgressBar bar = task2progress.remove(task);
			if (bar != null)
				bar.dispose();
			
	
		}
		
		synchronized (tasksToUpdate) {
			
			tasksToUpdate.remove(task);

		}
		
	}
	
	public void clearFinished() {
		
		Set<ITask> toRemove = new HashSet<ITask>(task2item.size());
		
		synchronized (task2item) {
			
			// detect parent tasks to remove
			for (ITask task: task2item.keySet()) {
				
				// do not clean
				if (
						// subtasks
						(task.getParent() != null 
						|| 
						// or running or queued tasks
						!task.getProgress().getComputationState().isFinished()))
					continue;
				
				toRemove.add(task);
			}
			
			// detect children to remove
			for (ITask task: task2item.keySet()) {
				
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
			
		}
		
		// now remove all those listen
		synchronized (tasksToRemove) {
			tasksToRemove.addAll(toRemove);
		}
		
	}
	
	/**
	 * Called from the SWT thread to clear the items of removed tasks
	 */
	protected void processRemoveTasks() {

		synchronized (tasksToRemove) {
			
			
			for (ITask task: tasksToRemove) {
		
				clear(task);
				tasksDisplayedNotStopped.remove(task);
				tasksToUpdate.remove(task);
				
			}
			
			tasksToRemove.clear();
						
		}
		
	}
	
	@Override
	public void taskCleaning(ITask task) {

		synchronized (tasksToRemove) {
			tasksToRemove.add(task);
		}
		
		
	}

}
