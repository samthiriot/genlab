package genlab.algog.testing;

import java.io.File;

import genlab.algog.algos.meta.BooleanGeneAlgo;
import genlab.algog.algos.meta.DoubleGeneAlgo;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.GoalAlgo;
import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.GraphBasicPropertiesAlgo;
import genlab.core.model.meta.basics.algos.WriteTableCSV;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.graphstream.algos.measure.GraphStreamAverageClustering;
import genlab.testing.commons.BasicTestWorkflow;

public class CaseAlgoGnetwork extends BasicTestWorkflow {

	final Double targetClustering;
	final Double targetDensity;
	final Double probaMutationP;
	final Double probaMutationK;
	final Double probaMutationN;
	final Integer generations;
	final Integer popSize;
	final String destFileAll;
	final String destFilePareto;
	
	

	public CaseAlgoGnetwork(Double targetClustering, Double targetDensity,
			Double probaMutationP, Double probaMutationK,
			Double probaMutationN, Integer generations, Integer popSize,
			String destFileAll, String destFilePareto) {
		super();
		this.targetClustering = targetClustering;
		this.targetDensity = targetDensity;
		this.probaMutationP = probaMutationP;
		this.probaMutationK = probaMutationK;
		this.probaMutationN = probaMutationN;
		this.generations = generations;
		this.popSize = popSize;
		this.destFileAll = destFileAll;
		this.destFilePareto = destFilePareto;
	}



	@Override
	protected void populateWorkflow(IGenlabWorkflowInstance workflow) {
		
		// ref algos
		final NSGA2GeneticExplorationAlgo nsgaAlgo = new NSGA2GeneticExplorationAlgo();
		
		final GenomeAlgo genomeAlgo = new GenomeAlgo();
		
		final BooleanGeneAlgo booleanGeneAlgo = new BooleanGeneAlgo();
		final IntegerGeneAlgo integerGeneAlgo = new IntegerGeneAlgo();
		final DoubleGeneAlgo doubleGeneAlgo = new DoubleGeneAlgo();
		
		final GoalAlgo goalAlgo = new GoalAlgo();
		
		final ConstantValueDouble constantDoubleAlgo = new ConstantValueDouble();
		
		final WattsStrogatzAlgo wsAlgo = new WattsStrogatzAlgo();
		
		final GraphStreamAverageClustering clusteringAlgo = new GraphStreamAverageClustering();
		final GraphBasicPropertiesAlgo graphPropertiesAlgo = new GraphBasicPropertiesAlgo();

		final WriteTableCSV writeTableCSVAlgo = new WriteTableCSV();

		// create algo instances
		
		// NSGA
		final IAlgoContainerInstance nsgaInstance = (IAlgoContainerInstance)nsgaAlgo.createInstance(workflow);
		workflow.addAlgoInstance(nsgaInstance);
		nsgaInstance.setValueForParameter(NSGA2GeneticExplorationAlgo.PARAM_SIZE_POPULATION, popSize);
		nsgaInstance.setValueForParameter(NSGA2GeneticExplorationAlgo.PARAM_STOP_MAXITERATIONS, generations);
		
		// WS
		final IAlgoInstance wsInstance = wsAlgo.createInstance(workflow);
		workflow.addAlgoInstance(wsInstance);
		wsInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(wsInstance);
		
		// genome
		final IAlgoInstance genomeWSInstance = genomeAlgo.createInstance(workflow);
		workflow.addAlgoInstance(genomeWSInstance);
		genomeWSInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(genomeWSInstance);
		genomeWSInstance.setName("WS_genome");
	
		
		// gene N
		{
			final IAlgoInstance integerInstanceN = integerGeneAlgo.createInstance(workflow);
			workflow.addAlgoInstance(integerInstanceN);
			integerInstanceN.setContainer(nsgaInstance);
			nsgaInstance.addChildren(integerInstanceN);
			integerInstanceN.setName("N");
			workflow.connect(
					genomeWSInstance, GenomeAlgo.OUTPUT_GENOME, 
					integerInstanceN, IntegerGeneAlgo.INPUT_GENOME
					);
			workflow.connect(
					integerInstanceN, IntegerGeneAlgo.OUTPUT_VALUE, 
					wsInstance, WattsStrogatzAlgo.INPUT_N
					);
			integerInstanceN.setValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM, 50);
			integerInstanceN.setValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM, 1000);
			integerInstanceN.setValueForParameter(IntegerGeneAlgo.PARAM_PROBA_MUTATION, probaMutationN);

		}
		
		// gene K
		{
			final IAlgoInstance integerInstanceK = integerGeneAlgo.createInstance(workflow);
			workflow.addAlgoInstance(integerInstanceK);
			integerInstanceK.setContainer(nsgaInstance);
			nsgaInstance.addChildren(integerInstanceK);
			integerInstanceK.setName("k");
			workflow.connect(
					genomeWSInstance, GenomeAlgo.OUTPUT_GENOME, 
					integerInstanceK, IntegerGeneAlgo.INPUT_GENOME
					);
			workflow.connect(
					integerInstanceK, IntegerGeneAlgo.OUTPUT_VALUE, 
					wsInstance, WattsStrogatzAlgo.INPUT_K
					);
			integerInstanceK.setValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM, 2);
			integerInstanceK.setValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM, 60);
			integerInstanceK.setValueForParameter(IntegerGeneAlgo.PARAM_PROBA_MUTATION, probaMutationK);

		}
		
		// gene p
		{
			final IAlgoInstance doubleInstanceP = doubleGeneAlgo.createInstance(workflow);
			workflow.addAlgoInstance(doubleInstanceP);
			doubleInstanceP.setContainer(nsgaInstance);
			nsgaInstance.addChildren(doubleInstanceP);
			doubleInstanceP.setName("p");
			workflow.connect(
					genomeWSInstance, GenomeAlgo.OUTPUT_GENOME, 
					doubleInstanceP, DoubleGeneAlgo.INPUT_GENOME
					);
			workflow.connect(
					doubleInstanceP, DoubleGeneAlgo.OUTPUT_VALUE, 
					wsInstance, WattsStrogatzAlgo.INPUT_P
					);
			doubleInstanceP.setValueForParameter(DoubleGeneAlgo.PARAM_MINIMUM, 0d);
			doubleInstanceP.setValueForParameter(DoubleGeneAlgo.PARAM_MAXIMUM, 1d);
			doubleInstanceP.setValueForParameter(DoubleGeneAlgo.PARAM_PROBA_MUTATION, probaMutationP);
		}
		
		
		// measure clustering
		final IAlgoInstance clusteringInstance = clusteringAlgo.createInstance(workflow);
		workflow.addAlgoInstance(clusteringInstance);
		clusteringInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(clusteringInstance);
		clusteringInstance.setName("clustering");
		workflow.connect(
				wsInstance, WattsStrogatzAlgo.OUTPUT_GRAPH,
				clusteringInstance, GraphStreamAverageClustering.INPUT_GRAPH
				);
		
		// set goal 1: clustering
		{
			final IAlgoInstance goalClustering = goalAlgo.createInstance(workflow);
			workflow.addAlgoInstance(goalClustering);
			goalClustering.setContainer(nsgaInstance);
			nsgaInstance.addChildren(goalClustering);
			goalClustering.setName("g_clustering");

			workflow.connect(
					clusteringInstance, GraphStreamAverageClustering.OUTPUT_AVERAGE_CLUSTERING,
					goalClustering, GoalAlgo.INPUT_VALUE
					);
			
			final IAlgoInstance constantDoubleClustering = constantDoubleAlgo.createInstance(workflow);
			workflow.addAlgoInstance(constantDoubleClustering);
			constantDoubleClustering.setContainer(nsgaInstance);
			nsgaInstance.addChildren(constantDoubleClustering);
			//constantDoubleClustering.setName("g_clustering");
			constantDoubleClustering.setValueForParameter(constantDoubleAlgo.getConstantParameter(), targetClustering);

			workflow.connect(
					constantDoubleClustering, ConstantValueDouble.OUTPUT,
					goalClustering, GoalAlgo.INPUT_TARGET
					);
		}
		
		// measure density
		final IAlgoInstance graphPropertiesInstance = graphPropertiesAlgo.createInstance(workflow);
		workflow.addAlgoInstance(graphPropertiesInstance);
		graphPropertiesInstance.setContainer(nsgaInstance);
		nsgaInstance.addChildren(graphPropertiesInstance);
		graphPropertiesInstance.setName("density");
		workflow.connect(
				wsInstance, WattsStrogatzAlgo.OUTPUT_GRAPH,
				graphPropertiesInstance, GraphBasicPropertiesAlgo.INPUT_GRAPH
				);
		
		
		// set goal density
		{
			final IAlgoInstance goalDensity = goalAlgo.createInstance(workflow);
			workflow.addAlgoInstance(goalDensity);
			goalDensity.setContainer(nsgaInstance);
			nsgaInstance.addChildren(goalDensity);
			goalDensity.setName("g_density");

			workflow.connect(
					graphPropertiesInstance, GraphBasicPropertiesAlgo.OUTPUT_DENSITY,
					goalDensity, GoalAlgo.INPUT_VALUE
					);
			
			final IAlgoInstance constantDoubleDensity = constantDoubleAlgo.createInstance(workflow);
			workflow.addAlgoInstance(constantDoubleDensity);
			constantDoubleDensity.setContainer(nsgaInstance);
			nsgaInstance.addChildren(constantDoubleDensity);
			//constantDoubleClustering.setName("g_clustering");
			constantDoubleDensity.setValueForParameter(constantDoubleAlgo.getConstantParameter(), targetDensity);

			workflow.connect(
					constantDoubleDensity, ConstantValueDouble.OUTPUT,
					goalDensity, GoalAlgo.INPUT_TARGET
					);
		}
		
		// outputs: write file
		{ 
			IAlgoInstance writeTableCSVInstance = writeTableCSVAlgo.createInstance(workflow);
			
			workflow.addAlgoInstance(writeTableCSVInstance);
			
			workflow.connect(
					nsgaInstance, NSGA2GeneticExplorationAlgo.OUTPUT_TABLE, 
					writeTableCSVInstance, WriteTableCSV.INPUT_TABLE
					);
			
			writeTableCSVInstance.setValueForParameter(writeTableCSVAlgo.PARAMETER_FILE, new File(destFileAll));
			
		}
		{ 
			IAlgoInstance writeTableCSVInstance = writeTableCSVAlgo.createInstance(workflow);
			
			workflow.addAlgoInstance(writeTableCSVInstance);
			
			workflow.connect(
					nsgaInstance, NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
					writeTableCSVInstance, WriteTableCSV.INPUT_TABLE
					);
			
			writeTableCSVInstance.setValueForParameter(writeTableCSVAlgo.PARAMETER_FILE, new File(destFilePareto));
			
		}
		
	}

}
