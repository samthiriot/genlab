package genlab.bayesianinference.inflib;

import genlab.core.IGenlabPlugin;

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
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	

	public static final String getName() {
		return "GenLab / Bayesian networks / InfLib";
	}

	public static final String getDescription() {
		return "integrates the Bayesian network implementation and inference engines proposed by the InfLib library";
	}

}
