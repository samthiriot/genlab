package genlab.graphstream.ui;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.graphstream.ui.views.AbstractGraphView;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IGenlabPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "genlab.graphstream.ui"; //$NON-NLS-1$

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
		plugin = this;
		
		GLLogger.infoTech("initializing the graphstream.ui plugin...", getClass());
		
		try {
			GLLogger.debugTech("attempting to load the graphstream class "+AbstractGraphView.CLASSNAME_VIEWER, getClass());
			context.getBundle().loadClass(AbstractGraphView.CLASSNAME_VIEWER);
			AbstractGraphView.isAvailable = true;
		} catch (ClassNotFoundException e) {
			AbstractGraphView.isAvailable = true;
			GLLogger.errorTech("unable to load the graphstream class "+AbstractGraphView.CLASSNAME_VIEWER+"; the viewer algos will not be available", getClass());
		}
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
	

	public static final String getName() {
		return "GenLab / GUI / Graphstream";
	}

	public static final String getDescription() {
		return "adds to GUI the network visualization facilities provided by the graphstream library";
	}

}
