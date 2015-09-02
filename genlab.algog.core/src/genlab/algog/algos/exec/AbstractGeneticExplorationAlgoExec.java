package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.AbstractGeneAlgo;
import genlab.algog.algos.meta.AbstractGeneticExplorationAlgo;
import genlab.algog.algos.meta.BooleanGeneAlgo;
import genlab.algog.algos.meta.DoubleGeneAlgo;
import genlab.algog.algos.meta.GeneticExplorationAlgoConstants;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.algog.internal.ABooleanGene;
import genlab.algog.internal.ADoubleGene;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AIntegerGene;
import genlab.algog.internal.ANumericGene;
import genlab.algog.internal.AnIndividual;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

/**
 * The executable counterpart of {@link GeneticExplorationAlgoContainerInstance}.
 * This is a container executable, which will contain successive executions.
 * Once started, it will generate a generation of the population, then 
 * start a subtask to evaluate this generation, then evaluate the fitness and generate
 * the next generation, then add another subtask to evaluate the next generation, 
 * and so on.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractGeneticExplorationAlgoExec extends AbstractContainerExecution {

		
	// random number generation
	protected RandomEngine coltRandom;
	protected Uniform uniform;

	// associates each gene to the point were the information has to be sent
	protected Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance = new LinkedHashMap<AGene<?>, IAlgoInstance>();
	
	// associates each genome with the algorithms which enable to evaluate individuals
	protected Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance = new HashMap<AGenome, Collection<IAlgoInstance>>();
	
	// associates each genome with the output where the fitness is retrieved from 
	protected Map<AGenome, List<IAlgoInstance>> genome2fitnessOutput = new LinkedHashMap<AGenome, List<IAlgoInstance>>();

	// for each generation, and each individual, stores the corresponding fitness
	protected Map<Integer, Set<AnIndividual>> offspringGeneration = new LinkedHashMap<Integer, Set<AnIndividual>>();
	protected Map<Integer, Set<AnIndividual>> parentGeneration = new LinkedHashMap<Integer, Set<AnIndividual>>();

	protected final GeneticExplorationAlgoContainerInstance algoInst;
	
	// associates each input connection to the corresponding value
	protected Map <IConnection,Object> inputConnection2value = new HashMap<IConnection, Object>();
	
	protected final int paramPopulationSize;
	
	protected final int paramStopMaxIterations;



	protected int iterationsMade = 0;

	
	public AbstractGeneticExplorationAlgoExec(IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
		

		this.algoInst = algoInst;
		
		this.autoFinishWhenChildrenFinished = false;
		this.autoUpdateProgressFromChildren = false;
		this.ignoreCancelFromChildren = true;
		this.ignoreFailuresFromChildren = true;

		// read parameters
		paramPopulationSize = (Integer) algoInst.getValueForParameter(AbstractGeneticExplorationAlgo.PARAM_SIZE_POPULATION);
		
		paramStopMaxIterations = (Integer) algoInst.getValueForParameter(AbstractGeneticExplorationAlgo.PARAM_STOP_MAXITERATIONS);

		
	}


	/**
	 * Stores the results for the current generation.
	 * They will be accessible through generation2fitness, generation2targets, generation2values
	 * @param resultFitness
	 * @param resultTargets
	 * @param resultValues
	 */
	protected void manageResultsForCurrentGeneration(Set<AnIndividual> individuals
			) {
		
		messages.infoUser("retrieving the fitness results for the generation "+iterationsMade, getClass());

		// store it 
		// only offspring without parents
		Set<AnIndividual> h = new HashSet<>(individuals);
		h.removeAll(parentGeneration.get(iterationsMade));
		offspringGeneration.put(iterationsMade, h);

		
		this.progress.incProgressMade();

		
	}
	/**
	 * Called before any other computation.
	 * Detects the actual work to do given the parameters.
	 */
	protected void analyzeParameters() {
		
		// build the list of the genomes
		messages.debugTech("analysis of the content of the genetic algo...", getClass());
		
		// load parameters for mutation and crossover
		final double etam = (Double)algoInst.getValueForParameter(AbstractGeneticExplorationAlgo.PARAM_ETA_MUTATION);
		final double etac = (Double)algoInst.getValueForParameter(AbstractGeneticExplorationAlgo.PARAM_ETA_CROSSOVER);
		
		Set<IAlgoInstance> allGoals = algoInst.collectGoals();
		
		Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstanceLocal = new LinkedHashMap<AGene<?>, IAlgoInstance>();

		for (IAlgoInstance childInstance: algoInst.getChildren()) {
			
			if (!(childInstance.getAlgo() instanceof GenomeAlgo))
				continue;
			
			// for each genome defined here...
		
			// create its counterpart here
			final Double crossoverProbability = (Double)childInstance.getValueForParameter(GenomeAlgo.PARAM_PROBA_CROSSOVER);
			AGenome genome = new AGenome(
					childInstance.getName(), 
					crossoverProbability
					);
			
			// retrieve its parameters
			List<AGene<?>> genesForThisGenome = new LinkedList<AGene<?>>();
			
			Set<IAlgoInstance> genomeEvaluationAlgos = new HashSet<IAlgoInstance>();
			Set<IAlgoInstance> genomeGoalAlgos = new LinkedHashSet<IAlgoInstance>();
	
			for (IConnection outC : childInstance.getOutputInstanceForOutput(GenomeAlgo.OUTPUT_GENOME).getConnections()) {
				
				// ... and for each connection out of there...
										
				// ... so for each gene included in this genome...
				IAlgoInstance geneInstance = outC.getTo().getAlgoInstance();
				IAlgo geneAlgo = geneInstance.getAlgo();
				
				AGene<?> gene = null;
								
				// create its counterpart with the same parameters
				if (geneAlgo instanceof IntegerGeneAlgo) {
					
					gene = new AIntegerGene( 
							geneInstance.getName(),
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION), 
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM),
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM),
							etam,
							etac
							);
					
				} else if (geneAlgo instanceof DoubleGeneAlgo) {
					
					gene = new ADoubleGene( 
							geneInstance.getName(), 
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION),
							(Double)geneInstance.getValueForParameter(DoubleGeneAlgo.PARAM_MINIMUM),
							(Double)geneInstance.getValueForParameter(DoubleGeneAlgo.PARAM_MAXIMUM),
							etam,
							etac
							);
					
				} else if (geneAlgo instanceof BooleanGeneAlgo) {
					
					gene = new ABooleanGene(
							geneInstance.getName(),
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION),
							etam,
							etac
							);
					
				} else {
					throw new WrongParametersException("this type of gene is not managed: "+geneAlgo.getClass());
				}
				
				// store it
				genesForThisGenome.add(gene);
				gene2geneAlgoInstanceLocal.put(gene, geneInstance);
				
				// explore children, and add them to the list of the algo isntances to execute for this genome.
				((GeneticExplorationAlgoContainerInstance)algoInst).collectAlgosToEvaluatePopulation(
						geneInstance, 
						genomeEvaluationAlgos,
						genomeGoalAlgos
						);
				
				if (genomeGoalAlgos.size() != allGoals.size())
					throw new WrongParametersException("each genome should be connected to algos connected to all the goals.");
				
			}		
			
			// sort the genes by name
			{
				Collections.sort(genesForThisGenome, new Comparator<AGene<?>>() {

					@Override
					public int compare(AGene<?> o1, AGene<?> o2) {
						return o1.name.compareTo(o2.name);
					}
					
				});
				// retrieve algos as a list
				List<AGene<?>> algoInstancesGenes = new LinkedList<>(gene2geneAlgoInstanceLocal.keySet());
				Collections.sort(algoInstancesGenes, new Comparator<AGene<?>>() {

					@Override
					public int compare(AGene<?> o1, AGene<?> o2) {
						return o1.name.compareTo(o2.name);
					}
					
				});
				for (AGene<?> g: algoInstancesGenes) {
					gene2geneAlgoInstance.put(g, gene2geneAlgoInstanceLocal.get(g));
				}
			}
						
			// store the list of all the executable algos for this genome 
			genome2algoInstance.put(genome, genomeEvaluationAlgos);
			genome2fitnessOutput.put(genome, new ArrayList<IAlgoInstance>(genomeGoalAlgos));
			
			genome.setGenes(genesForThisGenome);
				

			messages.debugTech("genome "+genome+" is associated with computations: "+genomeEvaluationAlgos, getClass());
			messages.debugTech("genome "+genome+" has for genes: "+genesForThisGenome, getClass());
			
		}
		
	}
	
	protected void initRandomNumberGenerator() {
		// TODO seed as a parameter
		messages.debugUser("initialization of the random network genetor of the genetic algo (COLT Mersenne Twister)...", getClass());
		coltRandom = new MersenneTwister();
		uniform = new Uniform(coltRandom);
				
	}

	protected Set<AnIndividual> generateInitialPopulation(AGenome genome, int popsize) {

		// TODO generation of the population with better init !
		
		Set<AnIndividual> population;
		
		messages.infoUser("generating the initial population ("+popsize+" individuals) for genome "+genome.name, getClass());
		population = genome.generateInitialGeneration(uniform, popsize);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		genome.printToStream(ps , population);

		messages.infoUser("generated "+popsize+" individuals: "+baos.toString(), getClass());		
		
		return population;
	}
	
	
	/**
	 * Step "construct population" of the genetic algo. Called before each 
	 * @return
	 */
	protected abstract Map<AGenome,Set<AnIndividual>> generateInitialPopulation();
	
	
	/**
	 * Returns an execution task for one iteration, that is for the evaluation of one generation.
	 * @return
	 */
	/**
	 * Returns an execution for one iteration, that is for one generation.
	 * @return
	 */
	protected GeneticExplorationOneGeneration createExecutableForGeneration(Map<AGenome,Set<AnIndividual>> generationToEvaluate) {
		
		// one iteration means: create one container to run the evaluation of this whole population
		// in other words, we are a supervisor which will contain a supervisor which contains population evaluations.
		
				
		messages.traceTech("creating the executable for the evaluation of this generation...", getClass());
				
		GeneticExplorationOneGeneration execOneGeneration = new GeneticExplorationOneGeneration(
				exec, 
				algoInst,
				generationToEvaluate,
				Collections.unmodifiableMap(genome2algoInstance),
				Collections.unmodifiableMap(genome2fitnessOutput),
				Collections.unmodifiableMap(gene2geneAlgoInstance),				
				inputConnection2value,
				"eval generation "+iterationsMade
				);
		
		execOneGeneration.setParent(this);
		execOneGeneration.getProgress().addListener(this);
		
		messages.traceTech("init links to this executable", getClass());
		execOneGeneration.initInputs(instance2execForSubtasks);

		messages.traceTech("defining the values received from outside", getClass());
		// TODO ???
		
		return execOneGeneration;
		
	}
	
	/**
	 * Displays the results for one given generation on the given stream.
	 * @param ps
	 * @param generationId
	 */
	protected void displayOnStream(PrintStream ps, int generationId) {
		
		for ( AnIndividual individual : parentGeneration.get(generationId) ) {
			ps.print(individual.fitness);
			ps.print("\t");
			ps.print(individual.genome);
			ps.print("\t");
			ps.print(Arrays.toString(individual.genes));
			ps.println();
		}
		
		for ( AnIndividual individual : offspringGeneration.get(generationId) ) {
			ps.print(individual.fitness);
			ps.print("\t");
			ps.print(individual.genome);
			ps.print("\t");
			ps.print(Arrays.toString(individual.genes));
			ps.println();
		}

	}
	
	
	
	/**
	 * Should return true if the algorithm has converged.
	 * If you don't have any automatic convergence detection, just return false.
	 * 
	 * @return
	 */
	protected abstract boolean hasConverged();
	
	/**
	 * Stops the exploration if the algo has converged or if the number of total iterations was reached.
	 * @return
	 */
	protected final boolean shouldContinueExploration() {
		return (iterationsMade < paramStopMaxIterations) && !hasConverged();
	}
	
	/**
	 * Adds columns and corresponding metadata for every gene.
	 * Returns the mapping between the genome and the corresponding gene columns 
	 * @param tab
	 */
	protected Map<AGenome,String[]> declareColumnsForGenes(GenlabTable tab) {

		final Map<AGenome,String[]> genome2geneColumns = new LinkedHashMap<AGenome, String[]>(genome2fitnessOutput.size());

		final Map<String,Map<String,Object>> tableMetadataGenes = new HashMap<String, Map<String,Object>>();

		// for each genome
		for (AGenome currentGenome: genome2fitnessOutput.keySet()) {
						
			String[] names = new String[currentGenome.getGenes().length];
			// and each gene of this genome
			for (int j=0; j<names.length; j++) {
			
				names[j] = "genes "+currentGenome.name+" / "+currentGenome.getGenes()[j].name;
				tab.declareColumn(names[j]);

				// store metadata
				Map<String,Object> geneMetadata = new HashMap<String, Object>();
				geneMetadata.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_VALUE, names[j]);
				final String nameForOutpt = currentGenome.name+" / "+currentGenome.getGenes()[j].name;
				// min and max if possible
				final AGene<?> gene = currentGenome.getGenes()[j];
				if (gene instanceof ANumericGene<?>) {
					ANumericGene nGene = (ANumericGene)gene;
					geneMetadata.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_MIN, nGene.min.doubleValue());
					geneMetadata.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_MAX, nGene.max.doubleValue());
				} 
				tableMetadataGenes.put(names[j], geneMetadata);

				
			}
			
			genome2geneColumns.put(currentGenome, names);
			
		}
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_GENES2METADATA, tableMetadataGenes);
		
		return genome2geneColumns;
	}
	

	/**
	 * Adds columns and corresponding metadata for every gene.
	 * Returns the mapping between the genome and the corresponding gene columns 
	 * @param tab
	 */
	protected Map<AGenome,String[]> declareColumnsForGoals(GenlabTable tab) {

		final Map<AGenome,String[]> genome2fitnessColumns = new LinkedHashMap<AGenome, String[]>(genome2fitnessOutput.size());
		final Map<String,Map<String,String>> tableMetadataGoals = new HashMap<String, Map<String,String>>();
		for (AGenome currentGenome: genome2fitnessOutput.keySet()) {
		
			String[] names = new String[genome2fitnessOutput.get(currentGenome).size()*3];
			int j=0;
			for (IAlgoInstance goalAI: genome2fitnessOutput.get(currentGenome)) {
			
				Map<String,String> colMetadataForGenome = new HashMap<String, String>();
				tableMetadataGoals.put(currentGenome.name+" / "+goalAI.getName(), colMetadataForGenome);
				
				names[j] = "target "+currentGenome.name+" / "+goalAI.getName();
				tab.declareColumn(names[j]);
				colMetadataForGenome.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET, names[j]);
				j++;
				
				names[j] = "value "+currentGenome.name+" / "+goalAI.getName();
				tab.declareColumn(names[j]);
				colMetadataForGenome.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE, names[j]);
				j++;
				
				names[j] = "fitness "+currentGenome.name+" / "+goalAI.getName();
				tab.declareColumn(names[j]);
				colMetadataForGenome.put(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_FITNESS, names[j]);
				j++;
				
			}
			
			genome2fitnessColumns.put(currentGenome, names);
			
		}
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_GOALS2COLS, tableMetadataGoals);
		
		return genome2fitnessColumns;
	}
	
	/**
	 * Stores inside a table the individuals for a population
	 * @param tab
	 * @param titleIteration
	 * @param iterationId
	 * @param titleGenome
	 * @param genome2fitnessColumns
	 * @param genome2geneColumns
	 * @param individuals
	 * @param indiv2fitness
	 * @param indiv2value
	 * @param indiv2target
	 */
	protected final void storeIndividualsData(
				GenlabTable tab, 
				String titleIteration, Integer iterationId, 
				String titleGenome, 
				Map<AGenome,String[]> genome2fitnessColumns,
				Map<AGenome,String[]> genome2geneColumns,
				Set<AnIndividual> individuals
				) {
		
		
		for (AnIndividual ind : individuals) {
			
			try {
				int rowId = tab.addRow();
				tab.setValue(rowId, titleIteration, iterationId);
				tab.setValue(rowId, titleGenome, ind.genome.name);
				
				// export fitness
				String[] colnames = genome2fitnessColumns.get(ind.genome);
				for (int i=0; i<colnames.length/3; i++) {
					
					int I=i*3;
					
					tab.setValue(
							rowId, 
							colnames[I], 
							ind.targets[i]
							);
					
					I=I+1;
					
					tab.setValue(
							rowId, 
							colnames[I], 
							ind.values[i]
							);
	
					I=I+1;
					
					tab.setValue(
							rowId, 
							colnames[I], 
							ind.fitness[i]
							);
				}
				
				// export genes
				String[] genesNames = genome2geneColumns.get(ind.genome);
				for (int i=0; i<genesNames.length; i++) {
				
					tab.setValue(
							rowId, 
							genesNames[i], 
							ind.genes[i]
							);
				}
			
			} catch (RuntimeException e) {
				messages.errorTech("error while exporting data for individual "+ind, getClass());
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * Packs all the data inside a table.
	 * @return
	 */
	protected final GenlabTable packDataInTable() {

		// TODO don't recreate a new table from 0 every time ?
		
		final String titleIteration = "iteration";
		final String titleGenome = "genome";
		
		GenlabTable tab = new GenlabTable();
		tab.declareColumn(titleIteration);
		tab.declareColumn(titleGenome);
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_COLTITLE_ITERATION, titleIteration);
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_MAX_ITERATIONS, paramStopMaxIterations);
		
		
		// declare columns for each fitness
		final Map<AGenome,String[]> genome2fitnessColumns = declareColumnsForGoals(tab);
		
		// declare columns for each possible gene
		final Map<AGenome,String[]> genome2geneColumns = declareColumnsForGenes(tab);
		
		storeIndividualsData(
			tab, 
			titleIteration, 0, titleGenome, 
			genome2fitnessColumns, genome2geneColumns, 
			parentGeneration.get(0)
		);
		
		for (Integer iterationId : offspringGeneration.keySet()) {
			// for each iteration
			storeIndividualsData(
				tab, 
				titleIteration, iterationId, titleGenome, 
				genome2fitnessColumns, genome2geneColumns, 
				offspringGeneration.get(iterationId)
			);
		}
		
		return tab;
	}
	
	/**
	 * At the very end of the exploration (at stop), packs data inside tables, 
	 * and also changes the executable status.
	 */
	protected final void manageEndOfExploration() {
		messages.infoUser("stopping after "+iterationsMade+" iterations", getClass());
		
		// update our computation state
		ComputationState ourState = null;
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		{
			
			if (somethingFailed)
				ourState = ComputationState.FINISHED_FAILURE;
			else if (somethingCanceled)
				ourState = ComputationState.FINISHED_CANCEL;
			else 
				ourState = ComputationState.FINISHED_OK;
			
		}
		
		if (ourState == ComputationState.FINISHED_OK) {
			
			GenlabTable tab = packDataInTable();
		
			res.setResult(AbstractGeneticExplorationAlgo.OUTPUT_TABLE, tab);
		}
		
		hookProcessResults(res, ourState);
		
		setResult(res);
		
		this.progress.setComputationState(ourState);
		

	}
	
	/**
	 * Called to propose inherited classes to export their specific results.
	 * @param res 
	 * @param ourState
	 */
	protected void hookProcessResults(ComputationResult res, ComputationState ourState) {
		
	}
	
//	public Set<AnIndividual> getIndividualsForPreviousLastGeneration() {
//		return offspringGeneration.get(iterationsMade-1);
//	}
	
	public Set<AnIndividual> getIndividualsForLastGeneration() {
		return offspringGeneration.get(iterationsMade);
	}
	
	/**
	 * Add something to the result to be exported as an intermediate version.
	 * @param res
	 */
	protected void completeContinuousIntermediateResult(ComputationResult res) {

		GenlabTable tab = packDataInTable();
		res.setResult(AbstractGeneticExplorationAlgo.OUTPUT_TABLE, tab);
		
	}
	
	/**
	 * exports an intermediate version of the result, if possible
	 */
	protected final void exportContinuousOutput() {
		
	
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		
		completeContinuousIntermediateResult(res);
		
		setResult(res);
		
		// notify children of our updates
		progress.setComputationState(ComputationState.SENDING_CONTINOUS);

		
		
		
	}
	
	/**
	 * Generate the next generation using specific selection, crossover and mutation operators.
	 * @return
	 */
	protected abstract Map<AGenome,Set<AnIndividual>> prepareNextGeneration();

	@Override
	/**
	 * Called when the exploration of one generation was finished.
	 */
	public final void computationStateChanged(IComputationProgress progress) {
		
		// if user asked for cancel, then this is a good time for cancelling now.
		if (opportunityCancel())
			return;
		
		synchronized (tasks) {

			// if the change in the algo is not a "finished" one, then ignore it (i.e. we don't react to any progress of it !)
			if (!progress.getComputationState().isFinished())
				return;
			
			// if the algo for which the progress changed is not the exploration of one generation, then ignore it.
			if (!(progress.getAlgoExecution() instanceof GeneticExplorationOneGeneration))
				return;
			
			// retrieve the algorithm which finished, that is the exploration of one generation 
			final GeneticExplorationOneGeneration algoFinished = (GeneticExplorationOneGeneration)progress.getAlgoExecution();
			
			try {
				// retrieve the different results of the evaluation of the generation: fitness, values explored, and targets
				Set<AnIndividual> individuals = algoFinished.getComputedIndividuals();
	
				if (individuals.size() != paramPopulationSize) {
					this.messages.errorUser("at iteration "+iterationsMade+", retrieved only "+individuals.size()+" instead of the "+paramPopulationSize+" expected", getClass());
				}
	//			final Map<AnIndividual,Double[]> resultFitness = algoFinished.getComputedFitness(); // (this is already a copy)
	//			final Map<AnIndividual,Object[]> resultValues = algoFinished.getComputedValues(); // (this is already a copy)
	//			final Map<AnIndividual,Object[]> resultTargets = algoFinished.getComputedTargets(); // (this is already a copy)
	
				
				// now use these results: store them ! 
				manageResultsForCurrentGeneration(individuals);
				
				// give the JVM some time and recommand a memory analysis (its good time for doing it !) 
				Runtime.getRuntime().gc();
				Thread.yield();
				
				// if the evaluation of the algorithm is done yet, then finish the process
				if (!shouldContinueExploration()) {
					manageEndOfExploration();
					return;
				}
				
				// one more time, maybe the user asked for cancelling, in this case don't start a novel iteration !
				if (opportunityCancel())
					return;
				
				// prepare next iteration: create the next generation, and pack it as an executable
				GeneticExplorationOneGeneration execFirstGen = createExecutableForGeneration(prepareNextGeneration());
				
				// start the evaluation of the next population, by adding the novel subtask to our set of children tasks
				messages.infoUser("starting the evaluation of generation "+iterationsMade, getClass());
				addTask(execFirstGen);
				
				iterationsMade++;
				
			} catch (Exception e) {
				messages.errorTech("error during the end of generation "+iterationsMade, getClass(), e);
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
			}
		}
		

	}
	
	/**
	 * Checks if cancel was asked by the user; in this case cancels this task
	 * please be nice and call it from time to time !
	 * @return
	 */
	protected boolean opportunityCancel() {
		if (canceled) {
			progress.setComputationState(ComputationState.FINISHED_CANCEL);
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		
		iterationsMade = 0;

		progress.setComputationState(ComputationState.STARTED);
		progress.setProgressTotal(paramStopMaxIterations);

		// init RNG
		initRandomNumberGenerator();
				
		// retrieve the results from outside
		// as a container, we may have received values from outside; so 
		// we will send it to our children
		// prepare the data to send
		inputConnection2value.clear();
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			Object value = getInputValueForInput(c.getTo());
			
			inputConnection2value.put(c, value);
		}
		
		// analyze the parameters of the genlab stuff
		analyzeParameters();
		
		// add the initial generation
		GeneticExplorationOneGeneration execFirstGen = createExecutableForGeneration(generateInitialPopulation());
		
		// start it !
		addTask(execFirstGen);
		
	}
	
	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {

		// we have no input of ourselves
		// still, we need to wait for the incoming links of our children to be ready
		
		// TODO this is the same as in the supervisor container; should we factorize ?
		
		// create execution links for each input expected;
		// its comes from the output to this container
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			createInputExecutableConnection(
					c.getTo(), 
					c, 
					instance2exec
					);
			
			inputsNotAvailable.add(c.getTo());
			
		}
		
		// also, we store the table for later usage (when we will create the subtasks !)
		this.instance2execOriginal = instance2exec;
		
		// and we create a version to be transmitted to our subtasks
		instance2execForSubtasks = new HashMap<IAlgoInstance, IAlgoExecution>(instance2execOriginal.size());
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			// for each algo exec out of this container, the actual 
			// contact during exec will be the supervisor.
			instance2execForSubtasks.put(c.getFrom().getAlgoInstance(), this);	
		}
		
	}


	
}
