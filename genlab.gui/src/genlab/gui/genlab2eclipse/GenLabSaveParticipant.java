package genlab.gui.genlab2eclipse;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;

import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;

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
		System.err.println("rollback !");
	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		
		// TODO should we save somehting when an eclipse project is saved ?
		
		/*
		GLLogger.debugTech("save each resource associated with the eclipse project", getClass());
		
		if (context.getKind() != ISaveContext.FULL_SAVE && context.getKind() != ISaveContext.PROJECT_SAVE)
			return;
		
		if (context.getProject() == null) {
			GLLogger.warnTech("unable to find the project in the saving context ?", getClass());
			System.err.println("unable to find the project in the saving context ?");
			return;
		} 
		
		IGenlabProject genlabProject = GenLab2eclipseUtils.getGenlabProjectForEclipseProject(context.getProject());
		if (genlabProject == null) {
			GLLogger.warnTech("unable to find the genlab project for saving...", getClass());
			return;
		}
		GenlabPersistence.getPersistence().saveProject(genlabProject);
		*/
		
	}

}
