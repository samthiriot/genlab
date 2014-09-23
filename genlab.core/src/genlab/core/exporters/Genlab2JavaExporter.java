package genlab.core.exporters;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.Parameter;

public class Genlab2JavaExporter {

	private Genlab2JavaExporter() {
		
	}
	
	
	protected static String getJavaForValue(Object value) {
	
		if (value instanceof String) {
			return "\""+value+"\"";
		}
		

		if (value instanceof File) {
			return "new File(\""+value+"\")";
		}
		
		// TODO should be part of the type of each param ? 
		
		return value.toString();
	}
	
	public static String generateJavaForWorkflowCreation(IGenlabWorkflowInstance workflow) {
	
		StringBuffer sb = new StringBuffer();
		
		
		// first build the list of the IAlgo used there
		Set<IAlgo> usedAlgos = new LinkedHashSet<IAlgo>(workflow.getAlgoInstances().size());
		for (IAlgoInstance ai : workflow.getAlgoInstances()) {
			usedAlgos.add(ai.getAlgo());
		}
		
		// list of the variable names already used
		Set<String> usedVarNames = new HashSet<String>();
		
		// imports
		sb.append("import genlab.core.model.instance.IGenlabWorkflowInstance;\n");
		sb.append("import genlab.core.model.instance.IAlgoInstance;\n");
		sb.append("import genlab.core.model.instance.IAlgoContainerInstance;\n");
		// useless ! sb.append("import genlab.core.model.instance.IConnection;\n");

		for (IAlgo algo: usedAlgos) {
			// declaration type
			sb.append("import ");
			sb.append(algo.getClass().getName());
			sb.append(";\n");
		}
		sb.append("\n\n");


		// TODO workflow ? 
		sb.append("IGenlabWorkflowInstance workflow = null; // TODO\n");
		
		// declare the algos used
		sb.append("// declare the use of the algorithms required for the workflow\n");
		Map<IAlgo,String> algo2varName = new HashMap<IAlgo, String>(usedAlgos.size());
		for (IAlgo algo: usedAlgos) {
			// declaration type
			sb.append("final ");
			sb.append(algo.getClass().getSimpleName());
			sb.append(" ");
			// variable name
			// TODO store in the list ? 
			String varName = algo.getClass().getSimpleName().substring(0, 1).toLowerCase() + algo.getClass().getSimpleName().substring(1); 
			algo2varName.put(algo, varName);
			usedVarNames.add(varName);
			sb.append(varName);
			// init
			sb.append(" = new ");
			sb.append(algo.getClass().getSimpleName());
			sb.append("();");
			sb.append("\n");
		}
		sb.append("\n\n");
		
		// declare the instances of each algo
		sb.append("// declare the instances of algorithms in the workflow\n");
		Map<IAlgoInstance,String> algoInstance2varName = new HashMap<IAlgoInstance, String>(workflow.getAlgoInstances().size());
		for (IAlgoInstance ai : workflow.getAlgoInstances()) {
			
			// declaration
			sb.append("final IAlgoInstance ");
			// variable name
			String varName = algoInstance2varName.get(ai);
			if (varName == null) {
				String varNameBase = ai.getAlgo().getClass().getSimpleName().substring(0,1).toLowerCase() + ai.getAlgo().getClass().getSimpleName().substring(1)+"Instance";
				int sub = 1;
				varName = varNameBase;
				while (usedVarNames.contains(varName)) {
					varName = varNameBase+(sub++);
				}
				usedVarNames.add(varName);
				algoInstance2varName.put(ai, varNameBase);
			}
			sb.append(varName);
			// init
			sb.append(" = ");
			sb.append(algo2varName.get(ai.getAlgo()));
			sb.append(".createInstance(workflow);\n");
			
			
			// add to workflow
			sb.append("workflow.addAlgoInstance(");
			sb.append(varName);
			sb.append(");\n");
			
			// TODO container ! is it the right order ?
			if (ai.getContainer() != null && ai.getContainer() != workflow) {
				sb.append(varName);
				sb.append(".setContainer((IAlgoContainerInstance)");
				sb.append(algoInstance2varName.get(ai.getContainer()));
				sb.append(");\n");
			}
			
			// declare parameters
			for (Parameter<?> p : ai.getParameters()) {
				sb.append(varName);
				sb.append(".setValueForParameter(");
				sb.append("\n\t");
				sb.append("\"").append(p.getId()).append("\",");
				sb.append("\n\t");
				sb.append(getJavaForValue(ai.getValueForParameter(p)));
				sb.append("\n);\n");
				
			}
			// empty line
			sb.append("\n");
			
		}
		
		sb.append("\n\n");
		
		
		// declare connections
		sb.append("// declare connections between algo instances\n");

		for (IConnection c: workflow.getConnections()) {
			final String algoInstanceFrom = algoInstance2varName.get(c.getFrom().getAlgoInstance());
			final String algoInstanceTo = algoInstance2varName.get(c.getTo().getAlgoInstance());
			sb.append("workflow.connect(\n");
			sb.append("\t");
			sb.append(algoInstanceFrom)
				.append(".getOutputInstanceForOutput(\"")
				.append(c.getFrom().getMeta().getId())
				.append("\"),\n");
			sb.append("\t");
			sb.append(algoInstanceTo)
				.append(".getInputInstanceForInput(\"")
				.append(c.getTo().getMeta().getId())
				.append("\")\n");
			sb.append(");\n");			
		}
		
		return sb.toString();
	}
	
	

}
