package genlab.algog.algos.exec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

public class GeneticExplorationOneGeneration extends
		AbstractContainerExecutionSupervisor {

	private GeneticExplorationAlgoContainerInstance geneAlgoInst;
	
	private final String name;
	
	private final Map<AGenome,Object[][]> generationToEvaluate;
	private Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance;
	private final Map<AGenome, IInputOutputInstance> genome2fitnessOutput;
	private final Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance;
	
	private int totalIterationsToDo = 0;
	private int totalIterationsDone = 0;

	/**
	 * Stores the iterator over genomes, the place for the next computation
	 */
	private final Object lockerParameters = new Object();
	private Iterator<AGenome> currentComputationIteratorProcessedGenome = null;
	private AGenome currentGenome = null;
	private int currentIndexIndividual = 0;
	private Object[] currentIndividual = null;
	private AnIndividual currentIndiv = null;

	private final Object lockerResults = new Object();
	private Map<AnIndividual,Double> computedFitness = new HashMap<AnIndividual, Double>();
	
	private final Map <IConnection,Object> inputConnection2value;
	
	/**
	 * 
	 * @param exec the execution context
	 * @param algoInst the genetic algo instance 
	 * @param generationToEvaluate the generation to be evaluated in this iteration
	 * @param genome2algoInstance the set of algo instances to use for the computation
	 */
	public GeneticExplorationOneGeneration(
			IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst,
			Map<AGenome,Object[][]> generationToEvaluate,
			Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance,
			Map<AGenome, IInputOutputInstance> genome2fitnessOutput,
			Map<AGene<?>,IAlgoInstance> gene2geneAlgoInstance,
			Map <IConnection,Object> inputConnection2value,
			String name
			) {
		
		super(exec, algoInst);
		
		this.geneAlgoInst = algoInst;
		this.name = name;
		
		this.generationToEvaluate = generationToEvaluate;
		this.genome2fitnessOutput = genome2fitnessOutput;
		this.genome2algoInstance = genome2algoInstance;
		this.gene2geneAlgoInstance = gene2geneAlgoInstance;
		this.inputConnection2value = inputConnection2value;
	}

	@Override
	protected void initFirstRun() {
		
		messages.debugTech("should create the exec for this generation", getClass());
		
		// compute the count of iterations to do
		totalIterationsToDo = 0;
		for (Object[][] popToCompute : generationToEvaluate.values()) {
			totalIterationsToDo += popToCompute.length;
		}
		progress.setProgressTotal(totalIterationsToDo);
		
		messages.debugTech("this generation requires "+totalIterationsToDo+" iterations", getClass());
		totalIterationsDone = 0;
		
		
	}

	@Override
	protected void startOfIteration() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean shouldContinueRun() {
		return totalIterationsDone<totalIterationsToDo;
	}

	@Override
	protected void endOfRun() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected IAlgoExecution createNextExecutionForOneIteration() {
		
		GeneticExplorationAlgoIndividualRun resExec;
		
		synchronized (lockerParameters) {
		
			// shit to the next individual to evaluate
			nextIteration();

			// create exec
			messages.traceTech("creating the executable for the exploration of individual: "+currentIndexIndividual+" of genome "+currentGenome, getClass());
			Collection<IAlgoInstance> algoInstancesToRun = genome2algoInstance.get(currentGenome);
			IInputOutputInstance fitnessOutput = genome2fitnessOutput.get(currentGenome);

			currentIndiv = new AnIndividual(currentGenome, currentIndividual);
			
			resExec = new GeneticExplorationAlgoIndividualRun(
					exec, 
					geneAlgoInst, 
					gene2geneAlgoInstance,
					inputConnection2value,
					algoInstancesToRun,
					fitnessOutput,
					currentIndiv, 
					currentIndexIndividual
					);	
			resExec.setParent(this);
			//addTask(resExec);
			
			
			// init its links
			messages.traceTech("init links", getClass());
			resExec.initInputs(instance2execForSubtasks);
		}
		
		return resExec;
		
	}
	
	/**
	 * Shifts to the next iteration. After this call, {@link #currentGenome} and {@link #currentIndexIndividual}
	 * will be set to valid values.
	 */
	protected void nextIteration() {
		
	
		// initial case: retrieve the first genome
		if (currentComputationIteratorProcessedGenome == null) {
			// init
			currentComputationIteratorProcessedGenome = generationToEvaluate.keySet().iterator();
			currentGenome = currentComputationIteratorProcessedGenome.next();
			currentIndexIndividual = 0;
		} else {
		
			// shift to the next individual
			currentIndexIndividual++;
		}
		
		// retrieve current population
		Object[][] currentPopulation = generationToEvaluate.get(currentGenome);
		
		// if we finished the current population, then shift to the next genome !
		if (currentIndexIndividual == currentPopulation.length) {
			currentGenome = currentComputationIteratorProcessedGenome.next();
			currentIndexIndividual = 0;
			currentPopulation = generationToEvaluate.get(currentGenome);
		}
		
		currentIndividual = currentPopulation[currentIndexIndividual];
		
		totalIterationsDone++;
	}
	
	protected void computeResultsForIndividual(GeneticExplorationAlgoIndividualRun indivRun) {

		Double resultFitness = indivRun.getResultFitness();
		AnIndividual indiv = indivRun.getIndividual();
		int individualId = indivRun.getIndividualId();

		messages.infoUser("computed fitness "+resultFitness+" for individual "+individualId+" of genome "+indiv.genome, getClass());

		// store results
		synchronized (lockerResults) {
			
			computedFitness.put(indiv, resultFitness);
			
		}
		
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (!progress.getComputationState().isFinished())
			return; // don't care about non finished events 
		
		// one of our child finished
		
		// don't super.computationStateChanged(progress);
		
		// ignore progress which is not coming from my children
		if (!tasks.contains(progress.getAlgoExecution()))
			return; 

		// retrieve the result for this computation
		if (!(progress.getAlgoExecution() instanceof GeneticExplorationAlgoIndividualRun))
			throw new ProgramException("should not receive messages from other than individual tests");
		
		// update our progress according to children
		switch (progress.getComputationState()) {
		case FINISHED_FAILURE:
			cancel(); // kill other tasks
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			return; // don't use this result
			
		case FINISHED_CANCEL:
			cancel(); // kill other tasks
			progress.setComputationState(ComputationState.FINISHED_CANCEL);
			return; // don't use this result
			
		case FINISHED_OK:
			// don't care 
			break;
		default:
			throw new ProgramException("unknown computation status with property 'finished': "+progress.getComputationState());
		}
		
		// retrieve results
		GeneticExplorationAlgoIndividualRun indivRun = (GeneticExplorationAlgoIndividualRun)progress.getAlgoExecution();
				
		computeResultsForIndividual(indivRun);
		
		// update our progress
		progress.incProgressMade();
		
		super.computationStateChanged(progress);
	}


	@Override
	protected String getSuffixForCurrentIteration() {
		return " "+totalIterationsDone+"/"+totalIterationsToDo+" ("+currentGenome+" ind "+currentIndexIndividual+")";
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the computed fitness for each individual of the generation.
	 * @return
	 */
	public Map<AnIndividual,Double> getComputedFitness() {
		return computedFitness;
	}
	
	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {

		// we have no input of ourselves
		
		// also, we store the table for later usage (when we will create the subtasks !)
		
		this.instance2execOriginal = instance2exec;
		
		// and we create a version to be transmitted to our subtasks
		instance2execForSubtasks = new HashMap<IAlgoInstance, IAlgoExecution>(instance2execOriginal);
		/*
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			// for each algo exec out of this container, the actual 
			// contact during exec will be the supervisor.
			instance2execForSubtasks.put(c.getFrom().getAlgoInstance(), this);	
		}
		*/
		
		progress.setComputationState(ComputationState.READY);
	}

}
