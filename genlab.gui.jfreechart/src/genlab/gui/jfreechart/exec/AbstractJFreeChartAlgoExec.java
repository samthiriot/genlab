package genlab.gui.jfreechart.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.jfreechart.algos.AbstractJFreechartAlgo;
import genlab.gui.views.AbstractViewOpenedByAlgo;

public abstract class AbstractJFreeChartAlgoExec extends AbstractOpenViewAlgoExec {

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
