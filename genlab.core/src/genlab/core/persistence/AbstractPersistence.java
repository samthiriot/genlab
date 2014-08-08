package genlab.core.persistence;

import genlab.core.usermachineinteraction.GLLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Used for all persistence entry points based on xstream.
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractPersistence {

	protected final XStream xstream; 

	public AbstractPersistence() {
		
		xstream = new XStream(new StaxDriver());

	}
	
	// TODO keep, remove ?
	public void persistAsXml(Object myObject, String absoluteFilename) {
		
		GLLogger.debugTech("saving an object "+myObject.getClass().getCanonicalName()+" as XML to "+absoluteFilename, getClass());
		try {
			xstream.toXML(
					myObject,
					new PrintStream(absoluteFilename)
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the object "+myObject.getClass().getCanonicalName()+" as XML to "+absoluteFilename, getClass(), e);

		}
	}
	
	// TODO keep, remove ? 
	public Object loadAsXml(String absoluteFilename) {
		
		GLLogger.debugTech("reading an object from XML: "+absoluteFilename, getClass());
		try {
			Object myObject = xstream.fromXML(new File(absoluteFilename));
			return myObject;
		} catch (com.thoughtworks.xstream.io.StreamException e) {
			GLLogger.warnTech("was unable to load a persisted element from xml: "+absoluteFilename, getClass(), e);
			return null;
		}
	}


}
