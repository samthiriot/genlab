package genlab.gui.views;

import genlab.core.model.instance.IAlgoInstance;

import org.eclipse.ui.IWorkbenchPart;

/**
 * Associated with any workbench part which displays something related to an algorithm instance.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public interface IViewAttachedToAlgoInstance extends IWorkbenchPart {

	public IAlgoInstance getAlgoInstance();
	
}
