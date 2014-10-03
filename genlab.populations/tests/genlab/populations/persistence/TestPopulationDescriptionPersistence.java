package genlab.populations.persistence;

import static org.junit.Assert.*;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.Test;

public class TestPopulationDescriptionPersistence {


	@Test
	public void testReadWrite() {
		
		File f;
		try {
			f = File.createTempFile("junit_populations_persistence", PopulationDescriptionPersistence.EXTENSION);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
		// create

		PopulationDescription desc = new PopulationDescription();
		
		AgentType type1 = new AgentType("type1", "");
		type1.addAttribute(new Attribute("weather", AttributeType.STRING));
		type1.addAttribute(new Attribute("rain", AttributeType.STRING));
		desc.addAgentType(type1);
		
		AgentType type2 = new AgentType("type 2 à %", "");
		type2.addAttribute(new Attribute("att1 à truc", AttributeType.STRING));
		type2.addAttribute(new Attribute("b12%[\"", AttributeType.STRING));
		desc.addAgentType(type2);
		
		type2.addInheritedTypes(type1);
		
		// try save
		PopulationDescriptionPersistence.singleton.writeToFile(desc, f);
		
		// try to load
		PopulationDescription loaded = PopulationDescriptionPersistence.singleton.readFromFile(f);
			
		// ensure they have the same content
		assertEquals(desc.getAllAgentTypes().size(), loaded.getAllAgentTypes().size());
		
		Iterator<IAgentType> it1 = desc.getAllAgentTypes().iterator();
		Iterator<IAgentType> it2 = loaded.getAllAgentTypes().iterator();
		while (it1.hasNext()) {
			IAgentType typeOrigin = it1.next();
			IAgentType typeLoaded = it2.next();
			assertEquals(typeOrigin.getName(), typeLoaded.getName());
			assertEquals(typeOrigin.getAllAttributesCount(), typeLoaded.getAllAttributesCount());
			assertEquals(typeOrigin.getLocalAttributesCount(), typeLoaded.getLocalAttributesCount());
			assertEquals(typeOrigin.getInheritedTypes().size(), typeLoaded.getInheritedTypes().size());
		}
		
	}

}
