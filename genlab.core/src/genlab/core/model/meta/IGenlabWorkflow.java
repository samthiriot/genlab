package genlab.core.model.meta;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.projects.IGenlabProject;

import java.io.File;
import java.util.Collection;

/**
 * 
 * A workflow is a container of algos. It is a specific algo that may, of course, 
 * be added as any other algo to a workflow.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IGenlabWorkflow extends IAlgo {

	
}
