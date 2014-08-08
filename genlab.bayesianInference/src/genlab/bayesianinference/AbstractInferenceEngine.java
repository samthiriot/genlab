package genlab.bayesianinference;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides basics for an implementation of an InferenceEngine, 
 * including a mapping between the implementation of an attribute and the attribute names.
 * 
 * @author Samuel Thiriot
 *
 * @param <AttributeClass>
 */
public abstract class AbstractInferenceEngine<AttributeClass extends Object> implements IInferenceEngine {

	protected Map<String, AttributeClass> attributeName2bnAttribute = new HashMap<String, AttributeClass>();
	
	
	public AbstractInferenceEngine() {
	
	}

	protected final AttributeClass getBNAttributeForAttributeName(String attributeName) {
		return attributeName2bnAttribute.get(attributeName);
		// TODO checks ?
		
	}
	
	protected final void setBNAttributeForAttributeName(String attributeName, AttributeClass bnAttribute) {
		attributeName2bnAttribute.put(attributeName, bnAttribute);
		// TODO check
	}
	
	protected abstract void buildInternalMappingForNetwork();
	
}
