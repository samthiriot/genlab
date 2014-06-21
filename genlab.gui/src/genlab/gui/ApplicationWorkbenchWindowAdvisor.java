package genlab.gui;

import java.util.HashSet;
import java.util.Set;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.quality.TestResponsivity;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolBarImpl;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	/**
	 * elements to remove from the top toolbar
	 */
	public static final String[] toHideInToolbar = new String[]{ 
									//"SearchField",
									//"org.eclipse.search.searchActionSet",
									"org.eclipse.ui.edit.text.actionSet.annotationNavigation",
									"org.eclipse.ui.edit.text.actionSet.navigation",
									//"org.eclipse.jdt.debug.ui.JavaSnippetToolbarActions",
									"debug",
									"org.eclipse.debug.ui.launchActionSet"
									};
	
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @SuppressWarnings("restriction")
    private void hideFromToolbar(String[] toRemoveArray) { 
    	
    	Set<String> toRemove = new HashSet<String>(toRemoveArray.length);
    	for (String s: toRemoveArray)
    		toRemove.add(s);
    	
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window instanceof WorkbenchWindow) {                	
		    MTrimBar topTrim = ((WorkbenchWindow) window).getTopTrim();
		    if (topTrim == null)
		    	return;
			for (MTrimElement element : topTrim.getChildren()) {
				
				//System.err.println("toolbar: "+element.getElementId());
				//System.err.println("toolbar: "+element.getClass());

				try {
			    if (toRemove.contains(element.getElementId())) {                     
			        ((Control) element.getWidget()).dispose();
			    } else if (element instanceof ToolBarImpl) {
			    	ToolBarImpl elementTB = (ToolBarImpl)element;
			    	for (MToolBarElement e: elementTB.getChildren()) {
						//System.err.println("toolbar sub: "+e.getElementId());

			    		if (toRemove.contains(e.getElementId())) {   
			    			((Control)e).dispose();
			    		}
			    	}
			    }
				} catch (RuntimeException e) {
					GLLogger.warnTech("error while attempting to hide in toolbar: "+element, getClass());
				}
			}
		}
    }
    
    public void preWindowOpen() {
    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(600, 400));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowFastViewBars(true);
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
        
        
        // we can try to run it right now. 
        // but, sometimes it is too early.
        // in this case, it will work in the post event
		VisualResources.initVisualResource(Display.getCurrent().getActiveShell());

		
		TestResponsivity.startTestResponsivity();
		 
		// hide elements from toolbar
		PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
			
			@Override
			public void windowOpened(IWorkbenchWindow window) {
				hideFromToolbar(toHideInToolbar);
			    	
			}
			
			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}
		});

	}
    
    @Override
    public void postWindowOpen() {

    	// try again (will have no impact if it worked the first time)
		VisualResources.initVisualResource(Display.getCurrent().getActiveShell());
    
		
    }
    
}
