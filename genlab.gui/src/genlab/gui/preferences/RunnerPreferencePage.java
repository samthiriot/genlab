package genlab.gui.preferences;

import genlab.gui.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * THe page of preferences for the local runner.
 * 
 * @author Samuel Thiriot
 *
 */
public class RunnerPreferencePage 
				extends FieldEditorPreferencePage 
				implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.genlab.preferences.pages.localrunner";
	
	public static final String KEY_MAX_CPUS = PAGE_ID+".max_cpus";
			
	public RunnerPreferencePage() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init(IWorkbench workbench) {
		
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Settings for the local runner, that is the process in charge of managing the execution of workflows on your local computer.");

	}



	@Override
	protected void createFieldEditors() {
		
		IntegerFieldEditor cpusEditor = new IntegerFieldEditor(
				KEY_MAX_CPUS,
				"Max &CPUs to use", 
				getFieldEditorParent()
				);
		
		cpusEditor.setValidRange(1, Runtime.getRuntime().availableProcessors()*4);
		addField(cpusEditor);
			    
	}

	// TODO  performOk, performApply, performDefaults, performCancel
}
