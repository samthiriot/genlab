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
	
	protected GenlabProject createEmptyProject() {
		
		TemporaryFolder tmpDir = new TemporaryFolder();
		try {
			tmpDir.create();
		} catch (IOException e) {
			e.printStackTrace();
			fail("unable to create tmp directory");
		}
		
		return new GenlabProject(tmpDir.getRoot().getAbsolutePath());

		
	}
	
	protected void compareProjects(GenlabProject project, IGenlabProject projectReaden) {
		
		assertNotSame(project, projectReaden);
		assertEquals("wrong directory", project.getBaseDirectory(), projectReaden.getBaseDirectory());
		assertEquals("wrong file", project.getProjectSavingFilename(), projectReaden.getProjectSavingFilename());
		assertEquals("wrong number of workflows", project.getWorkflows().size(), projectReaden.getWorkflows().size());
		for (String k : project.getAttachedObjects().keySet()) {
			assertEquals("wrong attached object", project.getAttachedObject(k), projectReaden.getAttachedObject(k));
		}
		
		
		
	}

	@Test
	public void testEmptyProject() {
		
		// create project
		GenlabProject project = createEmptyProject();
		
		// tune it
		project.setAttachedObject("test1", new Integer(2));
		project.setAttachedObject("test2", null);
		project.setAttachedObject("test3", "test string");
		project.setAttachedObject("test3", "test string with special characters !([$ $ ^{");

		
		// save it
		GenlabPersistence.getPersistence().saveProject(project);
		
		// check project file exists
		assertTrue((new File(project.getBaseDirectory()+File.separator+GenlabPersistence.FILENAME_PROJECT)).exists());
		
		// read it
		IGenlabProject projectReaden = GenlabPersistence.getPersistence().readProject(project.getBaseDirectory());
		
		compareProjects(project, projectReaden);
		
		
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
			assertTrue(workflow.containsAlgoInstance(a.getId()));
			assertTrue(workflowReaden.containsAlgoInstance(a.getId()));
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
		
		GenlabProject project = createEmptyProject();

		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(project, "workflowAA", "my desc", "my workflow");
		assertNotNull(workflow);
		
		// store it
		GenlabPersistence.getPersistence().saveProject(project);
		
		// check project and workflow files exists
		assertTrue((new File(project.getBaseDirectory()+File.separator+GenlabPersistence.FILENAME_PROJECT)).exists());
		assertTrue((new File(project.getBaseDirectory()+File.separator+workflow.getRelativeFilename())).exists());
		assertTrue((new File(workflow.getAbsolutePath())).exists());
		
		// read it again
		IGenlabProject projectReaden = GenlabPersistence.getPersistence().readProject(project.getBaseDirectory());
		
		compareProjects(project, projectReaden);
		
		// compare 
		assertEquals(1, projectReaden.getWorkflows().size());
		IGenlabWorkflowInstance workflowReaden = projectReaden.getWorkflows().iterator().next();
		
		compareWorkflows(workflow, workflowReaden);
		
	}
	

	

}
