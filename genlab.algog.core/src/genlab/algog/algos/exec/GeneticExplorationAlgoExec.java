package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.AbstractGeneAlgo;
import genlab.algog.algos.meta.BooleanGeneAlgo;
import genlab.algog.algos.meta.DoubleGeneAlgo;
import genlab.algog.algos.meta.GeneticExplorationAlgo;
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
import genlab.core.model.exec.IComputationResult;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.spi.DirStateFactory.Result;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

/**
 * The executable counterpart of {@link GeneticExplorationAlgoContainerInstance}.
 * This is a container executable, which will contain successive exections.
 * Once started, it will generate a generation of the population, then 
 * start a subtask to evaluate this generation, then evaluate the fitness and generate
 * the next generation, then add another subtask to evaluate the next generation, 
 * and so on.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationAlgoExec extends AbstractContainerExecution {

	protected int TODO_POP_SIZE_PER_GENOME = 100; 
	
	protected int iterationsMade = 0;
	
	// random number generation
	protected RandomEngine coltRandom;
	protected Uniform uniform;

	
	// associates each gene to the point were the information has to be sent
	protected Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance = new HashMap<AGene<?>, IAlgoInstance>();
	
	
	// associates each genome with the algorithms which enable to evaluate individuals
	protected Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance = new HashMap<AGenome, Collection<IAlgoInstance>>();
	
	// associates each genome with the output where the fitness is retrieved from 
	protected Map<AGenome, IInputOutputInstance> genome2fitnessOutput = new HashMap<AGenome, IInputOutputInstance>();

	// for each generation, and each individual, stores the corresponding fitness
	protected LinkedHashMap<Integer,Map<AnIndividual,Double>> generation2fitness = new LinkedHashMap<Integer, Map<AnIndividual,Double>>(500);

	protected final GeneticExplorationAlgoContainerInstance algoInst;
	
	// associates each input connection to the corresponding value
	protected Map <IConnection,Object> inputConnection2value = new HashMap<IConnection, Object>();
	
	protected final int paramStopMaxIterations;
	protected final int paramPopulationSize;

	
	public GeneticExplorationAlgoExec(
			IExecution exec, 
			GeneticExplorationAlgoContainerInstance algoInst) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());
		
		this.algoInst = algoInst;
		
		this.autoFinishWhenChildrenFinished = false;
		

		// read parameters
		paramStopMaxIterations = (Integer) algoInst.getValueForParameter(GeneticExplorationAlgo.PARAM_STOP_MAXITERATIONS);

		paramPopulationSize = (Integer) algoInst.getValueForParameter(GeneticExplorationAlgo.PARAM_SIZE_POPULATION);
	}
	
	/**
	 * Called before any other computation.
	 * Detects the actual work to do given the parameters.
	 */
	protected void analyzeParameters() {
		
		// build the list of the genomes
		messages.debugTech("analysis of the content of the genetic algo...", getClass());
		for (IAlgoInstance childInstance: algoInst.getChildren()) {
			
			if (!(childInstance.getAlgo() instanceof GenomeAlgo))
				continue;
			
			// for each genome defined here...
		
			// create its counterpart here
			AGenome genome = new AGenome(childInstance.getName());
			
			// retrieve its parameters
			
			
			Collection<AGene> genesForThisGenome = new LinkedList<AGene>();
			
			Set<IAlgoInstance> genomeEvaluationAlgos = new HashSet<IAlgoInstance>();
			IInputOutputInstance outputFitness = null;
			
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
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION.getId()), 
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM.getId()),
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM.getId())
							);
					
				} else if (geneAlgo instanceof DoubleGeneAlgo) {
					
					gene = new ADoubleGene( 
							geneInstance.getName(), 
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION.getId()),
							(Double)geneInstance.getValueForParameter(DoubleGeneAlgo.PARAM_MINIMUM.getId()),
							(Double)geneInstance.getValueForParameter(DoubleGeneAlgo.PARAM_MAXIMUM.getId())
							);
					
				} else if (geneAlgo instanceof BooleanGeneAlgo) {
					
					gene = new ABooleanGene(
							geneInstance.getName(),
							(Double)geneInstance.getValueForParameter(AbstractGeneAlgo.PARAM_PROBA_MUTATION.getId())
							);
					
				} else {
					throw new WrongParametersException("this type of gene is not managed: "+geneAlgo.getClass());
				}
				
				// store it
				genesForThisGenome.add(gene);
				gene2geneAlgoInstance.put(gene, geneInstance);
				
				// explore children, and add them to the list of the algo isntances to execute for this genome.
				outputFitness = ((GeneticExplorationAlgoContainerInstance)algoInst).collectAlgosToEvaluatePopulation(
						geneInstance, 
						genomeEvaluationAlgos
						);
				
			}		
			
			// store the list of all the executable algos for this genome 
			genome2algoInstance.put(genome, genomeEvaluationAlgos);
			genome2fitnessOutput.put(genome, outputFitness);
			
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


	protected Object[][] generateInitialPopulation(AGenome genome, int popsize) {
		
		Object[][] population;
		
		messages.infoUser("generating the initial population for genome "+genome.name, getClass());
		population = genome.generateInitialGeneration(uniform, popsize);
		genome.printToStream(System.out, population);
		
		return population;
	}
	
	
	/**
	 * Step "construct population" of the genetic algo. Called before each 
	 * @return
	 */
	protected Map<AGenome,Object[][]> generateInitialPopulation() {
	

		// associates each geneome to its corresponding population (for one generation)
		Map<AGenome,Object[][]> genome2population = new HashMap<AGenome, Object[][]>();

		// generate the novel population for each specy
		// for each specy
		messages.debugUser("generation of the population for generation "+iterationsMade, getClass());
		for (AGenome genome : genome2algoInstance.keySet()) {
	
			messages.debugUser("generation of the sub population based on genome: "+genome, getClass());

			
			Object[][] population = generateInitialPopulation(genome, paramPopulationSize/gene2geneAlgoInstance.size());
			
			genome2population.put(genome, population);

			
		}
				
		return genome2population;
		
	}
	

	
	/**
	 * Returns an execution for one iteration, that is for one generation.
	 * @return
	 */
	protected GeneticExplorationOneGeneration createExecutableForGeneration(Map<AGenome,Object[][]> generationToEvaluate) {
		
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
		
		
		for (Map.Entry<AnIndividual,Double> indiv2fitness : generation2fitness.get(generationId).entrySet()) {
			
			ps.print(indiv2fitness.getValue());
			ps.print("\t");
			ps.print(indiv2fitness.getKey().genome);
			ps.print("\t");
			ps.print(Arrays.toString(indiv2fitness.getKey().genes));
			ps.println();

		}
		
		
	}
	
	protected void manageResultsForCurrentGeneration(Map<AnIndividual,Double> result) {
		
		messages.debugUser("retrieving the fitness results for the generation "+iterationsMade, getClass());

		// store it 
		generation2fitness.put(iterationsMade, result);
		
		// display info on it
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double total = 0.0;
		final int count = result.size();
		
		AnIndividual best = null;
		
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
			
			double d = indiv2fitness.getValue();
			
			total += d;
			if (d < min) {
				min = d;
				best = indiv2fitness.getKey();
			}
			max = Math.max(d, max);
		
			System.err.println("raw fitness for "+Arrays.toString(indiv2fitness.getKey().genes)+": "+d);
		}
		
		// now reverse all the values so the genetic algo is attempting to maximize the fitness
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
		
			indiv2fitness.setValue(max-indiv2fitness.getValue()+1);
			
			
		}
		
		// recompute stats
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		total = 0.0;
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
			
			double d = indiv2fitness.getValue();
			
			total += d;
			if (d < min) {
				min = d;
			}
			if (d > max) {
				max = d;
				best = indiv2fitness.getKey();
			}
			
		}
		double average = total / count;
		
		messages.infoUser("for generation "+iterationsMade+": best fitness "+max+", worst "+min+", average "+average+"; best individual "+best, getClass());
		//displayOnStream(System.out, gen, res)
		
	}
	
	protected final boolean shouldContinueExploration() {
		// TODO finish criteria
		return iterationsMade < paramStopMaxIterations;
	}
	
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
			final String titleIteration = "iteration";
			final String titleGenome = "genome";
			final String titleFitness = "fitness";
			final String titleGenes = "genes";
	
	
			GenlabTable tab = new GenlabTable();
			tab.declareColumn(titleIteration);
			tab.declareColumn(titleGenome);
			tab.declareColumn(titleFitness);
			tab.declareColumn(titleGenes);
			
			for (Integer iterationId : generation2fitness.keySet()) {
				
				// for each iteration
				Map<AnIndividual,Double> indiv2fitness = generation2fitness.get(iterationId);
				for (Map.Entry<AnIndividual,Double> ind2fit : indiv2fitness.entrySet()) {
					
					AnIndividual ind = ind2fit.getKey();
					Double fitness = ind2fit.getValue();
					
					int rowId = tab.addRow();
					tab.setValue(rowId, titleIteration, iterationId);
					tab.setValue(rowId, titleGenome, ind.genome.name);
					tab.setValue(rowId, titleFitness, fitness);
					tab.setValue(rowId, titleGenes, ind.toString());
				}
				
			}
		
			res.setResult(GeneticExplorationAlgo.OUTPUT_TABLE, tab);
		}
		setResult(res);
		
		this.progress.setComputationState(ourState);
		

	}
	
	protected Object[][] crossoverOnePoint(final AGenome genome, Object[] indiv1, Object[] indiv2) {
		
		Object[][] res = new Object[2][];
		 
		final int minSlice = 1;
		final int maxSlice = genome.getGenes().length-1;
		
		int genIdx = uniform.nextIntFromTo(minSlice, maxSlice);

		// indiv 1
		Object[] novelIndiv = new Object[indiv1.length];
		for (int i=0; i<genIdx; i++) {
			novelIndiv[i]=indiv1[i];
		}
		for (int i=genIdx; i<indiv1.length; i++) {
			novelIndiv[i]=indiv2[i];
		}
		res[0] = novelIndiv;
		
		// indiv 2
		novelIndiv = new Object[indiv1.length];
		for (int i=0; i<genIdx; i++) {
			novelIndiv[i]=indiv2[i];
		}
		for (int i=genIdx; i<indiv1.length; i++) {
			novelIndiv[i]=indiv1[i];
		}
		res[1] = novelIndiv;

		 
		return res;
	}
	
	
	protected Object[][] crossoverArithmetic(final AGenome genome, Object[] indiv1, Object[] indiv2) {
		
		
		Object[][] res = new Object[2][];

		
		
		Object[] novelIndiv1 = new Object[indiv1.length];
		Object[] novelIndiv2 = new Object[indiv1.length];
		
		for (int i=0; i<indiv1.length; i++) {
		
			final double randomWeightFactor = uniform.nextDoubleFromTo(0, 1.0);
			ANumericGene<?> currentGene = (ANumericGene<?>)genome.getGenes()[i];
			
			novelIndiv1[i] = currentGene.crossoverArithmetic(indiv1[i], indiv2[i], randomWeightFactor);
			novelIndiv2[i] = currentGene.crossoverArithmetic(indiv2[i], indiv1[i], randomWeightFactor);

		}
		
		res[0] = novelIndiv1;
		res[1] = novelIndiv2;
						
		return res;
		
	}
	
	protected Map<AGenome,Object[][]> prepareNextGeneration() {
		
		
		int previousGenerationId = iterationsMade;
		
		// copy in order
		final Map<AnIndividual,Double> indiv2fitness =  generation2fitness.get(previousGenerationId);
		double sumOfFitness = 0;
		for (Map.Entry<AnIndividual,Double> i2f : indiv2fitness.entrySet()) {
			sumOfFitness += i2f.getValue();
		}
		
		System.err.println("sum fitness: "+sumOfFitness);
		// TODO idée: dans notre cas, conserver les N meilleurs permettra d'éprouver leur efficacité malgré l'aspect random
		
		
		// SELECT 
		
		// keep the best 
		// biased wheel
		// http://en.wikipedia.org/wiki/Fitness_proportionate_selection
		Map<AGenome,Set<AnIndividual>> selectedGenome2Population = new HashMap<AGenome, Set<AnIndividual>>();
		int toSelect = indiv2fitness.size()/2;
		while (toSelect > 0) {
			
			// run wheel !
			double r = uniform.nextDoubleFromTo(0, sumOfFitness);
			double localSum = 0;
			wheel: for (Map.Entry<AnIndividual,Double> i2f : indiv2fitness.entrySet()) {
				final AnIndividual individual = i2f.getKey();
				final Double fitness = i2f.getValue();
				localSum += fitness;
				if (localSum >= r) {
					//selected.add(i2f.getKey());
					
					Set<AnIndividual> indiv = selectedGenome2Population.get(individual.genome);
					if (indiv == null) {
						indiv = new HashSet<AnIndividual>(indiv2fitness.size());
						selectedGenome2Population.put(individual.genome, indiv);
					}
					if (indiv.add(individual)) {
						toSelect --;
						System.err.println("keeping: "+i2f.getKey()+", with fitness "+fitness);
					}
					break wheel;
				}
			}
			
		}		

		// TODO mutation ! 
		// final double probabilityMutate;
		
		// CROSS
		// TODO manage multi specy !
		
		Map<AGenome,Object[][]> novelGenome2Population = new HashMap<AGenome, Object[][]>();

		// stats on the count of mutation (to be returned to the user)
		Map<AGene<?>,Integer> statsGene2countMutations = new HashMap<AGene<?>, Integer>();

		for (AGenome genome: selectedGenome2Population.keySet()) {
			
			Set<AnIndividual> indivs = selectedGenome2Population.get(genome);
			
			List<AnIndividual> selectedPopIndex = new LinkedList<AnIndividual>(indivs);
			
			Object[][] novelPopulation = new Object[indiv2fitness.size()][];
			int novelPopulationSize = 0;
			
			
			while (novelPopulationSize < indiv2fitness.size()) {
			
				// randomly select individual 1
				int index1 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				// randomly select individual 2, different from 1
				int index2;
				do {
					index2 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				} while (index2 == index1);
				// randomly select the place where doing the cut
			
				
				
				Object[] indiv1 = selectedPopIndex.get(index1).genes;
				Object[] indiv2 = selectedPopIndex.get(index2).genes;

				//Object[][] novelIndividuals = crossoverOnePoint(genome, indiv1, indiv2);
				Object[][] novelIndividuals = crossoverArithmetic(genome, indiv1, indiv2);
				
				
				System.err.println("crossover: "+Arrays.toString(indiv1)+" and "+Arrays.toString(indiv2)+" => "+Arrays.toString(novelIndividuals[0]));
				novelPopulation[novelPopulationSize++] = novelIndividuals[0];
				
				if (novelPopulationSize >= novelPopulation.length) 
					break;
				
				System.err.println("crossover: "+Arrays.toString(indiv1)+" and "+Arrays.toString(indiv2)+" => "+Arrays.toString(novelIndividuals[1]));
				novelPopulation[novelPopulationSize++] = novelIndividuals[1];
				
				
			}

			// MUTATION

			final double probaMutation = 0.01;
			for (int i=0; i<novelPopulation.length; i++) {
				
				
				AGene<?>[] genes = genome.getGenes();
				for (int j=0; j<genes.length; j++) {
					 
					if (uniform.nextDoubleFromTo(0.0, 1.0) <= genes[j].getMutationProbability()) {
						
						Object[] individual = novelPopulation[i];
						String debugIndivBefore = Arrays.toString(individual);
						individual[j] = genes[j].generateRandomnly(uniform);
						System.err.println("mutate individual "+i+": "+debugIndivBefore +" => "+Arrays.toString(individual));
						
						// stats on mutation
						Integer count = statsGene2countMutations.get(genes[j]);
						if (count == null) {
							count = 0;
						} 
						statsGene2countMutations.put(genes[j], count+1);
					}
					
				}
				
				
			}
			
			novelGenome2Population.put(genome, novelPopulation);
			
		}
		
		{
			StringBuffer sb = new StringBuffer();
			sb.append("during the generation of the population ").append(iterationsMade);
			sb.append(" there were these mutations per gene: ");
			for (Map.Entry<AGene<?>,Integer> gene2count : statsGene2countMutations.entrySet()) {
				sb.append(gene2count.getKey().name);
				sb.append(":");
				sb.append(gene2count.getValue());
				sb.append("; ");
			}
			messages.infoUser(sb.toString(), getClass());
		}
		
		return novelGenome2Population;
		
	}
	
	@Override
	public final void computationStateChanged(IComputationProgress progress) {
		
		// let the parent manage its stuff
		super.computationStateChanged(progress);
		
		
		if (!progress.getComputationState().isFinished())
			return;
		
		if (!(progress.getAlgoExecution() instanceof GeneticExplorationOneGeneration))
			return;
		
		
		final GeneticExplorationOneGeneration algoFinished = (GeneticExplorationOneGeneration)progress.getAlgoExecution();
		
		final Map<AnIndividual,Double> result = new HashMap<AnIndividual, Double>(algoFinished.getComputedFitness());
	
		// manage results
		manageResultsForCurrentGeneration(result);
		displayOnStream(System.err, iterationsMade);
		
		Runtime.getRuntime().gc();
		Thread.yield();
		
		// we are now going to the next iteration; 
		if (!shouldContinueExploration()) {
			manageEndOfExploration();
			return;
		}
		
		// prepare next iteration		
		GeneticExplorationOneGeneration execFirstGen = createExecutableForGeneration(prepareNextGeneration());
		
		// start it !
		addTask(execFirstGen);
		
		
		iterationsMade++;

	}
	
	/*
	protected void createExecutionForOneIndividual(AGenome genome, Object[] individual) {
		
		
		Collection<IAlgoInstance> algoInstancesToRun = genome2algoInstance.get(genome);
		
		Map<IConnection,Object> incomingConnection2value = new HashMap<IConnection, Object>();
		
		for (AGene<?> gene: genome.getGenes()) {
			IAlgoInstance geneAlgoInstance = gene2geneAlgoInstance.get(gene);
			geneAlgoInstance.set
		}
		incomingConnection2value.put(key, value)
		
		GeneticExplorationAlgoIndividualRun individualIteration = new GeneticExplorationAlgoIndividualRun(
				exec, 
				algoInst, 
				algoInstancesToRun
				);
		
		
		
	}
	*/



	@Override
	public void run() {
		
		iterationsMade = 0;

		progress.setComputationState(ComputationState.STARTED);
		

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
