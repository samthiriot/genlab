package genlab.gui.preferences;

import genlab.core.exec.server.GenlabComputationServer;
import genlab.gui.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class RunnerPreferencesInitializer extends AbstractPreferenceInitializer {

	public RunnerPreferencesInitializer() {

	}

	@Override
	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(RunnerPreferencePage.KEY_MAX_CPUS, Runtime.getRuntime().availableProcessors());
		store.setDefault(RunnerPreferencePage.KEY_START_SERVER, false);
		store.setDefault(RunnerPreferencePage.KEY_START_SERVER_PORT, GenlabComputationServer.DEFAULT_PORT);
		
		// TODO
		store.setDefault(RunnerPreferencePage.KEY_SERVERS, Boolean.FALSE.toString()+"|192.168.0.1:"+GenlabComputationServer.DEFAULT_PORT);


	}

}
