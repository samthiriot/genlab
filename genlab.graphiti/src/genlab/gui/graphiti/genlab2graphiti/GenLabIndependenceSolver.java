package genlab.gui.graphiti.genlab2graphiti;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.features.impl.IIndependenceSolver;

/**
 * see http://www.eclipse.org/forums/index.php/t/266445/
 * 
 * @author B12772
 *
 */

public class GenLabIndependenceSolver implements IIndependenceSolver {

	private static Map<String, Object> objectMap = new HashMap<String, Object>();

	@Override
	public String getKeyForBusinessObject(Object bo) {
		String result = null;
		if(bo != null) {
			result = String.valueOf(bo.hashCode());
			
			if(!objectMap.containsKey(result))
				objectMap.put(result, bo);
		}
		return result;
	}

	@Override
	public Object getBusinessObjectForKey(String key) {
		return objectMap.get(key);
	}

}
