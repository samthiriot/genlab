package genlab.gui.launchconfigurations;

import genlab.core.commons.WrongParametersException;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

public class GenlabWorkflowLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup implements
		ILaunchConfigurationTabGroup {

	public GenlabWorkflowLaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		if (!mode.equals(org.eclipse.debug.core.ILaunchManager.RUN_MODE)) 
			throw new WrongParametersException("can only accept run mode: '"+mode+"'");
		
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[2];
		
		tabs[0] = new GenlabWorkflowLaunchConfigurationTabFirst();
		tabs[1] = new CommonTab();
		
		setTabs(tabs);
		
	}

	
	
}
