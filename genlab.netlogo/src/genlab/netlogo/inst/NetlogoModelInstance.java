package genlab.netlogo.inst;

import genlab.core.model.instance.AlgoInstanceCreatingInputsOutputs;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.netlogo.algos.NetlogoModelAlgo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nlogo.api.CompilerException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoList;
import org.nlogo.api.ModelType;
import org.nlogo.headless.HeadlessWorkspace;

@SuppressWarnings("serial")
public class NetlogoModelInstance extends AlgoInstanceCreatingInputsOutputs {

	private transient Map<String,IInputOutput<?>> netlogoVariable2input = new HashMap<String,IInputOutput<?>>();
	private transient Map<String,IInputOutput<?>> netlogoVariable2output = new HashMap<String,IInputOutput<?>>();
	
	public NetlogoModelInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		addParametersListener(this);
	}

	public NetlogoModelInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		addParametersListener(this);
	}

	protected void removeAllInputs() {
		while (!localInputs.isEmpty()) {
			IInputOutput input = localInputs.iterator().next();
			removeLocalInput(input);
		}
	}
	
	protected void removeAllOutputs() {
		while (!localOutputs.isEmpty()) {
			IInputOutput localOutput = localOutputs.iterator().next();
			removeLocalOutput(localOutput);
		}
	}
	protected void removeAllInputsAndOutputs() {
		removeAllInputs();
		removeAllOutputs();
	}
	
	protected List<?> loadListFromWorkspace(HeadlessWorkspace workspace, String variableName) {
		
		Object inputsObj = null;
		try {
			inputsObj = workspace.report(variableName);
		} catch (CompilerException | LogoException e) {
			// TODO
			GLLogger.errorTech("unable to find the variable "+variableName+"; please declare "+variableName+" returning a list", getClass(), e);
			return null;
		} 
		try {
			LogoList inputsLL = (LogoList)inputsObj;
			return inputsLL;
		} catch (ClassCastException e) {
			GLLogger.errorTech(variableName+" is not a list as expected; please declare "+variableName+" returning a list", getClass(), e);
			return null;
		}
		
	}
	
	public final static String NETLOGO_VARIABLE_INPUT_VARIABLES = "genlab-input-types";
	
	@Override
	protected void removeLocalInput(IInputOutput<?> i) {
		super.removeLocalInput(i);
		netlogoVariable2input.remove(i);
	}
	
	@Override
	protected void removeLocalOutput(IInputOutput<?> o) {
		super.removeLocalOutput(o);
		netlogoVariable2output.remove(o);
	}
	
	protected void attemptToLoadInputs(HeadlessWorkspace workspace) {
		
		// then ask the workplace for the list of inputs
		List<?> inputNames = loadListFromWorkspace(workspace, "genlab-inputs");
		if (inputNames == null) {
			removeAllInputs();
			return;
		}
		GLLogger.traceTech("the netlogo model declares for inputs "+inputNames, getClass());
		
		// query for each list of input its value (which should then be the default value now)
		Map<String,Object> inputName2value = new HashMap<String,Object>();
		for (Object o: inputNames) {
			final String inputName = (String)o;
			try {
				Object inputValue = workspace.report(inputName);
				inputName2value.put(inputName, inputValue);
				GLLogger.traceTech("input "+inputName+" has for type "+inputValue.getClass(), getClass());
						
			} catch (CompilerException | LogoException e) {
				GLLogger.errorUser("unable to find the value of input '"+inputName+"'", getClass());
			}
		}
		// then for each input identified, create the corresponding input
		for (Entry<String,Object> entry: inputName2value.entrySet()) {
			IInputOutput<?> previouslyDeclaredParameter = netlogoVariable2input.get(entry.getKey());
			if (entry.getValue().getClass().isAssignableFrom(Integer.class)) {
				if (previouslyDeclaredParameter != null && !(previouslyDeclaredParameter instanceof IntegerInOut)) {
					// it already existed with another type, let's remove it
					removeLocalInput(previouslyDeclaredParameter);
				}
				declareLocalInput(
						new IntegerInOut(
								"in_"+entry.getKey(), 
								entry.getKey(), 
								"detected from Netlogo",
								(Integer)entry.getValue()
								)
						);
				
			} else if (entry.getValue().getClass().isAssignableFrom(Double.class)) {
				if (previouslyDeclaredParameter != null && !(previouslyDeclaredParameter instanceof DoubleInOut)) {
					// it already existed with another type, let's remove it
					removeLocalInput(previouslyDeclaredParameter);
				}
				declareLocalInput(
						new DoubleInOut(
								"in_"+entry.getKey(), 
								entry.getKey(), 
								"detected from Netlogo",
								(Double)entry.getValue()
								)
						);
				
			} else {
				GLLogger.warnUser("Unable to deal with the type '"+entry.getValue().getClass().getCanonicalName()+"' detected for input '"+entry.getKey()+"'; it will be ignored.", getClass());
			}
			// TODO other cases ? 
		}
				
	}

	protected void attemptToLoadOutputs(HeadlessWorkspace workspace) {
		
		// then ask the workplace for the list of inputs
		List<?> outputNames = loadListFromWorkspace(workspace, "genlab-outputs");
		if (outputNames == null) {
			removeAllOutputs();
			return;
		}
		GLLogger.traceTech("the netlogo model declares for outputs "+outputNames, getClass());
		
		// query for each list of input its value (which should then be the default value now)
		Map<String,Class<?>> outputName2class = new HashMap<String,Class<?>>();
		for (Object o: outputNames) {
			final String inputName = (String)o;
			try {
				Object inputValue = workspace.report(inputName);
				outputName2class.put(inputName, inputValue.getClass());
				GLLogger.traceTech("output "+inputName+" has for type "+inputValue.getClass(), getClass());
						
			} catch (CompilerException | LogoException e) {
				e.printStackTrace();
				GLLogger.errorUser("unable to find the value of input '"+inputName+"'", getClass());
			}
		}
		// then for each input identified, create the corresponding input
		for (Entry<String,Class<?>> entry: outputName2class.entrySet()) {
			IInputOutput<?> previouslyDeclaredParameter = netlogoVariable2output.get(entry.getKey());
			if (entry.getValue().isAssignableFrom(Integer.class)) {
				if (previouslyDeclaredParameter != null && !(previouslyDeclaredParameter instanceof IntegerInOut)) {
					// it already existed with another type, let's remove it
					removeLocalOutput(previouslyDeclaredParameter);
				}
				declareLocalOutput(
						new IntegerInOut("out_"+entry.getKey(), entry.getKey(), "detected from Netlogo")
						);
				
			} else if (entry.getValue().isAssignableFrom(Double.class)) {
				if (previouslyDeclaredParameter != null && !(previouslyDeclaredParameter instanceof DoubleInOut)) {
					// it already existed with another type, let's remove it
					removeLocalOutput(previouslyDeclaredParameter);
				}
				declareLocalOutput(
						new DoubleInOut("out_"+entry.getKey(), entry.getKey(), "detected from Netlogo")
						);
				
			} else {
				GLLogger.warnUser("Unable to deal with the type '"+entry.getValue().getCanonicalName()+"' detected for output '"+entry.getKey()+"'; it will be ignored.", getClass());
			}
			// TODO other cases ? 
		}
				
	}

	/*
	class ThreadUpdateToParameters extends Thread {
		
		protected boolean cancel = false;
		protected final File modelFile;
		
		public ThreadUpdateToParameters(File modelFile) {
			
			this.modelFile = modelFile;
			
			setDaemon(true);
			setPriority(MIN_PRIORITY);
		}
		
		@Override
		public void run() {
			
			GLLogger.traceTech("starting update of parameters for the new model", NetlogoModelInstance.class);
			HeadlessWorkspace workspace = null;
			try {
				
				if (cancel)
					return;
				
				// create a workspace
				workspace = HeadlessWorkspace.newInstance();
				
				if (cancel)
					return;
				
				// open this file
				try {
					workspace.open(modelFile.getAbsolutePath());
				} catch (IOException e) {
					GLLogger.debugTech("unable to open model", getClass()); 
					removeAllInputsAndOutputs();
					return;
				} catch (CompilerException | LogoException e) {
					GLLogger.errorTech("unable to compile model: "+e.getMessage(), getClass(), e); 
					removeAllInputsAndOutputs();
					return;
				}
				
				if (cancel)
					return;
			
				attemptToLoadInputs(workspace);
				
				if (cancel)
					return;
			
				// TODO attempt to load outputs !
				
			} finally {
				if (workspace != null)
					try {
						workspace.dispose();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			GLLogger.traceTech("finished the update of the parameters for the new model", NetlogoModelInstance.class);
		}
		
		/**
		 * cancels run 
		 *
		public void cancelUpdate() {
			cancel = true;
		}
		
	}
	protected ThreadUpdateToParameters threadOfAdapation = null;
	
	protected void adaptInputsAndOutputsToFileAsync(File f) {
		if (threadOfAdapation != null) {
			threadOfAdapation.cancelUpdate();
		}
		threadOfAdapation = new ThreadUpdateToParameters(f);
		threadOfAdapation.start();
	}
	*/
	
	protected void adaptInputsAndOutputsToFile(File modelFile) {
		
		GLLogger.traceTech("starting update of parameters for the new model", NetlogoModelInstance.class);
		HeadlessWorkspace workspace = null;
		try {
			
			// create a workspace
			workspace = HeadlessWorkspace.newInstance();
			
			// open this file
			try {
				workspace.open(modelFile.getAbsolutePath());
			} catch (IOException e) {
				GLLogger.debugTech("unable to open model", getClass()); 
				removeAllInputsAndOutputs();
				return;
			} catch (CompilerException | LogoException e) {
				GLLogger.errorTech("unable to compile model: "+e.getMessage(), getClass(), e); 
				removeAllInputsAndOutputs();
				return;
			}
		
			attemptToLoadInputs(workspace);
		
			attemptToLoadOutputs(workspace);
			

		} finally {
			if (workspace != null)
				try {
					workspace.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		GLLogger.traceTech("finished the update of the parameters for the new model", NetlogoModelInstance.class);	
	}

	@Override
	protected void refreshFromParameters(String parameterId, Object novelValue) {

		if (!parameterId.equals(NetlogoModelAlgo.PARAM_NETLOGO_MODEL.getId()))
			return;
		
		File f = (File)novelValue;
		if (!f.exists()) {
			GLLogger.warnTech("unable to read file "+f, getClass());
			return;
		}
		adaptInputsAndOutputsToFile(f);
		
		//adaptInputsAndOutputsToFileAsync(f);		
		
	}

}
