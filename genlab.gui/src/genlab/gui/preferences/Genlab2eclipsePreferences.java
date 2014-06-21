package genlab.gui.preferences;

import genlab.core.exec.LocalComputationNode;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Activator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * Maps eclipse preferences with genlab settings. 
 * 
 * @see http://www.vogella.com/tutorials/EclipsePreferences/article.html
 * 
 * @author Samuel Thiriot
 *
 */
public class Genlab2eclipsePreferences {

	public static final Genlab2eclipsePreferences singleton = new Genlab2eclipsePreferences();
	
	private Genlab2eclipsePreferences() {
		// TODO Auto-generated constructor stub
		
		Activator.getDefault().getPreferenceStore()
		  .addPropertyChangeListener(new IPropertyChangeListener() {
		
		
			@Override
			public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
				
				if (event.getProperty().startsWith(RunnerPreferencePage.PAGE_ID)) {
					// event related to the local runnere preferences
					// update it
					updateGenlabSettingsFromEclipsePreferencesForLocalRunner();
				}
					
		    }
		  }); 
	
	}
	
	
	/**
	 * Loads the eclipse preferences and maps them to genlab settings.
	 * Called at startup
	 */
	public void atStartup() {
		
		GLLogger.infoUser("loading preferences and updating genlab settings", getClass());
		updateGenlabSettingsFromEclipsePreferences();
		
	}
	
	public Integer getLocalRunnerMaxCpusCount() {
		
		return Activator.getDefault().getPreferenceStore().getInt(RunnerPreferencePage.KEY_MAX_CPUS);
		
	}
	
	protected void updateGenlabSettingsFromEclipsePreferencesForLocalRunner() {
		
		LocalComputationNode.getSingleton().setCpusCount(getLocalRunnerMaxCpusCount());
	
	}
		
	
	protected void updateGenlabSettingsFromEclipsePreferences() {
		

		updateGenlabSettingsFromEclipsePreferencesForLocalRunner();
			
	}

}
