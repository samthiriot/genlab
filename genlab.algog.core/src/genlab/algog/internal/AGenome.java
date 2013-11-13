package genlab.algog.internal;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import cern.jet.random.Uniform;

public class AGenome {

	AGene<?>[] genes = null;
	
	public final String name;
	
	public AGenome(String name) {
		this.name = name;
	}
	
	public void setGenes(Collection<AGene> genesCollec) {
		
		this.genes = genesCollec.toArray(new AGene<?>[genesCollec.size()]);
	}
	
	public AGene<?>[] getGenes() {
		return genes;
	}
	
	protected Object[] generateARandomGenome(Uniform uniform) {
		
		
		Object[] values = new Object[genes.length];
		
		for (int i=0; i<genes.length; i++) {
			
			AGene<?> gene = genes[i];
			values[i] = gene.generateRandomnly(uniform);
					
		}
		
		return values;
		
	}
	
	public Object[][] generateInitialGeneration(Uniform uniform, int populationSize) {
		

		Object[][] population = new Object[populationSize][];
				
		for (int n=0; n<populationSize; n++) {
			population[n] = generateARandomGenome(uniform);
		}
		
		return population;
	}
	
	
	public void printToStream(PrintStream ps, Object[][] pop) {
		
		ps.println(Arrays.toString(genes));
		for (int n=0; n<pop.length; n++) {
			Object[] genome = pop[n];
			ps.println(Arrays.toString(genome));
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}
