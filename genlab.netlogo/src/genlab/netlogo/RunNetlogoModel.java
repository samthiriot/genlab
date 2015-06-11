package genlab.netlogo;

import genlab.core.model.exec.IComputationProgress;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nlogo.api.CompilerException;
import org.nlogo.api.LogoException;
import org.nlogo.app.App;
import org.nlogo.headless.HeadlessWorkspace;

public class RunNetlogoModel {

	public RunNetlogoModel() {
		// TODO Auto-generated constructor stub
	}
	
	private static Object poolWorkspacesLocker = new Object();
	private static Map<String,Set<HeadlessWorkspace>> poolAvailableWorkspacesForModels = new HashMap<String,Set<HeadlessWorkspace>>();
	private static Map<HeadlessWorkspace, String> poolRunningWorkspacesForModels = new HashMap<HeadlessWorkspace, String>();

	public static HeadlessWorkspace getWorkspaceWithModel(String modelFilename) {
		
		HeadlessWorkspace workspace = null;
		Set<HeadlessWorkspace> workspacesAvailable = null;
		
		// check if we have one; if we do, return it immediately (quick)
		synchronized (poolWorkspacesLocker) {
			workspacesAvailable = poolAvailableWorkspacesForModels.get(modelFilename);
			if (workspacesAvailable == null) {
				// the model was maybe never processed
				workspacesAvailable = new HashSet<HeadlessWorkspace>();
				poolAvailableWorkspacesForModels.put(modelFilename, workspacesAvailable);
			}
			
			if (!workspacesAvailable.isEmpty()) {
				// a workspace is available
				workspace = workspacesAvailable.iterator().next();
				workspacesAvailable.remove(workspace);
				// now we have a workspace
				poolRunningWorkspacesForModels.put(workspace, modelFilename);
				GLLogger.debugTech("reusing a Netlogo workspace: "+poolRunningWorkspacesForModels.size()+" running, "+workspacesAvailable.size()+" available for model "+ modelFilename, RunNetlogoModel.class);	
		
				return workspace;
			}
			
		}
		
		// looks like no workspace was available,
		// let's create one (slow) => because it's slow, don't lock everyone for that !
		GLLogger.debugTech("creating one more Netlogo workspace for model "+modelFilename, RunNetlogoModel.class);
		// no more workspace available
		// create a novel one
		workspace = HeadlessWorkspace.newInstance();
		// open model
		try {
			workspace.open(modelFilename);
		} catch (Exception e) {
			final String msg = "error while running the model: "+e.getMessage();
			throw new RuntimeException(msg, e);
		}
	
		// add it to the pool 
		synchronized (poolWorkspacesLocker) {
			// now we have a workspace
			poolRunningWorkspacesForModels.put(workspace, modelFilename);
			GLLogger.debugTech("providing a fresh Netlogo workspace: "+poolRunningWorkspacesForModels.size()+" running, "+workspacesAvailable.size()+" available for model "+ modelFilename, RunNetlogoModel.class);	
		}

		// and return it
		return workspace;
	}
	
	public static void returnWorkspace(HeadlessWorkspace workspace) {
		try {
			workspace.command("clear-globals");
		} catch (Exception e) {
			e.printStackTrace();
			GLLogger.warnTech("unable to clear netlogo workspace after run: "+e.getMessage(), RunNetlogoModel.class, e);
		} 
		
		synchronized (poolWorkspacesLocker) {
			String modelFilename = poolRunningWorkspacesForModels.remove(workspace);
			Set<HeadlessWorkspace> workspacesAvailable = poolAvailableWorkspacesForModels.get(modelFilename); 
			GLLogger.debugTech("one more Netlogo workspace is available: "+poolRunningWorkspacesForModels.size()+" running, "+workspacesAvailable.size()+" available for model "+ modelFilename, RunNetlogoModel.class);
			workspacesAvailable.add(workspace);
		}
		
	}
	
	/**
	 * When a workspace is assumed to be "contaminated" (after an error for instance),
	 * just drop it: remove it from the pool, dispose it, etc.
	 * @param workspace
	 */
	public static void dropWorkspaceFromPool(HeadlessWorkspace workspace) {
		GLLogger.warnTech("dropping a Netlogo workspace", RunNetlogoModel.class);
		synchronized (poolWorkspacesLocker) {
			poolRunningWorkspacesForModels.remove(workspace);
		}
		try {
			workspace.dispose();
		} catch (Exception e) {
			GLLogger.warnTech("error while disposing the netlogo workspace: "+e.getMessage(), RunNetlogoModel.class, e);
		}
	}

	public static Map<String,Object> runNetlogoModelHeadless(
			ListOfMessages messages, 
			String modelFilename, 
			Map<String,Object> inputs, 
			Collection<String> outputs, 
			int maxIterations,
			IComputationProgress progress
			) {
		
		// check file does exist
		// TODO 
		
		HeadlessWorkspace workspace = getWorkspaceWithModel(modelFilename);
		/*
		HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
		
		// open model
		try {
			workspace.open(modelFilename);
			
		} catch (Exception e) {
			final String msg = "error while running the model: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		*/
		if (progress != null) 
			progress.incProgressMade(10);
		
		// define parameters
		StringBuffer sbParameters = new StringBuffer();
		try {
			for (String varName : inputs.keySet()) {
				Object value = inputs.get(varName);
				String valueStr = NetlogoUtils.toNetlogoString(value);
				messages.debugTech("defining variable "+varName+" to "+valueStr, RunNetlogoModel.class);
				workspace.command("set " + varName+ " " + valueStr);
				if (sbParameters.length() > 0)
					sbParameters.append(", ");
				sbParameters.append(varName).append("=").append(valueStr);
			}
			
			messages.debugUser("initialized the model with parameters "+sbParameters.toString(), RunNetlogoModel.class);
			// TODO seed ?
			// workspace.command("set random-seed " + args[3]) ;
			
		} catch (Exception e) {
			final String msg = "error while defining parameters: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		if (progress != null) 
			progress.incProgressMade(10);

			
		// initialize model
		try {
			messages.debugUser("setup model...", RunNetlogoModel.class);
			long timestampStart = System.currentTimeMillis();
			workspace.command("setup-network-load");
			
			workspace.command("setup") ;
			long duration = System.currentTimeMillis() - timestampStart;
			messages.debugTech("init in "+duration+"ms", RunNetlogoModel.class);
		} catch (Exception e) {
			final String msg = "error while initializing the model: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		if (progress != null) 
			progress.incProgressMade(50);
		
		
		// run the model
		try {
			long timestampStart = System.currentTimeMillis();
			messages.debugUser("run model...", RunNetlogoModel.class);
			workspace.command("repeat "+maxIterations+" [ go ]") ;
			long duration = System.currentTimeMillis() - timestampStart;
			messages.debugTech("run in "+duration+"ms", RunNetlogoModel.class);
			
		} catch (Exception e) {
			final String msg = "error while running the model: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		if (progress != null) 
			progress.incProgressMade(50);

		// retrieve results
		try {
			
			messages.debugUser("retrieving results...", RunNetlogoModel.class);

			// retrieve last tick
			Object ticksEnd = workspace.report("ticks");
			messages.debugTech("finished after "+ticksEnd+" ticks", RunNetlogoModel.class);

			Map<String,Object> results = new HashMap<String, Object>(outputs.size());
			for (String outputName: outputs) {
				Object retrieved = workspace.report(outputName);
				messages.traceTech("retrieved from the model "+outputName+"= "+retrieved, RunNetlogoModel.class);
				results.put(outputName, retrieved);
			}
			
			results.put("_duration", ticksEnd);
			if (progress != null) 
				progress.incProgressMade(10);

			// return the workspace 
			returnWorkspace(workspace);
			return results;
		} catch(Exception ex) {
			// don't reuse this workspace
			if (workspace != null)
				dropWorkspaceFromPool(workspace);
			// and transmit error
			ex.printStackTrace();
			throw new RuntimeException("error in netlogo : "+ex.getMessage(), ex);
		} 

	
	}
	

	private static boolean appAlreadyCreated = false;
	private static final Object lockerGui = new Object();
	
	public static Map<String,Object> runNetlogoModelGraphical(
			final ListOfMessages messages, 
			final String modelFilename, 
			final Map<String,Object> inputs, 
			final Collection<String> outputs, 
			final int maxIterations,
			final IComputationProgress progress
			) {
		synchronized (lockerGui) {
			return runNetlogoModelGraphicalNotSynchronized(messages, modelFilename, inputs, outputs, maxIterations, progress);
		}
	}
	
	private static Map<String,Object> runNetlogoModelGraphicalNotSynchronized(
			final ListOfMessages messages, 
			final String modelFilename, 
			final Map<String,Object> inputs, 
			final Collection<String> outputs, 
			final int maxIterations,
			final IComputationProgress progress
			) {
		
		if (!appAlreadyCreated) {
			App.main(new String[0]);
			appAlreadyCreated = true;
			
			final LinkedList<Shell> currentShell = new LinkedList<Shell>();
			Display.getDefault().syncExec(new Runnable() {
				
				@Override
				public void run() {
					currentShell.add(Display.getDefault().getActiveShell());
				}
			});
			System.setSecurityManager(new RefuseExitSecurityManager(currentShell.get(0)));
			// useless: App.app().frame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		
	    final Map<String,Object>  res = new HashMap<String,Object>();
	    try {
	    	// open model 
	        java.awt.EventQueue.invokeAndWait(
	        		new Runnable() {
				        public void run() {
				        	// open model
							try {
								App.app().open(modelFilename);
								
								
							} catch (Exception e) {
								final String msg = "error while running the model: "+e.getMessage();
								messages.errorUser(msg, RunNetlogoModel.class, e);
								throw new RuntimeException(msg, e);
							}

							
							if (progress != null) 
								progress.incProgressMade(10);
							
				        }
	        		}
	        		);
	        
	     // define parameters
		try {
			
			for (String varName : inputs.keySet()) {
				Object value = inputs.get(varName);
				String valueStr = NetlogoUtils.toNetlogoString(value);
				messages.debugTech("defining variable "+varName+" to "+valueStr, RunNetlogoModel.class);
				App.app().command("set " + varName+ " " + valueStr);
			}
			
			// TODO seed ?
			// workspace.command("set random-seed " + args[3]) ;
			
		} catch (Exception e) {
			final String msg = "error while defining parameters: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		
		if (progress != null) 
			progress.incProgressMade(10);

	     } catch(Exception ex) {
	        ex.printStackTrace();
			throw new RuntimeException("error in netlogo : "+ex.getMessage(), ex);
	    }
	    
		// initialize model
		try {
			messages.debugTech("setup model", RunNetlogoModel.class);

			long timestampStart = System.currentTimeMillis();
			App.app().command("setup") ;
			long duration = System.currentTimeMillis() - timestampStart;
			messages.debugTech("init in "+duration+"ms", RunNetlogoModel.class);
		} catch (Exception e) {
			final String msg = "error while initializing the model: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		if (progress != null) 
			progress.incProgressMade(50);

		
		// run the model
		try {
			long timestampStart = System.currentTimeMillis();
			messages.debugTech("running model", RunNetlogoModel.class);
			App.app().command("reset-ticks") ;
			App.app().command("repeat "+maxIterations+" [ go ]") ;
			long duration = System.currentTimeMillis() - timestampStart;
			messages.debugTech("run in "+duration+"ms", RunNetlogoModel.class);
			
		} catch (Exception e) {
			final String msg = "error while running the model: "+e.getMessage();
			messages.errorUser(msg, RunNetlogoModel.class, e);
			throw new RuntimeException(msg, e);
		}
		if (progress != null) 
			progress.incProgressMade(50);

			
		// retrieve results
		try {
			
			// retrieve last tick
			Object ticksEnd = App.app().report("ticks");
			messages.debugTech("finished after "+ticksEnd+" ticks", RunNetlogoModel.class);

			Map<String,Object> results = new HashMap<String, Object>(outputs.size());
			for (String outputName: outputs) {
				Object retrieved = App.app().report(outputName);
				messages.traceTech("retrieved from the model "+outputName+"= "+retrieved, RunNetlogoModel.class);
				results.put(outputName, retrieved);
			}
		    
			results.put("_duration", ticksEnd);
			if (progress != null) 
				progress.incProgressMade(10);

			
			return results;
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("error in netlogo : "+ex.getMessage(), ex);
		} 
	
	}

}
