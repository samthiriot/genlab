package genlab.gui.handlers;

import genlab.gui.Utils;
import genlab.gui.wizards.NewWorkflowWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

// TODO is it the right way to do it ? 
public class CreateWorflowHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Utils.openWizard("genlab.gui.wizards.newworkflow");
		
		System.err.println(" should create a workflow :-); ");
		// TODO Auto-generated method stub
		return null;
	}

}
