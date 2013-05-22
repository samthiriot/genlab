package genlab.gui.graphiti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import genlab.basics.workflow.GenlabWorkflow;
import genlab.core.algos.AlgoInstance;
import genlab.core.projects.GenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class PersistenceUtils {

	private XStream xstream = null;
	
	private final static PersistenceUtils singleton = new PersistenceUtils();
	
	public static PersistenceUtils getPersistenceUtils() {
		return singleton;
	}
	
	private PersistenceUtils() {
		
		try {
			xstream = new XStream(new StaxDriver());
	
			xstream.alias("project", GenlabProject.class);
			xstream.alias("workflow", GenlabWorkflow.class);
			xstream.alias("algoinstance", AlgoInstance.class);

		} catch (Exception e) {
			GLLogger.errorTech("error when initializing xstream persitence.", getClass(), e);
		}
	}
	
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
