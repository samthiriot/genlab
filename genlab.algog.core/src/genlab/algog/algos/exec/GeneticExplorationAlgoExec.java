package genlab.algog.algos.exec;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.IntegerGeneAlgo;
import genlab.algog.algos.meta.ReceiveFitnessAlgo;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AIntegerGene;
import genlab.algog.types.Genome;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecutionIteration;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.InputOutputInstance;
import genlab.core.model.meta.IAlgo;

public class GeneticExplorationAlgoExec extends AbstractContainerExecutionSupervisor {

	
	int iterationsMade = 0;
	
	// random number generation
	RandomEngine coltRandom;
	Uniform uniform;

	Map<AGenome,Object[][]> genome2population = new HashMap<AGenome, Object[][]>();

	// associates each gene to the point were the information has to be sent
	Map<AGene,IAlgoInstance> gene2geneAlgoInstance = new HashMap<AGene, IAlgoInstance>();
	
	
	// associates each genome with the algorithms which enable to evaluate individuals
	Map<AGenome, Collection<IAlgoInstance>> genome2algoInstance = new HashMap<AGenome, Collection<IAlgoInstance>>();
	
	
	public GeneticExplorationAlgoExec(
			IExecution exec, 
			GeneticExplorationAlgoContainerInstance algoInst) {
		
		super(exec, algoInst);
		
		
	}


	@Override
	protected void initFirstRun() {
		
		iterationsMade = 0;
		
		// parse the parameters, which are stored as genlab entities.
		// make them intelligible.
			
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
			
			for (IConnection outC : childInstance.getOutputInstanceForOutput(GenomeAlgo.OUTPUT_GENOME).getConnections()) {
				
				// ... and for each connection out of there...
										
				// ... so for each gene included in this genome...
				IAlgoInstance geneInstance = outC.getTo().getAlgoInstance();
				IAlgo geneAlgo = geneInstance.getAlgo();
				
				AGene<?> gene = null;
				
				// create its counterpart with the same parameters
				if (geneAlgo instanceof IntegerGeneAlgo<?>) {
					
					gene = new AIntegerGene( 
							geneInstance.getName(), 
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MINIMUM.getId()),
							(Integer)geneInstance.getValueForParameter(IntegerGeneAlgo.PARAM_MAXIMUM.getId())
							);
					
				} else {
					throw new WrongParametersException("this type of gene is not managed: "+geneAlgo.getClass());
				}
				
				// store it
				genesForThisGenome.add(gene);
				gene2geneAlgoInstance.put(gene, geneInstance);
				
				// explore children, and add them to the list of the algo isntances to execute for this genome.
				((GeneticExplorationAlgoContainerInstance)algoInst).collectAlgosToEvaluatePopulation(
						geneInstance, 
						genomeEvaluationAlgos
						);
				
			}		
			
			// store the list of all the executable algos for this genome 
			genome2algoInstance.put(genome, genomeEvaluationAlgos);
			
			genome.setGenes(genesForThisGenome);
		
			messages.debugTech("genome "+genome+" is associated with computations: "+genomeEvaluationAlgos, getClass());
			messages.debugTech("genome "+genome+" has for genes: "+genesForThisGenome, getClass());
			
		}
		
		
		// init RNG
		// TODO seed as a parameter
		messages.debugUser("initialization of the random network genetor of the genetic algo (COLT Mersenne Twister)...", getClass());
		coltRandom = new MersenneTwister();
		uniform = new Uniform(coltRandom);
				
				
	}

	@Override
	protected void startOfIteration() {
	
		
	}

	@Override
	protected boolean shouldContinueRun() {
		return iterationsMade <= 2;
	}

	@Override
	protected void endOfRun() {
		iterationsMade++;

	}

	@Override
	protected String getSuffixForCurrentIteration() {
		
		return " run "+iterationsMade;
	}
	
	/**
	 * Returns an execution for one iteration, that is for one generation.
	 * @return
	 */
	@Override
	protected IAlgoExecution createNextExecutionForOneIteration() {
		
		// one iteration means: create one container to run the evaluation of this whole population
		// in other words, we are a supervisor which will contain a supervisor which contains population evaluations.
		
		// generate the novel population for each specy
		// for each specy
		messages.debugUser("generation of the population...", getClass());
		for (AGenome genome : genome2algoInstance.keySet()) {
	
			messages.debugUser("generation of the sub population based on genome: "+genome, getClass());

			Object[][] population = genome2population.get(genome);
			
			if (population == null) {
				messages.infoUser("generating the initial population for genome "+genome.name, getClass());
				population = genome.generateInitialGeneration(uniform, 10);
				genome.printToStream(System.out, population);
				genome2population.put(genome, population);
			} else {
				// TODO should evolve the population !
				
			}
			
		}
		
		// as a container, we may have received values from outside; so 
		// we will send it to our children
		// prepare the data to send
		Map<IConnection,Object> inputConnection2value = new HashMap<IConnection, Object>();
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			Object value = getInputValueForInput(c.getTo());
			
			inputConnection2value.put(c, value);
		}
		
		

		// create the container for the iteration
		messages.traceTech("creating the executable for the evaluation of this generation...", getClass());
		GeneticExplorationAlgoContainerInstance resExecIteration = new AbstractContainerExecutionIteration(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps(), // TODO another progress ? 
				inputConnection2value, 
				instance2execOriginal, // instance2execForSubtasks,
				getSuffixForCurrentIteration()
				);
		
		// create the iteration computations
		GeneticExplorationAlgoContainerInstance
		
		// TODO
		return null;
		
	}
	
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
	

}
