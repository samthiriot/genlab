package genlab.core;

import genlab.core.model.doc.AvailableInfo;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.usermachineinteraction.GLLogger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class Activator implements BundleActivator {

	public static BundleContext context;

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
		
		// once we will have been started, we will be able to init things
		bundleContext.addBundleListener(new BundleListener() {
			
			@Override
			public void bundleChanged(BundleEvent event) {
				
				if (event.getType() != BundleEvent.STARTED) 
					return;
				
				if (!event.getBundle().getSymbolicName().equals("genlab.core"))
					return;
				
				GLLogger.infoTech("detecting algos and infos", getClass());

				// preload the list of algos
				ExistingAlgos.getExistingAlgos();
				
				// preload the doc
				AvailableInfo.getAvailableInfo().detectFromExtensions();
			
				// preload listeners
				WorkflowHooks.getWorkflowHooks();
			}
		});
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		
	}

}
