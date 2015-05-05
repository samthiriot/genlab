package genlab.gui.preferences;

import genlab.core.exec.client.ComputationNodes;
import genlab.core.exec.client.ServerHostPreference;
import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Activator;

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
	

	protected void updateGenlabSettingsFromEclipsePreferencesForLocalRunner() {
		
		// parameters for local runner
		ComputationNodes.getSingleton().setParameterLocalThreadsMax(Activator.getDefault().getPreferenceStore().getInt(RunnerPreferencePage.KEY_MAX_CPUS));
		
		// parameters for starting a server
		// TODO start server
		GenlabComputationServer.getSingleton().setParameterStartServerPort(Activator.getDefault().getPreferenceStore().getInt(RunnerPreferencePage.KEY_START_SERVER_PORT));
		GenlabComputationServer.getSingleton().setParameterStartServer(Activator.getDefault().getPreferenceStore().getBoolean(RunnerPreferencePage.KEY_START_SERVER));
	
		// parameters for contacting a server
		ComputationNodes.getSingleton().setParameterListOfHosts(ServerHostPreference.parseAsList(Activator.getDefault().getPreferenceStore().getString(RunnerPreferencePage.KEY_SERVERS)));
		
		
	}
		
	
	protected void updateGenlabSettingsFromEclipsePreferences() {
		

		updateGenlabSettingsFromEclipsePreferencesForLocalRunner();
			
	}

}
