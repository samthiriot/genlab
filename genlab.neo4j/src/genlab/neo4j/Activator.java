package genlab.neo4j;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

		GLLogger.infoTech("initializing the neo4j plugin for genlab...", getClass());

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		Activator.context = null;
		super.stop(bundleContext);
	}
	
	public static Activator getDefault() {
		return plugin;
	}
	

	public static final String getName() {
		return "GenLab / Neo4j";
	}

	public static final String getDescription() {
		return "integrates exportation to the database network format";
	}

}
