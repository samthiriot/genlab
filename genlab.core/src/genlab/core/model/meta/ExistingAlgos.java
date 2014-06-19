package genlab.core.model.meta;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * discovers and publishes all the available algorithms.
 * Limitation: requires the underlying framework to be able to access
 * extension points. 
 * 
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

	private Map<String,IAlgo> name2algos = new HashMap<String,IAlgo>(50);
	private Map<String,IAlgo> classname2algos = new HashMap<String,IAlgo>(50);
	private Map<String,IAlgo> extensionId2algos = new HashMap<String,IAlgo>(50);
	private Map<String,IAlgo> id2algos = new HashMap<String,IAlgo>(50);

	private Map<String,IConstantAlgo> name2constantAlgo = new HashMap<String, IConstantAlgo>(30);
	private Map<String,IDisplayAlgo> name2displayAlgo = new HashMap<String, IDisplayAlgo>(30);

	private ExistingAlgos() {
		detectedFromExtensionPoints();
	}
	
	
	public void declareAlgo(IAlgo algo) {
		GLLogger.debugTech("detected available algorithm: "+algo.getId()+" - "+algo.getName()+" "+algo.getDescription(), getClass());
		name2algos.put(algo.getName(), algo);
		classname2algos.put(algo.getClass().getCanonicalName(), algo);
		id2algos.put(algo.getId(), algo);
		
		if (algo instanceof IConstantAlgo) {
			name2constantAlgo.put(algo.getName(), (IConstantAlgo) algo);
		}
		if (algo instanceof IDisplayAlgo) {
			name2displayAlgo.put(algo.getName(), (IDisplayAlgo)algo);
		}
	}
	
	
	private void detectedFromExtensionPoints() {
		
		GLLogger.debugTech("detecting available algorithms from plugins...", getClass());
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    if (reg == null) {
	    	GLLogger.warnTech("no extension registry detected; no algo can be detected", this.getClass());
	    	return;
	    }
	    
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_ALGOS_ID);
	    for (IConfigurationElement e : elements) {
	    	Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o == null) {
					GLLogger.errorTech("unable to load an algo", getClass());
				} else if (o instanceof IAlgo) {
					declareAlgo((IAlgo) o);
					extensionId2algos.put(e.getAttribute("id"), (IAlgo)o);
				} else {
					GLLogger.warnTech("detected something which is not an algo: "+o, getClass());
				}
			} catch (Throwable e1) {
				GLLogger.errorTech("error while detecting available algorithms: error with extension point "+e.getName(), getClass(), e1);
				e1.printStackTrace();
			}
			
		}
	    
		GLLogger.infoTech("detected "+name2algos.size()+" algorithms provided by plugins", getClass());

	}
	
	public Collection<String> getAlgoNames() {
		return name2algos.keySet();
	}
	
	public IAlgo getAlgoForExtensionId(String id) {
		
		return extensionId2algos.get(id);
	}
	

	public IAlgo getAlgoForId(String id) {
		
		return id2algos.get(id);
	}
	
	public IAlgo getAlgoForClass(String canonicalName) {
		
		IAlgo algo = classname2algos.get(canonicalName);
		
		if (algo == null) {
			// best effort: not declared through other ways (like extension points)
			// but maybe we can still find it ?
			try {
				GLLogger.traceTech("unable to find this class from declared one; attempting to load it: "+canonicalName, getClass());
				Class<IAlgo> c = (Class<IAlgo>) ClassLoader.getSystemClassLoader().loadClass(canonicalName);
				if (c != null) {
					GLLogger.traceTech("found this algo through dynamic loading: "+canonicalName, getClass());
					algo = c.newInstance();
					classname2algos.put(canonicalName, algo);	
				}
				
			} catch (ClassNotFoundException e) {
				GLLogger.warnTech("not able to load this class "+canonicalName, getClass(), e);
			} catch (InstantiationException e) {
				GLLogger.warnTech("not able to instanciate this class "+canonicalName, getClass(), e);
			} catch (IllegalAccessException e) {
				GLLogger.warnTech("not able to access this class "+canonicalName, getClass(), e);
			}
			
		}
		
		return algo;
	}
	
	public Collection<IAlgo> getAlgos() {
		return name2algos.values();
	}
	
	public boolean hasAlgoInCategory(String categId) {
		for (IAlgo algo : name2algos.values()) {
			if (categId.equals(algo.getCategoryId()))
				return true;
		}
		return false;
	}
	
	public Collection<IConstantAlgo> getConstantAlgos() {
		return name2constantAlgo.values();
	}
	
	public Collection<IDisplayAlgo> getDisplayAlgos() {
		return name2displayAlgo.values();
	}
	
	
 }
