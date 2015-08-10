package fr.edf.everest.exec;

import fr.edf.everest.ModelInput;
import fr.edf.everest.ModelOutput;
import fr.edf.everest.algos.AbstractEverestModelAlgo;
import genlab.core.commons.FileUtils;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class EverestModelExec extends AbstractAlgoExecutionOneshot {

	public EverestModelExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		
	}

	public EverestModelExec() {
	}

	@Override
	public long getTimeout() {
		return 0;
	}
	
	/**
	 * reads parameters from the algo instance and returns then ready to be stored as json
	 * for the python script.
	 * @return
	 */
	protected LinkedHashMap<String,Object> encodeParameters() {
		
		LinkedHashMap<String,Object> param2value = new LinkedHashMap<>();
		
		param2value.put("model_executable_id", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_MODEL_ID));
		Integer scenarioId = (Integer)algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SCENARIO_ID);
		param2value.put("model_scenario", scenarioId>0 ? scenarioId:null);
		
		param2value.put("server_url", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SERVER_URL));
		param2value.put("server_download_url", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SERVER_URL_DOWNLOAD));
		param2value.put("server_login", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SERVER_LOGIN));
		param2value.put("server_password", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SERVER_PASSWORD));
		param2value.put("server_disable_ssl", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_SERVER_DISABLE_SSL));
		
		param2value.put("proxy_enabled", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_PROXY_ENABLED));
		param2value.put("proxy_host", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_PROXY_HOST));
		param2value.put("proxy_port", algoInst.getValueForParameter(AbstractEverestModelAlgo.PARAM_PROXY_PORT));
		
		param2value.put("debug_rest", false);
		
		return param2value;
	}
	
	/**
	 * returns the everest algo we're associated with 
	 * @return
	 */
	protected AbstractEverestModelAlgo getEverestAlgoForRun() {
		return (AbstractEverestModelAlgo) this.getAlgoInstance().getAlgo();
	}
	
	/**
	 * returns the inputs to send for simulation in the form of "for this PID, then 
	 * send this value for this attribute, etc"
	 * pid / attribute-value
	 * @return
	 */
	protected Map<String,Map<String,Object>> encodeInputs() {
		
		// retrieve the model algo we are working with
		AbstractEverestModelAlgo everestAlgo = getEverestAlgoForRun();
		
		// prepare the result
		Map<String,Map<String,Object>> pid2attribute2value = new LinkedHashMap<>();
		
		// for each input, add the corresponding value
		for (ModelInput modelInput : everestAlgo.getEverestModelInputs()) {
			// find the inputs received in Genlab for this input (that is, for all the genlab inputs)
			Map<IInputOutput<?>,Object> genlabInput2value = new HashMap<IInputOutput<?>, Object>();
			for(IInputOutput<?> genlabInput: modelInput.getGenlabInputs()) {
				genlabInput2value.put(genlabInput, getInputValueForInput(genlabInput));
			}
			
			// prepare the storage of the result
			if (!pid2attribute2value.containsKey(modelInput.PID)) {
				pid2attribute2value.put(modelInput.PID, new LinkedHashMap<String,Object>());
			}
			// store the result
			pid2attribute2value.get(modelInput.PID).put(
					modelInput.attributeTechName,
					modelInput.getEverestValue(genlabInput2value)
					);
		}
		
		return pid2attribute2value;
	}
	
	/**
	 * returns the outputs to retrieve from simulation in the form of "for this type, 
	 * this PID, read these outputs"
	 * @return
	 */
	protected Map<String,Map<String,List<String>>> encodeOutputs() {

		// retrieve the model algo we are working with
		AbstractEverestModelAlgo everestAlgo = getEverestAlgoForRun();

		// prepare the result
		Map<String,Map<String,List<String>>> entitytype2pid2outputs = new LinkedHashMap<>();
		
		// for each output in Everest 
		for (ModelOutput output: everestAlgo.getEverestModelOutputs()) {
			
			if (!entitytype2pid2outputs.containsKey(output.entityTypeName)) {
				entitytype2pid2outputs.put(output.entityTypeName, new HashMap<String,List<String>>());
			}	
			Map<String, List<String>> outputPID2kpi = entitytype2pid2outputs.get(output.entityTypeName);
			if (!outputPID2kpi.containsKey(output.PID)) {
				outputPID2kpi.put(output.PID, new LinkedList<String>());
			}
			outputPID2kpi.get(output.PID).add(output.KPI);
		}
		
		return entitytype2pid2outputs;		
	}
	
	
	/**
	 * Creates the JSON content that describes the parameters of the Everest model
	 * simulation, the inputs to transmit to Everest, the outputs to read from everest
	 * @return
	 */
	protected final String encodeParameterFile() {
		
		LinkedHashMap<String, Object> allParameters = new LinkedHashMap<>();
		allParameters.put("parameters", encodeParameters());
		allParameters.put("inputs", encodeInputs());
		allParameters.put("outputs", encodeOutputs());
		
		return JSONValue.toJSONString(allParameters);
	}
	
	/**
	 * Takes this content and stores it into a tmp file.
	 * @param content
	 * @return
	 */
	protected final File writeParameterToFile(String content) {
		
		// get a tmp file for this experiment
		File tmpFile = FileUtils.createTmpFile("EverestInputFile_", ".json");
		
		//String targetFileName = tmpFile.getAbsolutePath();
		
		// fill it with data
		try {
			PrintWriter writer = new PrintWriter(tmpFile);
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException e) {
			throw new ProgramException("unable to write into our tmp file", e);
		}

		messages.debugTech("has written inputs to file "+tmpFile.getAbsolutePath(), getClass());
		
		// done
		return tmpFile;
	}
	
	protected final File createResultFile() {
		
		File r= FileUtils.createTmpFile("EverestOutputFile_", ".json");
		messages.debugTech("asking for results into file "+r.getAbsolutePath(), getClass());
		return r;

	}

	
	protected final JSONObject readResultFromFile(File fileForResults) {
		  
		// read the content of the file
		String content;
		try {
			content = org.apache.commons.io.FileUtils.readFileToString(fileForResults);
		} catch (IOException e) {
			throw new ProgramException("error while reading content from file "+fileForResults.getName()+": "+e.getMessage(), e);
		}
		  
		Object obj=JSONValue.parse(content);
		  
		return (JSONObject)obj;
		

	}
	
	private class StreamGobbler extends Thread {
	    InputStream is;
	    String type;

	    private StreamGobbler(InputStream is, String type) {
	        this.is = is;
	        this.type = type;
	    }

	    @Override
	    public void run() {
	        try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ((line = br.readLine()) != null)
	                messages.warnTech(line, getClass());
	        }
	        catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
	    }
	}
	
	protected void runPythonScript(File fileInputs, File fileOutputs) {
		
		// check the existence of the python script
		File pythonScript = new File("fr.edf.everest/pythonSrc/test1.py");
		if (!pythonScript.exists() || !pythonScript.isFile()) {
			throw new ProgramException("unable to find the python script "+pythonScript.getPath()+" for environment "+System.getenv("PYTHONPATH"));
		}
				
		try {
			// start the process
			Process process = new ProcessBuilder(
							"python",
							pythonScript.getAbsolutePath(),
							fileInputs.getAbsolutePath(),
							fileOutputs.getAbsolutePath()
							).start();
			
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
			errorGobbler.start();

			// retrieve its stream, so we can parse error and progress online
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			boolean errorReceived = false;
			
			while ((line = br.readLine()) != null) {
				
				if (line.startsWith("PROGRESS ")) {
					Integer progress = Integer.parseInt(line.substring(9));
					if (progress > 0 && progress <= 100)
						this.progress.setProgressMade(progress);

				} else if (line.startsWith("ERROR")) {
					// decode error and relay it to GenLab (and the user)
					messages.errorUser(line.substring(6), getClass());
					// keep that in memory, the process will be flaged failure
					errorReceived = true;
				} else { 
					this.messages.traceTech(line, getClass());
				}
			}
			
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (errorReceived || process.exitValue() != 0) {
				messages.errorTech("errors detected during the execution of the program; failure.", getClass());
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
				throw new RuntimeException("error during execution");
			}
			
		} catch (IOException e1) {
			messages.errorTech("error during the execution of the program: "+e1.getMessage(), getClass(), e1);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}
		
	}
	
	/**
	 * Decodes the json output of the model and stores it into this computation result.
	 * @param modelOutputs
	 * @param res
	 */
	protected void processResults(JSONObject modelOutputs, ComputationResult res) {

		// retrieve the model algo we are working with
		AbstractEverestModelAlgo everestAlgo = getEverestAlgoForRun();

		for (ModelOutput modelOutput: everestAlgo.getEverestModelOutputs()) {
			
			// retrieve the output expected by this output
			JSONObject resultsForEntitytype = (JSONObject)modelOutputs.get(modelOutput.entityTypeName);
			JSONObject resultsForPID = (JSONObject)resultsForEntitytype.get(modelOutput.PID);
			JSONArray resultsForKPI = (JSONArray)resultsForPID.get(modelOutput.KPI); 
			
			// ask this everest output to convert that to genlab outputs
			Map<IInputOutput<?>,Object> outputsToUse = modelOutput.getOutputValuesForGenlab(resultsForKPI);
			// and copy them to our resutl
			for (IInputOutput<?> o: outputsToUse.keySet()) {
				res.setResult(o, outputsToUse.get(o));
			}
		}
	}
	
	@Override
	public void run() {
		
		try {
			this.progress.setProgressTotal(100);
			progress.setComputationState(ComputationState.STARTED);
			
			// repare result of the algo
			ComputationResult res = new ComputationResult(this.getAlgoInstance(), progress, messages);
			this.setResult(res);
			
			
			// write the inputs to a tmp file
			File fileInputs = writeParameterToFile(encodeParameterFile());
			File fileOutputs = createResultFile();
			
			// run the program 
			runPythonScript(fileInputs, fileOutputs);
			
			// read the results and use them as outputs
			JSONObject resultRaw = readResultFromFile(fileOutputs);
			processResults(resultRaw, res);
			
			// delete the file
			fileInputs.delete();
			fileOutputs.delete();
			
			// it's all right folks
			progress.setComputationState(ComputationState.FINISHED_OK);
			
		} catch (RuntimeException e) {
			messages.errorUser("exception during execution: "+e.getMessage(), getClass(), e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		}
	}

	@Override
	public void cancel() {

	}

	@Override
	public void kill() {

	}

}
