package genlab.gui.jfreechart.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;

public abstract class AbstractJFreechartAlgo extends BasicAlgo {

	public final String eclipseViewId;
	
	public AbstractJFreechartAlgo(String name, String description, String eclipseViewId) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.DISPLAY, 
				null,
				null
				);
	
		this.eclipseViewId = eclipseViewId;
		
	}


}
