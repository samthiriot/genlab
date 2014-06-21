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
		
		store.setDefault(
	    		RunnerPreferencePage.KEY_MAX_CPUS, 
	    		Runtime.getRuntime().availableProcessors()
	    		);


	}

}
