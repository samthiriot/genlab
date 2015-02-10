package genlab.graphstream.examples;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.graphstream.algos.generators.BarabasiAlbertAlgo;
import genlab.graphstream.ui.algos.GraphDisplayAlgo;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.gui.examples.contributors.IGenlabExample;

public final class BAExample1 implements IGenlabExample {

	public BAExample1() {

	}
	

	@Override
	public String getName() {
		return "Barabasi Albert scale-free example";
	}

	@Override
	public String getDescription() {
		return "Shows how a Barabasi-Albert generator can be used with statistical analysis an vizualization of the graph.";
	}
	

	@Override
	public String getFileName() {
		return "gs_barabasi_albert_example_1";
	}



	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		// ref algos
		BarabasiAlbertAlgo ba = new BarabasiAlbertAlgo();
		ConstantValueInteger constantInt = new ConstantValueInteger();
		StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
		GraphDisplayAlgo displayAlgo = new GraphDisplayAlgo();
		
		// create instances inside the workflow
		{	
			IAlgoInstance baInstance = ba.createInstance(workflow);
			workflow.addAlgoInstance(baInstance);
			
			{
				IAlgoInstance constantN = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantN);
				constantN.setValueForParameter(constantInt.getConstantParameter(), 500);
				workflow.connect(
						 constantN,
						 ConstantValueInteger.OUTPUT,
						 baInstance,
						 BarabasiAlbertAlgo.PARAM_N
				);
			}
			
			{
				IAlgoInstance constantM = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantM);
				constantM.setValueForParameter(constantInt.getConstantParameter(), 1);
				workflow.connect(
						constantM,
						ConstantValueInteger.OUTPUT,
						baInstance,
						BarabasiAlbertAlgo.PARAM_M
	
				);
			}
			
			
			IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
			workflow.addAlgoInstance(stdOutInstance);
			workflow.connect(
					baInstance.getOutputInstanceForOutput(BarabasiAlbertAlgo.OUTPUT_GRAPH), 
					stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
					);
			
			IAlgoInstance displayDisplayAlgo = displayAlgo.createInstance(workflow);
			workflow.addAlgoInstance(displayDisplayAlgo);
			workflow.connect(
					baInstance,
					BarabasiAlbertAlgo.OUTPUT_GRAPH,
					displayDisplayAlgo,
					GraphDisplayAlgo.INPUT_GRAPH
					);
		}
		
	}


	@Override
	public GenlabExampleDifficulty getDifficulty() {
		return GenlabExampleDifficulty.BEGINNER;
	}


	@Override
	public void createFiles(File resourcesDirectory) {
		
	}


	@Override
	public Collection<IFlowType<?>> getIllustratedFlowTypes() {
		return new LinkedList<IFlowType<?>>() {{ add(SimpleGraphFlowType.SINGLETON); }};
	}

	@Override
	public Collection<AlgoCategory> getIllustratedAlgoCategories() {
		return new LinkedList<AlgoCategory>() {{ add(ExistingAlgoCategories.GENERATORS_GRAPHS); }};
	}


}
