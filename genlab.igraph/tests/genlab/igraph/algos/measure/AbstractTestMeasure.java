package genlab.igraph.algos.measure;

import genlab.core.exec.Execution;
import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Rigraph.RIGraphLibImplementation;
import genlab.igraph.commons.IGraphLibImplementation;
import genlab.igraph.natjna.IGraphLibImplementationNative;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.jna.Native;

/**
 * Tests a metric on random networks. Ensures it can be computed,
 * it changes for different graphs and is similar for the same graphs.
 * 
 * @author Samuel Thiriot
 *
 */
@RunWith(Parameterized.class)
public abstract class AbstractTestMeasure<MeasureType extends Object> {

	// parameterized
	protected IGraphLibImplementation lib = null;
		
    public AbstractTestMeasure(IGraphLibImplementation lib) {
    	
    	if (lib != null)
    		this.lib = lib;
    	
    }

    @Parameters
    public static Collection<Object[]> generateData() {
    	
	    return Arrays.asList(new Object[][] {
	    		{ 
	    			new RIGraphLibImplementation() 
	    		}, 
	    		{ 
	    			new IGraphLibImplementationNative() 
	    		}
	    	} 
	    );
    }
    
    @Before
    public void setup() {
    	

    }

    @After
    public void teardown() {
    	
    }
    

    private IExecution getExecution() {
    	return new Execution();
    }
    
    private long getSeed() {
    	Long seed = System.currentTimeMillis();
		return (long)seed.intValue();
    }
    
    protected abstract MeasureType applyMeasure(IGenlabGraph graph, IGraphLibImplementation lib, IExecution exec);

    protected abstract boolean checkMeasuresEqual(MeasureType m1, MeasureType m2);
    protected abstract boolean checkMeasuresDifferent(MeasureType m1, MeasureType m2);
    
    protected IGenlabGraph generateTestGraph(Long seed, IExecution exec) {
    	
    	return lib.generateWattsStrogatz(500, 1, 0.05, 2, false, false, exec, seed);
    }


    @Test(timeout = 1000000) // TODO !
    public void testMeasureOnOneGraphs() {

    	long seed1 = getSeed();
		IExecution exec = getExecution();
		
		// create 2 graphs with same seed
		IGenlabGraph g1 = generateTestGraph(seed1, exec);
		
		// apply measure
		MeasureType m1 = applyMeasure(g1, lib, exec);
		
		Assert.assertNotNull(m1);
		
	}
    

    @Test(timeout = 10000)
    public void compareMeasureWithOtherLibs() {

    	long seed1 = getSeed();
		IExecution exec = getExecution();
		
		// create 2 graphs with same seed
		IGenlabGraph g1 = generateTestGraph(seed1, exec);
		
		// apply measure
		IGraphLibImplementation lib1 = new RIGraphLibImplementation();
		IGraphLibImplementation lib2 = new IGraphLibImplementationNative();
		MeasureType m1 = applyMeasure(g1, lib1, exec);
		MeasureType m2 = applyMeasure(g1, lib2, exec);
		
		Assert.assertTrue(
				"the measure one the same graph by two different libraries should be the same "+m1+"; "+m2,
				checkMeasuresEqual(m1, m2)
				);
		
	}
    
    @Test(timeout = 10000)
    public void testMeasureOnTwoIsometricGraphs() {

    	long seed1 = getSeed();
		IExecution exec = getExecution();
		
		// create 2 graphs with same seed
		IGenlabGraph g1 = generateTestGraph(seed1, exec);
		IGenlabGraph g2 = generateTestGraph(seed1, exec);

		// apply measure
		MeasureType m1 = applyMeasure(g1, lib, exec);
		MeasureType m2 = applyMeasure(g2, lib, exec);
		
		Assert.assertTrue(
				"the measure applied on two isometric graphs should be similar",
				checkMeasuresEqual(m1, m2)
				);
		
	}
    
    @Test(timeout = 10000)
    public void testMeasureOnTwoDifferentGraphs() {

    	long seed1 = getSeed();
    	long seed2 = getSeed();
		while (seed1 == seed2) {
			seed2 = getSeed();
		}
		
		IExecution exec = getExecution();
		
		// create 2 graphs with same seed
		IGenlabGraph g1 = generateTestGraph(seed1, exec);
		IGenlabGraph g2 = generateTestGraph(seed2, exec);

		// apply measure
		MeasureType m1 = applyMeasure(g1, lib, exec);
		MeasureType m2 = applyMeasure(g2, lib, exec);
		
		Assert.assertTrue(
				"the measure applied on two non isometric graphs should be different",
				checkMeasuresDifferent(m1, m2)
				);
		
	}
	
}
