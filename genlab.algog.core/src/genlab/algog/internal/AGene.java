package genlab.algog.internal;

import cern.jet.random.Uniform;

/**
 * TODO  use a plugable random generator !
 * 
 * @author Samuel THiriot
 *
 * @param <TypeName>
 */
public abstract class AGene<TypeName extends Number> {

	public final String name;
	
	public final TypeName min;
	public final TypeName max;
	
	public AGene(String name, TypeName min, TypeName max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}
	
	public abstract TypeName generateRandomnly(Uniform uniform);
	
	@Override
	public String toString() {
		return name;
	}
	
}
