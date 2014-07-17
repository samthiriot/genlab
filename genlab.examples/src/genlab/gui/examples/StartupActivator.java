package genlab.gui.examples;

import genlab.examples.gui.creation.ExamplesCreation;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class StartupActivator implements IStartup {

	public StartupActivator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void earlyStartup() {

		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				ExamplesCreation.createAllWorkflowExamples();
				
			}
		});
		
	}

}
