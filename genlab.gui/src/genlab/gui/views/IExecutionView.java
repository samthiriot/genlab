package genlab.gui.views;

import genlab.core.exec.IExecution;

import org.eclipse.ui.IViewPart;

/**
 * Tags views created because of execution
 * 
 * @author Samuel Thiriot
 *
 */
public interface IExecutionView extends IViewPart {

	public IExecution getExecution();
	
}
