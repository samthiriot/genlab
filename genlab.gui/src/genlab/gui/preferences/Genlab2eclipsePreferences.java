package genlab.gui.preferences;

import genlab.core.exec.client.ComputationNodes;
import genlab.core.exec.client.ServerHostPreference;
import genlab.core.exec.server.GenlabComputationServer;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;
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
				
				if (event.getProperty().startsWith(LocalRunnerPreferencePage.PAGE_ID)) {
					// event related to the local runnere preferences
					// update it
					updateGenlabSettingsFromEclipsePreferencesForLocalRunner();
				} else if (event.getProperty().startsWith(LocalServerPreferencePage.PAGE_ID)) {
					// event related to the local runnere preferences
					// update it
					updateGenlabSettingsFromEclipsePreferencesForLocalServer();
				} else if (event.getProperty().startsWith(LoggingPreferencePage.PAGE_ID)) {
					// event related to the local runnere preferences
					// update it
					updateGenlabSettingsFromEclipsePreferencesForLogging();
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
		ComputationNodes.getSingleton().setParameterLocalThreadsMax(Activator.getDefault().getPreferenceStore().getInt(LocalRunnerPreferencePage.KEY_MAX_CPUS));
		// parameters for contacting a server
		ComputationNodes.getSingleton().setParameterListOfHosts(ServerHostPreference.parseAsList(Activator.getDefault().getPreferenceStore().getString(LocalRunnerPreferencePage.KEY_SERVERS)));
		
	}
		

	protected void updateGenlabSettingsFromEclipsePreferencesForLocalServer() {

		
		// parameters for starting a server
		GenlabComputationServer.getSingleton().setParameterInterfaceToBind(Activator.getDefault().getPreferenceStore().getString(LocalServerPreferencePage.KEY_START_SERVER_INTERFACE));
		GenlabComputationServer.getSingleton().setParameterStartServerPort(Activator.getDefault().getPreferenceStore().getInt(LocalServerPreferencePage.KEY_START_SERVER_PORT));
		GenlabComputationServer.getSingleton().setParameterStartServer(Activator.getDefault().getPreferenceStore().getBoolean(LocalServerPreferencePage.KEY_START_SERVER));
		

	}
	
	protected void updateGenlabSettingsFromEclipsePreferencesForLogging() {

		// message parameters
		ListOfMessages.DEFAULT_RELAY_TO_LOG4J = Activator.getDefault().getPreferenceStore().getBoolean(LoggingPreferencePage.KEY_MESSAGE_RELAY_CONSOLE);
				
		ListsOfMessages.getGenlabMessages().setFilterIgnoreBelow(
				MessageLevel.valueOf(Activator.getDefault().getPreferenceStore().getString(LoggingPreferencePage.KEY_MESSAGE_LEVEL_TECH)),
				MessageLevel.valueOf(Activator.getDefault().getPreferenceStore().getString(LoggingPreferencePage.KEY_MESSAGE_LEVEL_USER))
				);
		
		// precise values
		try {
			String values = Activator.getDefault().getPreferenceStore().getString(LoggingPreferencePage.KEY_MESSAGE_DETAILED_LEVELS);
			for (String value: values.split(",")) {
				String[] val = value.split("=", 2);
				String classname = val[0]; 
				MessageLevel level = MessageLevel.valueOf(val[1]);
				ListOfMessages.setLevelForClassname(classname, level);
			}
		} catch (Exception e) {
			GLLogger.warnUser("error while loading preferences for detailed logging; preferences are lost", getClass());
		}
		
	}
	
	protected void updateGenlabSettingsFromEclipsePreferences() {
		

		updateGenlabSettingsFromEclipsePreferencesForLogging();
		
		updateGenlabSettingsFromEclipsePreferencesForLocalRunner();
		updateGenlabSettingsFromEclipsePreferencesForLocalServer();
			
	}

}
