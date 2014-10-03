package genlab.gnuplot;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin implements BundleActivator, IGenlabPlugin {

	private static BundleContext context;

	// The shared instance
	private static Plugin plugin;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		plugin = this;

		GLLogger.infoTech("initializing the Gnuplot pluging, which exports data in the gnuplot format...", getClass());

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		plugin = null;

	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Plugin getDefault() {
		return plugin;
	}

	public static final String getName() {
		return "GenLab / gnuplot";
	}

	public static final String getDescription() {
		return "facilitates the exportation of data to the GnuPlot powerful graph engine";
	}
}
