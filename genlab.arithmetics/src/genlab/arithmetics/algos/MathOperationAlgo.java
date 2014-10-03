package genlab.arithmetics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;

// TODO mathalgo
public class MathOperationAlgo extends BasicAlgo {

	public MathOperationAlgo(String name, String description,
			AlgoCategory category, String imagePath, String imagePathBig) {
		super(name, description, category, imagePath, imagePathBig);
		// TODO Auto-generated constructor stub
	}

	public MathOperationAlgo(String name, String description, String categoryId) {
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
