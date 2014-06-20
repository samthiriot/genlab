package genlab.gui.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import genlab.core.exec.IExecution;
import genlab.gui.perspectives.IOutputGUIManagementListener;
import genlab.gui.perspectives.OutputsGUIManagement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.forms.widgets.Section;

public class RuntimePerspectiveIntroView extends AsbtractStaticMessageView implements IOutputGUIManagementListener {

	public static final String ID = "genlab.gui.views.RuntimePerspectiveIntroView";

	/**
	 * true if an async update is already submitted but not started;
	 * avoids frequent updates if not usefull
	 */
	protected boolean updatePending = false;

	protected Section sectionA;
	protected Tree treeWindows = null;
	protected Button buttonCloseAll = null;
	protected Button buttonCloseSelected = null;
	
	protected Map<IExecution, TreeItem> exec2item = new HashMap<IExecution, TreeItem>(50);
	protected Map<IViewPart, TreeItem> view2item = new HashMap<IViewPart, TreeItem>(500);
	
	public RuntimePerspectiveIntroView() {
		super("Runtime perspective");
		
	}

	protected void closeSelected() {
		
		for (TreeItem item : treeWindows.getSelection()) {
			
			final Object data = item.getData();
			if (exec2item.containsKey(data)) {
				OutputsGUIManagement.singleton.closeAllOutputsForExecution((IExecution) data);
			} else {
				OutputsGUIManagement.singleton.closeOutput((IViewPart) data);
			}
			 
		}
	}
	
	private void openSelectedView() {
		if (treeWindows.getSelectionCount() == 0)
			return;
		
		Object data = treeWindows.getSelection()[0].getData();
		if (data instanceof IViewPart) {
			OutputsGUIManagement.singleton.showOutput((IViewPart) data);

		}
	}
	
	protected void updateSelectionButton() {
		if (buttonCloseSelected == null || buttonCloseSelected.isDisposed())
			return;
		buttonCloseSelected.setEnabled(treeWindows.getSelectionCount()>0);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		toolkit.createLabel(
				form.getBody(), 
				"This is the runtime perspective. It is were you can monitor the progress of computations and where the displays will be opened (if you added any display algorithm to your workflow)."
				);
		

		sectionA = toolkit.createSection(form.getBody(),  Section.TITLE_BAR);
		sectionA.setExpanded(true);
		sectionA.setText("List of windows for runs");
		
		Composite sectionAclient = toolkit.createComposite(sectionA);
		sectionAclient.setLayout(new RowLayout(SWT.VERTICAL));
		sectionA.setClient(sectionAclient);
 
		// list of windows
		{
			treeWindows = toolkit.createTree(sectionAclient, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
			RowData layoutData = new RowData(400, 300);
			treeWindows.setLayoutData(layoutData);
			treeWindows.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSelectionButton();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			treeWindows.addKeyListener(new KeyListener() {
				
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.keyCode == SWT.DEL) {
						closeSelected();
						e.doit = false;
					} else if (e.keyCode == SWT.CR) {
						openSelectedView();
						e.doit = false;
					}
				}
				
				
				@Override
				public void keyPressed(KeyEvent e) {
					
				}
			});
			treeWindows.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseUp(MouseEvent e) {
					
				}
				
				@Override
				public void mouseDown(MouseEvent e) {
					
				}
				
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					openSelectedView();
				}
			});
		}
		// "close selected" button
		{
			buttonCloseSelected = toolkit.createButton(
					sectionAclient, 
					"close selected displays", 
					SWT.PUSH
					);
			buttonCloseSelected.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					closeSelected();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
			buttonCloseSelected.setEnabled(false);
		}
		// "close all" button
		{
			buttonCloseAll = toolkit.createButton(
					sectionAclient, 
					"close all output displays", 
					SWT.PUSH
					);
			buttonCloseAll.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					OutputsGUIManagement.singleton.closeAllOutputs();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
			buttonCloseAll.setEnabled(false);

		}
		
		// listen for events
		OutputsGUIManagement.singleton.addListener(this);
		
		// first update
		updateTree();

	}
	
	protected void updateTree() {
				
		updatePending = false;
		
		treeWindows.setRedraw(false);
		
		// retrieve data to display
		Map<IExecution, Collection<IViewPart>> exec2views = OutputsGUIManagement.singleton.getAllWindows();

		// list of elements used
		Set<IExecution> executionNotUsed = new HashSet<IExecution>(exec2item.keySet());
		Set<IViewPart> viewsNotUsed = new HashSet<IViewPart>(view2item.keySet());
		
		// add all elements
		for (IExecution exec: exec2views.keySet()) {
			
			TreeItem execItem = exec2item.get(exec);
			if (execItem == null) {
				execItem = new TreeItem(treeWindows, SWT.NONE);
				execItem.setText(exec.getId());
				execItem.setData(exec);
				exec2item.put(exec, execItem);
			}
			executionNotUsed.remove(exec);
			
			for (IViewPart view : exec2views.get(exec)) {
				TreeItem viewItem = view2item.get(view);
				if (viewItem == null) {
					viewItem = new TreeItem(execItem, SWT.NONE);
					viewItem.setText(view.getTitle());
					viewItem.setData(view);
					viewItem.setImage(view.getTitleImage());
					view2item.put(view, viewItem);
				}
				viewsNotUsed.remove(view);
			}
			
			execItem.setExpanded(true);
			
		}
		
		// remove all useless elements
		for (IViewPart v: viewsNotUsed) {
			view2item.remove(v).dispose();
		}
		for (IExecution e: executionNotUsed) {
			exec2item.remove(e).dispose();
		}
		
		treeWindows.setRedraw(true);
		
		buttonCloseAll.setEnabled(!exec2views.isEmpty());
		
		updateSelectionButton();
		
	}

	@Override
	public void notifyOutputGUIchanged() {
				
		if (updatePending)
			return;
		
		updatePending = true;
		form.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				updateTree();
			}
		});
	}

	@Override
	public void dispose() {

		treeWindows.dispose();
		buttonCloseAll.dispose();
		buttonCloseSelected.dispose();
		form.dispose();
		
		super.dispose();
	}

	@Override
	public void setFocus() {
		form.getBody().setFocus();
	}
	
	
	

}
