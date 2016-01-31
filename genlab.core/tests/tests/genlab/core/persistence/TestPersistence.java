package tests.genlab.core.persistence;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import genlab.core.model.instance.Connection;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowListener;
import genlab.core.model.meta.GenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.GenlabProject;
import genlab.core.projects.IGenlabProject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestPersistence {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	protected void compareWorkflows(IGenlabWorkflowInstance workflow , IGenlabWorkflowInstance workflowReaden) {
		
		assertNotSame(workflow, workflowReaden);
		assertEquals(workflow.getAbsolutePath(), workflowReaden.getAbsolutePath());
		
		assertNotSame(workflow.getAlgoInstances(), workflowReaden.getAlgoInstances());
		assertEquals(workflow.getAlgoInstances().size(), workflowReaden.getAlgoInstances().size());
		
		assertEquals(workflow.getId(), workflowReaden.getId());
		
		// TODO !!! test algo not null for workflow instances
		//assertNotNull(workflowReaden.getAlgo());
		//assertEquals(workflow.getAlgo().getId(), workflowReaden.getAlgo().getId());
		
		// check algo instances
		for (IAlgoInstance a : workflow.getAlgoInstances()) {
			assertTrue(workflow.containsAlgoInstance(a));
			assertTrue(workflow.containsAlgoInstanceId(a.getId()));
			assertTrue(workflowReaden.containsAlgoInstanceId(a.getId()));
			final IAlgoInstance aReaden = workflowReaden.getAlgoInstanceForId(a.getId());
			assertNotNull(aReaden);
			assertEquals(a.getId(), aReaden.getId());
			assertEquals(a.getAlgo().getId(), aReaden.getAlgo().getId());
		}
		
		// check connections
		assertEquals("wrong connection count", workflow.getConnections().size(), workflowReaden.getConnections().size());
		for (Connection c: workflow.getConnections()) {
			
			assertTrue(workflow.isConnected(c.getFrom(), c.getTo()));
			
			/*
			assertTrue(
				workflowReaden.isConnected(
						c.getFrom().getAlgoInstance().getInputInstanceForInput(c.getFrom().getMeta()), 
						c.getTo().getAlgoInstance().getInputInstanceForInput(c.getTo().getMeta())
						)
					);
			*/
		}
		
	}
	
	@Test
	public void testEmptyWorkflow() {
		
		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow("workflowAA", "my desc", "my workflow");
		assertNotNull(workflow);
		
		// store it
		GenlabPersistence.getPersistence().saveWorkflow(workflow);
		
		// check project and workflow files exists
		assertTrue((new File(workflow.getAbsolutePath())).exists());
		
		// read it again
		IGenlabWorkflowInstance workflowReaden = GenlabPersistence.getPersistence().readWorkflow(workflow.getAbsolutePath());
		
		// compare
		compareWorkflows(workflow, workflowReaden);
		
	}
	

	

}
