package genlab.gui.preferences;

import genlab.gui.Activator;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
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
public class LocalRunnerPreferencePage 
				extends FieldEditorPreferencePage 
				implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.genlab.preferences.pages.localrunner";
	
	public static final String KEY_MAX_CPUS = PAGE_ID+".max_cpus";

	public static final String KEY_SERVERS = PAGE_ID+".servers";


	public LocalRunnerPreferencePage() {
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
			HostsListFieldEditor list = new HostsListFieldEditor(
					KEY_SERVERS, 
					"connect to these GenLab servers to delegate computations", 
					getFieldEditorParent()
					);
			addField(list);
		}
	}

	// TODO  performOk, performApply, performDefaults, performCancel
}
