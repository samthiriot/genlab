package genlab.gui.preferences;

import genlab.gui.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
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
	public static final String KEY_START_SERVER = PAGE_ID+".start_server";
	public static final String KEY_START_SERVER_PORT = PAGE_ID+".start_server_port";

	public static final String KEY_SERVERS = PAGE_ID+".servers";

	
	public RunnerPreferencePage() {
	}


	@Override
	public void init(IWorkbench workbench) {
		
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Settings for the local runner, that is the process in charge of managing the execution of workflows on your local computer.");

	}

	@Override
	protected void createFieldEditors() {
		
		{
			IntegerFieldEditor cpusEditor = new IntegerFieldEditor(
					KEY_MAX_CPUS,
					"Max &CPUs to use", 
					getFieldEditorParent()
					);
			
			cpusEditor.setValidRange(1, Runtime.getRuntime().availableProcessors()*4);
			addField(cpusEditor);
		}
		{
			BooleanFieldEditor startServerEditor = new BooleanFieldEditor(
					KEY_START_SERVER, 
					"start server", 
					getFieldEditorParent()
					);
			addField(startServerEditor);
		}
		{
			IntegerFieldEditor portServerEditor = new IntegerFieldEditor(
					KEY_START_SERVER_PORT, 
					"start on port", 
					getFieldEditorParent()
					);
			portServerEditor.setValidRange(1000, 65535);
			addField(portServerEditor);
		}
		{
			HostsListFieldEditor list = new HostsListFieldEditor(KEY_SERVERS, "servers", getFieldEditorParent());
			addField(list);
		}
	}

	// TODO  performOk, performApply, performDefaults, performCancel
}
