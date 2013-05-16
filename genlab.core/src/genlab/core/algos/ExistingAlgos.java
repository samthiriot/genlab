package genlab.core.algos;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * discovers and publishes all the available algorithms
 * @author Samuel Thiriot
 */
public final class ExistingAlgos {

	private static ExistingAlgos singleton = null;
	
	public static ExistingAlgos getExistingAlgos() {
		if (singleton == null)
			singleton = new ExistingAlgos();
		return singleton;
	}
	
	private static final String EXTENSION_POINT_ALGOS_ID = "genlab.core.algo";

	private Map<String,IAlgo> name2algos = new HashMap<String,IAlgo>();
	
	private ExistingAlgos() {
		detectedFromExtensionPoints();
	}
	
	
	public void declareAlgo(IAlgo algo) {
		GLLogger.debugTech("detected available algorithm: "+algo.getName(), getClass());
		name2algos.put(algo.getName(), algo);
	}
	
	
	private void detectedFromExtensionPoints() {
		
		GLLogger.debugTech("detecting available algorithms from plugins...", getClass());
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_ALGOS_ID);
	    for (IConfigurationElement e : elements) {
	    	GLLogger.debugTech("Evaluating extension: "+e.getName(), getClass());
		    Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o instanceof IAlgo) {
					declareAlgo((IAlgo) o);
				} else {
					GLLogger.warnTech("detected something which is not an algo: "+o, getClass());
				}
			} catch (CoreException e1) {
				GLLogger.errorTech("error while detecting available algorithms: error with extension point "+e.getName(), getClass(), e1);
			}
			
		}
	    
		GLLogger.infoTech("detected "+name2algos.size()+" algorithms provided by plugins", getClass());

	}
	
	public Collection<String> getAlgoNames() {
		return name2algos.keySet();
	}
	
	public Collection<IAlgo> getAlgos() {
		return name2algos.values();
	}
 }
