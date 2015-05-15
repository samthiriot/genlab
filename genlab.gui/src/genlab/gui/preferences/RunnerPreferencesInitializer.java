package genlab.gui.preferences;

import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.gui.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class RunnerPreferencesInitializer extends AbstractPreferenceInitializer {

	public RunnerPreferencesInitializer() {

	}

	@Override
	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(LocalRunnerPreferencePage.KEY_MAX_CPUS, Runtime.getRuntime().availableProcessors());
		
		store.setDefault(LocalServerPreferencePage.KEY_START_SERVER, false);
		store.setDefault(LocalServerPreferencePage.KEY_START_SERVER_PORT, GenlabComputationServer.DEFAULT_PORT);
		store.setDefault(LocalServerPreferencePage.KEY_START_SERVER_INTERFACE, "automatic");
				
		store.setDefault(LocalRunnerPreferencePage.KEY_SERVERS, Boolean.FALSE.toString()+"|192.168.0.1:"+GenlabComputationServer.DEFAULT_PORT);

		store.setDefault(LoggingPreferencePage.KEY_MESSAGE_RELAY_CONSOLE, false);
		store.setDefault(LoggingPreferencePage.KEY_MESSAGE_LEVEL_USER, MessageLevel.TIP.toString());
		store.setDefault(LoggingPreferencePage.KEY_MESSAGE_LEVEL_TECH, MessageLevel.ERROR.toString());
		

	}

}
