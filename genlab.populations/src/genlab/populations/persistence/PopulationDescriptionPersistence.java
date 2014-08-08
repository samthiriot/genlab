package genlab.populations.persistence;

import genlab.core.persistence.AbstractPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.populations.Activator;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.PopulationDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.eclipse.core.runtime.Platform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Entry point for the persistence of populationd descriptions
 * 
 * @author Samuel Thiriot
 *
 */
public final class PopulationDescriptionPersistence extends AbstractPersistence {

	public static final String EXTENSION = ".popdesc"; 

	public static final String XML_TAG_POPDESC = "populationdescription";
	
	public static final PopulationDescriptionPersistence singleton = new PopulationDescriptionPersistence();
	
	private PopulationDescriptionPersistence() {
		
		super();
		
		// init xtream
		GLLogger.debugTech("initializing xstream for persistence...", getClass());
		try {
				
			// local load of classes
			xstream.setClassLoader(Activator.class.getClassLoader());
			
			
			xstream.alias(PopulationDescriptionConverter.XML_TAG_TYPE, IAgentType.class);
			xstream.alias(XML_TAG_POPDESC, genlab.populations.bo.PopulationDescription.class);
			xstream.alias(AgentTypeConverter.XML_TAG_ATTRIBUTE, Attribute.class);
			
			xstream.registerConverter(new PopulationDescriptionConverter());
			xstream.registerConverter(new AgentTypeConverter());
			xstream.registerConverter(new AttributeConverter());
					
			// TODO add converters for links 
			
		} catch (Exception e) {
			GLLogger.errorTech("error when initializing xstream persitence.", getClass(), e);
		}
	}

	public PopulationDescription readFromFile(File f) {
		
		return (PopulationDescription)xstream.fromXML(f);

	}
	
	public void writeToFile(PopulationDescription popDesc, File f) {
		
		GLLogger.debugTech("saving population description as XML to "+f, getClass());
		try {
			xstream.toXML(
					popDesc,
					new PrintStream(f)
					);
		} catch (FileNotFoundException e) {
			GLLogger.errorTech("error while saving the population description as XML to "+f, getClass(), e);

		}
	}
	
}
