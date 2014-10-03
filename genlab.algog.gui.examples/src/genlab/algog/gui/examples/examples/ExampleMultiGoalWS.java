package genlab.algog.gui.examples.examples;

import java.io.File;

import genlab.algog.algos.meta.DoubleGeneAlgo;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.GoalAlgo;
import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.gui.jfreechart.algos.AlgoGPlotAlgo;
import genlab.algog.gui.jfreechart.algos.AlgoGPlotRadarAlgo;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.GraphBasicPropertiesAlgo;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.graphstream.algos.measure.GraphStreamAverageClustering;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.gui.examples.contributors.IGenlabExample;

public class ExampleMultiGoalWS implements IGenlabExample {

	public ExampleMultiGoalWS() {
	}

	@Override
	public void fillInstance(IGenlabWorkflowInstance workflow) {
		
		// genetic algo host
		final NSGA2GeneticExplorationAlgo nsgaAlgo = new NSGA2GeneticExplorationAlgo();
		final IAlgoContainerInstance nsgaInstance = (IAlgoContainerInstance)nsgaAlgo.createInstance(workflow);
		workflow.addAlgoInstance(nsgaInstance);
		
		// genome
		final GenomeAlgo genomeAlgo = new GenomeAlgo();
		final IAlgoInstance genomeInstance = genomeAlgo.createInstance(workflow);
		genomeInstance.setName("WS");
		nsgaInstance.addChildren(genomeInstance);
		genomeInstance.setContainer(nsgaInstance);
		workflow.addAlgoInstance(genomeInstance);
		
		
		// genes
		final IntegerGeneAlgo integerGeneAlgo = new IntegerGeneAlgo();
		final DoubleGeneAlgo doubleGeneAlgo = new DoubleGeneAlgo();
		
		final IAlgoInstance geneKInstance = integerGeneAlgo.createInstance(workflow);
		geneKInstance.setName("K");
		nsgaInstance.addChildren(geneKInstance);
		geneKInstance.setContainer(nsgaInstance);
		workflow.addAlgoInstance(geneKInstance);
		geneKInstance.setValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM, 2);
		geneKInstance.setValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM, 10);
		
		workflow.connect(
				genomeInstance, 
				GenomeAlgo.OUTPUT_GENOME,
				geneKInstance,
				IntegerGeneAlgo.INPUT_GENOME
				);
		
		final IAlgoInstance geneNInstance = integerGeneAlgo.createInstance(workflow);
		geneNInstance.setName("k");
		nsgaInstance.addChildren(geneNInstance);
		geneNInstance.setContainer(nsgaInstance);
		workflow.addAlgoInstance(geneNInstance);
		geneNInstance.setValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM, 10);
		geneNInstance.setValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM, 500);
		
		workflow.connect(
				genomeInstance, 
				GenomeAlgo.OUTPUT_GENOME,
				geneNInstance,
				IntegerGeneAlgo.INPUT_GENOME
				);
		
		final IAlgoInstance genePInstance = doubleGeneAlgo.createInstance(workflow);
		genePInstance.setName("p");
		nsgaInstance.addChildren(genePInstance);
		genePInstance.setContainer(nsgaInstance);
		workflow.addAlgoInstance(genePInstance);
		genePInstance.setValueForParameter(DoubleGeneAlgo.PARAM_MINIMUM, 0.0);
		genePInstance.setValueForParameter(DoubleGeneAlgo.PARAM_MAXIMUM, 1.0);
		
		workflow.connect(
				genomeInstance, 
				GenomeAlgo.OUTPUT_GENOME,
				genePInstance,
				DoubleGeneAlgo.INPUT_GENOME
				);
		
		// generator
		final WattsStrogatzAlgo wsAlgo = new WattsStrogatzAlgo();
		final IAlgoInstance wsInstance = wsAlgo.createInstance(workflow);
		wsInstance.setName("ws");
		wsInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(wsInstance);
		workflow.addAlgoInstance(wsInstance);
		
		workflow.connect(
				geneKInstance, 
				IntegerGeneAlgo.OUTPUT_VALUE, 
				wsInstance, 
				WattsStrogatzAlgo.INPUT_K
				);
		workflow.connect(
				geneNInstance, 
				IntegerGeneAlgo.OUTPUT_VALUE, 
				wsInstance, 
				WattsStrogatzAlgo.INPUT_N
				);
		workflow.connect(
				genePInstance, 
				DoubleGeneAlgo.OUTPUT_VALUE, 
				wsInstance, 
				WattsStrogatzAlgo.INPUT_P
				);
		
		
		// measure
		final GraphStreamAverageClustering clusteringAlgo = new GraphStreamAverageClustering();
		final GraphBasicPropertiesAlgo graphPropertiesAlgo = new GraphBasicPropertiesAlgo();

		// measure clustering
		final IAlgoInstance clusteringInstance = clusteringAlgo.createInstance(workflow);
		clusteringInstance.setName("clustering");
		clusteringInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(clusteringInstance);
		workflow.addAlgoInstance(clusteringInstance);
		workflow.connect(
				wsInstance, WattsStrogatzAlgo.OUTPUT_GRAPH,
				clusteringInstance, GraphStreamAverageClustering.INPUT_GRAPH
				);

		
		final GoalAlgo goalAlgo = new GoalAlgo();
		final ConstantValueDouble constantDoubleAlgo = new ConstantValueDouble();

		// set goal 1: clustering
		{
			final IAlgoInstance goalClustering = goalAlgo.createInstance(workflow);
			goalClustering.setName("g_clustering");
			goalClustering.setContainer(nsgaInstance);
			nsgaInstance.addChildren(goalClustering);
			workflow.addAlgoInstance(goalClustering);

			workflow.connect(
					clusteringInstance, GraphStreamAverageClustering.OUTPUT_AVERAGE_CLUSTERING,
					goalClustering, GoalAlgo.INPUT_VALUE
					);
			
			final IAlgoInstance constantDoubleClustering = constantDoubleAlgo.createInstance(workflow);
			constantDoubleClustering.setContainer(nsgaInstance);
			nsgaInstance.addChildren(constantDoubleClustering);
			workflow.addAlgoInstance(constantDoubleClustering);
			constantDoubleClustering.setValueForParameter(constantDoubleAlgo.getConstantParameter(), 0.17);

			workflow.connect(
					constantDoubleClustering, ConstantValueDouble.OUTPUT,
					goalClustering, GoalAlgo.INPUT_TARGET
					);
		}
		
		// measure density
		final IAlgoInstance graphPropertiesInstance = graphPropertiesAlgo.createInstance(workflow);
		graphPropertiesInstance.setName("density");
		graphPropertiesInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(graphPropertiesInstance);
		workflow.addAlgoInstance(graphPropertiesInstance);
		workflow.connect(
				wsInstance, WattsStrogatzAlgo.OUTPUT_GRAPH,
				graphPropertiesInstance, GraphBasicPropertiesAlgo.INPUT_GRAPH
				);
		
		
		// set goal density
		{
			final IAlgoInstance goalDensity = goalAlgo.createInstance(workflow);
			goalDensity.setName("g_density");
			goalDensity.setContainer(nsgaInstance);
			nsgaInstance.addChildren(goalDensity);
			workflow.addAlgoInstance(goalDensity);
			
			workflow.connect(
					graphPropertiesInstance, GraphBasicPropertiesAlgo.OUTPUT_DENSITY,
					goalDensity, GoalAlgo.INPUT_VALUE
					);
			
			final IAlgoInstance constantDoubleDensity = constantDoubleAlgo.createInstance(workflow);
			//constantDoubleClustering.setName("g_clustering");
			constantDoubleDensity.setContainer(nsgaInstance);
			nsgaInstance.addChildren(constantDoubleDensity);
			workflow.addAlgoInstance(constantDoubleDensity);
			constantDoubleDensity.setValueForParameter(constantDoubleAlgo.getConstantParameter(), 0.021);

			workflow.connect(
					constantDoubleDensity, ConstantValueDouble.OUTPUT,
					goalDensity, GoalAlgo.INPUT_TARGET
					);
		}
		
		// add displays
		{
			final AlgoGPlotAlgo algogPlotAlgo = new AlgoGPlotAlgo();
			final IAlgoInstance algogPlotInstance = algogPlotAlgo.createInstance(workflow);
			workflow.addAlgoInstance(algogPlotInstance);
			
			workflow.connect(
					nsgaInstance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					algogPlotInstance,
					AlgoGPlotAlgo.INPUT_TABLE
					);
		}
		
		{
			final AlgoGPlotRadarAlgo algoPlotRadarAlgo = new AlgoGPlotRadarAlgo();
			final IAlgoInstance algogPlotInstance = algoPlotRadarAlgo.createInstance(workflow);
			workflow.addAlgoInstance(algogPlotInstance);
			
			workflow.connect(
					nsgaInstance, 
					NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					algogPlotInstance,
					AlgoGPlotRadarAlgo.INPUT_TABLE
					);
		}
		
		
	}

	@Override
	public String getFileName() {
		return "geneticalgo_multigoal_wattsstrogatz";
	}

	@Override
	public String getName() {
		return "multi-goal genetic algo applied to Watts Strogatz";
	}

	@Override
	public String getDescription() {
		return "A genetic algorithm optimizing a Watts Strogatz network generator";
	}

	@Override
	public void createFiles(File resourcesDirectory) {
		
	}

	@Override
	public GenlabExampleDifficulty getDifficulty() {
		return GenlabExampleDifficulty.ADVANCED;
	}

}
