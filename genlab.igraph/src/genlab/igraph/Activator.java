package genlab.igraph;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.igraph.Rigraph.RIGraph2Genlab;
import genlab.igraph.algos.generation.lcffamous.FamousLCFGraphs;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.natjna.IGraphNativeLibrary;
import genlab.igraph.natjna.IGraphRawLibrary;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;


public class Activator extends Plugin implements BundleActivator, IGenlabPlugin {

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
		

		IgraphLibFactory.isIGraphAvailable();

		
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
	

	public static final String getName() {
		return "GenLab / igraph";
	}

	public static final String getDescription() {
		return "integrates the igraph reference library for the analysis of networks";
	}

}
