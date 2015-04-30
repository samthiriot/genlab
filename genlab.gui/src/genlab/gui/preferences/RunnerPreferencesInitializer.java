package genlab.gui.preferences;

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
		store.setDefault(RunnerPreferencePage.KEY_START_SERVER_PORT, 25555);
		store.setDefault(RunnerPreferencePage.KEY_SERVER_PORT, 25555);
		store.setDefault(RunnerPreferencePage.KEY_SERVER_CONNECT, false);
		store.setDefault(RunnerPreferencePage.KEY_SERVER_HOSTNAME, "192.168.0.1");
		

	}

}
