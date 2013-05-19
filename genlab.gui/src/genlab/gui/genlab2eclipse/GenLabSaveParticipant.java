package genlab.gui.genlab2eclipse;

import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;

/**
 * Save participant is called as soon as an eclipse project is being saved. 
 * It saves all genlab data related to the project. 
 * The eclipse project and related informations (views, etc.) take care of that 
 * themselves.
 * 
 * @author Samuel Thiriot
 *
 */
public class GenLabSaveParticipant implements ISaveParticipant {

	public GenLabSaveParticipant() {
		// TODO Auto-generated constructor stub
		System.err.println("init ... !!!");

	}

	@Override
	public void doneSaving(ISaveContext context) {
		// TODO Auto-generated method stub
		System.err.println("saved... !!!");

	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
		// TODO Auto-generated method stub
		System.err.println("should save !!!");

	}

	@Override
	public void rollback(ISaveContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		
		GLLogger.debugTech("save each resource associated with the eclipse project", getClass());
		
		if (context.getProject() == null) {
			GLLogger.warnTech("unable to find the project in the saving context ?", getClass());
			System.err.println("unable to find the project in the saving context ?");
			return;
		} 
		
		IGenlabProject genlabProject = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(context.getProject());
		for (IGenlabWorkflow workflow : genlabProject.getWorkflows()) {
			GLLogger.debugTech("saving workflow: "+workflow, getClass());
			try {
				GenlabPersistence.getPersistence().saveWorkflow(workflow);
			} catch (Exception e) {
				GLLogger.errorTech("error while saving workflow "+workflow, getClass(), e);
				// TODO warn user
			}
		}
		
	}

}
