package genlab.gui.launchconfigurations;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;

public class GenlabWorkflowLaunchProcessFactory implements IProcessFactory {

	public GenlabWorkflowLaunchProcessFactory() {

	}

	@Override
	public IProcess newProcess(ILaunch launch, Process process, String label,
			Map attributes) {
		

		return null;
	}

}
