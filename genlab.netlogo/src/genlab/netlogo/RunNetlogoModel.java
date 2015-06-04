package genlab.netlogo;

import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nlogo.headless.HeadlessWorkspace;

public class RunNetlogoModel {

	public RunNetlogoModel() {
		// TODO Auto-generated constructor stub
	}
	

	
	public static Map<String,Object> runNetlogoModel(
			ListOfMessages messages, 
			String modelFilename, 
			Map<String,Object> inputs, 
			Collection<String> outputs, 
			int maxIterations,
			IComputationProgress progress
			) {
		
		// check file does exist
		// TODO 
		
		HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
				
		try {
			
			// open model
			try {
				workspace.open(modelFilename);
				
			} catch (Exception e) {
				final String msg = "error while running the model: "+e.getMessage();
				messages.errorUser(msg, RunNetlogoModel.class, e);
				throw new RuntimeException(msg, e);
			}
			if (progress != null) 
				progress.incProgressMade(10);
			
			// define parameters
			try {
				
				for (String varName : inputs.keySet()) {
					Object value = inputs.get(varName);
					String valueStr = NetlogoUtils.toNetlogoString(value);
					messages.debugTech("defining variable "+varName+" to "+valueStr, RunNetlogoModel.class);
					workspace.command("set " + varName+ " " + valueStr);
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

				
			// initialize model
			try {
				long timestampStart = System.currentTimeMillis();
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

				return results;
			} catch(Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("error in netlogo : "+ex.getMessage(), ex);
			} 

		} finally {
			if (workspace != null) {
				try {
					workspace.dispose();
				} catch (Exception e) {
					messages.warnTech("error while disposing the netlogo workspace: "+e.getMessage(), RunNetlogoModel.class, e);
				}
			}
		}
	}

}
