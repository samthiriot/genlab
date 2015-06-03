package genlab.gui.parameters;


import genlab.core.model.instance.IAlgoInstance;
import genlab.core.parameters.Parameter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Fancy parameters that self create the widget in the parameters view. 
 * Enable each plugin to really provide its own parameters without being dependant 
 * of the limited set of parameters provided in the core.
 * 
 * @author Samuel Thiriot
 *
 * @param <Type>
 */
public abstract class ParameterCreatingWidget<Type extends Object> extends Parameter<Type> {

	public ParameterCreatingWidget(String id, String name, String desc, Type defaultValue) {
		super(id, name, desc, defaultValue);
	}

	/**
	 * Creates the widget ready to be integrated; listens itself to its changes and 
	 * adapts algo instance parameters.
	 * @param algoInstance
	 * @param value
	 * @param parent
	 * @param toolkit
	 */
	public abstract Control createWidget(
			IAlgoInstance algoInstance, 
			Type value,
			Composite parent,
			FormToolkit toolkit
			);
	

}
