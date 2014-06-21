package genlab.gnuplot.algos;

import org.osgi.framework.Bundle;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.gnuplot.Activator;

public abstract class GnuplotAbstractAlgo extends BasicAlgo {

	public GnuplotAbstractAlgo(String name, String description,
			AlgoCategory category) {
		super(
				name, 
				description, 
				category, 
				"/icons/gnuplot"+IMAGE_PATH_PLACEHOLDER_SIZE+".png", 
				"/icons/gnuplot.svg"
				);

	}


	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
