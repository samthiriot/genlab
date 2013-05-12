package genlab.core.commons;

/**
 * An error on the program itself, that reached a state that is not 
 * supposed to occur. Reflects a problem in the coherency of the soft.
 * 
 * @author Samuel Thiriot
 *
 */
public class ProgramException extends GenLabException {

	public ProgramException() {
		// TODO Auto-generated constructor stub
	}

	public ProgramException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ProgramException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ProgramException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
