package genlab.gui.genlab2eclipse;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;

/**
 * TODO is not called... why ?
 * 
 * @author B12772
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
		// TODO Auto-generated method stub

	}

}
