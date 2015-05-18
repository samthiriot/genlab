package genlab.gui.preferences;

import genlab.core.usermachineinteraction.MessageLevel;
import genlab.gui.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * THe page of preferences for the local runner.
 * 
 * @author Samuel Thiriot
 *
 */
public class LoggingPreferencePage 
				extends FieldEditorPreferencePage 
				implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.genlab.preferences.pages.logging";
	
	public static final String KEY_MESSAGE_RELAY_CONSOLE = PAGE_ID+".relay_to_log4j";

	public static final String KEY_MESSAGE_LEVEL_TECH = PAGE_ID+".message_level_tech";
	public static final String KEY_MESSAGE_LEVEL_USER = PAGE_ID+".message_level_user";

	public static final String KEY_MESSAGE_DETAILED_LEVELS = PAGE_ID+".detailed_per_emittor";


	public LoggingPreferencePage() {
	}


	@Override
	public void init(IWorkbench workbench) {
		
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Configure the type and level of messages you're interested in for GenLab messages.");

	}
	
	public static String[][] getListOfOptionsForMessageLevels() {
		
		String[][] res = new String[MessageLevel.values().length][];
		
		MessageLevel[] levels = MessageLevel.values();
		
		for (int i=0; i<levels.length; i++) {
			res[i] = new String[] {levels[i].toString(), levels[i].toString()};
		}
		
		return res;
		
	}
	
	@Override
	protected void createFieldEditors() {

		{
			ComboFieldEditor defaultLevelUser = new ComboFieldEditor(
					KEY_MESSAGE_LEVEL_USER, 
					"see user messages more important than  ", 
					getListOfOptionsForMessageLevels(), 
					getFieldEditorParent()
					);
			addField(defaultLevelUser);
		}
		{
			ComboFieldEditor defaultLevelTech = new ComboFieldEditor(
					KEY_MESSAGE_LEVEL_TECH, 
					"see technical messages more important than ", 
					getListOfOptionsForMessageLevels(), 
					getFieldEditorParent()
					);
			addField(defaultLevelTech);
		}

		{
			BooleanFieldEditor relayLog4jEditor = new BooleanFieldEditor(
					KEY_MESSAGE_RELAY_CONSOLE, 
					"forward messages to log4j (typically writes messages in console / standard output streams)", 
					getFieldEditorParent()
					);
			addField(relayLog4jEditor);
		}
		
		{ 
			MessageLevelForEmittersFieldEditor details = new MessageLevelForEmittersFieldEditor(
					KEY_MESSAGE_DETAILED_LEVELS, 
					"fine tuning of messages you want to listen to", 
					getFieldEditorParent()
					);
			addField(details);
		}
	}

}
