package genlab.gui.parameters;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.parameters.ColorRGBParameterValue;
import genlab.gui.Utils;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RGBParameter extends ParameterCreatingWidget<ColorRGBParameterValue> {

	public RGBParameter(String id, String name, String desc, ColorRGBParameterValue defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@Override
	public Map<String,Boolean> check(ColorRGBParameterValue value) {
		return Collections.EMPTY_MAP;
	}

	
	@Override
	public ColorRGBParameterValue parseFromString(String value) {
		return new ColorRGBParameterValue(value);
	}
	
	@Override
	public Control createWidget(final IAlgoInstance algoInstance, ColorRGBParameterValue value,
			Composite parent, FormToolkit toolkit) {
		
		final ColorSelector colorSelector = new ColorSelector(parent);
		toolkit.adapt(colorSelector.getButton(), true, true);
		colorSelector.setColorValue(Utils.getRGB(value));
		
		colorSelector.addListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {

				algoInstance.setValueForParameter(
						getId(), 
						Utils.getColorRGBParameterValue((RGB) event.getNewValue())
						);

			}
		});
		
		return colorSelector.getButton();
		
	}

}
