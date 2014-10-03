package genlab.algog.core;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

public class Activator extends Plugin implements BundleActivator, IGenlabPlugin {

	private static BundleContext context;

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
		GLLogger.infoTech("initializing the genlab genetic algorithm plugin", getClass());
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
	public static Activator getDefault() {
		return plugin;
	}
	

	public static final String getName() {
		return "GenLab / AlgoG";
	}

	public static final String getDescription() {
		return "genetic algorithms as meta-heuristics";
	}

}
