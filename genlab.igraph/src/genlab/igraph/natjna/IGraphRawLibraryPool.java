package genlab.igraph.natjna;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;

public class IGraphRawLibraryPool {

	public static final IGraphRawLibraryPool  singleton = new IGraphRawLibraryPool();
	
	private LinkedList<IGraphLibrary> pool = new LinkedList<IGraphLibrary>();
	private LinkedList<IGraphLibrary> working = new LinkedList<IGraphLibrary>();
	private LinkedList<IGraphLibrary> available = new LinkedList<IGraphLibrary>();
	
	public IGraphLibrary getLibrary() {
		synchronized (pool) {
		
			if (available.isEmpty()) {
				GLLogger.traceTech("no more library available in the pool ("+pool.size()+"); creating a new one", getClass());
				IGraphLibrary novel = new IGraphLibrary();
				pool.add(novel);
				working.add(novel);
				return novel;
			} else {
				IGraphLibrary res = available.removeLast();
				working.add(res);
				GLLogger.traceTech("providing a native igraph library; "+pool.size()+" total, "+available.size()+" available", getClass());
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
