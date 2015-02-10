package genlab.r.examples.examples;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.gui.algos.GraphicalConsoleAlgo;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.gui.examples.contributors.IGenlabExample;
import genlab.r.algorithms.ExecuteRAlgorithm;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public class ExampleSimpleArithmetics implements IGenlabExample {

	public ExampleSimpleArithmetics() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		// constants
		ConstantValueInteger constantIntAlgo = new ConstantValueInteger();
		
		IAlgoInstance constantIntInstanceA = constantIntAlgo.createInstance(workflow);
		workflow.addAlgoInstance(constantIntInstanceA);
		constantIntInstanceA.setName("a");
		constantIntInstanceA.setValueForParameter(constantIntAlgo.parameterValue, 42);
		
		IAlgoInstance constantIntInstanceB = constantIntAlgo.createInstance(workflow);
		workflow.addAlgoInstance(constantIntInstanceB);
		constantIntInstanceB.setName("b");
		constantIntInstanceB.setValueForParameter(constantIntAlgo.parameterValue, 11);
		
		// r algo (core of the example)
		ExecuteRAlgorithm rAlgo = new ExecuteRAlgorithm();
		
		IAlgoInstance rInstance = rAlgo.createInstance(workflow);
		workflow.addAlgoInstance(rInstance);
		rInstance.setName("rAlgo");
		rInstance.setValueForParameter(rAlgo.PARAM_SCRIPT, "a+b");
		
		workflow.connect(
				constantIntInstanceA, constantIntAlgo.getConstantOuput(), 
				rInstance, rAlgo.INPUT_ANYTHING
				);
		workflow.connect(
				constantIntInstanceB, constantIntAlgo.getConstantOuput(), 
				rInstance, rAlgo.INPUT_ANYTHING
				);
		
		// display
		GraphicalConsoleAlgo consoleAlgo = new GraphicalConsoleAlgo();
		
		IAlgoInstance consoleInstance = consoleAlgo.createInstance(workflow);
		workflow.addAlgoInstance(consoleInstance);
		workflow.connect(
				rInstance, rAlgo.OUTPUT, 
				consoleInstance, consoleAlgo.INPUT
				);
		
	}

	@Override
	public String getFileName() {
		return "simple_arithmethics_in_R";
	}

	@Override
	public String getName() {
		return "simple arithmetics in R";
	}

	@Override
	public String getDescription() {
		return "drives a simple arithmetic operation in R";
	}

	@Override
	public void createFiles(File resourcesDirectory) {
		
	}

	@Override
	public GenlabExampleDifficulty getDifficulty() {
		return GenlabExampleDifficulty.BEGINNER;
	}

	@Override
	public Collection<IFlowType<?>> getIllustratedFlowTypes() {
		LinkedList<IFlowType<?>> l = new LinkedList<IFlowType<?>>();
		l.add(IntegerFlowType.SINGLETON);
		return l;
	}

	@Override
	public Collection<AlgoCategory> getIllustratedAlgoCategories() {
		LinkedList<AlgoCategory> l = new LinkedList<AlgoCategory>();
		l.add(ExistingAlgoCategories.ANALYSIS);
		return l;
	}

}
