package genlab.graphstream.examples;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.projects.IGenlabProject;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.graphstream.algos.measure.GraphStreamAverageClustering;
import genlab.graphstream.ui.algos.GraphDisplayAlgo;
import genlab.gui.algos.GraphicalConsoleAlgo;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.gui.examples.contributors.IGenlabExample;

public final class StatsExample1 implements IGenlabExample {

	public StatsExample1() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public String getName() {
		return "Statistics example";
	}

	@Override
	public String getDescription() {
		return "Shows how a to drive statistical analysis of graphs using graphstream algorithms.";
	}
	

	@Override
	public String getFileName() {
		return "gs_stats_example_1";
	}



	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		// ref algos
		WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
		ConstantValueInteger constantInt = new ConstantValueInteger();
		ConstantValueDouble constantDouble = new ConstantValueDouble();
		StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
		GraphDisplayAlgo displayAlgo = new GraphDisplayAlgo();
		GraphStreamAverageClustering clusteringAlgo = new GraphStreamAverageClustering();
		GraphicalConsoleAlgo consoleAlgo = new GraphicalConsoleAlgo();
		
		// create instances inside the workflow
		{	
			IAlgoInstance wsInstance = ws.createInstance(workflow);
			workflow.addAlgoInstance(wsInstance);
			
			{
				IAlgoInstance constantN = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantN);
				constantN.setValueForParameter(constantInt.getConstantParameter(), 500);
				workflow.connect(
						 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
				);
			}
			
			{
				IAlgoInstance constantK = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantK);
				constantK.setValueForParameter(constantInt.getConstantParameter(), 2);
				workflow.connect(
						constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
				);
			}
			
			{
				IAlgoInstance constantP = constantDouble.createInstance(workflow);
				workflow.addAlgoInstance(constantP);
				constantP.setValueForParameter(constantDouble.getConstantParameter(), 0.05);
				workflow.connect(
						constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
				);
			}
			
			IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
			workflow.addAlgoInstance(stdOutInstance);
			workflow.connect(
					wsInstance.getOutputInstanceForOutput(WattsStrogatzAlgo.OUTPUT_GRAPH), 
					stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
					);
			
			IAlgoInstance displayDisplayAlgo = displayAlgo.createInstance(workflow);
			workflow.addAlgoInstance(displayDisplayAlgo);
			workflow.connect(
					wsInstance,
					WattsStrogatzAlgo.OUTPUT_GRAPH,
					displayDisplayAlgo,
					GraphDisplayAlgo.INPUT_GRAPH
					);
			

			// connect clustering to graph
			IAlgoInstance clusteringInstance = clusteringAlgo.createInstance(workflow);
			workflow.addAlgoInstance(clusteringInstance);
			workflow.connect(
					wsInstance, 
					WattsStrogatzAlgo.OUTPUT_GRAPH,
					clusteringInstance,
					GraphStreamAverageClustering.INPUT_GRAPH
					);
			
			// connect display to all
			IAlgoInstance consoleInstance = consoleAlgo.createInstance(workflow);
			workflow.addAlgoInstance(consoleInstance);
			workflow.connect(
					clusteringInstance, 
					GraphStreamAverageClustering.OUTPUT_AVERAGE_CLUSTERING,
					consoleInstance,
					GraphicalConsoleAlgo.INPUT
					);
		}
		
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
		return new LinkedList<IFlowType<?>>() {{ add(SimpleGraphFlowType.SINGLETON); }};
	}
	
	@Override
	public Collection<AlgoCategory> getIllustratedAlgoCategories() {
		return new LinkedList<AlgoCategory>() {{ add(ExistingAlgoCategories.GENERATORS_GRAPHS); add(ExistingAlgoCategories.ANALYSIS_GRAPH); }};
	}


}
