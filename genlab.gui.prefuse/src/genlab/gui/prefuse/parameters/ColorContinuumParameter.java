package genlab.gui.prefuse.parameters;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.gui.Utils;
import genlab.gui.parameters.ParameterCreatingWidget;
import genlab.gui.prefuse.PrefuseUtils;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import prefuse.util.ColorLib;

public class ColorContinuumParameter extends ParameterCreatingWidget<ParamValueContinuum> {

	
	public ColorContinuumParameter(
			String id, 
			String name, 
			String desc,
			ParamValueContinuum 
			defaultValue
			) {
		super(id, name, desc, defaultValue);
	}
	
	
	@Override
	public Control createWidget(
			final IAlgoInstance algoInstance, 
			final ParamValueContinuum value, 
			Composite parent, 
			FormToolkit toolkit) {
		
		// create the host composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		toolkit.adapt(composite, true, true);

		// create widget for color 1
		final ColorSelector colorSelector1 = new ColorSelector(composite);
		toolkit.adapt(colorSelector1.getButton(), true, true);
		colorSelector1.setColorValue(Utils.getRGB(value.value1));
		colorSelector1.addListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {

				value.value1 = Utils.getColorRGBParameterValue((RGB) event.getNewValue()); 
				algoInstance.setValueForParameter(
						getId(), 
						value
						);

			}
		});

		// create widget for color 1
		final ColorSelector colorSelector2 = new ColorSelector(composite);
		toolkit.adapt(colorSelector2.getButton(), true, true);
		colorSelector2.setColorValue(Utils.getRGB(value.value2));
		colorSelector2.addListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {

				value.value2 = Utils.getColorRGBParameterValue((RGB) event.getNewValue()); 
				algoInstance.setValueForParameter(
						getId(), 
						value
						);


			}
		});

		return composite;
	}

	@Override
	public Map<String, Boolean> check(ParamValueContinuum value) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_MAP;
	}

	@Override
	public ParamValueContinuum parseFromString(String value) {
		throw new NotImplementedException();
	}

}
