package genlab.core.persistence;

import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;

import java.util.HashSet;
import java.util.Set;

public class PersistenceUtils {

	
	protected static Set<ClassLoader> detectedClassLoaders = null;
	
	protected static void listClassLoaders() {
		
		detectedClassLoaders = new HashSet<ClassLoader>();
		
        for (IAlgo algo: ExistingAlgos.getExistingAlgos().getAlgos()) {
    		detectedClassLoaders.add(algo.getClass().getClassLoader());
        }

	}
	
	public static Class loadFromGenlabBundleClassLoaders(String classname) throws ClassNotFoundException {
		if (detectedClassLoaders == null)
			listClassLoaders();
		for (ClassLoader cl: detectedClassLoaders) {
			try {
				Class c = cl.loadClass(classname);
				return c;
			} catch (ClassNotFoundException e) {
				// ignore silently
			}
		}
		throw new ClassNotFoundException("unable to load class "+classname);
	}
	private PersistenceUtils() {
		
	}

}
