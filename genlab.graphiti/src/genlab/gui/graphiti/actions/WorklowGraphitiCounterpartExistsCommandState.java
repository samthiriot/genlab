package genlab.gui.graphiti.actions;

import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

/**
 * Defines wether we display or not the menu for creating the graphical counterpart of 
 * a genlab workflow.
 * The idea is to enable this command only if the graphical counterpart does not exists.
 * 
 * @author sam
 */
public class WorklowGraphitiCounterpartExistsCommandState extends AbstractSourceProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getProvidedSourceNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
