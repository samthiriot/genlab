package genlab.core.model.meta;

import java.io.PrintStream;

/**
 * Objects which can be displayed as text; "dump" refers to a nearly complete
 * display of the content.
 *  
 * @author Samuel Thiriot
 *
 */
public interface IDumpableToText {

	public void dumpAsText(PrintStream ps);

}
