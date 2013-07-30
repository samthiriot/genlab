package genlab.igraph;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.igraph.algos.generation.lcffamous.FamousLCFGraphs;
import genlab.igraph.natjna.IGraphRawLibrary;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;


public class Activator extends Plugin implements BundleActivator {

	private static BundleContext context;

	// The shared instance
	private static Activator plugin;
		
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

		GLLogger.infoTech("initializing the igraph plugin for genlab...", getClass());

		GLLogger.debugTech("checking the availability of the native library...", getClass());
		if (!IGraphRawLibrary.isAvailable) {
			GLLogger.warnTech("unable to load the native igraph library; features will be missing", getClass());
			// TODO warn user as well
			// TODO deactivate all the igraph algorithms
		}
				
		// declare the LCF algos
		bundleContext.addBundleListener(new BundleListener() {
			
			@Override
			public void bundleChanged(BundleEvent event) {
				
				if (event.getType() != BundleEvent.STARTED) 
					return;
				
				if (!event.getBundle().getSymbolicName().equals("genlab.core"))
					return;
				
				GLLogger.infoTech("declaring famous LCF graphs", getClass());
				FamousLCFGraphs.declareFamousLCF();
			}
		});
		
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		Activator.context = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
