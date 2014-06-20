package genlab.gui.jfreechart.algos;

import genlab.core.model.meta.AlgoCategory;
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
	
	public AbstractJFreechartAlgo(String name, String description, String eclipseViewId, AlgoCategory algoCategory) {
		super(
				name, 
				description, 
				algoCategory, 
				null,
				null
				);
	
		this.eclipseViewId = eclipseViewId;
		
	}


}
