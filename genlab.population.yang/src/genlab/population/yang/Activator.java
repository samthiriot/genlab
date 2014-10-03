package genlab.population.yang;

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
		
		GLLogger.infoTech("initializing the GenLab/YANG population plugin for genlab; it provides algorithms for the generation of synthetic populations.", getClass());

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}


	public static final String getName() {
		return "GenLab / populations / YANG";
	}

	public static final String getDescription() {
		return "Integrates the YANG approach for the generation of synthetic populations, notably the use of Bayesian networks";
	}
}
