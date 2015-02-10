package genlab.r;

import genlab.core.IGenlabPlugin;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.r.rsession.Genlab2RSession;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

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
		
		GLLogger.infoTech("initializing the R plugin, which links GenLab to R if it is installed", getClass());

		
		// async test if R is available
		bundleContext.addBundleListener(new BundleListener() {
			
			@Override
			public void bundleChanged(BundleEvent event) {
				
				if (event.getType() != BundleEvent.STARTED) 
					return;
				
				if (!event.getBundle().getSymbolicName().equals("genlab.r"))
					return;

				// ensures R is available
				if (Genlab2RSession.isRAvailable()) {
					GLLogger.debugUser("It is possible to connect to R; the corresponding algorithms are available.", Activator.class);
				} else {
					GLLogger.warnUser("Not able to connect to R; the R algorithms will not be available. Please ensure R is installed in your environment (http://cran.r-project.org), and the Rserve R package is installed (type install.package(\"Rserve\") in a R console)", Activator.class);
				}
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

	public static String getName() {
		return "R";
	}
	
	public static String getDescription() {
		return "Integrates the access to the R environment";
	}
	
}
