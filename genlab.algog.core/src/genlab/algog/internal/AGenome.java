package genlab.algog.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cern.jet.random.Uniform;

public class AGenome {

	private AGene<?>[] genes = null;
	
	
	public final String name;
	public final Double crossoverProbability;
	
	public AGenome(String name, Double crossoverProbability) {
		this.name = name;
		this.crossoverProbability = crossoverProbability;
	}
	
	public void setGenes(Collection<AGene<?>> genesCollec) {
		
		this.genes = genesCollec.toArray(new AGene<?>[genesCollec.size()]);
	}
	
	public AGene<?>[] getGenes() {
		return genes;
	}
	
	protected AnIndividual generateARandomGenome(Uniform uniform) {
		
		Object[] ind = new Object[genes.length];
		
		for (int i=0; i<genes.length; i++) {
			
			AGene<?> gene = genes[i];
			ind[i] = gene.generateRandomnly(uniform);
		}
		
		return new AnIndividual(this, ind);//new Object[genes.length];;
		
	}
	
	public List<AnIndividual> generateInitialGeneration(Uniform uniform, int populationSize) {
		

		List<AnIndividual> population = new ArrayList<AnIndividual>(populationSize);//new Object[populationSize][];
				
		for (int n=0; n<populationSize; n++) {
			population.add(generateARandomGenome(uniform));
		}
		
		return population;
	}
	
	
	public void printToStream(PrintStream ps, List<AnIndividual> pop) {
		
		ps.println(Arrays.toString(genes));
		for (int n=0; n<pop.size(); n++) {
			AnIndividual ind = pop.get(n);
			ps.println(Arrays.toString(ind.genes));
		}
	}
	
	public String readableValues(Object[] ind) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i=0; i<genes.length; i++) {
			if (i>0)
				sb.append(",");
			sb.append(genes[i].name).append(":").append(ind[i]);
			
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}
