package genlab.gui.examples.contributors;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IConstantAlgo;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueFile;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.ConstantValueString;

import java.io.File;

public abstract class AbstractBasicExample implements IGenlabExample {

	public AbstractBasicExample() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createFiles(File resourcesDirectory) {

	}

	private IAlgoInstance createAndLinkConstantAlgo(
			IConstantAlgo algo, 
			IGenlabWorkflowInstance workflow, 
			Object value, 
			IAlgoInstance algoTo, IInputOutput<?> inputTo) {
		
		
		IAlgoInstance inst = algo.createInstance(workflow);
		inst.setValueForParameter(algo.getConstantParameter(), value);
		workflow.addAlgoInstance(inst);
		
		workflow.connect(
				inst, 
				algo.getConstantOuput(),
				algoTo,
				inputTo
				);
		
		return inst;
		
	}
	
	protected IAlgoInstance createAndLinkConstantString(IGenlabWorkflowInstance workflow, String value, IAlgoInstance algoTo, IInputOutput<String> inputTo) {
		
		return createAndLinkConstantAlgo(
				new ConstantValueString(), 
				workflow, 
				value, 
				algoTo, 
				inputTo
				);
		
	}
	

	
	protected IAlgoInstance createAndLinkConstantString(IGenlabWorkflowInstance workflow, File value, IAlgoInstance algoTo, IInputOutput<File> inputTo) {
		
		return createAndLinkConstantAlgo(
				new ConstantValueFile(), 
				workflow, 
				value, 
				algoTo, 
				inputTo
				);
		
	}
	
	protected IAlgoInstance createAndLinkConstantInteger(IGenlabWorkflowInstance workflow, Integer value, IAlgoInstance algoTo, IInputOutput<?> inputTo) {
		
		return createAndLinkConstantAlgo(
				new ConstantValueInteger(), 
				workflow, 
				value, 
				algoTo, 
				inputTo
				);
		
	}
	

	protected IAlgoInstance createAndLinkConstantDouble(IGenlabWorkflowInstance workflow, Double value, IAlgoInstance algoTo, IInputOutput<Double> inputTo) {
		
		return createAndLinkConstantAlgo(
				new ConstantValueDouble(), 
				workflow, 
				value, 
				algoTo, 
				inputTo
				);
		
	}
	
	protected IAlgoInstance createAndLinkConstantFile(IGenlabWorkflowInstance workflow, File value, IAlgoInstance algoTo, IInputOutput<Double> inputTo) {
		
		return createAndLinkConstantAlgo(
				new ConstantValueFile(), 
				workflow, 
				value, 
				algoTo, 
				inputTo
				);
		
	}
		
	
}
