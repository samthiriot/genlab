package genlab.core.model.doc;

import genlab.core.commons.WrongParametersException;

/**
 * Describes an author of a plugin, library or piece of code.
 * 
 * @author Samuel Thiriot
 *
 */
public class DocAuthor {

	public final String id;
	public final String name;
	public final String email;
	public final String webpage;
	
	public DocAuthor(String id, String name, String email, String webpage) {
		super();
		if (name == null)
			throw new WrongParametersException("the name of an author can not be null");
		if (id == null)
			throw new WrongParametersException("the id of an author can not be null");
		this.id = id;
		this.name = name;
		this.email = email;
		this.webpage = webpage;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
