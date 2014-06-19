package tests.genlab.core.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValue;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.GenlabProject;
import genlab.core.projects.IGenlabProject;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;

import java.io.File;

import org.junit.Test;

/**
 * Extends the basic persistence test, by adding examples of workspaces wit generation chains
 * 
 * @author Samuel Thiriot
 *
 */
public class TestPersistenceChains extends TestPersistence {


	@Test
	public void testWorkflowWS() {
		
		GenlabProject project = createEmptyProject();

		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(project, "workflowAA", "my desc", "my workflow");
		{
			WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
			ConstantValueInteger constantInt = new ConstantValueInteger();
			ConstantValueDouble constantDouble = new ConstantValueDouble();
			
			IAlgoInstance wsInstance = ws.createInstance(workflow);
			IAlgoInstance constantN = constantInt.createInstance(workflow);
			constantN.setValueForParameter("value", 500);
			workflow.connect(
					 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
					 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
			);
			
			IAlgoInstance constantK = constantInt.createInstance(workflow);
			constantN.setValueForParameter("value", 2);
			workflow.connect(
					constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
					 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
			);
			
			IAlgoInstance constantP = constantDouble.createInstance(workflow);
			constantP.setValueForParameter("value", 0.1);
			workflow.connect(
					constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
					 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
			);
			
		}
		
		
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
