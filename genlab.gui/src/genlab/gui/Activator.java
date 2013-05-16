package genlab.gui;

import java.io.File;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.genlab2eclipse.GenLabSaveParticipant;

import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
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

	@Override
	public void startup() throws CoreException {
		super.startup();
		
		System.err.println("loading data !");
		
		// add a save participant, and hook it, so our data will be save along with the eclipse's one.
		ISaveParticipant saveParticipant = new GenLabSaveParticipant();
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID, saveParticipant);
		if (lastState == null)
		    return;
		IPath location = lastState.lookup(new Path("save"));
		if (location == null)
		    return;
		// the plugin instance should read any important state from the file.
		File f = getStateLocation().append(location).toFile();
		 
		 
        // readStateFrom(f);
	}
	
	
}
