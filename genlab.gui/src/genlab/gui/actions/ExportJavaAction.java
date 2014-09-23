package genlab.gui.actions;

import genlab.core.exec.GenlabExecution;
import genlab.core.exporters.Genlab2JavaExporter;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.perspectives.RunPerspective;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ExportJavaAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.export.java";  
	
	public ExportJavaAction() {
		setId(ID);
		setText("export as Java");
	}
	
	
	public void run() {  
		
		IGenlabWorkflowInstance workflow = Utils.getSelectedWorflow();
		if (workflow == null)
			return;
		
		System.out.println(Genlab2JavaExporter.generateJavaForWorkflowCreation(workflow));
		
		GLLogger.infoUser("the java code for the instantiation of this workflow has been printed in the standard output", getClass());
		
		
	}  
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
