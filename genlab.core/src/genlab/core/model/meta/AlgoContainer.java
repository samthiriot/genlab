package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

/**
 * An algo container, that is a container of algos (like a loop for instance)
 * 
 * @author Samuel Thiriot
 */
public abstract class AlgoContainer extends BasicAlgo {

	public AlgoContainer(String name, String description,
			String longHtmlDescription, String categoryId, String imagePath) {
		super(name, description, longHtmlDescription, categoryId, imagePath);
	}

	public AlgoContainer(String name, String description, String categoryId) {
		super(name, description, categoryId);
	}

	

}
