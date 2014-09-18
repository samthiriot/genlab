package genlab.gui;

import java.util.HashSet;
import java.util.Set;

import genlab.gui.perspectives.WorkflowPerspective;
import genlab.gui.preferences.Genlab2eclipsePreferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

/**
 * This workbench advisor creates the window advisor, and specifies
 * the perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    @Override
    public IAdaptable getDefaultPageInput() 
	{
    	// show the workspace in the navigator
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public String getInitialWindowPerspectiveId() {
		return WorkflowPerspective.ID;
	} 

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {

        configurer.setSaveAndRestore(true); 

		// register everything, including CNF (navigator)
		IDE.registerAdapters();
		
		// no file linking
		// TODO not working...
		PlatformUI.getPreferenceStore().setValue(ResourcesPlugin.PREF_DISABLE_LINKING, true); 
		PlatformUI.getPreferenceStore().setValue(ResourcesPlugin.PREF_AUTO_REFRESH, true);

		// TODO hide the useless menus, categories, features...
		//Set<String> activatedActivityIds = new HashSet()<String>();
		//activatedActivityIds.add(arg0)
		//PlatformUI.getWorkbench().getActivitySupport().setEnabledActivityIds(activatedActivityIds);
		
	}

	@Override
	public void postStartup() {
		super.postStartup();
		
		// load preferences
		Genlab2eclipsePreferences.singleton.atStartup();
		
	}
	
}
