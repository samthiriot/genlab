package genlab.gui.actions;

import genlab.core.exec.Execution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class RunAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.run";  
	
	public RunAction() {
		setId(ID);
		setText("run workflow");
		
	}
	
	
	public void run() {  
	   
		// TODO remove
		
		GLLogger.debugTech("run called", getClass());
		
		IGenlabWorkflowInstance workflow = GenlabWorkflowInstance.currentTODO;
		if (workflow == null) {
			GLLogger.warnTech("null...", getClass());
			return;
		}
		
		// check workflow
		GLLogger.infoUser("checking the workflow "+workflow+"...", getClass());

		WorkflowCheckResult checkInfo = workflow.checkForRun();
		if (checkInfo.isReady()) {
			GLLogger.infoUser("ready :-)", getClass());
		} else {
			GLLogger.errorUser("problem..;", getClass());
			return;
		}
		
		{
			ConstantValueInteger c = new ConstantValueInteger();
			IAlgoInstance ci = c.createInstance(workflow);
			ci.setValueForParameter(c.paramId, 12);
		}
		
		//IAlgo algo = ExistingAlgos.getExistingAlgos().getAlgoForClass("genlab.graphstream.algos.generators.WattsStrogatzAlgo");
		//algo.createInstance(workflow);
		
		Execution exec = new Execution();
		exec.setExecutionForced(true);

		IAlgoExecution execution =  workflow.execute(exec);

		GLLogger.infoUser("start run !", getClass());

		execution.run();

		GLLogger.infoUser("done.", getClass());

			
	}  
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
