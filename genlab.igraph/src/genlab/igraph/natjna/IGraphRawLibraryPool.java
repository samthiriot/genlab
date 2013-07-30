package genlab.igraph.natjna;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;

public class IGraphRawLibraryPool {

	/**
	 * After this threshold, a Program exception is raised (this probably means some algo is not returning its library !).
	 */
	public static final int MAX_POOL_SIZE = 50;
	
	public static final IGraphRawLibraryPool  singleton = new IGraphRawLibraryPool();
	
	private LinkedList<IGraphLibrary> pool = new LinkedList<IGraphLibrary>();
	private LinkedList<IGraphLibrary> working = new LinkedList<IGraphLibrary>();
	private LinkedList<IGraphLibrary> available = new LinkedList<IGraphLibrary>();
	
	public static final String KEY_INFO_CREATED_INSTANCES = "igraph / total count of igraph native libraries instances";

	public IGraphLibrary getLibrary(IExecution exec) {
		synchronized (pool) {
		
			if (available.isEmpty()) {
				GLLogger.traceTech("no more library available in the pool ("+pool.size()+"); creating a new one", getClass());
				if (pool.size() >= MAX_POOL_SIZE)
					throw new ProgramException("max pool size reached for native libraries; either you have many processors, or some algos are not returning their library as they should");
				IGraphLibrary novel = new IGraphLibrary();
				pool.add(novel);
				working.add(novel);
				exec.setTechnicalInformation(KEY_INFO_CREATED_INSTANCES, pool.size());
				return novel;
			} else {
				IGraphLibrary res = available.removeLast();
				working.add(res);
				GLLogger.traceTech("providing a native igraph library; "+pool.size()+" total, "+available.size()+" available", getClass());
				exec.setTechnicalInformation(KEY_INFO_CREATED_INSTANCES, pool.size());
				return res;
			}
			
			
		}
	}
	

	public void returnLibrary(IGraphLibrary lib) {
		synchronized (pool) {
		
			working.remove(lib);
			available.add(lib);
			GLLogger.traceTech("a native igraph library is now available; "+pool.size()+" total, "+available.size()+" available", getClass());
			
		}
	}
	
	private IGraphRawLibraryPool() {
		
	}

}
