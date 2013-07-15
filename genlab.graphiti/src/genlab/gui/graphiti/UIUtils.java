package genlab.gui.graphiti;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class UIUtils {

	private UIUtils() {
	}

	public static String askString(String dialogTitle, String dialogMessage, String initialValue) { 
		String ret = null;
		Shell shell = getShell();
		InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, null);
		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			ret = inputDialog.getValue();
		}
		return ret;
	}
	
	/**
	 * Returns the currently active Shell.
	 * 
	 * @return The currently active Shell.
	 */
	private static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	
	public static void setValueInTransaction(final Text text, final String value) {
		// retrieve resources
		//final ResourceSetImpl resourceSet = new ResourceSetImpl();
	    
		final ResourceSet resourceSet = text.eResource().getResourceSet();
		
        TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain == null) {
        	// Not yet existing, create one
        	editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }

		editingDomain.getCommandStack().execute(
				new Command() {
					
					@Override
					public void undo() {
						
					}
					
					@Override
					public void redo() {
						
					}
					
					@Override
					public Collection<?> getResult() {
						return null;
					}
					
					@Override
					public String getLabel() {
						return "change text";
					}
					
					@Override
					public String getDescription() {
						return "update the value of the text";
					}
					
					@Override
					public Collection<?> getAffectedObjects() {
						return null;
					}
					
					@Override
					public void execute() {
						text.setValue(value);
					}
					
					@Override
					public void dispose() {
						
					}
					
					@Override
					public Command chain(Command command) {
						return null;
					}
					
					@Override
					public boolean canUndo() {
						return false;
					}
					
					@Override
					public boolean canExecute() {
						return true;
					}
				});

        
	}
}
