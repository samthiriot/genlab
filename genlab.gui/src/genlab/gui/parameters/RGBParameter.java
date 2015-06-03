package genlab.gui.parameters;

import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.parameters.Parameter;

public class RGBParameter extends ParameterCreatingWidget<RGB> {

	public RGBParameter(String id, String name, String desc, RGB defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@Override
	public Map<String,Boolean> check(RGB value) {
		return Collections.EMPTY_MAP;
	}

	
	@Override
	public RGB parseFromString(String value) {
		StringTokenizer st = new StringTokenizer(value, "|");
		try {
			String type = st.nextToken();
			String a = st.nextToken();
			String b = st.nextToken();
			String c = st.nextToken();
			
			if (type.equals("rgb")) {
				// decode as RGB
				return new RGB(
						Integer.parseInt(a), 
						Integer.parseInt(b), 
						Integer.parseInt(c)
						);
			} else if (type.equals("hsb")) {
				return new RGB(
						Float.parseFloat(a),
						Float.parseFloat(b),
						Float.parseFloat(c)
						);
			} else {
				throw new RuntimeException("unknown color type "+type);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("wrong value for parameter "+getId()+"; format is 'rgb'|int|int|int or hsb|float|float|float", e);
		}
		
	}

	@Override
	public String toSaveString(Object valueRaw) {
		
		final RGB value = (RGB)valueRaw;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("rgb|");
		sb.append(value.red).append("|");
		sb.append(value.green).append("|");
		sb.append(value.blue);
		
		return sb.toString();
	}
		
	
	@Override
	public Control createWidget(final IAlgoInstance algoInstance, RGB value,
			Composite parent, FormToolkit toolkit) {
		
		final ColorSelector colorSelector = new ColorSelector(parent);
		toolkit.adapt(colorSelector.getButton(), true, true);
		colorSelector.setColorValue(value);
		
		colorSelector.addListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {

				algoInstance.setValueForParameter(getId(), event.getNewValue());

			}
		});
		
		return colorSelector.getButton();
		
	}

}
