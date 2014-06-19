package genlab.gui.jfreechart.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.gui.algos.AbstractOpenViewContinuousAlgoExec;
import genlab.gui.jfreechart.algos.AbstractJFreechartAlgo;

public abstract class AbstractJFreeChartAlgoExec extends AbstractOpenViewContinuousAlgoExec {

	protected final AbstractJFreechartAlgo algo;
	
	public AbstractJFreeChartAlgoExec(
			IExecution exec, 
			IAlgoInstance algoInst) {
		
		super(
				exec, 
				algoInst, 
				((AbstractJFreechartAlgo) algoInst.getAlgo()).eclipseViewId
				);

		this.algo = (AbstractJFreechartAlgo) algoInst.getAlgo();
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	


}
