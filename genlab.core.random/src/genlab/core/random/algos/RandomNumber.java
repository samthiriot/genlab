package genlab.core.random.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.InputOutput;

public class RandomNumber extends BasicAlgo {

	
	public RandomNumber(String name, String description, String longHtmlDescription, String categoryId, String imagePath) {
		super(name, description, longHtmlDescription, categoryId, imagePath);
		// TODO Auto-generated constructor stub
	}

	public RandomNumber(String name, String description, String categoryId) {
		super(name, description, categoryId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}

}
