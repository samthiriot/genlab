package genlab.gui;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import genlab.gui.perspectives.WorkflowPerspective;
import genlab.gui.preferences.Genlab2eclipsePreferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

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

        
        // declare default eclipse IDE imgs
        declareWorkbenchImages();
        
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
	
	public static final String ICONS_PATH = "$nl$/icons/full/";
	public static final String PATH_OBJECT = ICONS_PATH + "obj16/";
	public static final String PATH_ELOCALTOOL  = ICONS_PATH + "elcl16/";
	
	/**
	 * Defines the default IDE images
	 */
	private void declareWorkbenchImages() {
		
		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
		
		declareWorkbenchImage(
				ideBundle, 
				IDE.SharedImages.IMG_OBJ_PROJECT, 
				PATH_OBJECT+"prj_obj.gif", 
				true
				);
		
		declareWorkbenchImage(
				ideBundle, 
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, 
				PATH_OBJECT+"cprj_obj.gif", 
				true
				);
		

		declareWorkbenchImage(
				ideBundle, 
				IDE.SharedImages.IMG_OPEN_MARKER, 
				PATH_ELOCALTOOL+"getoobj_tsk.gif", 
				true
				);
		
		declareWorkbenchImage(
				ideBundle, 
				IDE.SharedImages.IMG_OBJS_TASK_TSK, 
				PATH_OBJECT+"taskmrk_tsk.gif", 
				true
				);
		declareWorkbenchImage(
				ideBundle, 
				IDE.SharedImages.IMG_OBJS_BKMRK_TSK, 
				PATH_OBJECT+"bkmrk_tsk.gif", 
				true
				);
		
	}
	
	private void declareWorkbenchImage(Bundle ideBundle, String symbolicName, String path, boolean shared) {
		URL url = FileLocator.find(ideBundle, new Path(path), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		WorkbenchImages.declareImage(symbolicName, desc, shared);
	}
	
}
