package genlab.bayesianinference;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, IGenlabPlugin {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		GLLogger.infoTech("loading the Bayesian network plugin; it provides basic elements for Bayesian networks and inference", getClass());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	

	public static final String getName() {
		return "GenLab / Bayesian Inference";
	}

	public static final String getDescription() {
		return "Shares the concept of Bayesian network and inference engines with other plugins. Provides no actual implementation of these concepts.";
	}

}
