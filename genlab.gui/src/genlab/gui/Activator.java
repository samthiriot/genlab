package genlab.gui;

import genlab.core.exec.TaskManagers;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.genlab2eclipse.EclipseResourceListener;
import genlab.gui.genlab2eclipse.ExecutionListener;
import genlab.gui.genlab2eclipse.GenLabSaveParticipant;
import genlab.gui.genlab2eclipse.StartupTasksDisplayer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.splash.SplashHandlerFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "genlab.gui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		super.start(context);
		GLLogger.infoTech("initializing the Graphical User Interface plugin for genlab...", getClass());
		plugin = this;
		
		// monitor loading tasks
		GLLogger.infoTech("listen for loading informations and display them", getClass());
		// ask genlab to transmit info on inits
		TaskManagers.getTaskManagers().startupTasks.addListener(StartupTasksDisplayer.singleton);
		// and register it for eclipse
		
		
		// add a save participant, and hook it, so our data will be save along with the eclipse's one.
		GLLogger.infoTech("registering a save participant...", getClass());
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(
				PLUGIN_ID, 
				new GenLabSaveParticipant()
				);

		// listen for workspace events, so we will load the corresponding genlab resources.
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new EclipseResourceListener(),
				IResourceChangeEvent.POST_CHANGE
				);
		
		// listen for execution events
		{
			ExecutionListener execL = new ExecutionListener();
		}
		
		/*
		 * if (lastState == null)
		 
		    return;
		IPath location = lastState.lookup(new Path("save"));
		if (location == null)
		    return;
		// the plugin instance should read any important state from the file.
		File f = getStateLocation().append(location).toFile();
		*/		 
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
