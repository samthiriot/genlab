package genlab.core.parameters;

import java.util.Collections;
import java.util.List;

/**
 * A parameter in which a value is chosen among a list of possible values
 * 
 * @author Samuel Thiriot
 *
 */
public class ListParameter extends NumberParameter<Integer> {

	private transient List<String> items = null;
	
	public ListParameter(String id, String name, String desc) {
		super(id, name, desc, 0);
		this.items = Collections.EMPTY_LIST;
		minValue = 0;
		maxValue = 0;
		step = 1;
	}

	
	public ListParameter(String id, String name, String desc,
			Integer defaultValue, List<String> items) {
		super(id, name, desc, defaultValue);
		this.items = items;
		minValue = 0;
		maxValue = Math.max(0, items.size() - 1);
		step = 1;
	}

	@Override
	public Integer parseFromString(String value) {
		return Integer.parseInt(value);
	}
	
	public final List<String> getItems() {
		return items;
	}
	
	public final String[] getItemsAsArray() {
		
		String[] s = new String[items.size()];
		return items.toArray(s);
	}
	
	public final String getLabel(Integer idx) {
		return items.get(idx);
	}
	
	public final void setItems(List<String> items) {
		this.items = items;
	}

}
