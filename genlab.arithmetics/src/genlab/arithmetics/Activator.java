package genlab.arithmetics;

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
		GLLogger.infoUser("initializing the GenLab/arithmetics plugin, which provides basic arithmetic parsing and interpretation", getClass());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	

	public static final String getName() {
		return "GenLab / Arithmetics";
	}

	public static final String getDescription() {
		return "facilitates the use of scripting (as Javascript) to conduct simple arithmetic computations";
	}

}
