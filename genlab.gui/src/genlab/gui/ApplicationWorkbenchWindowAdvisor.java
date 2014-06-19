package genlab.gui;

import genlab.gui.perspectives.RunPerspective;
import genlab.gui.perspectives.WorkflowPerspective;
import genlab.quality.TestResponsivity;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
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
		 
    }
    
    @Override
    public void postWindowOpen() {

    	// try again (will have no impact if it worked the first time)
		VisualResources.initVisualResource(Display.getCurrent().getActiveShell());
    
		
    }
    
}
