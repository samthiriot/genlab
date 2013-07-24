package genlab.core;

import genlab.core.model.doc.AvailableInfo;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.usermachineinteraction.GLLogger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

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
		GLLogger.infoTech("initializing the genlab core...", getClass());
		
		// preload the list of algos
		ExistingAlgos.getExistingAlgos();
		
		// preload the doc
		AvailableInfo.getAvailableInfo().detectFromExtensions();
	
		// preload listeners
		WorkflowHooks.getWorkflowHooks();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
