package genlab.bayesianinference.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;

public abstract class AbstractInflibAlgo extends BasicAlgo {

	public AbstractInflibAlgo(String name, String description,
			AlgoCategory category, String imagePath, String imagePathBig) {
		super(name, description, category, imagePath, imagePathBig);
		
	}

	public AbstractInflibAlgo(String name, String description, String categoryId) {
		super(name, description, categoryId);
	}

	

}
